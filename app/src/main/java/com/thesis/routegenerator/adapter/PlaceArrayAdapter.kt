package com.thesis.routegenerator.adapter

import android.content.Context
import android.graphics.Typeface
import android.text.style.StyleSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Tasks
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FetchPlaceResponse
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.thesis.routegenerator.R
import com.thesis.routegenerator.model.PlaceSearchModel
import kotlinx.android.synthetic.main.layout_place_items.view.*
import org.jetbrains.anko.onUiThread
import java.lang.Exception
import java.net.ConnectException
import java.util.*
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException


/**
 * An adapter class for the recycler view in setLocationFragment for place search.
 * @param context: Context of given fragment
 * @param onPlaceClickListener: OnClickListener object for a place
 * @param mPlacesClient: Places Client for GooglePlaces API
 */
class PlaceArrayAdapter(private val context: Context, var onPlaceClickListener: OnPlaceClickListener,val mPlacesClient: PlacesClient) : RecyclerView.Adapter<PlaceArrayAdapter.PlaceArrayViewHolder>(),
    Filterable {

    private var mContext : Context = context
    private var resultList = arrayListOf<PlaceSearchModel>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceArrayViewHolder {
        val view : View = LayoutInflater.from(context).inflate(R.layout.layout_place_items, parent, false)
        return PlaceArrayViewHolder(view, onPlaceClickListener)
    }

    override fun onBindViewHolder(holder: PlaceArrayViewHolder, position: Int) {

        val placeDataItem = resultList[position]
        if (!resultList.isNullOrEmpty()) {
            val str = placeDataItem.fullText
            Log.i("PlaceAdapter:\n", str)
            var kept = ""
            var remainder = ""
            if(str.contains(",")){
                 kept = str.substring( 0, str.indexOf(","))
                 remainder = str.substring(str.indexOf(",")+1, str.length)
            }else if(str.contains("-")){
                kept = str.substring( 0, str.indexOf("-"))
                remainder = str.substring(str.indexOf("-")+1, str.length)
            }

            holder.name.text = kept
            holder.description.text = remainder
        }

    }

    override fun getItemCount(): Int {
        return when {
            resultList.isNullOrEmpty() -> 0
            else -> resultList.size
        }
    }

    fun getAutocomplete(mPlacesClient: PlacesClient, constraint: CharSequence): List<AutocompletePrediction> {
        var list = listOf<AutocompletePrediction>()
        val token = AutocompleteSessionToken.newInstance()
        val request = FindAutocompletePredictionsRequest.builder()
            .setTypeFilter(TypeFilter.ESTABLISHMENT)
            .setSessionToken(token)
            .setQuery(constraint.toString())
            .build()
        val prediction = mPlacesClient.findAutocompletePredictions(request)
        try {
            Tasks.await(prediction, 2, TimeUnit.SECONDS)

        }catch (e : ConnectException){
            e.printStackTrace()
        } catch (e: ExecutionException) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        } catch (e: TimeoutException) {
            e.printStackTrace()
        }

        if (prediction.isSuccessful) {
            val findAutocompletePredictionsResponse = prediction.result
            findAutocompletePredictionsResponse?.let {
                list = findAutocompletePredictionsResponse.autocompletePredictions
            }
            return list
        }
        return list
    }




    override fun getFilter(): Filter {
        return object : Filter() {
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                if (results != null && results.count > 0) {
                    mContext.onUiThread {
                        notifyDataSetChanged()
                    }
                } else {
                    notifyDataSetChanged()
                }
            }

            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filterResults = FilterResults()
                if (constraint != null) {
                    resultList.clear()
                    val address = getAutocomplete(mPlacesClient, constraint.toString())
                    address.let {
                        for (i in address.indices) {
                            val item = address[i]
                            resultList.add(PlaceSearchModel(item.placeId, item.getFullText(StyleSpan(Typeface.BOLD)).toString()))
                        }
                    }
                    filterResults.values = resultList
                    filterResults.count = resultList.size
                }
                return filterResults
            }
        }
    }

   inner class PlaceArrayViewHolder(itemView: View, onPlaceClickListener:  OnPlaceClickListener): RecyclerView.ViewHolder(itemView), View.OnClickListener{
       var name = itemView.placeNameSearch
       var description = itemView.placeAddressSearch
          var onPlaceClickListener : OnPlaceClickListener

         init{
             this.onPlaceClickListener = onPlaceClickListener
             itemView.setOnClickListener(this)
         }

         override fun onClick(v: View?) {
            val item = resultList[adapterPosition]

             val placeId = item.placeId
             val placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG)
             val request : FetchPlaceRequest = FetchPlaceRequest.builder(placeId, placeFields).build()
             mPlacesClient.fetchPlace(request)
                 .addOnSuccessListener(object : OnSuccessListener<FetchPlaceResponse>{
                     override fun onSuccess(response: FetchPlaceResponse?) {
                         val place = response!!.place
                         onPlaceClickListener.click(place)
                     }
                 }).addOnFailureListener(object : OnFailureListener{
                     override fun onFailure(p0: Exception) {
                         if(p0 is ApiException){
                             Log.i("eXCEPTION", p0.message)
                         }
                     }
                 })

         }
    }

     interface OnPlaceClickListener {
        fun click(place : Place)
    }
}