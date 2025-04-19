package com.example.viewmodel
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apartmentrentalsmobileapp.features.retailer.data.implementation.FirebaseAdminImplementation
import com.example.apartmentrentalsmobileapp.features.retailer.entity.Apartment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class ApartmentViewModel : ViewModel() {

    private val firebaseRepo = FirebaseAdminImplementation()
    val apartments = MutableLiveData<List<Apartment>>()
    val loading = MutableLiveData<Boolean>()
    val errorMessage = MutableLiveData<String?>()



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
}
