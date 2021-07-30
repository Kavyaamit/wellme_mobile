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
import com.wellme.LeftSideMenuActivity
import com.wellme.R
import com.wellme.databinding.FragmentAddWeightBinding
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

class AddWeightFragment : Fragment(), View.OnClickListener{
    var binding : FragmentAddWeightBinding? = null
    var userDTO : UserDTO? = null
    var s_weight : String? = ""
    var weight : Float = 0.0f
    var access_token : String? = ""
    var activity : Activity? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_weight, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
        access_token = UtilMethod.instance.getAccessToken(requireContext())
        var regular : Typeface? = ResourcesCompat.getFont(requireContext(), R.font.poppins_regular)
        binding?.header?.setTypeface(regular)
        binding?.tvWeight?.setTypeface(regular)
        binding?.tvWeightText?.setTypeface(regular)
        binding?.tvAddWeight?.setTypeface(regular)
        binding?.back?.setOnClickListener(this)
        binding?.ivMinus?.setOnClickListener(this)
        binding?.ivPlus?.setOnClickListener(this)
        binding?.llWeight?.setOnClickListener(this)

        userDTO = UtilMethod.instance.getUser(requireContext())
        if(userDTO!=null){
            s_weight = userDTO!!.current_weight
            if(UtilMethod.instance.isStringNullOrNot(s_weight)){
                s_weight = userDTO!!.weight
            }
            else{
                weight = s_weight!!.toFloat()
                if(weight == 0f){
                    s_weight = userDTO!!.weight
                }
            }
            if(!UtilMethod.instance.isStringNullOrNot(s_weight)){
                weight = s_weight!!.toFloat()
                setValue(weight)
            }

        }
    }

    fun setValue(float: Float){
        var s1 : String? = UtilMethod.instance.getFormatedAmountString(weight)
        binding?.tvWeight!!.setText(s1+"kg")
    }

    override fun onClick(v: View?) {
        if(v == binding?.back){
            activity?.onBackPressed()
        }
        else if(v == binding?.ivPlus){
            weight += 0.1f
            setValue(weight)
        }
        else if(v == binding?.ivMinus){
            if(weight>0){
                weight -= 0.1f
                setValue(weight)
            }
        }
        else if(v == binding?.llWeight){
            callAddWeightTask()
        }
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
                DialogInterface.OnClickListener { dialog, which -> callAddWeightTask() })
            alertDialog.setNegativeButton(context!!.resources!!.getString(R.string.network_cancel), DialogInterface.OnClickListener { dialog, which ->  })
            var alertDialog1 = alertDialog.create()
            alertDialog1.show()
        }
        catch (e: Exception){

        }
    }


    fun callAddWeightTask(){

        var progressDialog : ProgressDialog = ProgressDialog.show(requireContext(), "", "")
        progressDialog.setContentView(R.layout.progress_loader)
        progressDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        progressDialog.setCancelable(false)

        val service = Retrofit.Builder()
            .baseUrl(""+ AppConstants.BASE_URL_NEW)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(AppService::class.java)

        service.callAddWeight("Bearer "+access_token, ""+weight).enqueue(object :
            Callback<ResponseBody> {

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                progressDialog.dismiss()
                t.printStackTrace()
                dialogForCheckNetworkError()
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
                            var userDTO : UserDTO? = UtilMethod.instance.getUser(requireContext())
                            userDTO!!.current_weight = ""+weight
                            UtilMethod.instance.setUser(requireContext(), userDTO)
                            requireActivity().onBackPressed()
                        }
                        else{

                        }

                    }
                }
            }
        })
    }
}