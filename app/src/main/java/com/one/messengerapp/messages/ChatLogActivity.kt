package com.one.messengerapp.messages

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.one.messengerapp.R
import com.one.messengerapp.messages.NewMessageActivity.Companion.USER_KEY
import com.one.messengerapp.models.ChatMessage
import com.one.messengerapp.models.User
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_chat_log.*
import kotlinx.android.synthetic.main.chat_from_row.view.*
import kotlinx.android.synthetic.main.chat_to_row.view.*

class ChatLogActivity : AppCompatActivity() {

    companion object{
        val TAG = "ChatLog"
    }

    private lateinit var user: User
    private val mAuth = FirebaseAuth.getInstance()
    private val adapter = GroupAdapter<GroupieViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        recyclerview_chat_log.adapter = adapter

        user = intent.getParcelableExtra<User>(USER_KEY)!!
        supportActionBar?.title = user.username

        listenForMessage()

        send_button_chat_log.setOnClickListener {
            Log.d(TAG, "Attempt to send message.....")
            performSendMessage()
        }
    }

    private fun listenForMessage() {
        val fromId = mAuth.uid
        val toId = user.uid
        val ref = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId")

        ref.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(ChatMessage::class.java)

                chatMessage?.let {
                    Log.d(TAG, it.text + " | FromId:" + it.fromId + " | ToId:" + it.toId)
                    if (it.fromId == mAuth.uid){
                        val currentUser = LatestMessagesActivity.currentUser ?: return
                        adapter.add(ChatFromItem(it.text, currentUser))
                    } else {
                        adapter.add(ChatToItem(it.text,user))
                    }
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

    private fun performSendMessage() {
        val text = edittext_chat_log.text.toString()
        val fromId = mAuth.uid
        val toId = user.uid

        if (fromId == null) return

        val ref = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId").push()

        val toRef = FirebaseDatabase.getInstance().getReference("/user-messages/$toId/$fromId").push()

        val chatMessage = ChatMessage(ref.key.toString(), text, fromId, toId, System.currentTimeMillis() / 1000)

        ref.setValue(chatMessage)
                .addOnSuccessListener {
                    Log.d(TAG, "Saved our chat message: ${ref.key}")
                    edittext_chat_log.text.clear()
//                    Mengambil posisi chat terakhir
                    recyclerview_chat_log.scrollToPosition(adapter.itemCount - 1)
                }
                .addOnFailureListener {
                    Log.d(TAG, it.message.toString())
                }

        toRef.setValue(chatMessage)
    }
}

class ChatFromItem(val text: String, val user: User): Item<GroupieViewHolder>(){
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.textview_chat_from.text = text

        // Load user image
        val uri = user.profileImageUrl
        val targetImageView = viewHolder.itemView.imageview_chat_from
        Picasso.get().load(uri).into(targetImageView)
    }
    override fun getLayout(): Int {
        return R.layout.chat_from_row
    }
}

class ChatToItem(val text: String, val user: User): Item<GroupieViewHolder>(){
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.textview_chat_to.text = text

        // Load user image
        val uri = user.profileImageUrl
        val targetImageView = viewHolder.itemView.imageview_chat_to
        Picasso.get().load(uri).into(targetImageView)
    }
    override fun getLayout(): Int {
        return R.layout.chat_to_row
    }
}