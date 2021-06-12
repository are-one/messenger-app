package com.one.messengerapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.one.messengerapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        val view = binding.root
        setContentView(view)

        binding.buttonRegister.setOnClickListener {
            val username = binding.edittextUsername.text.toString()
            val email = binding.edittextEmail.text.toString()
            val password = binding.edittextPassword.text.toString()
        }

        binding.tvAlreadyHaveAnAccount.setOnClickListener {
            val intentLogin = Intent(this, LoginActivity::class.java)
            startActivity(intentLogin)
        }

    }
}