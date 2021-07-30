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
import com.wellme.adapter.WorkoutAdapter
import com.wellme.databinding.FragmentWorkoutBinding
import com.wellme.dto.WorkoutDTO
import com.wellme.utils.AppConstants
import com.wellme.utils.AppService
import com.wellme.utils.OnItemClickListener.OnItemClickCallback
import com.wellme.utils.UtilMethod
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class WorkoutFragment : Fragment(), View.OnClickListener{
    var binding : FragmentWorkoutBinding? = null
    var linearLayoutManager : LinearLayoutManager? = null
    var workout_list : ArrayList<WorkoutDTO> = ArrayList()
    var workout_list1 : List<WorkoutDTO> = ArrayList()
    var s_date : String? = ""
    var bundle : Bundle? = null
    var activity1 : Activity? = null


    private val onItemClickDetails: OnItemClickCallback = object : OnItemClickCallback {
        override fun onItemClicked(view: View?, position: Int) {
            if(workout_list!=null){
                var dto : WorkoutDTO = workout_list.get(position)
                if(dto!=null){
                    var id : String = dto.id
                    if(id=="0"){
                        var fragmentTransaction : FragmentTransaction? = activity?.supportFragmentManager?.beginTransaction()
                        var fragment : Fragment = AddStaticWorkoutFragment()
                        var bun : Bundle = Bundle()
                        bun.putString("exercise", "0")
                        bun.putString("id", "0")
                        bun.putString("exercise_name", dto.exercise_name)
                        bun.putString("met", dto.met)
                        bun.putString("date", s_date)
                        fragment!!.arguments = bun
                        fragmentTransaction?.replace(R.id.container_home, fragment)
                        fragmentTransaction?.addToBackStack(null)
                        fragmentTransaction?.commit()
                    }
                    else{
                        var fragmentTransaction : FragmentTransaction? = activity?.supportFragmentManager?.beginTransaction()
                        var fragment = AddWorkoutFragment()
                        var bun : Bundle = Bundle()
                        bun.putString("exercise_id", dto.exercise)
                        bun.putString("id", dto.id)
                        bun.putString("exercise_name", dto.exercise_name)
                        bun.putString("met", dto.met)
                        bun.putString("date", s_date)
                        fragment!!.arguments = bun
                        fragmentTransaction?.replace(R.id.container_home, fragment)
                        fragmentTransaction?.addToBackStack(null)
                        fragmentTransaction?.commit()
                    }
                }

            }


        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_workout, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
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


    fun initView(){
        var regular : Typeface? = ResourcesCompat.getFont(requireContext(), R.font.poppins_regular)
        binding?.header?.setTypeface(regular)
        binding?.etSearch?.setTypeface(regular)
        linearLayoutManager = LinearLayoutManager(requireContext())
        linearLayoutManager?.orientation = LinearLayoutManager.VERTICAL
        binding?.rvWorkout?.layoutManager = linearLayoutManager

        binding?.back?.setOnClickListener(this)
        workout_list = ArrayList()
        workout_list!!.add(WorkoutDTO("0", "0", "Walking", "", "", "", "", "", ""))
        workout_list!!.add(WorkoutDTO("0", "0", "Running", "", "", "", "", "", ""))
        workout_list!!.add(WorkoutDTO("0", "0", "Bicycling", "", "", "7.5", "", "", ""))
        workout_list!!.add(WorkoutDTO("0", "0", "Swimming", "", "", "5.8", "", "", ""))
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


        service.getExerciseList().enqueue(object : Callback<ResponseBody> {

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
                    var object1 : JSONObject = JSONObject(response1)
                    Log.v("Response 123 ", "==> "+response1)
                    if(object1!=null){
                        var gson : Gson = Gson()
                        workout_list1 = gson.fromJson(object1.getJSONArray("result").toString(), Array<WorkoutDTO>::class.java).toList()
                        if(workout_list1!=null){
                            workout_list!!.addAll(workout_list1)
                        }

                        if(workout_list!=null){
                            binding?.rvWorkout?.adapter = WorkoutAdapter(requireContext(), workout_list, onItemClickDetails)
                        }
                    }
                }
            }
        })
    }




    override fun onClick(v: View?) {
        if(v == binding?.back){
            activity?.onBackPressed()
        }
    }
}