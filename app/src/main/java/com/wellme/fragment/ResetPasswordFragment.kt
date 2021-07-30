package com.wellme.fragment

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
import com.wellme.LeftSideMenuActivity
import com.wellme.MainActivity
import com.wellme.R
import com.wellme.databinding.FragmentResetPasswordBinding
import com.wellme.utils.AppConstants
import com.wellme.utils.AppService
import com.wellme.utils.UtilMethod
import okhttp3.ResponseBody
import okhttp3.internal.Util
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class ResetPasswordFragment : Fragment(), View.OnClickListener {
    var binding : FragmentResetPasswordBinding? = null
    var regular : Typeface? = null
    var s_otp : String? = ""
    var s_password : String? = ""
    var s_confirm_password : String? = ""
    var s_username : String? = ""
    var bundle : Bundle? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_reset_password, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        regular = ResourcesCompat.getFont(requireContext(), R.font.poppins_regular)
        binding?.text!!.setTypeface(regular)
        binding?.etOtp!!.setTypeface(regular)
        binding?.etNewPassword!!.setTypeface(regular)
        binding?.etConfirmPassword!!.setTypeface(regular)
        binding?.submit!!.setOnClickListener(this)
        binding?.cross!!.setOnClickListener(this)
        bundle = arguments
        if(bundle!=null){
            s_username = bundle?.getString("username")
        }
    }

    fun isValid() : Boolean{
        if(UtilMethod.instance.isStringNullOrNot(s_otp)){
            UtilMethod.instance.dialogOK(requireContext(), "", "Please enter OTP", requireContext().resources.getString(R.string.ok), false)
            return false
        }
        else if(UtilMethod.instance.isStringNullOrNot(s_password)){
            UtilMethod.instance.dialogOK(requireContext(), "", "Please enter password", requireContext().resources.getString(R.string.ok), false)
            return false
        }
        else if(UtilMethod.instance.isStringNullOrNot(s_confirm_password)){
            UtilMethod.instance.dialogOK(requireContext(), "", "Please enter confirm password", requireContext().resources.getString(R.string.ok), false)
            return false
        }
        else if(s_password!!.length<6){
            UtilMethod.instance.dialogOK(requireContext(), "", "Please enter at least 6 character", requireContext().resources.getString(R.string.ok), false)
            return false
        }
        else if(s_confirm_password!!.length<6){
            UtilMethod.instance.dialogOK(requireContext(), "", "Please enter at least 6 character", requireContext().resources.getString(R.string.ok), false)
            return false
        }
        else if(!s_password.equals(s_confirm_password, true)){
            UtilMethod.instance.dialogOK(requireContext(), "", "Mismatch password", requireContext().resources.getString(R.string.ok), false)
            return false
        }
        return true
    }

    override fun onClick(v: View?) {
        if(v!!.id == R.id.submit){
            s_otp = binding?.etOtp?.text.toString()
            s_password = binding?.etNewPassword?.text.toString()
            s_confirm_password = binding?.etConfirmPassword?.text.toString()
            if(isValid()){
                callResetPasswordTask()
            }
        }
        else if(v!!.id == R.id.cross){
            activity!!.supportFragmentManager.popBackStack()
        }
    }

    fun callResetPasswordTask(){

        var progressDialog : ProgressDialog = ProgressDialog.show(requireContext(), "", "")
        progressDialog.setContentView(R.layout.progress_loader)
        progressDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        progressDialog.setCancelable(false)

        val service = Retrofit.Builder()
            .baseUrl(""+ AppConstants.BASE_URL_NEW)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(AppService::class.java)

        service.callChangePassword(s_username, s_password).enqueue(object :
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
                Log.d("Code 1 ",">>>>"+code )
                if(code >199 && code < 300) {
                    var response1 : String = response.body()!!.string()

                    Log.d("response1 ",">>>>"+response1 )
                    if(!UtilMethod.instance.isStringNullOrNot(response1)){
                        var obj : JSONObject = JSONObject(response1)
                        if(obj!=null){
                            var success : Int = obj.getInt("success")
                            if(success == 200){
                                val intent : Intent = Intent(requireContext(), MainActivity::class.java)
                                startActivity(intent)
                                requireActivity().finish()
                            }
                        }

                    }
                }
                else{
                    UtilMethod.instance.dialogOK(requireContext(), "", ""+requireContext().resources.getString(R.string.user_invalid_message), ""+requireContext().resources.getString(R.string.ok), false)
                }

            }
        })
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
                DialogInterface.OnClickListener { dialog, which -> if(status==1){callResetPasswordTask()}  })
            alertDialog.setNegativeButton(context!!.resources!!.getString(R.string.network_cancel), DialogInterface.OnClickListener { dialog, which ->  })
            var alertDialog1 = alertDialog.create()
            alertDialog1.show()
        }
        catch (e: Exception){

        }
    }
}