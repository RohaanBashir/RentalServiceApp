package com.example.apartmentrentalsmobileapp.features.OfflineMode.model

import com.example.apartmentrentalsmobileapp.features.retailer.entity.Apartment

interface OfflineModeRoomInterface {

    suspend fun fetchApartmentsFromRoomDatabase(): List<Apartment>
}