package com.thesis.routegenerator.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.signature.ObjectKey
import com.thesis.routegenerator.R
import com.thesis.routegenerator.model.PlaceType
import com.thesis.routegenerator.ui.PriorityAndFrequencyFragment
import kotlinx.android.synthetic.main.layout_priority_place_item.view.*

/**
 * An adapter class for the recycler view in PriorityAndFrequencyFragment
 * @param fragment: an object of PriorityAndFrequencyFragment
 * @param priorityList: A list of items in the priorityList
 * @param context: Context of given fragment
 */
class SetPriorityAdapter(val fragment: PriorityAndFrequencyFragment, val priorityList: MutableList<PlaceType>,
                         val context: Context) : RecyclerView.Adapter<SetPriorityAdapter.SetPriorityViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SetPriorityViewHolder {
        val view : View = LayoutInflater.from(context).inflate(R.layout.layout_priority_place_item, parent, false)
        return SetPriorityViewHolder(view)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: SetPriorityViewHolder, position: Int) {
        val placeType : PlaceType = priorityList[position]

        holder.priorityPlaceType.text = placeType.placeName
        Glide.with(this.context)
            .load(placeType.placeIcon)
            .signature(ObjectKey(System.currentTimeMillis()))
            .into(holder.priorityPlaeTypeIcon)

        holder.move_icon.setOnTouchListener { _, event ->
            // Handling drag and drop event
            if(event.actionMasked== MotionEvent.ACTION_DOWN){
                fragment.touchHelper?.startDrag(holder)
            }
            false
        }
    }

    override fun getItemCount(): Int {
        return priorityList.size
    }


    inner class SetPriorityViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val priorityPlaceType = itemView.tv_priority_place_type
        val priorityPlaeTypeIcon = itemView.tv_priority_place_type_icon
        val move_icon = itemView.iv_move
    }

}
