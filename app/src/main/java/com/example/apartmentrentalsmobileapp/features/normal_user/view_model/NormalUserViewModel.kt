package com.example.apartmentrentalsmobileapp.features.normal_user.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apartmentrentalsmobileapp.features.normal_user.data.NormalUserFireBaseImp
import com.example.apartmentrentalsmobileapp.features.retailer.data.implementation.FirebaseAdminImplementation
import com.example.apartmentrentalsmobileapp.features.retailer.entity.Apartment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NormalUserViewModel : ViewModel(){

    private val firebaseRepo = NormalUserFireBaseImp()
    val apartments = MutableLiveData<List<Apartment>>()
    val loading = MutableLiveData<Boolean>()
    val errorMessage = MutableLiveData<String?>()



    init {
        viewModelScope.launch {
            refreshApartments()
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