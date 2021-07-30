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
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.wellme.LeftSideMenuActivity
import com.wellme.MainActivity
import com.wellme.R
import com.wellme.adapter.MedicalConditionAdapter
import com.wellme.adapter.OccupationAdapter
import com.wellme.databinding.FragmentUpdateProfileBinding
import com.wellme.dto.MedicalConditionDTO
import com.wellme.dto.OccupationDTO
import com.wellme.dto.UserDTO
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

class UpdateProfileFragment : Fragment(),  View.OnClickListener{
    var binding : FragmentUpdateProfileBinding? = null
    var userDTO : UserDTO? = null
    var regular : Typeface? = null
    var s_first_name : String? = ""
    var s_last_name : String? = ""
    var s_gender : String? = ""
    var s_age : String? = ""
    var s_weight : String? = ""
    var s_height : String? = ""
    var s_medical_condition_id : String? = ""
    var s_medical_condition : String? = ""
    var s_occupation_id : String? = ""
    var s_occupation_name : String? = ""
    var medicalcondition_list : List<MedicalConditionDTO> = ArrayList()
    var medicalcondition_filterlist : MutableList<MedicalConditionDTO> = ArrayList()
    var occupation_list : List<OccupationDTO> = ArrayList()
    var occupation_filterlist : MutableList<OccupationDTO> = ArrayList()
    var medical_condition_dialog : Dialog? = null
    var occupation_dialog : Dialog? = null
    var adapter : MedicalConditionAdapter? = null
    var occupationAdapter : OccupationAdapter? = null
    var occupation_pos : Int = -1
    var medical_condition_pos : Int = -1
    var accessToken : String? = ""
    var activity : Activity? = null





    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_update_profile, container, false)
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
        accessToken = UtilMethod.instance.getAccessToken(requireContext())
        initView()
    }

    fun initView(){
        regular = ResourcesCompat.getFont(requireContext(), R.font.poppins_regular)
        binding?.etAge!!.setTypeface(regular)
        binding?.etFirstName!!.setTypeface(regular)
        binding?.etLastName!!.setTypeface(regular)
        binding?.etWeight!!.setTypeface(regular)
        binding?.etHeight!!.setTypeface(regular)
        binding?.tvMedicalCondition!!.setTypeface(regular)
        binding?.tvOccupation!!.setTypeface(regular)
        binding?.tvMale!!.setTypeface(regular)
        binding?.tvFemale!!.setTypeface(regular)
        binding?.btnUpdate?.setTypeface(regular)
        binding?.tvEditProfile?.setTypeface(regular)
        binding?.tvAgeUnit?.setTypeface(regular)
        binding?.tvWeightUnit?.setTypeface(regular)
        binding?.tvHeightUnit?.setTypeface(regular)
        binding?.tvMale?.setOnClickListener(this)
        binding?.tvFemale?.setOnClickListener(this)
        binding?.maleLayout?.setOnClickListener(this)
        binding?.femaleLayout?.setOnClickListener(this)
        binding?.llMedicalCondition?.setOnClickListener(this)
        binding?.llOccupation?.setOnClickListener(this)
        binding?.back?.setOnClickListener(this)
        binding?.btnUpdate?.setOnClickListener(this)
        userDTO = UtilMethod.instance.getUser(requireContext())
        setData()

    }

    fun setData(){
        if(userDTO!=null){
            s_first_name = userDTO!!.first_name
            s_last_name = userDTO!!.last_name
            s_age = userDTO!!.age
            s_weight = userDTO!!.current_weight
            s_height = userDTO!!.initial_logged_height
            s_medical_condition = userDTO!!.medical_condition
            s_medical_condition_id = userDTO!!.medical_condition_id
            s_occupation_id = userDTO!!.occupation_id
            s_occupation_name = userDTO!!.occupation

            if(!UtilMethod.instance.isStringNullOrNot(s_first_name)){
                binding?.etFirstName!!.setText(s_first_name)
            }
            if(!UtilMethod.instance.isStringNullOrNot(s_last_name)){
                binding?.etLastName!!.setText(s_last_name)
            }
            if(!UtilMethod.instance.isStringNullOrNot(s_age)){
                binding?.etAge!!.setText(s_age)
            }
            if(!UtilMethod.instance.isStringNullOrNot(s_weight)){
                binding?.etWeight!!.setText(s_weight)
            }
            if(!UtilMethod.instance.isStringNullOrNot(s_height)){
                binding?.etHeight!!.setText(s_height)
            }
            if(!UtilMethod.instance.isStringNullOrNot(s_occupation_name)){
                binding?.tvOccupation!!.setText(s_occupation_name)
            }

        }
        callMedicalConditionAndOccupationTask()
    }


    fun isValid() : Boolean{
        if(UtilMethod.instance.isStringNullOrNot(s_first_name)){
            UtilMethod.instance.dialogOK(context, "", context?.resources?.getString(R.string.first_name_empty_validation), context?.resources?.getString(R.string.ok), false)
            binding?.etFirstName?.requestFocus()
            return false
        }
        else if(UtilMethod.instance.isStringNullOrNot(s_last_name)){
            UtilMethod.instance.dialogOK(context, "", context?.resources?.getString(R.string.last_name_empty_validation), context?.resources?.getString(R.string.ok), false)
            binding?.etLastName?.requestFocus()
            return false
        }
        else if(UtilMethod.instance.isStringNullOrNot(s_gender)){
            UtilMethod.instance.dialogOK(context, "", context?.resources?.getString(R.string.gender_empty_validation), context?.resources?.getString(R.string.ok), false)
            return false
        }
        else if(UtilMethod.instance.isStringNullOrNot(s_age)){
            UtilMethod.instance.dialogOK(context, "", context?.resources?.getString(R.string.age_empty_validation), context?.resources?.getString(R.string.ok), false)
            binding?.etAge?.requestFocus()
            return false
        }
        else if(UtilMethod.instance.isStringNullOrNot(s_weight)){
            UtilMethod.instance.dialogOK(context, "", context?.resources?.getString(R.string.weight_empty_validation), context?.resources?.getString(R.string.ok), false)
            binding?.etWeight?.requestFocus()
            return false
        }
        else if(UtilMethod.instance.isStringNullOrNot(s_height)){
            UtilMethod.instance.dialogOK(context, "", context?.resources?.getString(R.string.height_empty_validation), context?.resources?.getString(R.string.ok), false)
            binding?.etHeight?.requestFocus()
            return false
        }
        else if(UtilMethod.instance.isStringNullOrNot(s_medical_condition_id)){
            UtilMethod.instance.dialogOK(context, "", context?.resources?.getString(R.string.medical_condtion_empty_validation), context?.resources?.getString(R.string.ok), false)
            return false
        }
        else if(UtilMethod.instance.isStringNullOrNot(s_occupation_id)){
            UtilMethod.instance.dialogOK(context, "", context?.resources?.getString(R.string.occupation_empty_validation), context?.resources?.getString(R.string.ok), false)
            return false
        }

        return true
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
                DialogInterface.OnClickListener { dialog, which ->if(status == 1){ callMedicalConditionAndOccupationTask() }else if(status == 2){ callUpdateProfileTask() } })
            alertDialog.setNegativeButton(context!!.resources!!.getString(R.string.network_cancel), DialogInterface.OnClickListener { dialog, which ->  })
            var alertDialog1 = alertDialog.create()
            alertDialog1.show()
        }
        catch (e: Exception){

        }
    }

    fun callMedicalConditionAndOccupationTask(){

        var progressDialog : ProgressDialog = ProgressDialog.show(requireContext(), "", "")
        progressDialog.setContentView(R.layout.progress_loader)
        progressDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        progressDialog.setCancelable(false)

        val service = Retrofit.Builder()
            .baseUrl(""+ AppConstants.BASE_URL_NEW)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(AppService::class.java)


        service.getDataList().enqueue(object : Callback<ResponseBody> {

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
                Log.v("Response ", "==>"+response.raw().code())
                var response1 : String = response.body()!!.string()
                if(!UtilMethod.instance.isStringNullOrNot(response1)){
                    var object1 : JSONObject = JSONObject(response1)
                    if(object1!=null){
                        var gson : Gson = Gson()
                        medicalcondition_list = gson.fromJson(object1.getJSONArray("medicalcondition").toString(), Array<MedicalConditionDTO>::class.java).toList()
                        medicalcondition_filterlist = gson.fromJson(object1.getJSONArray("medicalcondition").toString(), Array<MedicalConditionDTO>::class.java).toMutableList()
                        occupation_list = gson.fromJson(object1.getJSONArray("occupationserializer").toString(), Array<OccupationDTO>::class.java).toMutableList()
                        occupation_filterlist = gson.fromJson(object1.getJSONArray("occupationserializer").toString(), Array<OccupationDTO>::class.java).toMutableList()
                        setMedicalCondition()
                    }
                }
                else{

                }
            }
        })
    }


    override fun onClick(v: View?) {
        if(v == binding?.back){
            activity?.onBackPressed()
        }
        else if(v == binding?.maleLayout || v == binding?.tvMale){
            s_gender = "male"
            Log.v("Click on", "male")
            binding?.maleLayout?.setBackground(context?.resources?.getDrawable(R.drawable.light_blue_bg_with_gray_rounded_corner))
            binding?.femaleLayout?.setBackground(context?.resources?.getDrawable(R.drawable.white_bg_with_theme_rounded_corner))
        }
        else if(v == binding?.femaleLayout || v == binding?.tvFemale){
            s_gender = "female"
            print("Click on female")
            binding?.maleLayout?.setBackground(context?.resources?.getDrawable(R.drawable.white_bg_with_theme_rounded_corner))
            binding?.femaleLayout?.setBackground(context?.resources?.getDrawable(R.drawable.light_blue_bg_with_gray_rounded_corner))
        }
        else if(v == binding?.llMedicalCondition || v == binding?.tvMedicalCondition){
            showMedicalConditionListDialog()
        }
        else if(v == binding?.llOccupation || v == binding?.tvOccupation){
            showOccupationDialog()
        }
        else if(v == binding?.btnUpdate){
            s_first_name = binding?.etFirstName?.text.toString()
            s_last_name = binding?.etLastName?.text.toString()
            s_weight = binding?.etWeight?.text.toString()
            s_age = binding?.etAge?.text.toString()
            s_height = binding?.etHeight?.text.toString()
            if(isValid()){
                callUpdateProfileTask()
            }
        }
    }

    fun showOccupationDialog(){
        occupation_dialog = Dialog(requireContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        occupation_dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        occupation_dialog!!.setContentView(R.layout.dialog_selection)
        occupation_dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        occupation_dialog!!.show()

        var header : TextView? = occupation_dialog?.findViewById(R.id.header)
        var et_search : TextView? = occupation_dialog?.findViewById(R.id.et_search)
        var rv_item : RecyclerView? = occupation_dialog?.findViewById(R.id.rv_item)
        var back : ImageView? = occupation_dialog?.findViewById(R.id.back)

        var linearLayoutManager1 : LinearLayoutManager = LinearLayoutManager(requireContext())
        linearLayoutManager1.orientation = LinearLayoutManager.VERTICAL
        rv_item!!.layoutManager = linearLayoutManager1
        occupationAdapter = OccupationAdapter(requireContext(), occupation_list,occupation_filterlist, onItemClickCallback, occupation_pos,s_occupation_id+"")
        rv_item!!.adapter = occupationAdapter

        var regular1 : Typeface? = ResourcesCompat.getFont(requireContext(), R.font.poppins_regular)
        header?.setTypeface(regular1)
        et_search?.setTypeface(regular1)

        header?.text = requireContext().resources.getString(R.string.state)
        et_search?.hint = requireContext().resources.getString(R.string.search)

        filterOccupationList("",occupation_list)
        occupationAdapter?.notifyData()

        back?.setOnClickListener(View.OnClickListener {view ->
            occupation_dialog!!.dismiss()

        })


        et_search!!.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterOccupationList(s.toString(),occupation_list)
                occupationAdapter?.notifyData()
            }

        })

    }

    fun setMedicalCondition(){
        s_medical_condition = ""
        if(medicalcondition_list!=null && !UtilMethod.instance.isStringNullOrNot(s_medical_condition_id)){
            var result: List<String> = s_medical_condition_id!!.split(",").map { it.trim() }
            if(result!=null){
                for(j in 0..result.size-1){
                    var s_medical_condition_id : String? = result.get(j)
                    for(i in 0..medicalcondition_list.size-1){
                        var s_m_condition : String? = medicalcondition_list.get(i).id
                        if(!UtilMethod.instance.isStringNullOrNot(s_medical_condition_id) && !UtilMethod.instance.isStringNullOrNot(s_m_condition)){
                            if(s_medical_condition_id.equals(s_m_condition, true)){

                                medicalcondition_list.get(i).status = true
                                medicalcondition_filterlist.get(i).status = true
                                if(UtilMethod.instance.isStringNullOrNot(s_medical_condition)){
                                    s_medical_condition = medicalcondition_list.get(i).medical_condition
                                }
                                else{
                                    s_medical_condition +=", "+ medicalcondition_list.get(i).medical_condition
                                }
                                break
                            }
                        }
                    }
                }
            }
            if(!UtilMethod.instance.isStringNullOrNot(s_medical_condition)){
                binding?.tvMedicalCondition!!.setText(s_medical_condition)
            }
        }
    }

    fun callUpdateProfileTask(){

        var progressDialog : ProgressDialog = ProgressDialog.show(requireContext(), "", "")
        progressDialog.setContentView(R.layout.progress_loader)
        progressDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        progressDialog.setCancelable(false)

        val service = Retrofit.Builder()
            .baseUrl(""+AppConstants.BASE_URL_NEW)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(AppService::class.java)

        service.updateProfile("Bearer "+accessToken, s_first_name, s_last_name, s_age, s_gender, s_weight, s_height, s_medical_condition_id, s_occupation_id).enqueue(object :
            Callback<ResponseBody> {

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                progressDialog.dismiss()
                t.printStackTrace()
                dialogForCheckNetworkError(2)
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
                            Log.v("REsponse Data", "==> "+response1)
                            var userDTO : UserDTO = Gson().fromJson(obj.getJSONObject("user_profile").toString(), UserDTO::class.java)
                            if(userDTO!=null){
                                UtilMethod.instance.setUser(requireContext(), userDTO)
                                (activity as LeftSideMenuActivity).changeLeftSide()
                                requireActivity().onBackPressed()

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

    private val onItemClickCallback : OnItemClickListener.OnItemClickCallback = object : OnItemClickListener.OnItemClickCallback{
        override fun onItemClicked(view: View?, position: Int) {
            occupation_pos = position
            s_occupation_id = occupation_list.get(position).id
            binding?.tvOccupation!!.setText(occupation_list.get(position).occupations)
            occupation_dialog!!.dismiss()
        }

    }

    private val onItemClickCall: OnItemClickListener.OnItemClickCallback = object :
        OnItemClickListener.OnItemClickCallback {
        override fun onItemClicked(view: View?, position: Int) {
            //medical_condition_pos = position
            //s_medical_condition_id = medicalcondition_list.get(position).id
            //binding?.tvMedicalCondition!!.setText(medicalcondition_list.get(position).medical_condition)
            //medical_condition_dialog?.dismiss()
            var id : String = medicalcondition_filterlist!!.get(position).id
            medicalcondition_filterlist.get(position).status = !medicalcondition_filterlist.get(position).status
            if(medicalcondition_list.size > medicalcondition_filterlist.size){
                for(i in 0..medicalcondition_list.size-1){
                    var s_id : String = medicalcondition_list!!.get(i).id
                    if(s_id.equals(id, true)){
                        medicalcondition_list.get(i).status = medicalcondition_filterlist.get(position).status
                        break
                    }
                }
            }
            else{
                medicalcondition_list.get(position).status = medicalcondition_filterlist.get(position).status
            }
            if(adapter!=null){
                adapter!!.notifyDataSetChanged()
            }



        }
    }


    fun showMedicalConditionListDialog(){
        medical_condition_dialog = Dialog(requireContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        var window : Window? = medical_condition_dialog?.window
        medical_condition_dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        medical_condition_dialog!!.setContentView(R.layout.dialog_selection)
        medical_condition_dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        medical_condition_dialog!!.show()

        var header : TextView? = medical_condition_dialog?.findViewById(R.id.header)
        var et_search : TextView? = medical_condition_dialog?.findViewById(R.id.et_search)
        var rv_item : RecyclerView? = medical_condition_dialog?.findViewById(R.id.rv_item)
        var btn_done : Button? = medical_condition_dialog?.findViewById(R.id.btn_done)
        var back : ImageView? = medical_condition_dialog?.findViewById(R.id.back)

        var linearLayoutManager : LinearLayoutManager = LinearLayoutManager(requireContext())
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        rv_item!!.layoutManager = linearLayoutManager
        btn_done!!.visibility = View.VISIBLE
        adapter = MedicalConditionAdapter(requireContext(), medicalcondition_list, medicalcondition_filterlist,onItemClickCall, medical_condition_pos,s_medical_condition_id+"")
        rv_item!!.adapter = adapter

        var regular1 : Typeface? = ResourcesCompat.getFont(requireContext(), R.font.poppins_regular)
        header?.setTypeface(regular1)
        et_search?.setTypeface(regular1)
        btn_done?.setTypeface(regular1)

        header?.text = requireContext().resources.getString(R.string.medical_condition)
        et_search?.hint = requireContext().resources.getString(R.string.search)


        filterMedicalConditionList("",medicalcondition_list)
        adapter?.notifyData()

        back?.setOnClickListener(View.OnClickListener {view ->
            medical_condition_dialog!!.dismiss()

        })

        btn_done?.setOnClickListener(View.OnClickListener { view ->
            medical_condition_dialog!!.dismiss()
            s_medical_condition_id = ""
            s_medical_condition = ""
            for(i in 0..medicalcondition_list.size-1){
                var dto : MedicalConditionDTO = medicalcondition_list.get(i)
                if(dto!=null){
                    if(dto.status){
                        if(!UtilMethod.instance.isStringNullOrNot(s_medical_condition_id)){
                            s_medical_condition_id+=","+dto.id
                            s_medical_condition+=", "+dto.medical_condition
                        }
                        else{
                            s_medical_condition_id = dto.id
                            s_medical_condition = dto.medical_condition
                        }
                    }
                }

            }
            //s_medical_condition_id = medicalcondition_list.get(position).id
            binding?.tvMedicalCondition!!.setText(s_medical_condition)


            //binding?.tvMedicalCondition!!.setText(medicalcondition_list.get(position).medical_condition)

        })


        et_search!!.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterMedicalConditionList(s.toString(),medicalcondition_list)
                adapter?.notifyData()
            }

        })

    }


    fun filterOccupationList(search: String,list : List<OccupationDTO>){


        if (occupation_filterlist!=null){
            occupation_filterlist.clear()
        }
        for (dto : OccupationDTO in list) {
            if (dto.occupations.toLowerCase().contains(search.toLowerCase())) {
                occupation_filterlist.add(dto)
            }
        }

    }


    fun filterMedicalConditionList(search: String,list : List<MedicalConditionDTO>){


        if (medicalcondition_filterlist!=null){
            medicalcondition_filterlist.clear()
        }
        for (dto : MedicalConditionDTO in list) {
            if (dto.medical_condition.toLowerCase().contains(search.toLowerCase())) {
                medicalcondition_filterlist.add(dto)
            }
        }

    }




}