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
import com.wellme.R
import com.wellme.databinding.FragmentForgotPasswordBinding
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

class ForgotPasswordFragment : Fragment(), View.OnClickListener{
    var binding : FragmentForgotPasswordBinding? = null
    var regular : Typeface? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_forgot_password, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
            regular = ResourcesCompat.getFont(requireContext(), R.font.poppins_regular)
            binding?.etUsername!!.setTypeface(regular)
            binding?.text!!.setTypeface(regular)
            binding?.submit!!.setTypeface(regular)
            binding?.submit!!.setOnClickListener(this)
            binding?.cross!!.setOnClickListener(this)
    }

    fun isValid() : Boolean{
        var s_username : String? = binding?.etUsername!!.text.toString()
        if(UtilMethod.instance.isStringNullOrNot(s_username)){
           UtilMethod.instance.dialogOK(requireContext(), "", "Please enter mobile number", requireContext().resources.getString(R.string.ok), false)
           return false
        }
        else if(s_username!!.length>10 || s_username!!.length<10){
            UtilMethod.instance.dialogOK(requireContext(), "", "Please enter valid mobile number", requireContext().resources.getString(R.string.ok), false)
            return false
        }
        return true
    }

    override fun onClick(v: View?) {
        if(v!!.id == R.id.submit){
            if(isValid()) {
                callForgotPasswordTask()
            }
        }
        else if(v!!.id == R.id.cross){
            activity!!.supportFragmentManager.popBackStack()
        }
    }

    fun callForgotPasswordTask(){

        var progressDialog : ProgressDialog = ProgressDialog.show(requireContext(), "", "")
        progressDialog.setContentView(R.layout.progress_loader)
        progressDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        progressDialog.setCancelable(false)

        val service = Retrofit.Builder()
            .baseUrl(""+ AppConstants.BASE_URL_NEW)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(AppService::class.java)

        service.callUserExist(binding?.etUsername?.text.toString()).enqueue(object :
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
                Log.d("response1 Code ",">>>>"+code )
                if(code >199 && code < 300) {
                    var response1 : String = response.body()!!.string()
                    if(!UtilMethod.instance.isStringNullOrNot(response1)){
                        var obj : JSONObject = JSONObject(response1)
                        if(obj!=null){
                            var code1 : Int = obj.getInt("success")
                            if(code1 == 200){
                                var fragmentTransaction: FragmentTransaction? = activity?.supportFragmentManager?.beginTransaction()
                                var fragment: Fragment = ResetPasswordFragment()
                                var bundle : Bundle = Bundle()
                                bundle.putString("username", binding?.etUsername?.text.toString())
                                fragment.arguments = bundle
                                fragmentTransaction?.replace(R.id.container_splash, fragment)
                                fragmentTransaction?.addToBackStack(null)
                                fragmentTransaction?.commit()
                                }
                                else{
                                    UtilMethod.instance.dialogOK(requireContext(), "", obj.getString("message"), requireContext().resources.getString(R.string.ok), false)
                                }
                        }

                    }
                    Log.d("response1 ",">>>>"+response1 )

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
                DialogInterface.OnClickListener { dialog, which -> if(status==1){callForgotPasswordTask()}  })
            alertDialog.setNegativeButton(context!!.resources!!.getString(R.string.network_cancel), DialogInterface.OnClickListener { dialog, which ->  })
            var alertDialog1 = alertDialog.create()
            alertDialog1.show()
        }
        catch (e: Exception){

        }
    }
}