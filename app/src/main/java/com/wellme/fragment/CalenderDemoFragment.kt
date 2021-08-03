package com.wellme.fragment

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.wellme.LeftSideMenuActivity
import com.wellme.R
import com.wellme.adapter.CalenderAdapter
import com.wellme.databinding.CalenderDemoBinding

import com.wellme.dto.FeedbackTypeDTO
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
import java.util.*
import kotlin.collections.ArrayList

class CalenderDemoFragment : Fragment(), View.OnClickListener{
    var binding : CalenderDemoBinding? = null

    var regular : Typeface? = null
    var s_access_token : String? = ""
    var s_month : Int =0
    var s_year : Int =0
    var feedback_type_list : List<FeedbackTypeDTO> = ArrayList()
    var linearLayoutManager : LinearLayoutManager? = null


    var days_list : Array<String> = Array(42){""}
//    val days_list = arrayOfNulls<String>(42) // returns Array<String?>


    var activity : Activity? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.calender_demo, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        initView()
    }



   fun getCurrentDate(){

       var calendar = Calendar.getInstance()

//        val day:String = LocalDate.now().dayOfWeek.name

//       Log.d("date",">>>>"+calendar.minimalDaysInFirstWeek)

       val day: Int = calendar.get(java.util.Calendar.DAY_OF_WEEK)
       Log.d("date",">>>>"+day)

       when (day) {
           java.util.Calendar.SUNDAY -> {
               Log.d("date",">>>>"+Calendar.SUNDAY)
           }
           java.util.Calendar.MONDAY -> {
               Log.d("date",">>>>"+Calendar.MONDAY)
           }
           java.util.Calendar.SATURDAY -> {
               Log.d("date",">>>>SATURDAY")
           }
       }
   }

fun setAdapter(){
    linearLayoutManager = LinearLayoutManager(requireContext())
    linearLayoutManager!!.orientation = LinearLayoutManager.HORIZONTAL
    binding!!.rvCalender.layoutManager = linearLayoutManager

    if(days_list!=null){
        binding?.rvCalender?.adapter = CalenderAdapter(requireContext(), days_list ,s_month,s_year)
    }
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

    fun initView(){
        regular = ResourcesCompat.getFont(requireContext(), R.font.poppins_regular)

        binding?.header?.setTypeface(regular)
        binding?.back?.setOnClickListener(this)
        s_access_token = UtilMethod.instance.getAccessToken(requireContext())
        var calendar = Calendar.getInstance()

        s_month = calendar.get(Calendar.MONTH)
        s_year = calendar.get(Calendar.YEAR)

        Log.d("Month",">>>"+s_month+">>>"+s_year)
        setCalendar()
//        getCurrentDate()
    }

    override fun onClick(v: View?) {
        if(v == binding?.back){
            requireActivity().onBackPressed()
        }

    }






    var onItemClickCallback : OnItemClickListener.OnItemClickCallback = object : OnItemClickListener.OnItemClickCallback{

        override fun onItemClicked(view: View?, position: Int) {


        }

    }

    fun dialogForCheckNetworkError(status : Int){
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
                DialogInterface.OnClickListener { dialog, which -> if(status == 1){callFeedbackTypeListAPI()}else if(status==2){callFeedbackTypeListAPI()} })
            alertDialog.setNegativeButton(context!!.resources!!.getString(R.string.network_cancel), DialogInterface.OnClickListener { dialog, which -> })
            var alertDialog1 = alertDialog.create()
            alertDialog1.show()
        }
        catch (e: Exception){

        }
    }

    fun callFeedbackTypeListAPI(){

        var progressDialog : ProgressDialog = ProgressDialog.show(requireContext(), "", "")
        progressDialog.setContentView(R.layout.progress_loader)
        progressDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        progressDialog.setCancelable(false)

        val service = Retrofit.Builder()
            .baseUrl(""+ AppConstants.BASE_URL_NEW)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(AppService::class.java)


        service.getFoodFeedbackTypeList().enqueue(object :
            Callback<ResponseBody> {

            /* The HTTP call failed. This method is run on the main thread */
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.d("TAG_", "An error happened!")
                progressDialog.dismiss()
                t.printStackTrace()
                dialogForCheckNetworkError(1)

            }

            /* The HTTP call was successful, we should still check status code and response body
             * on a production app. This method is run on the main thread */
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                /* This will print the response of the network call to the Logcat */
                progressDialog.dismiss()
                var response1 : String = response.body()!!.string()
                Log.v("Subscription Response ", "==> "+response1)
                if(!UtilMethod.instance.isStringNullOrNot(response1)){
                    var object1 : JSONObject = JSONObject(response1)
                    if(object1!=null){
                        var gson : Gson = Gson()
                        feedback_type_list = gson.fromJson(object1.getJSONArray("feedback_type").toString(), Array<FeedbackTypeDTO>::class.java).toList()
                        if(feedback_type_list!=null){
                            Log.v("Feedback Size ", "==> "+feedback_type_list.size)
                        }
                    }
                }
                else{

                }
            }
        })
    }

    fun setCalendar(){
       var cal = Calendar.getInstance();



        cal[Calendar.MONTH] = s_month
        cal[Calendar.YEAR] = s_year
        cal[Calendar.DATE] = 1

        var date = cal.time

        var max :Int = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        var d1 : Int = cal.get(Calendar.DAY_OF_WEEK) - 1

        Log.d("day_list",">>>>"+d1)

        days_list = Array(42){ "" }

        for (i in d1..42){

            var j : Int = i-d1+1
            if (j<=max){
                Log.d("setData",">>>"+j)
                days_list.set(i,""+ j)
            }

        }

       setAdapter()

        Log.d("day_list",">>>>"+days_list.get(5))
    }


    /*public void setCalendar() {
        cal = Calendar.getInstance();
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.YEAR, year1);
        cal.set(Calendar.DATE, 1);
        Date date1 = cal.getTime();
        String year = utilities.formateDateShowAcc1(date1.toString());
        binding.tvMonth.setText("" + year);
        int max = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        int d1 = cal.get(Calendar.DAY_OF_WEEK) - 1;
        days_list = new String[42];
        for (int i = d1; i < 42; i++) {

            int j = i - d1 + 1;
            if (j <= max) {
                days_list[i] = j + "";
            }
        }
        calenderTimingAdapter = new CalenderTimingAdapter1(context, days_list, lunch_timing, dinner_timing, subscription_list, month, year1);
        binding.calender.setAdapter(calenderTimingAdapter);
        helper = new Helper();
        helper.getGridViewSize(binding.calender);


        binding.calender.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                Calendar cal = Calendar.getInstance();
                int current_month = cal.get(Calendar.MONTH);

                if (month == current_month) {
                    int current_day = cal.get(Calendar.DATE);
                    String s_day = days_list[position];
                    if (!UtilMethod.isStringNullOrBlank(s_day)) {
                        int day = Integer.parseInt(s_day);
                        if (day < current_day) {

                        } else {
                            setMonth1(position);
                        }
                    }
                } else {
                    setMonth1(position);
                }
                return true;
            }
        });


    }*/

}