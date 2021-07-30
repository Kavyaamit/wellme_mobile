package com.wellme.fragment

import android.Manifest
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
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.wellme.LeftSideMenuActivity
import com.wellme.R
import com.wellme.adapter.*
import com.wellme.databinding.FragmentRegistrationPersonalDetailsBinding
import com.wellme.dto.*
import com.wellme.utils.*
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class RegistrationWithPersonalDetailsFragment : Fragment(), View.OnClickListener{
    var binding : FragmentRegistrationPersonalDetailsBinding? = null
    var s_first_name : String = ""
    var s_last_name : String = ""
    var s_email_address : String = ""
    var s_gender : String = ""
    var s_weight : String = ""
    var s_age : String = "23"
    var s_height : String = ""
    var s_occupation : String = "Job"
    var s_medical_condition : String = "Good"
    var s_city : String = "indore"
    var s_city_id : String = ""
    var s_mobile_number : String? = ""
    var medicalcondition_list : List<MedicalConditionDTO> = ArrayList()
    var medicalcondition_filterlist : MutableList<MedicalConditionDTO> = ArrayList()
    var occupation_list : List<OccupationDTO> = ArrayList()
    var occupation_filterlist : MutableList<OccupationDTO> = ArrayList()
    var goal_list : List<GoalDTO> = ArrayList()
    var goal_filterlist : MutableList<GoalDTO> = ArrayList()
    var medical_condition_dialog : Dialog? = null
    var occupation_dialog : Dialog? = null
    var adapter : MedicalConditionAdapter? = null
    var occupationAdapter : OccupationAdapter? = null
    var medical_condition_pos : Int = -1
    var occupation_pos : Int = -1
    var goalAdapter : GoalsAdapter? = null
    var goal_dialog : Dialog? = null
    var goal_pos : Int = -1
    var state_pos : Int = -1
    var state_dialog : Dialog? = null
    var state_list : List<StateDTO> = ArrayList()
    var state_filterlist : MutableList<StateDTO> = ArrayList()
    var stateAdapter : StateAdapter? = null
    var city_pos : Int = -1
    var city_dialog : Dialog? = null
    var city_list : List<CityDTO> = ArrayList()
    var city_filterlist : MutableList<CityDTO> = ArrayList()
    var cityAdapter : CityAdapter? = null
    var goal_id : String = ""
    var occupation_id : String = ""
    var medical_condition_id : String = ""
    var s_state_name : String = ""
    var s_city_name : String = ""
    var accessToken : String? = ""
    var state_id : String? = ""

    var search_city : String? = ""
    var search_state: String? = ""


    var rv_item_city : RecyclerView? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_registration_personal_details, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        callGetMedicalConditionAndOccupationTask()
    }

    fun initView(){
        binding?.done?.setOnClickListener(this)
        binding?.back?.setOnClickListener(this)
        binding?.maleLayout?.setOnClickListener(this)
        binding?.tvMale?.setOnClickListener(this)
        binding?.tvFemale?.setOnClickListener(this)
        binding?.femaleLayout?.setOnClickListener(this)
        binding?.llMedicalCondition?.setOnClickListener(this)
        binding?.tvMedicalCondition?.setOnClickListener(this)
        binding?.llOccupation?.setOnClickListener(this)
        binding?.tvOccupation?.setOnClickListener(this)
        binding?.llGoal?.setOnClickListener(this)
        binding?.tvGoal?.setOnClickListener(this)
        binding?.tvState?.setOnClickListener(this)
        binding?.llState?.setOnClickListener(this)
        binding?.llCity?.setOnClickListener(this)
        binding?.tvCity?.setOnClickListener(this)
        accessToken = UtilMethod.instance.getAccessToken(requireContext())
        s_mobile_number = UtilMethod.instance.getMobileNumber(requireContext())

        var light : Typeface? = ResourcesCompat.getFont(requireContext(), R.font.poppins_light)
        var bold : Typeface? = ResourcesCompat.getFont(requireContext(), R.font.poppins_bold)
        var regular : Typeface? = ResourcesCompat.getFont(requireContext(), R.font.poppins_regular)

        binding?.etFirstName?.setTypeface(regular)
        binding?.etLastName?.setTypeface(regular)
        binding?.tvState?.setTypeface(regular)
        binding?.personalDetail?.setTypeface(regular)
        binding?.etAge?.setTypeface(regular)
        binding?.tvCity?.setTypeface(regular)
        binding?.etWeight?.setTypeface(regular)
        binding?.etHeight?.setTypeface(regular)
        binding?.tvMale?.setTypeface(regular)
        binding?.tvFemale?.setTypeface(regular)
        binding?.tvMedicalCondition?.setTypeface(regular)
        binding?.tvOccupation?.setTypeface(regular)
        binding?.done?.setTypeface(regular)
        binding?.tvHeightUnit?.setTypeface(regular)
        binding?.tvWeightUnit?.setTypeface(regular)
        binding?.tvGoal?.setTypeface(regular)

    }

    override fun onClick(v: View?){
        if(v == binding?.back){
            activity?.onBackPressed()
        }
        else if(v == binding?.maleLayout || v == binding?.tvMale){
            s_gender = "male"
            Log.v("Click on", "male")
            binding?.maleLayout?.setBackground(context?.resources?.getDrawable(R.drawable.light_blue_bg_with_gray_rounded_corner))
            binding?.tvMale?.setTextColor(context!!.resources!!.getColor(R.color.white))
            binding?.tvFemale?.setTextColor(context!!.resources!!.getColor(R.color.black))
            binding?.femaleLayout?.setBackground(context?.resources?.getDrawable(R.drawable.white_bg_with_theme_rounded_corner))
        }
        else if(v == binding?.femaleLayout || v == binding?.tvFemale){
            s_gender = "female"
            print("Click on female")
            binding?.maleLayout?.setBackground(context?.resources?.getDrawable(R.drawable.white_bg_with_theme_rounded_corner))
            binding?.femaleLayout?.setBackground(context?.resources?.getDrawable(R.drawable.light_blue_bg_with_gray_rounded_corner))
            binding?.tvMale?.setTextColor(context!!.resources!!.getColor(R.color.black))
            binding?.tvFemale?.setTextColor(context!!.resources!!.getColor(R.color.white))
        }
        else if(v == binding?.done){
            s_first_name = binding?.etFirstName?.text.toString()
            s_last_name = binding?.etLastName?.text.toString()
            s_weight = binding?.etWeight?.text.toString()
            s_age = binding?.etAge?.text.toString()
            s_height = binding?.etHeight?.text.toString()
            if (isValid()){
                /*var fragmentTransaction : FragmentTransaction? = activity!!.supportFragmentManager.beginTransaction()
                var fragment : Fragment = FitnessLevelFragment()
                fragmentTransaction?.replace(R.id.container_splash, fragment)
                fragmentTransaction?.addToBackStack(null)
                fragmentTransaction?.commit()*/
                callCreateProfileTask()
            }
        }
        else if(v == binding?.llMedicalCondition || v == binding?.tvMedicalCondition){
            showMedicalConditionListDialog()
        }
        else if(v == binding?.llOccupation || v == binding?.tvOccupation){
            if (occupationAdapter!=null){
                filterOccupationList("",occupation_list)
                occupationAdapter?.notifyData()
            }
            showOccupationDialog()
        }
        else if(v == binding?.llGoal || v == binding?.tvGoal){
            showGoalListDialog()
        }
        else if(v == binding?.llState || v == binding?.tvState){
            showStateDialog()
        }
        else if(v == binding?.llCity || v == binding?.tvCity){
            if(UtilMethod.instance.isStringNullOrNot(s_state_name)){
                UtilMethod.instance.dialogOK(context, "", context?.resources?.getString(R.string.state_empty_validation), context?.resources?.getString(R.string.ok), false)
            }
            else{
                showCityDialog()
            }
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
                DialogInterface.OnClickListener { dialog, which -> if(status==1){callGetMedicalConditionAndOccupationTask()}else if(status==2){callCreateProfileTask()}
                })
            alertDialog.setNegativeButton(context!!.resources!!.getString(R.string.network_cancel), DialogInterface.OnClickListener { dialog, which ->  })
            var alertDialog1 = alertDialog.create()
            alertDialog1.show()
        }
        catch (e: Exception){

        }
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
        else if(UtilMethod.instance.isStringNullOrNot(medical_condition_id)){
            UtilMethod.instance.dialogOK(context, "", context?.resources?.getString(R.string.medical_condtion_empty_validation), context?.resources?.getString(R.string.ok), false)
            return false
        }
        else if(UtilMethod.instance.isStringNullOrNot(occupation_id)){
            UtilMethod.instance.dialogOK(context, "", context?.resources?.getString(R.string.occupation_empty_validation), context?.resources?.getString(R.string.ok), false)
            return false
        }
        else if(UtilMethod.instance.isStringNullOrNot(goal_id)){
            UtilMethod.instance.dialogOK(context, "", context?.resources?.getString(R.string.goal_empty_validation), context?.resources?.getString(R.string.ok), false)
            return false
        }
        else if(UtilMethod.instance.isStringNullOrNot(s_state_name)){
            UtilMethod.instance.dialogOK(context, "", context?.resources?.getString(R.string.state_empty_validation), context?.resources?.getString(R.string.ok), false)
            return false
        }
        else if(UtilMethod.instance.isStringNullOrNot(s_city_name)){
            UtilMethod.instance.dialogOK(context, "", context?.resources?.getString(R.string.city_empty_validation), context?.resources?.getString(R.string.ok), false)
            return false
        }

        return true
    }

    fun showStateDialog(){
        state_dialog = Dialog(requireContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        state_dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        state_dialog!!.setContentView(R.layout.dialog_selection)
        state_dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        state_dialog!!.show()

        var header : TextView? = state_dialog?.findViewById(R.id.header)
        var et_search : TextView? = state_dialog?.findViewById(R.id.et_search)
        var rv_item : RecyclerView? = state_dialog?.findViewById(R.id.rv_item)
        var back : ImageView? = state_dialog?.findViewById(R.id.back)



        var linearLayoutManager1 : LinearLayoutManager = LinearLayoutManager(requireContext())
        linearLayoutManager1.orientation = LinearLayoutManager.VERTICAL
        rv_item!!.layoutManager = linearLayoutManager1
        stateAdapter = StateAdapter(requireContext(), state_list,state_filterlist, onItemCallbackState , state_pos,state_id+"")
        rv_item!!.adapter = stateAdapter

        var regular1 : Typeface? = ResourcesCompat.getFont(requireContext(), R.font.poppins_regular)
        header?.setTypeface(regular1)
        et_search?.setTypeface(regular1)

        header?.text = requireContext().resources.getString(R.string.state)
        et_search?.hint = requireContext().resources.getString(R.string.search)

        filterStateList("",state_list)
        stateAdapter?.notifyData()

        et_search!!.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterStateList(s.toString(),state_list)
                stateAdapter?.notifyData()
            }

        })





        back?.setOnClickListener(View.OnClickListener {view ->
            state_dialog!!.dismiss()

        })


    }

    fun showCityDialog(){
        city_dialog = Dialog(requireContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        city_dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        city_dialog!!.setContentView(R.layout.dialog_selection)
        city_dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        city_dialog!!.show()

        var header : TextView? = city_dialog?.findViewById(R.id.header)
        var et_search : TextView? = city_dialog?.findViewById(R.id.et_search)
        rv_item_city = city_dialog?.findViewById(R.id.rv_item)
        var back : ImageView? = city_dialog?.findViewById(R.id.back)



        /*if (!UtilMethod.instance.isStringNullOrNot(search_city)){
            search_city=""
            callCityAPITask(state_id+"")

        }*/

        filterCityList("",city_list)
        cityAdapter?.notifyData()

        if (city_filterlist!=null){
            if (city_filterlist.size>0){

                setCityAdapter()
            }else{

            }
        }

       /* var linearLayoutManager1 : LinearLayoutManager = LinearLayoutManager(requireContext())
        linearLayoutManager1.orientation = LinearLayoutManager.VERTICAL
        rv_item_city?.layoutManager = linearLayoutManager1
        cityAdapter = CityAdapter(requireContext(), city_list, onItemClickCallbackCity, city_pos)
        rv_item_city!!.adapter = cityAdapter*/



        var regular1 : Typeface? = ResourcesCompat.getFont(requireContext(), R.font.poppins_regular)
        header?.setTypeface(regular1)
        et_search?.setTypeface(regular1)

        header?.text = requireContext().resources.getString(R.string.city_hint)
        et_search?.hint = requireContext().resources.getString(R.string.search)


        et_search!!.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                search_city = s.toString();
                Log.d("s",">>>"+s);

                filterCityList(s.toString(),city_list)
                cityAdapter?.notifyData()
//                callCityAPITask(state_id+"")
            }

        })



        back?.setOnClickListener(View.OnClickListener {view ->
            city_dialog!!.dismiss()

        })

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
        occupationAdapter = OccupationAdapter(requireContext(), occupation_list,occupation_filterlist, onItemClickCallback, occupation_pos,occupation_id)
        rv_item!!.adapter = occupationAdapter

        var regular1 : Typeface? = ResourcesCompat.getFont(requireContext(), R.font.poppins_regular)
        header?.setTypeface(regular1)
        et_search?.setTypeface(regular1)

        header?.text = requireContext().resources.getString(R.string.occupation_hint)
        et_search?.hint = requireContext().resources.getString(R.string.search)




        back?.setOnClickListener(View.OnClickListener {view ->
            occupation_dialog!!.dismiss()

        })


        et_search!!.addTextChangedListener(object : TextWatcher{
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


    fun showMedicalConditionListDialog(){
        medical_condition_dialog = Dialog(requireContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        var window : Window? = medical_condition_dialog?.window
        medical_condition_dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        medical_condition_dialog!!.setContentView(R.layout.dialog_selection)
        medical_condition_dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        medical_condition_dialog!!.show()

        var header : TextView? = medical_condition_dialog?.findViewById(R.id.header)
        var et_search : TextView? = medical_condition_dialog?.findViewById(R.id.et_search)
        var btn_done : Button? = medical_condition_dialog?.findViewById(R.id.btn_done)
        var rv_item : RecyclerView? = medical_condition_dialog?.findViewById(R.id.rv_item)
        var back : ImageView? = medical_condition_dialog?.findViewById(R.id.back)

        var linearLayoutManager : LinearLayoutManager = LinearLayoutManager(requireContext())
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        rv_item!!.layoutManager = linearLayoutManager
        adapter = MedicalConditionAdapter(requireContext(), medicalcondition_list,medicalcondition_filterlist, onItemClickCall, medical_condition_pos,medical_condition_id)
        rv_item!!.adapter = adapter

        var regular1 : Typeface? = ResourcesCompat.getFont(requireContext(), R.font.poppins_regular)
        header?.setTypeface(regular1)
        et_search?.setTypeface(regular1)
        btn_done?.setTypeface(regular1)
        btn_done!!.visibility = View.VISIBLE
        
        header?.text = requireContext().resources.getString(R.string.medical_condition)
        et_search?.hint = requireContext().resources.getString(R.string.search)

        btn_done?.setOnClickListener(View.OnClickListener { view ->
            medical_condition_dialog!!.dismiss()
            medical_condition_id = ""
            s_medical_condition = ""
            for(i in 0..medicalcondition_list.size-1){
                var dto : MedicalConditionDTO = medicalcondition_list.get(i)
                if(dto!=null){
                    if(dto.status){
                        if(!UtilMethod.instance.isStringNullOrNot(medical_condition_id)){
                            medical_condition_id+=","+dto.id
                            s_medical_condition+=", "+dto.medical_condition
                        }
                        else{
                            medical_condition_id = dto.id
                            s_medical_condition = dto.medical_condition
                        }
                    }
                }

            }
            //s_medical_condition_id = medicalcondition_list.get(position).id
            binding?.tvMedicalCondition!!.setText(s_medical_condition)


            //binding?.tvMedicalCondition!!.setText(medicalcondition_list.get(position).medical_condition)

        })


        filterMedicalConditionList("",medicalcondition_list)
        adapter?.notifyData()

        back?.setOnClickListener(View.OnClickListener {view ->
            medical_condition_dialog!!.dismiss()

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

    fun showGoalListDialog(){
        goal_dialog = Dialog(requireContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        var window : Window? = goal_dialog?.window
        goal_dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        goal_dialog!!.setContentView(R.layout.dialog_selection)
        goal_dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        goal_dialog!!.show()

        var header : TextView? = goal_dialog?.findViewById(R.id.header)
        var et_search : TextView? = goal_dialog?.findViewById(R.id.et_search)
        var rv_item : RecyclerView? = goal_dialog?.findViewById(R.id.rv_item)
        var back : ImageView? = goal_dialog?.findViewById(R.id.back)

        var linearLayoutManager : LinearLayoutManager = LinearLayoutManager(requireContext())
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        rv_item!!.layoutManager = linearLayoutManager
        goalAdapter = GoalsAdapter(requireContext(), goal_list,goal_filterlist, onItemClickCallGoal, goal_pos,goal_id)
        rv_item!!.adapter = goalAdapter

        var regular1 : Typeface? = ResourcesCompat.getFont(requireContext(), R.font.poppins_regular)
        header?.setTypeface(regular1)
        et_search?.setTypeface(regular1)

        header?.text = requireContext().resources.getString(R.string.goal)
        et_search?.hint = requireContext().resources.getString(R.string.search)


        filterGoalList("",goal_list)
        goalAdapter?.notifyData()

        back?.setOnClickListener(View.OnClickListener {view ->
            goal_dialog!!.dismiss()

        })

        et_search!!.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterGoalList(s.toString(),goal_list)
                goalAdapter?.notifyData()
            }

        })


    }

    private val onItemCallbackState : OnItemClickListener.OnItemClickCallback = object : OnItemClickListener.OnItemClickCallback{
        override fun onItemClicked(view: View?, position: Int) {
            state_pos = position
            s_state_name = state_filterlist.get(position).name
            binding?.tvState!!.setText(s_state_name)
            state_dialog!!.dismiss()
            state_id = state_filterlist.get(position).id
            search_city="";
            callCityAPITask(state_filterlist.get(position).id)


        }

    }



    private val onItemClickCall: OnItemClickListener.OnItemClickCallback = object :
        OnItemClickListener.OnItemClickCallback {
        override fun onItemClicked(view: View?, position: Int) {
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

    private val onItemClickCallGoal: OnItemClickListener.OnItemClickCallback = object :
        OnItemClickListener.OnItemClickCallback {
        override fun onItemClicked(view: View?, position: Int) {
            goal_pos = position
            goal_id = goal_filterlist.get(position).id
            binding?.tvGoal!!.setText(goal_filterlist.get(position).goals)
            goal_dialog?.dismiss()

        }
    }

    private val onItemClickCallback : OnItemClickListener.OnItemClickCallback = object : OnItemClickListener.OnItemClickCallback{
        override fun onItemClicked(view: View?, position: Int) {
            occupation_pos = position
            occupation_id = occupation_filterlist.get(position).id
            binding?.tvOccupation!!.setText(occupation_filterlist.get(position).occupations)
            occupation_dialog!!.dismiss()
        }

    }

    private val onItemClickCallbackCity : OnItemClickListener.OnItemClickCallback = object : OnItemClickListener.OnItemClickCallback{
        override fun onItemClicked(view: View?, position: Int) {
            city_pos = position
            s_city_name = city_filterlist.get(position).name
            s_city_id = city_filterlist.get(position).id
            binding?.tvCity!!.setText(s_city_name)
            city_dialog!!.dismiss()
        }

    }

    fun callGetMedicalConditionAndOccupationTask(){

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
                var response1 : String = response.body()!!.string()
                if(!UtilMethod.instance.isStringNullOrNot(response1)){
                    var object1 : JSONObject = JSONObject(response1)
                    if(object1!=null){
                        var gson : Gson = Gson()
                        medicalcondition_list = gson.fromJson(object1.getJSONArray("medicalcondition").toString(), Array<MedicalConditionDTO>::class.java).toList()
                        medicalcondition_filterlist = gson.fromJson(object1.getJSONArray("medicalcondition").toString(), Array<MedicalConditionDTO>::class.java).toMutableList()
                        occupation_list = gson.fromJson(object1.getJSONArray("occupationserializer").toString(), Array<OccupationDTO>::class.java).toMutableList()
                        occupation_filterlist = gson.fromJson(object1.getJSONArray("occupationserializer").toString(), Array<OccupationDTO>::class.java).toMutableList()
                        goal_list = gson.fromJson(object1.getJSONArray("goal").toString(), Array<GoalDTO>::class.java).toList()
                        goal_filterlist = gson.fromJson(object1.getJSONArray("goal").toString(), Array<GoalDTO>::class.java).toMutableList()
                        state_list = gson.fromJson(object1.getJSONArray("state").toString(), Array<StateDTO>::class.java).toList()
                        state_filterlist = gson.fromJson(object1.getJSONArray("state").toString(), Array<StateDTO>::class.java).toMutableList()
                    }
                }
                else{

                }
            }
        })
    }

    fun callCityAPITask(state_id : String){

        var progressDialog : ProgressDialog = ProgressDialog.show(requireContext(), "", "")
        progressDialog.setContentView(R.layout.progress_loader)
        progressDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        progressDialog.setCancelable(false)

        val service = Retrofit.Builder()
            .baseUrl(""+ AppConstants.BASE_URL_NEW)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(AppService::class.java)




        service.getCityList(state_id,"").enqueue(object : Callback<ResponseBody> {

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
                var response1 : String = response.body()!!.string()
                Log.v("Subscription Response ", "==> "+response1)
                if(!UtilMethod.instance.isStringNullOrNot(response1)){
                    var object1 : JSONObject = JSONObject(response1)
                    if(object1!=null){
                        var gson : Gson = Gson()
                        city_list = gson.fromJson(object1.getJSONArray("result").toString(), Array<CityDTO>::class.java).toList()
                        city_filterlist = gson.fromJson(object1.getJSONArray("result").toString(), Array<CityDTO>::class.java).toMutableList()


                        if (city_filterlist!=null){

                            if (city_filterlist.size>0){

                                setCityAdapter()
                            }
                        }




                    }
                }
                else{

                }
            }
        })
    }

    fun setCityAdapter(){

        var linearLayoutManager1 : LinearLayoutManager = LinearLayoutManager(requireContext())
        linearLayoutManager1.orientation = LinearLayoutManager.VERTICAL
        rv_item_city?.layoutManager = linearLayoutManager1
        cityAdapter = CityAdapter(requireContext(), city_list,city_filterlist, onItemClickCallbackCity, city_pos,s_city_id)
        rv_item_city?.adapter = cityAdapter
    }

    fun callCreateProfileTask(){

        var progressDialog : ProgressDialog = ProgressDialog.show(requireContext(), "", "")
        progressDialog.setContentView(R.layout.progress_loader)
        progressDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        progressDialog.setCancelable(false)

        val service = Retrofit.Builder()
            .baseUrl(""+ AppConstants.BASE_URL_NEW)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(AppService::class.java)


        service.callCreateProfile("Bearer "+accessToken, s_first_name, s_last_name, ""+s_mobile_number, s_age, s_gender, s_city_name, s_state_name, s_weight, medical_condition_id, occupation_id, s_height, goal_id, "1.0", "").enqueue(object : Callback<ResponseBody> {

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
                var code : Int = response.raw().code()
                Log.v("Response Code ", " ==> "+code+" "+response.raw())
                if(code >199 && code < 300) {
                    var response1 : String = response.body()!!.string()
                    if(!UtilMethod.instance.isStringNullOrNot(response1)){
                        Log.v("REsponse == > ", "=="+response1)
                        var obj : JSONObject = JSONObject(response1)
                        var success : Int = obj.getInt("success")
                        if(success == 200){
                            var userDTO : UserDTO = Gson().fromJson(obj.getJSONObject("user_profile").toString(), UserDTO::class.java)
                            if(userDTO!=null){
                                UtilMethod.instance.setUser(requireContext(), userDTO)
                                var fitness_level : String? = userDTO.fitness_level
                                var fitness_purpose : String? = userDTO.fitness_purpose
                                var target_time : String? = userDTO.target_weight_time
                                var fitness_target : String? = userDTO.fitness_target

                                if(UtilMethod.instance.isStringNullOrNot(fitness_target)){
                                    var fragmentTransaction : FragmentTransaction? = activity!!.supportFragmentManager.beginTransaction()
                                    var fragment : Fragment = FitnessPurposeFragment()
                                    fragmentTransaction?.replace(R.id.container_splash, fragment)
                                    fragmentTransaction?.commit()
                                }
                                else if(UtilMethod.instance.isStringNullOrNot(fitness_level)){
                                    var fragmentTransaction : FragmentTransaction? = activity!!.supportFragmentManager.beginTransaction()
                                    var fragment : Fragment = FitnessLevelFragment()
                                    fragmentTransaction?.replace(R.id.container_splash, fragment)
                                    fragmentTransaction?.commit()
                                }
                                else if(UtilMethod.instance.isStringNullOrNot(fitness_purpose)){
                                    var fragmentTransaction : FragmentTransaction? = activity!!.supportFragmentManager.beginTransaction()
                                    var fragment : Fragment = FitnessNextStepFragment()
                                    fragmentTransaction?.replace(R.id.container_splash, fragment)
                                    fragmentTransaction?.commit()
                                }

                                else if(UtilMethod.instance.isStringNullOrNot(target_time)){
                                    var fragmentTransaction : FragmentTransaction? = activity!!.supportFragmentManager.beginTransaction()
                                    var fragment : Fragment = WeightSummaryFragment()
                                    fragmentTransaction?.replace(R.id.container_splash, fragment)
                                    fragmentTransaction?.commit()
                                }
                                else{
                                    try {
                                        val onPermissonResult: OnPermissonResult1 = object : OnPermissonResult1 {
                                            override fun onPermissionResult(result: Boolean) {
                                                Log.v("Result ", " $result")
                                                if (result) {
                                                    UtilMethod.instance.setConnectWithfitness(requireContext(), false)
                                                    val intent : Intent = Intent(requireContext(), LeftSideMenuActivity::class.java)
                                                    startActivity(intent)
                                                    requireActivity().finish()
                                                }
                                                else{
                                                    requireActivity().finish()
                                                }

                                            }
                                        }
                                        checkLocationPermission(requireActivity(), onPermissonResult)
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                }

                            }
                        }
                        else{

                        }

                    }
                }

            }
        })
    }

    fun checkLocationPermission(
        activityCompat: Activity?,
        result: OnPermissonResult1
    ) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P){
            Dexter.withActivity(activityCompat)
                .withPermissions(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACTIVITY_RECOGNITION
                ).withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                        if (report.deniedPermissionResponses.size == 0) {
                            result.onPermissionResult(true)
                        }
                        else{
                            result.onPermissionResult(false)
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        permissions: List<PermissionRequest?>?,
                        token: PermissionToken
                    ) {
                        token.continuePermissionRequest()
                    }
                }).onSameThread().check()
        }
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { // only for gingerbread and newer versions
            Dexter.withActivity(activityCompat)
                .withPermissions(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ).withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                        if (report.deniedPermissionResponses.size == 0) {
                            result.onPermissionResult(true)
                        }
                        else{
                            result.onPermissionResult(false)
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        permissions: List<PermissionRequest?>?,
                        token: PermissionToken
                    ) {
                        token.continuePermissionRequest()
                    }
                }).onSameThread().check()
        } else {
            result.onPermissionResult(true)
            //settingsrequest();
        }
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




    fun filterGoalList(search: String,list : List<GoalDTO>){


        if (goal_filterlist!=null){
            goal_filterlist.clear()
        }
        for (dto : GoalDTO in list) {
            if (dto.goals.toLowerCase().contains(search.toLowerCase())) {
                goal_filterlist.add(dto)
            }
        }

    }


    fun filterStateList(search: String,list : List<StateDTO>){


        if (state_filterlist!=null){
            state_filterlist.clear()
        }
        for (dto : StateDTO in list) {
            if (dto.name.toLowerCase().contains(search.toLowerCase())) {
                state_filterlist.add(dto)
            }
        }

    }

    fun filterCityList(search: String,list : List<CityDTO>){


        if (city_filterlist!=null){
            city_filterlist.clear()
        }
        for (dto : CityDTO in list) {
            if (dto.name.toLowerCase().contains(search.toLowerCase())) {
                city_filterlist.add(dto)
            }
        }

    }

}