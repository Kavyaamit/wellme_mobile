package com.wellme.fragment

import android.app.AlertDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.arch.core.executor.TaskExecutor
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskExecutors
import com.google.firebase.FirebaseException
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.gson.Gson
import com.wellme.R
import com.wellme.databinding.FragmentRegistrationBinding
import com.wellme.dto.RegistrationResultDTO
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
import java.util.concurrent.TimeUnit
import kotlin.properties.Delegates

class RegistrationFragment : Fragment(), View.OnClickListener, AppConstants {
    var binding : FragmentRegistrationBinding? = null
    var s_email: String? = null
    var s_mobile_number : String = ""
    var s_password : String = ""
    var s_confirm_password : String = ""
    var length1 : Int = 0
    var otpDialog : Dialog? = null
    var s_verification_id : String = ""
    var mAuth : FirebaseAuth? = null
    var timer : CountDownTimer? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_registration, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()

    }

    fun initView(){
        binding?.register?.setOnClickListener(this)
        binding?.cross?.setOnClickListener(this)
        binding?.loginLayout?.setOnClickListener(this)
        mAuth = FirebaseAuth.getInstance()
        var bold : Typeface?  = ResourcesCompat.getFont(requireContext(), R.font.poppins_bold)
        var light : Typeface?  = ResourcesCompat.getFont(requireContext(), R.font.poppins_light)
        var regular : Typeface?  = ResourcesCompat.getFont(requireContext(), R.font.poppins_regular)

        binding?.etMobileNumber?.setTypeface(regular)
        binding?.etEmail?.setTypeface(regular)
        binding?.etPassword?.setTypeface(regular)
        binding?.etConfirmPassword?.setTypeface(regular)
        binding?.register?.setTypeface(bold)
        binding?.text?.setTypeface(bold)
        binding?.tvPrivacyPolicy?.setTypeface(bold)
    }

    override fun onClick(v: View?) {
        if(v == binding?.register){
            s_email = binding?.etEmail?.text.toString()
            s_mobile_number = binding?.etMobileNumber?.text.toString()
            s_password = binding?.etPassword?.text.toString()
            s_confirm_password = binding?.etConfirmPassword?.text.toString()
            if(isValid()){
                //callRegistrationTask()
                //showOTPDialog()
                sendVerificationCode()
            }
        }
        else if(v == binding?.loginLayout){
            activity?.onBackPressed()
        }
        else if(v == binding?.cross){
            activity?.onBackPressed()
        }
    }

    fun isValid() : Boolean{
        if(UtilMethod.instance.isStringNullOrNot(s_mobile_number)){
            UtilMethod.instance.dialogOK(context, "", context?.resources?.getString(R.string.mobile_number_empty_validation), ""+context?.resources?.getString(R.string.ok), false )
            binding?.etMobileNumber?.requestFocus()
            return false
        }
        else if(isMobileInvalid(s_mobile_number)){
            UtilMethod.instance.dialogOK(context, "", context?.resources?.getString(R.string.mobile_number_invalid_validation), ""+context?.resources?.getString(R.string.ok), false )
            binding?.etMobileNumber?.requestFocus()
            return false
        }
        else if(!isValidMobile(s_mobile_number)){
            UtilMethod.instance.dialogOK(context, "", context?.resources?.getString(R.string.mobile_number_invalid_validation), ""+context?.resources?.getString(R.string.ok), false )
            binding?.etMobileNumber?.requestFocus()
            return false
        }

        else if(UtilMethod.instance.isStringNullOrNot(s_email)){
            UtilMethod.instance.dialogOK(context, "", context?.resources?.getString(R.string.email_empty_validation), ""+context?.resources?.getString(R.string.ok), false )
            binding?.etEmail?.requestFocus()
            return false
        }
        else if(!UtilMethod.instance.isEmailValid(s_email)){
            UtilMethod.instance.dialogOK(context, "", context?.resources?.getString(R.string.email_invalid_validation), ""+context?.resources?.getString(R.string.ok), false )
            binding?.etEmail?.requestFocus()
            return false
        }
        else if(UtilMethod.instance.isStringNullOrNot(s_password)){
            UtilMethod.instance.dialogOK(context, "", context?.resources?.getString(R.string.password_empty_validation), ""+context?.resources?.getString(R.string.ok), false )
            binding?.etPassword?.requestFocus()
            return false
        }
        else if(UtilMethod.instance.isStringNullOrNot(s_confirm_password)){
            UtilMethod.instance.dialogOK(context, "", context?.resources?.getString(R.string.confirm_password_empty_validation), ""+context?.resources?.getString(R.string.ok), false )
            binding?.etConfirmPassword?.requestFocus()
            return false
        }
        else if(isPasswordInvalid(s_password)){
            UtilMethod.instance.dialogOK(context, "", context?.resources?.getString(R.string.password_invalid_validation), ""+context?.resources?.getString(R.string.ok), false )
            binding?.etPassword?.requestFocus()
            return false
        }
        else if(isPasswordInvalid(s_confirm_password)){
            UtilMethod.instance.dialogOK(context, "", context?.resources?.getString(R.string.password_invalid_validation), ""+context?.resources?.getString(R.string.ok), false )
            binding?.etConfirmPassword?.requestFocus()
            return false
        }
        else if(!s_confirm_password.equals(s_password)){
            UtilMethod.instance.dialogOK(context, "", context?.resources?.getString(R.string.password_mismatch), ""+context?.resources?.getString(R.string.ok), false )
            binding?.etConfirmPassword?.requestFocus()
            return false
        }
        return true
    }


    fun isPasswordInvalid(s1 : String) : Boolean{
        var age : Int by Delegates.notNull()
        age = s1?.length
        return (age < 6 || age > 20)
    }

    fun isMobileInvalid(s1 : String) : Boolean{

        var age : Int by Delegates.notNull()
        age = s1?.length
        return (age < 10)
    }

    fun isValidMobile(phone : String ):Boolean {
    return android.util.Patterns.PHONE.matcher(phone).matches();
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
                DialogInterface.OnClickListener { dialog, which -> callRegistrationTask() })
            alertDialog.setNegativeButton(context!!.resources!!.getString(R.string.network_cancel), DialogInterface.OnClickListener { dialog, which ->  })
            var alertDialog1 = alertDialog.create()
            alertDialog1.show()
        }
        catch (e: Exception){

        }
    }

    fun showOTPDialog(){
        var regular : Typeface? = ResourcesCompat.getFont(requireContext(), R.font.poppins_regular)
        var bold : Typeface? = ResourcesCompat.getFont(requireContext(), R.font.poppins_bold)
        otpDialog = Dialog(requireContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        otpDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        otpDialog!!.setContentView(R.layout.dialog_otp_verify)
        otpDialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        otpDialog!!.show()
        var et_otp : EditText = otpDialog!!.findViewById(R.id.et_otp)
        var verify: Button = otpDialog!!.findViewById(R.id.verify)
        var text : TextView = otpDialog!!.findViewById(R.id.text)
        var tv_countdown_text : TextView = otpDialog!!.findViewById(R.id.tv_countdown_text)
        var resend_text : TextView = otpDialog!!.findViewById(R.id.resend_text)
        var resend_otp : TextView = otpDialog!!.findViewById(R.id.resend_otp)
        var ll_resend : LinearLayout = otpDialog!!.findViewById(R.id.ll_resend)
        var cross : ImageView = otpDialog!!.findViewById(R.id.cross)
        tv_countdown_text!!.setTypeface(regular)
        ll_resend!!.visibility = View.GONE
        resend_otp!!.setTypeface(bold)
        resend_text!!.setTypeface(regular)
        text!!.setTypeface(bold)
        et_otp!!.setTypeface(regular)
        verify!!.setTypeface(regular)
        cross.setOnClickListener(View.OnClickListener {view ->
            otpDialog!!.dismiss()
            if(timer!=null){
                timer!!.cancel()
            }
        })

        resend_otp.setOnClickListener(View.OnClickListener {view ->
            otpDialog!!.dismiss()
            sendVerificationCode()
        })

        verify.setOnClickListener(View.OnClickListener {view ->
            //otpDialog!!.dismiss()
            var s_otp : String? = et_otp!!.text.toString()
            if(UtilMethod.instance.isStringNullOrNot(s_otp)){
                UtilMethod.instance.dialogOK(requireContext(), "", "Please enter OTP", requireContext().resources.getString(R.string.ok), false)
            }
            else{
                if(timer!=null){
                    timer!!.cancel()
                }
                verifyVerificationCode(s_otp!!)
            }
        })

        timer = object : CountDownTimer(60000,1000){
            override fun onTick(millisUntilFinished: Long) {
                var l1 : Long = millisUntilFinished/1000
                var t1 : Int =  Integer.parseInt(""+l1)
                tv_countdown_text!!.setText("OTP will come within 00:"+getValue(t1))
            }

            override fun onFinish() {
                ll_resend!!.visibility = View.VISIBLE
                tv_countdown_text!!.visibility = View.GONE

            }

        }





        timer!!.start()


    }

    fun getValue(f1 : Int) : String{
        var s1 : String = ""
        if(f1<10){
            s1 = "0"+f1
        }
        else{
            s1 = ""+f1
        }
        return s1
    }

    fun verifyVerificationCode(otp : String){
        var credential : PhoneAuthCredential = PhoneAuthProvider.getCredential(s_verification_id, otp)
        signInWithPhoneAuthCredential(credential)

    }

    fun signInWithPhoneAuthCredential(credential : PhoneAuthCredential){
        mAuth!!.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity(), object : OnCompleteListener<AuthResult>{
                override fun onComplete(p0: Task<AuthResult>) {
                    if(p0.isSuccessful){
                        otpDialog!!.dismiss()
                        callRegistrationTask()
                    }
                    else if(p0.isCanceled){
                        UtilMethod.instance.dialogOK(requireContext(), "", "OTP does not match", requireContext().resources.getString(R.string.ok),false)
                    }
                    else if(p0.isComplete){
                        otpDialog!!.dismiss()
                        callRegistrationTask()
                    }
                }

            })
    }

    fun sendVerificationCode(){
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            "+91"+s_mobile_number,
            60,
            TimeUnit.SECONDS,
            requireActivity(),
            mCallbacks
        )
    }

    var mCallbacks : PhoneAuthProvider.OnVerificationStateChangedCallbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
        override fun onVerificationCompleted(p0: PhoneAuthCredential) {
            Log.v("Hello World", "=>"+p0.smsCode)

        }

        override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
            super.onCodeSent(p0, p1)
            Log.v("Code ", "==> "+p0)
            s_verification_id = p0.toString()
            showOTPDialog()
        }

        override fun onVerificationFailed(p0: FirebaseException) {
           Log.v("Phone Exception", "==> "+p0)
            Log.v("Phone Exception", "==> "+p0.message)
        }

    }

    fun callRegistrationTask(){

        var progressDialog : ProgressDialog = ProgressDialog.show(requireContext(), "", "")
        progressDialog.setContentView(R.layout.progress_loader)
        progressDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        progressDialog.setCancelable(false)


        val service = Retrofit.Builder()
            .baseUrl(""+ AppConstants.BASE_URL_NEW)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(AppService::class.java)

        service.callRegistration(s_mobile_number, s_email, s_password, s_confirm_password).enqueue(object :
            Callback<ResponseBody> {

            /* The HTTP call failed. This method is run on the main thread */
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                progressDialog.dismiss()
                Log.v("TAG_", "An error happened!")
                t.printStackTrace()
                dialogForCheckNetworkError()
            }

            /* The HTTP call was successful, we should still check status code and response body
             * on a production app. This method is run on the main thread */
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                progressDialog.dismiss()
                Log.v("code ", "==>"+response.code())
                if(response.code() > 199 && response.code() < 300) {
                    var response1: String = response.body()!!.string()
                    Log.v("Response Registration ", "==>"+response1)

                    if (!UtilMethod.instance.isStringNullOrNot(response1)) {
                        var obj : JSONObject  = JSONObject(response1)
                        if(obj != null){
                            var success : Int = obj.getInt("success")
                            if(success == 200){
                                var gson: Gson = Gson()
                                var data: RegistrationResultDTO =
                                    gson.fromJson(response1, RegistrationResultDTO::class.java)
                                if (data != null) {
                                    UtilMethod.instance.setAccessToken(data.access, requireContext())
                                    UtilMethod.instance.setMobileNumber(s_mobile_number, requireContext())
                                    var fragmentTransaction : FragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
                                    fragmentTransaction?.replace(R.id.container_splash, RegistrationWithPersonalDetailsFragment())
                                    fragmentTransaction?.commit()
                                }
                            }
                            else{
                                UtilMethod.instance.dialogOK(requireContext(), "", ""+obj.getString("message"), requireContext().resources.getString(R.string.ok), false)
                            }
                        }
                        else{

                        }

                    }
                }
                else{
                    
                }

            }
        })
    }


}