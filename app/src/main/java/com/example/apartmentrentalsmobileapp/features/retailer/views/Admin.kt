package com.example.apartmentrentalsmobileapp.features.retailer.views
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.apartmentrentalsmobileapp.R
import com.example.apartmentrentalsmobileapp.features.CreateAppartment.views.CreateApartmentActivity
import com.example.apartmentrentalsmobileapp.features.auth.model.auth_repo.FirebaseAuthInterface
import com.example.apartmentrentalsmobileapp.features.auth.model.data.FirebaseAuthImp
import com.example.apartmentrentalsmobileapp.features.auth.views.LoginPage
import com.example.viewmodel.ApartmentViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.InetSocketAddress
import java.net.Socket

class Admin : AppCompatActivity() {

    private val viewModel: ApartmentViewModel by viewModels()
    private lateinit var adapter: ApartmentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        //ui components

        val name = findViewById<TextView>(R.id.Name)
        name.text = intent.getStringExtra("name")
        var authInterface: FirebaseAuthInterface = FirebaseAuthImp(application)

        var btnSignOut = findViewById<ImageButton>(R.id.btnIcon)


        btnSignOut.setOnClickListener {
            if(NetworkUtils.isInternetAvailable(this)){
                lifecycleScope.launch(Dispatchers.IO) {
                    authInterface.signOut(application)
                }
                var intent = Intent(this, LoginPage::class.java)
                startActivity(intent)
                finish()
            }else{
                Snackbar.make(findViewById(android.R.id.content), "Check your internet connection and try again...", Snackbar.LENGTH_SHORT).show()
            }

        }


        viewModel.loading.observe(this) { value ->

            if(value == true){
                val rootView = findViewById<View>(android.R.id.content)
                Snackbar.make(rootView, "Loading Apartments...", Snackbar.LENGTH_SHORT).show()
            }else{

                val rootView = findViewById<View>(android.R.id.content)
                Snackbar.make(rootView, "Apartments loaded!", Snackbar.LENGTH_SHORT).show()
            }
        }

        viewModel.errorMessage.observe(this) { value ->
            val rootView = findViewById<View>(android.R.id.content)
            Snackbar.make(rootView, value.toString(), Snackbar.LENGTH_SHORT).show()
         }

        // Setup RecyclerView
        val recyclerView = findViewById<RecyclerView>(R.id.propertyList)
        adapter = ApartmentAdapter(
            onEditClick = { apartment ->
                if(NetworkUtils.isInternetAvailable(this)){
                    if(viewModel.createEncryptedPreferences(this).getString("currentUid","null") == apartment.ownerId){
                        val intent = Intent(this, CreateApartmentActivity::class.java)
                        intent.putExtra("editMode", "true")
                        intent.putExtra("apartmentId", apartment.id)
                        intent.putExtra("imgUrl", apartment.imageUrl)
                        intent.putExtra("description", apartment.description)
                        intent.putExtra("imgUrl", apartment.imageUrl)
                        intent.putExtra("area", apartment.areaSize)
                        intent.putExtra("rooms", apartment.rooms)
                        intent.putExtra("price", apartment.pricePerMonth)
                        intent.putExtra("ownerId", apartment.ownerId)
                        startActivity(intent)
                    }else{
                        Snackbar.make(findViewById(android.R.id.content), "This not belongs to you", Snackbar.LENGTH_SHORT).show()
                    }
                }else{
                    Snackbar.make(findViewById(android.R.id.content), "Check your internet connection and try again...", Snackbar.LENGTH_SHORT).show()

                }

            },
            onDeleteClick = { apartment ->

                if(NetworkUtils.isInternetAvailable(this)){
                    AlertDialog.Builder(this)
                        .setTitle("Delete Apartment")
                        .setMessage("Are you sure you want to delete this apartment?")
                        .setPositiveButton("Delete") { dialog, _ ->
                            if(NetworkUtils.isInternetAvailable(this)){
                                viewModel.deleteApartment(apartment)
                                dialog.dismiss()
                            }
                        }
                        .setNegativeButton("Cancel") { dialog, _ ->
                            dialog.dismiss()
                        }.show()
                }else{
                    Snackbar.make(findViewById(android.R.id.content), "Check your internet connection and try again...", Snackbar.LENGTH_SHORT).show()

                }

            }
        )
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // Observe data
        viewModel.apartments.observe(this) { list ->
            adapter.submitList(list)
        }

        // FloatingActionButton to create new apartment
        val fab = findViewById<FloatingActionButton>(R.id.floatingButton)
        fab.setOnClickListener {
            if(NetworkUtils.isInternetAvailable(this)){
                val ownerId = intent.getStringExtra("uid")
                Intent(this, CreateApartmentActivity::class.java).also { intent ->
                    intent.putExtra("ownerId", ownerId)
                    startActivity(intent)
                }
            }else{
                Snackbar.make(findViewById(android.R.id.content), "Check your internet connection and try again...", Snackbar.LENGTH_SHORT).show()
            }

        }
    }
    suspend fun hasActualInternet(): Boolean = withContext(Dispatchers.IO) {
        return@withContext try {
            val socket = Socket()
            socket.connect(InetSocketAddress("8.8.8.8", 53), 1500) // Google DNS
            socket.close()
            true
        } catch (e: Exception) {
            false
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshApartments()
    }

    override fun onStop() {
        super.onStop()
        viewModel.saveCachedApartmentToRoom()
    }
}

