package com.thesis.routegenerator.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.signature.ObjectKey
import com.thesis.routegenerator.R
import com.thesis.routegenerator.model.PlaceDetailsIcon
import kotlinx.android.synthetic.main.layout_place_details.view.*

/**
 * An adapter class for the recycler view in placeDetailsFragment
 * @param placeDetailsIconList: List of items in the recycler view
 * @param context: Context of given fragment
 */
class PlaceDetailsAdapter(private val placeDetailsIconList : MutableList<PlaceDetailsIcon>,
                           private val context: Context) : RecyclerView.Adapter<PlaceDetailsAdapter.PlaceDetailsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceDetailsViewHolder {
        val view : View = LayoutInflater.from(context).inflate(R.layout.layout_place_details, parent, false)
        return PlaceDetailsViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlaceDetailsViewHolder, position: Int) {
        val placeDetailsIcon : PlaceDetailsIcon  = placeDetailsIconList[position]

        Glide.with(this.context)
            .load(placeDetailsIcon.placeDetailsIcon)
            .signature(ObjectKey(System.currentTimeMillis()))
            .into(holder.placeDetailsIv)

        holder.placeDetailsTv.text = placeDetailsIcon.placeDetailsName

    }

    override fun getItemCount(): Int {
        return placeDetailsIconList.size
    }

    inner class PlaceDetailsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val placeDetailsIv = itemView.place_details_iv
        val placeDetailsTv = itemView.place_details_tv

    }

}