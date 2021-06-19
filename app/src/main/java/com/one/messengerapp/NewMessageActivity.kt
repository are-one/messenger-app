package com.one.messengerapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.one.messengerapp.databinding.ActivityNewMessageBinding

class NewMessageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewMessageBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewMessageBinding.inflate(layoutInflater)

        val view = binding.root
        setContentView(view)

        supportActionBar?.title = "Select User"

        binding.recyclerviewNewmessage.adapter
    }
}