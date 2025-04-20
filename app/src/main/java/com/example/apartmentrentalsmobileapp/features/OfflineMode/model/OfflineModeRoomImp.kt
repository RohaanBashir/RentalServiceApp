package com.example.apartmentrentalsmobileapp.features.OfflineMode.model

import android.app.Application
import androidx.room.Room
import com.example.apartmentrentalsmobileapp.features.retailer.entity.Apartment

class OfflineModeRoomImp(application: Application) : OfflineModeRoomInterface {

    private val roomInstance = Room.databaseBuilder(
        application,
        AppDatabase::class.java,
        "apartment_database"
    ).build()
    private val apartmentDao = roomInstance.cachedApartmentDao()


    override suspend fun fetchApartmentsFromRoomDatabase(): List<Apartment> {

        val list: List<Apartment> = apartmentDao.getLast10Apartments().map { entity ->
            Apartment(
                id = entity.id,
                imageUrl = entity.imageUrl,
                description = entity.description,
                areaSize = entity.areaSize,
                rooms = entity.rooms,
                pricePerMonth = entity.pricePerMonth,
                ownerId = entity.ownerId,
                lastUpdated = entity.lastUpdated
            )
        }
        return list
    }
}