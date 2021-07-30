package com.wellme.fragment

import android.app.Activity
import android.app.Dialog
import android.app.TimePickerDialog
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.wellme.LeftSideMenuActivity
import com.wellme.R
import com.wellme.adapter.ReminderthroughDialogAdapter
import com.wellme.databinding.FragmentSetReminderBinding
import com.wellme.dto.ReminderDTO
import com.wellme.dto.ReminderDataDTO
import com.wellme.dto.TrackMealDTO
import com.wellme.receiver.*
import com.wellme.utils.OnItemClickListener
import com.wellme.utils.UtilMethod
import kotlinx.android.synthetic.main.fragment_set_reminder.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class UpdateReminderFragment : Fragment(), View.OnClickListener, CompoundButton.OnCheckedChangeListener{
    var binding : FragmentSetReminderBinding? = null
    var regular : Typeface? = null
    var type : Int = 0
    var bundle : Bundle? = null
    var dialog : Dialog? = null
    var list : ArrayList<ReminderDataDTO> = ArrayList()
    var text_type : Int = 0
    val gson : Gson? = Gson()
    var reminder_list : List<ReminderDTO> = ArrayList()

    var s_reminder : String? = ""
    var reminder_old_list : ArrayList<ReminderDTO> = ArrayList()
    var reminderDTO : ReminderDTO? = null
    var s_time : String? = ""
    var s_repeating_time : String? = ""
    var time : Int = 1
    var hour1 : Int = 1
    var date1 : Int = 1
    var activity : Activity? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_set_reminder, container, false)
        return binding!!.root
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        this.activity = activity
    }

    override fun onResume() {
        super.onResume()
        (activity as LeftSideMenuActivity).enableBottomBar()
        (activity as LeftSideMenuActivity).setActiveSection(4)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    fun initView(){
        regular = ResourcesCompat.getFont(requireContext(), R.font.poppins_regular)
        binding?.header?.setTypeface(regular)
        binding?.reminderText?.setTypeface(regular)
        binding?.tvBreakfastTime?.setTypeface(regular)
        binding?.tvDinnerTime?.setTypeface(regular)
        binding?.tvEveningSnackTime?.setTypeface(regular)
        binding?.tvLunchTime?.setTypeface(regular)
        binding?.tvRemindMeOnceTime?.setTypeface(regular)
        binding?.tvRemindMeTrackTime?.setTypeface(regular)
        binding?.tvMorningSnackTime?.setTypeface(regular)
        binding?.cbBreaakfast?.setTypeface(regular)
        binding?.cbMorningSnack?.setTypeface(regular)
        binding?.cbLunch?.setTypeface(regular)
        binding?.cbEveningSnack?.setTypeface(regular)
        binding?.cbDinner?.setTypeface(regular)
        binding?.cbTrackMealOnceAt?.setTypeface(regular)
        binding?.cbTrackMealOnceEveryWeek?.setTypeface(regular)
        binding?.cbLogweightEveryMonthOn?.setTypeface(regular)
        binding?.cbLogweightOnceEveryWeek?.setTypeface(regular)
        binding?.cbWalkingOnceAt?.setTypeface(regular)
        binding?.cbWorkingOnceAt?.setTypeface(regular)
        binding?.cbDrinkWaterDayInWeek?.setTypeface(regular)
        binding?.cbDrinkWaterEveryDatetime?.setTypeface(regular)
        binding?.cbDrinkWaterEveryHour?.setTypeface(regular)
        binding?.cbDrinkWaterEveryTime?.setTypeface(regular)
        binding?.tvSave?.setTypeface(regular)
        binding?.tvLogweightRemindMeOnceEvery?.setTypeface(regular)
        binding?.tvRemindMeLogweightTime?.setTypeface(regular)
        binding?.tvWalkingTime?.setTypeface(regular)
        binding?.tvWorkingTime?.setTypeface(regular)
        binding?.tvDrinkWaterEveryTime?.setTypeface(regular)
        binding?.tvDrinkWaterDayInWeek?.setTypeface(regular)
        binding?.tvDrinkWaterEveryDatetime?.setTypeface(regular)
        binding?.tvDrinkWaterEveryHourTime?.setTypeface(regular)
        binding?.cbTrackMealOnceEveryWeek!!.setOnCheckedChangeListener(this)
        binding?.cbTrackMealOnceAt!!.setOnCheckedChangeListener(this)
        binding?.cbLogweightEveryMonthOn!!.setOnCheckedChangeListener(this)
        binding?.cbLogweightOnceEveryWeek!!.setOnCheckedChangeListener(this)
        binding?.cbDrinkWaterEveryTime!!.setOnCheckedChangeListener(this)
        binding?.cbDrinkWaterEveryHour!!.setOnCheckedChangeListener(this)
        binding?.cbDrinkWaterEveryDatetime!!.setOnCheckedChangeListener(this)
        binding?.cbDrinkWaterDayInWeek!!.setOnCheckedChangeListener(this)
        binding?.cbBreaakfast!!.setOnCheckedChangeListener(this)
        binding?.cbMorningSnack!!.setOnCheckedChangeListener(this)
        binding?.cbLunch!!.setOnCheckedChangeListener(this)
        binding?.cbEveningSnack!!.setOnCheckedChangeListener(this)
        binding?.cbDinner!!.setOnCheckedChangeListener(this)
        binding?.tvBreakfastTime!!.setOnClickListener(this)
        binding?.tvMorningSnackTime!!.setOnClickListener(this)
        binding?.tvLunchTime!!.setOnClickListener(this)
        binding?.tvEveningSnackTime!!.setOnClickListener(this)
        binding?.tvDinnerTime!!.setOnClickListener(this)
        binding?.tvDrinkWaterEveryDatetime!!.setOnClickListener(this)
        binding?.tvRemindMeTrackTime!!.setOnClickListener(this)
        binding?.tvWalkingTime!!.setOnClickListener(this)
        binding?.tvWorkingTime!!.setOnClickListener(this)
        binding?.tvRemindMeOnceTime!!.setOnClickListener(this)
        binding?.tvRemindMeLogweightTime!!.setOnClickListener(this)
        binding?.tvDrinkWaterEveryHourTime!!.setOnClickListener(this)
        binding?.tvDrinkWaterEveryTime!!.setOnClickListener(this)
        binding?.tvDrinkWaterDayInWeek!!.setOnClickListener(this)
        binding?.tvLogweightRemindMeOnceEvery!!.setOnClickListener(this)
        binding?.llSave!!.setOnClickListener(this)
        binding?.back!!.setOnClickListener(this)

        s_reminder = UtilMethod.instance.getReminderList(requireContext())
        if(!UtilMethod.instance.isStringNullOrNot(s_reminder)) {
            reminder_list = gson!!.fromJson(s_reminder, Array<ReminderDTO>::class.java).toList()
            reminder_old_list = ArrayList()
            reminder_old_list.addAll(reminder_list)

        }

        bundle = arguments
        if(bundle!=null){
            type = bundle!!.getInt("type")
            reminderDTO = reminder_old_list.get(type-1)
            if(reminderDTO!=null){
                s_time = reminderDTO!!.time
                s_repeating_time = reminderDTO!!.repeating_val
            }
            if(type == 1){
                binding?.walkingLayout!!.visibility = View.VISIBLE
                binding?.workingLayout!!.visibility = View.GONE
                binding?.mealLayout!!.visibility = View.GONE
                binding?.logWeightLayout!!.visibility = View.GONE
                binding?.drinkWaterLayout!!.visibility = View.GONE
                binding?.header?.setText(requireActivity().resources.getString(R.string.walking_reminder))
                setData(binding?.cbWalkingOnceAt!!, binding?.tvWalkingTime!!)

            }
            else if(type == 2){
                binding?.walkingLayout!!.visibility = View.GONE
                binding?.workingLayout!!.visibility = View.VISIBLE
                binding?.mealLayout!!.visibility = View.GONE
                binding?.logWeightLayout!!.visibility = View.GONE
                binding?.drinkWaterLayout!!.visibility = View.GONE
                binding?.header?.setText(requireActivity().resources.getString(R.string.working_reminder))
                setData(binding?.cbWorkingOnceAt!!, binding?.tvWorkingTime!!)
            }
            else if(type == 3){
                binding?.walkingLayout!!.visibility = View.GONE
                binding?.workingLayout!!.visibility = View.GONE
                binding?.mealLayout!!.visibility = View.GONE
                binding?.logWeightLayout!!.visibility = View.VISIBLE
                binding?.drinkWaterLayout!!.visibility = View.GONE
                binding?.header?.setText(requireActivity().resources.getString(R.string.log_weight_reminder))
                setLogWeightData(binding?.cbLogweightEveryMonthOn!!, binding?.cbLogweightOnceEveryWeek!!, binding?.tvRemindMeLogweightTime!!, binding?.tvLogweightRemindMeOnceEvery!!)
            }
            else if(type == 4){
                binding?.walkingLayout!!.visibility = View.GONE
                binding?.workingLayout!!.visibility = View.GONE
                binding?.mealLayout!!.visibility = View.VISIBLE
                binding?.logWeightLayout!!.visibility = View.GONE
                binding?.drinkWaterLayout!!.visibility = View.GONE
                binding?.header?.setText(requireActivity().resources.getString(R.string.track_meal_reminder))
                setTrackMealData(binding?.cbBreaakfast!!, binding?.cbMorningSnack!!, binding?.cbLunch!!, binding?.cbEveningSnack!!, binding?.cbDinner!!, binding?.cbTrackMealOnceAt!!, binding?.cbTrackMealOnceEveryWeek!!, binding?.tvBreakfastTime!!, binding?.tvMorningSnackTime!!, binding?.tvLunchTime!!, binding?.tvEveningSnackTime!!, binding?.tvDinnerTime!!, binding?.tvRemindMeTrackTime!!, binding?.tvRemindMeOnceTime!!)

            }
            else if(type == 5){
                binding?.walkingLayout!!.visibility = View.GONE
                binding?.workingLayout!!.visibility = View.GONE
                binding?.mealLayout!!.visibility = View.GONE
                binding?.logWeightLayout!!.visibility = View.GONE
                binding?.drinkWaterLayout!!.visibility = View.VISIBLE
                binding?.header?.setText(requireActivity().resources.getString(R.string.drink_water_reminder))
                setDrinkWaterData(binding?.cbDrinkWaterEveryHour!!, binding?.cbDrinkWaterEveryTime!!, binding?.cbDrinkWaterEveryDatetime!!, binding?.cbDrinkWaterDayInWeek!!, binding?.tvDrinkWaterEveryHourTime!!, binding?.tvDrinkWaterEveryTime!!, binding?.tvDrinkWaterEveryDatetime!!, binding?.tvDrinkWaterDayInWeek!!)
            }
        }

    }

    fun getDay(i1 : Int) : String{
        var s1 : String = ""
        if(i1 == 1){
            s1 = "Sunday"
        }
        else if(i1 == 2){
            s1 = "Monday"
        }
        else if(i1 == 3){
            s1 = "Tuesday"
        }
        else if(i1 == 4){
            s1 = "Wednesday"
        }
        else if(i1 == 5){
            s1 = "Thursday"
        }
        else if(i1 == 6){
            s1 = "Friday"
        }
        else if(i1 == 7){
            s1 = "Saturday"
        }
        return s1

    }

    fun setTrackMealData(cb_breakfast : CheckBox, cb_morning : CheckBox, cb_lunch : CheckBox, cb_evening : CheckBox, cb_night : CheckBox, cb_once : RadioButton, cb_week : RadioButton, tv_breakfast : TextView, tv_morning : TextView, tv_lunch : TextView, tv_evening : TextView, tv_dinner : TextView, tv_once : TextView, tv_week : TextView){
        var cal : Calendar = Calendar.getInstance()
        var cur_date : Date = cal.time
        var day : Int = cal.get(Calendar.DAY_OF_WEEK)
        var week : Int = cal.get(Calendar.DAY_OF_WEEK)

        if(!UtilMethod.instance.isStringNullOrNot(s_repeating_time)){
            if(s_repeating_time.equals("day", true)){
                cb_once.isChecked = true
                tv_once.setText(s_time)
                tv_week.setText(UtilMethod.instance.getDayFormat(cur_date))
                tv_breakfast.setText(UtilMethod.instance.getTime1(cur_date))
                tv_morning.setText(UtilMethod.instance.getTime1(cur_date))
                tv_lunch.setText(UtilMethod.instance.getTime1(cur_date))
                tv_evening.setText(UtilMethod.instance.getTime1(cur_date))
                tv_dinner.setText(UtilMethod.instance.getTime1(cur_date))
            }
            else if(s_repeating_time.equals("week", true)){
                cb_week.isChecked = true
                tv_week.setText(s_time)
                tv_once.setText(UtilMethod.instance.getTime1(cur_date))
                tv_breakfast.setText(UtilMethod.instance.getTime1(cur_date))
                tv_morning.setText(UtilMethod.instance.getTime1(cur_date))
                tv_lunch.setText(UtilMethod.instance.getTime1(cur_date))
                tv_evening.setText(UtilMethod.instance.getTime1(cur_date))
                tv_dinner.setText(UtilMethod.instance.getTime1(cur_date))

            }
            else{
                var repeating_list : List<String> = s_repeating_time!!.split(",")
                var time_list : List<String> = s_time!!.split(",")
                var s_breakfast : String = ""
                var s_morning_snack : String = ""
                var s_lunch : String = ""
                var s_evening : String = ""
                var s_dinner : String = ""
                if(repeating_list!=null){
                    for(i in 0..repeating_list.size-1){
                        var s1 : String? = repeating_list.get(i)
                        if(s1.equals(context!!.resources.getString(R.string.breakfast))){
                            s_breakfast = time_list.get(i)
                        }
                        if(s1?.trim().equals(context!!.resources.getString(R.string.morning_snack), true)){
                            s_morning_snack = time_list.get(i)
                        }
                        if(s1?.trim().equals(context!!.resources.getString(R.string.lunch), true)){
                            s_lunch = time_list.get(i)
                        }
                        if(s1?.trim().equals(context!!.resources.getString(R.string.evening_snack), true)){
                            s_evening = time_list.get(i)
                        }
                        if(s1?.trim().equals(context!!.resources.getString(R.string.dinner), true)){
                            s_dinner = time_list.get(i)
                        }

                    }
                }

                tv_week.setText(UtilMethod.instance.getDayFormat(cur_date))
                tv_once.setText(UtilMethod.instance.getTime1(cur_date))
                Log.v("Breakfast", "=> "+s_breakfast)
                Log.v("Morning", "=> "+s_morning_snack)
                Log.v("Lunch", "=> "+s_lunch)
                Log.v("Evening", "=> "+s_evening)
                Log.v("Dinner", "=> "+s_dinner)
                if(!UtilMethod.instance.isStringNullOrNot(s_breakfast)){
                    cb_breakfast.isChecked = true
                    tv_breakfast.setText(s_breakfast)
                }
                else{
                    tv_breakfast.setText(UtilMethod.instance.getTime1(cur_date))
                }
                if(!UtilMethod.instance.isStringNullOrNot(s_morning_snack)){
                    cb_morning.isChecked = true
                    tv_morning.setText(s_morning_snack)
                }
                else{
                    tv_morning.setText(UtilMethod.instance.getTime1(cur_date))
                }
                if(!UtilMethod.instance.isStringNullOrNot(s_lunch)){
                    cb_lunch.isChecked = true
                    tv_lunch.setText(s_lunch)
                }
                else{
                    tv_lunch.setText(UtilMethod.instance.getTime1(cur_date))
                }
                if(!UtilMethod.instance.isStringNullOrNot(s_evening)){
                    cb_evening.isChecked = true
                    tv_evening.setText(s_evening)
                }
                else{
                    tv_evening.setText(UtilMethod.instance.getTime1(cur_date))
                }
                if(!UtilMethod.instance.isStringNullOrNot(s_dinner)){
                    cb_dinner.isChecked = true
                    tv_dinner.setText(s_dinner)
                }
                else{
                    tv_dinner.setText(UtilMethod.instance.getTime1(cur_date))
                }

            }
        }
        else{
            tv_once.setText(UtilMethod.instance.getTime1(cur_date))
            tv_breakfast.setText(UtilMethod.instance.getTime1(cur_date))
            tv_morning.setText(UtilMethod.instance.getTime1(cur_date))
            tv_lunch.setText(UtilMethod.instance.getTime1(cur_date))
            tv_evening.setText(UtilMethod.instance.getTime1(cur_date))
            tv_dinner.setText(UtilMethod.instance.getTime1(cur_date))
            tv_week.setText(UtilMethod.instance.getDayFormat(cur_date))

        }
    }

    fun setLogWeightData(cb_day : RadioButton, cb_week : RadioButton, tv_day : TextView, tv_week : TextView){
        var cal : Calendar = Calendar.getInstance()
        var cur_date : Date = cal.time
        var date : Int = cal.get(Calendar.DAY_OF_MONTH)
        var day : Int = cal.get(Calendar.DAY_OF_WEEK)

        if(!UtilMethod.instance.isStringNullOrNot(s_repeating_time)){
            if(s_repeating_time.equals("day", true)){
                cb_day.isChecked = true
                tv_day.setText(s_time)
            }
            else{
                tv_day.setText(UtilMethod.instance.getDate(date))
            }

            if(s_repeating_time.equals("week", true)){
                cb_week.isChecked = true
                tv_week.setText(s_time)
            }
            else{
                tv_week.setText(UtilMethod.instance.getDayFormat(cur_date))
            }
        }
        else{
            date1 = date
            tv_day.setText(UtilMethod.instance.getDate(date))
            tv_week.setText(UtilMethod.instance.getDayFormat(cur_date))
        }

    }

    fun setDrinkWaterData(cb_hour : RadioButton, cb_time : RadioButton, cb_datetime : RadioButton, cb_week : RadioButton, tv_hour : TextView, tv_time : TextView, tv_datetime : TextView, tv_week : TextView){
        var cal : Calendar = Calendar.getInstance()
        var cur_date : Date = cal.time
        var week : Int = cal.get(Calendar.DAY_OF_WEEK)

        if(!UtilMethod.instance.isStringNullOrNot(s_repeating_time)){
            if(s_repeating_time.equals("hour", true)){
                cb_hour.isChecked = true
                tv_hour.setText(s_time)
            }
            else{
                tv_hour.setText("1 hour")
            }
            if(s_repeating_time.equals("time", true)){
                cb_time.isChecked = true
                tv_time.setText(s_time)
            }
            else{
                tv_time.setText("1 time")
            }
            if(s_repeating_time.equals("day", true)){
                cb_datetime.isChecked = true
                tv_datetime.setText(s_time)
            }
            else{
                tv_datetime.setText(UtilMethod.instance.getTime1(cur_date))
            }
            if(s_repeating_time.equals("week", true)){
                cb_week.isChecked = true
                tv_week.setText(s_time)
            }
            else{
                tv_week.setText(UtilMethod.instance.getDayFormat(cur_date))
            }
        }
        else{
            tv_hour.setText("1 hour")
            tv_time.setText("1 time")
            tv_datetime.setText(UtilMethod.instance.getTime1(cur_date))
            tv_week.setText(UtilMethod.instance.getDayFormat(cur_date))
        }
    }

    fun setData(cb : CheckBox, tv : TextView){
        if(!UtilMethod.instance.isStringNullOrNot(s_time)){
            tv.setText(s_time)
            cb.isChecked = true
        }
        else{
            var cal : Calendar = Calendar.getInstance()
            var cur_date : Date = cal.time
            tv.setText(UtilMethod.instance.getTime1(cur_date))
        }

    }

    override fun onClick(v: View?) {
        if(v == binding?.tvBreakfastTime){
            val cal = Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
                cal.set(Calendar.HOUR_OF_DAY, hour)
                cal.set(Calendar.MINUTE, minute)
                binding?.tvBreakfastTime!!.text = SimpleDateFormat("hh:mm a").format(cal.time)
                binding?.cbBreaakfast!!.isChecked = true
            }
            TimePickerDialog(requireContext(), timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), false).show()

        }
        else if(v == binding?.tvMorningSnackTime){
            val cal = Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
                cal.set(Calendar.HOUR_OF_DAY, hour)
                cal.set(Calendar.MINUTE, minute)
                binding?.tvMorningSnackTime!!.text = SimpleDateFormat("hh:mm a").format(cal.time)
                binding?.cbMorningSnack!!.isChecked = true
            }
            TimePickerDialog(requireContext(), timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), false).show()

        }
        else if(v == binding?.tvLunchTime){
            val cal = Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
                cal.set(Calendar.HOUR_OF_DAY, hour)
                cal.set(Calendar.MINUTE, minute)
                binding?.tvLunchTime!!.text = SimpleDateFormat("hh:mm a").format(cal.time)
                binding?.cbLunch!!.isChecked = true
            }
            TimePickerDialog(requireContext(), timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), false).show()

        }
        else if(v == binding?.tvEveningSnackTime){
            val cal = Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
                cal.set(Calendar.HOUR_OF_DAY, hour)
                cal.set(Calendar.MINUTE, minute)
                binding?.tvEveningSnackTime!!.text = SimpleDateFormat("hh:mm a").format(cal.time)
                binding?.cbEveningSnack!!.isChecked = true
            }
            TimePickerDialog(requireContext(), timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), false).show()

        }
        else if(v == binding?.tvDinnerTime){
            val cal = Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
                cal.set(Calendar.HOUR_OF_DAY, hour)
                cal.set(Calendar.MINUTE, minute)
                binding?.tvDinnerTime!!.text = SimpleDateFormat("hh:mm a").format(cal.time)
                binding?.cbDinner!!.isChecked = true
            }
            TimePickerDialog(requireContext(), timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), false).show()

        }
        else if(v == binding?.tvWalkingTime){
            val cal = Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
                cal.set(Calendar.HOUR_OF_DAY, hour)
                cal.set(Calendar.MINUTE, minute)
                binding?.tvWalkingTime!!.text = SimpleDateFormat("hh:mm a").format(cal.time)
                binding?.cbWalkingOnceAt!!.isChecked = true
            }
            TimePickerDialog(requireContext(), timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), false).show()

        }
        else if(v == binding?.tvWorkingTime){
            val cal = Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
                cal.set(Calendar.HOUR_OF_DAY, hour)
                cal.set(Calendar.MINUTE, minute)
                binding?.tvWorkingTime!!.text = SimpleDateFormat("hh:mm a").format(cal.time)
                binding?.cbWorkingOnceAt!!.isChecked = true
            }
            TimePickerDialog(requireContext(), timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), false).show()

        }
        else if(v == binding?.tvDrinkWaterEveryDatetime){
            val cal = Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
                cal.set(Calendar.HOUR_OF_DAY, hour)
                cal.set(Calendar.MINUTE, minute)
                binding?.tvDrinkWaterEveryDatetime!!.text = SimpleDateFormat("hh:mm a").format(cal.time)
                binding?.cbDrinkWaterEveryDatetime!!.isChecked = true
            }
            TimePickerDialog(requireContext(), timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), false).show()

        }
        else if(v == binding?.tvRemindMeTrackTime){
            val cal = Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
                cal.set(Calendar.HOUR_OF_DAY, hour)
                cal.set(Calendar.MINUTE, minute)
                binding?.tvRemindMeTrackTime!!.text = SimpleDateFormat("hh:mm a").format(cal.time)
                binding?.cbTrackMealOnceAt!!.isChecked = true
            }
            TimePickerDialog(requireContext(), timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), false).show()

        }
        else if(v == binding?.tvRemindMeOnceTime){
            text_type = 1
            var aa : Array<Array<String>> = arrayOf(requireContext().resources.getStringArray(R.array.week_array))
            list = ArrayList()
            for(i in 0..aa[0].size-1){
                list.add(ReminderDataDTO(aa[0].get(i)))
            }
            showReminderDialog(binding?.cbTrackMealOnceEveryWeek!!.text.toString())

        }
        else if(v == binding?.tvRemindMeLogweightTime){
            text_type = 2
            var aa : Array<Array<String>> = arrayOf(requireContext().resources.getStringArray(R.array.date_array))
            list = ArrayList()
            for(i in 0..aa[0].size-1){
                list.add(ReminderDataDTO(aa[0].get(i)))
            }
            showReminderDialog(binding?.cbLogweightEveryMonthOn!!.text.toString())
        }
        else if(v == binding?.tvDrinkWaterEveryHourTime){
            text_type = 3
            var aa : Array<Array<String>> = arrayOf(requireContext().resources.getStringArray(R.array.hour_array))
            list = ArrayList()
            for(i in 0..aa[0].size-1){
                list.add(ReminderDataDTO(aa[0].get(i)))
            }
            showReminderDialog(binding?.cbDrinkWaterEveryHour!!.text.toString())
        }

        else if(v == binding?.tvDrinkWaterEveryTime){
            text_type = 4
            var aa : Array<Array<String>> = arrayOf(requireContext().resources.getStringArray(R.array.time_array))
            list = ArrayList()
            for(i in 0..aa[0].size-1){
                list.add(ReminderDataDTO(aa[0].get(i)))
            }
            showReminderDialog(binding?.cbDrinkWaterEveryTime!!.text.toString())
        }
        else if(v == binding?.tvDrinkWaterDayInWeek){
            text_type = 5
            var aa : Array<Array<String>> = arrayOf(requireContext().resources.getStringArray(R.array.week_array))
            list = ArrayList()
            for(i in 0..aa[0].size-1){
                list.add(ReminderDataDTO(aa[0].get(i)))
            }
            showReminderDialog(binding?.cbDrinkWaterDayInWeek!!.text.toString())
        }
        else if(v == binding?.tvLogweightRemindMeOnceEvery){
            text_type = 6
            var aa : Array<Array<String>> = arrayOf(requireContext().resources.getStringArray(R.array.week_array))
            list = ArrayList()
            for(i in 0..aa[0].size-1){
                list.add(ReminderDataDTO(aa[0].get(i)))
            }
            showReminderDialog(binding?.cbLogweightOnceEveryWeek!!.text.toString())
        }
        else if(v == binding?.llSave){
            if(type == 2){


                if(binding?.cbWorkingOnceAt!!.isChecked){
                    var s_date : String = binding?.tvWorkingTime!!.text.toString()
                    if(!UtilMethod.instance.isStringNullOrNot(s_date)){
                        if(s_time.equals(s_date, true)){

                        }
                        else{
                            reminderDTO!!.time = s_date
                            reminder_old_list.set(type-1, reminderDTO!!)
                            var s1 : String = gson!!.toJson(reminder_old_list)
                            UtilMethod.instance.setReminderList(s1, requireContext())

                            var date : Date? = UtilMethod.instance.getDateWithoutDate(s_date)
                            var calendar : Calendar = Calendar.getInstance()
                            var current_calendar : Calendar = Calendar.getInstance()
                            var current_calendar1 : Calendar = Calendar.getInstance()
                            if(date!=null) {
                                calendar.set(Calendar.HOUR_OF_DAY, date!!.hours)
                                calendar.set(Calendar.MINUTE, date!!.minutes)
                                calendar.set(Calendar.SECOND, 0)
                            }
                            current_calendar.add(Calendar.MINUTE, 15)
                            var current_ms : Long = current_calendar.timeInMillis
                            var current1_ms : Long = current_calendar1.timeInMillis
                            var calendar_ms : Long = calendar.timeInMillis
                            var difference_ms : Long = 0

                            if(current_ms >= calendar_ms){
                                calendar.add(Calendar.DATE, 1)
                                difference_ms = calendar.timeInMillis - current1_ms
                            }
                            else{
                                difference_ms = calendar_ms - current1_ms
                            }


                            var componentName : ComponentName = ComponentName(context!!, WorkingSchedular::class.java)
                            var info : JobInfo? = null
                            if(Build.VERSION.SDK_INT > 23) {
                                info = JobInfo.Builder(123, componentName)
                                    .setRequiresCharging(false)
                                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_NONE)
                                    .setPersisted(false)
                                    .setMinimumLatency(difference_ms)
                                    .build()
                            }
                            else{
                                info = JobInfo.Builder(123, componentName)
                                    .setRequiresCharging(false)
                                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_NONE)
                                    .setPersisted(false)
                                    .setPeriodic(difference_ms)
                                    .build()
                            }
                            var scheduler : JobScheduler = context!!.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
                            scheduler!!.schedule(info)

                        }


                    }
                }
                else{
                    if(!UtilMethod.instance.isStringNullOrNot(s_time)){
                        var scheduler : JobScheduler = context!!.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
                        scheduler!!.cancel(123)
                    }
                    reminderDTO!!.time = ""
                    reminder_old_list.set(type-1, reminderDTO!!)
                    var s1 : String = gson!!.toJson(reminder_old_list)
                    UtilMethod.instance.setReminderList(s1, requireContext())
                }

            }
            else if(type == 1){
                if(binding?.cbWalkingOnceAt!!.isChecked){
                    var s_date : String = binding?.tvWalkingTime!!.text.toString()
                    if(!UtilMethod.instance.isStringNullOrNot(s_date)){
                        if(s_time.equals(s_date, true)){

                        }
                        else{
                            reminderDTO!!.time = s_date
                            reminder_old_list.set(type-1, reminderDTO!!)
                            var s1 : String = gson!!.toJson(reminder_old_list)
                            UtilMethod.instance.setReminderList(s1, requireContext())

                            var date : Date? = UtilMethod.instance.getDateWithoutDate(s_date)
                            var calendar : Calendar = Calendar.getInstance()
                            var current_calendar : Calendar = Calendar.getInstance()
                            var current_calendar1 : Calendar = Calendar.getInstance()
                            if(date!=null) {
                                calendar.set(Calendar.HOUR_OF_DAY, date!!.hours)
                                calendar.set(Calendar.MINUTE, date!!.minutes)
                                calendar.set(Calendar.SECOND, 0)
                            }
                            current_calendar.add(Calendar.MINUTE, 15)
                            var current_ms : Long = current_calendar.timeInMillis
                            var current1_ms : Long = current_calendar1.timeInMillis
                            var calendar_ms : Long = calendar.timeInMillis
                            var difference_ms : Long = 0

                            if(current_ms >= calendar_ms){
                                Log.v("Hello ", "World")
                                calendar.add(Calendar.DATE, 1)
                                difference_ms = calendar.timeInMillis - current1_ms
                            }
                            else{
                                Log.v("Hello ", "World12")
                                difference_ms = calendar_ms - current1_ms
                            }

                            Log.v("Difference ", "==> "+difference_ms)
                            var componentName : ComponentName = ComponentName(context!!, WalkingSchedular::class.java)
                            var info : JobInfo? = null
                            if(Build.VERSION.SDK_INT > 23) {
                                Log.v("Hello ", "if")
                                info = JobInfo.Builder(456, componentName)
                                    .setRequiresCharging(false)
                                    .setMinimumLatency(difference_ms)
                                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_NONE)
                                    .setPersisted(false)
                                    .build()
                            }
                            else{
                                Log.v("Hello ", "else")
                                info = JobInfo.Builder(456, componentName)
                                    .setRequiresCharging(false)
                                    .setPeriodic(difference_ms)
                                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_NONE)
                                    .setPersisted(false)
                                    .setPeriodic(difference_ms)
                                    .build()
                            }
                            Log.v("Info", "==> "+info.toString())
                            var scheduler : JobScheduler = context!!.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
                            scheduler!!.schedule(info)
                        }


                    }
                }
                else{
                    if(!UtilMethod.instance.isStringNullOrNot(s_time)){
                        var scheduler : JobScheduler = context!!.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
                        scheduler!!.cancel(456)
                    }
                    reminderDTO!!.time = ""
                    reminder_old_list.set(type-1, reminderDTO!!)
                    var s1 : String = gson!!.toJson(reminder_old_list)
                    UtilMethod.instance.setReminderList(s1, requireContext())
                }
            }
            else if(type == 3){
                var calendar : Calendar = Calendar.getInstance()
                var current_calendar : Calendar = Calendar.getInstance()
                var current_calendar1 : Calendar = Calendar.getInstance()

                if(binding?.cbLogweightOnceEveryWeek!!.isChecked){
                    var s_date : String = binding?.tvLogweightRemindMeOnceEvery!!.text.toString()
                    var s_repeat : String = "week"
                    if(!UtilMethod.instance.isStringNullOrNot(s_date)){
                        if(s_time.equals(s_date, true)){

                        }
                        else {
                            reminderDTO!!.time = s_date
                            reminderDTO!!.repeating_val = s_repeat
                            reminder_old_list.set(type - 1, reminderDTO!!)
                            var s1: String = gson!!.toJson(reminder_old_list)
                            UtilMethod.instance.setReminderList(s1, requireContext())

                            var i1: Int = UtilMethod.instance.getDay(s_date)
                            calendar.set(Calendar.HOUR_OF_DAY, 8)
                            calendar.set(Calendar.MINUTE, 0)
                            calendar.set(Calendar.SECOND, 0)
                            var current_i1: Int = calendar.get(Calendar.DAY_OF_WEEK)
                            if (current_i1 > i1) {
                                calendar.add(Calendar.DAY_OF_YEAR, 7 - (current_i1 - i1))
                            }
                            current_calendar.add(Calendar.MINUTE, 15)
                            var current_ms: Long = current_calendar.timeInMillis
                            var current1_ms: Long = current_calendar1.timeInMillis
                            var calendar_ms: Long = calendar.timeInMillis
                            var difference_ms: Long = 0

                            if (current_ms >= calendar_ms) {
                                calendar.add(Calendar.DATE, 7)
                                difference_ms = calendar.timeInMillis - current1_ms
                            } else {
                                difference_ms = calendar_ms - current1_ms
                            }
                            setAlertThroughLogWeight(difference_ms)
                        }
                    }

                }
                else if(binding?.cbLogweightEveryMonthOn!!.isChecked){
                    var s_date : String = binding?.tvRemindMeLogweightTime!!.text.toString()
                    var s_repeat : String = "day"
                    if(!UtilMethod.instance.isStringNullOrNot(s_date)){
                        if(s_time.equals(s_date, true)){

                        }
                        else {
                            reminderDTO!!.time = s_date
                            reminderDTO!!.repeating_val = s_repeat
                            reminder_old_list.set(type - 1, reminderDTO!!)
                            var s1: String = gson!!.toJson(reminder_old_list)
                            UtilMethod.instance.setReminderList(s1, requireContext())
                            calendar.set(Calendar.HOUR_OF_DAY, 8)
                            calendar.set(Calendar.MINUTE, 0)
                            calendar.set(Calendar.SECOND, 0)
                            var current_i1: Int = calendar.get(Calendar.DATE)
                            if (current_i1 > date1) {
                                calendar.add(Calendar.DAY_OF_YEAR,
                                    calendar.getActualMaximum(Calendar.DAY_OF_MONTH) - (current_i1 - date1)
                                )
                            }
                            else{
                                calendar.set(Calendar.DATE,  date1)
                            }
                            current_calendar.add(Calendar.MINUTE, 15)
                            var current_ms: Long = current_calendar.timeInMillis
                            var current1_ms: Long = current_calendar1.timeInMillis
                            var calendar_ms: Long = calendar.timeInMillis
                            var difference_ms: Long = 0

                            if (current_ms >= calendar_ms) {
                                calendar.add(Calendar.DAY_OF_YEAR, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
                                difference_ms = calendar.timeInMillis - current1_ms
                            } else {
                                difference_ms = calendar_ms - current1_ms
                            }
                            setAlertThroughLogWeight(difference_ms)
                        }
                    }
                }
                else{
                    if(!UtilMethod.instance.isStringNullOrNot(s_time)){
                        var scheduler : JobScheduler = context!!.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
                        scheduler!!.cancel(678)
                    }
                    reminderDTO!!.time = ""
                    reminderDTO!!.repeating_val = ""
                    reminder_old_list.set(type-1, reminderDTO!!)
                    var s1 : String = gson!!.toJson(reminder_old_list)
                    UtilMethod.instance.setReminderList(s1, requireContext())
                }
            }
            else if(type == 4){
                setMealTrack()

            }
            else if(type == 5){
                setDrinkWaterTrack()
            }
            Toast.makeText(requireContext(), "Reminder successfully updated!", Toast.LENGTH_LONG).show()
            requireActivity().onBackPressed()

        }else if (v==binding?.back){

            requireActivity().onBackPressed()
        }
    }


    fun setMealTrack(){
        var s_time : String? = ""
        var s_repeating_time : String? = ""
        var scheduler : JobScheduler = context!!.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        scheduler!!.cancel(777)
        if(binding?.cbTrackMealOnceEveryWeek!!.isChecked){
            s_repeating_time = "week"
            s_time = binding?.tvRemindMeOnceTime!!.text.toString()
            if(!UtilMethod.instance.isStringNullOrNot(s_repeating_time) && !UtilMethod.instance.isStringNullOrNot(s_time)){
                setTrackMealAlert2(s_repeating_time!!, s_time!!)
            }
        }
        else if(binding?.cbTrackMealOnceAt!!.isChecked){
            s_repeating_time = "day"
            s_time = binding?.tvRemindMeTrackTime!!.text.toString()
            if(!UtilMethod.instance.isStringNullOrNot(s_repeating_time) && !UtilMethod.instance.isStringNullOrNot(s_time)){
                setTrackMealAlert2(s_repeating_time!!, s_time!!)
            }
        }
        else{
            var track_meal : ArrayList<TrackMealDTO> = ArrayList()
            if(binding?.cbBreaakfast!!.isChecked){
                track_meal.add(TrackMealDTO(binding?.cbBreaakfast!!.text.toString(), binding?.tvBreakfastTime!!.text.toString()))
            }
            if(binding?.cbMorningSnack!!.isChecked){
                track_meal.add(TrackMealDTO(binding?.cbMorningSnack!!.text.toString(), binding?.tvMorningSnackTime!!.text.toString()))
            }
            if(binding?.cbLunch!!.isChecked){
                track_meal.add(TrackMealDTO(binding?.cbLunch!!.text.toString(), binding?.tvLunchTime!!.text.toString()))
            }
            if(binding?.cbEveningSnack!!.isChecked){
                track_meal.add(TrackMealDTO(binding?.cbEveningSnack!!.text.toString(), binding?.tvEveningSnackTime!!.text.toString()))
            }
            if(binding?.cbDinner!!.isChecked){
                track_meal.add(TrackMealDTO(binding?.cbDinner!!.text.toString(), binding?.tvDinnerTime!!.text.toString()))
            }
            if(track_meal!=null){
                for(j in 0..track_meal.size-1){
                    if(UtilMethod.instance.isStringNullOrNot(s_repeating_time)){
                        s_repeating_time = track_meal.get(j).type
                        s_time = track_meal.get(j).time
                    }
                    else{
                        s_repeating_time+=", "+track_meal.get(j).type
                        s_time+=", "+track_meal.get(j).time
                    }
                }
                setTrackMealAlert3(s_repeating_time!!, s_time!!, track_meal)
            }
        }
    }

    fun setDrinkWaterTrack(){
        var s_repeat : String = ""
        var s_alert_time : String = ""
        if(!UtilMethod.instance.isStringNullOrNot(s_repeating_time) && !binding?.cbDrinkWaterEveryHour!!.isChecked && !binding?.cbDrinkWaterEveryTime!!.isChecked && !binding?.cbDrinkWaterEveryDatetime!!.isChecked && !binding?.cbDrinkWaterDayInWeek!!.isChecked){
            var scheduler : JobScheduler = context!!.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
            scheduler!!.cancel(567)

            reminderDTO!!.time = ""
            reminder_old_list.set(type-1, reminderDTO!!)
            var s1 : String = gson!!.toJson(reminder_old_list)
            UtilMethod.instance.setReminderList(s1, requireContext())

        }
        else{
            if(!UtilMethod.instance.isStringNullOrNot(s_repeating_time)){
                var scheduler : JobScheduler = context!!.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
                scheduler!!.cancel(567)
            }
            if(binding?.cbDrinkWaterEveryTime!!.isChecked){
                s_repeat = "time"
                s_alert_time = binding?.tvDrinkWaterEveryTime!!.text.toString()
            }
            else if(binding?.cbDrinkWaterEveryHour!!.isChecked){
                s_repeat = "hour"
                s_alert_time = binding?.tvDrinkWaterEveryHourTime!!.text.toString()
            }
            else if(binding?.cbDrinkWaterEveryDatetime!!.isChecked){
                s_repeat = "day"
                s_alert_time = binding?.tvDrinkWaterEveryDatetime!!.text.toString()
            }
            else if(binding?.cbDrinkWaterDayInWeek!!.isChecked){
                s_repeat = "week"
                s_alert_time = binding?.tvDrinkWaterDayInWeek!!.text.toString()
            }
            setDrinkWaterAlert2(s_repeat, s_alert_time)
        }

    }

    fun setTrackMealAlert3(repeat : String, time1 : String, list1 : ArrayList<TrackMealDTO>){
        var calendar : Calendar = Calendar.getInstance()
        var current_calendar : Calendar = Calendar.getInstance()
        var current_calendar1 : Calendar = Calendar.getInstance()

        reminderDTO!!.time = time1
        reminderDTO!!.repeating_val = repeat
        reminder_old_list.set(type-1, reminderDTO!!)
        var s1 : String = gson!!.toJson(reminder_old_list)
        UtilMethod.instance.setReminderList(s1, requireContext())
        var l1 : Long = 0L
        var short_l1 : Long = 0L
        if(list1!=null){
            l1 = calendar.timeInMillis

            var s1 : String? = ""
            var short_s1 : String? = ""
            for(i in 0..list1.size-1){
                var dto : TrackMealDTO = list1.get(i)
                if(dto!=null){
                    var date : Date? = UtilMethod.instance.getDateWithoutDate1(dto.time)
                    if(date!=null){
                        Log.v("Position ", "==> "+i)
                        var date_ms : Long = date.time
                        if(short_l1 == 0L || date_ms<short_l1){
                            short_l1 = date_ms
                            short_s1 = dto.time
                        }
                        if(date_ms>l1){
                            s1 = dto.time
                            break
                        }
                    }
                }
            }

            var set_date : Date? = null
            if(!UtilMethod.instance.isStringNullOrNot(s1)){
                set_date = UtilMethod.instance.getDateWithoutDate1(s1)
            }
            else{
                set_date = UtilMethod.instance.getDateWithoutDate2(short_s1)
            }

            current_calendar.add(Calendar.MINUTE, 15)
            var current_ms : Long = current_calendar.timeInMillis
            var current1_ms : Long = current_calendar1.timeInMillis
            var calendar_ms : Long = set_date!!.time
            var difference_ms : Long = 0

            if(current_ms >= calendar_ms){
                calendar.add(Calendar.DATE, 1)
                difference_ms = calendar.timeInMillis - current1_ms
            }
            else{
                difference_ms = calendar_ms - current1_ms
            }
            setAlertThroughMeal(difference_ms)

        }
    }

    fun setTrackMealAlert2(repeat : String, time1 : String){
        var calendar : Calendar = Calendar.getInstance()
        var current_calendar : Calendar = Calendar.getInstance()
        var current_calendar1 : Calendar = Calendar.getInstance()

        reminderDTO!!.time = time1
        reminderDTO!!.repeating_val = repeat
        reminder_old_list.set(type-1, reminderDTO!!)
        var s1 : String = gson!!.toJson(reminder_old_list)
        UtilMethod.instance.setReminderList(s1, requireContext())

        if(repeat.equals("day", true)){
            var date : Date? = UtilMethod.instance.getDateWithoutDate(time1)
            if(date!=null) {
                calendar.set(Calendar.HOUR_OF_DAY, date!!.hours)
                calendar.set(Calendar.MINUTE, date!!.minutes)
                calendar.set(Calendar.SECOND, 0)
            }
            current_calendar.add(Calendar.MINUTE, 15)
            var current_ms : Long = current_calendar.timeInMillis
            var current1_ms : Long = current_calendar1.timeInMillis
            var calendar_ms : Long = calendar.timeInMillis
            var difference_ms : Long = 0

            if(current_ms >= calendar_ms){
                calendar.add(Calendar.DATE, 1)
                difference_ms = calendar.timeInMillis - current1_ms
            }
            else{
                difference_ms = calendar_ms - current1_ms
            }
            setAlertThroughMeal(difference_ms)
        }
        else if(repeat.equals("week", true)){
            Log.v("Time Value", "=> "+time1)
            var i1 : Int = UtilMethod.instance.getDay(time1)
            calendar.set(Calendar.HOUR_OF_DAY, 8)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            var current_i1 : Int = calendar.get(Calendar.DAY_OF_WEEK)
            if(current_i1> i1){
                calendar.add(Calendar.DAY_OF_YEAR, 7 -(current_i1 - i1))
            }
            current_calendar.add(Calendar.MINUTE, 15)
            var current_ms : Long = current_calendar.timeInMillis
            var current1_ms : Long = current_calendar1.timeInMillis
            var calendar_ms : Long = calendar.timeInMillis
            var difference_ms : Long = 0

            if(current_ms >= calendar_ms){
                calendar.add(Calendar.DATE, 7)
                difference_ms = calendar.timeInMillis - current1_ms
            }
            else{
                difference_ms = calendar_ms - current1_ms
            }
            setAlertThroughMeal(difference_ms)
        }

    }

    fun setDrinkWaterAlert2(repeat : String, time1 : String){

        var calendar : Calendar = Calendar.getInstance()
        var current_calendar : Calendar = Calendar.getInstance()
        var current_calendar1 : Calendar = Calendar.getInstance()

        reminderDTO!!.time = time1
        reminderDTO!!.repeating_val = repeat
        reminder_old_list.set(type-1, reminderDTO!!)
        var s1 : String = gson!!.toJson(reminder_old_list)
        UtilMethod.instance.setReminderList(s1, requireContext())

        if(repeat.equals("day", true)){
            var date : Date? = UtilMethod.instance.getDateWithoutDate(time1)
            if(date!=null) {
                calendar.set(Calendar.HOUR_OF_DAY, date!!.hours)
                calendar.set(Calendar.MINUTE, date!!.minutes)
                calendar.set(Calendar.SECOND, 0)
            }
            current_calendar.add(Calendar.MINUTE, 15)
            var current_ms : Long = current_calendar.timeInMillis
            var current1_ms : Long = current_calendar1.timeInMillis
            var calendar_ms : Long = calendar.timeInMillis
            var difference_ms : Long = 0

            if(current_ms >= calendar_ms){
                calendar.add(Calendar.DATE, 1)
                difference_ms = calendar.timeInMillis - current1_ms
            }
            else{
                difference_ms = calendar_ms - current1_ms
            }
            setAlert(difference_ms)
        }
        else if(repeat.equals("week", true)){
            var i1 : Int = UtilMethod.instance.getDay(s_time)
            calendar.set(Calendar.HOUR_OF_DAY, 8)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            var current_i1 : Int = calendar.get(Calendar.DAY_OF_WEEK)
            if(current_i1> i1){
                calendar.add(Calendar.DAY_OF_YEAR, 7 -(current_i1 - i1))
            }
            current_calendar.add(Calendar.MINUTE, 15)
            var current_ms : Long = current_calendar.timeInMillis
            var current1_ms : Long = current_calendar1.timeInMillis
            var calendar_ms : Long = calendar.timeInMillis
            var difference_ms : Long = 0

            if(current_ms >= calendar_ms){
                calendar.add(Calendar.DATE, 7)
                difference_ms = calendar.timeInMillis - current1_ms
            }
            else{
                difference_ms = calendar_ms - current1_ms
            }
            setAlert(difference_ms)
        }
        else if(repeat.equals("hour", true)){
            UtilMethod.instance.getReminderThroughHour(hour1, context!!)
            var s1 : String? = UtilMethod.instance.getReminderTimeList(requireContext())
            if(!UtilMethod.instance.isStringNullOrNot(s1)){
                var list : List<String> = gson!!.fromJson(s1, Array<String>::class.java).toList()
                var list1 : ArrayList<String> = ArrayList()
                list1.addAll(list)
                if(list1!=null){
                    for(i in 0..list1.size-1){
                        var date : Date? = UtilMethod.instance.getDateWithoutDate(list1.get(i))
                        if(date!=null) {
                            calendar.set(Calendar.HOUR_OF_DAY, date!!.hours)
                            calendar.set(Calendar.MINUTE, date!!.minutes)
                            calendar.set(Calendar.SECOND, 0)
                            if(current_calendar.timeInMillis < calendar.timeInMillis ){
                                var difference_ms : Long = calendar.timeInMillis - current_calendar.timeInMillis
                                setAlert(difference_ms)
                                break
                            }
                        }
                    }
                }
            }
        }
        else if(repeat.equals("time", true)){
            UtilMethod.instance.getReminderThroughTime(time, context!!)
            var s1 : String? = UtilMethod.instance.getReminderTimeList(requireContext())
            if(!UtilMethod.instance.isStringNullOrNot(s1)){
                var list : List<String> = gson!!.fromJson(s1, Array<String>::class.java).toList()
                var list1 : ArrayList<String> = ArrayList()
                list1.addAll(list)
                if(list1!=null){
                    for(i in 0..list1.size-1){
                        var date : Date? = UtilMethod.instance.getDateWithoutDate(list1.get(i))
                        if(date!=null) {
                            calendar.set(Calendar.HOUR_OF_DAY, date!!.hours)
                            calendar.set(Calendar.MINUTE, date!!.minutes)
                            calendar.set(Calendar.SECOND, 0)
                            if(current_calendar.timeInMillis < calendar.timeInMillis ){
                                var difference_ms : Long = calendar.timeInMillis - current_calendar.timeInMillis
                                setAlert(difference_ms)
                                break
                            }
                        }
                    }
                }
            }

        }
    }

    fun setAlertThroughLogWeight(difference_ms : Long){
        var componentName : ComponentName = ComponentName(context!!, LogWeightSchedular::class.java)
        var info : JobInfo? = null
        if(Build.VERSION.SDK_INT > 23) {
            info = JobInfo.Builder(567, componentName)
                .setRequiresCharging(false)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_NONE)
                .setPersisted(false)
                .setMinimumLatency(difference_ms)
                .build()
        }
        else{
            info = JobInfo.Builder(678, componentName)
                .setRequiresCharging(false)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_NONE)
                .setPersisted(false)
                .setPeriodic(difference_ms)
                .build()
        }
        var scheduler : JobScheduler = context!!.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        scheduler!!.schedule(info)
    }


    fun setAlert(difference_ms : Long){
        var componentName : ComponentName = ComponentName(context!!, DrinkWaterSchedular::class.java)
        var info : JobInfo? = null
        if(Build.VERSION.SDK_INT > 23) {
            info = JobInfo.Builder(678, componentName)
                .setRequiresCharging(false)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_NONE)
                .setPersisted(false)
                .setMinimumLatency(difference_ms)
                .build()
        }
        else{
            info = JobInfo.Builder(567, componentName)
                .setRequiresCharging(false)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_NONE)
                .setPersisted(false)
                .setPeriodic(difference_ms)
                .build()
        }
        var scheduler : JobScheduler = context!!.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        scheduler!!.schedule(info)
    }


    fun setAlertThroughMeal(difference_ms : Long){
        var componentName : ComponentName = ComponentName(context!!, TrackMealSchedular::class.java)
        var info : JobInfo? = null
        if(Build.VERSION.SDK_INT > 23) {
            info = JobInfo.Builder(777, componentName)
                .setRequiresCharging(false)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_NONE)
                .setPersisted(false)
                .setMinimumLatency(difference_ms)
                .build()
        }
        else{
            info = JobInfo.Builder(777, componentName)
                .setRequiresCharging(false)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_NONE)
                .setPersisted(false)
                .setPeriodic(difference_ms)
                .build()
        }
        var scheduler : JobScheduler = context!!.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        scheduler!!.schedule(info)
    }
    fun showReminderDialog(title : String){
        dialog = Dialog(requireContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog!!.setContentView(R.layout.dialog_reminder_data)
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog!!.show()
        var tv_header : TextView = dialog!!.findViewById(R.id.tv_header)
        var tv_cancel : TextView = dialog!!.findViewById(R.id.tv_cancel)
        var rv_data : RecyclerView = dialog!!.findViewById(R.id.rv_data)
        tv_header!!.setTypeface(regular)
        tv_cancel!!.setTypeface(regular)
        tv_header!!.setText(title)
        var linearLayoutManager : LinearLayoutManager = LinearLayoutManager(requireContext())
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        rv_data.layoutManager = linearLayoutManager
        rv_data.adapter = ReminderthroughDialogAdapter(requireContext(), list, onItemClickCallback)
        tv_cancel.setOnClickListener(View.OnClickListener { view ->
            dialog!!.dismiss()
        })
    }

    private val onItemClickCallback : OnItemClickListener.OnItemClickCallback = object : OnItemClickListener.OnItemClickCallback{
        override fun onItemClicked(view: View?, position: Int) {
            dialog!!.dismiss()
            if(text_type == 1){
                binding?.tvRemindMeOnceTime!!.setText(""+list.get(position).value)
                binding?.cbTrackMealOnceEveryWeek!!.isChecked = true
            }
            else if(text_type == 2){
                date1 = position +1
                binding?.tvRemindMeLogweightTime!!.setText(""+list.get(position).value)
                binding?.cbLogweightEveryMonthOn!!.isChecked = true

            }
            else if(text_type == 3){
                binding?.tvDrinkWaterEveryHourTime!!.setText(""+list.get(position).value)
                hour1 = position+1
                binding?.cbDrinkWaterEveryHour?.isChecked = true
            }
            else if(text_type == 4){
                binding?.tvDrinkWaterEveryTime!!.setText(""+list.get(position).value)
                time = position+1
                binding?.cbDrinkWaterEveryTime?.isChecked = true
            }
            else if(text_type == 5){
                binding?.tvDrinkWaterDayInWeek!!.setText(""+list.get(position).value)
                binding?.cbDrinkWaterDayInWeek?.isChecked = true
            }
            else if(text_type == 6){
                binding?.tvLogweightRemindMeOnceEvery!!.setText(""+list.get(position).value)
                binding?.cbLogweightOnceEveryWeek?.isChecked = true
            }

        }

    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        if(buttonView == binding?.cbLogweightOnceEveryWeek){
            if(isChecked){
                binding?.cbLogweightEveryMonthOn!!.isChecked = false
            }
        }
        else if(buttonView == binding?.cbLogweightEveryMonthOn){
            if(isChecked){
                binding?.cbLogweightOnceEveryWeek!!.isChecked = false
            }
        }
        else if(buttonView == binding?.cbDrinkWaterDayInWeek){
            if(isChecked){
                binding?.cbDrinkWaterEveryDatetime!!.isChecked = false
                binding?.cbDrinkWaterEveryHour!!.isChecked = false
                binding?.cbDrinkWaterEveryTime!!.isChecked = false

            }
        }
        else if(buttonView == binding?.cbDrinkWaterEveryDatetime){
            if(isChecked){
                binding?.cbDrinkWaterDayInWeek!!.isChecked = false
                binding?.cbDrinkWaterEveryHour!!.isChecked = false
                binding?.cbDrinkWaterEveryTime!!.isChecked = false
            }
        }
        else if(buttonView == binding?.cbDrinkWaterEveryHour){
            if(isChecked){
                binding?.cbDrinkWaterEveryDatetime!!.isChecked = false
                binding?.cbDrinkWaterDayInWeek!!.isChecked = false
                binding?.cbDrinkWaterEveryTime!!.isChecked = false
            }
        }
        else if(buttonView == binding?.cbDrinkWaterEveryTime){
            if(isChecked){
                binding?.cbDrinkWaterEveryDatetime!!.isChecked = false
                binding?.cbDrinkWaterEveryHour!!.isChecked = false
                binding?.cbDrinkWaterDayInWeek!!.isChecked = false
            }
        }
        else if(buttonView == binding?.cbBreaakfast){
            if(isChecked){
                binding?.cbTrackMealOnceAt!!.isChecked = false
                binding?.cbTrackMealOnceEveryWeek!!.isChecked = false
            }
        }
        else if(buttonView == binding?.cbMorningSnack){
            if(isChecked){
                binding?.cbTrackMealOnceAt!!.isChecked = false
                binding?.cbTrackMealOnceEveryWeek!!.isChecked = false
            }
        }
        else if(buttonView == binding?.cbLunch){
            if(isChecked){
                binding?.cbTrackMealOnceAt!!.isChecked = false
                binding?.cbTrackMealOnceEveryWeek!!.isChecked = false
            }
        }
        else if(buttonView == binding?.cbEveningSnack){
            if(isChecked){
                binding?.cbTrackMealOnceAt!!.isChecked = false
                binding?.cbTrackMealOnceEveryWeek!!.isChecked = false
            }
        }
        else if(buttonView == binding?.cbDinner){
            if(isChecked){
                binding?.cbTrackMealOnceAt!!.isChecked = false
                binding?.cbTrackMealOnceEveryWeek!!.isChecked = false
            }
        }
        else if(buttonView == binding?.cbTrackMealOnceAt){
            if(isChecked){
                binding?.cbBreaakfast!!.isChecked = false
                binding?.cbMorningSnack!!.isChecked = false
                binding?.cbLunch!!.isChecked = false
                binding?.cbEveningSnack!!.isChecked = false
                binding?.cbDinner!!.isChecked = false
                binding?.cbTrackMealOnceEveryWeek!!.isChecked = false
            }
        }
        else if(buttonView == binding?.cbTrackMealOnceEveryWeek){
            if(isChecked){
                binding?.cbBreaakfast!!.isChecked = false
                binding?.cbMorningSnack!!.isChecked = false
                binding?.cbLunch!!.isChecked = false
                binding?.cbEveningSnack!!.isChecked = false
                binding?.cbDinner!!.isChecked = false
                binding?.cbTrackMealOnceAt!!.isChecked = false
            }
        }


    }
}