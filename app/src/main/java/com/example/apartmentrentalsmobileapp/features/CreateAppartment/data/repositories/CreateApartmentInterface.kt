package com.example.apartmentrentalsmobileapp.features.CreateAppartment.data.repositories

import com.example.apartmentrentalsmobileapp.features.retailer.entity.Apartment

interface CreateApartmentInterface {


    suspend fun uploadToFireBase(apartment: Apartment)
    suspend fun updateApartment(apartment: Apartment)

}