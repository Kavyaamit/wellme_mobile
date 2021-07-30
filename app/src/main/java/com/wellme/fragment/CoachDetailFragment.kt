package com.wellme.fragment

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import com.wellme.LeftSideMenuActivity
import com.wellme.R
import com.wellme.adapter.TimeSlotsAdapter
import com.wellme.databinding.FragmentCoachDetailsBinding
import com.wellme.dto.CoachProfileDTO
import com.wellme.dto.TimeSlotsDTO
import com.wellme.utils.AppConstants
import com.wellme.utils.AppService
import com.wellme.utils.OnItemClickListener
import com.wellme.utils.UtilMethod
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class CoachDetailFragment : Fragment(), View.OnClickListener{
    var binding : FragmentCoachDetailsBinding? = null
    var regular : Typeface? = null
    var bundle : Bundle? = null
    var coach_id : String? = ""
    var coach_fullname : String? = ""
    var access_token : String? = ""
    var coach_profile_dto : CoachProfileDTO? = null
    var s_full_name : String? = ""
    var s_bio : String? = ""
    var s_phone : String? = ""
    var s_email : String? = ""
    var s_gender : String? = ""
    var s_age : String? = ""
    var s_weight : String? = ""
    var s_height : String? = ""
    var time_pos : Int = -1
    var s_language_known : String? = ""
    var s_department : String? = ""
    var s_chest : String? = ""
    var s_waist : String? = ""
    var s_hips : String? = ""
    var mobile : String? = ""
    var s_profile_inage : String? = ""
    var s_start_time : String? = ""
    var s_end_time : String? = ""
    var s_select_time : String? = ""
    var s_select_date : String? = ""
    var language_known : ArrayList<String> = ArrayList()
    var coach_department : ArrayList<String> = ArrayList()


    var coach_time : MutableList<TimeSlotsDTO> = ArrayList()
    var city_dialog : BottomSheetDialog? = null
    var rv_item_city : RecyclerView? = null

    var timeSlotsAdapter : TimeSlotsAdapter? = null

    var header : TextView? = null
    var activity : Activity? = null
    var c = Calendar.getInstance()

        override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_coach_details, container, false)
        return binding?.root
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        this.activity = activity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as LeftSideMenuActivity).disableBottomBar()
        initView()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (activity as LeftSideMenuActivity).enableBottomBar()
    }

    fun initView(){
        regular = ResourcesCompat.getFont(requireContext(), R.font.poppins_regular)
        binding?.header!!.setTypeface(regular)
        binding?.tvAge!!.setTypeface(regular)
        binding?.tvAgeVal!!.setTypeface(regular)
        binding?.tvArmLeft!!.setTypeface(regular)
        binding?.tvArmLeftVal!!.setTypeface(regular)
        binding?.tvArmRight!!.setTypeface(regular)
        binding?.tvArmRightVal!!.setTypeface(regular)
        binding?.tvBioHeader!!.setTypeface(regular)
        binding?.tvBodyDetails!!.setTypeface(regular)
        binding?.tvCalf!!.setTypeface(regular)
        binding?.tvCalfVal!!.setTypeface(regular)
        binding?.tvChest!!.setTypeface(regular)
        binding?.tvChestVal!!.setTypeface(regular)
        binding?.tvDescription!!.setTypeface(regular)
        binding?.tvEmail!!.setTypeface(regular)
        binding?.tvGender!!.setTypeface(regular)
        binding?.tvGenderVal!!.setTypeface(regular)
        binding?.tvHeight!!.setTypeface(regular)
        binding?.tvHeightVal!!.setTypeface(regular)
        binding?.tvHips!!.setTypeface(regular)
        binding?.tvHipsVal!!.setTypeface(regular)
        binding?.tvLanguage!!.setTypeface(regular)
        binding?.tvLanguageVal!!.setTypeface(regular)
        binding?.tvMobileNumber!!.setTypeface(regular)
        binding?.tvMusclesMass!!.setTypeface(regular)
        binding?.tvMusclesMassVal!!.setTypeface(regular)
        binding?.tvPersonalDetails!!.setTypeface(regular)
        binding?.tvPersonalDetails!!.setTypeface(regular)
        binding?.tvRequestForBooking!!.setTypeface(regular)
        binding?.tvSpeciality!!.setTypeface(regular)
        binding?.tvSpecialityVal!!.setTypeface(regular)
        binding?.tvThigh!!.setTypeface(regular)
        binding?.tvUserName!!.setTypeface(regular)
        binding?.tvThighVal!!.setTypeface(regular)
        binding?.tvWaist!!.setTypeface(regular)
        binding?.tvWeight!!.setTypeface(regular)
        binding?.tvWaistVal!!.setTypeface(regular)
        binding?.tvWeightVal!!.setTypeface(regular)
        binding?.back?.setOnClickListener(this)
        binding?.llSave?.setOnClickListener(this)
        binding?.ivCall?.setOnClickListener(this)
        binding?.ivChat?.setOnClickListener(this)
        access_token = UtilMethod.instance.getAccessToken(requireContext())
        bundle = arguments
        if(bundle!=null){
            coach_id = bundle!!.getString("coach_id")
            if(!UtilMethod.instance.isStringNullOrNot(coach_id)){
                callCoachDetailAPI()
            }
        }

    }

    fun callCoachDetailAPI(){

        var progressDialog : ProgressDialog = ProgressDialog.show(requireContext(), "", "")
        progressDialog.setContentView(R.layout.progress_loader)
        progressDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        progressDialog.setCancelable(false)

        val service = Retrofit.Builder()
            .baseUrl(""+ AppConstants.BASE_URL_NEW)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(AppService::class.java)


        service.getCoachProfile("Bearer "+access_token, coach_id!!).enqueue(object :
            Callback<ResponseBody> {

            /* The HTTP call failed. This method is run on the main thread */
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.d("TAG_", "An error happened!")
                progressDialog.dismiss()
                t.printStackTrace()
                dialogForCheckNetworkError()
            }

            /* The HTTP call was successful, we should still check status code and response body
             * on a production app. This method is run on the main thread */
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                /* This will print the response of the network call to the Logcat */
                progressDialog.dismiss()
                Log.v("REsponse Value ", "==>"+response.raw().code())
                Log.v("REsponse Value ", "==>"+access_token)
                Log.v("REsponse Value ", "==>"+coach_id)
                var response1 : String = response.body()!!.string()
                Log.v("Coach Detail Response ", "==> "+response1)
                if(!UtilMethod.instance.isStringNullOrNot(response1)){
                    var object1 : JSONObject = JSONObject(response1)
                    if(object1!=null){
                        var gson : Gson = Gson()
                        coach_profile_dto = gson.fromJson(object1.getJSONObject("coach_profile").toString(), CoachProfileDTO::class.java)
                        if(coach_profile_dto!=null){
                            binding?.fullLayout!!.visibility = View.VISIBLE
                            s_full_name = coach_profile_dto!!.full_name
                            s_phone = coach_profile_dto!!.phone
                            s_email = coach_profile_dto!!.email
                            s_gender = coach_profile_dto!!.gender
                            s_age = coach_profile_dto!!.age
                            s_bio = coach_profile_dto!!.bio
                            s_height = coach_profile_dto!!.height
                            s_weight = coach_profile_dto!!.weight
                            s_chest = coach_profile_dto!!.chest
                            s_waist = coach_profile_dto!!.waist
                            s_hips = coach_profile_dto!!.hip
                            s_language_known = coach_profile_dto!!.language
                            s_department = coach_profile_dto!!.coach_department
                            mobile = coach_profile_dto!!.phone
                            s_profile_inage = coach_profile_dto!!.profile_image
                            s_start_time = coach_profile_dto!!.start_time
                            s_end_time = coach_profile_dto!!.end_date


                            if(!UtilMethod.instance.isStringNullOrNot(s_full_name)){
                                binding?.tvUserName?.setText(s_full_name)
                            }
                            else{
                                binding?.tvUserName?.visibility = View.GONE
                            }
                            if(!UtilMethod.instance.isStringNullOrNot(s_phone)){
                                binding?.tvMobileNumber?.setText(s_phone)
                            }
                            else{
                                binding?.tvMobileNumber?.visibility = View.GONE
                            }
                            if(!UtilMethod.instance.isStringNullOrNot(s_email)){
                                binding?.tvEmail?.setText(s_email)
                            }
                            else{
                                binding?.tvEmail?.visibility = View.GONE
                            }

                            if(!UtilMethod.instance.isStringNullOrNot(s_bio)){
                                binding?.tvDescription?.setText(s_bio)
                            }
                            else{
                                binding?.tvDescription?.visibility = View.GONE
                                binding?.llDescription?.visibility = View.GONE
                            }

                            setData(binding?.tvAgeVal!!, s_age!!, "1")
                            setData(binding?.tvChestVal!!, s_chest!!, "2")
                            setData(binding?.tvWaistVal!!, s_waist!!, "2")
                            setData(binding?.tvHipsVal!!, s_hips!!, "2")
                            setData(binding?.tvWeightVal!!, s_weight!!, "2")
                            setData(binding?.tvHeightVal!!, s_height!!, "2")
                            setData(binding?.tvGenderVal!!, s_gender!!, "3")
                            setData(binding?.tvLanguageVal!!, s_language_known!!, "3")
                            setData(binding?.tvSpecialityVal!!, s_department!!, "3")
                            Picasso.get().load(AppConstants.IMAGE_URL_NEW1+""+s_profile_inage).error(R.drawable.default_image).into(binding!!.ivProfile)



                        }
                        else{
                            binding?.fullLayout!!.visibility = View.GONE
                        }
                    }
                }
                else{

                }
            }
        })
    }

    fun dialogForCheckNetworkError(){
        try{
            var alertDialog : AlertDialog.Builder
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                alertDialog = AlertDialog.Builder(context, android.R.style.Theme_Material_Light_Dialog_Alert)
            }
            else{
                alertDialog = AlertDialog.Builder(context)
            }
            alertDialog.setTitle("")
            alertDialog.setMessage(Html.fromHtml(""+resources.getString(R.string.network_error)))
            alertDialog.setCancelable(false)
            alertDialog.setPositiveButton(context!!.resources!!.getString(R.string.try_again),
                DialogInterface.OnClickListener { dialog, which -> callCoachDetailAPI() })
            alertDialog.setNegativeButton(context!!.resources!!.getString(R.string.network_cancel), DialogInterface.OnClickListener { dialog, which -> requireActivity().supportFragmentManager.popBackStack() })
            var alertDialog1 = alertDialog.create()
            alertDialog1.show()
        }
        catch (e: Exception){

        }
    }

    fun dialogForCheckNetworkError1(){
        try{
            var alertDialog : AlertDialog.Builder
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                alertDialog = AlertDialog.Builder(context, android.R.style.Theme_Material_Light_Dialog_Alert)
            }
            else{
                alertDialog = AlertDialog.Builder(context)
            }
            alertDialog.setTitle("")
            alertDialog.setMessage(Html.fromHtml(""+resources.getString(R.string.network_error)))
            alertDialog.setCancelable(false)
            alertDialog.setPositiveButton(context!!.resources!!.getString(R.string.try_again),
                DialogInterface.OnClickListener { dialog, which -> callBookingForCoachAPI() })
            alertDialog.setNegativeButton(context!!.resources!!.getString(R.string.network_cancel), DialogInterface.OnClickListener { dialog, which ->  })
            var alertDialog1 = alertDialog.create()
            alertDialog1.show()
        }
        catch (e: Exception){

        }
    }

    fun callBookingForCoachAPI(){

        var progressDialog : ProgressDialog = ProgressDialog.show(requireContext(), "", "")
        progressDialog.setContentView(R.layout.progress_loader)
        progressDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        progressDialog.setCancelable(false)

        val service = Retrofit.Builder()
            .baseUrl(""+ AppConstants.BASE_URL_NEW)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(AppService::class.java)


        service.callBookCoach("Bearer "+access_token, coach_id!!, s_full_name,s_select_date,s_select_time!!).enqueue(object :
            Callback<ResponseBody> {

            /* The HTTP call failed. This method is run on the main thread */
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.d("TAG_", "An error happened!")
                progressDialog.dismiss()
                t.printStackTrace()
                dialogForCheckNetworkError1()
            }

            /* The HTTP call was successful, we should still check status code and response body
             * on a production app. This method is run on the main thread */
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                /* This will print the response of the network call to the Logcat */
                progressDialog.dismiss()
                Log.v("REsponse Value ", "==>"+response.raw().code())
                var response1 : String = response.body()!!.string()

                if(!UtilMethod.instance.isStringNullOrNot(response1)){
                    var object1 : JSONObject = JSONObject(response1)
                    if(object1!=null){
                        var msg : String = object1.getString("message")
                        UtilMethod.instance.dialogOK(requireContext(), "", msg, requireContext().resources.getString(R.string.ok), false)
                        binding?.llSave!!.visibility = View.GONE
                    }
                }
                else{

                }
            }
        })
    }


    fun showDialog(){
        try{
            var alertDialog : AlertDialog.Builder
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                alertDialog = AlertDialog.Builder(requireContext(), android.R.style.Theme_Material_Light_Dialog_Alert)
            }
            else{
                alertDialog = AlertDialog.Builder(requireContext())
            }
            alertDialog.setTitle("")
            alertDialog.setMessage(Html.fromHtml("Do you want to send request for booking this coach?"))
            alertDialog.setCancelable(false)
            alertDialog.setNegativeButton(requireContext().resources.getString(R.string.no),
                DialogInterface.OnClickListener { dialog, which ->
                    dialog.dismiss()

                })
            alertDialog.setPositiveButton(requireContext().resources.getString(R.string.yes),
                DialogInterface.OnClickListener { dialog, which ->
                    dialog.dismiss()

                    showBottomDialog()

                })
            var alertDialog1 = alertDialog.create()
            alertDialog1.show()
        }
        catch (e: Exception){

        }
    }

    fun setData(tv : TextView, s_value : String, type : String){
        if(!UtilMethod.instance.isStringNullOrNot(s_value)){
            if(type.equals("1", true)){
                var i1 : Int = s_value.toInt()
                if(i1>0){
                    tv.setText(s_value)
                }
                else{
                    tv.setText(requireContext().resources.getString(R.string.not_mentioned))
                }
            }
            else if(type.equals("2", true)){
                var i1 : Float = s_value.toFloat()
                if(i1>0){
                    tv.setText(UtilMethod.instance.getFormatedAmountString(i1))
                }
                else{
                    tv.setText(requireContext().resources.getString(R.string.not_mentioned))
                }
            }
            else{
                tv.setText(s_value)
            }
        }
        else{
            tv.setText(requireContext().resources.getString(R.string.not_mentioned))
        }
    }

    override fun onClick(v: View?) {
        if(v == binding?.back){
            requireActivity().onBackPressed()
        }
        else if(v == binding?.llSave){
            showDialog()


        }
        else if(v == binding?.ivChat){
            var fragment : Fragment = ChatFragment()
            var fragmentTransaction : FragmentTransaction? = requireActivity().supportFragmentManager.beginTransaction()
            var bundle : Bundle = Bundle()
            bundle.putString("receiver_id", coach_id)
            bundle.putString("receiver_name", s_full_name)
            fragment.arguments = bundle
            fragmentTransaction?.replace(R.id.container_home, fragment)
            fragmentTransaction?.addToBackStack(null)
            fragmentTransaction?.commit()
        }
        else if(v == binding?.ivCall){

            if(!UtilMethod.instance.isStringNullOrNot(mobile)){
                call(mobile+"")
            }

        }
    }


    fun call(mobile : String) {
        val dialIntent = Intent(Intent.ACTION_DIAL)
        dialIntent.data = Uri.parse("tel:" + mobile)
        startActivity(dialIntent)
    }

    fun setTimeSlots(start_time : String,end_time : String,date : String){
        Log.v("Start Time", "==> "+start_time)
        Log.v("End Time", "==> "+end_time)
        Log.v("Start Time", "==> "+date)

    }



    fun setNewTimeSlots(start_time : String,end_time : String,date : String){

//        var format : String = "dd/MM/yyyy hh:mm:ss"
        var format : String = "yyyy-MM-dd hh:mm:ss"
        var sdf : DateFormat = SimpleDateFormat(format)
        var dateObj1 : Date  = sdf.parse(date+ " " + start_time)
        var dateObj2 : Date  = sdf.parse(date + " " + end_time)

        System.out.println("Date Start: "+dateObj1)
        System.out.println("Date End: "+dateObj2)

        var df : DateFormat = SimpleDateFormat("hh:mm a")

         Log.d("dif", "<<<>>>>"+df.format(dateObj1.time))
        Log.d("dif", "<<<>>>>"+df.format(dateObj2.time))

        var dif : Long = dateObj1.time
        Log.d("dif", ">>>>"+dif)

        var currentTime : Long  = Calendar.getInstance().timeInMillis

        while (dif <= dateObj2.time) {
            var slot : Date  = Date(dif)
            var date1 : String=  df.format(slot.time)
            var time : TimeSlotsDTO? = null
            if (dif<currentTime){
//                 time = TimeSlotsDTO("",date1,true)
            }
            else{
                time  = TimeSlotsDTO("",date1,false)
            }
            System.out.println("Hour Slot --->" + date1)
            System.out.println("Hour Hour --->" + time)

            if (time!=null){
                coach_time.add(time)
            }

            dif += 3600000/3
        }

        if (timeSlotsAdapter!=null){
            timeSlotsAdapter?.setnotify()
            timeSlotsAdapter?.notifyDataSetChanged()
        }else{

            setAdapter()

        }
        Log.d("size","coachtime>>>"+coach_time.size)

        if (coach_time.size==0){

            setCurrentDate(header!!,true)

            UtilMethod.instance.dialogOK(requireContext(), "", "the coach not available at this time", requireContext().resources.getString(R.string.ok), false)
        }
    }


    fun showBottomDialog(){
        city_dialog = BottomSheetDialog(requireContext(), android.R.style.Theme_Black_NoTitleBar)
        city_dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        city_dialog!!.setContentView(R.layout.dialog_time_slot)
        city_dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        city_dialog!!.show()

        header = city_dialog?.findViewById(R.id.header)
        var tv_send_request : TextView? = city_dialog?.findViewById(R.id.send_request)
        rv_item_city = city_dialog?.findViewById(R.id.rv_item)
        var back : ImageView? = city_dialog?.findViewById(R.id.back)


         setAdapter()

        setCurrentDate(header!!,false)

        var regular1 : Typeface? = ResourcesCompat.getFont(requireContext(), R.font.poppins_regular)
        header?.setTypeface(regular1)





        header?.setOnClickListener(View.OnClickListener { view->

            selectDate(header!!)
        })

        back?.setOnClickListener(View.OnClickListener {view ->
            city_dialog!!.dismiss()

        })

        tv_send_request?.setOnClickListener(View.OnClickListener {view ->


            if (isAllselect()){
                city_dialog!!.dismiss()
                callBookingForCoachAPI()
            }


        })



    }


    private val onItemCallback : OnItemClickListener.OnItemClickCallback = object : OnItemClickListener.OnItemClickCallback{
        override fun onItemClicked(view: View?, position: Int) {

            if (coach_time.get(position).disable){

                UtilMethod.instance.dialogOK(requireContext(),"","please select other time",requireContext().resources.getString(R.string.ok),false)

            }else{

                time_pos = position
                s_select_time = coach_time.get(position).time

                var df : DateFormat = SimpleDateFormat("hh:mm a")
                var sdf : DateFormat = SimpleDateFormat("HH:mm")

                var date :Date = df.parse(s_select_time)

                s_select_time = sdf.format(date)


                if(timeSlotsAdapter!=null){
                    timeSlotsAdapter?.setData(time_pos)
                    timeSlotsAdapter?.notifyDataSetChanged()
                }

            }


            Log.d("s_select_time ",">>>>>"+s_select_time)
            Log.d("time_pos ",">>>>>"+time_pos)
            /*binding?.tvState!!.setText(s_state_name)
            state_dialog!!.dismiss()
            state_id = state_list.get(position).id
            search_city="";
            callCityAPITask(state_list.get(position).id)*/



        }

    }


    fun setCurrentDate(textView: TextView,isAdd :Boolean){
        val c = Calendar.getInstance()

        if (isAdd){

            c.add(Calendar.DATE,1)
        }


        var dateFormat : DateFormat= SimpleDateFormat("yyyy-MM-dd")


        var today : Date  = c.time;//getting date
        var formatter  :DateFormat = SimpleDateFormat("MMM dd, yyyy")//formating according to my need
        var date : String  = formatter.format(today)
        textView.setText(date)

        s_select_date = dateFormat.format(today)


        if(!UtilMethod.instance.isStringNullOrNot(s_select_date)){
            if (coach_time!=null){
                if (coach_time.size>0){
                    coach_time.clear()

                    setNewTimeSlots("06:00:00","23:59:59",s_select_date+"")
                }else{
                    setNewTimeSlots("06:00:00","23:59:59",s_select_date+"")
                }
            }
        }




//        setNewTimeSlots(s_start_time+"",s_end_time+"",s_select_date+"")
    }


    fun selectDate(textView: TextView){

        var year = c.get(Calendar.YEAR)
        var month = c.get(Calendar.MONTH)
        var day = c.get(Calendar.DAY_OF_MONTH)

        var thisAMonth :Int = 0
        var thisADay :Int = 0
        var thisAYear :Int = 0

        var dpd : DatePickerDialog? = null


        dpd  = DatePickerDialog(requireContext(), DatePickerDialog.OnDateSetListener { view2, thisYear, thisMonth, thisDay ->

            thisAMonth = thisMonth + 1
            thisADay = thisDay
            thisAYear = thisYear

            s_select_date = ""+thisYear + "-" + thisAMonth + "-" + thisDay


            if(!UtilMethod.instance.isStringNullOrNot(s_select_date)){
                if (coach_time!=null){
                    if (coach_time.size>0){
                        coach_time.clear()
                        time_pos = -1
                        s_select_time=""
                        timeSlotsAdapter?.setData(time_pos)
                        setNewTimeSlots("06:00:00","23:59:59",s_select_date+"")
                    }
                }
            }

            c.set(thisYear,thisMonth,thisDay)


            var dateFormat : DateFormat= SimpleDateFormat("yyyy-MM-dd")
            var convertedDate : Date = Date()
            try {
                convertedDate = dateFormat.parse(s_select_date);
                var sdfnewformat : DateFormat  = SimpleDateFormat("MMM dd, yyyy")
                var finalDateString :String  = sdfnewformat.format(convertedDate)
                textView.setText(finalDateString)
            } catch (e : ParseException) {
                e.printStackTrace()
            }

        }, year, month, day)
        dpd?.datePicker?.setMinDate(System.currentTimeMillis() - 1000)
        dpd.show()
    }


    fun isAllselect() : Boolean{

        if (UtilMethod.instance.isStringNullOrNot(s_select_date)){

            UtilMethod.instance.dialogOK(requireContext(), "", "please select date", requireContext().resources.getString(R.string.ok), false)
            return false
        }else if (UtilMethod.instance.isStringNullOrNot(s_select_time)){

            UtilMethod.instance.dialogOK(requireContext(), "", "please select time", requireContext().resources.getString(R.string.ok), false)
            return false
        }


        return true
    }


    fun setAdapter(){

        var linearLayoutManager1 : LinearLayoutManager = LinearLayoutManager(requireContext())
        linearLayoutManager1.orientation = LinearLayoutManager.HORIZONTAL
        rv_item_city?.layoutManager = linearLayoutManager1
        timeSlotsAdapter = TimeSlotsAdapter(requireContext(), coach_time, onItemCallback, time_pos)
        rv_item_city!!.adapter = timeSlotsAdapter

    }

}