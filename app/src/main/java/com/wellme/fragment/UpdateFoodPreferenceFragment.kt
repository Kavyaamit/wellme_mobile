package com.wellme.fragment

import android.app.Activity
import android.app.AlertDialog
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
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CheckBox
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.wellme.LeftSideMenuActivity
import com.wellme.MainActivity
import com.wellme.R
import com.wellme.databinding.FragmentUpdateFoodPreferencesBinding
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

class UpdateFoodPreferenceFragment : Fragment(), View.OnClickListener, AdapterView.OnItemSelectedListener{
    var binding : FragmentUpdateFoodPreferencesBinding? = null
    var regular : Typeface? = null
    var dietList : ArrayList<String> = ArrayList()
    var s_diet_preference : String? = ""
    var s_preferred_cuisines : String? = ""
    var s_allergies : String? = ""
    var accessToken : String? = null
    var userDTO : UserDTO? = null
    var allergies_list : List<String> = ArrayList()
    var preferred_cuisines_list : List<String> = ArrayList()
    var activity : Activity? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_update_food_preferences, container, false)
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
        userDTO = UtilMethod.instance.getUser(requireContext())
        accessToken = UtilMethod.instance.getAccessToken(requireContext())
        regular = ResourcesCompat.getFont(requireContext(), R.font.poppins_regular)
        dietList.add( requireActivity().resources.getString(R.string.vegetarian)+"")
        dietList.add( requireActivity().resources.getString(R.string.eggitarian)+"")
        dietList.add( requireActivity().resources.getString(R.string.non_vegetarian)+"")
        initView()
    }

    fun initView(){



        binding?.cbBeef?.setTypeface(regular)
        binding?.cbDairy?.setTypeface(regular)
        binding?.cbChinese?.setTypeface(regular)
        binding?.cbContinental?.setTypeface(regular)
        binding?.cbEastIndian?.setTypeface(regular)
        binding?.cbEgg?.setTypeface(regular)
        binding?.cbLamb?.setTypeface(regular)
        binding?.cbNorthIndian?.setTypeface(regular)
        binding?.cbNuts?.setTypeface(regular)
        binding?.cbOther?.setTypeface(regular)
        binding?.cbPork?.setTypeface(regular)
        binding?.cbPoultry?.setTypeface(regular)
        binding?.cbSeafood?.setTypeface(regular)
        binding?.cbSouthIndian?.setTypeface(regular)
        binding?.cbWestIndian?.setTypeface(regular)
        binding?.cbWheat?.setTypeface(regular)
        binding?.tvAllergies?.setTypeface(regular)
        binding?.tvDietPreference?.setTypeface(regular)
        binding?.tvPreferredCuisines?.setTypeface(regular)
        binding?.tvFoodPreferences?.setTypeface(regular)
        binding?.ivSubmit?.setOnClickListener(this)
        binding?.ivBack?.setOnClickListener(this)
        binding?.spDietPreference!!.setOnItemSelectedListener(this)

        binding?.cbOther?.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                binding?.llOthers?.visibility = View.VISIBLE
            }
            else{
                binding?.llOthers?.visibility = View.GONE
            }
        }

        val aa = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, dietList)
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding?.spDietPreference!!.adapter = aa

        if(userDTO!=null){
            s_diet_preference = userDTO!!.diet_preference
            s_preferred_cuisines = userDTO!!.preference_cuisine
            s_allergies = userDTO!!.allergies
            if(!UtilMethod.instance.isStringNullOrNot(s_diet_preference)){

                for(i in 0..dietList.size - 1){
                    if(dietList.get(i) == s_diet_preference){
                        binding?.spDietPreference!!.setSelection(i)
                        break
                    }
                }
            }
            if(!UtilMethod.instance.isStringNullOrNot(s_allergies)){
                allergies_list = s_allergies!!.split("|")
                if(allergies_list!=null){
                    binding?.cbWheat?.isChecked = checkedItem(binding?.cbWheat)
                    binding?.cbDairy?.isChecked = checkedItem(binding?.cbDairy)
                    binding?.cbBeef?.isChecked = checkedItem(binding?.cbBeef)
                    binding?.cbSeafood?.isChecked = checkedItem(binding?.cbSeafood)
                    binding?.cbEgg?.isChecked = checkedItem(binding?.cbEgg)
                    binding?.cbNuts?.isChecked = checkedItem(binding?.cbNuts)
                    binding?.cbLamb?.isChecked = checkedItem(binding?.cbLamb)
                    binding?.cbPoultry?.isChecked = checkedItem(binding?.cbPoultry)
                    binding?.cbPork?.isChecked = checkedItem(binding?.cbPork)
                    binding?.cbOther?.isChecked = checkedItem2(binding?.cbOther)
                }

            }

            if(!UtilMethod.instance.isStringNullOrNot(s_preferred_cuisines)){
                preferred_cuisines_list = s_preferred_cuisines!!.split("|")
                if(preferred_cuisines_list!=null){
                    binding?.cbChinese?.isChecked = checkedItem1(binding?.cbChinese)
                    binding?.cbContinental?.isChecked = checkedItem1(binding?.cbContinental)
                    binding?.cbSouthIndian?.isChecked = checkedItem1(binding?.cbSouthIndian)
                    binding?.cbNorthIndian?.isChecked = checkedItem1(binding?.cbNorthIndian)
                    binding?.cbEastIndian?.isChecked = checkedItem1(binding?.cbEastIndian)
                    binding?.cbWestIndian?.isChecked = checkedItem1(binding?.cbWestIndian)
                }
            }
        }
    }

    fun checkedItem2(checkbox : CheckBox?) : Boolean{
        var flag : Boolean = false
        for(i in 0..allergies_list.size-1 ){
            if(allergies_list.get(i).contains(checkbox?.text.toString())){
                var s12 : String = checkbox?.text.toString()+" ("
                flag = true
                var s1 : String =  allergies_list.get(i).substring(s12.length)
                var s2 : String =  s1.replace(")", "")
                binding?.llOthers!!.visibility = View.VISIBLE
                binding?.etOthers!!.setText(s2)
                break
            }
        }
        return flag
    }

    fun checkedItem(checkbox : CheckBox?) : Boolean{
        var flag : Boolean = false
        for(i in 0..allergies_list.size-1 ){
            if(allergies_list.get(i) == checkbox?.text.toString()){

                flag = true
                break
            }
        }
        return flag
    }

    fun checkedItem1(checkbox : CheckBox?) : Boolean{
        var flag : Boolean = false
        for(i in 0..preferred_cuisines_list.size-1 ){
            if(preferred_cuisines_list.get(i) == checkbox?.text.toString()){

                flag = true
                break
            }
        }
        return flag
    }


    fun getAllergies(checkbox : CheckBox){
        if(checkbox!!.isChecked){
            if(UtilMethod.instance.isStringNullOrNot(s_allergies)){
                s_allergies = checkbox.text.toString()
            }
            else{
                s_allergies+="|"+checkbox.text.toString()
            }
        }

    }

    fun getFoodPreference(checkbox : CheckBox){
        if(checkbox!!.isChecked){
            if(UtilMethod.instance.isStringNullOrNot(s_preferred_cuisines)){
                s_preferred_cuisines = checkbox.text.toString()
            }
            else{
                s_preferred_cuisines+="|"+checkbox.text.toString()
            }
        }

    }

    override fun onClick(v: View?) {
        if(v == binding?.ivSubmit){
            s_allergies = ""
            s_preferred_cuisines = ""
            getFoodPreference(binding?.cbNorthIndian!!)
            getFoodPreference(binding?.cbChinese!!)
            getFoodPreference(binding?.cbContinental!!)
            getFoodPreference(binding?.cbEastIndian!!)
            getFoodPreference(binding?.cbSouthIndian!!)
            getFoodPreference(binding?.cbWestIndian!!)
            getAllergies(binding?.cbBeef!!)
            getAllergies(binding?.cbDairy!!)
            getAllergies(binding?.cbEgg!!)
            getAllergies(binding?.cbLamb!!)
            getAllergies(binding?.cbNuts!!)
            getAllergies(binding?.cbPork!!)
            getAllergies(binding?.cbPoultry!!)
            getAllergies(binding?.cbSeafood!!)
            getAllergies(binding?.cbWheat!!)
            if(binding?.cbOther?.isChecked!!){
                if(UtilMethod.instance.isStringNullOrNot(s_allergies)){
                    s_allergies = "Others (Specify) ("+binding?.etOthers?.text.toString()+")"
                }
                else{
                    s_allergies +="|Others (Specify) ("+ binding?.etOthers?.text.toString()+")"
                }
            }

            if(isValid()){
                callUpdateFoodPreferredTask()
            }



        }
        else if(v == binding?.ivBack){
            requireActivity().onBackPressed()
        }
    }

    fun isValid() : Boolean{
        if(UtilMethod.instance.isStringNullOrNot(s_diet_preference) && UtilMethod.instance.isStringNullOrNot(s_allergies) && UtilMethod.instance.isStringNullOrNot(s_preferred_cuisines)){
            UtilMethod.instance.dialogOK(requireContext(), "", "Please select at least one ", requireContext().resources.getString(R.string.ok), false)
            return false
        }
        return true
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
                DialogInterface.OnClickListener { dialog, which -> callUpdateFoodPreferredTask() })
            alertDialog.setNegativeButton(context!!.resources!!.getString(R.string.network_cancel), DialogInterface.OnClickListener { dialog, which ->  })
            var alertDialog1 = alertDialog.create()
            alertDialog1.show()
        }
        catch (e: Exception){

        }
    }

    fun callUpdateFoodPreferredTask(){

        var progressDialog : ProgressDialog = ProgressDialog.show(requireContext(), "", "")
        progressDialog.setContentView(R.layout.progress_loader)
        progressDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        progressDialog.setCancelable(false)

        val service = Retrofit.Builder()
            .baseUrl(""+ AppConstants.BASE_URL_NEW)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(AppService::class.java)

        service.updateFoodPreference("Bearer "+accessToken, s_diet_preference, s_allergies, s_preferred_cuisines).enqueue(object :
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
                            Log.v("REsponse Data", "==> "+response1)
                            var userDTO : UserDTO = Gson().fromJson(obj.getJSONObject("user_profile").toString(), UserDTO::class.java)
                            if(userDTO!=null){
                                UtilMethod.instance.setUser(requireContext(), userDTO)
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

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        s_diet_preference = dietList.get(position)
    }
}