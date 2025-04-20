package com.example.apartmentrentalsmobileapp.features.CreateAppartment.views

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.example.apartmentrentalsmobileapp.R
import com.example.apartmentrentalsmobileapp.features.CreateAppartment.viewModel.CreateApartmentViewModel
import com.example.apartmentrentalsmobileapp.features.retailer.entity.Apartment
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar

class CreateApartmentActivity : AppCompatActivity() {

    private lateinit var selectedImageView: ImageView  // Optional: if you want to show image
    var imgUri : Uri? = null


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_apartment)

        val viewModel = CreateApartmentViewModel()

        // ui components
        val uploadBtn      = findViewById<FloatingActionButton>(R.id.uploadButton)
        val description    = findViewById<TextView>(R.id.descriptionInput)
        val area           = findViewById<TextView>(R.id.areaSizeInput)
        val noRooms        = findViewById<TextView>(R.id.roomsInput)
        val price          = findViewById<TextView>(R.id.priceInput)
        val add            = findViewById<TextView>(R.id.submitButton)
        val progress       = findViewById<ProgressBar>(R.id.submitProgress)
        selectedImageView  = findViewById(R.id.apartmentImage)

        val editMode = intent.getStringExtra("editMode") == "true"





        viewModel.loading.observe(this) { isLoading ->
            if (isLoading) {
                add.text = ""
                add.isEnabled = false
                progress.visibility = View.VISIBLE
            } else {
                add.text = if (editMode) "Edit Apartment" else "Add Apartment"
                add.isEnabled = true
                progress.visibility = View.GONE
            }
        }

        if (editMode) {
            add.text = "Edit Apartment"
            val apartmentId = intent.getStringExtra("apartmentId")!!

            // pull everything out of the Intent
            imgUri = intent.getStringExtra("imgUrl")?.toUri()
            description.text = intent.getStringExtra("description")
            area.text        = intent.getStringExtra("area")
            noRooms.text     = intent.getStringExtra("rooms")
            price.text       = intent.getStringExtra("price")

            //showing the image
            imgUri?.let { selectedImageView.setImageURI(it) }

            //new image listener
            uploadBtn.setOnClickListener {
                ImagePicker.with(this)
                    .cropSquare()
                    .compress(1024)
                    .maxResultSize(1080, 1080)
                    .start()
            }

            add.setOnClickListener {
                if (description.text.isNotBlank() && area.text.isNotBlank()
                    && noRooms.text.isNotBlank() && price.text.isNotBlank()
                    && imgUri != null
                ) {
                    if(NetworkUtils.isInternetAvailable(this)){
                        val updated = Apartment(
                            id           = apartmentId,
                            imageUrl     = imgUri.toString(),
                            description  = description.text.toString(),
                            areaSize     = area.text.toString(),
                            rooms        = noRooms.text.toString(),
                            pricePerMonth= price.text.toString(),
                            ownerId      = intent.getStringExtra("ownerId")!!
                        )
                        viewModel.updateApartment(updated)
                        finish()
                    }else{
                        val rootView = findViewById<View>(android.R.id.content)
                        Snackbar.make(rootView, "Check you internet connection and try again ...", Snackbar.LENGTH_SHORT).show()
                    }

                } else {
                    Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                }
            }

        }
    else{
            viewModel.loading.observe(this) { isLoading->
                if(isLoading){
                    add.text = ""
                    add.isEnabled = false
                    progress.visibility = View.VISIBLE
                }else{
                    add.text = "Add Apartment"
                    add.isEnabled = true
                    progress.visibility = View.GONE
                }


            }

            selectedImageView = findViewById(R.id.apartmentImage)

            uploadBtn.setOnClickListener {
                ImagePicker.Companion.with(this)
                    .crop().cropSquare() // Crop image (Optional)
                    .compress(1024)	    // Final image size in KB
                    .maxResultSize(1080, 1080)	// Final image resolution
                    .start()
            }

            add.setOnClickListener {
                if(!description.text.isEmpty() && !area.text.isEmpty() && !noRooms.text.isEmpty() &&
                    !price.text.isEmpty() && imgUri != null){
                    //getting owner id
                    var ownerId = intent.getStringExtra("ownerId")
                    //creating object
                    var apartment: Apartment = Apartment("",imgUri.toString(),description.text.toString(),
                        area.text.toString(),noRooms.text.toString(),
                        price.text.toString(),ownerId.toString())
                    //calling the function
                    viewModel.uploadFireBase(apartment)
                    finish()
                }
            }
        }





    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK && data != null) {
            val uri: Uri = data.data!!
            selectedImageView.setImageURI(uri) // Optional preview
            Toast.makeText(this, "Image Selected: $uri", Toast.LENGTH_SHORT).show()
            imgUri = uri
        } else if (resultCode == ImagePicker.Companion.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.Companion.getError(data), Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
        }
    }
}