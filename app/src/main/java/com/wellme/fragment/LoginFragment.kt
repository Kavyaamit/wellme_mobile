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
import com.google.firebase.iid.FirebaseInstanceId
import com.google.gson.Gson
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.wellme.LeftSideMenuActivity
import com.wellme.R
import com.wellme.databinding.FragmentLoginBinding
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


class LoginFragment : Fragment(), View.OnClickListener{
    var binding : FragmentLoginBinding? = null
    var s1 : String = ""
    var s_username : String = ""
    var s_password : String = ""
    var REQUEST_OAUTH_REQUEST_CODE : Int = 10090
    var s_notification_id : String? = ""
    //val client = OkHttpClient()
    var access : String? = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    public fun initView(){
        binding?.login?.setOnClickListener(this)
        binding?.signup?.setOnClickListener(this)
        binding?.tvForgotPassword?.setOnClickListener(this)

        var bold : Typeface?  = ResourcesCompat.getFont(requireContext(), R.font.poppins_bold)
        var light : Typeface?  = ResourcesCompat.getFont(requireContext(), R.font.poppins_light)
        var regular : Typeface?  = ResourcesCompat.getFont(requireContext(), R.font.poppins_regular)

        binding?.text?.setTypeface(bold)
        binding?.tvForgotPassword?.setTypeface(regular)
        binding?.etUsername?.setTypeface(regular)
        binding?.etPassword?.setTypeface(regular)
        binding?.signup?.setTypeface(bold)
        binding?.signupText?.setTypeface(regular)
        binding?.login?.setTypeface(bold)
        s_notification_id = FirebaseInstanceId.getInstance().getToken()
        Log.v("Notification ID ", "==> "+s_notification_id)

    }

    override fun onClick(v: View?) {
        if(v == binding?.login){
            s_username = binding?.etUsername?.text.toString()
            s_password = binding?.etPassword?.text.toString()
            if(isValid()){
                /*var fragmentTransaction : FragmentTransaction? = activity?.supportFragmentManager?.beginTransaction()
                fragmentTransaction?.replace(R.id.container_splash, HomeFragment())
                fragmentTransaction?.commit()*/
                callLoginTask()

            }
        }
        else if(v == binding?.signup){
            var fragmentTransaction : FragmentTransaction? = activity?.supportFragmentManager?.beginTransaction()
            var fragment : Fragment = RegistrationFragment()
            fragmentTransaction?.replace(R.id.container_splash, fragment)
            fragmentTransaction?.addToBackStack(null)
            fragmentTransaction?.commit()
        }
        else if(v == binding?.tvForgotPassword){
            var fragmentTransaction : FragmentTransaction? = activity?.supportFragmentManager?.beginTransaction()
            var fragment : Fragment = ForgotPasswordFragment()
            fragmentTransaction?.replace(R.id.container_splash, fragment)
            fragmentTransaction?.addToBackStack(null)
            fragmentTransaction?.commit()
        }
    }


