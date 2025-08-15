package com.choi.cafelogger.ui.upload

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
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

            try {
                requireContext().contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (e: SecurityException) {
                Log.e("cafeloggerDEBUG", "Failed to persist URI permission: ${e.message}")
            }

            view?.findViewById<ImageView>(R.id.ivUploadIcon)?.apply {
                setImageURI(uri)
                imageTintList = null
                scaleType = ImageView.ScaleType.CENTER_CROP
            }
        }
    }

    private val prefs by lazy {
        requireContext().getSharedPreferences("cafelogger_prefs", Context.MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enterTransition = com.google.android.material.transition.platform.MaterialSharedAxis(
            com.google.android.material.transition.platform.MaterialSharedAxis.X, /* forward = */ true
        )

        returnTransition = com.google.android.material.transition.platform.MaterialSharedAxis(
            com.google.android.material.transition.platform.MaterialSharedAxis.X, /* forward = */ false
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_upload, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // dropdown data
        val roastOptions = listOf("Light", "Medium-Light", "Medium", "Medium-Dark", "Dark")
        val typeOptions   = listOf("Beans", "Latte", "Pour over", "Cappuccino", "Cortado", "Espresso", "Macchiato", "Americano", "Other")

        // adapters
        val typeAdapter  = ArrayAdapter(requireContext(), R.layout.dropdown_item, typeOptions)
        val roastAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, roastOptions)

        // views
        val actvType          = view.findViewById<AutoCompleteTextView>(R.id.actvType)
        val actvRoast         = view.findViewById<AutoCompleteTextView>(R.id.actvRoast)
        val etLocation        = view.findViewById<TextInputEditText>(R.id.etLocation)
        val etOrigin          = view.findViewById<TextInputEditText>(R.id.etOrigin)
        val etProcess         = view.findViewById<TextInputEditText>(R.id.etProcess)
        val btnSubmit         = view.findViewById<MaterialButton>(R.id.btnSubmitUpload)
        val uploadArea        = view.findViewById<View>(R.id.flImageUpload)
        val uploadIcon        = view.findViewById<ImageView>(R.id.ivUploadIcon)

        // setup dropdowns
        actvType.setAdapter(ArrayAdapter(requireContext(), R.layout.dropdown_item, typeOptions))
        actvRoast.setAdapter(ArrayAdapter(requireContext(), R.layout.dropdown_item, roastOptions))
        listOf(actvType, actvRoast).forEach {
            it.setOnClickListener { v -> (v as AutoCompleteTextView).showDropDown() }
        }

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
                put("imageUri", selectedImageUri?.toString().orEmpty())
            }

            appendUpload(json)

            parentFragmentManager.setFragmentResult("upload_success", Bundle())
            parentFragmentManager.popBackStack()

            Toast.makeText(requireContext(), "Saved locally", Toast.LENGTH_SHORT).show()
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
}
