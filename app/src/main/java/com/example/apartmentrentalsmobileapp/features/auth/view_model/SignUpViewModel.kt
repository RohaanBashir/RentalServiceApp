package com.example.apartmentrentalsmobileapp.features.auth.view_model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apartmentrentalsmobileapp.features.auth.entities.User
import com.example.apartmentrentalsmobileapp.features.auth.model.auth_repo.FirebaseAuthInterface
import com.example.apartmentrentalsmobileapp.features.auth.model.data.FirebaseAuthImp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SignUpViewModel: ViewModel() {

    val currentUser : User? = null;
    private val firebaseAuthInterface: FirebaseAuthInterface = FirebaseAuthImp()
    val showLoading = MutableLiveData<Boolean>()
    val errorMessage = MutableLiveData<String>()

    fun signUp(name: String, email: String, pass: String){

        showLoading.value = true
        viewModelScope.launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    firebaseAuthInterface.signUp(name, email, pass)
                }
                Log.d("SignUpSuccess", "User: $result")
            } catch (e: Exception) {
                Log.e("SignUpFailed", "Exception: ${e.message}")
                errorMessage.value = e.message
            } finally {
                showLoading.value = false
            }
        }
    }
}