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
import android.os.CountDownTimer
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseException
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.wellme.MainActivity
import com.wellme.R
import com.wellme.databinding.FragmentChangePasswordBinding
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
import java.util.concurrent.TimeUnit

class ChangePasswordFragment : Fragment(), View.OnClickListener{
    var binding : FragmentChangePasswordBinding? = null
    var s_user : String? = ""
    var regular : Typeface? = null
    var bold : Typeface? = null
    var s_otp : String? = ""
    var s_password : String? = ""
    var s_confirm_password : String? = ""
    var s_verification_id : String = ""
    var user : UserDTO? = null
    var timer : CountDownTimer? = null
    var mAuth : FirebaseAuth? = null
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_change_password, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mAuth = FirebaseAuth.getInstance();
        user = UtilMethod.instance.getUser(requireContext())
        if(user!=null){
            s_user = user!!.phone
            sendVerificationCode()
        }
        regular = ResourcesCompat.getFont(requireContext(), R.font.poppins_regular)
        bold = ResourcesCompat.getFont(requireContext(), R.font.poppins_bold)
        binding?.text!!.setTypeface(regular)
        binding?.etOtp!!.setTypeface(regular)
        binding?.etNewPassword!!.setTypeface(regular)
        binding?.etConfirmPassword!!.setTypeface(regular)
        binding?.resendText!!.setTypeface(regular)
        binding?.tvCountdownText!!.setTypeface(regular)
        binding?.resendOtp!!.setTypeface(bold)
        binding?.submit!!.setOnClickListener(this)
        binding?.cross!!.setOnClickListener(this)
        binding?.resendOtp!!.setOnClickListener(this)
    }

    fun setTimer1(){
        binding!!.llResend!!.visibility = View.GONE
        timer = object : CountDownTimer(60000,1000){
            override fun onTick(millisUntilFinished: Long) {
                var l1 : Long = millisUntilFinished/1000
                var t1 : Int =  Integer.parseInt(""+l1)
                binding!!.tvCountdownText!!.setText("OTP will come within 00:"+getValue(t1))
            }

            override fun onFinish() {
                binding!!.llResend!!.visibility = View.VISIBLE
                binding!!.tvCountdownText!!.visibility = View.GONE

            }

        }
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

    fun sendVerificationCode(){
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            "+91"+s_user,
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
            binding!!.tvCountdownText.visibility = View.VISIBLE
            setTimer1()

        }

        override fun onVerificationFailed(p0: FirebaseException) {
            Log.v("Phone Exception", "==> "+p0)
            Log.v("Phone Exception", "==> "+p0.message)
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
                if(timer!=null){
                    timer!!.cancel()
                }
                verifyVerificationCode(s_otp!!)
            }
        }
        else if(v!!.id == R.id.cross){
            activity!!.supportFragmentManager.popBackStack()
        }
        else if(v!!.id == R.id.resend_otp){
            sendVerificationCode()
        }
    }

    fun verifyVerificationCode(otp : String){
        var credential : PhoneAuthCredential = PhoneAuthProvider.getCredential(s_verification_id, otp)
        signInWithPhoneAuthCredential(credential)

    }

    fun signInWithPhoneAuthCredential(credential : PhoneAuthCredential){
        mAuth!!.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity(), object : OnCompleteListener<AuthResult> {
                override fun onComplete(p0: Task<AuthResult>) {
                    if(p0.isSuccessful){
                        callResetPasswordTask()
                    }
                    else if(p0.isCanceled){
                        UtilMethod.instance.dialogOK(requireContext(), "", "OTP does not match", requireContext().resources.getString(R.string.ok),false)
                    }
                    else if(p0.isComplete){
                        callResetPasswordTask()
                    }
                }

            })
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

        service.callChangePassword(s_user, s_password).enqueue(object :
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