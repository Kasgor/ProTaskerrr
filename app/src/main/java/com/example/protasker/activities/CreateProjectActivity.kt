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
import com.example.protasker.databinding.ActivityCreateProjectBinding
import com.example.protasker.firebase.FirestoreClass
import com.example.protasker.models.Project
import com.example.protasker.utils.Constants
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.IOException


private lateinit var binding: ActivityCreateProjectBinding

class CreateProjectActivity : BaseActivity() {
    companion object{
        private const val READ_STORAGE_PERMISSION_CODE=1
        private const val PICK_IMAGE_REQUEST_CODE=2
    }
    private var SelectedImageURI : Uri? =null
    private var projectImageURL: String =""

    private lateinit var userName: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateProjectBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupActionBar()

        if (intent.hasExtra(Constants.NAME)) {
            userName = intent.getStringExtra(Constants.NAME)!!
        }

        binding.ivBoardImage.setOnClickListener {
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){
                showGalleryforChoose()
            }
            else{
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    CreateProjectActivity.READ_STORAGE_PERMISSION_CODE
                )
            }
        }
        binding.btnCreate.setOnClickListener{
            if (SelectedImageURI != null) {

                uploadProjectImage()
            } else {

                showProgressDialog(resources.getString(R.string.please_wait))
                createProject()
            }
        }


    }
    // Get the file extension from the URI
    private fun getFileExtemsion(uri: Uri?): String?{
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(contentResolver.getType(uri!!))
    }

    /**
     * Handle permission request result
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode== CreateProjectActivity.READ_STORAGE_PERMISSION_CODE){
            if(grantResults.isNotEmpty()&& grantResults[0]==PackageManager.PERMISSION_GRANTED){
                showGalleryforChoose()
            }
        }
        else{

        }
    }

    /**
     * Handle activity result from gallery selection
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode== Activity.RESULT_OK && requestCode== CreateProjectActivity.PICK_IMAGE_REQUEST_CODE && data!!.data!=null){
            SelectedImageURI = data.data
            try{
                    Glide
                        .with(this)
                        .load(SelectedImageURI)
                        .centerCrop()
                        .placeholder(R.drawable.ic_user_place_holder)
                        .into(binding.ivBoardImage)
            }
            catch (e: IOException){

            }


        }
    }

    /**
     *  Show gallery for image selection
     */
    private fun showGalleryforChoose(){
        var galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, CreateProjectActivity.PICK_IMAGE_REQUEST_CODE)
    }

    /**
     * Upload the project image to Firebase Storage
     */
    private fun uploadProjectImage(){
        showProgressDialog(resources.getString(R.string.please_wait))
        val sRef: StorageReference = FirebaseStorage.getInstance().reference.child(
            "POJECT_IMAGE" + System.currentTimeMillis() + "."
                    + getFileExtemsion( SelectedImageURI)
        )

        sRef.putFile(SelectedImageURI!!)
            .addOnSuccessListener { taskSnapshot ->
                Log.e(
                    "",
                    taskSnapshot.metadata!!.reference!!.downloadUrl.toString()
                )

                taskSnapshot.metadata!!.reference!!.downloadUrl
                    .addOnSuccessListener { uri ->
                        Log.e("", uri.toString())

                        projectImageURL = uri.toString()

                        createProject()
                    }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(
                    this,
                    exception.message,
                    Toast.LENGTH_LONG
                ).show()
                hideProgressDialog()
            }
    }

    /**
     * setup custom tool bar
     */
    private fun setupActionBar() {

        setSupportActionBar(binding.toolbarCreateBoardActivity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        binding.toolbarCreateBoardActivity.setNavigationOnClickListener { onBackPressed() }
    }

    /**
     * Create the project using the provided information
     */
    private fun createProject(){
        val assignedUsersArrayList: ArrayList<String> = ArrayList()
        assignedUsersArrayList.add(getID())

        val project = Project(
            binding.etBoardName.text.toString(),
            projectImageURL,
            userName,
            assignedUsersArrayList
        )

        FirestoreClass().createProject(this, project)
    }

    /**
     * Callback method called when project creation is successful
     */
    fun projectCreatedSuccessfully() {

        hideProgressDialog()
        setResult(Activity.RESULT_OK)

        finish()
    }
}