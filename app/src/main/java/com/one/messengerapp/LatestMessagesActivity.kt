package com.one.messengerapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.one.messengerapp.databinding.ActivityLatestMessagesBinding
import com.google.firebase.auth.FirebaseAuth

class LatestMessagesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLatestMessagesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLatestMessagesBinding.inflate(layoutInflater)

        val view = binding.root
        setContentView(view)

        verifyUserIsLoggedIn()
    }

    private fun verifyUserIsLoggedIn() {
        val uid = FirebaseAuth.getInstance().uid

        if (uid == null) {
            val intent = Intent(this, RegisterActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }
}