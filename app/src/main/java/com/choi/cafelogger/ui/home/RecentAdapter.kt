package com.choi.cafelogger.ui.home

import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.choi.cafelogger.R
import com.choi.cafelogger.model.UploadItem

class RecentAdapter(
    private val onClick: (UploadItem) -> Unit
) : ListAdapter<UploadItem, RecentAdapter.VH>(Diff) {

    object Diff : DiffUtil.ItemCallback<UploadItem>() {
        override fun areItemsTheSame(a: UploadItem, b: UploadItem) = a.timestamp == b.timestamp
        override fun areContentsTheSame(a: UploadItem, b: UploadItem) = a == b
    }

    inner class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val img: ImageView = itemView.findViewById(R.id.imgThumb)
        private val caption: TextView = itemView.findViewById(R.id.tvCaption)

        fun bind(item: UploadItem) {
            val uri = item.imageUri?.let { Uri.parse(it) }

            img.load(uri) {
                crossfade(true)
            }
            caption.text = when {
                !item.drinkStyle.isNullOrBlank() -> item.drinkStyle
                !item.type.isNullOrBlank() -> item.type
                else -> "Upload"
            }
            itemView.setOnClickListener { onClick(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recent_upload, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) =
        holder.bind(getItem(position))
}
