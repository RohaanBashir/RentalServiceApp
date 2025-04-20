package com.example.apartmentrentalsmobileapp.features.auth.model.auth_repo

import android.app.Application
import android.content.Context
import com.example.apartmentrentalsmobileapp.features.auth.entities.User
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.Deferred

interface FirebaseAuthInterface {

    suspend fun loginIn(email:String, pass:String) : FirebaseUser?;
    suspend fun signUp(name:String, email:String, pass:String,role: String) : FirebaseUser?;
    suspend fun saveUserToDatabase(user: User)
    suspend fun checkUserRole(uid: String):String;
    suspend fun getCurrentUserToken():String;
    suspend fun getCurrentUserUser(uid: String):User;
    suspend fun signOut(context: Context)
}