package com.example.protasker.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.protasker.databinding.ActivityIntroBinding


class IntroActivity : BaseActivity() {

    private lateinit var binding: ActivityIntroBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntroBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnSignInIntro.setOnClickListener {startActivity(Intent(this, SingInActivity::class.java))}
        binding.btnSignUpIntro.setOnClickListener {startActivity(Intent(this, SingUpActivity::class.java))}

    }
}