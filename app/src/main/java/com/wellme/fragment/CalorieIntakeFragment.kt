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
import com.wellme.adapter.FoodTypeAdapter
import com.wellme.databinding.FragmentCalorieIntakeBinding
import com.wellme.dto.CalorieIntakeDTO
import com.wellme.dto.FoodTypeDTO
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

class CalorieIntakeFragment : Fragment(), View.OnClickListener{
    var binding : FragmentCalorieIntakeBinding? = null
    var linearLayoutManager1 : LinearLayoutManager? = null
    var s_access_token : String? = ""
    var s_date : String? = ""
    var calorie_intake_list : List<CalorieIntakeDTO> = ArrayList()
    var bundle : Bundle? = null
    var food_type_list : List<FoodTypeDTO> = ArrayList()
    var activity : Activity? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_calorie_intake, container, false)
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
        bundle = arguments
        if(bundle!=null){
            s_date = bundle!!.getString("date")
            Log.v("Date ", "==> "+s_date)
        }
        initView()
    }

    fun initView(){
        var regular : Typeface? = ResourcesCompat.getFont(requireContext(), R.font.poppins_regular)
        binding?.header?.setTypeface(regular)
        binding?.tvTotalCal?.setTypeface(regular)
        linearLayoutManager1 = LinearLayoutManager(requireContext())
        linearLayoutManager1?.orientation = LinearLayoutManager.VERTICAL
        binding?.rvFoodtype?.layoutManager = linearLayoutManager1
        binding?.back?.setOnClickListener(this)
        s_access_token = UtilMethod.instance.getAccessToken(requireContext())
        callFoodTypeAPI()


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
                DialogInterface.OnClickListener { dialog, which -> callFoodTypeAPI() })
            alertDialog.setNegativeButton(context!!.resources!!.getString(R.string.network_cancel), DialogInterface.OnClickListener { dialog, which -> requireActivity().supportFragmentManager.popBackStack() })
            var alertDialog1 = alertDialog.create()
            alertDialog1.show()
        }
        catch (e: Exception){

        }
    }

    fun callFoodTypeAPI(){

        var progressDialog : ProgressDialog = ProgressDialog.show(requireContext(), "", "")
        progressDialog.setContentView(R.layout.progress_loader)
        progressDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        progressDialog.setCancelable(false)

        val service = Retrofit.Builder()
            .baseUrl(""+ AppConstants.BASE_URL_NEW)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(AppService::class.java)


        service.getFoodFeedbackTypeList().enqueue(object :
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
                Log.v("Notification Response ", "==> "+response1)
                if(!UtilMethod.instance.isStringNullOrNot(response1)){
                    var object1 : JSONObject = JSONObject(response1)
                    if(object1!=null){
                        var gson : Gson = Gson()
                        food_type_list = gson.fromJson(object1.getJSONArray("food_type").toString(), Array<FoodTypeDTO>::class.java).toList()
                        callCalorieIntakeAPI()

                    }
                }
                else{

                }
            }
        })
    }



    override fun onClick(v: View?) {
       if (v==binding?.back){

            activity?.onBackPressed()
        }
    }

    var onItemClickCallback : OnItemClickListener.OnItemClickCallback = object : OnItemClickListener.OnItemClickCallback{
        override fun onItemClicked(view: View?, position: Int) {
            var fragmentTransaction : FragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
            var fragment : Fragment = FrequentlyTrackedFoodsFragment()
            var bundle : Bundle = Bundle()
            bundle.putString("food_type", food_type_list.get(position).food_type_name)
            bundle.putString("date", s_date)
            fragment.arguments = bundle
            fragmentTransaction?.replace(R.id.container_home, fragment)
            fragmentTransaction?.addToBackStack(null)
            fragmentTransaction?.commit()
        }

    }

    fun callCalorieIntakeAPI(){
        var progressDialog : ProgressDialog = ProgressDialog.show(requireContext(), "", "")
        progressDialog.setContentView(R.layout.progress_loader)
        progressDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        progressDialog.setCancelable(false)

        val service = Retrofit.Builder()
            .baseUrl(""+ AppConstants.BASE_URL_NEW)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(AppService::class.java)


        service.callCalorieIntakeList("Bearer "+s_access_token, "20"+s_date).enqueue(object : Callback<ResponseBody> {

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
                Log.v("REsponse Code ", "==> "+response.raw().code())
                Log.v("REsponse Code ", "==> "+response.raw().body())
                var response1 : String = response.body()!!.string()
                if(!UtilMethod.instance.isStringNullOrNot(response1)){
                    Log.v("ClrieIntkeResponse ", "==> "+response1)
                    var object1 : JSONObject = JSONObject(response1)
                    if(object1!=null){
                        var gson : Gson = Gson()
                        calorie_intake_list = gson.fromJson(object1.getJSONArray("result").toString(), Array<CalorieIntakeDTO>::class.java).toList()

                        if(calorie_intake_list!=null){
                            if(calorie_intake_list.size>0){
                                var total_cal : Float = 0f

                                for(i in 0..calorie_intake_list.size - 1){
                                    var dto: CalorieIntakeDTO = calorie_intake_list.get(i)
                                    if(dto!=null && food_type_list!=null){
                                        var s_total_calorie : String = dto.total_calorie
                                        var food_type: String = dto.foodtype
                                        if(!UtilMethod.instance.isStringNullOrNot(s_total_calorie)){
                                            total_cal+=s_total_calorie.toFloat()
                                        }
                                        for(j in 0..food_type_list.size-1){
                                            var s_type = food_type_list.get(j).food_type_name
                                            if(!UtilMethod.instance.isStringNullOrNot(food_type) && !UtilMethod.instance.isStringNullOrNot(s_type)){
                                                if(s_type.equals(food_type, true)){
                                                    var list1 : ArrayList<CalorieIntakeDTO> = food_type_list.get(j).calorie_list
                                                    if(list1==null){
                                                        list1 = ArrayList()
                                                    }
                                                    list1.add(dto)
                                                    food_type_list.get(j).calorie_list = list1
                                                    break
                                                }
                                            }
                                        }
                                    }
                                }





                                binding?.tvTotalCal!!.setText(UtilMethod.instance.getFormatedAmountString(total_cal)+" of 2500 Cal")

                            }






                        }
                    }

                    binding?.rvFoodtype!!.adapter = FoodTypeAdapter(requireContext(), food_type_list, onItemClickCallback)
                }
                else{

                }

            }
        })
    }

}