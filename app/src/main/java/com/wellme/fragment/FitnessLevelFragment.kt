package com.wellme.fragment

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
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
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.wellme.LeftSideMenuActivity
import com.wellme.R
import com.wellme.databinding.FragmentFitnessLevelBinding
import com.wellme.dto.UserDTO
import com.wellme.utils.AppConstants
import com.wellme.utils.AppService
import com.wellme.utils.OnPermissonResult1
import com.wellme.utils.UtilMethod
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class FitnessLevelFragment : Fragment(), View.OnClickListener{
    var binding : FragmentFitnessLevelBinding? = null;
    var s_fitness_level : String = ""
    var access_token : String? = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_fitness_level, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    fun initView(){
        binding?.noExerciseLayout?.setOnClickListener(this)
        binding?.onceAWeekLayout?.setOnClickListener(this)
        binding?.twiceAWeekLayout?.setOnClickListener(this)
        binding?.sometimeLayout?.setOnClickListener(this)
        binding?.back?.setOnClickListener(this)
        binding?.next?.setOnClickListener(this)
        access_token = UtilMethod.instance.getAccessToken(requireContext())

        var bold : Typeface? = ResourcesCompat.getFont(requireContext(), R.font.poppins_bold)
        var light : Typeface? = ResourcesCompat.getFont(requireContext(), R.font.poppins_light)
        var regular : Typeface? = ResourcesCompat.getFont(requireContext(), R.font.poppins_regular)

        binding?.tvNoExercise?.setTypeface(regular)
        binding?.tvSometimes?.setTypeface(regular)
        binding?.tvTwiceWeek?.setTypeface(regular)
        binding?.onceWeek?.setTypeface(regular)
        binding?.noExerciseDetail?.setTypeface(light)
        binding?.sometimeDetail?.setTypeface(light)
        binding?.twiceWeekDetail?.setTypeface(light)
        binding?.onceWeekDetail?.setTypeface(light)
        binding?.header?.setTypeface(light)
        binding?.next?.setTypeface(bold)
    }


    override fun onClick(v: View?) {
        if(v == binding?.noExerciseLayout){
            s_fitness_level = "No Exercise"
            binding?.cbNoExercise?.visibility = View.VISIBLE
            binding?.cbOnceAWeek?.visibility = View.GONE
            binding?.cbTwiceAWeek?.visibility = View.GONE
            binding?.cbSometimes?.visibility = View.GONE
        }
        else if(v == binding?.onceAWeekLayout){
            s_fitness_level = "Once A Week"
            binding?.cbNoExercise?.visibility = View.GONE
            binding?.cbOnceAWeek?.visibility = View.VISIBLE
            binding?.cbTwiceAWeek?.visibility = View.GONE
            binding?.cbSometimes?.visibility = View.GONE
        }
        else if(v == binding?.twiceAWeekLayout){
            s_fitness_level = "Twice A Week"
            binding?.cbNoExercise?.visibility = View.GONE
            binding?.cbOnceAWeek?.visibility = View.GONE
            binding?.cbTwiceAWeek?.visibility = View.VISIBLE
            binding?.cbSometimes?.visibility = View.GONE
        }
        else if(v == binding?.sometimeLayout){
            s_fitness_level = "Sometime"
            binding?.cbNoExercise?.visibility = View.GONE
            binding?.cbOnceAWeek?.visibility = View.GONE
            binding?.cbTwiceAWeek?.visibility = View.GONE
            binding?.cbSometimes?.visibility = View.VISIBLE
        }
        else if(v == binding?.next){
            if(isValid()){
                /*var fragmentTransaction : FragmentTransaction = activity!!.supportFragmentManager.beginTransaction()
                fragmentTransaction.replace(R.id.container_splash, FitnessNextStepFragment())
                fragmentTransaction.addToBackStack(null)
                fragmentTransaction.commit()*/
                callUpdateFitnessLevelTask()
            }

        }
        else if(v == binding?.back){
            activity?.onBackPressed()
        }
    }

    fun isValid() : Boolean{
        if(UtilMethod.instance.isStringNullOrNot(s_fitness_level)){
            UtilMethod.instance.dialogOK(context, "", ""+context?.resources?.getString(R.string.fitness_level_validation), context?.resources?.getString(R.string.ok), false)
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
                DialogInterface.OnClickListener { dialog, which -> callUpdateFitnessLevelTask() })
            alertDialog.setNegativeButton(context!!.resources!!.getString(R.string.network_cancel), DialogInterface.OnClickListener { dialog, which ->  })
            var alertDialog1 = alertDialog.create()
            alertDialog1.show()
        }
        catch (e: Exception){

        }
    }

    fun callUpdateFitnessLevelTask(){

        var progressDialog : ProgressDialog = ProgressDialog.show(requireContext(), "", "")
        progressDialog.setContentView(R.layout.progress_loader)
        progressDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        progressDialog.setCancelable(false)

        val service = Retrofit.Builder()
            .baseUrl(""+ AppConstants.BASE_URL_NEW)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(AppService::class.java)

        service.updateFitnessLevel("Bearer "+access_token, s_fitness_level).enqueue(object :
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
                        var obj : JSONObject = JSONObject(response1)
                        var success : Int = obj.getInt("success")
                        if(success == 200){
                            var userDTO : UserDTO = Gson().fromJson(obj.getJSONObject("user_profile").toString(), UserDTO::class.java)
                            if(userDTO!=null){
                                UtilMethod.instance.setUser(requireContext(), userDTO)
                                var fitness_level : String? = userDTO.fitness_level
                                var fitness_purpose : String? = userDTO.fitness_purpose
                                var target_time : String? = userDTO.target_weight_time

                                if(UtilMethod.instance.isStringNullOrNot(fitness_level)){
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
                                    try {
                                        val onPermissonResult: OnPermissonResult1 = object : OnPermissonResult1 {
                                            override fun onPermissionResult(result: Boolean) {
                                                Log.v("Result ", " $result")
                                                if (result) {
                                                    UtilMethod.instance.setConnectWithfitness(requireContext(), false)
                                                    val intent : Intent = Intent(requireContext(), LeftSideMenuActivity::class.java)
                                                    startActivity(intent)
                                                    requireActivity().finish()
                                                }
                                                else{
                                                    requireActivity().finish()
                                                }

                                            }
                                        }
                                        checkLocationPermission(requireActivity(), onPermissonResult)
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
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
    fun checkLocationPermission(
        activityCompat: Activity?,
        result: OnPermissonResult1
    ) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P){
            Dexter.withActivity(activityCompat)
                .withPermissions(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACTIVITY_RECOGNITION
                ).withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                        if (report.deniedPermissionResponses.size == 0) {
                            result.onPermissionResult(true)
                        }
                        else{
                            result.onPermissionResult(false)
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        permissions: List<PermissionRequest?>?,
                        token: PermissionToken
                    ) {
                        token.continuePermissionRequest()
                    }
                }).onSameThread().check()
        }
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { // only for gingerbread and newer versions
            Dexter.withActivity(activityCompat)
                .withPermissions(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ).withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                        if (report.deniedPermissionResponses.size == 0) {
                            result.onPermissionResult(true)
                        }
                        else{
                            result.onPermissionResult(false)
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        permissions: List<PermissionRequest?>?,
                        token: PermissionToken
                    ) {
                        token.continuePermissionRequest()
                    }
                }).onSameThread().check()
        } else {
            result.onPermissionResult(true)
            //settingsrequest();
        }
    }
}