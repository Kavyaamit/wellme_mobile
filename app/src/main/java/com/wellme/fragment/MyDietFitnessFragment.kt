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
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.wellme.LeftSideMenuActivity
import com.wellme.R
import com.wellme.adapter.AssignedDietPlanAdapter
import com.wellme.adapter.AssignedFitnessPlanAdapter
import com.wellme.databinding.FragmentMyDietWorkoutBinding
import com.wellme.dto.AssignedDietPlanDTO
import com.wellme.dto.AssignedFitnessPlanDTO
import com.wellme.dto.DietPlanDTO
import com.wellme.dto.FitnessPlanDTO
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
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MyDietFitnessFragment : Fragment(), View.OnClickListener{
    var binding : FragmentMyDietWorkoutBinding? = null
    var s_date : String? = null
    var s_access_token : String? = null
    var regular : Typeface? = null
    var diet_list : List<DietPlanDTO> = ArrayList()
    var fitness_list : List<FitnessPlanDTO> = ArrayList()
    var assigned_diet_plan_list : ArrayList<AssignedDietPlanDTO> = ArrayList()
    var assigned_fitness_plan_list : ArrayList<AssignedFitnessPlanDTO> = ArrayList()
    var linearLayoutManager : LinearLayoutManager? = null
    var linearLayoutManager1 : LinearLayoutManager? = null
    var activity : Activity? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_my_diet_workout,container, false)
        return binding!!.root
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        this.activity = activity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        s_access_token = UtilMethod.instance.getAccessToken(context!!)
        var date : Date = Date()
        var simpleDateFormat : SimpleDateFormat = SimpleDateFormat("yy-MM-dd")
        s_date = simpleDateFormat.format(date)
        regular = ResourcesCompat.getFont(context!!, R.font.poppins_regular)
        binding?.tvDiet?.setTypeface(regular)
        binding?.tvFitness?.setTypeface(regular)
        linearLayoutManager = LinearLayoutManager(context!!)
        linearLayoutManager1 = LinearLayoutManager(context!!)
        linearLayoutManager!!.orientation = LinearLayoutManager.VERTICAL
        linearLayoutManager1!!.orientation = LinearLayoutManager.VERTICAL
        binding?.rvDietPlan!!.layoutManager = linearLayoutManager
        binding?.rvFitnessPlan!!.layoutManager = linearLayoutManager1
        (activity as LeftSideMenuActivity).disableBottomBar()
        binding?.llFitness!!.setOnClickListener(this)
        binding?.llDiet!!.setOnClickListener(this)
        binding?.back!!.setOnClickListener(this)
        callDietPlanAPI()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (activity as LeftSideMenuActivity).enableBottomBar()
    }

    override fun onClick(v: View?) {
        if(v == binding?.llFitness){
            binding!!.rvDietPlan.visibility = View.GONE
            binding!!.rvFitnessPlan.visibility = View.VISIBLE
            binding?.bgFitness!!.setBackgroundColor(resources!!.getColor(R.color.base_color))
            binding?.bgDiet!!.setBackgroundColor(resources!!.getColor(R.color.grey))
            callFitnessPlanAPI()
        }
        else if(v == binding?.llDiet){
            binding!!.rvDietPlan.visibility = View.VISIBLE
            binding!!.rvFitnessPlan.visibility = View.GONE
            binding?.bgFitness!!.setBackgroundColor(resources!!.getColor(R.color.grey))
            binding?.bgDiet!!.setBackgroundColor(resources!!.getColor(R.color.base_color))
            callDietPlanAPI()
        }
        else if(v == binding?.back){
            requireActivity().supportFragmentManager.popBackStack()
        }
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
                DialogInterface.OnClickListener { dialog, which -> if(status==1){callDietPlanAPI()} else if(status==2){callFitnessPlanAPI()} })
            alertDialog.setNegativeButton(context!!.resources!!.getString(R.string.network_cancel), DialogInterface.OnClickListener { dialog, which ->  })
            var alertDialog1 = alertDialog.create()
            alertDialog1.show()
        }
        catch (e: Exception){

        }
    }


    fun callDietPlanAPI(){
        var progressDialog : ProgressDialog = ProgressDialog.show(requireContext(), "", "")
        progressDialog.setContentView(R.layout.progress_loader)
        progressDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        progressDialog.setCancelable(false)

        val service = Retrofit.Builder()
            .baseUrl(""+ AppConstants.BASE_URL_NEW)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(AppService::class.java)


        service.callDietPlanList("Bearer "+s_access_token, "20"+s_date).enqueue(object :
            Callback<ResponseBody> {

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
                Log.v("REsponse ", "==> "+response)
                var response1 : String = response.body()!!.string()
                if(!UtilMethod.instance.isStringNullOrNot(response1)){
                    var gson : Gson = Gson()
                    var object1 : JSONObject = JSONObject(response1)
                    diet_list = ArrayList()
                    assigned_diet_plan_list = ArrayList()
                    diet_list = gson.fromJson(object1.getJSONArray("food_plan").toString(), Array<DietPlanDTO>::class.java).toList()
                    if(diet_list!=null){

                        var s1 = diet_list.sortedWith(compareBy({it.time}))
                        for(i in 0..s1.size-1){
                            var flag : Boolean = false
                            var dto : DietPlanDTO = s1.get(i)
                            var time : String = s1.get(i).time
                            if(assigned_diet_plan_list!=null) {
                                for (j in 0..assigned_diet_plan_list.size - 1) {
                                    var assignedDTO : AssignedDietPlanDTO = assigned_diet_plan_list.get(j)

                                    if(assignedDTO!=null){
                                        var assigned_time : String = assignedDTO.time
                                        var list : ArrayList<DietPlanDTO> = assignedDTO.diet_plan
                                        if(time == assigned_time){
                                            if(list==null){
                                                list = ArrayList()
                                            }
                                            list.add(dto)
                                            assignedDTO.diet_plan = list
                                            assignedDTO.time = assigned_time
                                            assigned_diet_plan_list.set(j, assignedDTO)
                                            flag = true
                                            break;
                                        }
                                    }

                                }
                            }
                            if(!flag){
                                if(assigned_diet_plan_list == null){
                                    assigned_diet_plan_list = ArrayList()
                                }
                                var d1 : ArrayList<DietPlanDTO> = ArrayList()
                                d1.add(dto)
                                assigned_diet_plan_list.add(AssignedDietPlanDTO(time, d1))

                            }
                        }
                        if(assigned_diet_plan_list!=null){
                            binding!!.rvDietPlan.visibility = View.VISIBLE
                            binding!!.rvDietPlan.adapter = AssignedDietPlanAdapter(requireContext(), assigned_diet_plan_list)
                        }
                    }

                }
                else{

                }

            }
        })
    }


    fun callFitnessPlanAPI(){
        var progressDialog : ProgressDialog = ProgressDialog.show(requireContext(), "", "")
        progressDialog.setContentView(R.layout.progress_loader)
        progressDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        progressDialog.setCancelable(false)

        val service = Retrofit.Builder()
            .baseUrl(""+ AppConstants.BASE_URL_NEW)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(AppService::class.java)


        service.callFitnessPlanList("Bearer "+s_access_token, "20"+s_date).enqueue(object :
            Callback<ResponseBody> {

            /* The HTTP call failed. This method is run on the main thread */
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.d("TAG_", "An error happened!")
                progressDialog.dismiss()
                t.printStackTrace()
                dialogForCheckNetworkError(2)
            }

            /* The HTTP call was successful, we should still check status code and response body
             * on a production app. This method is run on the main thread */
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                /* This will print the response of the network call to the Logcat */
                progressDialog.dismiss()
                Log.v("Diet Response Code ", "==> "+response.raw().code())
                Log.v("Diet Response Code ", "==> "+response.raw().body())
                var response1 : String = response.body()!!.string()
                if(!UtilMethod.instance.isStringNullOrNot(response1)) {
                    var gson: Gson = Gson()
                    var object1 : JSONObject = JSONObject(response1)
                    fitness_list = ArrayList()
                    assigned_fitness_plan_list = ArrayList()
                    fitness_list = gson.fromJson(
                        object1.getJSONArray("fitness_exercise_planner_list").toString(),
                        Array<FitnessPlanDTO>::class.java
                    ).toList()

                    if(fitness_list!=null){
                        var s1 = fitness_list.sortedWith(compareBy({it.exercise_type}))
                        if(s1!=null){
                            for(i in 0..s1.size-1){
                                var flag : Boolean = false
                                var dto : FitnessPlanDTO = s1.get(i)
                                var exercise_type : String = s1.get(i).exercise_type
                                if(assigned_fitness_plan_list!=null) {
                                    for (j in 0..assigned_fitness_plan_list.size - 1) {
                                        var assignedDTO : AssignedFitnessPlanDTO = assigned_fitness_plan_list.get(j)

                                        if(assignedDTO!=null){
                                            var assigned_exercise_type : String = assignedDTO.exercise_type
                                            var list : ArrayList<FitnessPlanDTO> = assignedDTO.exercise_list
                                            if(exercise_type == assigned_exercise_type){
                                                if(list==null){
                                                    list = ArrayList()
                                                }
                                                list.add(dto)
                                                assignedDTO.exercise_list = list
                                                assignedDTO.exercise_type = assigned_exercise_type
                                                assigned_fitness_plan_list.set(j, assignedDTO)
                                                flag = true
                                                break;
                                            }
                                        }

                                    }
                                }
                                if(!flag){
                                    if(assigned_fitness_plan_list == null){
                                        assigned_fitness_plan_list = ArrayList()
                                    }
                                    var d1 : ArrayList<FitnessPlanDTO> = ArrayList()
                                    d1.add(dto)
                                    assigned_fitness_plan_list.add(AssignedFitnessPlanDTO(exercise_type, d1))

                                }
                            }
                            if(assigned_fitness_plan_list!=null){
                                binding!!.rvFitnessPlan.visibility = View.VISIBLE
                                binding!!.rvFitnessPlan.adapter = AssignedFitnessPlanAdapter(requireContext(), assigned_fitness_plan_list)
                            }
                        }
                    }


                }
            }
        })
    }

}