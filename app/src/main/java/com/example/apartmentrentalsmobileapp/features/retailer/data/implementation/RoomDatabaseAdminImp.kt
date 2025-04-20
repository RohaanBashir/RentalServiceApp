package com.example.apartmentrentalsmobileapp.features.retailer.data.implementation

import android.app.Application
import androidx.room.Room
import com.example.apartmentrentalsmobileapp.features.retailer.data.repositories.RoomDatabaseAdminInterface
import com.example.apartmentrentalsmobileapp.features.retailer.entity.Apartment
import com.example.apartmentrentalsmobileapp.features.retailer.entity.toCached

class RoomDatabaseAdminImp(application: Application) : RoomDatabaseAdminInterface{

    private val database = DatabaseProvider.getDatabase(application)
    val apartmentDao = database.cachedApartmentDao()

    override suspend fun saveAllCachedApartments(apartments: MutableList<Apartment>) {

        val cachedApartments = apartments.map { it -> it.toCached() }
        apartmentDao.insertApartments(cachedApartments)
    }
}