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
import com.wellme.adapter.ExpertiseAdapter
import com.wellme.adapter.PickCoachAdapter
import com.wellme.databinding.FragmentPickCoachBinding
import com.wellme.dto.CoachDTO
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

class PickCoachFragment : Fragment(), View.OnClickListener{
    var binding : FragmentPickCoachBinding? = null
    var coach_list : List<CoachDTO> = ArrayList()
    var diet_coach_list : ArrayList<CoachDTO> = ArrayList()
    var fitness_coach_list : ArrayList<CoachDTO> = ArrayList()
    var linearLayoutManager : LinearLayoutManager? = null
    var linearLayoutManager1 : LinearLayoutManager? = null
    var activity1 : Activity? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_pick_coach, container, false)
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
        (activity1 as LeftSideMenuActivity).setActiveSection(4)

    }

    fun initView(){
        var regular :Typeface? = ResourcesCompat.getFont(requireContext(), R.font.poppins_regular)
        linearLayoutManager = LinearLayoutManager(requireContext())
        linearLayoutManager1 = LinearLayoutManager(requireContext())
        linearLayoutManager?.orientation = LinearLayoutManager.VERTICAL
        linearLayoutManager1?.orientation = LinearLayoutManager.VERTICAL
        binding?.rvDietCoach?.layoutManager = linearLayoutManager
        binding?.rvFitnessCoach?.layoutManager = linearLayoutManager1
        binding?.back?.setOnClickListener(this)
        binding?.ivFilter!!.setOnClickListener(this)
        binding?.llDiet?.setOnClickListener(this)
        binding?.llFitness?.setOnClickListener(this)
        binding?.tvDiet!!.setTypeface(regular)
        binding?.tvFitness!!.setTypeface(regular)
        callCoachListAPI()
    }

    var onItemClickCallbackFitness : OnItemClickListener.OnItemClickCallback = object : OnItemClickListener.OnItemClickCallback{

        override fun onItemClicked(view: View?, position: Int) {
            var fragmentTransaction : FragmentTransaction? = requireActivity().supportFragmentManager.beginTransaction()
            var fragment : Fragment = CoachDetailFragment()
            var bun : Bundle = Bundle()
            bun.putString("coach_id", fitness_coach_list.get(position).coach_id)
            fragment.arguments = bun
            fragmentTransaction?.replace(R.id.container_home, fragment)
            fragmentTransaction?.addToBackStack(null)
            fragmentTransaction?.commit()
        }

    }

    var onItemClickCallbackDiet : OnItemClickListener.OnItemClickCallback = object : OnItemClickListener.OnItemClickCallback{

        override fun onItemClicked(view: View?, position: Int) {
            var fragmentTransaction : FragmentTransaction? = requireActivity().supportFragmentManager.beginTransaction()
            var fragment : Fragment = CoachDetailFragment()
            var bun : Bundle = Bundle()
            bun.putString("coach_id", diet_coach_list.get(position).coach_id)
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


        service.getCoachList().enqueue(object :
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
                        coach_list = gson.fromJson(object1.getJSONArray("result").toString(), Array<CoachDTO>::class.java).toList()

                        if(coach_list!=null){
                            getList()
                        }
                    }
                }
                else{

                }
            }
        })
    }

    fun getList(){
        diet_coach_list = ArrayList()
        fitness_coach_list = ArrayList()
        if(coach_list!=null){
            for(i in 0..coach_list.size-1){
                var dto : CoachDTO = coach_list.get(i)
                if(dto!=null){
                    var name : String = dto.name
                    if(!UtilMethod.instance.isStringNullOrNot(name)){
                        if(name.equals("Diet Coach", true)){
                            diet_coach_list!!.add(dto)
                        }
                        else if(name.equals("Fitness Coach", true)){
                            fitness_coach_list!!.add(dto)
                        }
                    }
                }
            }
            if(diet_coach_list!=null){
                binding?.rvDietCoach!!.adapter = PickCoachAdapter(requireContext(), diet_coach_list, onItemClickCallbackDiet)
            }
            if(fitness_coach_list!=null){
                binding?.rvFitnessCoach!!.adapter = PickCoachAdapter(requireContext(), fitness_coach_list, onItemClickCallbackFitness)
            }
            Log.v("Size of Diet ", "==> "+diet_coach_list.size)
            Log.v("Size of Fitness ", "==> "+fitness_coach_list.size)
        }
    }

    override fun onClick(v: View?) {
        if(v == binding?.ivFilter){
            var fragmentTransaction : FragmentTransaction? = activity?.supportFragmentManager?.beginTransaction()
            fragmentTransaction?.replace(R.id.container_home, FilterFragment())
            fragmentTransaction?.addToBackStack(null)
            fragmentTransaction?.commit()
        }else if (v==binding?.back){

            requireActivity().supportFragmentManager.popBackStack()
        }
        else if(v == binding?.llDiet){
            binding?.rvDietCoach!!.visibility = View.VISIBLE
            binding?.rvFitnessCoach!!.visibility = View.GONE
            binding?.bgFitness!!.setBackgroundColor(resources!!.getColor(R.color.grey))
            binding?.bgDiet!!.setBackgroundColor(resources!!.getColor(R.color.base_color))
        }
        else if(v == binding?.llFitness){
            binding?.rvDietCoach!!.visibility = View.GONE
            binding?.rvFitnessCoach!!.visibility = View.VISIBLE
            binding?.bgFitness!!.setBackgroundColor(resources!!.getColor(R.color.base_color))
            binding?.bgDiet!!.setBackgroundColor(resources!!.getColor(R.color.grey))
        }
    }
}