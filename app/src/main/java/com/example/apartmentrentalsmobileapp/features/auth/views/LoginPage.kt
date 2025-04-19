package com.example.apartmentrentalsmobileapp.features.auth.views

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.apartmentrentalsmobileapp.R
import com.example.apartmentrentalsmobileapp.features.auth.view_model.AuthViewModel
import com.example.apartmentrentalsmobileapp.features.normal_user.views.NormalUser
import com.example.apartmentrentalsmobileapp.features.retailer.views.Admin
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseApp

class LoginPage : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login_page)
        FirebaseApp.initializeApp(this)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //viewModel
        val authViewModel: AuthViewModel by viewModels()

        //Ui Components
        val signUpButton: TextView = findViewById(R.id.txtSignUp)
        val email: EditText = findViewById<EditText>(R.id.editTextEmail)
        val pass: EditText = findViewById<EditText>(R.id.editTextPassword)
        val loginBtn: Button = findViewById<Button>(R.id.btnLogin)
        val progressionIndicator : ProgressBar = findViewById<ProgressBar>(R.id.progressBar)


        //loginBtn
        loginBtn.setOnClickListener {
            if(!email.text.isEmpty() && !pass.text.isEmpty()){
                authViewModel.logIn(email.text.toString(),pass.text.toString(), applicationContext)
            }
        }

        //auth status and getting to the new screen
        authViewModel.authStatus.observe(this) { status ->
            if(status.toString() == "Admin"){
                val intent = Intent(this, Admin::class.java)
                intent.putExtra("name", authViewModel.currentUser?.name)
                intent.putExtra("email", authViewModel.currentUser?.email)
                intent.putExtra("uid", authViewModel.currentUser?.uid)
                intent.putExtra("role", authViewModel.currentUser?.role)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
            }else{
                val intent = Intent(this, NormalUser::class.java)
                intent.putExtra("name", authViewModel.currentUser?.name)
                intent.putExtra("email", authViewModel.currentUser?.email)
                intent.putExtra("uid", authViewModel.currentUser?.uid)
                intent.putExtra("role", authViewModel.currentUser?.role)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
            }
        }
        //loading indicator
        authViewModel.showLoading.observe(this) { isLoading ->
            if (isLoading == true) {
                progressionIndicator.visibility = View.VISIBLE
                loginBtn.visibility = View.GONE
                signUpButton.isClickable = false

            } else {
                progressionIndicator.visibility = View.GONE
                loginBtn.visibility = View.VISIBLE
                signUpButton.isClickable = true
            }
        }

        //signup Page
        signUpButton.setOnClickListener {
            val intent = Intent(this, SignUpPage::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }

        //snackBar
        authViewModel.errorMessage.observe(this) { error ->
            val rootView = findViewById<View>(android.R.id.content)
            Snackbar.make(rootView, error, Snackbar.LENGTH_SHORT).show()

        }
    }
}