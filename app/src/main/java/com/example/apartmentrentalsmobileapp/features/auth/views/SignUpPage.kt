package com.example.apartmentrentalsmobileapp.features.auth.views

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.apartmentrentalsmobileapp.R
import com.example.apartmentrentalsmobileapp.features.auth.view_model.AuthViewModel
import com.google.android.material.snackbar.Snackbar

class SignUpPage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sign_up_page)


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //ui elements
        val signUpBtn = findViewById<Button>(R.id.btnSignUp)
        val logInBtn = findViewById<TextView>(R.id.txtLogin)
        val progressionIndicator = findViewById<ProgressBar>(R.id.progressBar)
        val name = findViewById<TextView>(R.id.signName)
        val email = findViewById<TextView>(R.id.signupEmail)
        val pass = findViewById<TextView>(R.id.signUpPassword)
        val repeatPass = findViewById<TextView>(R.id.signupRepeatPassword)
        val radioGroup = findViewById<RadioGroup>(R.id.radioUserType)

        //viewModel
        val authViewModel: AuthViewModel by viewModels()


        //signUpButton
        signUpBtn.setOnClickListener {
                if (name.text.toString() != "" && email.text.toString() != "" && pass.text.toString() != "" && repeatPass.text.toString() != "") {
                    if (pass.text.toString() == repeatPass.text.toString()) {
                        val selectedRadioButtonId = radioGroup.checkedRadioButtonId
                        if (selectedRadioButtonId != -1) {
                            val selectedRadioButton = findViewById<RadioButton>(selectedRadioButtonId)
                            val selectedRole = selectedRadioButton.text.toString()
                            authViewModel.signUp(
                                name.text.toString(), email.text.toString(),
                                pass.text.toString(),selectedRole
                            )
                        }
                    }
                }
        }


        //authStatus
        authViewModel.authStatus.observe(this) { status ->

            if(status == "signUp Succeed"){
                val intent = Intent(this, LoginPage::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
                finish()
            }


        }

        //signUp button
        logInBtn.setOnClickListener {
            val intent = Intent(this, LoginPage::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            finish()
        }


        //to show the progress indicator while doing SignUp
        authViewModel.showLoading.observe(this) { isLoading ->
            if (isLoading == true) {
                progressionIndicator.visibility = View.VISIBLE
                signUpBtn.visibility = View.GONE
                logInBtn.isClickable = false
            } else {
                progressionIndicator.visibility = View.GONE
                signUpBtn.visibility = View.VISIBLE
                logInBtn.isClickable = true
            }
        }

        authViewModel.errorMessage.observe(this) { error ->
            val rootView = findViewById<View>(android.R.id.content)
            Snackbar.make(rootView, error, Snackbar.LENGTH_SHORT).show()
        }
    }
}