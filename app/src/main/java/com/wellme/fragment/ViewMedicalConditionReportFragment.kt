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
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import com.wellme.LeftSideMenuActivity
import com.wellme.R
import com.wellme.databinding.FragmentViewMedicalReportBinding
import com.wellme.dto.FeedbackTypeDTO
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

class ViewMedicalConditionReportFragment : Fragment(), View.OnClickListener{
    var binding : FragmentViewMedicalReportBinding? = null
    var bundle : Bundle? = null
    var s_medical_doc : String? = ""
    var s_document_type : String? = ""


    var regular : Typeface? = null
    var s_access_token : String? = ""
    var feedback_type_list : List<FeedbackTypeDTO> = ArrayList()
    var activity : Activity? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_view_medical_report, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.wvReport?.settings?.javaScriptEnabled = true;
        binding?.wvReport?.settings?.mediaPlaybackRequiresUserGesture = true;
//        binding?.wvReport?.loadUrl("https://docs.google.com/gview?embedded=true&url=http://173.212.250.62:3024/media/user_image/24-07-2021_Vaccination_Plan.pdf")

        bundle = arguments
        if(bundle!=null){
            s_medical_doc = bundle!!.getString("image")
            s_document_type = bundle!!.getString("document_type")

            Log.d("s_medical_doc",">>>>"+s_document_type)
            setData()
        }
        initView()
    }


   fun setData(){

       if (s_document_type=="image"){
               if(!UtilMethod.instance.isStringNullOrNot(s_medical_doc)){
                   Picasso.get().load(AppConstants.IMAGE_URL_NEW1+""+s_medical_doc)?.into(binding?.ivReport)
                   binding?.ivReport?.visibility = View.VISIBLE
               }else{
                   binding?.ivReport?.visibility = View.GONE
               }
       }
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

    fun initView(){
        regular = ResourcesCompat.getFont(requireContext(), R.font.poppins_regular)

        binding?.header?.setTypeface(regular)
        binding?.back?.setOnClickListener(this)
        s_access_token = UtilMethod.instance.getAccessToken(requireContext())

    }

    override fun onClick(v: View?) {
        if(v == binding?.back){
            requireActivity().onBackPressed()
        }

    }






    var onItemClickCallback : OnItemClickListener.OnItemClickCallback = object : OnItemClickListener.OnItemClickCallback{

        override fun onItemClicked(view: View?, position: Int) {


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
                DialogInterface.OnClickListener { dialog, which -> if(status == 1){callFeedbackTypeListAPI()}else if(status==2){callFeedbackTypeListAPI()} })
            alertDialog.setNegativeButton(context!!.resources!!.getString(R.string.network_cancel), DialogInterface.OnClickListener { dialog, which -> })
            var alertDialog1 = alertDialog.create()
            alertDialog1.show()
        }
        catch (e: Exception){

        }
    }

    fun callFeedbackTypeListAPI(){

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
                dialogForCheckNetworkError(1)

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
                        feedback_type_list = gson.fromJson(object1.getJSONArray("feedback_type").toString(), Array<FeedbackTypeDTO>::class.java).toList()
                        if(feedback_type_list!=null){
                            Log.v("Feedback Size ", "==> "+feedback_type_list.size)
                        }
                    }
                }
                else{

                }
            }
        })
    }

}