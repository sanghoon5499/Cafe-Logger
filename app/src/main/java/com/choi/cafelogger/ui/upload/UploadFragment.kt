package com.choi.cafelogger.ui.upload

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.choi.cafelogger.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import org.json.JSONArray
import org.json.JSONObject
import androidx.core.content.edit

class UploadFragment : Fragment() {

    private var selectedImageUri: Uri? = null
    private val pickPhoto = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedImageUri = uri
            view?.findViewById<ImageView>(R.id.ivUploadIcon)?.apply {
                setImageURI(uri)
                imageTintList = null
                scaleType = ImageView.ScaleType.CENTER_CROP
            }
        }
    }
    private val prefs by lazy {
        requireContext().getSharedPreferences("cafelogger_local", Context.MODE_PRIVATE)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the XML
        return inflater.inflate(R.layout.fragment_upload, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d("cafeloggerDEBUG", "UploadFragment.kt")
        Log.d("cafeloggerDEBUG", "getAllUploads:${getAllUploads()}")
        super.onViewCreated(view, savedInstanceState)

        // dropdown data
        val typeOptions  = listOf("Beans", "Beverage", "Other")
        val roastOptions = listOf("Light", "Medium-Light", "Medium", "Medium-Dark", "Dark")
        val bevOptions   = listOf("Latte", "Pour over", "Cappuccino", "Cortado", "Espresso", "Macchiato", "Americano")

        // adapters
        val typeAdapter  = ArrayAdapter(requireContext(), R.layout.dropdown_item, typeOptions)
        val roastAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, roastOptions)
        val bevAdapter   = ArrayAdapter(requireContext(), R.layout.dropdown_item, bevOptions)

        // views
        val actvType          = view.findViewById<AutoCompleteTextView>(R.id.actvType)
        val actvRoast         = view.findViewById<AutoCompleteTextView>(R.id.actvRoast)
        val actvBeverageStyle = view.findViewById<AutoCompleteTextView>(R.id.actvBeverageStyle)
        val etLocation        = view.findViewById<TextInputEditText>(R.id.etLocation)
        val etOrigin          = view.findViewById<TextInputEditText>(R.id.etOrigin)
        val etProcess         = view.findViewById<TextInputEditText>(R.id.etProcess)
        val btnSubmit         = view.findViewById<MaterialButton>(R.id.btnSubmitUpload)
        val uploadArea        = view.findViewById<View>(R.id.flImageUpload)
        val uploadIcon        = view.findViewById<ImageView>(R.id.ivUploadIcon)

        // setup dropdowns
        actvType.setAdapter(typeAdapter)
        actvRoast.setAdapter(roastAdapter)
        actvBeverageStyle.setAdapter(bevAdapter)
        actvType.setOnClickListener { actvType.showDropDown() }
        actvRoast.setOnClickListener { actvRoast.showDropDown() }
        actvBeverageStyle.setOnClickListener { actvBeverageStyle.showDropDown() }

        // open photo picker
        uploadArea.setOnClickListener {
            pickPhoto.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
        uploadIcon.setOnClickListener { uploadArea.performClick() }

        // submit -> collect -> JSON -> save/append
        btnSubmit.setOnClickListener {
            val json = JSONObject().apply {
                put("timestamp", System.currentTimeMillis())
                put("location", etLocation.text?.toString()?.trim().orEmpty())
                put("type", actvType.text?.toString()?.trim().orEmpty())
                put("roast", actvRoast.text?.toString()?.trim().orEmpty())
                put("origin", etOrigin.text?.toString()?.trim().orEmpty())
                put("process", etProcess.text?.toString()?.trim().orEmpty())
                put("drinkStyle", actvBeverageStyle.text?.toString()?.trim().orEmpty())
                put("imageUri", selectedImageUri?.toString().orEmpty())
            }

            appendUpload(json)

            Toast.makeText(requireContext(), "Saved locally", Toast.LENGTH_SHORT).show()
//            clearForm(etLocation, actvType, actvRoast, etOrigin, etProcess, actvBeverageStyle)
        }
    }

    private fun appendUpload(entry: JSONObject) {
        val key = "uploads_json"
        val existing = prefs.getString(key, "[]") ?: "[]"
        val array = try {
            JSONArray(existing)
        } catch (_: Exception) {
            JSONArray()
        }
        array.put(entry)
        prefs.edit() { putString(key, array.toString()) }
    }

    // (Optional) helper to read everything back later
    fun getAllUploads(): JSONArray {
        val key = "uploads_json"
        val existing = prefs.getString(key, "[]") ?: "[]"
        return try { JSONArray(existing) } catch (_: Exception) { JSONArray() }
    }
}
