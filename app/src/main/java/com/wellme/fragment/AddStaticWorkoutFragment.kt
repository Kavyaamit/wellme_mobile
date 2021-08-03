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
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.wellme.LeftSideMenuActivity
import com.wellme.R
import com.wellme.databinding.FragmentAddStaticWorkoutBinding
import com.wellme.dto.UserDTO
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


class AddStaticWorkoutFragment : Fragment(), View.OnClickListener, AdapterView.OnItemSelectedListener {
    var binding : FragmentAddStaticWorkoutBinding? = null
    var regular : Typeface? = null
    var typeList : ArrayList<String> = ArrayList()
    var exercise_id : String? = ""
    var id : String? = ""
    var exercise_name : String? = ""
    var s_met : String? = ""
    var bundle : Bundle? = null
    var s_speed : String? = ""
    var s_distance : String? = ""
    var s_cal : String? = ""
    var s_time : String? = ""
    var met : Float = 0f
    var speed : Float = 0f
    var distance : Float = 0f
    var time : Int = 0
    var is_speed : Boolean = false
    var is_distance : Boolean = false
    var is_time : Boolean = false
    var s_access_token : String? = ""
    var userDTO : UserDTO? = null
    var s_current_weight : String? = ""
    var activity : Activity? = null



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_static_workout, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        regular = ResourcesCompat.getFont(requireContext(), R.font.poppins_regular)
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
        s_access_token = UtilMethod.instance.getAccessToken(requireContext())
        userDTO = UtilMethod.instance.getUser(requireContext())
        if(userDTO!=null){
            s_current_weight = userDTO!!.current_weight
        }
        binding?.btnWorkout!!.setTypeface(regular)
        binding?.header!!.setTypeface(regular)
        binding?.tvCalorieBurnUnit!!.setTypeface(regular)
        binding?.tvDistanceUnit!!.setTypeface(regular)
        binding?.tvOr!!.setTypeface(regular)
        binding?.tvSpeedUnit!!.setTypeface(regular)
        binding?.tvTimeUnit!!.setTypeface(regular)
        binding?.etCalorieBurn!!.setTypeface(regular)
        binding?.etDistance!!.setTypeface(regular)
        binding?.etSpeed!!.setTypeface(regular)
        binding?.etTime!!.setTypeface(regular)
        binding?.etCalorieBurn!!.setTypeface(regular)
        binding?.btnWorkout!!.setOnClickListener(this)
        bundle = arguments
        if(bundle!=null){
            exercise_id = bundle!!.getString("exercise_id")
            exercise_name = bundle!!.getString("exercise_name")
            id = bundle!!.getString("id")
            s_met = bundle!!.getString("met")
            if(!UtilMethod.instance.isStringNullOrNot(exercise_name)){
                binding?.header!!.setText(exercise_name)
                if(exercise_name == "Walking"){
                    typeList.add("Slow")
                    typeList.add("Moderator")
                    typeList.add("Fast")
                    typeList.add("Custom")
                    binding?.llSpeed!!.visibility = View.VISIBLE
                    binding?.llDistance!!.visibility = View.VISIBLE
                    speed = 3.5f
                    binding?.etSpeed?.setText(UtilMethod.instance.getFormatedAmountString(speed))
                }
                else if(exercise_name == "Running"){
                    typeList.add("Slow")
                    typeList.add("Moderator")
                    typeList.add("Fast")
                    typeList.add("Custom")
                    binding?.llSpeed!!.visibility = View.VISIBLE
                    binding?.llDistance!!.visibility = View.VISIBLE
                    speed = 3.5f
                    binding?.etSpeed?.setText(UtilMethod.instance.getFormatedAmountString(speed))
                }
                else if(exercise_name == "Bicycling"){
                    binding?.llSpeed!!.visibility = View.GONE
                    binding?.llType!!.visibility = View.GONE
                    met = 7.5f
                }
                else{
                    typeList.add("Light")
                    typeList.add("Vigorous")
                    binding?.llSpeed!!.visibility = View.GONE
                    binding?.llDistance!!.visibility = View.GONE
                    met = 5.8f
                }
            }

                binding?.etSpeed!!.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(s: Editable?) {

                    }

                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

                    }

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                        if(exercise_name=="Walking" || exercise_name=="Running"){
                            if(is_speed) {
                                var s12: String = "" + s
                                s_distance = binding?.etDistance!!.text.toString()
                                s_time = binding?.etTime!!.text.toString()
                                if (!UtilMethod.instance.isStringNullOrNot(s12)) {
                                    speed = s12!!.toFloat()
                                    if(exercise_name == "Walking"){
                                        met = 0.5f * speed
                                    }
                                    else if(exercise_name == "Running"){
                                        met = 0.95f * speed
                                    }

                                    if (!UtilMethod.instance.isStringNullOrNot(s_time)) {
                                        time = s_time!!.toInt()
                                        distance = speed * time / 60
                                        binding?.etDistance!!.setText(
                                            UtilMethod.instance.getFormatedAmountString(
                                                distance
                                            )
                                        )
                                    }

                                }
                                else{
                                    distance = 0f
                                    time = 0
                                    binding?.etTime!!.setText("")
                                    binding?.etDistance!!.setText("")
                                }
                            }
                        }
                        else{

                        }







                    }
                })

            binding?.etTime!!.addTextChangedListener(object : TextWatcher{
                override fun afterTextChanged(s: Editable?) {

                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    var s12 = ""+s
                    if(exercise_name=="Walking" || exercise_name=="Running"){
                        if(is_time) {
                            if (!UtilMethod.instance.isStringNullOrNot(s12)) {
                                if (speed > 0) {
                                    time = s12.toInt()
                                    distance = speed * time / 60
                                    binding?.etDistance!!.setText(
                                        UtilMethod.instance.getFormatedAmountString(
                                            distance
                                        )
                                    )

                                }
                            } else {
                                time = 0
                                distance = 0f
                                binding?.etDistance!!.setText(
                                    UtilMethod.instance.getFormatedAmountString(
                                        distance
                                    )
                                )
                            }
                        }
                    }
                    else if(exercise_name == "Bicycling"){
                        s_distance = binding?.etDistance!!.text.toString()
                        if(!UtilMethod.instance.isStringNullOrNot(s12)){
                            time = s12.toInt()
                            if(!UtilMethod.instance.isStringNullOrNot(s_distance)){
                                distance = s_distance!!.toFloat()
                            }
                            else{
                                distance = 1.0f
                            }
                            var speed1 : Float = distance * 60 /time
                            if(speed1<=9){
                                met = 3.5f
                            }
                            else if(speed1<15){
                                met = 5.8f
                            }
                            else if(speed1<20){
                                met = 6.8f
                            }
                            else if(speed1<23){
                                met = 8.0f
                            }
                            else if(speed1<26){
                                met = 10.0f
                            }
                            else if(speed1<31){
                                met = 12.0f
                            }
                            else if(speed1>=31){
                                met = 15.8f
                            }

                        }
                        else{

                        }
                    }

                }

            })

            binding?.etSpeed!!.setOnFocusChangeListener(object : View.OnFocusChangeListener{
                override fun onFocusChange(v: View?, hasFocus: Boolean) {
                    is_speed = hasFocus
                }

            })

            binding?.etDistance!!.setOnFocusChangeListener(object : View.OnFocusChangeListener{
                override fun onFocusChange(v: View?, hasFocus: Boolean) {
                    is_distance = hasFocus
                    if(hasFocus){
                        is_speed = false
                    }
                }

            })

            binding?.etTime!!.setOnFocusChangeListener(object : View.OnFocusChangeListener{
                override fun onFocusChange(v: View?, hasFocus: Boolean) {
                        is_time = hasFocus
                        if(hasFocus){
                            is_speed = false
                        }
                }

            })

            binding?.etDistance!!.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {

                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    var s12 : String  = ""+s
                    if(exercise_name=="Walking" || exercise_name=="Running") {
                        if (is_distance) {
                            s_time = binding?.etTime?.text.toString()
                            if (speed > 0) {
                                if (!UtilMethod.instance.isStringNullOrNot(s12) && !UtilMethod.instance.isStringNullOrNot(
                                        s_time
                                    )
                                ) {
                                    distance = s12.toFloat()
                                    time = s_time!!.toInt()
                                    speed = distance * 60 / time
                                    binding?.etSpeed!!.setText(
                                        UtilMethod.instance.getFormatedAmountString(
                                            speed
                                        )
                                    )
                                } else if (!UtilMethod.instance.isStringNullOrNot(s12)) {
                                    distance = s12.toFloat()
                                    time = (distance * 60 / speed).toInt()
                                    binding?.etTime!!.setText("" + time)
                                } else {
                                    distance = 0f
                                    time = 0
                                    binding?.etTime!!.setText("")
                                }
                            }

                        }
                    }
                    else if(exercise_name == "Bicycling"){
                        s_time = binding?.etTime!!.text.toString()
                        if(!UtilMethod.instance.isStringNullOrNot(s12)){
                            distance = s12.toFloat()
                            if(!UtilMethod.instance.isStringNullOrNot(s_time)){
                                time = s_time!!.toInt()
                            }
                            else{
                                time = 1
                            }
                            var speed1 : Float = distance * 60 /time
                            if(speed1<=9){
                                met = 3.5f
                            }
                            else if(speed1<15){
                                met = 5.8f
                            }
                            else if(speed1<20){
                                met = 6.8f
                            }
                            else if(speed1<23){
                                met = 8.0f
                            }
                            else if(speed1<26){
                                met = 10.0f
                            }
                            else if(speed1<31){
                                met = 12.0f
                            }
                            else if(speed1>=31){
                                met = 15.8f
                            }

                        }
                        else{

                        }
                    }
                }
            })



        }

        if(typeList!=null) {

            val aa = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, typeList)
            aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding?.spType!!.adapter = aa
            binding?.spType!!.setOnItemSelectedListener(this)
        }


    }

    fun callAPI(){
        s_speed = binding?.etSpeed!!.text.toString()
        s_distance = binding?.etDistance!!.text.toString()
        s_time = binding?.etTime!!.text.toString()
        s_cal = binding?.etCalorieBurn!!.text.toString()
        if(UtilMethod.instance.isStringNullOrNot(s_cal) && UtilMethod.instance.isStringNullOrNot(s_time)){
            UtilMethod.instance.dialogOK(requireContext(), "", "Please enter calories or time", "Ok", false)
        }
        else if(!UtilMethod.instance.isStringNullOrNot(s_cal)){
            if(UtilMethod.instance.isStringNullOrNot(s_time)){
                s_time = "0"
            }
            if(UtilMethod.instance.isStringNullOrNot(s_distance)){
                s_distance = "0"
            }
            if(UtilMethod.instance.isStringNullOrNot(s_speed)){
                s_speed = "0"
            }
            callCreateProfileTask(""+s_cal)
        }
        else{
            redirectAPI()
        }
    }



    override fun onClick(v: View?) {
        if(v == binding?.btnWorkout){
            callAPI()
        }
        else if(v == binding?.back){
            requireActivity().onBackPressed()
        }
    }

    fun redirectAPI(){
        var curr_wei : Float = 0.0f
        var kcal : Float = 0.0f
        if(!UtilMethod.instance.isStringNullOrNot(s_speed)){
            speed = s_speed!!.toFloat()
        }
        if(!UtilMethod.instance.isStringNullOrNot(s_distance)){
            distance = s_distance!!.toFloat()
        }
        if(!UtilMethod.instance.isStringNullOrNot(s_time)){
            time = s_time!!.toInt()
        }
        if(!UtilMethod.instance.isStringNullOrNot(s_current_weight)){
            curr_wei = s_current_weight!!.toFloat()
        }

        kcal = (met * 3.5f * curr_wei * time)/200

        callCreateProfileTask(""+kcal)

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
                DialogInterface.OnClickListener { dialog, which -> callAPI() })
            alertDialog.setNegativeButton(context!!.resources!!.getString(R.string.network_cancel), DialogInterface.OnClickListener { dialog, which ->  dialog!!.dismiss()})
            var alertDialog1 = alertDialog.create()
            alertDialog1.show()
        }
        catch (e: Exception){

        }
    }


    fun callCreateProfileTask(cal : String){

        var progressDialog : ProgressDialog = ProgressDialog.show(requireContext(), "", "")
        progressDialog.setContentView(R.layout.progress_loader)
        progressDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        progressDialog.setCancelable(false)

        val service = Retrofit.Builder()
            .baseUrl(""+ AppConstants.BASE_URL_NEW)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(AppService::class.java)


        service.callAddWorkout("Bearer "+s_access_token, "0", "0", ""+s_time, cal, cal, ""+s_speed, ""+s_distance, "0", ""+exercise_name).enqueue(object :
            Callback<ResponseBody> {

            /* The HTTP call failed. This method is run on the main thread */
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.d("TAG_", "An error happened!")
                progressDialog.dismiss()
                dialogForCheckNetworkError()
            }

            /* The HTTP call was successful, we should still check status code and response body
             * on a production app. This method is run on the main thread */
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                /* This will print the response of the network call to the Logcat */
                progressDialog.dismiss()
                var code : Int = response.raw().code()
                Log.v("REquest ", "==> "+response.raw().body())
                Log.v("Response Code ", " ==> "+code+" "+response.raw())
                if(code >199 && code < 300) {
                    var response1 : String = response.body()!!.string()
                    if(!UtilMethod.instance.isStringNullOrNot(response1)){
                        Log.v("REsponse == > ", "=="+response1)
                        var obj : JSONObject = JSONObject(response1)
                        var success : Int = obj.getInt("success")
                        if(success == 200){
                            requireActivity().onBackPressed()
                        }
                        else{

                        }

                    }
                }

            }
        })
    }


    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        var s_type : String = typeList.get(position)
        if(!UtilMethod.instance.isStringNullOrNot(exercise_name)){
            if(exercise_name == "Swimming"){

                if(s_type.equals("Light", true)){
                    met = 5.8f
                }
                else{
                    met = 9.8f
                }
            }
            else if(exercise_name == "Walking"){
                if(s_type.equals("Slow", true)){
                    met = 0.5f * 3.5f
                    is_speed = true
                    speed = 3.5f
                    binding?.etSpeed!!.isFocusable = false
                    binding?.etSpeed!!.isFocusableInTouchMode = false
                }
                else if(s_type.equals("Moderator", true)){
                    met = 0.5f * 5f
                    speed = 5.0f
                    is_speed = true
                    binding?.etSpeed!!.isFocusable = false
                    binding?.etSpeed!!.isFocusableInTouchMode = false
                }
                else if(s_type.equals("Fast", true)){
                    met = 0.5f * 7.5f
                    speed = 7.5f
                    is_speed = true
                    binding?.etSpeed!!.isFocusable = false
                    binding?.etSpeed!!.isFocusableInTouchMode = false

                }
                else if(s_type.equals("Custom", true)){
                    binding?.etSpeed!!.isFocusable = true
                    binding?.etSpeed!!.isFocusableInTouchMode = true
                    binding?.etSpeed!!.requestFocus()
                }
                if(is_time){
                    binding!!.etDistance!!.requestFocus()
                }
                if(is_distance){
                    binding!!.etTime!!.requestFocus()
                }
                binding?.etSpeed!!.setText(UtilMethod.instance.getFormatedAmountString(speed))
           }
            else if(exercise_name == "Running"){
                if(s_type.equals("Slow", true)){
                    met = 8.3f
                    speed = 8.0f
                    is_speed = true
                    binding?.etSpeed!!.isFocusable = false
                    binding?.etSpeed!!.isFocusableInTouchMode = false
                }
                else if(s_type.equals("Moderator", true)){
                    met = 11.8f
                    speed = 13.0f
                    is_speed = true
                    binding?.etSpeed!!.isFocusable = false
                    binding?.etSpeed!!.isFocusableInTouchMode = false
                }
                else if(s_type.equals("Fast", true)){
                    met = 16.0f
                    speed = 17.0f
                    is_speed = true
                    binding?.etSpeed!!.isFocusable = false
                    binding?.etSpeed!!.isFocusableInTouchMode = false
                }
                else if(s_type.equals("Custom", true)){
                    binding?.etSpeed!!.isFocusable = true
                    binding?.etSpeed!!.isFocusableInTouchMode = true
                    binding?.etSpeed!!.requestFocus()
                }

                if(is_time){
                    binding!!.etDistance!!.requestFocus()
                }
                if(is_distance){
                    binding!!.etTime!!.requestFocus()
                }
                binding?.etSpeed!!.setText(UtilMethod.instance.getFormatedAmountString(speed))
            }
        }
    }
}