package com.example.crypytocoins.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.crypytocoins.R
import com.example.crypytocoins.models.Coin
import com.example.crypytocoins.models.ViewType

class RecyclerViewAdapter(context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

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
                title.text = list[position].name
                descr.text = list[position].description
            }
            is TitleOnlyViewHolder -> holder.apply {
                title.text = list[position].name
            }
        }
    }

    override fun getItemCount(): Int = list.size

    inner class DescriptionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image = view.findViewById<ImageView>(R.id.imageview_coin)
        val title: TextView = view.findViewById<TextView>(R.id.textview_coin_title)
        val descr: TextView = view.findViewById<TextView>(R.id.textview_coin_descr)
    }

    inner class TitleOnlyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image = view.findViewById<ImageView>(R.id.imageview_coin_2)
        val title: TextView = view.findViewById<TextView>(R.id.textview_coin_title_2)
    }
}