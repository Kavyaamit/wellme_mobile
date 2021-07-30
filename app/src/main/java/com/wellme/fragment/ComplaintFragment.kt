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
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.wellme.LeftSideMenuActivity
import com.wellme.R
import com.wellme.adapter.ComplaintTypeAdapter
import com.wellme.databinding.FragmentComplaintBinding
import com.wellme.dto.ComplaintTypeDTO
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

class ComplaintFragment : Fragment(), View.OnClickListener{
    var binding : FragmentComplaintBinding? = null
    var regular : Typeface? = null
    var s_type_id : String = ""
    var s_access_token : String? = ""
    var s_description : String = ""
    var s_other_title : String = ""
    var s_type : String = ""
    var complaint_type_list : List<ComplaintTypeDTO> = ArrayList()
    var complaint_type_list1 : ArrayList<ComplaintTypeDTO> = ArrayList()
    var dialog1 : Dialog? = null
    var activity : Activity? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_complaint, container, false)
        return binding?.root
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        this.activity = activity
    }

    override fun onResume() {
        super.onResume()
        (activity as LeftSideMenuActivity).disableBottomBar()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        (activity as LeftSideMenuActivity).enableBottomBar()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        regular = ResourcesCompat.getFont(requireContext(), R.font.poppins_regular)
        binding?.header!!.setTypeface(regular)
        binding?.tvType!!.setTypeface(regular)
        binding?.etOtherTitle!!.setTypeface(regular)
        binding?.etDescription!!.setTypeface(regular)
        binding?.btnSubmit!!.setTypeface(regular)
        binding?.btnSubmit!!.setOnClickListener(this)
        binding?.back!!.setOnClickListener(this)
        binding?.llTitle!!.setOnClickListener(this)
        s_access_token = UtilMethod.instance.getAccessToken(requireContext())
        callComplaintTypeListAPI()
    }

    fun dialogForCheckNetworkError1(){
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
                DialogInterface.OnClickListener { dialog, which -> callComplaintTypeListAPI() })
            alertDialog.setNegativeButton(context!!.resources!!.getString(R.string.network_cancel), DialogInterface.OnClickListener { dialog, which -> requireActivity().supportFragmentManager.popBackStack() })
            var alertDialog1 = alertDialog.create()
            alertDialog1.show()
        }
        catch (e: Exception){

        }
    }


    fun callComplaintTypeListAPI(){

        var progressDialog : ProgressDialog = ProgressDialog.show(requireContext(), "", "")
        progressDialog.setContentView(R.layout.progress_loader)
        progressDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        progressDialog.setCancelable(false)

        val service = Retrofit.Builder()
            .baseUrl(""+ AppConstants.BASE_URL_NEW)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(AppService::class.java)


        service.getComplaintTypeList().enqueue(object :
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
                        complaint_type_list = gson.fromJson(object1.getJSONArray("result").toString(), Array<ComplaintTypeDTO>::class.java).toList()
                        if(complaint_type_list!=null){

                        }
                    }
                }
                else{

                }
            }
        })
    }

    fun showComplaintTypeDialog(){
        dialog1 = Dialog(requireContext())
        var window : Window? = dialog1!!.window
        dialog1!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog1!!.setContentView(R.layout.dialog_complaint_type)
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog1!!.setCanceledOnTouchOutside(true)
        window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        var rv_complaint_type : RecyclerView = dialog1!!.findViewById(R.id.rv_complaint_type)
        var linearLayoutManager : LinearLayoutManager = LinearLayoutManager(requireContext())
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        rv_complaint_type.layoutManager = linearLayoutManager
        complaint_type_list1 = ArrayList()
        if(complaint_type_list!=null){
            complaint_type_list1.addAll(complaint_type_list)
            complaint_type_list1.add(ComplaintTypeDTO("0", "Others"))
        }
        rv_complaint_type.adapter = ComplaintTypeAdapter(requireContext(), complaint_type_list1, onItemClickCallback)
        dialog1!!.show()


    }

    var onItemClickCallback : OnItemClickListener.OnItemClickCallback = object : OnItemClickListener.OnItemClickCallback{

        override fun onItemClicked(view: View?, position: Int) {
            dialog1!!.dismiss()
            var dto : ComplaintTypeDTO = complaint_type_list1.get(position)
            if(dto!=null){
                s_type_id = dto.id
                s_type = dto.complain_title
                binding?.tvType!!.setText(s_type)
                if(s_type_id.equals("0", true)){
                    binding?.llOtherTitle!!.visibility = View.VISIBLE
                }
                else{
                    binding?.llOtherTitle!!.visibility = View.GONE
                }
                binding?.etOtherTitle!!.setText("")
                s_other_title = ""
            }
        }

    }


    override fun onClick(v: View?) {
        if(v == binding?.back){
            requireActivity().onBackPressed()
        }
        else if(v == binding?.llTitle){
            showComplaintTypeDialog()
        }
        else if(v == binding?.btnSubmit){
            callAPI()
        }
    }
     fun callAPI(){
        s_other_title = binding?.etOtherTitle?.text.toString()
        s_description = binding?.etDescription?.text.toString()
        if(isValid()){
            if(s_type_id.equals("0", true)){
                callAddComplaintTask(s_other_title, s_description)
            }
            else{
                callAddComplaintTask(s_type, s_description)
            }

        }
    }

    fun callAddComplaintTask(title : String, description : String){

        var progressDialog : ProgressDialog = ProgressDialog.show(requireContext(), "", "")
        progressDialog.setContentView(R.layout.progress_loader)
        progressDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        progressDialog.setCancelable(false)

        val service = Retrofit.Builder()
            .baseUrl(""+AppConstants.BASE_URL_NEW)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(AppService::class.java)

        service.callAddComplaintAPI("Bearer "+s_access_token,title, description).enqueue(object :
            Callback<ResponseBody> {

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                progressDialog.dismiss()
                t.printStackTrace()
                dialogForCheckNetworkError1()
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                progressDialog.dismiss()
                var code : Int = response.raw().code()
                if(code >199 && code < 300) {
                    var response1 : String = response.body()!!.string()
                    if(!UtilMethod.instance.isStringNullOrNot(response1)){
                        Toast.makeText(requireContext(), "Complaint send successfully!!", Toast.LENGTH_LONG).show()
                        requireActivity().onBackPressed()
                    }
                }
                else{
                    UtilMethod.instance.dialogOK(requireContext(), "", ""+requireContext().resources.getString(R.string.user_invalid_message), ""+requireContext().resources.getString(R.string.ok), false)
                }

            }
        })
    }

    fun isValid() : Boolean{
        if(UtilMethod.instance.isStringNullOrNot(s_type_id)){
            UtilMethod.instance.dialogOK(requireContext(),"", requireContext().resources.getString(R.string.complaint_type_validation), requireContext().resources.getString(R.string.ok), false)
            return false
        }
        else if(s_type.equals("Others", true) && UtilMethod.instance.isStringNullOrNot(s_other_title)){
            UtilMethod.instance.dialogOK(requireContext(),"", requireContext().resources.getString(R.string.title_validation), requireContext().resources.getString(R.string.ok), false)
            return false
        }
        else if(UtilMethod.instance.isStringNullOrNot(s_description)){
            UtilMethod.instance.dialogOK(requireContext(),"", requireContext().resources.getString(R.string.description_validation), requireContext().resources.getString(R.string.ok), false)
            return false
        }
        return true
    }
}