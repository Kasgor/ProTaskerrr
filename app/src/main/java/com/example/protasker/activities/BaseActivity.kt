package com.example.protasker.activities

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.protasker.R
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
/**
 * BaseActivity is the parent class for all activities in the application.
 * It provides common functionality and utilities that can be used by child activities.
 */
open class BaseActivity : AppCompatActivity() {

    private var doubleBackToExitPressedOnce = false

    private lateinit var mProgressDialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)
    }
    /**
     * Displays a progress dialog with the given text.
     */
    fun showProgressDialog(text: String) {
        mProgressDialog = Dialog(this)

        mProgressDialog.setContentView(R.layout.dialog_progress)
        val textView = mProgressDialog.findViewById<TextView>(R.id.tv_progress_text)
        textView.text = text

        mProgressDialog.show()
    }
    /**
     * Hides the progress dialog if it is currently showing.
     */
    fun hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog!!.isShowing) {
            if (!isFinishing) {
                mProgressDialog!!.dismiss()
            }

        }
    }
    /**
     * Returns the current user ID from Firebase authentication.
     */
    fun getID(): String {
        return FirebaseAuth.getInstance().currentUser!!.uid
    }
    /**
     * Handles the double back press to exit the activity.
     * Shows a toast message on the first press and exits on the second press.
     */
    fun doubleBackToExit() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }

        this.doubleBackToExitPressedOnce = true
        Toast.makeText(
            this,
            resources.getString(R.string.please_click_back_again_to_exit),
            Toast.LENGTH_SHORT
        ).show()

        Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
    }
    /**
     * Displays an error snackbar with the given message.
     */
    fun showErrorSnackBar(message: String) {
        val snackBar =
            Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
        val snackBarView = snackBar.view
        snackBarView.setBackgroundColor(
            ContextCompat.getColor(
                this@BaseActivity,
                R.color.error_color
            )
        )
        snackBar.show()
    }
}