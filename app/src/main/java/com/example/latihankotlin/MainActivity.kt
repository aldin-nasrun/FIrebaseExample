package com.example.latihankotlin

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.lang.Exception

class MainActivity : AppCompatActivity() {
    lateinit var auth : FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        auth = FirebaseAuth.getInstance()

        btn_register.setOnClickListener {
            registerUser()
        }
        btn_login.setOnClickListener {
            loginUser()
        }

        btn_logout.setOnClickListener {
            auth.signOut()
            checkLoginState()
        }
        btn_update.setOnClickListener {
            updateUser()
        }

    }

    override fun onStart() {
        super.onStart()
        checkLoginState()
    }

    private fun registerUser(){
        val email = et_regMail.text.toString()
        val password = et_regPass.text.toString()
        if (email.isNotEmpty() && password.isNotEmpty()){
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    auth.createUserWithEmailAndPassword(email, password).await()
                    withContext(Dispatchers.Main){
                        checkLoginState()
                    }
                }catch (e :Exception){
                    withContext(Dispatchers.Main){
                        Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
    private fun loginUser(){
        val email = et_loginMail.text.toString()
        val password = et_loginPass.text.toString()
        if (email.isNotEmpty() && password.isNotEmpty()){
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    auth.signInWithEmailAndPassword(email, password).await()
                    withContext(Dispatchers.Main){
                        checkLoginState()
                    }
                }catch (e :Exception){
                    withContext(Dispatchers.Main){
                        Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun updateUser(){
        auth.currentUser?.let {user ->
            val username = et_username.text.toString()
            val pictureURI = Uri.parse("android.resource://$packageName//${R.drawable.car4}")
            val profileUpdate = UserProfileChangeRequest.Builder()
                .setDisplayName(username)
                .setPhotoUri(pictureURI)
                .build()

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    user.updateProfile(profileUpdate).await()
                    withContext(Dispatchers.Main){
                        checkLoginState()
                        Toast.makeText(this@MainActivity, "Success Update", Toast.LENGTH_SHORT).show()
                    }

                }catch (e : Exception){
                    withContext(Dispatchers.Main){
                        Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun checkLoginState() {
        val user = auth.currentUser
        if (user == null){
            tv_status.text ="You not Logged in"
        }else{
            tv_status.text ="you logged in"
            et_username.setText(user.displayName)
            iv_profile.setImageURI(user.photoUrl)
        }
    }
}