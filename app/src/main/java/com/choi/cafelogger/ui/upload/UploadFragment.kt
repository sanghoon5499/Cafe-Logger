package com.choi.cafelogger.ui.upload

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.fragment.app.Fragment
import com.choi.cafelogger.R

class UploadFragment : Fragment() {

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

        super.onViewCreated(view, savedInstanceState)

        // dropdown data
        val typeOptions  = listOf("Beans", "Beverage", "Other")
        val roastOptions = listOf("Light", "Medium-Light", "Medium", "Medium-Dark", "Dark")
        val bevOptions   = listOf("Latte", "Pour over", "Cappuccino", "Cortado", "Espresso", "Macchiato", "Americano")

        // adapters
        val typeAdapter  = ArrayAdapter(requireContext(), R.layout.dropdown_item, typeOptions)
        val roastAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, roastOptions)
        val bevAdapter   = ArrayAdapter(requireContext(), R.layout.dropdown_item, bevOptions)

        // find views via the fragment’s root view
        val actvType          = view.findViewById<AutoCompleteTextView>(R.id.actvType)
        val actvRoast         = view.findViewById<AutoCompleteTextView>(R.id.actvRoast)
        val actvBeverageStyle = view.findViewById<AutoCompleteTextView>(R.id.actvBeverageStyle)

        // set them up
        actvType.setAdapter(typeAdapter)
        actvRoast.setAdapter(roastAdapter)
        actvBeverageStyle.setAdapter(bevAdapter)

        // show dropdown on click
        actvType.setOnClickListener { actvType.showDropDown() }
        actvRoast.setOnClickListener { actvRoast.showDropDown() }
        actvBeverageStyle.setOnClickListener { actvBeverageStyle.showDropDown() }

        // …and any other view logic you had in your Activity
    }
}
