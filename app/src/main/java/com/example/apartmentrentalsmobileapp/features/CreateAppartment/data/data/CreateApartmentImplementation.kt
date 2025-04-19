package com.example.apartmentrentalsmobileapp.features.CreateAppartment.data.data

import com.example.apartmentrentalsmobileapp.features.CreateAppartment.data.repositories.CreateApartmentInterface
import com.example.apartmentrentalsmobileapp.features.retailer.entity.Apartment
import com.example.apartmentrentalsmobileapp.secrets.MySecrets
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

class CreateApartmentImplementation : CreateApartmentInterface {


    var secrets: MySecrets = MySecrets()
    val database = FirebaseDatabase.getInstance(secrets.firebaseDatabaseReferenceLink)
    val apartmentRef = database.getReference("apartments")

    override suspend fun uploadToFireBase(apartment: Apartment) {
        try {
            apartmentRef.child(apartment.id).setValue(apartment).await()
        } catch (e: Exception) {
            throw Exception("Failed to upload apartment: ${e.message}", e)
        }
    }

    override suspend fun updateApartment(apartment: Apartment) {
        try {
            apartmentRef
                .child(apartment.id)
                .setValue(apartment)
                .await()
        } catch (e: Exception) {
            throw Exception("Failed to update apartment [${apartment.id}]: ${e.message}", e)
        }
    }

}