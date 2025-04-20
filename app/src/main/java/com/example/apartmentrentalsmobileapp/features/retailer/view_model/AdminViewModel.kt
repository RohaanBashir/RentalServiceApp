package com.example.viewmodel
import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apartmentrentalsmobileapp.features.retailer.data.implementation.FirebaseAdminImplementation
import com.example.apartmentrentalsmobileapp.features.retailer.data.implementation.RoomDatabaseAdminImp
import com.example.apartmentrentalsmobileapp.features.retailer.entity.Apartment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class ApartmentViewModel(application: Application) : AndroidViewModel(application) {

    private val firebaseRepo = FirebaseAdminImplementation()
    val apartments = MutableLiveData<List<Apartment>>()
    val loading = MutableLiveData<Boolean>()
    val errorMessage = MutableLiveData<String?>()
    private val roomDatabaseAdminInterface = RoomDatabaseAdminImp(application)


    init {
        viewModelScope.launch {
            refreshApartments()
        }
    }

    fun deleteApartment(apartmentId: String){

        viewModelScope.launch(Dispatchers.IO) {
            loading.postValue(true)
            try {
                firebaseRepo.deleteApartment(apartmentId)
                refreshApartments()
            } catch (e: Exception) {
                errorMessage.postValue(e.message)
            } finally {
                loading.postValue(false)
            }
        }
    }

    fun refreshApartments() {
        viewModelScope.launch(Dispatchers.IO) {
            loading.postValue(true)
            try {
                val list = firebaseRepo.getAllApartments()
                apartments.postValue(list)
                errorMessage.postValue(null)
            } catch (e: Exception) {
                errorMessage.postValue(e.message)
            } finally {
                loading.postValue(false)
            }
        }
    }

    fun saveCachedApartmentToRoom(){

        viewModelScope.launch(Dispatchers.IO){
            try {
                val list = getCachedApartmentList(apartments)
                roomDatabaseAdminInterface.saveAllCachedApartments(list)
                errorMessage.postValue(null)
            } catch (e: Exception) {
                errorMessage.postValue(e.message)
            } finally {
                loading.postValue(false)
            }

        }

    }

    fun getCachedApartmentList(apartment: MutableLiveData<List<Apartment>>): MutableList<Apartment> {
        return apartment.value
            ?.sortedByDescending { it.lastUpdated }
            ?.take(10)
            ?.toMutableList() ?: mutableListOf()
    }

}
