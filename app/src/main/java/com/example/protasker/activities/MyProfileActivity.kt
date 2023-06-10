package com.example.protasker.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.protasker.R
import com.example.protasker.databinding.ActivityMyProfileBinding
import com.example.protasker.firebase.FirestoreClass
import com.example.protasker.models.User
import com.example.protasker.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.IOException


class MyProfileActivity : BaseActivity() {
    companion object{
        private const val READ_STORAGE_PERMISSION_CODE=1
        private const val PICK_IMAGE_REQUEST_CODE=2
    }

    private var SelectedImageURI : Uri? =null
    private var profileImageURI: String =""
    private lateinit var userDetails: User

    private lateinit var binding: ActivityMyProfileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMyProfileBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupActionBar()
        FirestoreClass().loadDataOfUser(this)
        binding.ivProfileUserImage.setOnClickListener {
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED){
                showImageChooser()
            }
            else{
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    READ_STORAGE_PERMISSION_CODE
                )
            }
        }
        binding.btnUpdate.setOnClickListener {
            if(SelectedImageURI!=null) {
                uploadUserImageToTheFire()
            }
            else{
                showProgressDialog(resources.getString(R.string.please_wait))
                updateData()
            }
        }
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName("New Display Name")
            .setPhotoUri(Uri.parse("https://example.com/profile.jpg"))
            .build()
        val dfsdfasd =FirebaseAuth.getInstance().getCurrentUser()?.phoneNumber


    }

    /**
     *
     * Handle permission request result
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode== READ_STORAGE_PERMISSION_CODE){
            if(grantResults.isNotEmpty()&& grantResults[0]==PackageManager.PERMISSION_GRANTED){
                showImageChooser()
            }
        }
        else{

        }
    }

    /**
     *  Handle activity result from gallery selection
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode==Activity.RESULT_OK && requestCode== PICK_IMAGE_REQUEST_CODE && data!!.data!=null){
            SelectedImageURI = data.data
            try{
                Glide
                    .with(this)
                    .load(SelectedImageURI)
                    .centerCrop()
                    .placeholder(R.drawable.ic_user_place_holder)
                    .into(binding.ivProfileUserImage)
            }
            catch (e:IOException){

            }


        }
    }

    /**
     * Update user data with the changes made
     */
    fun updateData(){
        var userHash = HashMap<String, Any>()

        if(profileImageURI.isNotEmpty()&&profileImageURI!=userDetails.image){
            userHash[Constants.IMAGE]=profileImageURI
        }
        if(binding.etName.text.toString()!=userDetails.name&&binding.etName.text.toString()!=""){
            userHash[Constants.NAME]=binding.etName.text.toString()

        }
        if(binding.etMobile.text.toString()!=userDetails.mobile.toString()&&binding.etMobile.text.toString()!=""){
            userHash[Constants.MOBILE]=binding.etMobile.text.toString()

        }

        FirestoreClass().updateUserProfileData(this, userHash)

    }

    /**
     * Get the file extension from the URI
     */
    private fun getFileExtemsion(uri: Uri?): String?{
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(contentResolver.getType(uri!!))
    }

    /**
     * Upload user image to Firebase Storage
     */
    private fun uploadUserImageToTheFire(){
        showProgressDialog(resources.getString(R.string.please_wait))
        if(SelectedImageURI!=null){
            val storageRefer : StorageReference
            = FirebaseStorage.getInstance().reference.child("USER"+ System.currentTimeMillis()+"."+getFileExtemsion(SelectedImageURI))

            storageRefer.putFile(SelectedImageURI!!).addOnSuccessListener {
                taskSnapshot ->
                Log.e("Firebase img uri" , taskSnapshot.metadata!!.reference!!.downloadUrl.toString())

                taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
                    uri ->
                    profileImageURI = uri.toString()
               updateData()

                }
            }.addOnFailureListener{
                exeption->
                Toast.makeText(this, exeption.message, Toast.LENGTH_LONG).show()
                hideProgressDialog()
            }
        }
    }

    /**
     * Show the gallery for selecting an image
     */
    private fun showImageChooser(){
        var galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST_CODE)
    }

    /**
     * Set up the action bar
     */
    private fun setupActionBar(){
        setSupportActionBar(binding.toolbarMyProfileActivity)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            actionBar.title = resources.getString(R.string.my_profile)
        }

        binding.toolbarMyProfileActivity.setNavigationOnClickListener { onBackPressed() }
    }

    /**
     *  Set the UI with user data
     */
    fun setUI(user: User){
        userDetails= user
        Glide
            .with(this)
            .load(user.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(binding.ivProfileUserImage)

        binding.etName.setText(user.name)
        binding.etEmail.setText(user.email)
        binding.etMobile.setText(user.mobile.toString())

    }

    /**
     * Callback method called when profile update is successful
     */
    fun profileUpdateSuccess(){
        hideProgressDialog()
        setResult(Activity.RESULT_OK)
        finish()
    }

}