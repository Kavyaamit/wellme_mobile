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
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import com.wellme.LeftSideMenuActivity
import com.wellme.R
import com.wellme.adapter.OffersViewAllAdapter
import com.wellme.databinding.FragmentOfferlistBinding
import com.wellme.dto.OfferListDTO
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
import java.text.FieldPosition

class OfferListThroughSubcriptionFragment : Fragment(), View.OnClickListener{
    var binding : FragmentOfferlistBinding? = null
    var linearLayoutManager : LinearLayoutManager? = null
    var testinomial_list : List<OfferListDTO> = ArrayList()
    var regular : Typeface? = null
    var activity : Activity? = null
    var access_token : String? = ""
    var s_discount : String? = ""
    var s_discount_type : String? = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_offerlist, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        linearLayoutManager = LinearLayoutManager(requireContext())
        linearLayoutManager!!.orientation = LinearLayoutManager.VERTICAL
        binding!!.rvTestimonials.layoutManager = linearLayoutManager
        regular = ResourcesCompat.getFont(requireContext(), R.font.poppins_regular)
        binding!!.header.setTypeface(regular)
        binding!!.tvNoOfferError.setTypeface(regular)
        binding?.back!!.setOnClickListener(this)
        access_token = UtilMethod.instance.getAccessToken(requireContext())
        callTestinomialAPI()
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        this.activity = activity
    }

    override fun onResume() {
        super.onResume()
        (activity as LeftSideMenuActivity).disableBottomBar()

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
                DialogInterface.OnClickListener { dialog, which -> callTestinomialAPI() })
            alertDialog.setNegativeButton(context!!.resources!!.getString(R.string.network_cancel), DialogInterface.OnClickListener { dialog, which -> requireActivity().supportFragmentManager.popBackStack() })
            var alertDialog1 = alertDialog.create()
            alertDialog1.show()
        }
        catch (e: Exception){

        }
    }

    fun callTestinomialAPI(){

        var progressDialog : ProgressDialog = ProgressDialog.show(requireContext(), "", "")
        progressDialog.setContentView(R.layout.progress_loader)
        progressDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        progressDialog.setCancelable(false)

        val service = Retrofit.Builder()
            .baseUrl(""+ AppConstants.BASE_URL_NEW)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(AppService::class.java)


        service.getOfferList("Bearer "+access_token,"1").enqueue(object :
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
                Log.v("Notification Response ", "==> "+response1)
                if(!UtilMethod.instance.isStringNullOrNot(response1)){
                    var object1 : JSONObject = JSONObject(response1)
                    if(object1!=null){
                        var gson : Gson = Gson()
                        testinomial_list = gson.fromJson(object1.getJSONArray("offer_data").toString(), Array<OfferListDTO>::class.java).toList()

                        if(testinomial_list!=null){
                            if(testinomial_list.size>0) {
                                binding?.rvTestimonials?.adapter = OffersViewAllAdapter(
                                    requireContext(),
                                    testinomial_list,
                                    1,
                                    onItemCallback
                                )
                                binding?.tvNoOfferError!!.visibility = View.GONE
                                binding?.rvTestimonials!!.visibility = View.VISIBLE
                            }
                            else{
                                binding?.tvNoOfferError!!.visibility = View.VISIBLE
                                binding?.rvTestimonials!!.visibility = View.GONE
                            }
                        }
                        else{
                            binding?.tvNoOfferError!!.visibility = View.VISIBLE
                            binding?.rvTestimonials!!.visibility = View.GONE
                        }
                    }
                }
                else{

                }
            }
        })
    }

    var onItemCallback : OnItemClickListener.OnItemClickCallback = object : OnItemClickListener.OnItemClickCallback{
        override fun onItemClicked(view: View?, position: Int) {
            dialogOffer(position)
        }

    }

    fun callCheckOfferValidityAPI(id : String?){

        var progressDialog : ProgressDialog = ProgressDialog.show(requireContext(), "", "")
        progressDialog.setContentView(R.layout.progress_loader)
        progressDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        progressDialog.setCancelable(false)

        val service = Retrofit.Builder()
            .baseUrl(""+ AppConstants.BASE_URL_NEW)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(AppService::class.java)


        service.checkUserOfferValidity("Bearer "+access_token,id).enqueue(object :
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
                Log.v("Validity Response ", "==> "+response1)
                if(!UtilMethod.instance.isStringNullOrNot(response1)){
                    var obj : JSONObject = JSONObject(response1)
                    if(obj!=null){
                        var status : Int = obj.getInt("success")
                        if(status == 200){
                            UtilMethod.instance.setString(id, context!!, "offer_id")
                            UtilMethod.instance.setString("", context!!, "coupon_code")
                            UtilMethod.instance.setString(s_discount, context!!, "discount")
                            UtilMethod.instance.setString(s_discount_type, context!!, "discount_type")
                            requireActivity().supportFragmentManager.popBackStack()
                        }
                        else{
                            var message : String? = obj.getString("message")
                            UtilMethod.instance.dialogOK(context!!, "", message, context!!.resources!!.getString(R.string.ok), false)
                        }
                    }

                }
                else{

                }
            }
        })
    }

    fun dialogOffer(position: Int){
        var dto : OfferListDTO = testinomial_list.get(position)
        var regular : Typeface? = ResourcesCompat.getFont(requireContext(), R.font.poppins_regular)
        var dialog : Dialog = Dialog(requireContext(), android.R.style.Theme_Translucent_NoTitleBar_Fullscreen)
        dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog!!.setContentView(R.layout.dialog_offer)
        var btn_cancel : Button = dialog.findViewById(R.id.btn_cancel)
        var btn_apply : Button = dialog.findViewById(R.id.btn_apply)
        var tv_description : TextView = dialog.findViewById(R.id.tv_description)
        var iv_offer : ImageView = dialog.findViewById(R.id.iv_offer)
        var des : String = dto.description
        var image : String? = dto.image
        if(!UtilMethod.instance.isStringNullOrNot(image)){
            Picasso.get().load(AppConstants.IMAGE_URL_NEW1+""+dto.image).into(iv_offer)
        }
        if(!UtilMethod.instance.isStringNullOrNot(des)){
            tv_description!!.setText(des)
        }
        else{
            tv_description!!.setText(" ")
        }

        tv_description.setTypeface(regular)
        btn_cancel.setTypeface(regular)
        btn_apply.setTypeface(regular)
        btn_apply.setText("Apply")
        dialog.show()
        btn_cancel.setOnClickListener(View.OnClickListener {view ->
            dialog.dismiss()
        })
        btn_apply.setOnClickListener(View.OnClickListener {view ->
            dialog.dismiss()
            s_discount = dto!!.discount_value
            s_discount_type = dto!!.discount_type
            callCheckOfferValidityAPI(dto.id)

        })
    }

    override fun onClick(v: View?) {
        if(v == binding?.back){
            requireActivity().supportFragmentManager.popBackStack()
        }
    }
}