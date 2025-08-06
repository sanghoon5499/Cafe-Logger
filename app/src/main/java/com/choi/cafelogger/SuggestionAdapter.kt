package com.choi.cafelogger

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken

class SuggestionAdapter(
    private val onClick: (AutocompletePrediction, AutocompleteSessionToken) -> Unit
) : RecyclerView.Adapter<SuggestionAdapter.ViewHolder>() {

    private val items = mutableListOf<AutocompletePrediction>()
    private var sessionToken: AutocompleteSessionToken? = null

    fun submitList(
        newItems: List<AutocompletePrediction>,
        token: AutocompleteSessionToken
    ) {
        items.clear()
        items.addAll(newItems)
        sessionToken = token
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_list_item_1, parent, false) as TextView
    )

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val prediction = items[position]
        holder.text.text = prediction.getPrimaryText(null)
        holder.text.setOnClickListener {
            sessionToken?.let { tok ->
                onClick(prediction, tok)
            }
        }
    }

    class ViewHolder(val text: TextView) : RecyclerView.ViewHolder(text)
}
