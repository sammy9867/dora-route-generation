package com.thesis.routegenerator.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.signature.ObjectKey
import com.thesis.routegenerator.R
import com.thesis.routegenerator.model.PlaceType
import kotlinx.android.synthetic.main.layout_place_type_item.view.*
import org.jetbrains.anko.textColor

/**
 * An adapter class for the recycler view in MultiSelectionFragment
 * @param getSelectedItems: A list of items that are selected.
 * @param defaultPlaceTypeList: A list of default GooglePlace items
 * @param context: Context of given fragment
 */
class MultiSelectionAdapter(private val getSelectedItems : MutableList<String>, private val defaultPlaceTypeList : MutableList<PlaceType>,
                            private val context: Context) : RecyclerView.Adapter<MultiSelectionAdapter.MultiSelectionViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MultiSelectionViewHolder {
        val view : View = LayoutInflater.from(context).inflate(R.layout.layout_place_type_item, parent, false)
        return MultiSelectionViewHolder(view)
    }

    override fun onBindViewHolder(holder: MultiSelectionViewHolder, position: Int) {

        val placeType : PlaceType  = defaultPlaceTypeList[position]
        holder.placeTypeTv.text = placeType.placeName

        if(placeType.isSelected){
            Glide.with(this.context)
                .load(R.drawable.ic_check_white)
                .signature(ObjectKey(System.currentTimeMillis()))
                .into(holder.placeIcon)

             holder.placeIcon.background = context.getDrawable(R.drawable.circle_border_white)
             holder.placeTypeTv.textColor = context.getColor(R.color.white)
             holder.itemView.setBackgroundColor(context.getColor(R.color.black))


        }else{
            Glide.with(this.context)
                .load(placeType.placeIcon)
                .signature(ObjectKey(System.currentTimeMillis()))
                .into(holder.placeIcon)

            holder.placeIcon.background = context.getDrawable(R.drawable.circle_border)
            holder.placeTypeTv.textColor = context.getColor(R.color.black)
            holder.itemView.setBackgroundColor(Color.WHITE)
        }

        //After onClick
        holder.itemView.setOnClickListener {

            val selectedPlaceName = placeType.placeName
            if(getSelectedItems.contains(selectedPlaceName)){
                placeType.isSelected = false
                getSelectedItems.remove(selectedPlaceName)
            }else{
                placeType.isSelected = true
                getSelectedItems.add(selectedPlaceName)
            }

            if(placeType.isSelected){
                Glide.with(this.context)
                    .load(R.drawable.ic_check_white)
                    .signature(ObjectKey(System.currentTimeMillis()))
                    .into(holder.placeIcon)

                holder.placeIcon.background = context.getDrawable(R.drawable.circle_border_white)
                holder.placeTypeTv.textColor = context.getColor(R.color.white)
                holder.itemView.setBackgroundColor(context.getColor(R.color.black))



            }else{
                Glide.with(this.context)
                    .load(placeType.placeIcon)
                    .signature(ObjectKey(System.currentTimeMillis()))
                    .into(holder.placeIcon)

                holder.placeIcon.background = context.getDrawable(R.drawable.circle_border)
                holder.placeTypeTv.textColor = context.getColor(R.color.black)
                holder.itemView.setBackgroundColor(Color.WHITE)
            }
        }
    }


    override fun getItemCount(): Int {
        return defaultPlaceTypeList.size
    }

    inner class MultiSelectionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        val placeTypeTv = itemView.tv_place_type
        val placeIcon = itemView.place_type_icon
    }

}