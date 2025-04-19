package com.example.apartmentrentalsmobileapp.features.normal_user.views

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.apartmentrentalsmobileapp.R
import com.example.apartmentrentalsmobileapp.features.normal_user.view_model.NormalUserViewModel
import com.example.apartmentrentalsmobileapp.features.retailer.views.UserApartmentAdapter
import com.google.android.material.snackbar.Snackbar

class NormalUser : AppCompatActivity() {

    private val viewModel: NormalUserViewModel by viewModels()
    private lateinit var adapter: UserApartmentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_normal_user)

        // Set name and role
        findViewById<TextView>(R.id.userName).text = intent.getStringExtra("name")
        findViewById<TextView>(R.id.userTextRole).text

        // Sign out action
        findViewById<ImageButton>(R.id.UserBtnIcon).setOnClickListener {
            finish() // or sign out logic
        }

        // Setup RecyclerView
        val recyclerView = findViewById<RecyclerView>(R.id.propertyList)
        adapter = UserApartmentAdapter()
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // Observe apartments
        viewModel.apartments.observe(this) { list ->
            adapter.submitList(list)
        }

        // Observe loading
        viewModel.loading.observe(this) { isLoading ->
            val rootView = findViewById<View>(android.R.id.content)
            if (isLoading) {
                Snackbar.make(rootView, "Loading apartments...", Snackbar.LENGTH_SHORT).show()
            } else {
                Snackbar.make(rootView, "Apartments loaded!", Snackbar.LENGTH_SHORT).show()
            }
        }

        // Observe error
        viewModel.errorMessage.observe(this) { error ->
            error?.let {
                Snackbar.make(findViewById(android.R.id.content), it, Snackbar.LENGTH_LONG).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshApartments()
    }
}
