package com.example.viewmodel
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.example.apartmentrentalsmobileapp.features.auth.entities.User
import com.example.apartmentrentalsmobileapp.features.auth.model.auth_repo.FirebaseAuthInterface
import com.example.apartmentrentalsmobileapp.features.auth.model.data.FirebaseAuthImp
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
    val auth: FirebaseAuthInterface = FirebaseAuthImp(application)


    init {
        viewModelScope.launch {
            refreshApartments()
        }
    }

    fun deleteApartment(apartment: Apartment){

        val currentUserId = createEncryptedPreferences(getApplication()).getString("currentUid", "null")
        viewModelScope.launch(Dispatchers.IO) {
            loading.postValue(true)
            try {
                if(currentUserId == apartment.ownerId){
                    if(NetworkUtils.isInternetAvailable(getApplication())){
                        firebaseRepo.deleteApartment(apartment.id)
                        refreshApartments()
                    }
                }else{
                    errorMessage.postValue("This does not belongs to you...")
                }

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

    fun createEncryptedPreferences(context: Context): SharedPreferences {
        val masterKey = MasterKey.Builder(context.applicationContext)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        return EncryptedSharedPreferences.create(
            context.applicationContext,
            "secure_prefs",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

}
