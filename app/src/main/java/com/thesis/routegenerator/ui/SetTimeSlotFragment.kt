package com.thesis.routegenerator.ui


import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import com.shashank.sony.fancytoastlib.FancyToast

import com.thesis.routegenerator.R
import com.thesis.routegenerator.databinding.FragmentSetTimeSlotBinding
import com.thesis.routegenerator.viewmodel.CommunicatorViewModel
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.*

class SetTimeSlotFragment : Fragment() {

    private var model: CommunicatorViewModel?=null
    var timeDifference = 0

    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // binding object that holds all views in the given fragment
        val binding: FragmentSetTimeSlotBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_set_time_slot, container, false)

        // to store values that can be communicated between fragments
        model = ViewModelProviders.of(activity!!).get(CommunicatorViewModel::class.java)

        //direction changer
        model!!.setDirectionChanger(0)
        model!!.allRoutes.value = null

        //Default Values
        if(model!!.dateFrom.value == null){
            loadDefaultValues(binding)
        }else{
            setObservers(binding)
        }

        //OR User define values
        setDateFromDialog(binding)
        setTimeFromDialog(binding)
        setTimeToDialog(binding)

        // navigate back to the previous fragment
        binding.icTimePrev.setOnClickListener(
            Navigation.createNavigateOnClickListener(R.id.action_setTimeSlotFragment_to_setLocationFragment2))

        // navigate to the next fragment
        binding.icTimeNext.setOnClickListener{

            Log.i("Time From: ",model!!.dateforTimeFrom.value!!.toString())
            Log.i("Time To: ",model!!.dateforTimeTo.value!!.toString())
            timeDifference = getDifference(model!!.dateforTimeFrom.value!!, model!!.dateforTimeTo.value!!)
            Log.i("Time Diff: ",timeDifference.toString())
            if(model!!.timeFrom.value == model!!.timeTo.value){
                FancyToast.makeText(context, "Set proper time slot!", FancyToast.LENGTH_SHORT, FancyToast.WARNING, false).show()
            }else if(timeDifference > 6){
                FancyToast.makeText(context, "Time slot should be less than 6 hours!", FancyToast.LENGTH_SHORT, FancyToast.WARNING, false).show()
            }
            else{
                view!!.findNavController().navigate(R.id.action_setTimeSlotFragment_to_mapFragment)
            }

        }


        return binding.root
    }

    /**
     * Load default values for date and time.
     */
    private fun loadDefaultValues(binding: FragmentSetTimeSlotBinding){

        val cal = Calendar.getInstance()

        val formatterCurrDateTv  =  SimpleDateFormat("dd/MM/yyyy")
        val formatterCurrDate  =  SimpleDateFormat("yyyyMMdd")
        val currDateTv = formatterCurrDateTv.format(Date()).toString()
        val currDate = formatterCurrDate.format(Date()).toString()

        binding.dateFrom.text = "  " + currDateTv
        model!!.dateFromTv.value = "  " + currDateTv
        model!!.setDateFrom(currDate)

        val formatterCurWeek  =  SimpleDateFormat("u")
        var dayOfWeek = formatterCurWeek.format(cal.time).toInt()
        if(dayOfWeek == 7) dayOfWeek = 0
        model!!.setWeek(dayOfWeek)


        val timefromCal = SimpleDateFormat("HH:mm").format(cal.time).toString()
        val fromHour = timefromCal.substring(0, 2).toInt()
        val fromMin = timefromCal.substring(3, timefromCal.length).toInt()
        model!!.setDateForTimeFrom(cal.time)
        val timeFrom = fromHour * 100 + fromMin
        binding.timeFrom.text = "  " + SimpleDateFormat("HH:mm").format(cal.time)
        model!!.timeFromTv.value = "  " + SimpleDateFormat("HH:mm").format(cal.time)

        model!!.setTimeFrom(timeFrom)

        cal.add(Calendar.HOUR, 3)
        model!!.setDateForTimeTo(cal.time)
        val timeToCal = SimpleDateFormat("HH:mm").format(cal.time).toString()
        val ToHour = timeToCal.substring(0, 2).toInt()
        val ToMin = timeToCal.substring(3, timeToCal.length).toInt()


        val timeTo = ToHour * 100 + ToMin
        binding.timeTo.text = "  " + SimpleDateFormat("HH:mm").format(cal.time)
        model!!.timeToTv.value = "  " + SimpleDateFormat("HH:mm").format(cal.time)
        model!!.setTimeTo(timeTo)
    }

    /**
     * Setting up Date from Dialog box.
     */
    private fun setDateFromDialog(binding: FragmentSetTimeSlotBinding){

        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        var tempYear = c.get(Calendar.YEAR)
        var tempMonth = c.get(Calendar.MONTH)
        var tempDay = c.get(Calendar.DAY_OF_MONTH)

        binding.dateFrom.setOnClickListener{
            val datePickerDialog = DatePickerDialog(context!!, DatePickerDialog.OnDateSetListener{_, mYear, mMonth, mDay ->

                val incMonth = mMonth + 1
                val dayFormatted = String.format("%02d", mDay)
                val monthFormatted = String.format("%02d", incMonth)

                val dateString = "" + mYear + "" +monthFormatted +"" +dayFormatted
                binding.dateFrom.text ="  " + dayFormatted + "/" + monthFormatted + "/" + mYear
                model!!.dateFromTv.value = "  " + dayFormatted + "/" + monthFormatted + "/" + mYear

                model!!.setDateFrom(dateString)

                tempYear = mYear
                tempMonth = mMonth
                tempDay =  mDay

                val formatterCurWeek  =  SimpleDateFormat("u")
                var dayOfWeek = formatterCurWeek.format(Date(mYear, mMonth, mDay)).toInt() - 1
                if(dayOfWeek == 7) dayOfWeek = 0
                model!!.setWeek(dayOfWeek)

            }, year, month, day)
            datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1
            datePickerDialog.show()
        }

    }

    /**
     * Setting up Time from Dialog box.
     */
    private fun setTimeFromDialog(binding: FragmentSetTimeSlotBinding){

        val c = Calendar.getInstance()
        binding.timeFrom.setOnClickListener{
            val timePickerDialog = TimePickerDialog(context!!, TimePickerDialog.OnTimeSetListener{_, mHour, mMin ->
                c.set(Calendar.HOUR_OF_DAY, mHour)
                c.set(Calendar.MINUTE, mMin)

                model!!.setDateForTimeFrom(c.time)

               // val formatter  =  DecimalFormat("00")
               // val formattedHourFrom = formatter.format(mHour)
              //  val formattedMinFrom = formatter.format(mMin)
                val timeFrom = mHour * 100 + mMin
                model!!.setTimeFrom(timeFrom)

                binding.timeFrom.text = "  " + SimpleDateFormat("HH:mm").format(c.time)
                model!!.timeFromTv.value ="  " + SimpleDateFormat("HH:mm").format(c.time)




            }, c.get(Calendar.HOUR_OF_DAY),c.get(Calendar.MINUTE), true)

            timePickerDialog.show()
        }


    }


    /**
     * Setting up Time to Dialog box.
     */
    private fun setTimeToDialog(binding: FragmentSetTimeSlotBinding){

        val c = Calendar.getInstance()
        binding.timeTo.setOnClickListener{
            val timePickerDialog = TimePickerDialog(context!!, TimePickerDialog.OnTimeSetListener{_, mHour, mMin ->
                c.set(Calendar.HOUR_OF_DAY, mHour)
                c.set(Calendar.MINUTE, mMin)
                model!!.setDateForTimeTo(c.time)
                //val formatter  =  DecimalFormat("00")
                //val formattedHourTo = formatter.format(mHour)
                //val formattedMinTo= formatter.format(mMin)
                val timeTo = mHour* 100 + mMin
                model!!.setTimeTo(timeTo)


                binding.timeTo.text = "  " + SimpleDateFormat("HH:mm").format(c.time)
                model!!.timeToTv.value ="  " + SimpleDateFormat("HH:mm").format(c.time)


            }, c.get(Calendar.HOUR_OF_DAY),c.get(Calendar.MINUTE), true)

            timePickerDialog.show()
        }
    }

    /**
     * Function to set up observers for lifecycle changes in the textViews.
     **/
    private fun setObservers(binding: FragmentSetTimeSlotBinding){
        model!!.dateFromTv.observe(this, object : Observer<Any> {
            override fun onChanged(o: Any?) {
                binding.dateFrom.text = o as String
            }
        })

        model!!.timeFromTv.observe(this, object : Observer<Any> {
            override fun onChanged(o: Any?) {
                binding.timeFrom.text = o as String
            }
        })

        model!!.timeToTv.observe(this, object : Observer<Any> {
            override fun onChanged(o: Any?) {
                binding.timeTo.text = o as String
            }
        })
    }


    /**
     * Function to calculate the diffenece between timeFrom and timeTo.
     * @param d1: timeFrom Date object
     * @param d2: timeTo Date object
     **/
    private fun getDifference(d1 : Date, d2 : Date) : Int{

        Log.i("Date 1", d1.toString())
        Log.i("Date 2", d2.toString())

        var diff = d2.time - d1.time
        var hours = 0
        if(diff < 0){
            diff = d1.time - d2.time
            diff = Math.abs(diff)
            diff /=  1000 //remove milliseconds
            hours = (diff / (60 * 60) % 24).toInt()
            hours = 24 - hours
            if(hours >= 6){
                val minutes = diff / 60 % 6
               // Log.i("Mins", minutes.toString())
                if(minutes >= 1){ // should be <= 6 hours.
                    hours = 30
            }
        }else{
            diff /=  1000 //remove milliseconds
            hours = (diff / (60 * 60) % 24).toInt()                }

        if(hours >= 6){
                val minutes = diff / 60 % 60
               // Log.i("Mins", minutes.toString())
                if(minutes >= 1){ // should be <= 6 hours.
                    hours = 30
                }
            }
        }


        return hours
    }

    
}



