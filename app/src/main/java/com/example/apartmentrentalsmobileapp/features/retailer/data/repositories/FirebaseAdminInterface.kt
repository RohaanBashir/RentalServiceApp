package com.example.apartmentrentalsmobileapp.features.retailer.data.repositories

import com.example.apartmentrentalsmobileapp.features.retailer.entity.Apartment

interface FirebaseAdminInterface {

    suspend fun addApartment(apartment: Apartment)
    suspend fun deleteApartment(apartmentId: String)
    suspend fun getAllApartments() : MutableList<Apartment>

}