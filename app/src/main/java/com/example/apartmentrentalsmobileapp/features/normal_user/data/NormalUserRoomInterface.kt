package com.example.apartmentrentalsmobileapp.features.normal_user.data

import com.example.apartmentrentalsmobileapp.features.retailer.entity.Apartment

interface NormalUserRoomInterface {

    suspend fun saveAllCachedApartments(apartments : MutableList<Apartment>)
}