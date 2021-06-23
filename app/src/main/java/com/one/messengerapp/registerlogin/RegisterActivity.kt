package com.one.messengerapp.registerlogin

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.one.messengerapp.LatestMessagesActivity
import com.one.messengerapp.LoginActivity
import com.one.messengerapp.databinding.ActivityRegisterBinding
import com.one.messengerapp.models.User
import java.util.*

class RegisterActivity : AppCompatActivity() {

    companion object{
        const val TAG = "RegisterActivity"
    }

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mAuth = FirebaseAuth.getInstance()
        verifyUserIsLoggedIn()

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.buttonRegister.setOnClickListener {
            performRegister()
        }

        binding.tvAlreadyHaveAnAccount.setOnClickListener {
            val intentLogin = Intent(this, LoginActivity::class.java)
            startActivity(intentLogin)
        }

        binding.selectphotoRegisterButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            getContent.launch(intent)
        }
    }

    private fun verifyUserIsLoggedIn(){
        val currentUser = mAuth.currentUser

        if (currentUser != null) {
            val intent = Intent(this, LatestMessagesActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    var selectedPhotoUrl: Uri? = null
    var getContent = registerForActivityResult(StartActivityForResult()){
        if (it.resultCode == Activity.RESULT_OK && it.data != null){

            selectedPhotoUrl = it.data!!.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUrl)

            binding.selectImageviewRegister.setImageBitmap(bitmap)

            binding.selectphotoRegisterButton.alpha = 0f

//            val bitmapDrawable = BitmapDrawable(bitmap)
//            binding.selectphotoRegisterButton.setBackgroundDrawable(bitmapDrawable)
        }
    }

    private fun performRegister() {
        val email = binding.edittextEmail.text.toString()
        val password = binding.edittextPassword.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please input text email/password!", Toast.LENGTH_SHORT).show()
            return
        }

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if (!it.isSuccessful) return@addOnCompleteListener

                    Log.d(TAG, "Successfully created user with uid : ${it.result?.user?.uid}")
                    uploadImageToFirebaseStorage()
                }
                .addOnFailureListener {
                    Log.d(TAG, "Failed create user: ${it.message}")
                    Toast.makeText(this,"Failed create user: ${it.message}", Toast.LENGTH_SHORT).show()
                }
    }

    private fun uploadImageToFirebaseStorage() {
        if (selectedPhotoUrl == null) return
        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")

        ref.putFile(selectedPhotoUrl!!)
                .addOnSuccessListener {
                    Toast.makeText(this, "Gambar berhasil diupload", Toast.LENGTH_SHORT).show()

                    ref.downloadUrl.addOnSuccessListener {
                        Log.d(TAG, "File location: $it")
                        saveUserToFirebaseDatabase(it.toString())
                    }
                }
            .addOnFailureListener {

            }
    }

    private fun saveUserToFirebaseDatabase(profileImageUrl: String) {
        val uid = mAuth.uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")

        val user = User(uid, binding.edittextUsername.text.toString(), profileImageUrl)

        ref.setValue(user)
            .addOnSuccessListener {
                Log.d(TAG, "Finally we saved the user to Firebase database")

                val intent = Intent(this, LatestMessagesActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            .addOnFailureListener {
                Log.d(TAG, "Failed to set value to database: ${it.message}")
            }
    }
}
