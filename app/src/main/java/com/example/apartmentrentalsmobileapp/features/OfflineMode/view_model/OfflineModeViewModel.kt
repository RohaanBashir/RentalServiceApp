package com.example.apartmentrentalsmobileapp.features.OfflineMode.view_model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apartmentrentalsmobileapp.features.OfflineMode.model.OfflineModeRoomImp
import com.example.apartmentrentalsmobileapp.features.retailer.entity.Apartment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class OfflineModeViewModel(application: Application) : AndroidViewModel(application) {


    val cachedApartments = MutableLiveData<List<Apartment>>()
    var testLive = MutableLiveData<Boolean>()
    var offlineModeInterface = OfflineModeRoomImp(application)
    var isOffline : MutableLiveData<Boolean> = MutableLiveData<Boolean>()


    init {
        viewModelScope.launch {
            getCachedApartments()
            isOffline.postValue(true)
        }
    }

    fun getCachedApartments(){
       viewModelScope.launch(Dispatchers.IO){
           try {
               cachedApartments.postValue(offlineModeInterface.fetchApartmentsFromRoomDatabase())
           } catch (e: Exception) {
           } finally {

           }

       }


    }






}