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
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.wellme.LeftSideMenuActivity
import com.wellme.R
import com.wellme.adapter.WeightGoalProgressAdapter
import com.wellme.databinding.FragmentWeightGoalProgressBinding
import com.wellme.dto.UserProfileWeightDTO
import com.wellme.dto.UserWeightDTO
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

class WeightGoalProgressFragment : Fragment(), View.OnClickListener{
    var binding : FragmentWeightGoalProgressBinding? = null
    var access_token : String? = ""
    var weight_list : List<UserWeightDTO> = ArrayList()
    var adapter1 : WeightGoalProgressAdapter? = null
    var userProfileWeight : UserProfileWeightDTO? = null
    var activity1 : Activity? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_weight_goal_progress, container, false)
        return binding?.root
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        this.activity1 = activity
    }

    override fun onResume() {
        super.onResume()
        (activity1 as LeftSideMenuActivity).enableBottomBar()
        (activity1 as LeftSideMenuActivity).setActiveSection(0)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.llAddWeight?.setOnClickListener(this)

        var linearLayoutManager = LinearLayoutManager(requireContext())
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        binding?.rvWeightGoal?.layoutManager = linearLayoutManager
        initView()
    }

    fun initView(){
        access_token = UtilMethod.instance.getAccessToken(requireContext())
        var regular : Typeface? = ResourcesCompat.getFont(requireContext(), R.font.poppins_regular)
        binding?.header?.setTypeface(regular)
        binding?.tvAddWeight?.setTypeface(regular)
        binding?.tvStartWeight?.setTypeface(regular)
        binding?.tvStartWeightAmount?.setTypeface(regular)
        binding?.tvStartWeightDate?.setTypeface(regular)
        binding?.tvTargetWeight?.setTypeface(regular)
        binding?.tvTargetWeightAmount?.setTypeface(regular)
        binding?.tvTargetWeightDate?.setTypeface(regular)
        binding?.tvWeightLogAdded?.setTypeface(regular)
        binding?.tvWeightText?.setTypeface(regular)
        binding?.back?.setOnClickListener(this)
        callUserWeightTask()
    }

    override fun onClick(v: View?) {
        if(v == binding?.llAddWeight){
            var fragmentTransaction : FragmentTransaction? = activity?.supportFragmentManager?.beginTransaction()
            fragmentTransaction?.replace(R.id.container_home, AddWeightFragment())
            fragmentTransaction?.addToBackStack(null)
            fragmentTransaction?.commit()

        }
        else if(v == binding?.back){
            activity?.onBackPressed()
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
                DialogInterface.OnClickListener { dialog, which -> callUserWeightTask() })
            alertDialog.setNegativeButton(context!!.resources!!.getString(R.string.network_cancel), DialogInterface.OnClickListener { dialog, which -> requireActivity().supportFragmentManager.popBackStack() })
            var alertDialog1 = alertDialog.create()
            alertDialog1.show()
        }
        catch (e: Exception){

        }
    }


    fun callUserWeightTask(){

        var progressDialog : ProgressDialog = ProgressDialog.show(requireContext(), "", "")
        progressDialog.setContentView(R.layout.progress_loader)
        progressDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        progressDialog.setCancelable(false)

        val service = Retrofit.Builder()
            .baseUrl(""+ AppConstants.BASE_URL_NEW)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(AppService::class.java)

        service.callUserWeight("Bearer "+access_token).enqueue(object :
            Callback<ResponseBody> {

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                progressDialog.dismiss()
                t.printStackTrace()
                dialogForCheckNetworkError()
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                progressDialog.dismiss()
                var code : Int = response.raw().code()
                binding?.fullLayout!!.visibility = View.VISIBLE
                if(code >199 && code < 300) {
                    var response1 : String = response.body()!!.string()
                    if(!UtilMethod.instance.isStringNullOrNot(response1)){
                        Log.v("Response Weight", "==> "+response1)
                        var obj : JSONObject = JSONObject(response1)
                        var success : Int = obj.getInt("success")
                        if(success == 200){
                            var gson : Gson = Gson()
                            userProfileWeight = gson.fromJson(obj.getJSONObject("user_profile").toString(), UserProfileWeightDTO::class.java)
                            weight_list = gson.fromJson(obj.getJSONArray("user_weight").toString(), Array<UserWeightDTO>::class.java).toList()

                            if(weight_list!=null){
                                adapter1 = WeightGoalProgressAdapter(requireContext(), weight_list)
                                binding?.rvWeightGoal?.adapter = adapter1
                                if(weight_list.size > 0){
                                    binding?.llWeight!!.visibility = View.VISIBLE
                                }
                                else{
                                    binding?.llWeight!!.visibility = View.GONE
                                }
                            }
                            else{
                                binding?.llWeight!!.visibility = View.GONE
                            }

                            if(userProfileWeight!=null){
                                var s_target_weight : String? = userProfileWeight!!.target_weight
                                var s_start_weight : String? = userProfileWeight!!.initial_weight
                                if(!UtilMethod.instance.isStringNullOrNot(s_target_weight)){
                                    var target_weight : Float = s_target_weight!!.toFloat()
                                    if(target_weight == 0f){
                                        binding?.llTargetWeight!!.visibility = View.GONE
                                    }
                                    else{
                                        binding?.llTargetWeight!!.visibility = View.VISIBLE
                                        binding?.tvTargetWeightAmount!!.setText(UtilMethod.instance.getFormatedAmountString(target_weight)+" kg")
                                        if(!UtilMethod.instance.isStringNullOrNot(userProfileWeight!!.target_weight_time)) {
                                            binding?.tvTargetWeightDate!!.setText(
                                                UtilMethod.instance.getDate(
                                                    userProfileWeight!!.target_weight_time
                                                )
                                            )
                                        }
                                        else{
                                            binding?.tvTargetWeightDate!!.setText("")
                                        }
                                    }
                                }
                                else{
                                    binding?.llTargetWeight!!.visibility = View.GONE
                                }

                                if(!UtilMethod.instance.isStringNullOrNot(s_start_weight)){
                                    var start_weight : Float = s_start_weight!!.toFloat()
                                    if(start_weight == 0f){
                                        binding?.llStartWeight!!.visibility = View.GONE
                                    }
                                    else{
                                        binding?.llStartWeight!!.visibility = View.VISIBLE
                                        binding?.tvStartWeightAmount!!.setText(UtilMethod.instance.getFormatedAmountString(start_weight)+" kg")
                                        binding?.tvStartWeightDate!!.setText(UtilMethod.instance.getDate(userProfileWeight!!.created_time))
                                    }
                                }
                                else{
                                    binding?.llStartWeight!!.visibility = View.GONE
                                }

                            }

                        }

                    }
                }
            }
        })
    }
}