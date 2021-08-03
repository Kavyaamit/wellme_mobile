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
import com.wellme.LeftSideMenuActivity
import com.wellme.R
import com.wellme.databinding.FragmentAddWorkoutBinding
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

class AddWorkoutFragment : Fragment(), View.OnClickListener{
    var binding : FragmentAddWorkoutBinding? = null
    var bun : Bundle? = null
    var s_time : String? = ""
    var exercise_id : String? = ""
    var exercise_name : String? = ""
    var s_met : String? = ""
    var cb_id : String? = ""
    var s_access_token : String? = ""
    var userDTO : UserDTO? = null
    var s_current_weight : String? = ""
    var s_cal : String? = ""
    var time : Int = 0
    var activity : Activity? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_workout, container, false)
        return binding?.root
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        this.activity = activity
    }

    override fun onResume() {
        super.onResume()
        (activity as LeftSideMenuActivity).enableBottomBar()
        (activity as LeftSideMenuActivity).setActiveSection(0)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    fun initView(){
        s_access_token = UtilMethod.instance.getAccessToken(requireContext())
        userDTO = UtilMethod.instance.getUser(requireContext())
        if(userDTO!=null){
            s_current_weight = userDTO!!.current_weight
        }
        var regular : Typeface? = ResourcesCompat.getFont(requireContext(), R.font.poppins_regular)
        binding?.etCalorieBurn?.setTypeface(regular)
        binding?.header?.setTypeface(regular)
        binding?.etTime?.setTypeface(regular)
        binding?.etCalorieBurn?.setTypeface(regular)
        binding?.tvOr?.setTypeface(regular)
        binding?.tvTimeUnit?.setTypeface(regular)
        binding?.tvCalorieBurnUnit?.setTypeface(regular)
        binding?.tvAddWorkout?.setTypeface(regular)
        binding?.back?.setOnClickListener(this)
        binding?.tvAddWorkout!!.setOnClickListener(this)
        bun = arguments
        if(bun!=null) {
            exercise_id = bun!!.getString("exercise_id")
            exercise_name = bun!!.getString("exercise_name")
            s_met = bun!!.getString("met")
            cb_id = bun!!.getString("id")
            binding?.header?.setText(exercise_name)

        }
    }

    fun callAPI(){
        s_time = binding?.etTime!!.text.toString()
        s_cal = binding?.etCalorieBurn!!.text.toString()
        if(UtilMethod.instance.isStringNullOrNot(s_cal) && UtilMethod.instance.isStringNullOrNot(s_time)){
            UtilMethod.instance.dialogOK(requireContext(), "", "Please enter calories or time", "Ok", false)
        }
        else if(!UtilMethod.instance.isStringNullOrNot(s_cal)){
            callCreateProfileTask(""+s_cal)
        }
        else{
            redirectAPI()
        }
    }

    override fun onClick(v: View?) {
        if(v == binding?.back){
            activity?.onBackPressed()
        }
        else if(v == binding?.tvAddWorkout){
            callAPI()
        }
    }


    fun redirectAPI(){
        var curr_wei : Float = 0.0f
        var kcal : Float = 0.0f
        var met : Float = 0.0f

        if(!UtilMethod.instance.isStringNullOrNot(s_time)){
            time = s_time!!.toInt()
        }
        if(!UtilMethod.instance.isStringNullOrNot(s_met)){
            met = s_time!!.toFloat()
        }
        if(!UtilMethod.instance.isStringNullOrNot(s_current_weight)){
            curr_wei = s_current_weight!!.toFloat()
        }

        kcal = (met * 3.5f * curr_wei * time)/200

        callCreateProfileTask(""+kcal)

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
                DialogInterface.OnClickListener { dialog, which -> callAPI() })
            alertDialog.setNegativeButton(context!!.resources!!.getString(R.string.network_cancel), DialogInterface.OnClickListener { dialog, which ->  })
            var alertDialog1 = alertDialog.create()
            alertDialog1.show()
        }
        catch (e: Exception){

        }
    }

    fun callCreateProfileTask(cal : String){

        var progressDialog : ProgressDialog = ProgressDialog.show(requireContext(), "", "")
        progressDialog.setContentView(R.layout.progress_loader)
        progressDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        progressDialog.setCancelable(false)

        val service = Retrofit.Builder()
            .baseUrl(""+ AppConstants.BASE_URL_NEW)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(AppService::class.java)


        service.callAddWorkout("Bearer "+s_access_token, ""+exercise_id, ""+cb_id, ""+time, cal, cal, "0", "0", "1", ""+exercise_name).enqueue(object :
            Callback<ResponseBody> {

            /* The HTTP call failed. This method is run on the main thread */
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.d("TAG_", "An error happened!")
                progressDialog.dismiss()
                t.printStackTrace()
                dialogForCheckNetworkError()
            }

            /* The HTTP call was successful, we should still check status code and response body
             * on a production app. This method is run on the main thread */
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                /* This will print the response of the network call to the Logcat */
                progressDialog.dismiss()
                var code : Int = response.raw().code()
                Log.v("REquest ", "==> "+response.raw().body())
                Log.v("Response Code ", " ==> "+code+" "+response.raw())
                if(code >199 && code < 300) {
                    var response1 : String = response.body()!!.string()
                    if(!UtilMethod.instance.isStringNullOrNot(response1)){
                        Log.v("REsponse == > ", "=="+response1)
                        var obj : JSONObject = JSONObject(response1)
                        var success : Int = obj.getInt("success")
                        if(success == 200){
                            requireActivity().onBackPressed()
                        }
                        else{

                        }

                    }
                }

            }
        })
    }

}