    fun isValid() : Boolean{
        if(UtilMethod.instance.isStringNullOrNot(s_username)){
            UtilMethod.instance.dialogOK(context!!, "", ""+context?.resources!!.getString(R.string.mobile_number_empty_validation), context!!.resources!!.getString(R.string.ok), false)
            return false
        }
        else if(s_username.length > 10 || s_username.length < 10){
            UtilMethod.instance.dialogOK(context!!, "", ""+context?.resources!!.getString(R.string.mobile_number_invalid_validation), context!!.resources!!.getString(R.string.ok), false)
            return false
        }
        else if(UtilMethod.instance.isStringNullOrNot(s_password)){
            UtilMethod.instance.dialogOK(context!!, "", context?.resources?.getString(R.string.password_empty_validation), context?.resources?.getString(R.string.ok), false)
            return false
        }
        return true
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
                DialogInterface.OnClickListener { dialog, which -> if(status==1){callLoginTask()} else if(status==2){callUpdateNotificationTask()}else if(status==3){callGetProfileTask()} })
            alertDialog.setNegativeButton(context!!.resources!!.getString(R.string.network_cancel), DialogInterface.OnClickListener { dialog, which ->  })
            var alertDialog1 = alertDialog.create()
            alertDialog1.show()
        }
        catch (e: Exception){

        }
    }

    fun callLoginTask(){

        var progressDialog : ProgressDialog = ProgressDialog.show(requireContext(), "", "")
        progressDialog.setContentView(R.layout.progress_loader)
        progressDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        progressDialog.setCancelable(false)

        val service = Retrofit.Builder()
            .baseUrl(""+AppConstants.BASE_URL_NEW)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(AppService::class.java)

        service.callLogin(s_username, s_password).enqueue(object :
            Callback<ResponseBody> {

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                progressDialog.dismiss()
                Log.d("error","error>>>"+t.message);
                t.printStackTrace()
                dialogForCheckNetworkError(1)
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                progressDialog.dismiss()
                var code : Int = response.raw().code()
                if(code >199 && code < 300) {
                    var response1 : String = response.body()!!.string()

                    Log.d("response1 ",">>>>"+response1 )
                    if(!UtilMethod.instance.isStringNullOrNot(response1)){
                        var obj : JSONObject = JSONObject(response1)
                        if(obj!=null){
                            access = obj.getString("access")
                            UtilMethod.instance.setAccessToken(access, requireContext())
                            callUpdateNotificationTask()
                        }
                    }
                }
                else{
                    UtilMethod.instance.dialogOK(requireContext(), "", ""+requireContext().resources.getString(R.string.user_invalid_message), ""+requireContext().resources.getString(R.string.ok), false)
                }

            }
        })
    }

    fun callUpdateNotificationTask(){

        var progressDialog : ProgressDialog = ProgressDialog.show(requireContext(), "", "")
        progressDialog.setContentView(R.layout.progress_loader)
        progressDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        progressDialog.setCancelable(false)

        val service = Retrofit.Builder()
            .baseUrl(""+AppConstants.BASE_URL_NEW)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(AppService::class.java)

        service.updateNotification("Bearer "+access, s_notification_id).enqueue(object :
            Callback<ResponseBody> {

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                progressDialog.dismiss()
                t.printStackTrace()
                dialogForCheckNetworkError(2)
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                progressDialog.dismiss()
                var code : Int = response.raw().code()
                if(code >199 && code < 300) {

                    var response1 : String = response.body()!!.string()

                    Log.d("response2 ",">>>>"+response1 )
                    callGetProfileTask()
                }
                else{
                    UtilMethod.instance.dialogOK(requireContext(), "", ""+requireContext().resources.getString(R.string.user_invalid_message), ""+requireContext().resources.getString(R.string.ok), false)
                }

            }
        })
    }

    fun callGetProfileTask(){

        var progressDialog : ProgressDialog = ProgressDialog.show(requireContext(), "", "")
        progressDialog.setContentView(R.layout.progress_loader)
        progressDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        progressDialog.setCancelable(false)

        val service = Retrofit.Builder()
            .baseUrl(""+AppConstants.BASE_URL_NEW)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(AppService::class.java)

        service.callGetProfileTask("Bearer "+access).enqueue(object :
            Callback<ResponseBody> {

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                progressDialog.dismiss()
                t.printStackTrace()
                dialogForCheckNetworkError(3)
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                progressDialog.dismiss()
                var code : Int = response.raw().code()
                Log.d("Profile Response Code",">>>>"+code )
                if(code >199 && code <300) {
                    var response1 : String = response.body()!!.string()

                    Log.d("response1 ",">>>>"+response1 )

                    if(!UtilMethod.instance.isStringNullOrNot(response1)){
                        var obj : JSONObject = JSONObject(response1)
                        var success : Int = obj.getInt("success")
                        if(success == 200){
                            Log.v("User Response ", "==> "+response1)
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
                            var fragmentTransaction : FragmentTransaction? = activity?.supportFragmentManager?.beginTransaction()
                            var fragment : Fragment = RegistrationWithPersonalDetailsFragment()
                            fragmentTransaction?.replace(R.id.container_splash, fragment)
                            fragmentTransaction?.addToBackStack(null)
                            fragmentTransaction?.commit()

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

    override fun onResume() {
        super.onResume()

        binding?.etUsername?.setText("")
        binding?.etPassword?.setText("")
    }

}

