package com.wellme.fragment

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
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.wellme.LeftSideMenuActivity
import com.wellme.MainActivity
import com.wellme.R
import com.wellme.adapter.GoalForUpdateAdapter
import com.wellme.databinding.FragmentUpdateGoalBinding
import com.wellme.dto.GoalDTO
import com.wellme.dto.UserDTO
import com.wellme.utils.AppConstants
import com.wellme.utils.AppService
import com.wellme.utils.OnItemClickListener
import com.wellme.utils.UtilMethod
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class UpdateGoalFragment : Fragment(), View.OnClickListener{
    var binding : FragmentUpdateGoalBinding? = null
    var linearLayoutManager : LinearLayoutManager? = null
    var userDTO : UserDTO? = null
    var goal_id : String? = null
    var accessToken : String? = null
    var goalForUpdateAdapter : GoalForUpdateAdapter? = null
    var goal_list : List<GoalDTO> = ArrayList()
    var activity : Activity? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_update_goal, container, false)
        return binding?.root
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        this.activity = activity
    }

    override fun onResume() {
        super.onResume()
        (activity as LeftSideMenuActivity).enableBottomBar()
        (activity as LeftSideMenuActivity).setActiveSection(4)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    fun initView(){
        userDTO = UtilMethod.instance.getUser(requireContext())
        accessToken = UtilMethod.instance.getAccessToken(requireContext())
        var regular : Typeface? = ResourcesCompat.getFont(requireContext(), R.font.poppins_regular)
        binding?.tvGoal?.setTypeface(regular)
        binding?.ivSubmit?.setOnClickListener(this)
        binding?.ivBack?.setOnClickListener(this)

        linearLayoutManager = LinearLayoutManager(requireContext())
        linearLayoutManager?.orientation = LinearLayoutManager.VERTICAL
        binding?.rvGoal?.layoutManager = linearLayoutManager
        if(userDTO!=null){
            goal_id = userDTO?.goal_id
        }
        callGoalListTask()

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
                DialogInterface.OnClickListener { dialog, which -> if(status==1){callGoalListTask()}else if(status==2){ callUpdateGoalTask()} })
            alertDialog.setNegativeButton(context!!.resources!!.getString(R.string.network_cancel), DialogInterface.OnClickListener { dialog, which ->  })
            var alertDialog1 = alertDialog.create()
            alertDialog1.show()
        }
        catch (e: Exception){

        }
    }

    fun callGoalListTask(){

        var progressDialog : ProgressDialog = ProgressDialog.show(requireContext(), "", "")
        progressDialog.setContentView(R.layout.progress_loader)
        progressDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        progressDialog.setCancelable(false)

        val service = Retrofit.Builder()
            .baseUrl(""+ AppConstants.BASE_URL_NEW)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(AppService::class.java)


        service.getDataList().enqueue(object : Callback<ResponseBody> {

            /* The HTTP call failed. This method is run on the main thread */
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.d("TAG_", "An error happened!")
                progressDialog.dismiss()
                t.printStackTrace()
                dialogForCheckNetworkError(1)
            }

            /* The HTTP call was successful, we should still check status code and response body
             * on a production app. This method is run on the main thread */
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                /* This will print the response of the network call to the Logcat */
                progressDialog.dismiss()
                Log.v("Response", "==>"+response.code())
                var response1 : String = response.body()!!.string()
                if(!UtilMethod.instance.isStringNullOrNot(response1)){
                    var object1 : JSONObject = JSONObject(response1)
                    if(object1!=null){
                        var gson : Gson = Gson()
                        goal_list = gson.fromJson(object1.getJSONArray("goal").toString(), Array<GoalDTO>::class.java).toList()
                        goalForUpdateAdapter = GoalForUpdateAdapter(requireContext(), goal_list, onItemClickCallback, goal_id)
                        binding?.rvGoal?.adapter = goalForUpdateAdapter
                    }
                }
                else{

                }
            }
        })
    }

    fun callUpdateGoalTask(){

        var progressDialog : ProgressDialog = ProgressDialog.show(requireContext(), "", "")
        progressDialog.setContentView(R.layout.progress_loader)
        progressDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        progressDialog.setCancelable(false)

        val service = Retrofit.Builder()
            .baseUrl(""+AppConstants.BASE_URL_NEW)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(AppService::class.java)

        service.updateGoal("Bearer "+accessToken, goal_id).enqueue(object :
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
                    if(!UtilMethod.instance.isStringNullOrNot(response1)){
                        var obj : JSONObject = JSONObject(response1)
                        var success : Int = obj.getInt("success")
                        if(success == 200){
                            Log.v("REsponse Data", "==> "+response1)
                            var userDTO : UserDTO = Gson().fromJson(obj.getJSONObject("user_profile").toString(), UserDTO::class.java)
                            if(userDTO!=null){
                                UtilMethod.instance.setUser(requireContext(), userDTO)
                                requireActivity().onBackPressed()

                            }
                        }
                        else{

                        }

                    }
                }
                else if(code == 401){
                    UtilMethod.instance.setAccessToken("", requireContext())
                    UtilMethod.instance.setUser(requireContext(), null)
                    val intent : Intent = Intent(requireContext(), MainActivity::class.java)
                    startActivity(intent)
                    requireActivity().finish()
                }





            }
        })
    }

    private val onItemClickCallback : OnItemClickListener.OnItemClickCallback = object : OnItemClickListener.OnItemClickCallback{

        override fun onItemClicked(view: View?, position: Int) {
            goal_id = goal_list.get(position).id
            if(goalForUpdateAdapter!=null){
                goalForUpdateAdapter?.setData(goal_id)
                goalForUpdateAdapter?.notifyDataSetChanged()
            }
        }

    }

    override fun onClick(v: View?) {
        if(v == binding?.ivSubmit){
            callUpdateGoalTask()
        }
        else if(v == binding?.ivBack){
            requireActivity().onBackPressed()
        }
    }
}