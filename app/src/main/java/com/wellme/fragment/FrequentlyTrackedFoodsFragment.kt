package com.wellme.fragment

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.wellme.LeftSideMenuActivity
import com.wellme.R
import com.wellme.adapter.TrackedFoodAdapter
import com.wellme.databinding.FragmentTrackedFoodsBinding
import com.wellme.dto.TrackedFoodDTO
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

class FrequentlyTrackedFoodsFragment : Fragment(), View.OnClickListener{
    var binding : FragmentTrackedFoodsBinding? = null
    var linearLayoutManager : LinearLayoutManager? = null
    var bundle : Bundle? = null
    var s_food_type : String? = ""
    var tracked_food_list : List<TrackedFoodDTO> = ArrayList()
    var s_calorie_intake_id : String = ""
    var s_food_id : String = ""
    var s_quantity : String = ""
    var s_total_calorie : String = ""
    var s_food_name : String = ""
    var s_size : String = ""
    var s_access_token : String? = ""
    var s_date : String? = ""
    var quantityDialog : Dialog? = null
    var activity : Activity? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_tracked_foods, container, false)
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
        var regular : Typeface? = ResourcesCompat.getFont(requireContext(), R.font.poppins_regular)
        binding?.etSearch?.setTypeface(regular)
        binding?.header?.setTypeface(regular)
        linearLayoutManager = LinearLayoutManager(requireContext())
        linearLayoutManager?.orientation = LinearLayoutManager.VERTICAL
        binding?.rvTrackedFoods?.layoutManager = linearLayoutManager
        s_access_token = UtilMethod.instance.getAccessToken(requireContext())

        binding?.etSearch!!.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                callTrackedFoodAPI()
            }

        })

        bundle = arguments
        if(bundle!=null){
            s_food_type = bundle!!.getString("food_type")
            s_date = bundle!!.getString("date")
            if(!UtilMethod.instance.isStringNullOrNot(s_food_type)){
                callTrackedFoodAPI()
            }
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
                DialogInterface.OnClickListener { dialog, which -> callTrackedFoodAPI() })
            alertDialog.setNegativeButton(context!!.resources!!.getString(R.string.network_cancel), DialogInterface.OnClickListener { dialog, which -> requireActivity().supportFragmentManager.popBackStack() })
            var alertDialog1 = alertDialog.create()
            alertDialog1.show()
        }
        catch (e: Exception){

        }
    }

    fun callTrackedFoodAPI(){

        var progressDialog : ProgressDialog = ProgressDialog.show(requireContext(), "", "")
        progressDialog.setContentView(R.layout.progress_loader)
        progressDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        progressDialog.setCancelable(false)

        val service = Retrofit.Builder()
            .baseUrl(""+ AppConstants.BASE_URL_NEW)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(AppService::class.java)


        service.getTrackedFoodList(""+s_food_type, ""+binding?.etSearch!!.text.toString()).enqueue(object :
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
                        tracked_food_list = gson.fromJson(object1.getJSONArray("result").toString(), Array<TrackedFoodDTO>::class.java).toList()

                        if(tracked_food_list!=null){
                            binding?.rvTrackedFoods?.adapter = TrackedFoodAdapter(requireContext(), tracked_food_list, onItemClickCallback)
                        }
                    }
                }
                else{

                }
            }
        })
    }


    fun showQuantityDialog(){
        quantityDialog = Dialog(context!!, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        quantityDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        quantityDialog!!.setContentView(R.layout.dialog_update_bodymeasurement)
        quantityDialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        quantityDialog!!.show()
        var regular : Typeface? = ResourcesCompat.getFont(context!!, R.font.poppins_regular)
        var tv_header : TextView = quantityDialog!!.findViewById(R.id.tv_header)
        var btn_update : Button = quantityDialog!!.findViewById(R.id.btn_update)
        var tv_value : TextView = quantityDialog!!.findViewById(R.id.tv_value)
        var iv_plus : ImageView = quantityDialog!!.findViewById(R.id.iv_plus)
        var iv_minus : ImageView = quantityDialog!!.findViewById(R.id.iv_minus)
        tv_header!!.setTypeface(regular)
        btn_update!!.setTypeface(regular)
        tv_value!!.setTypeface(regular)
        tv_value!!.setText("1")
        tv_header!!.setText(s_food_name+" ( in "+s_size+" )")
        btn_update.setText("Save")
        var quan : Int = 1
        var calorie_per_item : Float = 0f
        var total_calories : Float = 0f
        var total_quan : Int = 0

        iv_plus.setOnClickListener(View.OnClickListener {view ->
            quan = 0
            var s_qu : String = tv_value!!.text.toString()
            if(!UtilMethod.instance.isStringNullOrNot(s_qu)){
                quan = s_qu.toInt()
            }
            quan+=1
            setValue(tv_value, quan)
        })

        iv_minus.setOnClickListener(View.OnClickListener {view ->
            quan = 0
            var s_qu : String = tv_value!!.text.toString()
            if(!UtilMethod.instance.isStringNullOrNot(s_qu)){
                quan = s_qu.toInt()
            }
            if(quan>1){
                quan-=1
            }
            else{
                quantityDialog!!.dismiss()
            }

            setValue(tv_value, quan)
        })


        btn_update.setOnClickListener(View.OnClickListener {view ->
            quantityDialog!!.dismiss()
            if(quan>0){

                if(!UtilMethod.instance.isStringNullOrNot(s_total_calorie)){
                    total_calories = s_total_calorie.toFloat()
                }
                if(!UtilMethod.instance.isStringNullOrNot(s_quantity)){
                    total_quan = s_quantity.toInt()
                }

                if(total_calories>0 && total_quan>0){
                    calorie_per_item = total_calories / total_quan
                }
                total_calories = calorie_per_item * quan

                callAddCalorieIntakeAPI(quan, total_calories, calorie_per_item)
            }
        })








    }

    fun setValue(tv : TextView, f1 : Int){
        tv!!.setText(""+f1)
    }

    fun callAddCalorieIntakeAPI(quan : Int, total_Cal : Float, calories_per_item : Float){

        var progressDialog : ProgressDialog = ProgressDialog.show(requireContext(), "", "")
        progressDialog.setContentView(R.layout.progress_loader)
        progressDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        progressDialog.setCancelable(false)

        val service = Retrofit.Builder()
            .baseUrl(""+ AppConstants.BASE_URL_NEW)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(AppService::class.java)

                    Log.v("DAte ", "==> "+s_date)


        service.callAddCalorieIntake("Bearer "+s_access_token,
            ""+s_calorie_intake_id,
            s_food_id,
            ""+quan,
            ""+total_Cal,
            s_food_name,
            ""+calories_per_item,
            s_food_type!!,
            s_size,
            "20"+s_date+" 10:10:10+00:00").enqueue(object :
            Callback<ResponseBody> {

            /* The HTTP call failed. This method is run on the main thread */
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.d("TAG_", "An error happened!")
                progressDialog.dismiss()
                t.printStackTrace()
            }

            /* The HTTP call was successful, we should still check status code and response body
             * on a production app. This method is run on the main thread */
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                /* This will print the response of the network call to the Logcat */
                progressDialog.dismiss()
                Log.v("Response Code ", "==> "+response.raw().code())
                Log.v("Response Code ", "==> "+response.raw().body())
                var response1 : String = response.body()!!.string()
                Log.v("Subscription Response ", "==> "+response1)
                if(!UtilMethod.instance.isStringNullOrNot(response1)){
                    var object1 : JSONObject = JSONObject(response1)
                    if(object1!=null){
                        var success : Int = object1.getInt("success")
                        if(success == 200){
                            requireActivity().onBackPressed()
                        }
                    }
                }
                else{

                }
            }
        })
    }



    var onItemClickCallback : OnItemClickListener.OnItemClickCallback = object : OnItemClickListener.OnItemClickCallback{
        override fun onItemClicked(view: View?, position: Int) {
            var dto : TrackedFoodDTO = tracked_food_list.get(position)
            if(dto!=null){
                s_calorie_intake_id = dto.id
                s_food_id = dto.id
                s_food_name = dto.food_name
                s_quantity = dto.weight
                s_total_calorie = dto.calory
                s_size = dto.size
                showQuantityDialog()

                //callAddCalorieIntakeAPI()
            }
        }

    }


    override fun onClick(v: View?) {

    }
}