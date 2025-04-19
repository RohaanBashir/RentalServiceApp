package com.example.apartmentrentalsmobileapp.features.normal_user.data

import com.example.apartmentrentalsmobileapp.features.retailer.entity.Apartment

interface NormalUserFirebaseInterface {

    suspend fun getAllApartments() : MutableList<Apartment>
}