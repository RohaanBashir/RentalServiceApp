package com.example.apartmentrentalsmobileapp.features.normal_user.views

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.apartmentrentalsmobileapp.R
import com.example.apartmentrentalsmobileapp.features.normal_user.view_model.NormalUserViewModel
import com.example.apartmentrentalsmobileapp.features.retailer.views.UserApartmentAdapter
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.button.MaterialButton

class NormalUser : AppCompatActivity(), NormalUser.FilterDialogFragment.FilterListener {

    private val viewModel: NormalUserViewModel by viewModels()
    private lateinit var adapter: UserApartmentAdapter
    private var currentFilters = FilterDialogFragment.FilterData()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_normal_user)

        // Set name and role
        findViewById<TextView>(R.id.userName).text = intent.getStringExtra("name")
        findViewById<TextView>(R.id.userTextRole).text = "Normal User"

        val fab = findViewById<FloatingActionButton>(R.id.filterFab)

        // Sign out
        findViewById<ImageButton>(R.id.UserBtnIcon).setOnClickListener {
            finish()
        }

        // RecyclerView
        val recyclerView = findViewById<RecyclerView>(R.id.propertyList)
        adapter = UserApartmentAdapter()
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // Observe apartments
        viewModel.apartments.observe(this) { list ->
            val filteredList = list.filter { apartment ->
                (currentFilters.minArea == null || apartment.areaSize.toInt() >= currentFilters.minArea!!) &&
                        (currentFilters.maxArea == null || apartment.areaSize.toInt() <= currentFilters.maxArea!!) &&
                        (currentFilters.minPrice == null || apartment.areaSize.toInt() >= currentFilters.minPrice!!) &&
                        (currentFilters.maxPrice == null || apartment.areaSize.toInt() <= currentFilters.maxPrice!!) &&
                        (currentFilters.rooms == null || apartment.rooms.toInt() == currentFilters.rooms)
            }
            adapter.submitList(filteredList)
        }

        // Loading state
        viewModel.loading.observe(this) { isLoading ->
            val rootView = findViewById<View>(android.R.id.content)
            val message = if (isLoading) "Loading apartments..." else "Apartments loaded!"
            Snackbar.make(rootView, message, Snackbar.LENGTH_SHORT).show()
        }

        // FAB: open filter dialog
        fab.setOnClickListener {
            FilterDialogFragment().apply {
                setFilterListener(this@NormalUser)
            }.show(supportFragmentManager, "FilterDialog")
        }

        // Error messages
        viewModel.errorMessage.observe(this) { error ->
            error?.let {
                Snackbar.make(findViewById(android.R.id.content), it, Snackbar.LENGTH_LONG).show()
            }
        }
    }

    override fun onFilterApplied(filterData: FilterDialogFragment.FilterData) {
        currentFilters = filterData
        viewModel.refreshApartments()
        Toast.makeText(this, "Filters applied", Toast.LENGTH_SHORT).show()
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshApartments()
    }

    override fun onStop() {
        super.onStop()
        viewModel.saveCachedApartmentToRoom()
    }

    class FilterDialogFragment : DialogFragment() {

        data class FilterData(
            val minArea: Float? = null,
            val maxArea: Float? = null,
            val minPrice: Float? = null,
            val maxPrice: Float? = null,
            val rooms: Int? = null
        )

        private var filterData = FilterData()
        private var filterListener: FilterListener? = null

        interface FilterListener {
            fun onFilterApplied(filterData: FilterData)
        }

        fun setFilterListener(listener: NormalUser) {
            this.filterListener = listener
        }

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            val builder = AlertDialog.Builder(requireContext(), com.google.android.material.R.style.ThemeOverlay_Material3_MaterialAlertDialog)
            val inflater = LayoutInflater.from(requireContext())
            val view = inflater.inflate(R.layout.fragment_filter_dialog, null)

            val minAreaEdit = view.findViewById<TextInputEditText>(R.id.min_area)
            val maxAreaEdit = view.findViewById<TextInputEditText>(R.id.max_area)
            val minPriceEdit = view.findViewById<TextInputEditText>(R.id.min_price)
            val maxPriceEdit = view.findViewById<TextInputEditText>(R.id.max_price)
            val roomsChipGroup = view.findViewById<ChipGroup>(R.id.rooms_chip_group)
            val btnClear = view.findViewById<MaterialButton>(R.id.clear_button)
            val btnApply = view.findViewById<MaterialButton>(R.id.apply_button)

            btnClear.setOnClickListener {
                minAreaEdit.text?.clear()
                maxAreaEdit.text?.clear()
                minPriceEdit.text?.clear()
                maxPriceEdit.text?.clear()
                roomsChipGroup.clearCheck()
                filterData = FilterData()
            }

            btnApply.setOnClickListener {
                try {
                    val rooms = when (val id = roomsChipGroup.checkedChipId) {
                        View.NO_ID -> null
                        else -> {
                            val chip = view.findViewById<Chip>(id)
                            if (chip.text == "4+") 4 else chip.text.toString().toInt()
                        }
                    }

                    filterData = FilterData(
                        minArea = minAreaEdit.text.toString().toFloatOrNull(),
                        maxArea = maxAreaEdit.text.toString().toFloatOrNull(),
                        minPrice = minPriceEdit.text.toString().toFloatOrNull(),
                        maxPrice = maxPriceEdit.text.toString().toFloatOrNull(),
                        rooms = rooms
                    )
                    filterListener?.onFilterApplied(filterData)
                    dismiss()
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "Invalid input!", Toast.LENGTH_SHORT).show()
                }
            }

            builder.setView(view)
            return builder.create()
        }
    }
}