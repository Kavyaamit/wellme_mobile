package com.wellme.fragment

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.Context
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
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.data.Field
import com.google.gson.Gson
import com.wellme.LeftSideMenuActivity
import com.wellme.MainActivity
import com.wellme.R
import com.wellme.adapter.BlogAdapter
import com.wellme.adapter.SubscribeAdapter
import com.wellme.adapter.TestinomialsAdapter
import com.wellme.databinding.FragmentHomeBinding
import com.wellme.dto.BlogDTO
import com.wellme.dto.SubscriptionDTO
import com.wellme.dto.TestimonialDTO
import com.wellme.dto.UserDTO
import com.wellme.utils.AppConstants
import com.wellme.utils.AppService
import com.wellme.utils.OnDialogClickListener
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

class HomeFragment : Fragment(), View.OnClickListener{
    var binding : FragmentHomeBinding? = null
    var cal : Calendar? = null
    var access_token : String? = ""
    var linearLayoutManager : LinearLayoutManager? = null
    var linearLayoutManager1 : LinearLayoutManager? = null
    var testimonial_list : List<TestimonialDTO> = ArrayList()
    var blog_list : List<BlogDTO> = ArrayList()
    var testinomialsAdapter : TestinomialsAdapter? = null
    var blogAdapter : BlogAdapter? = null
    var subscription_list : List<SubscriptionDTO> = ArrayList()
    var subscribeAdapter : SubscribeAdapter? = null
    var s_current_weight : String? = ""
    var dto : UserDTO? = null
    var datePickerDialog : DatePickerDialog? = null
    var s_date : String = ""
    var activity : Activity? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
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
        var regular : Typeface? = ResourcesCompat.getFont(requireContext(), R.font.poppins_regular)
        binding?.llDate?.setOnClickListener(this)
        binding?.ivWaterIntakePlus?.setOnClickListener(this)
        binding?.ivCalorieEatenPlus?.setOnClickListener(this)
        binding?.ivCalorieBurnPlus?.setOnClickListener(this)
        binding?.ivMenu?.setOnClickListener(this)
        binding?.tvTestimonialsViewall?.setOnClickListener(this)
        binding?.tvBlogViewall?.setOnClickListener(this)
        dto = UtilMethod.instance.getUser(requireContext())
        if(dto!=null){
            s_current_weight = dto!!.current_weight

        }
        initView()
    }

    fun initView(){
        access_token = UtilMethod.instance.getAccessToken(requireContext())
        var regular : Typeface? = ResourcesCompat.getFont(requireContext(), R.font.poppins_regular)
        var bold : Typeface? = ResourcesCompat.getFont(requireContext(), R.font.poppins_bold)
        binding?.tvCalorie?.setTypeface(bold)
        binding?.tvCalorieUnit?.setTypeface(regular)
        binding?.tvDistance?.setTypeface(bold)
        binding?.tvDate?.setTypeface(regular)
        binding?.tvDistanceUnit?.setTypeface(regular)
        binding?.tvTime?.setTypeface(bold)
        binding?.tvTimeUnit?.setTypeface(regular)
        binding?.tvTestimonials?.setTypeface(regular)
        binding?.tvBlog?.setTypeface(regular)
        binding?.tvTestimonialsViewall?.setTypeface(regular)
        binding?.tvBlogViewall?.setTypeface(regular)
        binding?.tvCalorieBurnAmt?.setTypeface(bold)
        binding?.tvCalorieEatenAmt?.setTypeface(bold)
        binding?.tvWaterIntakeAmt?.setTypeface(bold)
        binding?.tvCalorieBurnMax?.setTypeface(regular)
        binding?.tvCalorieBurnText?.setTypeface(regular)
        binding?.tvPercentage?.setTypeface(regular)
        binding?.waterIntakeText?.setTypeface(regular)
        binding?.calorieBurnText?.setTypeface(regular)
        binding?.calorieEatenText?.setTypeface(regular)
        binding?.tvCalorieEatenMax?.setTypeface(regular)
        binding?.tvCalorieBurn?.setTypeface(regular)
        binding?.tvCurrentWeight?.setTypeface(regular)
        binding?.tvCurrentWeightText?.setTypeface(regular)
        binding?.tvTargetWeight?.setTypeface(regular)
        binding?.tvTargetWeightText?.setTypeface(regular)
        binding?.tvWeightType?.setTypeface(regular)
        binding?.tvWeightTypeText?.setTypeface(regular)
        binding?.tvWeightUnit1?.setTypeface(regular)
        binding?.tvWeightUnit2?.setTypeface(regular)
        binding?.tvWeightUnit3?.setTypeface(regular)
        linearLayoutManager = LinearLayoutManager(requireContext())
        linearLayoutManager!!.orientation = LinearLayoutManager.HORIZONTAL
        binding?.rvTestimonials!!.layoutManager = linearLayoutManager

        linearLayoutManager1 = LinearLayoutManager(requireContext())
        linearLayoutManager1!!.orientation = LinearLayoutManager.HORIZONTAL
        binding?.rvBlog!!.layoutManager = linearLayoutManager1
        cal = Calendar.getInstance()
        var date : Date = Date()
        var simpleDateFormat : SimpleDateFormat = SimpleDateFormat("yy-MM-dd")
        s_date = simpleDateFormat.format(date)
        var flag : Boolean? = UtilMethod.instance.isConnnectWithFitness(requireContext())
        if(flag == true){
            readData()
        }

        callAPI(s_date)



    }


    fun callAPI(date : String?){

        var progressDialog : ProgressDialog = ProgressDialog.show(requireContext(), "", "")
        progressDialog.setContentView(R.layout.progress_loader)
        progressDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        progressDialog.setCancelable(false)

        val service = Retrofit.Builder()
            .baseUrl(""+ AppConstants.BASE_URL_NEW)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(AppService::class.java)


        service.callHomeAPI("Bearer "+access_token, date).enqueue(object : Callback<ResponseBody> {

            /* The HTTP call failed. This method is run on the main thread */
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.d("TAG_", "An error happened!")
                progressDialog.dismiss()
                t.printStackTrace()
                dialogForCheckNetworkError();
            }

            /* The HTTP call was successful, we should still check status code and response body
             * on a production app. This method is run on the main thread */
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                /* This will print the response of the network call to the Logcat */
                progressDialog.dismiss()
                var code : Int = response.raw().code()

                if(code == 200) {
                    var response1: String = response.body()!!.string()
                    if (!UtilMethod.instance.isStringNullOrNot(response1)) {
                        Log.v("Response ", "==> "+response1)
                        var object1: JSONObject = JSONObject(response1)
                        var gson : Gson = Gson()
                        if(object1!=null){
                            try{
                                var user_profile : JSONObject  = object1.getJSONObject("user_profile")
                                if(user_profile!=null){
                                    var s_initial_logged_weight : String = user_profile.getString("initial_logged_weight")
                                    var s_current_weight : String = user_profile.getString("current_weight")
                                    var s_target_weight : String = user_profile.getString("target_weight")
                                    var s_weight_loss : String = user_profile.getString("weght_loss")
                                    var s_weight_gain : String = user_profile.getString("weight_gain")
                                    var s_calorie_eaten_amount : String = user_profile.getString("calorie_eaten_amount")
                                    var s_calorie_burn_amount : String = user_profile.getString("calorie_burn_amount")
                                    var s_total_water_amount : String = user_profile.getString("total_water_amount")
                                    var initial_logged_weight : Float = 0f
                                    var current_weight : Float = 0f
                                    var weight_loss : Float = 0f
                                    var weight_gain : Float = 0f
                                    var target_weight : Float = 0f
                                    var total_water_amount : Int = 0
                                    var calorie_burn_amount : Int = 0
                                    var calorie_eaten_amount : Int = 0

                                    if(!UtilMethod.instance.isStringNullOrNot(s_initial_logged_weight)){
                                        initial_logged_weight = s_initial_logged_weight.toFloat()
                                    }
                                    if(!UtilMethod.instance.isStringNullOrNot(s_current_weight)){
                                        current_weight = s_current_weight.toFloat()
                                    }
                                    if(!UtilMethod.instance.isStringNullOrNot(s_weight_loss)){
                                        weight_loss = s_weight_loss.toFloat()
                                    }
                                    if(!UtilMethod.instance.isStringNullOrNot(s_target_weight)){
                                        target_weight = s_target_weight.toFloat()
                                    }
                                    if(!UtilMethod.instance.isStringNullOrNot(s_weight_gain)){
                                        weight_gain = s_weight_gain.toFloat()
                                    }
                                    if(!UtilMethod.instance.isStringNullOrNot(s_calorie_burn_amount)){
                                        calorie_burn_amount = s_calorie_burn_amount.toFloat().toInt()
                                    }
                                    if(!UtilMethod.instance.isStringNullOrNot(s_calorie_eaten_amount)){
                                        calorie_eaten_amount = s_calorie_eaten_amount.toFloat().toInt()
                                    }
                                    if(!UtilMethod.instance.isStringNullOrNot(s_total_water_amount)){
                                        total_water_amount = s_total_water_amount.toFloat().toInt()
                                    }

                                    binding?.tvCalorieBurnAmt!!.setText(""+calorie_burn_amount)
                                    binding?.tvCalorieBurn!!.setText(""+calorie_burn_amount)
                                    binding?.tvCalorieEatenAmt!!.setText(""+calorie_eaten_amount)
                                    binding?.tvWaterIntakeAmt!!.setText(""+total_water_amount)

                                    if(weight_gain>0){
                                        binding?.tvWeightType!!.setText(UtilMethod.instance.getFormatedAmountString(weight_gain))
                                        binding?.tvWeightTypeText!!.setText("Weight Gain")

                                    }else if(weight_loss>0){
                                        binding?.tvWeightType!!.setText(UtilMethod.instance.getFormatedAmountString(weight_loss))
                                        binding?.tvWeightTypeText!!.setText("Weight Loss")

                                    }else{

                                        binding?.tvWeightType!!.setText("0.00")
                                        binding?.tvWeightTypeText!!.setText("Neutral")
                                    }



                                    binding?.tvCurrentWeight!!.setText(UtilMethod.instance.getFormatedAmountString(current_weight))
                                    binding?.tvTargetWeight!!.setText(UtilMethod.instance.getFormatedAmountString(target_weight))
                                }
                            }
                            catch(e : Exception){
                                Log.v("Exception", "==> "+e)
                            }
                            testimonial_list = gson.fromJson(object1.getJSONArray("testimonial").toString(), Array<TestimonialDTO>::class.java).toList()
                            blog_list = gson.fromJson(object1.getJSONArray("blog").toString(), Array<BlogDTO>::class.java).toList()
                            subscription_list = gson.fromJson(object1.getJSONArray("plans").toString(), Array<SubscriptionDTO>::class.java).toList()

                            if(blog_list!=null){
                                blogAdapter = BlogAdapter(requireActivity(), blog_list)
                                binding?.rvBlog!!.adapter = blogAdapter
                                binding?.llBlogHeader?.visibility = View.VISIBLE
                                if(blog_list.size > 1){
                                    binding?.tvBlogViewall!!.visibility = View.VISIBLE
                                }
                                else{
                                    binding?.llBlogHeader?.visibility = View.GONE
                                }
                            }
                            else{
                                binding?.llBlogHeader?.visibility = View.GONE
                            }

                            if(testimonial_list!=null){
                                testinomialsAdapter = TestinomialsAdapter(requireActivity(), testimonial_list)
                                binding?.rvTestimonials!!.adapter = testinomialsAdapter
                                binding?.llTestinomialHeader?.visibility = View.VISIBLE
                                if(testimonial_list.size > 1){
                                    binding?.tvTestimonialsViewall!!.visibility = View.VISIBLE
                                }
                                else{
                                    binding?.llTestinomialHeader?.visibility = View.GONE
                                }
                            }
                            else{
                                binding?.llTestinomialHeader?.visibility = View.GONE
                            }
                            if(subscription_list!=null){
                                if(subscription_list.size >0) {
                                    subscribeAdapter = SubscribeAdapter(requireContext(), subscription_list)
                                    binding?.vpSubscribe!!.adapter = subscribeAdapter
                                    binding?.vpSubscribe!!.visibility = View.VISIBLE
                                }
                                else{
                                    binding?.vpSubscribe!!.visibility = View.GONE
                                }
                            }
                            else{
                                binding?.vpSubscribe!!.visibility = View.GONE
                            }
                        }
                    } else {

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
            alertDialog.setMessage(Html.fromHtml("Oops!! Internet connection is very slow. Please try again"))
            alertDialog.setCancelable(false)
            alertDialog.setPositiveButton("Try Again",
                DialogInterface.OnClickListener { dialog, which -> callAPI(s_date) })
            alertDialog.setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, which -> requireActivity().finish() })
            var alertDialog1 = alertDialog.create()
            alertDialog1.show()
        }
        catch (e: Exception){

        }
    }

    var onDialogClickListenerPositive : OnDialogClickListener.OnDialogClickCallback = object : OnDialogClickListener.OnDialogClickCallback{
        override fun onDialogClicked(dialog: DialogInterface?, position: Int) {
            dialog!!.dismiss()
            callAPI(s_date)
        }

    }

    var onDialogClickListenerNegative : OnDialogClickListener.OnDialogClickCallback = object : OnDialogClickListener.OnDialogClickCallback{
        override fun onDialogClicked(dialog: DialogInterface?, position: Int) {
            dialog!!.dismiss()
            requireActivity().finish()
        }

    }





    private fun readData() {
        Fitness.getHistoryClient(requireActivity(), GoogleSignIn.getLastSignedInAccount(requireActivity())!!)
            .readDailyTotal(DataType.TYPE_STEP_COUNT_DELTA)
            .addOnSuccessListener { dataSet ->
                val total = if (dataSet.isEmpty) 0 else dataSet.dataPoints[0].getValue(Field.FIELD_STEPS).asInt().toLong()
                Log.i("Home", "Total steps: $total")

            }
            .addOnFailureListener { e -> Log.w("Home", "There was a problem getting the step count.", e) }

        Fitness.getHistoryClient(requireActivity(), GoogleSignIn.getLastSignedInAccount(requireActivity())!!)
            .readDailyTotal(DataType.TYPE_BASAL_METABOLIC_RATE)
            .addOnSuccessListener { dataSet ->
                val total = if (dataSet.isEmpty) 0 else dataSet.dataPoints[0].getValue(Field.FIELD_CALORIES)
                Log.i("Home", "Total calorie: $total")

            }
            .addOnFailureListener { e -> Log.w("Home", "There was a problem getting the step count.", e) }

        Fitness.getHistoryClient(requireActivity(), GoogleSignIn.getLastSignedInAccount(requireActivity())!!)
            .readDailyTotal(DataType.TYPE_MOVE_MINUTES)
            .addOnSuccessListener { dataSet ->
                val total: Int = if (dataSet.isEmpty) 0 else dataSet.dataPoints[0].getValue(Field.FIELD_DURATION).asInt()
                if(!UtilMethod.instance.isStringNullOrNot(s_current_weight)){
                    var current_weight: Float = s_current_weight!!.toFloat()
                    if(current_weight>0){
                        var cal : Float = 2.8f * 3.5f * current_weight/200
                        binding?.tvCalorie?.setText(UtilMethod.instance.getFormatedAmountString(cal))
                    }

                }
                binding?.fitnessLayout!!.visibility = View.VISIBLE
                binding?.fitnessView!!.visibility = View.VISIBLE
                binding?.tvTime?.setText(UtilMethod.instance.getTime(total))

            }
            .addOnFailureListener { e -> Log.w("Home", "There was a problem getting the step count.", e) }


        Fitness.getHistoryClient(requireActivity(), GoogleSignIn.getLastSignedInAccount(requireActivity())!!)
            .readDailyTotal(DataType.TYPE_DISTANCE_DELTA)
            .addOnSuccessListener { dataSet ->
                val total : Float = if (dataSet.isEmpty) 0f else dataSet.dataPoints[0].getValue(Field.FIELD_DISTANCE).asFloat()
                Log.i("Home", "Total Distance: $total")
                val mile : Float = total * 0.000621371192f
                binding?.tvDistance?.setText(UtilMethod.instance.getFormatedAmountString(mile))

            }
            .addOnFailureListener { e -> Log.w("Home", "There was a problem getting the step count.", e) }
    }


    override fun onClick(v: View?) {
        if(v == binding?.ivWaterIntakePlus){
            var fragmentTransaction : FragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
            var fragment : Fragment = WaterIntakeFragment()
            var bun : Bundle = Bundle()
            bun.putString("date", s_date)
            fragment.arguments = bun
            fragmentTransaction?.replace(R.id.container_home, fragment)
            fragmentTransaction?.addToBackStack(null)
            fragmentTransaction?.commit()
        }
        else if(v == binding?.ivCalorieEatenPlus){
            var fragmentTransaction : FragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
            var fragment : Fragment = CalorieIntakeFragment()
            var bundle : Bundle = Bundle()
            bundle.putString("date", s_date)
            fragment.arguments = bundle
            fragmentTransaction?.replace(R.id.container_home, fragment)
            fragmentTransaction?.addToBackStack(null)
            fragmentTransaction?.commit()
        }
        else if(v == binding?.ivCalorieBurnPlus){
            var fragmentTransaction : FragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
            var fragment : Fragment = CalorieBurnFragment()
            var bundle : Bundle = Bundle()
            bundle.putString("date", s_date)
            fragment.arguments = bundle
            fragmentTransaction?.replace(R.id.container_home, fragment)
            fragmentTransaction?.addToBackStack(null)
            fragmentTransaction?.commit()
        }
        else if(v == binding?.tvTestimonialsViewall){
            var fragmentTransaction : FragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
            var fragment : Fragment = ViewAllTestinomialsFragment()
            fragmentTransaction?.replace(R.id.container_home, fragment)
            fragmentTransaction?.addToBackStack(null)
            fragmentTransaction?.commit()
        }
        else if(v == binding?.tvBlogViewall){
            var fragmentTransaction : FragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
            var fragment : Fragment = ViewAllBlogFragment()
            fragmentTransaction?.replace(R.id.container_home, fragment)
            fragmentTransaction?.addToBackStack(null)
            fragmentTransaction?.commit()
        }
        else if(v == binding?.llDate){
            var calendar : Calendar = Calendar.getInstance()
            var date1 : String = ""
            if(!binding?.tvDate!!.text.toString().equals("Today", true)){
                calendar.time = UtilMethod.instance.getDate13(binding?.tvDate!!.text.toString())
            }
            var year : Int = calendar.get(Calendar.YEAR)
            var month : Int  = calendar.get(Calendar.MONTH)
            var day : Int  = calendar.get(Calendar.DAY_OF_MONTH)
            datePickerDialog = DatePickerDialog(requireActivity(), DatePickerDialog.OnDateSetListener
            { view, year, monthOfYear, dayOfMonth ->
                var s1 : String = ""+year
                s_date = UtilMethod.instance.getDate12(s1+"-"+UtilMethod.instance.getValue(monthOfYear+1)+"-"+dayOfMonth)
                binding?.tvDate!!.setText(""+s_date)
                callAPI(s_date)
            }, year, month, day)
            datePickerDialog!!.datePicker.maxDate = System.currentTimeMillis()
            datePickerDialog!!.show()
        } else if(v == binding?.ivMenu){

            (activity as LeftSideMenuActivity).closeDrawer1()
        }
    }
}