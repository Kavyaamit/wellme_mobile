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
import com.google.gson.Gson
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.wellme.LeftSideMenuActivity
import com.wellme.R
import com.wellme.databinding.FragmentFitnessResultBinding
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

class FitnessResultFragment : Fragment(), View.OnClickListener{
    var binding : FragmentFitnessResultBinding? = null
    var accesstoken : String? = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_fitness_result, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    fun initView(){
        var light : Typeface? = ResourcesCompat.getFont(requireContext(), R.font.poppins_light)
        var regular : Typeface? = ResourcesCompat.getFont(requireContext(), R.font.poppins_regular)
        var bold : Typeface? = ResourcesCompat.getFont(requireContext(), R.font.poppins_bold)

        binding?.text?.setTypeface(regular)
        binding?.letsGetStarted?.setTypeface(bold)
        binding?.tvTakeCalorie?.setTypeface(regular)
        binding?.tvBurnCalorie?.setTypeface(regular)
        binding?.tvTakeStep?.setTypeface(regular)
        binding?.back?.setOnClickListener(this)
        binding?.letsGetStarted?.setOnClickListener(this)
        accesstoken = UtilMethod.instance.getAccessToken(requireContext())
        callGetProfileTask(accesstoken)
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
                DialogInterface.OnClickListener { dialog, which -> callGetProfileTask(accesstoken) })
            alertDialog.setNegativeButton(context!!.resources!!.getString(R.string.network_cancel), DialogInterface.OnClickListener { dialog, which ->  })
            var alertDialog1 = alertDialog.create()
            alertDialog1.show()
        }
        catch (e: Exception){

        }
    }


    fun callGetProfileTask(accesstaken : String?){

        var progressDialog : ProgressDialog = ProgressDialog.show(requireContext(), "", "")
        progressDialog.setContentView(R.layout.progress_loader)
        progressDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        progressDialog.setCancelable(false)

        val service = Retrofit.Builder()
            .baseUrl(""+AppConstants.BASE_URL_NEW)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(AppService::class.java)

        service.callGetProfileTask("Bearer "+accesstaken).enqueue(object :
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
                            Log.v("Response ", "==> "+response1)
                            var userDTO : UserDTO = Gson().fromJson(obj.getJSONObject("user_profile").toString(), UserDTO::class.java)
                            if(userDTO!=null){
                                UtilMethod.instance.setUser(requireContext(), userDTO)

                               /* var current_weight:String = userDTO.current_weight.toString()
                                var target_weight:String = userDTO.target_weight.toString()

                                if (current_weight>target_weight){
                                    binding?.text!!.setText(requireContext().resources.getString(R.string.to_lose_certain_amount_text))

                                }else if (current_weight<target_weight){

                                    binding?.text!!.setText(requireContext().resources.getString(R.string.to_gain_certain_amount_text))
                                }*/
                            }
                        }
                        else{


                        }

                    }
                }

            }
        })
    }


    override fun onClick(v: View?) {
        if(v == binding?.back){
            activity?.onBackPressed()
        }
        else if(v == binding?.letsGetStarted){
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