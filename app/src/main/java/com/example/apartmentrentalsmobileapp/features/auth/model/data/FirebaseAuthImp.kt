package com.example.apartmentrentalsmobileapp.features.auth.model.data

import android.util.Log
import com.example.apartmentrentalsmobileapp.features.auth.entities.User
import com.example.apartmentrentalsmobileapp.features.auth.model.auth_repo.FirebaseAuthInterface
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class FirebaseAuthImp : FirebaseAuthInterface {


    private val auth = FirebaseAuth.getInstance()

    override suspend fun loginIn(email: String, pass: String): FirebaseUser? = suspendCancellableCoroutine { cont ->
        auth.signInWithEmailAndPassword(email, pass)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    cont.resume(task.result?.user)
                } else {
                    cont.resumeWithException(task.exception ?: Exception(task.result?.toString()))
                }
            }
    }

    override suspend fun signUp(name:String, email:String, pass:String): FirebaseUser? = suspendCancellableCoroutine { cont ->

        auth.createUserWithEmailAndPassword(email, pass)
            .addOnCompleteListener { task ->
                if (cont.isActive) {
                    if (task.isSuccessful) {
                        cont.resume(task.result?.user)
                    } else {
                        cont.resumeWithException(task.exception ?: Exception("Unknown signup error"))
                    }
                }
            }
            .addOnFailureListener { exception ->
                if (cont.isActive) {
                    cont.resumeWithException(exception)
                }
            }
    }
}