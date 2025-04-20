package com.example.apartmentrentalsmobileapp.features.normal_user.data

import AppDatabase
import android.app.Application
import androidx.room.Room
import com.example.apartmentrentalsmobileapp.features.retailer.entity.Apartment
import com.example.apartmentrentalsmobileapp.features.retailer.entity.toCached

class NormalUserRoomInterfaceImp(application: Application): NormalUserRoomInterface {

    private val database = DatabaseProvider.getDatabase(application)
    val apartmentDao = database.cachedApartmentDao()

    override suspend fun saveAllCachedApartments(apartments: MutableList<Apartment>) {
        val cachedApartments = apartments.map { it.toCached() }
        apartmentDao.insertApartments(cachedApartments)
    }


}