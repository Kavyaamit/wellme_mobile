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
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.wellme.LeftSideMenuActivity
import com.wellme.R
import com.wellme.databinding.FragmentAddWaterIntakeBinding
import com.wellme.dto.CurrentDateDTO
import com.wellme.dto.WaterIntakeDTO
import com.wellme.utils.AppConstants
import com.wellme.utils.AppService
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


class WaterIntakeFragment : Fragment(), View.OnClickListener{
    var binding : FragmentAddWaterIntakeBinding? = null
    var quantity : Float = 0f
    var access_token : String? = ""
    var regular : Typeface? = null
    var water_intake_list : List<WaterIntakeDTO> = ArrayList()
    var calendar : Calendar? = null
    var date_list : List<CurrentDateDTO> = ArrayList()
    var bun : Bundle? = null
    var s_date : String? = ""
    var activity : Activity? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_water_intake, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        date_list = UtilMethod.instance.getDateList()
        initView()
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        this.activity = activity
    }

    override fun onResume() {
        super.onResume()
        (activity as LeftSideMenuActivity).enableBottomBar()
        (activity as LeftSideMenuActivity).setActiveSection(0)

    }

    fun initView(){
        regular = ResourcesCompat.getFont(requireContext(), R.font.poppins_regular)
        access_token = UtilMethod.instance.getAccessToken(requireContext())
        binding?.header?.setTypeface(regular)
        binding?.tvDailyWaterText?.setTypeface(regular)
        binding?.tvWaterQuantity?.setTypeface(regular)
        binding?.tvWaterText?.setTypeface(regular)
        binding?.tvWaterText?.setTypeface(regular)
        binding?.tvWaterQuantity1?.setTypeface(regular)
        binding?.tvWaterQuantity2?.setTypeface(regular)
        binding?.tvWaterQuantity3?.setTypeface(regular)
        binding?.tvWaterQuantity4?.setTypeface(regular)
        binding?.tvWaterQuantity5?.setTypeface(regular)
        binding?.tvWaterQuantity6?.setTypeface(regular)
        binding?.tvWaterQuantity7?.setTypeface(regular)
        binding?.tvDate1?.setTypeface(regular)
        binding?.tvDate2?.setTypeface(regular)
        binding?.tvDate3?.setTypeface(regular)
        binding?.tvDate4?.setTypeface(regular)
        binding?.tvDate5?.setTypeface(regular)
        binding?.tvDate6?.setTypeface(regular)
        binding?.tvDate7?.setTypeface(regular)

        binding?.back?.setOnClickListener(this)
        binding?.ivPlus?.setOnClickListener(this)
        binding?.ivMinus?.setOnClickListener(this)
        bun = arguments
        if(bun!=null){
            s_date = bun!!.getString("date")
        }


        getWaterIntakesTask()

    }
    fun setTextData(quantity : Float, flag : Boolean){

        if(quantity>0 && quantity < 12){
            binding?.ivPlus!!.setImageDrawable(requireContext().resources.getDrawable(R.drawable.blue_plus))
            binding?.ivMinus!!.setImageDrawable(requireContext().resources.getDrawable(R.drawable.blue_minus))
        }
        else if(quantity == 0f){
            binding?.ivPlus!!.setImageDrawable(requireContext().resources.getDrawable(R.drawable.blue_plus))
            binding?.ivMinus!!.setImageDrawable(requireContext().resources.getDrawable(R.drawable.gray_minus))
        }
        else if(quantity == 12f){
            binding?.ivPlus!!.setImageDrawable(requireContext().resources.getDrawable(R.drawable.gray_plus))
            binding?.ivMinus!!.setImageDrawable(requireContext().resources.getDrawable(R.drawable.blue_minus))
        }
        if(quantity  == 0f){
            binding?.ivGlass!!.setImageDrawable(requireContext().resources.getDrawable(R.drawable.empty_glass))
        }
        else if(quantity  == 1f){
            binding?.ivGlass!!.setImageDrawable(requireContext().resources.getDrawable(R.drawable.glass_1))
        }
        else if(quantity  == 2f){
            binding?.ivGlass!!.setImageDrawable(requireContext().resources.getDrawable(R.drawable.glass_2))
        }
        else if(quantity  == 3f){
            binding?.ivGlass!!.setImageDrawable(requireContext().resources.getDrawable(R.drawable.glass_3))
        }
        else if(quantity  == 4f){
            binding?.ivGlass!!.setImageDrawable(requireContext().resources.getDrawable(R.drawable.glass_4))
        }
        else if(quantity  == 5f || quantity  == 6f){
            binding?.ivGlass!!.setImageDrawable(requireContext().resources.getDrawable(R.drawable.glass_5))
        }
        else if(quantity  == 7f){
            binding?.ivGlass!!.setImageDrawable(requireContext().resources.getDrawable(R.drawable.glass_6))
        }
        else if(quantity  == 8f || quantity  == 9f){
            binding?.ivGlass!!.setImageDrawable(requireContext().resources.getDrawable(R.drawable.glass_7))
        }
        else if(quantity  == 10f || quantity  == 11f){
            binding?.ivGlass!!.setImageDrawable(requireContext().resources.getDrawable(R.drawable.glass_8))
        }
        else if(quantity  == 12f){
            binding?.ivGlass!!.setImageDrawable(requireContext().resources.getDrawable(R.drawable.glass_9))
        }

        var s_quan : String = ""+quantity.toInt()
        binding?.tvWaterQuantity7!!.setText(s_quan+"/12")
        var f1 : Float = 0f
        f1 = quantity/12
        var s1: String = ""+quantity.toInt()
        binding?.tvWaterText?.setText(s1+" of 12 Glasses")


        val param1 = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.MATCH_PARENT,
            f1
        )
        binding?.llFill!!.layoutParams = param1

        val param = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            0,
            f1
        )
        binding?.layout7!!.layoutParams = param


        var amount: Int = quantity.toInt() * 250
        Log.v("Amount ", "==> "+amount)
        if(flag) {
            callAddWeightTask("" + amount)
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
                DialogInterface.OnClickListener { dialog, which -> if(status==1){getWaterIntakesTask()} })
            alertDialog.setNegativeButton(context!!.resources!!.getString(R.string.network_cancel), DialogInterface.OnClickListener { dialog, which ->  })
            var alertDialog1 = alertDialog.create()
            alertDialog1.show()
        }
        catch (e: Exception){

        }
    }


    fun getWaterIntakesTask(){

        var progressDialog : ProgressDialog = ProgressDialog.show(requireContext(), "", "")
        progressDialog.setContentView(R.layout.progress_loader)
        progressDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        progressDialog.setCancelable(false)

        val service = Retrofit.Builder()
            .baseUrl(""+ AppConstants.BASE_URL_NEW)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(AppService::class.java)


        service.callWaterIntake("Bearer "+access_token).enqueue(object : Callback<ResponseBody> {

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
                var code : Int = response.raw().code()
                Log.v("Raw Code", "==> "+response.raw())
                if(code == 200) {
                    var response1: String = response.body()!!.string()
                    if (!UtilMethod.instance.isStringNullOrNot(response1)) {
                        var object1: JSONObject = JSONObject(response1)
                        if(object1!=null){
                            var gson : Gson = Gson()
                            water_intake_list = gson.fromJson(object1.getJSONArray("result").toString(), Array<WaterIntakeDTO>::class.java).toList()

                            if(water_intake_list!=null && date_list!=null){
                                for(i in 0..date_list.size-1){
                                    var quan : Int? = 0
                                    var pos : Int = i
                                    var day : String? = date_list.get(i).day
                                    var compare_date1 : Date = date_list.get(i).date
                                    var given_date : Int = compare_date1.date
                                    compare_date1.hours = 0
                                    compare_date1.minutes = 0
                                    compare_date1.seconds = 0
                                    var ms : Long = compare_date1.time
                                    for(j in 0..water_intake_list.size-1){
                                        var compare_date2 : Date? = UtilMethod.instance.getDateWithoutTime(water_intake_list.get(j).added_on)
                                        if(compare_date2!=null){
                                            compare_date2.hours = 0
                                            compare_date2.minutes = 0
                                            compare_date2.seconds = 0
                                            var ms1 : Long = compare_date2.time
                                            var diff_ms : Long = 0
                                            if(ms1>ms){
                                                diff_ms = ms1 - ms
                                            }
                                            else{
                                                diff_ms = ms -ms1
                                            }
                                            if(diff_ms < 1001){
                                                var s_water_amount : String = ""+water_intake_list.get(j).water_amount
                                                if(!UtilMethod.instance.isStringNullOrNot(s_water_amount)){
                                                    quan = s_water_amount.toFloat().toInt()/250
                                                    break
                                                }

                                            }
                                        }
                                    }
                                    if(pos == 0){
                                        setData(binding?.layout1, binding?.tvWaterQuantity1, binding?.tvDate1, ""+quan, day, given_date)
                                    }
                                    else if(pos == 1){
                                        setData(binding?.layout2, binding?.tvWaterQuantity2, binding?.tvDate2, ""+quan, day, given_date)
                                    }
                                    else if(pos == 2){
                                        setData(binding?.layout3, binding?.tvWaterQuantity3, binding?.tvDate3, ""+quan, day, given_date)
                                    }
                                    else if(pos == 3){
                                        setData(binding?.layout4, binding?.tvWaterQuantity4, binding?.tvDate4, ""+quan, day, given_date)
                                    }
                                    else if(pos == 4){
                                        setData(binding?.layout5, binding?.tvWaterQuantity5, binding?.tvDate5, ""+quan, day, given_date)
                                    }
                                    else if(pos == 5){
                                        setData(binding?.layout6, binding?.tvWaterQuantity6, binding?.tvDate6, ""+quan, day, given_date)
                                    }
                                    else if(pos == 6){
                                        setData(binding?.layout7, binding?.tvWaterQuantity7, binding?.tvDate7, ""+quan, day, given_date)
                                        quantity = quan!!.toFloat()
                                        setTextData(quantity, false)
                                    }


                                }
                            }

                        }

                    } else {

                    }
                }
            }
        })
    }

    fun setData(linearLayout: LinearLayout?, tvQuantity : TextView?, tvDate : TextView?, quantity : String?, day : String?, date : Int){
        var s_date : String = UtilMethod.instance.getValue(date)+" "+day
        var s_quantity : String = quantity+"/12"
        tvQuantity?.setText(s_quantity)
        tvDate?.setText(s_date)
        if(!UtilMethod.instance.isStringNullOrNot(quantity)) {
            var f1: Float = 0f
            f1 = quantity!!.toFloat() / 12
            val param = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                f1
            )
            linearLayout!!.layoutParams = param
        }
    }


    fun callAddWeightTask(amount : String?){

        var progressDialog : ProgressDialog = ProgressDialog.show(requireContext(), "", "")
        progressDialog.setContentView(R.layout.progress_loader)
        progressDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        progressDialog.setCancelable(false)

        val service = Retrofit.Builder()
            .baseUrl(""+ AppConstants.BASE_URL_NEW)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(AppService::class.java)

        service.callAddWaterIntake("Bearer "+access_token, ""+amount, "20"+s_date+" 10:10:10+00:00").enqueue(object :
            Callback<ResponseBody> {

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                progressDialog.dismiss()
                t.printStackTrace()
                dialogForCheckNetworkError(2)
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                progressDialog.dismiss()
                var code : Int = response.raw().code()
                Log.v("Code ", "==> "+code)
                if(code >199 && code < 300) {
                    var response1 : String = response.body()!!.string()
                    if(!UtilMethod.instance.isStringNullOrNot(response1)){
                        var obj : JSONObject = JSONObject(response1)
                        var success : Int = obj.getInt("success")
                        if(success == 200){
                            //getWaterIntakesTask()
                        }
                        else{

                        }

                    }
                }
            }
        })
    }



    override fun onClick(v: View?) {
        if(v == binding?.ivPlus){
            if(quantity<12){
                quantity +=1
                setTextData(quantity, true)
            }
        }
        else if(v == binding?.ivMinus){
            if(quantity>0){
                quantity -=1
                setTextData(quantity, true)
            }
        }
        else if(v == binding?.back){
            requireActivity().onBackPressed()
        }
    }
}