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
import com.wellme.adapter.CalorieBurnAdapter
import com.wellme.databinding.FragmentCalorieBurnBinding
import com.wellme.dto.CalorieBurnDTO
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

class CalorieBurnFragment : Fragment(), View.OnClickListener{
    var binding : FragmentCalorieBurnBinding? = null
    var linearLayoutManager : LinearLayoutManager? = null
    var s_access_token : String? = ""
    var calorie_burn_list : List<CalorieBurnDTO> = ArrayList()
    var s_date : String? = ""
    var bundle : Bundle? = null
    var activity : Activity? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_calorie_burn, container, false)
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
        (activity as LeftSideMenuActivity).enableBottomBar()
        (activity as LeftSideMenuActivity).setActiveSection(0)

    }



    fun initView(){
        linearLayoutManager = LinearLayoutManager(requireContext())
        linearLayoutManager?.orientation = LinearLayoutManager.VERTICAL
        binding?.rvCalorieBurn?.layoutManager = linearLayoutManager
        binding?.ivPlus?.setOnClickListener(this)
        binding?.back?.setOnClickListener(this)
        var regular : Typeface? = ResourcesCompat.getFont(requireContext(), R.font.poppins_regular)
        binding?.header?.setTypeface(regular)
        binding?.tvError!!.visibility = View.GONE
        binding?.tvError!!.setTypeface(regular)
        binding?.tvWeightText?.setTypeface(regular)
        s_access_token = UtilMethod.instance.getAccessToken(requireContext())
        Log.v("Token Value", "==> "+s_access_token)
        bundle = arguments
        if(bundle!=null){
            s_date = bundle!!.getString("date")
        }
        callWorkoutAPI()
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
                DialogInterface.OnClickListener { dialog, which -> callWorkoutAPI() })
            alertDialog.setNegativeButton(context!!.resources!!.getString(R.string.network_cancel), DialogInterface.OnClickListener { dialog, which -> requireActivity().supportFragmentManager.popBackStack() })
            var alertDialog1 = alertDialog.create()
            alertDialog1.show()
        }
        catch (e: Exception){

        }
    }


    fun callWorkoutAPI(){
        var progressDialog : ProgressDialog = ProgressDialog.show(requireContext(), "", "")
        progressDialog.setContentView(R.layout.progress_loader)
        progressDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        progressDialog.setCancelable(false)

        val service = Retrofit.Builder()
            .baseUrl(""+ AppConstants.BASE_URL_NEW)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(AppService::class.java)


        service.callUserExercise("Bearer "+s_access_token, "20"+s_date).enqueue(object : Callback<ResponseBody> {

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
                if(!UtilMethod.instance.isStringNullOrNot(response1)){
                    Log.v("Response ", "==> "+response1)
                    var object1 : JSONObject = JSONObject(response1)
                    if(object1!=null){
                        var gson : Gson = Gson()
                        calorie_burn_list = gson.fromJson(object1.getJSONArray("result").toString(), Array<CalorieBurnDTO>::class.java).toList()

                        if(calorie_burn_list!=null){
                            if(calorie_burn_list.size>0) {
                                binding?.rvCalorieBurn?.adapter =
                                    CalorieBurnAdapter(requireContext(), calorie_burn_list)
                                binding!!.rvCalorieBurn.visibility = View.VISIBLE
                                binding!!.tvError.visibility = View.GONE
                            }
                            else{
                                binding!!.rvCalorieBurn.visibility = View.GONE
                                binding!!.tvError.visibility = View.VISIBLE
                            }
                        }
                        else{
                            UtilMethod.instance.dialogOK(requireContext(), "", "No calorie burn from your side yet!", "OK", false)
                        }
                    }
                }
                else{

                }

            }
        })
    }


    override fun onClick(v: View?) {
        if(v == binding?.ivPlus){
            var fragment : Fragment  = WorkoutFragment()
            var bundle : Bundle = Bundle()
            bundle.putString("date", s_date)
            fragment.arguments = bundle
            var fragmentTransaction : FragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
            fragmentTransaction?.replace(R.id.container_home, fragment)
            fragmentTransaction?.addToBackStack(null)
            fragmentTransaction?.commit()
        }
        if(v == binding?.back){
            activity?.onBackPressed()
        }
    }

}