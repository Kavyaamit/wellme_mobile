package com.wellme.fragment

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
import com.google.gson.Gson
import com.wellme.R
import com.wellme.databinding.FragmentAddBodyMeasurementBinding
import com.wellme.dto.UserDTO
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
import java.text.SimpleDateFormat
import java.util.*

class AddBodyMeasurementFragment : Fragment(), View.OnClickListener {
    var binding : FragmentAddBodyMeasurementBinding? = null
    var body_measurement_id : String? = ""
    var access_token : String? = ""
    var bundle : Bundle? = null
    var s_measurement_type_id: String? = ""
    var s_body_name: String? = ""
    var s_body_value: String? = ""
    var userDTO : UserDTO? = null
    var s_body_fat : String? = ""
    var s_date : String? = ""
    var fat : Float = 0.0f

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_body_measurement, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var regular : Typeface? = ResourcesCompat.getFont(requireContext(), R.font.poppins_regular)

        binding?.header?.setTypeface(regular)
        binding?.tvWeight?.setTypeface(regular)
        binding?.tvWeightText?.setTypeface(regular)
        binding?.tvAddWeight?.setTypeface(regular)
        binding?.tvAddWeight?.setOnClickListener(this)
        binding?.back?.setOnClickListener(this)
        binding?.ivMinus?.setOnClickListener(this)
        binding?.ivPlus?.setOnClickListener(this)
        binding?.llWeight?.setOnClickListener(this)


        binding?.back!!.setOnClickListener(this)
        access_token = UtilMethod.instance.getAccessToken(requireContext())

        bundle = arguments
        if(bundle!=null) {
            s_measurement_type_id = bundle!!.getString("id")
            s_body_name = bundle!!.getString("name")
            s_body_fat = bundle!!.getString("value")
        }

        var date : Date = Date()
        Log.d("date",">>>>"+date)
        var simpleDateFormat : SimpleDateFormat = SimpleDateFormat("yy-MM-dd HH:MM:SS")
        s_date = simpleDateFormat.format(date)


        userDTO = UtilMethod.instance.getUser(requireContext())
        Log.d("userData",">>>"+userDTO)
//        if(userDTO!=null){
//            s_body_fat = userDTO!!.body_fat
//            if(UtilMethod.instance.isStringNullOrNot(s_body_fat)){
//                s_body_fat = userDTO!!.fat
//            }
//            else{
//                fat = s_body_fat!!.toFloat()
//                if(fat == 0f){
//                    s_body_fat = userDTO!!.fat
//                }
//            }
            if(!UtilMethod.instance.isStringNullOrNot(s_body_fat)){
                fat = s_body_fat!!.toFloat()
                setValue(fat)
            }else{
                binding?.tvWeight!!.setText("00.00")
            }
if(!UtilMethod.instance.isStringNullOrNot(s_body_name)){


    if (s_body_name.equals("Body fat")){
        binding?.header?.setText(s_body_name+" (in %)")
    }else if (s_body_name.equals("Muscle mass")){
        binding?.header?.setText(s_body_name+" (kg)")
    }else{
        binding?.header?.setText(s_body_name+" (cm)")
    }

    binding?.tvWeightText?.setText("Enter "+s_body_name)
    binding?.tvAddWeight?.setText("Add "+s_body_name)
            }

//        }


    }

    fun setValue(float: Float){
        var s1 : String? = UtilMethod.instance.getFormatedAmountString(fat)
        binding?.tvWeight!!.setText(s1)
    }


    override fun onClick(v: View?) {
        if(v == binding!!.back){
            activity!!.supportFragmentManager.popBackStack()
        }else if(v==binding!!.tvAddWeight){
            callAddBodyFat()
        } else if(v == binding?.ivPlus){
            fat += 0.1f
            setValue(fat)
        }
        else if(v == binding?.ivMinus){
            if(fat>0){
                fat -= 0.1f
                setValue(fat)
            }
        }
    }
    fun callAddBodyFat(){

        Log.d("11","1>>"+s_measurement_type_id)
        Log.d("11","2>>"+fat)
        Log.d("11","3>>20"+s_date)

        var progressDialog : ProgressDialog = ProgressDialog.show(requireContext(), "", "")
        progressDialog.setContentView(R.layout.progress_loader)
        progressDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        progressDialog.setCancelable(false)

        val service = Retrofit.Builder()
            .baseUrl(""+ AppConstants.BASE_URL_NEW)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(AppService::class.java)

        service.callAddBodyFat("Bearer "+access_token,s_measurement_type_id,""+fat,"20"+s_date+".000000+00:00" ).enqueue(object :
            Callback<ResponseBody> {

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                progressDialog.dismiss()
                t.printStackTrace()
                dialogForCheckNetworkError()
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                progressDialog.dismiss()
                var code : Int = response.raw().code()
                // binding?.fullLayout!!.visibility = View.VISIBLE

//                activity!!.supportFragmentManager.popBackStack()
                if(code >199 && code < 300) {
                    var response1 : String = response.body()!!.string()
                    Log.v("Body Measurement", "==> "+response1)
                    if(!UtilMethod.instance.isStringNullOrNot(response1)){

                        Log.v("Body Measurement", "==> "+response1)

                        activity!!.supportFragmentManager.popBackStack()


                    }
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
                DialogInterface.OnClickListener { dialog, which -> callAddBodyFat() })
            alertDialog.setNegativeButton(context!!.resources!!.getString(R.string.network_cancel), DialogInterface.OnClickListener { dialog, which -> requireActivity().supportFragmentManager.popBackStack() })
            var alertDialog1 = alertDialog.create()
            alertDialog1.show()
        }
        catch (e: Exception){

        }
    }


}