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
import com.wellme.databinding.FragmentFitnessPurposeBinding
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

class FitnessPurposeFragment : Fragment(), View.OnClickListener{
    var binding : FragmentFitnessPurposeBinding? = null
    var s_fitness_target : String = ""
    var access_token : String? = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_fitness_purpose, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    fun initView(){
        access_token = UtilMethod.instance.getAccessToken(context!!)
        binding?.weightLossLayout?.setOnClickListener(this)
        binding?.increasedFitnessLayout?.setOnClickListener(this)
        binding?.medicalConditionLayout?.setOnClickListener(this)
        binding?.muscleGainLayout?.setOnClickListener(this)
        binding?.weightGainLayout?.setOnClickListener(this)
        binding?.overallWellnessLayout?.setOnClickListener(this)
        binding?.done?.setOnClickListener(this)

        var light : Typeface? = ResourcesCompat.getFont(requireContext(), R.font.poppins_light)
        var bold : Typeface? = ResourcesCompat.getFont(requireContext(), R.font.poppins_bold)
        var regular : Typeface? = ResourcesCompat.getFont(requireContext(), R.font.poppins_regular)

        binding?.appName?.setTypeface(light)
        binding?.text?.setTypeface(bold)
        binding?.tvIncreasedFitness?.setTypeface(regular)
        binding?.tvOverallWellness?.setTypeface(regular)
        binding?.tvMuscleGain?.setTypeface(regular)
        binding?.tvMedicalCondition?.setTypeface(regular)
        binding?.tvWeightGain?.setTypeface(regular)
        binding?.tvWeightLoss?.setTypeface(regular)
        binding?.done?.setTypeface(bold)


    }

    override fun onClick(v: View?) {
        if(v == binding?.weightLossLayout){
            s_fitness_target = "Weight Loss"
            binding?.cbWeightLoss?.visibility = View.VISIBLE
            binding?.cbIncreasedFitness?.visibility = View.INVISIBLE
            binding?.cbMedicalCondition?.visibility = View.INVISIBLE
            binding?.cbMuscleGain?.visibility = View.INVISIBLE
            binding?.cbWeightGain?.visibility = View.INVISIBLE
            binding?.cbOverallWellness?.visibility = View.INVISIBLE

        }
        else if(v == binding?.increasedFitnessLayout){
            s_fitness_target = "Increased Fitness"
            binding?.cbWeightLoss?.visibility = View.INVISIBLE
            binding?.cbIncreasedFitness?.visibility = View.VISIBLE
            binding?.cbMedicalCondition?.visibility = View.INVISIBLE
            binding?.cbMuscleGain?.visibility = View.INVISIBLE
            binding?.cbWeightGain?.visibility = View.INVISIBLE
            binding?.cbOverallWellness?.visibility = View.INVISIBLE
        }
        else if(v == binding?.medicalConditionLayout){
            s_fitness_target = "Medical Condition"
            binding?.cbWeightLoss?.visibility = View.INVISIBLE
            binding?.cbIncreasedFitness?.visibility = View.INVISIBLE
            binding?.cbMedicalCondition?.visibility = View.VISIBLE
            binding?.cbMuscleGain?.visibility = View.INVISIBLE
            binding?.cbWeightGain?.visibility = View.INVISIBLE
            binding?.cbOverallWellness?.visibility = View.INVISIBLE
        }
        else if(v == binding?.muscleGainLayout){
            s_fitness_target = "Muscle Gain"
            binding?.cbWeightLoss?.visibility = View.INVISIBLE
            binding?.cbIncreasedFitness?.visibility = View.INVISIBLE
            binding?.cbMedicalCondition?.visibility = View.INVISIBLE
            binding?.cbMuscleGain?.visibility = View.VISIBLE
            binding?.cbWeightGain?.visibility = View.INVISIBLE
            binding?.cbOverallWellness?.visibility = View.INVISIBLE
        }
        else if(v == binding?.weightGainLayout){
            s_fitness_target = "Weight Gain"
            binding?.cbWeightLoss?.visibility = View.INVISIBLE
            binding?.cbIncreasedFitness?.visibility = View.INVISIBLE
            binding?.cbMedicalCondition?.visibility = View.INVISIBLE
            binding?.cbMuscleGain?.visibility = View.INVISIBLE
            binding?.cbWeightGain?.visibility = View.VISIBLE
            binding?.cbOverallWellness?.visibility = View.INVISIBLE
        }
        else if(v == binding?.overallWellnessLayout){
            s_fitness_target = "Overall Wellness"
            binding?.cbWeightLoss?.visibility = View.INVISIBLE
            binding?.cbIncreasedFitness?.visibility = View.INVISIBLE
            binding?.cbMedicalCondition?.visibility = View.INVISIBLE
            binding?.cbMuscleGain?.visibility = View.INVISIBLE
            binding?.cbWeightGain?.visibility = View.INVISIBLE
            binding?.cbOverallWellness?.visibility = View.VISIBLE
        }
        else if(v == binding?.done){
            if(isValid()){

                callUpdateFitnessTargetTask()
            }
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
                DialogInterface.OnClickListener { dialog, which -> callUpdateFitnessTargetTask() })
            alertDialog.setNegativeButton(context!!.resources!!.getString(R.string.network_cancel), DialogInterface.OnClickListener { dialog, which ->  })
            var alertDialog1 = alertDialog.create()
            alertDialog1.show()
        }
        catch (e: Exception){

        }
    }

    fun callUpdateFitnessTargetTask(){

        var progressDialog : ProgressDialog = ProgressDialog.show(requireContext(), "", "")
        progressDialog.setContentView(R.layout.progress_loader)
        progressDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        progressDialog.setCancelable(false)

        val service = Retrofit.Builder()
            .baseUrl(""+ AppConstants.BASE_URL_NEW)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(AppService::class.java)

        service.updateFitnessTarget("Bearer "+access_token, s_fitness_target).enqueue(object :
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

    fun isValid() : Boolean{
        if(UtilMethod.instance.isStringNullOrNot(s_fitness_target)){
            UtilMethod.instance.dialogOK(context, "", ""+context?.resources?.getString(R.string.fitness_purpose_validation), context?.resources?.getString(R.string.ok), false)
            return false
        }
        return true
    }
}