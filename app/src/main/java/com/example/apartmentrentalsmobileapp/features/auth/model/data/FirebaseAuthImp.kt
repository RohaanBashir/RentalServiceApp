package com.example.apartmentrentalsmobileapp.features.auth.model.data

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.example.apartmentrentalsmobileapp.features.auth.entities.User
import com.example.apartmentrentalsmobileapp.features.auth.model.auth_repo.FirebaseAuthInterface
import com.example.apartmentrentalsmobileapp.secrets.MySecrets
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException


class FirebaseAuthImp : FirebaseAuthInterface {


    private val auth = FirebaseAuth.getInstance()
    private val secrets: MySecrets= MySecrets()

    val database = FirebaseDatabase.getInstance(secrets.firebaseDatabaseReferenceLink)
    val usersRef = database.getReference("users")


    override suspend fun loginIn(email: String, pass: String): FirebaseUser? = suspendCancellableCoroutine { cont ->
        auth.signInWithEmailAndPassword(email, pass)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    cont.resume(task.result?.user)
                    print(task.result?.user)
                } else {
                    cont.resumeWithException(task.exception ?: Exception(task.result?.toString()))
                }
            }.addOnFailureListener { exp ->
                if (cont.isActive) {
                    cont.resumeWithException(exp)
                }
            }
    }

    override suspend fun signUp(name:String, email:String, pass:String,role: String): FirebaseUser? = suspendCancellableCoroutine { cont ->

        auth.createUserWithEmailAndPassword(email, pass)
            .addOnCompleteListener { task ->
                if (cont.isActive) {
                    if (task.isSuccessful) {
                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                val user = User(task.result?.user?.uid, name, email,role)
                                saveUserToDatabase(user)
                                cont.resume(task.result.user)
                            } catch (e: Exception) {
                                cont.resumeWithException(e)
                            }
                        }

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

    override suspend fun saveUserToDatabase(user: User) {

        try {
            usersRef.child(user.uid.toString()).setValue(user).await()

        }catch (e: Exception) {
            throw Exception("Failed to save user: ${e.message}")
        }
    }

    override suspend fun checkUserRole(uid: String): String {
        try {
            val snapshot = usersRef.child(uid)
                .child("role")
                .get()
                .await()

            val role = snapshot.value
            return role.toString()
        } catch (e: Exception) {
            throw Exception("checkUserRole failed: ${e.message}", e)
        }
    }

    override suspend fun getCurrentUserToken(): String {

        val user = auth.currentUser

        try{
            val tokenResult = user?.getIdToken(true)?.await()
            return tokenResult.toString()
        }catch(e: Exception){
            throw Exception("Token retrieval error: ${e.message}")
        }
    }

    override suspend fun getCurrentUserUser(uid: String): User {

        try {
            val snapshot = usersRef
                .child(uid)
                .get()
                .await()
            print(snapshot)
            val map = snapshot.value as? Map<*, *> ?: throw Exception("Invalid user data")
            print(map)
            return User(uid = map["uid"].toString(), name = map["name"].toString(), email = map["email"].toString(),role = map["role"].toString())
        } catch (e: Exception) {
            throw Exception("checkUserRole failed: ${e.message}", e)
        }
    }

    override suspend fun signOut(context: Context) {
        try {
            auth.signOut()
            var sharedPreferences = createEncryptedPreferences(context)
            sharedPreferences.edit().remove("token").apply()
            sharedPreferences.edit().remove("currentUid").apply()
            sharedPreferences.edit().remove("name").apply()
            sharedPreferences.edit().remove("role").apply()
            sharedPreferences.edit().remove("logedIn").apply()


        } catch (e: FirebaseAuthException) {
            throw Exception("Sign out failed: ${e.message}")
        } catch (e: Exception) {
            throw Exception("Unexpected error during sign out: ${e.message}")
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


