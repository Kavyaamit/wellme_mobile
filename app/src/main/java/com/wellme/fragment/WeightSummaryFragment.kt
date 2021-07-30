package com.wellme.fragment

import android.app.AlertDialog
import android.app.Dialog
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
import android.view.Window
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.wellme.R
import com.wellme.databinding.FragmentWeightSummaryBinding
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
import java.util.*

class WeightSummaryFragment : Fragment(), View.OnClickListener{
    var binding : FragmentWeightSummaryBinding? = null
    var regular : Typeface? = null
    var userDTO : UserDTO ? = null
    var s_weight : String? = ""
    var s_height : String? = ""
    var total_height : Float = 0f
    var weight : Float = 0f
    var current_weight : Float = 0f
    var s_access_token : String? = ""
    var bmi_amount : Double = 0.0
    var target_type : String = "Easy"
    var date : Date? = null
    var ideal_bmi :Float = 18.5f


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_weight_summary, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        regular = ResourcesCompat.getFont(requireContext(), R.font.poppins_regular)
        initView()
    }

    fun initView(){
        s_access_token = UtilMethod.instance.getAccessToken(requireContext())
        date = Date()
        userDTO = UtilMethod.instance.getUser(requireContext())
        if(userDTO!=null){
            s_height = userDTO!!.initial_logged_height
            s_weight = userDTO!!.current_weight

        }
        if(!UtilMethod.instance.isStringNullOrNot(s_height) && !UtilMethod.instance.isStringNullOrNot(s_weight)){
            if(s_height!!.contains(".")){
                total_height = s_height!!.toFloat()

            }
            else{
                total_height = s_height!!.toInt() * 1.0f
            }

            if(s_weight!!.contains(".")){
                weight = s_weight!!.toFloat()
                current_weight = s_weight!!.toFloat()

            }
            else{
                weight = s_weight!!.toInt() * 1.0f
                current_weight = s_weight!!.toInt() * 1.0f
            }
            total_height = total_height/100
            total_height*=total_height
            Log.v("Total Height", "==> "+total_height)
            bmi_amount = weight /  total_height.toDouble()
            binding?.tvBmiAmount!!.setText(""+UtilMethod.instance.getFormatedDoubleAmountString(bmi_amount))
            binding?.tvIdealBmiAmount!!.setText(""+UtilMethod.instance.getFormatedAmountString(ideal_bmi))
            binding?.tvWeight!!.setText(UtilMethod.instance.getFormatedAmountString(weight)+" "+requireContext().resources.getString(R.string.kg))



            setIdealWeight()




        }
        binding?.bmiTitle?.setTypeface(regular)
        binding?.idealWeightTitle?.setTypeface(regular)
        binding?.submit?.setTypeface(regular)
        binding?.tvHeader?.setTypeface(regular)
        binding?.tvTargetTime?.setTypeface(regular)
        binding?.tvTargetWeight?.setTypeface(regular)
        binding?.tvTargetWeightAmount?.setTypeface(regular)
        binding?.tvIdealBmiAmount?.setTypeface(regular)
        binding?.tvTargetWeightTime?.setTypeface(regular)
        binding?.tvWeight?.setTypeface(regular)
        binding?.tvBmiAmount?.setTypeface(regular)
        binding?.tvIdealWeight?.setTypeface(regular)
        binding?.ivPlus!!.setOnClickListener(this)
        binding?.ivMinus!!.setOnClickListener(this)
        binding?.tvTargetTime?.setOnClickListener(this)
        binding?.submit?.setOnClickListener(this)
        binding?.tvIdealBmiTitle?.setTypeface(regular)
        binding?.tvCurrentWeightTitle?.setTypeface(regular)
        binding?.tvCurrentWeight?.setTypeface(regular)


    }


    override fun onClick(v: View?) {
        if(v == binding?.ivPlus){
            weight+=0.5f
            binding?.tvWeight!!.setText(UtilMethod.instance.getFormatedAmountString(weight)+" "+requireContext().resources.getString(R.string.kg))
            getData()
        }
        else if(v == binding?.ivMinus){
            if(weight>0){
                weight-=0.5f
            }
            binding?.tvWeight!!.setText(UtilMethod.instance.getFormatedAmountString(weight)+" "+requireContext().resources.getString(R.string.kg))
            getData()
        }
        else if(v == binding?.tvTargetTime){
            setDialog()
        }
        else if(v == binding?.submit){
            var s1 : String? = UtilMethod.instance.getDateFormat(date)
            callUpdateTargetWeightTask(s1)
        }
    }

    fun callAPI(){
        var s1 : String? = UtilMethod.instance.getDateFormat(date)
        callUpdateTargetWeightTask(s1)
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
            alertDialog.setNegativeButton(context!!.resources!!.getString(R.string.network_cancel), DialogInterface.OnClickListener { dialog, which ->  })
            var alertDialog1 = alertDialog.create()
            alertDialog1.show()
        }
        catch (e: Exception){

        }
    }

    fun callUpdateTargetWeightTask(s1 : String?){

        var progressDialog : ProgressDialog = ProgressDialog.show(requireContext(), "", "")
        progressDialog.setContentView(R.layout.progress_loader)
        progressDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        progressDialog.setCancelable(false)

        val service = Retrofit.Builder()
            .baseUrl(""+AppConstants.BASE_URL_NEW)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(AppService::class.java)

        service.updateTargetWeight("Bearer "+s_access_token, ""+weight, s1, target_type).enqueue(object :
            Callback<ResponseBody> {

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                progressDialog.dismiss()
                t.printStackTrace()
                dialogForCheckNetworkError()
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

                                    var fragmentTransaction : FragmentTransaction? = activity!!.supportFragmentManager.beginTransaction()
                                    var fragment : Fragment = FitnessResultFragment()
                                    fragmentTransaction?.replace(R.id.container_splash, fragment)
                                    fragmentTransaction?.commit()


                            }
                        else{

                        }

                    }
                }

            }
        })
    }

    fun setDialog(){
        var state_dialog : Dialog = Dialog(requireContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        state_dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        state_dialog!!.setContentView(R.layout.dialog_target_type)
        state_dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        state_dialog!!.show()
        var ll_easy : LinearLayout = state_dialog!!.findViewById(R.id.ll_easy)
        var ll_medium : LinearLayout = state_dialog!!.findViewById(R.id.ll_medium)
        var ll_hard : LinearLayout = state_dialog!!.findViewById(R.id.ll_hard)
        var ll_very_hard : LinearLayout = state_dialog!!.findViewById(R.id.ll_very_hard)
        var tv_easy : TextView = state_dialog!!.findViewById(R.id.tv_easy)
        var tv_easy_time : TextView = state_dialog!!.findViewById(R.id.tv_easy_time)
        var tv_medium : TextView = state_dialog!!.findViewById(R.id.tv_medium)
        var tv_medium_time : TextView = state_dialog!!.findViewById(R.id.tv_medium_time)
        var tv_hard : TextView = state_dialog!!.findViewById(R.id.tv_hard)
        var tv_hard_time : TextView = state_dialog!!.findViewById(R.id.tv_hard_time)
        var tv_very_hard : TextView = state_dialog!!.findViewById(R.id.tv_very_hard)
        var tv_very_hard_time : TextView = state_dialog!!.findViewById(R.id.tv_very_hard_time)
        if(regular!=null){
            tv_easy.setTypeface(regular)
            tv_easy_time.setTypeface(regular)
            tv_medium.setTypeface(regular)
            tv_medium_time.setTypeface(regular)
            tv_hard.setTypeface(regular)
            tv_hard_time.setTypeface(regular)
            tv_very_hard.setTypeface(regular)
            tv_very_hard_time.setTypeface(regular)
        }
        ll_easy.setOnClickListener(View.OnClickListener {view ->
            state_dialog!!.dismiss()
            target_type = ""+requireContext().resources.getString(R.string.easy)
            binding?.tvTargetTime!!.setText(target_type)
            getData()
        })

        ll_medium.setOnClickListener(View.OnClickListener {view ->
            state_dialog!!.dismiss()
            target_type = ""+requireContext().resources.getString(R.string.medium)
            binding?.tvTargetTime!!.setText(target_type)
            getData()
        })

        ll_hard.setOnClickListener(View.OnClickListener {view ->
            state_dialog!!.dismiss()
            target_type = ""+requireContext().resources.getString(R.string.hard)
            binding?.tvTargetTime!!.setText(target_type)
            getData()
        })

        ll_very_hard.setOnClickListener(View.OnClickListener {view ->
            state_dialog!!.dismiss()
            target_type = ""+requireContext().resources.getString(R.string.very_hard)
            binding?.tvTargetTime!!.setText(target_type)
            getData()
        })

    }


    fun getData(){
        var diff : Int = -1
        var difference_weight : Float = 0.0f

        if(current_weight < weight ){
            diff = -1
        }
        else if(current_weight > weight){
            diff = 1
        }
        else{
            diff = 0
        }

        if(diff == -1){
            difference_weight = weight - current_weight
            binding?.tvTargetWeightAmount!!.setText(requireContext().resources.getString(R.string.target_weight_amount)+" gain "+UtilMethod.instance.getFormatedAmountString(difference_weight)+" Kg?")
            binding?.tvTargetWeightAmount!!.visibility = View.VISIBLE
            binding?.tvTargetWeightTime!!.visibility = View.VISIBLE
        }
        else if(diff == 1){
            difference_weight = current_weight - weight
            binding?.tvTargetWeightAmount!!.setText(requireContext().resources.getString(R.string.target_weight_amount)+" lose "+UtilMethod.instance.getFormatedAmountString(difference_weight)+" Kg?")
            binding?.tvTargetWeightAmount!!.visibility = View.VISIBLE
            binding?.tvTargetWeightTime!!.visibility = View.VISIBLE
        }
        else{
            binding?.tvTargetWeightAmount!!.visibility = View.GONE
            binding?.tvTargetWeightTime!!.visibility = View.GONE
        }

        binding?.tvTargetWeightTime!!.setText(requireContext().resources.getString(R.string.target_weight_time)+" "+getTime(difference_weight))


    }

    fun getTime(differenceAmt : Float) : String? {
        var s1 : String? = ""
        var total_time : Float = 0.0f
        if(target_type.equals("Easy", true)){
            total_time = differenceAmt/0.25f
        }
        else if(target_type.equals("Medium", true)){
            total_time = differenceAmt/0.50f
        }
        else if(target_type.equals("Hard", true)){
            total_time = differenceAmt/0.75f
        }
        else if(target_type.equals("Very Hard", true)){
            total_time = differenceAmt/1.00f
        }
        var day : Int = (total_time * 7).toInt()
        var cal : Calendar = Calendar.getInstance()
        cal!!.add(Calendar.DAY_OF_YEAR, day)
        date = cal.time

        if(total_time > 4){
            var month : Int = total_time.toInt() / 4
            var remain_day : Int = ((total_time * 7) % 30).toInt()
            var s2 : String = ""+month.toInt()
            var s3: String = ""+remain_day.toInt()
            s1 = s2+" month and "+s3+" days"
        }
        else if(total_time<1){
            var remain_day : Int = ((total_time * 7) % 30).toInt()
            var s3: String = ""+remain_day.toInt()
            s1 = s3+" days"
        }
        else{
            var s2: String = ""+total_time.toInt()
            s1 = s2+" weeks"
        }

        //s1 = UtilMethod.instance.getFormatedAmountString(total_time)
        return s1
    }


    fun setIdealWeight(){

        var s_height : String? = s_height
        var s_gender : String? = userDTO?.gender
        var s_age : String? = userDTO?.age
        var height : Float? = 0f
        var age : Int? =0
        var idealWeight : Float = 0f


        if(!UtilMethod.instance.isStringNullOrNot(s_height)){
            height = s_height!!.toFloat()
        }
        if(!UtilMethod.instance.isStringNullOrNot(s_age)){
            age = s_age!!.toInt()
        }


        idealWeight = getIdealWeight(age, height!!, s_gender)

        binding?.tvIdealWeight?.setText(UtilMethod.instance.getFormatedAmountString(idealWeight)+"kg")
        binding?.tvCurrentWeight?.setText(UtilMethod.instance.getFormatedAmountString(current_weight)+" kg")


            binding?.tvIdealWeight?.setText(UtilMethod.instance.getFormatedAmountString(idealWeight)+" "+requireContext().resources.getString(R.string.kg))


    }

    fun getIdealWeight(a1 : Int?, h1 : Float, g1 : String?) : Float{
        var f1 : Float = 0f
        var ideal_height : Float = 150f

        if(h1 > ideal_height){
            var remain_height : Float = h1 - ideal_height
            var inch : Float = remain_height / 2.54f
            if(g1.equals("male", true)){
                f1 = 56.2f + 1.41f * inch
            }
            else if(g1.equals("female", true)){
                f1 = 53.1f + 1.36f * inch
            }
        }
        else if(h1 == ideal_height){
            if(g1.equals("male", true)){
                f1 = 56.2f
            }
            else if(g1.equals("female", true)){
                f1 = 53.1f
            }
        }
        else{
            if(g1.equals("male", true)){
                if(a1 == 1){
                    f1 = 8.4f
                }
                else if(a1 == 2){
                    f1 = 10.1f
                }
                else if(a1 == 3){
                    f1 = 11.8f
                }
                else if(a1 == 4){
                    f1 = 13.5f
                }
                else if(a1 == 5){
                    f1 = 14.8f
                }
                else if(a1 == 6){
                    f1 = 16.3f
                }
                else if(a1 == 7){
                    f1 = 18.0f
                }
                else if(a1 == 8){
                    f1 = 19.7f
                }
                else if(a1 == 9){
                    f1 = 21.5f
                }
                else if(a1 == 10){
                    f1 = 23.5f
                }
                else if(a1 == 11){
                    f1 = 10.1f
                }
                else if(a1 == 12){
                    f1 = 10.1f
                }
                else if(a1 == 13){
                    f1 = 10.1f
                }
                else if(a1 == 14){
                    f1 = 10.1f
                }
                else if(a1 == 15){
                    f1 = 10.1f
                }
                else if(a1 == 16){
                    f1 = 10.1f
                }
                else if(a1 == 17){
                    f1 = 10.1f
                }
                else if(a1 == 18){
                    f1 = 10.1f
                }
                else if(a1 == 19){
                    f1 = 10.1f
                }
                else if(a1 == 20){
                    f1 = 10.1f
                }
                else{
                    f1 = 56.2f
                }
            }
            else if(g1.equals("female", true)){
                if(a1 == 1){
                    f1 = 7.8f
                }
                else if(a1 == 2){
                    f1 = 9.6f
                }
                else if(a1 == 3){
                    f1 = 11.2f
                }
                else if(a1 == 4){
                    f1 = 12.9f
                }
                else if(a1 == 5){
                    f1 = 14.5f
                }
                else if(a1 == 6){
                    f1 = 16.0f
                }
                else if(a1 == 7){
                    f1 = 17.6f
                }
                else if(a1 == 8){
                    f1 = 19.4f
                }
                else if(a1 == 9){
                    f1 = 21.3f
                }
                else if(a1 == 10){
                    f1 = 23.6f
                }
                else if(a1 == 11){
                    f1 = 10.1f
                }
                else if(a1 == 12){
                    f1 = 10.1f
                }
                else if(a1 == 13){
                    f1 = 10.1f
                }
                else if(a1 == 14){
                    f1 = 10.1f
                }
                else if(a1 == 15){
                    f1 = 10.1f
                }
                else if(a1 == 16){
                    f1 = 10.1f
                }
                else if(a1 == 17){
                    f1 = 10.1f
                }
                else if(a1 == 18){
                    f1 = 10.1f
                }
                else if(a1 == 19){
                    f1 = 10.1f
                }
                else if(a1 == 20){
                    f1 = 10.1f
                }
                else{
                    f1 = 53.1f
                }
            }

        }

        return f1
    }

}