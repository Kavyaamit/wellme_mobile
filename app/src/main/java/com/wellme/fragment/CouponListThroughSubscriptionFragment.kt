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
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import com.wellme.R
import com.wellme.adapter.OffersViewAllAdapter
import com.wellme.databinding.FragmentCouponlistBinding
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

class CouponListThroughSubscriptionFragment : Fragment(), View.OnClickListener{
    var binding : FragmentCouponlistBinding? = null
    var regular : Typeface? = null
    var access_token : String? = ""
    var linearLayoutManager : LinearLayoutManager? = null
    var coupon_list : List<OfferListDTO> = ArrayList()
    var s_coupon_code : String? = ""
    var s_discount_type : String? = ""
    var s_discount : String? = ""
    var s_offer_id : String? = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_couponlist, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getView1()
    }

    fun getView1(){
        linearLayoutManager = LinearLayoutManager(requireContext())
        linearLayoutManager!!.orientation = LinearLayoutManager.VERTICAL
        binding!!.rvCoupons.layoutManager = linearLayoutManager
        regular = ResourcesCompat.getFont(context!!, R.font.poppins_regular)
        binding!!.header.setTypeface(regular)
        binding!!.etCoupon.setTypeface(regular)
        binding!!.tvApply.setTypeface(regular)
        binding?.back!!.setOnClickListener(this)
        binding?.tvApply!!.setOnClickListener(this)
        access_token = UtilMethod.instance.getAccessToken(requireContext())
        callCouponAPI()

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
                DialogInterface.OnClickListener { dialog, which -> callCouponAPI() })
            alertDialog.setNegativeButton(context!!.resources!!.getString(R.string.network_cancel), DialogInterface.OnClickListener { dialog, which -> requireActivity().supportFragmentManager.popBackStack() })
            var alertDialog1 = alertDialog.create()
            alertDialog1.show()
        }
        catch (e: Exception){

        }
    }

    fun callCouponAPI(){

        var progressDialog : ProgressDialog = ProgressDialog.show(requireContext(), "", "")
        progressDialog.setContentView(R.layout.progress_loader)
        progressDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        progressDialog.setCancelable(false)

        val service = Retrofit.Builder()
            .baseUrl(""+ AppConstants.BASE_URL_NEW)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(AppService::class.java)


        service.getOfferList("Bearer "+access_token,"2").enqueue(object :
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
                        coupon_list = gson.fromJson(object1.getJSONArray("offer_data").toString(), Array<OfferListDTO>::class.java).toList()

                        if(coupon_list!=null){
                            binding?.rvCoupons?.adapter = OffersViewAllAdapter(requireContext(), coupon_list, 0, onItemCallback)
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

    fun dialogOffer(position: Int){
        var dto : OfferListDTO = coupon_list.get(position)
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
        btn_apply.setText("Select")
        dialog.show()
        btn_cancel.setOnClickListener(View.OnClickListener {view ->
            dialog.dismiss()
        })
        btn_apply.setOnClickListener(View.OnClickListener {view ->
            dialog.dismiss()
            s_coupon_code = dto!!.coupon_code
            if(!UtilMethod.instance.isStringNullOrNot(s_coupon_code)){
                s_offer_id = dto!!.id
                s_discount = dto!!.discount_value
                s_discount_type = dto!!.discount_type
                binding?.etCoupon!!.setText(""+s_coupon_code)
            }
            //callCheckOfferValidityAPI(dto.id)

        })
    }

    override fun onClick(p0: View?) {
        if(p0 == binding?.back){
            requireActivity().supportFragmentManager.popBackStack()
        }
        else if(p0 == binding?.tvApply){
            s_coupon_code = binding?.etCoupon!!.text.toString()
            if(UtilMethod.instance.isStringNullOrNot(s_coupon_code)){
                UtilMethod.instance.dialogOK(context!!, "", context!!.resources!!.getString(R.string.coupon_code_validation), context!!.resources!!.getString(R.string.ok), false)
            }
            else{
                callCheckCouponValidityAPI()
            }
        }
    }

    fun callCheckCouponValidityAPI(){

        var progressDialog : ProgressDialog = ProgressDialog.show(requireContext(), "", "")
        progressDialog.setContentView(R.layout.progress_loader)
        progressDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        progressDialog.setCancelable(false)

        val service = Retrofit.Builder()
            .baseUrl(""+ AppConstants.BASE_URL_NEW)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(AppService::class.java)


        service.checkUserCouponValidity("Bearer "+access_token, s_coupon_code).enqueue(object :
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
                            UtilMethod.instance.setString(s_offer_id, context!!, "offer_id")
                            UtilMethod.instance.setString(s_coupon_code, context!!, "coupon_code")
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
}