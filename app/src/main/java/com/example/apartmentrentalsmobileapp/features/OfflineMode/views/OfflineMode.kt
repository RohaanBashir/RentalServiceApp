package com.example.apartmentrentalsmobileapp.features.OfflineMode.views

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.apartmentrentalsmobileapp.R
import com.example.apartmentrentalsmobileapp.features.OfflineMode.view_model.OfflineModeViewModel
import com.example.apartmentrentalsmobileapp.features.normal_user.view_model.NormalUserViewModel
import com.google.android.material.snackbar.Snackbar

class OfflineMode : ComponentActivity() {

    private lateinit var recyclerView: RecyclerView
    private val viewModel: OfflineModeViewModel by viewModels()

    private val adapter = OfflineApartmentAdapter { apartment ->
        // Handle item click here if needed
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_offline_mode)


        viewModel.isOffline.observe(this) { value ->
            if(value == true){
                val rootView = findViewById<View>(android.R.id.content)
                Snackbar.make(rootView, "Offline Mode!", Snackbar.LENGTH_SHORT).show()
            }
        }

        recyclerView = findViewById(R.id.propertyListoffline)


        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter


        viewModel.cachedApartments.observe(this) { value->
            adapter.submitList(value)
        }

    }

}
