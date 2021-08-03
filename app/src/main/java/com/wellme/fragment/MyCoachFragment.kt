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
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.wellme.LeftSideMenuActivity
import com.wellme.R
import com.wellme.adapter.MyCoachAdapter
import com.wellme.adapter.MyCoachExpertiseAdapter
import com.wellme.databinding.FragmentMyCoachBinding
import com.wellme.dto.MyCoachDTO
import com.wellme.dto.UserDTO
import com.wellme.utils.*
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class MyCoachFragment : Fragment(), View.OnClickListener{
    var binding : FragmentMyCoachBinding? = null
    var coach_list : List<MyCoachDTO> = ArrayList()
    var linearLayoutManager : LinearLayoutManager? = null
    var access_token : String = ""
    var activity1 : Activity? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_my_coach, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    fun initView(){
        var regular :Typeface? = ResourcesCompat.getFont(requireContext(), R.font.poppins_regular)
        binding?.header?.setTypeface(regular)
        binding?.tvError?.setTypeface(regular)
        binding?.tvCoach?.setTypeface(regular)
        binding?.tvSubscription?.setTypeface(regular)
        binding?.btnCoach?.setTypeface(regular)
        binding?.btnSubscription?.setTypeface(regular)
        linearLayoutManager = LinearLayoutManager(requireContext())
        linearLayoutManager?.orientation = LinearLayoutManager.VERTICAL
        binding?.rvCoach?.layoutManager = linearLayoutManager
        binding?.ivFilter?.setOnClickListener(this)
        binding?.back?.setOnClickListener(this)
        binding?.btnSubscription?.setOnClickListener(this)
        binding?.btnCoach?.setOnClickListener(this)
        access_token = UtilMethod.instance.getAccessToken(requireContext())+""
        callCoachListAPI()
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        this.activity1 = activity
    }

    override fun onResume() {
        super.onResume()
        (activity1 as LeftSideMenuActivity).enableBottomBar()
        (activity1 as LeftSideMenuActivity).setActiveSection(3)

    }


    var onItemClickCallback : OnItemClickListener.OnItemClickCallback = object : OnItemClickListener.OnItemClickCallback{

        override fun onItemClicked(view: View?, position: Int) {
            var fragmentTransaction : FragmentTransaction? = requireActivity().supportFragmentManager.beginTransaction()
            var fragment : Fragment = CoachDetailFragment()
            var bun : Bundle = Bundle()
            bun.putString("coach_id", coach_list.get(position).coach_id)
            fragment.arguments = bun
            fragmentTransaction?.replace(R.id.container_home, fragment)
            fragmentTransaction?.addToBackStack(null)
            fragmentTransaction?.commit()
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
                DialogInterface.OnClickListener { dialog, which -> callCoachListAPI() })
            alertDialog.setNegativeButton(context!!.resources!!.getString(R.string.network_cancel), DialogInterface.OnClickListener { dialog, which -> requireActivity().supportFragmentManager.popBackStack() })
            var alertDialog1 = alertDialog.create()
            alertDialog1.show()
        }
        catch (e: Exception){

        }
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

        service.callGetProfileTask("Bearer "+access_token).enqueue(object :
            Callback<ResponseBody> {

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                progressDialog.dismiss()
                t.printStackTrace()
                dialogForCheckNetworkError()
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
                                var s_end_date : String? = obj.getString("end_date")
                                Log.v("End Date", "==? "+s_end_date)
                                if(!UtilMethod.instance.isStringNullOrNot(s_end_date)){
                                    binding!!.llSubscription.visibility = View.GONE
                                    binding!!.llCoach.visibility = View.VISIBLE
                                }
                                else{
                                    binding!!.llSubscription.visibility = View.VISIBLE
                                    binding!!.llCoach.visibility = View.GONE
                                }

                            }
                        }

                    }
                }

            }
        })
    }



    fun callCoachListAPI(){

        var progressDialog : ProgressDialog = ProgressDialog.show(requireContext(), "", "")
        progressDialog.setContentView(R.layout.progress_loader)
        progressDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        progressDialog.setCancelable(false)

        val service = Retrofit.Builder()
            .baseUrl(""+ AppConstants.BASE_URL_NEW)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(AppService::class.java)


        service.getMyCoachList("Bearer "+access_token).enqueue(object :
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
                var response1 : String = response.body()!!.string()
                Log.v("Subscription Response ", "==> "+response1)
                if(!UtilMethod.instance.isStringNullOrNot(response1)){
                    var object1 : JSONObject = JSONObject(response1)
                    if(object1!=null){
                        var gson : Gson = Gson()
                        coach_list = gson.fromJson(object1.getJSONArray("result").toString(), Array<MyCoachDTO>::class.java).toList()

                        if(coach_list!=null){
                            if(coach_list.size>0){
                                binding?.rvCoach?.adapter = MyCoachAdapter(requireContext(),  coach_list, onItemClickCallback)
                                binding?.tvError!!.visibility = View.GONE
                                binding?.rvCoach!!.visibility = View.VISIBLE
                            }
                            else{
                                binding?.tvError!!.visibility = View.GONE
                                binding?.rvCoach!!.visibility = View.GONE
                                callGetProfileTask()
                            }

                        }
                        else{
                            binding?.tvError!!.visibility = View.GONE
                            binding?.rvCoach!!.visibility = View.GONE
                            callGetProfileTask()
                        }
                    }
                }
                else{

                }
            }
        })
    }
    override fun onClick(v: View?) {
        if(v == binding?.ivFilter){
            var fragmentTransaction : FragmentTransaction? = activity?.supportFragmentManager?.beginTransaction()
            fragmentTransaction?.replace(R.id.container_home, FilterFragment())
            fragmentTransaction?.addToBackStack(null)
            fragmentTransaction?.commit()
        }
        else if (v==binding?.back){
            requireActivity().onBackPressed()
        }
        else if(v == binding?.btnSubscription){
            var fragmentTransaction : FragmentTransaction? = requireActivity().supportFragmentManager.beginTransaction()
            fragmentTransaction?.replace(R.id.container_home, SubscriptionFragment())
            fragmentTransaction?.addToBackStack(null)
            fragmentTransaction?.commit()
        }
        else if(v == binding?.btnCoach){
            var fragmentTransaction : FragmentTransaction? = requireActivity().supportFragmentManager.beginTransaction()
            fragmentTransaction?.replace(R.id.container_home, PickCoachFragment())
            fragmentTransaction?.addToBackStack(null)
            fragmentTransaction?.commit()
        }
    }
}