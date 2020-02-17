package com.thesis.routegenerator.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import com.github.vipulasri.timelineview.TimelineView
import com.thesis.routegenerator.R
import com.thesis.routegenerator.model.TimeLineModel
import kotlinx.android.synthetic.main.layout_map_timeline.view.*


/**
 * An adapter class for the recycler view in the bottom sheet in MapFragment
 * @param mFeedList: A list of items in the timeLineList
 */
class MapTimeLineAdapter(private val mFeedList: List<TimeLineModel>, val context : Context) : RecyclerView.Adapter<MapTimeLineAdapter.TimeLineViewHolder>() {

    private lateinit var mLayoutInflater: LayoutInflater

    override fun getItemViewType(position: Int): Int {
        return TimelineView.getTimeLineViewType(position, itemCount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeLineViewHolder {

        if(!::mLayoutInflater.isInitialized) {
            mLayoutInflater = LayoutInflater.from(parent.context)
        }

        return TimeLineViewHolder(mLayoutInflater.inflate(R.layout.layout_map_timeline, parent, false), viewType)
    }

    override fun onBindViewHolder(holder: TimeLineViewHolder, position: Int) {

        val timeLineModel = mFeedList[position]

        holder.place_name.text = timeLineModel.place_name
        holder.place_type.text = timeLineModel.place_type
        holder.expected_arrival.text = timeLineModel.expected_arrival
        holder.expected_departure.text = timeLineModel.expected_departure
        holder.timeline.marker = getDrawable(holder.itemView.context, timeLineModel.place_icon)

    }

    override fun getItemCount() = mFeedList.size


    private fun getDrawable(context: Context, drawableResId: Int): Drawable? {
        return VectorDrawableCompat.create(context.resources, drawableResId, context.theme)
    }


    inner class TimeLineViewHolder(itemView: View, viewType: Int) : RecyclerView.ViewHolder(itemView) {

        val place_name = itemView.place_name_tv
        var place_type= itemView.place_type_tv
        val expected_arrival = itemView.expected_arrival_tv
        val expected_departure = itemView.expected_departure_tv
        val timeline = itemView.timeline

        init {
            timeline.initLine(viewType)
        }
    }

}