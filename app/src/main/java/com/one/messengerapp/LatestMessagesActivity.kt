package com.one.messengerapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.one.messengerapp.databinding.ActivityLatestMessagesBinding

class LatestMessagesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLatestMessagesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLatestMessagesBinding.inflate(layoutInflater)

        val view = binding.root
        setContentView(view)
    }
}