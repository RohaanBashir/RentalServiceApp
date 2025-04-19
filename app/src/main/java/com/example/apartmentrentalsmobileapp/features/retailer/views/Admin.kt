package com.example.apartmentrentalsmobileapp.features.retailer.views
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.apartmentrentalsmobileapp.R
import com.example.apartmentrentalsmobileapp.features.CreateAppartment.views.CreateApartmentActivity
import com.example.viewmodel.ApartmentViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar

class Admin : AppCompatActivity() {

    private val viewModel: ApartmentViewModel by viewModels()
    private lateinit var adapter: ApartmentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        //ui components

        val name = findViewById<TextView>(R.id.Name)
        name.text = intent.getStringExtra("name")



        viewModel.loading.observe(this) { value ->

            if(value == true){
                val rootView = findViewById<View>(android.R.id.content)
                Snackbar.make(rootView, "Loading Apartments...", Snackbar.LENGTH_SHORT).show()
            }else{

                val rootView = findViewById<View>(android.R.id.content)
                Snackbar.make(rootView, "Apartments loaded!", Snackbar.LENGTH_SHORT).show()
            }
        }

        // Setup RecyclerView
        val recyclerView = findViewById<RecyclerView>(R.id.propertyList)
        adapter = ApartmentAdapter(
            onEditClick = { apartment ->
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
            },
            onDeleteClick = { apartment ->
                AlertDialog.Builder(this)
                    .setTitle("Delete Apartment")
                    .setMessage("Are you sure you want to delete this apartment?")
                    .setPositiveButton("Delete") { dialog, _ ->
                        viewModel.deleteApartment(apartment.id)
                        dialog.dismiss()
                    }
                    .setNegativeButton("Cancel") { dialog, _ ->


                        dialog.dismiss()
                    }
                    .show()
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
            val ownerId = intent.getStringExtra("uid")
            Intent(this, CreateApartmentActivity::class.java).also { intent ->
                intent.putExtra("ownerId", ownerId)
                startActivity(intent)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshApartments()
    }
}

