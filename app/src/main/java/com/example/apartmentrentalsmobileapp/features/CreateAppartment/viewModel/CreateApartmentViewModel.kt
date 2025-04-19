package com.example.apartmentrentalsmobileapp.features.CreateAppartment.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apartmentrentalsmobileapp.features.CreateAppartment.data.data.CreateApartmentImplementation
import com.example.apartmentrentalsmobileapp.features.retailer.entity.Apartment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

class CreateApartmentViewModel : ViewModel() {


    val createApartmentInterface = CreateApartmentImplementation()
    var uploadState : MutableLiveData<Boolean> = MutableLiveData<Boolean>()
    var loading : MutableLiveData<Boolean> = MutableLiveData<Boolean>()


    fun uploadFireBase(apartment: Apartment){

        assignUUID(apartment)
        loading.value = true
        viewModelScope.launch(Dispatchers.IO) { // Move to IO thread
            try {
                createApartmentInterface.uploadToFireBase(apartment)
                withContext(Dispatchers.Main) {
                    uploadState.value = true
                    loading.value = false
                }
            } catch (e: Exception) {
                Log.e("UploadError", "Failed: ${e.message}")
                withContext(Dispatchers.Main) {
                    uploadState.value = false
                    loading.value = false
                }
            }
        }

    }

    fun updateApartment(apartment: Apartment){

        loading.value = true
        viewModelScope.launch(Dispatchers.IO) { // Move to IO thread
            try {
                createApartmentInterface.updateApartment(apartment)
                withContext(Dispatchers.Main) {
                    uploadState.value = true
                    loading.value = false
                }
            } catch (e: Exception) {
                Log.e("UploadError", "Failed: ${e.message}")
                withContext(Dispatchers.Main) {
                    uploadState.value = false
                    loading.value = false
                }
            }
        }

    }

    fun assignUUID(apartment: Apartment): Apartment {
        apartment.id = UUID.randomUUID().toString()
        return apartment
    }







}