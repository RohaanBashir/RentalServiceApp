package com.example.apartmentrentalsmobileapp.features.retailer.data.repositories

import com.example.apartmentrentalsmobileapp.features.retailer.entity.Apartment

interface RoomDatabaseAdminInterface {

    suspend fun saveAllCachedApartments(apartments : MutableList<Apartment>)
}