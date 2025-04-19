package com.example.apartmentrentalsmobileapp.features.retailer.data.implementation

import com.example.apartmentrentalsmobileapp.features.retailer.data.repositories.FirebaseAdminInterface
import com.example.apartmentrentalsmobileapp.features.retailer.entity.Apartment
import com.example.apartmentrentalsmobileapp.secrets.MySecrets
import com.google.firebase.Firebase
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.database
import kotlinx.coroutines.tasks.await

class FirebaseAdminImplementation : FirebaseAdminInterface {


    private val secrets = MySecrets()
    val database = FirebaseDatabase.getInstance(secrets.firebaseDatabaseReferenceLink).getReference("apartments")


    override suspend fun addApartment(apartment: Apartment) {
        try {
            // Adds a new Apartment under its unique ID
            database.child(apartment.id)
                .setValue(apartment)
                .await()
        } catch (e: Exception) {
            throw Exception("Failed to add apartment: ${e.message}")
        }
    }

    override suspend fun deleteApartment(apartmentId: String) {
        try {
            // Removes the node for this apartment ID
            database.child(apartmentId)
                .removeValue()
                .await()
        } catch (e: Exception) {
            throw Exception("Failed to delete apartment: ${e.message}")
        }
    }

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