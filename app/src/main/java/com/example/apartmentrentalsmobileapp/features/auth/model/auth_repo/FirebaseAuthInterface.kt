package com.example.apartmentrentalsmobileapp.features.auth.model.auth_repo

import com.example.apartmentrentalsmobileapp.features.auth.entities.User
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.Deferred

interface FirebaseAuthInterface {

    suspend fun loginIn(email:String, pass:String) : FirebaseUser?;
    suspend fun signUp(name:String, email:String, pass:String) : FirebaseUser?;
}