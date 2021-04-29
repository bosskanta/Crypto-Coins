package com.example.crypytocoins.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import coil.ImageLoader
import coil.decode.SvgDecoder
import coil.request.LoadRequest
import com.example.crypytocoins.R
import com.example.crypytocoins.models.Coin
import com.example.crypytocoins.models.ViewType
import com.squareup.picasso.Picasso


class RecyclerViewAdapter(private val context: Context, private val activity: AppCompatActivity) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val list = mutableListOf<Coin>()

    fun setList(coinList: MutableList<Coin>) {
        list.clear()
        list.addAll(coinList)
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return when (list[position].viewType) {
            ViewType.DESCRIPTION -> ViewType.DESCRIPTION.ordinal
            ViewType.TITLE_ONLY -> ViewType.TITLE_ONLY.ordinal
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ViewType.DESCRIPTION.ordinal -> {
                DescriptionViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.coins_with_description, parent, false)
                )
            }
            else -> {
                TitleOnlyViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.coins_title_only, parent, false)
                )
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is DescriptionViewHolder -> holder.apply {
                titleTextView.text = list[position].name
                descrTextView.text = list[position].description
                setIcon(iconImageView, position)
            }
            is TitleOnlyViewHolder -> holder.apply {
                titleTextView.text = list[position].name
                setIcon(iconImageView, position)
            }
        }

    }

    override fun getItemCount(): Int = list.size

    fun setIcon(iconImageView: AppCompatImageView, position: Int) {
        val iconSize = context.resources.getDimension(R.dimen.icon_size).toInt()

        // Pre-process URL string
        list[position].iconUrl = list[position].iconUrl.replace("?size=48x48", "")
        if (list[position].iconUrl.contains(".svg")) {
            list[position].iconType = "vector"
        }

        if (list[position].iconType == "vector") {
            // svg image loader
            iconImageView.loadSvg(list[position].iconUrl)
        } else {
            // png, jpg image loader
            Picasso.get()
                .load(list[position].iconUrl)
                .resize(iconSize, iconSize)
                .centerCrop()
                .into(iconImageView)
        }
    }

    inner class DescriptionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val iconImageView: AppCompatImageView = view.findViewById(R.id.imageview_coin)
        val titleTextView: TextView = view.findViewById(R.id.textview_coin_title)
        val descrTextView: TextView = view.findViewById(R.id.textview_coin_descr)
    }

    inner class TitleOnlyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val iconImageView: AppCompatImageView = view.findViewById(R.id.imageview_coin_2)
        val titleTextView: TextView = view.findViewById(R.id.textview_coin_title_2)
    }
}

// Extension function, for AppCompatImageView
fun AppCompatImageView.loadSvg(myUrl: String?) {
    myUrl?.let {
        val imageLoader = ImageLoader.Builder(this.context)
            .componentRegistry {
                add(SvgDecoder(this@loadSvg.context))
            }
            .build()
        val request = LoadRequest.Builder(this.context)
            .data(it)
            .target(this)
            .build()
        imageLoader.execute(request)
    }
}