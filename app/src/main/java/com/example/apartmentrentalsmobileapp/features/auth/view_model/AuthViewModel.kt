package com.example.apartmentrentalsmobileapp.features.auth.view_model

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.example.apartmentrentalsmobileapp.features.auth.entities.User
import com.example.apartmentrentalsmobileapp.features.auth.model.auth_repo.FirebaseAuthInterface
import com.example.apartmentrentalsmobileapp.features.auth.model.data.FirebaseAuthImp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class AuthViewModel(application: Application): AndroidViewModel(application) {


    private val firebaseAuthInterface: FirebaseAuthInterface = FirebaseAuthImp()
    val showLoading = MutableLiveData<Boolean>()
    val errorMessage = MutableLiveData<String>()
    var sharedPreferences: SharedPreferences? = null
    val authStatus = MutableLiveData<String>()
    var currentUser: User? = null
    val navigateToOffline = MutableLiveData<Boolean>()
    var redirectToHome: MutableLiveData<Boolean> = MutableLiveData<Boolean>()



    init {
        //checking for the offline mode
        val hasInternet = NetworkUtils.isInternetAvailable(getApplication())
        val sharedPreferences = createEncryptedPreferences(getApplication())
        navigateToOffline.value = !hasInternet && sharedPreferences.getString("logedIn", null) == "true"
        redirectToHome.value = hasInternet && sharedPreferences.getString("logedIn", null) == "true"

    }

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

                currentUser = firebaseAuthInterface.getCurrentUserUser(result.uid)
                withContext(Dispatchers.IO) {
                    sharedPreferences?.edit()?.apply {
                        putString("token", token)
                        putString("currentUid", result.uid)
                        putString("name" ,currentUser?.name)
                        putString("role" ,currentUser?.role)
                        putString("logedIn", "true")
                        apply()
                    } ?: throw Exception("SharedPreferences not initialized")
                }

                //now see if its admin or normal_user
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


    fun createEncryptedPreferences(context: Context): SharedPreferences {
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

}