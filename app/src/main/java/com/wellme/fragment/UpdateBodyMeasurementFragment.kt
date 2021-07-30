package com.wellme.fragment

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
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
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.wellme.LeftSideMenuActivity
import com.wellme.MainActivity
import com.wellme.R
import com.wellme.databinding.FragmentUpdateBodyMeasurementBinding
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

class UpdateBodyMeasurementFragment : Fragment(), View.OnClickListener{
    var binding : FragmentUpdateBodyMeasurementBinding? = null
    var regular : Typeface? = null
    var armLeftDialog : Dialog? = null
    var armRightDialog : Dialog? = null
    var bodyfatsDialog : Dialog? = null
    var muscleMassDialog : Dialog? = null
    var hipsDialog : Dialog? = null
    var thighDialog : Dialog? = null
    var calfDialog : Dialog? = null
    var chestDialog : Dialog? = null
    var waistDialog : Dialog? = null
    var userDTO : UserDTO? = null
    var s_arm_left : String? = "0"
    var s_arm_right : String? = "0"
    var s_body_fats : String? = "0"
    var s_muscles_mass : String? = "0"
    var s_hip : String? = "0"
    var s_waist : String? = "0"
    var s_calf : String? = "0"
    var s_thigh : String? = "0"
    var s_chest : String? = "0"
    var s_accesstoken : String? = ""
    var activity : Activity? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_update_body_measurement, container, false)
        return binding?.root
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        this.activity = activity
    }

    override fun onResume() {
        super.onResume()
        (activity as LeftSideMenuActivity).enableBottomBar()
        (activity as LeftSideMenuActivity).setActiveSection(4)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        regular = ResourcesCompat.getFont(requireContext(), R.font.poppins_regular)
        initView()
    }

    fun initView(){
        binding?.tvArmLeftText?.setTypeface(regular)
        binding?.tvArmRightText?.setTypeface(regular)
        binding?.tvBodyFats?.setTypeface(regular)
        binding?.tvBodyFatsText?.setTypeface(regular)
        binding?.tvBodyMeasurement?.setTypeface(regular)
        binding?.tvCalfText?.setTypeface(regular)
        binding?.tvChestText?.setTypeface(regular)
        binding?.tvHipsText?.setTypeface(regular)
        binding?.tvMuscleMassText?.setTypeface(regular)
        binding?.tvThighText?.setTypeface(regular)
        binding?.tvWaistText?.setTypeface(regular)
        binding?.tvChest?.setTypeface(regular)
        binding?.tvBodyFats?.setTypeface(regular)
        binding?.tvMusclesMass?.setTypeface(regular)
        binding?.tvHips?.setTypeface(regular)
        binding?.tvWaist?.setTypeface(regular)
        binding?.tvArmLeft?.setTypeface(regular)
        binding?.tvArmRight?.setTypeface(regular)
        binding?.tvThigh?.setTypeface(regular)
        binding?.tvCalf?.setTypeface(regular)
        binding?.llArmLeft?.setOnClickListener(this)
        binding?.llArmRight?.setOnClickListener(this)
        binding?.llBodyFats?.setOnClickListener(this)
        binding?.llCalf?.setOnClickListener(this)
        binding?.llChest?.setOnClickListener(this)
        binding?.llHips?.setOnClickListener(this)
        binding?.llMusclesMass?.setOnClickListener(this)
        binding?.llThigh?.setOnClickListener(this)
        binding?.llWaist?.setOnClickListener(this)
        binding?.ivBack?.setOnClickListener(this)
        s_accesstoken = UtilMethod.instance.getAccessToken(requireContext())
        setData()



    }

    fun setTextData(tv_title : TextView?, tv_val : TextView?, value : String?, title : String, sub_title : String?){
        if(!UtilMethod.instance.isStringNullOrNot(value)){
            var i1 : Int = value!!.toInt()
            if(i1>0){
                tv_val!!.visibility = View.VISIBLE
                tv_title!!.setText(title)
                setValue(tv_val, i1)
            }
            else{
                tv_val!!.visibility = View.GONE
                tv_title!!.setText(sub_title)
            }
        }
        else{
            tv_val!!.visibility = View.GONE
            tv_title!!.setText(sub_title)
        }
    }

    fun setData(){
        userDTO = UtilMethod.instance.getUser(requireContext())
        if(userDTO!=null){
            s_waist = userDTO!!.waist
            s_hip = userDTO!!.hip
            s_arm_left = userDTO!!.arm_left
            s_arm_right = userDTO!!.arm_reght
            s_body_fats = userDTO!!.body_fat
            s_calf = userDTO!!.calf
            s_chest = userDTO!!.chest
            s_muscles_mass = userDTO!!.muscle_mass
            s_thigh = userDTO!!.thigh

            setTextData(binding?.tvArmLeftText, binding?.tvArmLeft, s_arm_left, requireContext().resources.getString(R.string.your_arm_left), requireContext().resources.getString(R.string.click_here_to_add_arm_left))
            setTextData(binding?.tvArmRightText, binding?.tvArmRight, s_arm_right, requireContext().resources.getString(R.string.your_arm_right), requireContext().resources.getString(R.string.click_here_to_add_arm_right))
            setTextData(binding?.tvBodyFatsText, binding?.tvBodyFats, s_body_fats, requireContext().resources.getString(R.string.your_body_fat), requireContext().resources.getString(R.string.click_here_to_add_body_fat))
            setTextData(binding?.tvCalfText, binding?.tvCalf, s_calf, requireContext().resources.getString(R.string.your_calf), requireContext().resources.getString(R.string.click_here_to_add_calf))
            setTextData(binding?.tvChestText, binding?.tvChest, s_chest, requireContext().resources.getString(R.string.your_chest), requireContext().resources.getString(R.string.click_here_to_add_chest))
            setTextData(binding?.tvHipsText, binding?.tvHips, s_hip, requireContext().resources.getString(R.string.your_hips), requireContext().resources.getString(R.string.click_here_to_add_hips))
            setTextData(binding?.tvMuscleMassText, binding?.tvMusclesMass, s_muscles_mass, requireContext().resources.getString(R.string.your_muscle_mass), requireContext().resources.getString(R.string.click_here_to_add_muscle_mass))
            setTextData(binding?.tvThighText, binding?.tvThigh, s_thigh, requireContext().resources.getString(R.string.your_thigh), requireContext().resources.getString(R.string.click_here_to_add_thigh))
            setTextData(binding?.tvWaistText, binding?.tvWaist, s_waist, requireContext().resources.getString(R.string.your_waist), requireContext().resources.getString(R.string.click_here_to_add_waist))

        }
    }

    override fun onClick(v: View?) {
        if(v == binding?.llArmLeft){
            showArmLeftDialog()
        }
        else if(v == binding?.llArmRight){
            showArmRightDialog()
        }
        else if(v == binding?.llBodyFats){
            showBodyFatDialog()
        }
        else if(v == binding?.llMusclesMass){
            showMuscleMassDialog()
        }
        else if(v == binding?.llCalf){
            showCalfDialog()
        }
        else if(v == binding?.llChest){
            showChestDialog()
        }
        else if(v == binding?.llHips){
            showHipsDialog()
        }
        else if(v == binding?.llThigh){
            showThighDialog()
        }
        else if(v == binding?.llWaist){
            showWaistDialog()
        }else if(v == binding?.ivBack){
            requireActivity().onBackPressed()
        }

    }


    fun showArmLeftDialog(){
        var arm_left : Int = 0
        armLeftDialog = Dialog(requireContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        armLeftDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        armLeftDialog!!.setContentView(R.layout.dialog_update_bodymeasurement)
        armLeftDialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        armLeftDialog!!.show()
        var tv_header : TextView = armLeftDialog!!.findViewById(R.id.tv_header)
        var btn_update : Button = armLeftDialog!!.findViewById(R.id.btn_update)
        var tv_value : TextView = armLeftDialog!!.findViewById(R.id.tv_value)
        var iv_plus : ImageView = armLeftDialog!!.findViewById(R.id.iv_plus)
        var iv_minus : ImageView = armLeftDialog!!.findViewById(R.id.iv_minus)
        tv_header!!.setTypeface(regular)
        btn_update!!.setTypeface(regular)
        tv_value!!.setTypeface(regular)
        tv_header!!.setText("Arm Left (in cm)")
        if(!UtilMethod.instance.isStringNullOrNot(s_arm_left)){
            arm_left = s_arm_left!!.toInt()
            setValue(tv_value, arm_left)
        }
        iv_plus.setOnClickListener(View.OnClickListener {view ->
            arm_left+=1
            setValue(tv_value, arm_left)
        })

        iv_minus.setOnClickListener(View.OnClickListener {view ->
            if(arm_left>=1) {
                arm_left-= 1
                setValue(tv_value, arm_left)
            }
        })

        btn_update.setOnClickListener(View.OnClickListener {view ->
            s_arm_left = ""+arm_left
            armLeftDialog!!.dismiss()
            callUpdateBodyMeasurementTask()

        })
    }

    fun showArmRightDialog(){
        var arm_right : Int = 0
        armRightDialog = Dialog(requireContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        armRightDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        armRightDialog!!.setContentView(R.layout.dialog_update_bodymeasurement)
        armRightDialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        armRightDialog!!.show()
        var tv_header : TextView = armRightDialog!!.findViewById(R.id.tv_header)
        var btn_update : Button = armRightDialog!!.findViewById(R.id.btn_update)
        var tv_value : TextView = armRightDialog!!.findViewById(R.id.tv_value)
        var iv_plus : ImageView = armRightDialog!!.findViewById(R.id.iv_plus)
        var iv_minus : ImageView = armRightDialog!!.findViewById(R.id.iv_minus)
        tv_header!!.setTypeface(regular)
        btn_update!!.setTypeface(regular)
        tv_value!!.setTypeface(regular)
        tv_header!!.setText("Arm Right (in cm)")
        if(!UtilMethod.instance.isStringNullOrNot(s_arm_right)){
            arm_right = s_arm_right!!.toInt()
            setValue(tv_value, arm_right)
        }
        iv_plus.setOnClickListener(View.OnClickListener {view ->
            arm_right+=1
            setValue(tv_value, arm_right)
        })

        iv_minus.setOnClickListener(View.OnClickListener {view ->
            if(arm_right>=1) {
                arm_right-= 1
                setValue(tv_value, arm_right)
            }
        })

        btn_update.setOnClickListener(View.OnClickListener {view ->
            s_arm_right = ""+arm_right
            armRightDialog!!.dismiss()
            callUpdateBodyMeasurementTask()

        })


    }

    fun showBodyFatDialog(){
        var body_fat : Int =0
        bodyfatsDialog = Dialog(requireContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        bodyfatsDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        bodyfatsDialog!!.setContentView(R.layout.dialog_update_bodymeasurement)
        bodyfatsDialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        bodyfatsDialog!!.show()
        var tv_header : TextView = bodyfatsDialog!!.findViewById(R.id.tv_header)
        var btn_update : Button = bodyfatsDialog!!.findViewById(R.id.btn_update)
        var tv_value : TextView = bodyfatsDialog!!.findViewById(R.id.tv_value)
        var iv_plus : ImageView = bodyfatsDialog!!.findViewById(R.id.iv_plus)
        var iv_minus : ImageView = bodyfatsDialog!!.findViewById(R.id.iv_minus)
        tv_header!!.setTypeface(regular)
        btn_update!!.setTypeface(regular)
        tv_value!!.setTypeface(regular)
        tv_header!!.setText("Body Fats (in %)")
        if(!UtilMethod.instance.isStringNullOrNot(s_body_fats)){
            body_fat = s_body_fats!!.toInt()
            setValue(tv_value, body_fat)
        }

        iv_plus.setOnClickListener(View.OnClickListener {view ->
            if(body_fat<100) {
                body_fat += 1
                setValue(tv_value, body_fat)
            }
        })

        iv_minus.setOnClickListener(View.OnClickListener {view ->
            if(body_fat>=1) {
                body_fat-= 1
                setValue(tv_value, body_fat)
            }
        })

        btn_update.setOnClickListener(View.OnClickListener {view ->
            s_body_fats = ""+body_fat
            bodyfatsDialog!!.dismiss()
            callUpdateBodyMeasurementTask()

        })

    }

    fun showCalfDialog(){
        var calf : Int = 0
        calfDialog = Dialog(requireContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        calfDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        calfDialog!!.setContentView(R.layout.dialog_update_bodymeasurement)
        calfDialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        calfDialog!!.show()
        var tv_header : TextView = calfDialog!!.findViewById(R.id.tv_header)
        var btn_update : Button = calfDialog!!.findViewById(R.id.btn_update)
        var tv_value : TextView = calfDialog!!.findViewById(R.id.tv_value)
        var iv_plus : ImageView = calfDialog!!.findViewById(R.id.iv_plus)
        var iv_minus : ImageView = calfDialog!!.findViewById(R.id.iv_minus)
        tv_header!!.setTypeface(regular)
        btn_update!!.setTypeface(regular)
        tv_value!!.setTypeface(regular)
        tv_header!!.setText("Calf (in cm)")
        if(!UtilMethod.instance.isStringNullOrNot(s_calf)){
            calf = s_calf!!.toInt()
            setValue(tv_value, calf)
        }
        iv_plus.setOnClickListener(View.OnClickListener {view ->
            calf+=1
            setValue(tv_value, calf)
        })

        iv_minus.setOnClickListener(View.OnClickListener {view ->
            if(calf>=1) {
                calf-= 1
                setValue(tv_value, calf)
            }
        })

        btn_update.setOnClickListener(View.OnClickListener {view ->
            s_calf = ""+calf
            calfDialog!!.dismiss()
            callUpdateBodyMeasurementTask()

        })


    }

    fun showMuscleMassDialog(){
        var muscle_mass : Int = 0
        muscleMassDialog = Dialog(requireContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        muscleMassDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        muscleMassDialog!!.setContentView(R.layout.dialog_update_bodymeasurement)
        muscleMassDialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        muscleMassDialog!!.show()
        var tv_header : TextView = muscleMassDialog!!.findViewById(R.id.tv_header)
        var btn_update : Button = muscleMassDialog!!.findViewById(R.id.btn_update)
        var tv_value : TextView = muscleMassDialog!!.findViewById(R.id.tv_value)
        var iv_plus : ImageView = muscleMassDialog!!.findViewById(R.id.iv_plus)
        var iv_minus : ImageView = muscleMassDialog!!.findViewById(R.id.iv_minus)
        tv_header!!.setTypeface(regular)
        btn_update!!.setTypeface(regular)
        tv_value!!.setTypeface(regular)
        tv_header!!.setText("Muscles Mass (in %)")
        if(!UtilMethod.instance.isStringNullOrNot(s_muscles_mass)){
            muscle_mass = s_muscles_mass!!.toInt()
            setValue(tv_value, muscle_mass)
        }

        iv_plus.setOnClickListener(View.OnClickListener {view ->
            if(muscle_mass<100) {
                muscle_mass += 1
                setValue(tv_value, muscle_mass)
            }
        })

        iv_minus.setOnClickListener(View.OnClickListener {view ->
            if(muscle_mass>=1) {
                muscle_mass-= 1
                setValue(tv_value, muscle_mass)
            }
        })

        btn_update.setOnClickListener(View.OnClickListener {view ->
            s_muscles_mass = ""+muscle_mass
            muscleMassDialog!!.dismiss()
            callUpdateBodyMeasurementTask()

        })
    }

    fun showHipsDialog(){
        var hips : Int = 0
        hipsDialog = Dialog(requireContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        hipsDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        hipsDialog!!.setContentView(R.layout.dialog_update_bodymeasurement)
        hipsDialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        hipsDialog!!.show()
        var tv_header : TextView = hipsDialog!!.findViewById(R.id.tv_header)
        var btn_update : Button = hipsDialog!!.findViewById(R.id.btn_update)
        var tv_value : TextView = hipsDialog!!.findViewById(R.id.tv_value)
        var iv_plus : ImageView = hipsDialog!!.findViewById(R.id.iv_plus)
        var iv_minus : ImageView = hipsDialog!!.findViewById(R.id.iv_minus)
        tv_header!!.setTypeface(regular)
        btn_update!!.setTypeface(regular)
        tv_value!!.setTypeface(regular)
        tv_header!!.setText("Hips (in cm)")
        if(!UtilMethod.instance.isStringNullOrNot(s_hip)){
            hips = s_hip!!.toInt()
            setValue(tv_value, hips)
        }

        iv_plus.setOnClickListener(View.OnClickListener {view ->
            hips+=1
            setValue(tv_value, hips)
        })

        iv_minus.setOnClickListener(View.OnClickListener {view ->
            if(hips>=1) {
                hips-= 1
                setValue(tv_value, hips)
            }
        })

        btn_update.setOnClickListener(View.OnClickListener {view ->
            s_hip = ""+hips
            hipsDialog!!.dismiss()
            callUpdateBodyMeasurementTask()

        })


    }

    fun showChestDialog(){
        var chest : Int = 0
        chestDialog = Dialog(requireContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        chestDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        chestDialog!!.setContentView(R.layout.dialog_update_bodymeasurement)
        chestDialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        chestDialog!!.show()
        var tv_header : TextView = chestDialog!!.findViewById(R.id.tv_header)
        var btn_update : Button = chestDialog!!.findViewById(R.id.btn_update)
        var tv_value : TextView = chestDialog!!.findViewById(R.id.tv_value)
        var iv_plus : ImageView = chestDialog!!.findViewById(R.id.iv_plus)
        var iv_minus : ImageView = chestDialog!!.findViewById(R.id.iv_minus)
        tv_header!!.setTypeface(regular)
        btn_update!!.setTypeface(regular)
        tv_value!!.setTypeface(regular)
        tv_header!!.setText("Chest (in cm)")
        if(!UtilMethod.instance.isStringNullOrNot(s_chest)){
            chest = s_chest!!.toInt()
            setValue(tv_value, chest)
        }
        iv_plus.setOnClickListener(View.OnClickListener {view ->
            chest+=1
            setValue(tv_value, chest)
        })

        iv_minus.setOnClickListener(View.OnClickListener {view ->
            if(chest>=1) {
                chest-= 1
                setValue(tv_value, chest)
            }
        })

        btn_update.setOnClickListener(View.OnClickListener {view ->
            s_chest = ""+chest
            chestDialog!!.dismiss()
            callUpdateBodyMeasurementTask()

        })

    }

    fun showThighDialog(){
        var thigh : Int = 0
        thighDialog = Dialog(requireContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        thighDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        thighDialog!!.setContentView(R.layout.dialog_update_bodymeasurement)
        thighDialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        thighDialog!!.show()
        var tv_header : TextView = thighDialog!!.findViewById(R.id.tv_header)
        var btn_update : Button = thighDialog!!.findViewById(R.id.btn_update)
        var tv_value : TextView = thighDialog!!.findViewById(R.id.tv_value)
        var iv_plus : ImageView = thighDialog!!.findViewById(R.id.iv_plus)
        var iv_minus : ImageView = thighDialog!!.findViewById(R.id.iv_minus)
        tv_header!!.setTypeface(regular)
        btn_update!!.setTypeface(regular)
        tv_value!!.setTypeface(regular)
        tv_header!!.setText("Thigh (in cm)")
        if(!UtilMethod.instance.isStringNullOrNot(s_thigh)){
            thigh = s_thigh!!.toInt()
            setValue(tv_value, thigh)
        }

        iv_plus.setOnClickListener(View.OnClickListener {view ->
            thigh+=1
            setValue(tv_value, thigh)
        })

        iv_minus.setOnClickListener(View.OnClickListener {view ->
            if(thigh>=1) {
                thigh-= 1
                setValue(tv_value, thigh)
            }
        })

        btn_update.setOnClickListener(View.OnClickListener {view ->
            s_thigh = ""+thigh
            thighDialog!!.dismiss()
            callUpdateBodyMeasurementTask()

        })


    }

    fun showWaistDialog(){
        var waist : Int = 0
        waistDialog = Dialog(requireContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        waistDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        waistDialog!!.setContentView(R.layout.dialog_update_bodymeasurement)
        waistDialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        waistDialog!!.show()
        var tv_header : TextView = waistDialog!!.findViewById(R.id.tv_header)
        var btn_update : Button = waistDialog!!.findViewById(R.id.btn_update)
        var tv_value : TextView = waistDialog!!.findViewById(R.id.tv_value)
        var iv_plus : ImageView = waistDialog!!.findViewById(R.id.iv_plus)
        var iv_minus : ImageView = waistDialog!!.findViewById(R.id.iv_minus)
        tv_header!!.setTypeface(regular)
        btn_update!!.setTypeface(regular)
        tv_value!!.setTypeface(regular)
        tv_header!!.setText("Waist (in cm)")
        if(!UtilMethod.instance.isStringNullOrNot(s_waist)){
            waist = s_waist!!.toInt()
            setValue(tv_value, waist)
        }

        iv_plus.setOnClickListener(View.OnClickListener {view ->
            waist+=1
            setValue(tv_value, waist)
        })

        iv_minus.setOnClickListener(View.OnClickListener {view ->
            if(waist>=1) {
                waist-= 1
                setValue(tv_value, waist)
            }
        })
        btn_update.setOnClickListener(View.OnClickListener {view ->
            s_waist = ""+waist
            waistDialog!!.dismiss()
            callUpdateBodyMeasurementTask()

        })
    }

    fun setValue(tv : TextView, f1 : Int){
        tv!!.setText(""+f1)
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
                DialogInterface.OnClickListener { dialog, which -> callUpdateBodyMeasurementTask() })
            alertDialog.setNegativeButton(context!!.resources!!.getString(R.string.network_cancel), DialogInterface.OnClickListener { dialog, which ->  })
            var alertDialog1 = alertDialog.create()
            alertDialog1.show()
        }
        catch (e: Exception){

        }
    }

    fun callUpdateBodyMeasurementTask(){

        var progressDialog : ProgressDialog = ProgressDialog.show(requireContext(), "", "")
        progressDialog.setContentView(R.layout.progress_loader)
        progressDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        progressDialog.setCancelable(false)

        val service = Retrofit.Builder()
            .baseUrl(""+ AppConstants.BASE_URL_NEW)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(AppService::class.java)

        service.updateBodyMeasurement("Bearer "+s_accesstoken, s_body_fats, s_muscles_mass, s_arm_right, s_arm_left, s_chest, s_thigh, s_calf, s_hip, s_waist).enqueue(object :
            Callback<ResponseBody> {

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                progressDialog.dismiss()
                t.printStackTrace()
                dialogForCheckNetworkError()
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                progressDialog.dismiss()
                var code : Int = response.raw().code()
                Log.v("Response Code ", "==> "+code)
                if(code >199 && code < 300) {
                    var response1 : String = response.body()!!.string()
                    if(!UtilMethod.instance.isStringNullOrNot(response1)){
                        var obj : JSONObject = JSONObject(response1)
                        var success : Int = obj.getInt("success")
                        if(success == 200){
                            Log.v("REsponse Data", "==> "+response1)
                            var userDTO : UserDTO = Gson().fromJson(obj.getJSONObject("user_profile").toString(), UserDTO::class.java)
                            if(userDTO!=null){
                                UtilMethod.instance.setUser(requireContext(), userDTO)
                                setData()
                            }
                        }
                        else{

                        }

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



}