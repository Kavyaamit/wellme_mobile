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
import com.google.gson.Gson
import com.wellme.LeftSideMenuActivity
import com.wellme.R
import com.wellme.databinding.FragmentAboutusandprivacyBinding
import com.wellme.dto.AboutUsAndPrivacyDTO
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

class AboutusAndPrivacyFragment : Fragment(), View.OnClickListener{
    var binding : FragmentAboutusandprivacyBinding? = null
    var s_title : String = ""
    var s_description : String = ""
    var regular : Typeface? = null
    var s_access_token : String? = ""
    var s_type : String? = ""
    var bundle : Bundle? = null
    var activity : Activity? = null
    var list : List<AboutUsAndPrivacyDTO> = ArrayList()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_aboutusandprivacy, container, false)
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
        (activity as LeftSideMenuActivity).disableBottomBar()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        (activity as LeftSideMenuActivity).enableBottomBar()
    }

    fun initView(){
        regular = ResourcesCompat.getFont(requireContext(), R.font.poppins_regular)

        binding?.header?.setTypeface(regular)
        binding?.tvAboutUs?.setTypeface(regular)
        binding?.back?.setOnClickListener(this)
        s_access_token = UtilMethod.instance.getAccessToken(requireContext())

        bundle = arguments
        if(bundle!=null){

            s_type = bundle?.getString("type")
            Log.d("s_type",">>>"+s_type)
            callAboutusAndPrivacy()
        }

        if (s_type.equals("About_us")){

            binding?.header?.setText("About Us")

        }else{

            binding?.header?.setText("Privacy Policy")
        }
    }

    override fun onClick(v: View?) {
        if(v == binding?.back){
            requireActivity().onBackPressed()
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
                DialogInterface.OnClickListener { dialog, which -> callAboutusAndPrivacy() })
            alertDialog.setNegativeButton(context!!.resources!!.getString(R.string.network_cancel), DialogInterface.OnClickListener { dialog, which -> requireActivity().supportFragmentManager.popBackStack() })
            var alertDialog1 = alertDialog.create()
            alertDialog1.show()
        }
        catch (e: Exception){

        }
    }

    fun callAboutusAndPrivacy(){

        var progressDialog : ProgressDialog = ProgressDialog.show(requireContext(), "", "")
        progressDialog.setContentView(R.layout.progress_loader)
        progressDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        progressDialog.setCancelable(false)

        val service = Retrofit.Builder()
            .baseUrl(""+ AppConstants.BASE_URL_NEW)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(AppService::class.java)

        service.callAboutUsAndPrivacy("Bearer "+s_access_token,s_type).enqueue(object :
            Callback<ResponseBody> {

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                progressDialog.dismiss()
                t.printStackTrace()
                dialogForCheckNetworkError()
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                progressDialog.dismiss()
                var code : Int = response.raw().code()
                Log.d("code",">>>>"+code)
                if(code >199 && code < 300) {
                    var response1 : String = response.body()!!.string()
                    if(!UtilMethod.instance.isStringNullOrNot(response1)){

                        Log.d("response1",">>>>"+response1)
                        var obj : JSONObject = JSONObject(response1)
                        var success : Int = obj.getInt("success")

                        if (success==200){
                            var text :String = ""
                            list = Gson().fromJson(obj.getJSONArray("result").toString(), Array<AboutUsAndPrivacyDTO>::class.java).toList()

                            if (list!=null){
                                if (list.size>0){

                                    if (s_type.equals("About_us")){

                                        text = list.get(0).about_us

                                    }else{

                                        text = list.get(0).privacy_policy
                                    }
                                }
                            }

                            if (!UtilMethod.instance.isStringNullOrNot(text)){

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                    binding?.tvAboutUs?.setText(Html.fromHtml(text, Html.FROM_HTML_MODE_COMPACT))
                                } else {
                                    binding?.tvAboutUs?.setText(Html.fromHtml(text))
                                }
                            }

                        }
                        /*Toast.makeText(requireContext(), "Feedback send successfully!!", Toast.LENGTH_LONG).show()
                        requireActivity().onBackPressed()*/
                    }
                }
                else{
                    UtilMethod.instance.dialogOK(requireContext(), "", ""+requireContext().resources.getString(R.string.user_invalid_message), ""+requireContext().resources.getString(R.string.ok), false)
                }

            }
        })
    }

    fun isValid() : Boolean{
        if(UtilMethod.instance.isStringNullOrNot(s_title)){
            UtilMethod.instance.dialogOK(requireContext(), "", requireContext().resources.getString(R.string.title_validation), requireContext().resources.getString(R.string.ok), false)
            return false
        }
        else if(UtilMethod.instance.isStringNullOrNot(s_description)){
            UtilMethod.instance.dialogOK(requireContext(), "", requireContext().resources.getString(R.string.description_validation), requireContext().resources.getString(R.string.ok), false)
            return false
        }
        return true
    }
}