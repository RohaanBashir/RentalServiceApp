package com.example.apartmentrentalsmobileapp.features.normal_user.data

import com.example.apartmentrentalsmobileapp.features.retailer.entity.Apartment
import com.example.apartmentrentalsmobileapp.secrets.MySecrets
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await


class NormalUserFireBaseImp : NormalUserFirebaseInterface {

    private val secrets = MySecrets()
    val database = FirebaseDatabase.getInstance(secrets.firebaseDatabaseReferenceLink).getReference("apartments")

    override suspend fun getAllApartments(): MutableList<Apartment> {
        try {
            // Retrieves all apartments under "Apartments"
            val snapshot = database
                .get()
                .await()

            val apartments = mutableListOf<Apartment>()
            snapshot.children.forEach { child ->
                child.getValue(Apartment::class.java)?.let { apt ->
                    apartments.add(apt)
                }
            }
            return apartments
        } catch (e: Exception) {
            throw Exception("Failed to fetch apartments: ${e.message}")
        }
    }
}