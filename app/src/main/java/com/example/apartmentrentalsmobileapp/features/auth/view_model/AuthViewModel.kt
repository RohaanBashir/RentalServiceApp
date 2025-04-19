package com.example.apartmentrentalsmobileapp.features.auth.view_model

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.example.apartmentrentalsmobileapp.features.auth.entities.User
import com.example.apartmentrentalsmobileapp.features.auth.model.auth_repo.FirebaseAuthInterface
import com.example.apartmentrentalsmobileapp.features.auth.model.data.FirebaseAuthImp
import com.google.firebase.auth.FirebaseAuthException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class AuthViewModel: ViewModel() {


    private val firebaseAuthInterface: FirebaseAuthInterface = FirebaseAuthImp()
    val showLoading = MutableLiveData<Boolean>()
    val errorMessage = MutableLiveData<String>()
    var sharedPreferences: SharedPreferences? = null
    val authStatus = MutableLiveData<String>()
    var currentUser: User? = null

    fun signUp(name: String, email: String, pass: String, role: String) {

        showLoading.value = true
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    firebaseAuthInterface.signUp(name, email, pass, role)
                }
                Log.d("signup", " Sign up Auth Succeed")
                authStatus.value = "signUp Succeed"
            } catch (e: Exception) {
                Log.e("SignUpFailed", "Exception: ${e.message}")
                errorMessage.value = e.message
            } finally {
                showLoading.value = false
            }
        }
    }

    fun logIn(email: String, pass: String, context: Context) {
        showLoading.value = true
        viewModelScope.launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    firebaseAuthInterface.loginIn(email, pass)
                }

                //getting token and storing into encrypted sharedPreferences
                val token = try {
                    result?.getIdToken(true)?.await()?.token ?: throw Exception("Token is null")
                } catch (e: Exception) {
                    throw Exception("Failed to get ID token: ${e.message}")
                }
                if (sharedPreferences == null) {
                    sharedPreferences = createEncryptedPreferences(context)
                }

                withContext(Dispatchers.IO) {
                    sharedPreferences?.edit()?.apply {
                        putString("token", token)
                        putString("currentUid", result.uid)
                        apply() // async save
                    } ?: throw Exception("SharedPreferences not initialized")
                }

                //now see if its admin or normal_user
                currentUser = firebaseAuthInterface.getCurrentUserUser(result.uid)
                if (currentUser?.role == "Regular User") {
                    authStatus.value = "Regular User"
                } else {
                    authStatus.value = "Admin"
                }
            } catch (e: Exception) {
                Log.e("LoginFailed", "Exception: ${e.message}")
                errorMessage.value = e.message
            } finally {
                showLoading.value = false
            }
        }
    }

    private fun createEncryptedPreferences(context: Context): SharedPreferences {
        val masterKey = MasterKey.Builder(context.applicationContext)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        return EncryptedSharedPreferences.create(
            context.applicationContext,
            "secure_prefs",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    suspend fun verifyToken(): Boolean {

        try {
            return sharedPreferences?.getString(
                "token",
                ""
            ) == firebaseAuthInterface.getCurrentUserToken()
        } catch (e: Exception) {
            throw Exception(e.toString())
        }
    }
}