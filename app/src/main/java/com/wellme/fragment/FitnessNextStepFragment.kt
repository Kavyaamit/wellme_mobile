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
import androidx.fragment.app.FragmentTransaction
import com.google.gson.Gson
import com.wellme.R
import com.wellme.databinding.FragmentNextStepBinding
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

class FitnessNextStepFragment : Fragment(), View.OnClickListener{
    var binding : FragmentNextStepBinding? = null
    var s_next_step : String = ""
    var access_token : String? = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_next_step, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    fun initView(){
        binding?.trackCalorieLayout?.setOnClickListener(this)
        binding?.coachedLayout?.setOnClickListener(this)
        binding?.dietLayout?.setOnClickListener(this)
        binding?.back?.setOnClickListener(this)
        binding?.next?.setOnClickListener(this)
        access_token = UtilMethod.instance.getAccessToken(requireContext())

        var regular : Typeface? = ResourcesCompat.getFont(requireContext(), R.font.poppins_regular)
        var bold : Typeface? = ResourcesCompat.getFont(requireContext(), R.font.poppins_bold)

        binding?.tvTrackCalorie?.setTypeface(regular)
        binding?.tvCoached?.setTypeface(regular)
        binding?.tvDiet?.setTypeface(regular)
        binding?.next?.setTypeface(bold)
        binding?.text?.setTypeface(regular)

    }

    override fun onClick(v: View?) {
        if(v == binding?.coachedLayout){
            s_next_step = "Get Coached"
            binding?.cbTrackCalorie?.visibility = View.INVISIBLE
            binding?.cbGetDiet?.visibility = View.INVISIBLE
            binding?.cbGetCoached?.visibility = View.VISIBLE
        }
        else if(v == binding?.dietLayout){
            s_next_step = "Get Diet"
            binding?.cbTrackCalorie?.visibility = View.INVISIBLE
            binding?.cbGetDiet?.visibility = View.VISIBLE
            binding?.cbGetCoached?.visibility = View.INVISIBLE
        }
        else if(v == binding?.trackCalorieLayout){
            s_next_step = "Track Calorie"
            binding?.cbTrackCalorie?.visibility = View.VISIBLE
            binding?.cbGetDiet?.visibility = View.INVISIBLE
            binding?.cbGetCoached?.visibility = View.INVISIBLE
        }
        else if(v == binding?.back){
            activity?.onBackPressed()
        }
        else if(v == binding?.next){
            if(isValid()){
                callUpdateFitnessPurposeTask()
            }
        }
    }

    fun isValid() : Boolean{
        if(UtilMethod.instance.isStringNullOrNot(s_next_step)){
            UtilMethod.instance.dialogOK(context!!, "", "Please select any one step", context!!.resources!!.getString(R.string.ok), false)
            return false
        }
        return true
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
                DialogInterface.OnClickListener { dialog, which -> callUpdateFitnessPurposeTask() })
            alertDialog.setNegativeButton(context!!.resources!!.getString(R.string.network_cancel), DialogInterface.OnClickListener { dialog, which ->  })
            var alertDialog1 = alertDialog.create()
            alertDialog1.show()
        }
        catch (e: Exception){

        }
    }


    fun callUpdateFitnessPurposeTask(){

        var progressDialog : ProgressDialog = ProgressDialog.show(requireContext(), "", "")
        progressDialog.setContentView(R.layout.progress_loader)
        progressDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        progressDialog.setCancelable(false)

        val service = Retrofit.Builder()
            .baseUrl(""+ AppConstants.BASE_URL_NEW)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(AppService::class.java)

        service.updateFitnessPurpose("Bearer "+access_token, s_next_step).enqueue(object :
            Callback<ResponseBody> {

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                progressDialog.dismiss()
                t.printStackTrace()
                dialogForCheckNetworkError()
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                progressDialog.dismiss()
                var code : Int = response.raw().code()
                if(code >199 && code < 300) {
                    var response1 : String = response.body()!!.string()
                    if(!UtilMethod.instance.isStringNullOrNot(response1)){
                        Log.v("REponse", "==> "+response1)
                        var obj : JSONObject = JSONObject(response1)
                        var success : Int = obj.getInt("success")
                        if(success == 200){
                            var userDTO : UserDTO = Gson().fromJson(obj.getJSONObject("user_profile").toString(), UserDTO::class.java)
                            if(userDTO!=null){
                                UtilMethod.instance.setUser(requireContext(), userDTO)
                                var fitness_level : String? = userDTO.fitness_level
                                var fitness_purpose : String? = userDTO.fitness_purpose
                                var target_time : String? = userDTO.target_weight_time
                                var fitness_target : String? = userDTO.fitness_target

                                if(UtilMethod.instance.isStringNullOrNot(fitness_target)){
                                    var fragmentTransaction : FragmentTransaction? = activity!!.supportFragmentManager.beginTransaction()
                                    var fragment : Fragment = FitnessPurposeFragment()
                                    fragmentTransaction?.replace(R.id.container_splash, fragment)
                                    fragmentTransaction?.commit()
                                }
                                else if(UtilMethod.instance.isStringNullOrNot(fitness_level)){
                                    var fragmentTransaction : FragmentTransaction? = activity!!.supportFragmentManager.beginTransaction()
                                    var fragment : Fragment = FitnessLevelFragment()
                                    fragmentTransaction?.replace(R.id.container_splash, fragment)
                                    fragmentTransaction?.commit()
                                }
                                else if(UtilMethod.instance.isStringNullOrNot(fitness_purpose)){
                                    var fragmentTransaction : FragmentTransaction? = activity!!.supportFragmentManager.beginTransaction()
                                    var fragment : Fragment = FitnessNextStepFragment()
                                    fragmentTransaction?.replace(R.id.container_splash, fragment)
                                    fragmentTransaction?.commit()
                                }
                                else if(UtilMethod.instance.isStringNullOrNot(target_time)){
                                    var fragmentTransaction : FragmentTransaction? = activity!!.supportFragmentManager.beginTransaction()
                                    var fragment : Fragment = WeightSummaryFragment()
                                    fragmentTransaction?.replace(R.id.container_splash, fragment)
                                    fragmentTransaction?.commit()
                                }
                                else{
                                    var fragmentTransaction : FragmentTransaction? = activity!!.supportFragmentManager.beginTransaction()
                                    var fragment : Fragment = FitnessResultFragment()
                                    fragmentTransaction?.replace(R.id.container_splash, fragment)
                                    fragmentTransaction?.commit()
                                }

                            }
                        }
                        else{

                        }

                    }
                }

            }
        })
    }
 }