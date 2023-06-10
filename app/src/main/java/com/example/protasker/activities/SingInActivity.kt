package com.example.protasker.activities

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.WindowManager
import android.widget.Toast
import com.example.protasker.R
import com.example.protasker.databinding.ActivitySingInBinding
import com.example.protasker.firebase.FirestoreClass
import com.example.protasker.models.User
import com.google.firebase.auth.FirebaseAuth

class SingInActivity : BaseActivity() {
    private lateinit var binding: ActivitySingInBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivitySingInBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)

        setContentView(binding.root)


        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )


        setupActionBar()
        binding.btnSignUp.setOnClickListener {
            signIn()
        }
    }
    /**
     * setup custom tool bar
     */
    private fun setupActionBar() {

        setSupportActionBar(findViewById(R.id.toolbar_sign_in_activity))

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
        }

        findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar_sign_in_activity).setNavigationOnClickListener { onBackPressed() }

    }

    /**
     * try to sing in user by entered information
     */
    private fun signIn() {

        val email: String = binding.etEmail.text.toString().trim { it <= ' ' }
        val password: String = binding.etPassword.text.toString().trim { it <= ' ' }

        if (validate(email, password)) {

            showProgressDialog(resources.getString(R.string.please_wait))

            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        FirestoreClass().loadDataOfUser(this)
                    } else {
                        Toast.makeText(
                            this,
                            task.exception!!.message,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
        }
    }

    /**
     * start activity on successful sing in
     */
    fun signInSuccess(user: User) {

        hideProgressDialog()

        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    /**
     * validation of user`s data
     */
    private fun validate(email: String, password: String): Boolean {
        return if (TextUtils.isEmpty(email)) {
            showErrorSnackBar("Please enter email.")
            false
        } else if (TextUtils.isEmpty(password)) {
            showErrorSnackBar("Please enter password.")
            false
        } else {
            true
        }
    }
}