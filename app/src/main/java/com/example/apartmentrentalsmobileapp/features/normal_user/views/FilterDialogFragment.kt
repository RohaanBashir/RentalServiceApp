import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.apartmentrentalsmobileapp.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textfield.TextInputEditText

class FilterDialogFragment : DialogFragment() {

    private var filterData: FilterData = FilterData()
    private var filterListener: FilterListener? = null

    interface FilterListener {
        fun onFilterApplied(filterData: FilterData)
    }

    fun setFilterListener(listener: FilterListener) {
        this.filterListener = listener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext(), com.google.android.material.R.style.ThemeOverlay_Material3_MaterialAlertDialog)
        val inflater = LayoutInflater.from(requireContext())
        val view = inflater.inflate(R.layout.fragment_filter_dialog, null)

        // Initialize views
        val minAreaEditText = view.findViewById<TextInputEditText>(R.id.min_area)
        val maxAreaEditText = view.findViewById<TextInputEditText>(R.id.max_area)
        val minPriceEditText = view.findViewById<TextInputEditText>(R.id.min_price)
        val maxPriceEditText = view.findViewById<TextInputEditText>(R.id.max_price)
        val roomsChipGroup = view.findViewById<ChipGroup>(R.id.rooms_chip_group)
        val clearButton = view.findViewById<MaterialButton>(R.id.clear_button)
        val applyButton = view.findViewById<MaterialButton>(R.id.apply_button)

        // Set click listeners
        clearButton.setOnClickListener {
            minAreaEditText.text?.clear()
            maxAreaEditText.text?.clear()
            minPriceEditText.text?.clear()
            maxPriceEditText.text?.clear()
            roomsChipGroup.clearCheck()
            filterData = FilterData()
        }

        applyButton.setOnClickListener {
            try {
                filterData = FilterData(
                    minArea = minAreaEditText.text.toString().toFloatOrNull(),
                    maxArea = maxAreaEditText.text.toString().toFloatOrNull(),
                    minPrice = minPriceEditText.text.toString().toFloatOrNull(),
                    maxPrice = maxPriceEditText.text.toString().toFloatOrNull(),
                    rooms = when (roomsChipGroup.checkedChipId) {
                        View.NO_ID -> null
                        else -> {
                            val chip = view.findViewById<Chip>(roomsChipGroup.checkedChipId)
                            if (chip.text == "4+") 4 else chip.text.toString().toInt()
                        }
                    }
                )

                filterListener?.onFilterApplied(filterData)
                dismiss()
            } catch (e: NumberFormatException) {
                Toast.makeText(requireContext(), "Please enter valid numbers", Toast.LENGTH_SHORT).show()
            }
        }

        builder.setView(view)
        return builder.create()
    }

    data class FilterData(
        val minArea: Float? = null,
        val maxArea: Float? = null,
        val minPrice: Float? = null,
        val maxPrice: Float? = null,
        val rooms: Int? = null
    )
}