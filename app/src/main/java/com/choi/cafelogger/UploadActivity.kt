package com.choi.cafelogger

import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.appcompat.app.AppCompatActivity

class UploadActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("cafeloggerDEBUG", "UploadActivity.kt")

        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_upload)

        // ----------- Dropdown Menu Lists ----------- //
        val typeOptions  = listOf("Beans", "Beverage", "Other")
        val roastOptions = listOf("Light", "Medium-Light", "Medium", "Medium-Dark" ,"Dark")
        val beverageTypeOptions = listOf(  "Latte", "Pour over", "Cappuccino", "Cortado", "Espresso",
                                    "Macchiato", "Americano")

        val typeAdapter  = ArrayAdapter(this, R.layout.dropdown_item, typeOptions)
        val roastAdapter = ArrayAdapter(this, R.layout.dropdown_item, roastOptions)
        val beverageTypeAdapter = ArrayAdapter(this, R.layout.dropdown_item, beverageTypeOptions)

        val actvType            = findViewById<AutoCompleteTextView>(R.id.actvType)
        val actvRoast           = findViewById<AutoCompleteTextView>(R.id.actvRoast)
        val actvBeverageStyle   = findViewById<AutoCompleteTextView>(R.id.actvBeverageStyle)

        actvType.setAdapter(typeAdapter)
        actvRoast.setAdapter(roastAdapter)
        actvBeverageStyle.setAdapter(beverageTypeAdapter)

        actvType.setOnClickListener{ actvType.showDropDown() }
        actvRoast.setOnClickListener{ actvRoast.showDropDown() }
        actvBeverageStyle.setOnClickListener { actvBeverageStyle.showDropDown() }


        // ----------- ------------------- ----------- //
    }
}
