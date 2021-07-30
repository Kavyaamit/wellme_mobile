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
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.paypal.android.sdk.payments.*
import com.wellme.LeftSideMenuActivity
import com.wellme.R
import com.wellme.adapter.SubscriptionAdapter
import com.wellme.databinding.FragmentSubscriptionBinding
import com.wellme.dto.SubscriptionDTO
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
import java.math.BigDecimal
import java.util.*
import kotlin.collections.ArrayList

class SubscriptionFragment : Fragment(), View.OnClickListener{
    var binding : FragmentSubscriptionBinding? = null
    var access_token : String? = ""
    var subscription_list : List<SubscriptionDTO> = ArrayList()
    var regular : Typeface? = null
    var linearLayoutManager : LinearLayoutManager? = null
    var config : PayPalConfiguration = PayPalConfiguration()
                            .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
        .acceptCreditCards(false)
        .clientId("AVWFZNP8kbF_Yl7qfGpmbOSjcF9G9GQGwFfDYtsiaeYLE_wZzGsJ3yV_Cf8T9i6XOxhYG3293Bbtichq")
        var PAYPAL_REQUEST_CODE  : Int = 7777
    var s_plan_id : String? = ""
    var s_plan_amount : String? = ""
    var s_end_date : String? = ""
    var s_validity : String? = ""
    var activity : Activity? = null



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_subscription, container, false)
        return binding?.root
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        this.activity = activity
    }

    override fun onResume() {
        super.onResume()
        (activity as LeftSideMenuActivity).enableBottomBar()
        (activity as LeftSideMenuActivity).setActiveSection(1)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        regular = ResourcesCompat.getFont(requireContext(), R.font.poppins_regular)
        binding?.header!!.setTypeface(regular)
        linearLayoutManager = LinearLayoutManager(requireContext())
        linearLayoutManager!!.orientation = LinearLayoutManager.VERTICAL
        binding?.rvSubscription!!.layoutManager = linearLayoutManager

        access_token = UtilMethod.instance.getAccessToken(requireContext())

        var intent : Intent = Intent(requireContext(), PayPalService::class.java)
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config)
        requireActivity().startService(intent)

        binding?.back?.setOnClickListener(this)

        callSubscriptionAPI()
    }

    override fun onClick(v: View?) {

        if (v==binding?.back){

            requireActivity().onBackPressed()

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
                DialogInterface.OnClickListener { dialog, which -> callSubscriptionAPI() })
            alertDialog.setNegativeButton(context!!.resources!!.getString(R.string.network_cancel), DialogInterface.OnClickListener { dialog, which -> requireActivity().supportFragmentManager.popBackStack() })
            var alertDialog1 = alertDialog.create()
            alertDialog1.show()
        }
        catch (e: Exception){

        }
    }
    fun callSubscriptionAPI(){

        var progressDialog : ProgressDialog = ProgressDialog.show(requireContext(), "", "")
        progressDialog.setContentView(R.layout.progress_loader)
        progressDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        progressDialog.setCancelable(false)

        val service = Retrofit.Builder()
            .baseUrl(""+ AppConstants.BASE_URL_NEW)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(AppService::class.java)


        service.getSubscriptionList("Bearer "+access_token).enqueue(object : Callback<ResponseBody> {

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
                        subscription_list = gson.fromJson(object1.getJSONArray("subscription_data").toString(), Array<SubscriptionDTO>::class.java).toList()
                        //s_end_date = object1.getString("end_date")

                        if(subscription_list!=null){
                            binding?.rvSubscription?.adapter = SubscriptionAdapter(requireContext(), subscription_list, onItemClickCallback, onItemClickCallbackBuyNow)
                        }
                    }
                }
                else{

                }
            }
        })
    }

    fun callRateAPI(f1 : Double){

        var progressDialog : ProgressDialog = ProgressDialog.show(requireContext(), "", "")
        progressDialog.setContentView(R.layout.progress_loader)
        progressDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        progressDialog.setCancelable(false)

        val service = Retrofit.Builder()
            .baseUrl("https://api.exchangeratesapi.io/")
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(AppService::class.java)


        service.getRateAPI().enqueue(object : Callback<ResponseBody> {

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
                        var rate_obj : JSONObject = object1.getJSONObject("rates")
                        if(rate_obj!=null){
                            var inr : Double = rate_obj.getDouble("INR")
                            var usd : Double = rate_obj.getDouble("USD")
                            processPayment(f1 * usd / inr)
                        }
                    }
                }
                else{

                }
            }
        })
    }

    var onItemClickCallback : OnItemClickListener.OnItemClickCallback = object : OnItemClickListener.OnItemClickCallback{
        override fun onItemClicked(view: View?, position: Int) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

    }

    var onItemClickCallbackBuyNow : OnItemClickListener.OnItemClickCallback = object : OnItemClickListener.OnItemClickCallback{
        override fun onItemClicked(view: View?, position: Int) {
            var s_amount : String = subscription_list.get(position).amount
//            if(!UtilMethod.instance.isStringNullOrNot(s_amount)){
//                s_plan_id = subscription_list.get(position).id
//                s_plan_amount = subscription_list.get(position).amount
//                s_validity = subscription_list.get(position).no_of_days
//                //callRateAPI(s_amount.toDouble())
//                dialogSelectPaymentType(s_plan_amount!!)
//            }

            var fragmentTransaction : FragmentTransaction? = requireActivity().supportFragmentManager.beginTransaction()
            var bundle : Bundle = Bundle()
            bundle.putString("subscription_id", subscription_list.get(position).id)
            UtilMethod.instance.setString("", context!!, "offer_id")
            UtilMethod.instance.setString("", context!!, "coupon_code")
            UtilMethod.instance.setString("", context!!, "discount")
            UtilMethod.instance.setString("", context!!, "discount_type")
            var fragment : Fragment = SubscriptionDetailFragment()
            fragment.arguments = bundle
            fragmentTransaction?.replace(R.id.container_home, fragment)
            fragmentTransaction?.addToBackStack(null)
            fragmentTransaction?.commit()

        }

    }

    fun dialogSelectPaymentType(s1 : String){
        var dialog : Dialog? = Dialog(requireContext())
        var window : Window? = dialog!!.window
        dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog!!.setContentView(R.layout.dialog_payment_type)
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog!!.setCanceledOnTouchOutside(true)
        window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        var regular1 : Typeface? = ResourcesCompat.getFont(requireContext(), R.font.poppins_regular)
        var tv_razorpay : TextView = dialog!!.findViewById(R.id.tv_razorpay)
        var tv_paypal : TextView = dialog!!.findViewById(R.id.tv_paypal)
        tv_paypal.setTypeface(regular1)
        tv_razorpay.setTypeface(regular1)
        dialog.show()
        tv_paypal.setOnClickListener (View.OnClickListener{ view ->
            dialog.dismiss()
            //callRateAPI(s1.toDouble())
            processPayment(s1.toDouble()/70)
        })


        tv_razorpay.setOnClickListener(View.OnClickListener {view ->
            dialog.dismiss()
            var intent : Intent = Intent(requireActivity(), com.wellme.PaymentActivity::class.java)
            intent.putExtra("amount", s1)
            startActivityForResult(intent, 1002)
        })

    }

    fun processPayment(f1 : Double){
        var paypalPayment : PayPalPayment = PayPalPayment(BigDecimal(f1.toString()), "USD", "Purchase Goods", PayPalPayment.PAYMENT_INTENT_SALE)
        var intnet : Intent = Intent(requireContext(), PaymentActivity::class.java)
        intnet.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION,config)
        intnet.putExtra(PaymentActivity.EXTRA_PAYMENT,paypalPayment)
        startActivityForResult(intnet, PAYPAL_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == PAYPAL_REQUEST_CODE){
            if(resultCode == Activity.RESULT_OK){
                    var paymentConfirmation : PaymentConfirmation = data!!.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION)
                    if(paymentConfirmation!=null){
                        var paymentDetails : String = paymentConfirmation.toJSONObject().toString(4)
                        if(!UtilMethod.instance.isStringNullOrNot(paymentDetails)){
                            var jsonObject : JSONObject = JSONObject(paymentDetails)
                            if(jsonObject!=null){
                                var responseJSON : JSONObject = jsonObject.getJSONObject("response")
                                if(responseJSON!=null){
                                    var calendar : Calendar = Calendar.getInstance()
                                    var end_calendar : Calendar = Calendar.getInstance()
                                    var purchase_calendar : Calendar = Calendar.getInstance()
                                    var validity: Int =0
                                    var transaction_id : String = responseJSON.getString("id")
                                    var state : String = responseJSON.getString("state")
                                    if(!UtilMethod.instance.isStringNullOrNot(s_end_date)){
                                        var end_date : Date = UtilMethod.instance.getDate1(s_end_date)



                                        var end_ms : Long = 0
                                        var current_ms : Long = calendar.timeInMillis
                                        if(!UtilMethod.instance.isStringNullOrNot(s_validity)){
                                            validity = s_validity!!.toInt()
                                        }
                                        if(end_date!=null){
                                            end_ms = end_date.time
                                            if(end_ms>current_ms){
                                                calendar.time = end_date
                                                calendar.add(Calendar.SECOND, 1)

                                            }
                                            else{

                                            }

                                        }
                                        end_calendar.time = calendar.time
                                        end_calendar.add(Calendar.DAY_OF_YEAR, validity)
                                        var s_start_date : String? = UtilMethod.instance.getDateFormat(calendar.time)
                                        var s_end_date : String? = UtilMethod.instance.getDateFormat(end_calendar.time)
                                        var s_purchased_date : String? = UtilMethod.instance.getDateFormat(purchase_calendar.time)
                                        callAddTransaction(transaction_id, s_start_date!!, s_end_date!!, s_purchased_date!!, "Paypal")

                                    }
                                    else{
                                        end_calendar.time = calendar.time
                                        if(!UtilMethod.instance.isStringNullOrNot(s_validity)){
                                            validity = s_validity!!.toInt()
                                        }
                                        end_calendar.add(Calendar.DAY_OF_YEAR, validity)
                                        var s_start_date : String? = UtilMethod.instance.getDateFormat(calendar.time)
                                        var s_end_date : String? = UtilMethod.instance.getDateFormat(end_calendar.time)
                                        var s_purchased_date : String? = UtilMethod.instance.getDateFormat(purchase_calendar.time)
                                        callAddTransaction(transaction_id, s_start_date!!, s_end_date!!, s_purchased_date!!, "Paypal")
                                    }

                                }
                            }
                        }
                    }
                }
        }
        else if(requestCode == 1002){
            if(resultCode == Activity.RESULT_OK){
                var validity: Int =0
                var purchase_calendar : Calendar = Calendar.getInstance()
                var paymentDetails : String = data!!.getStringExtra("razorpay_id")
                if(!UtilMethod.instance.isStringNullOrNot(paymentDetails) && !UtilMethod.instance.isStringNullOrNot(s_end_date)){
                    var end_date : Date = UtilMethod.instance.getDate1(s_end_date)
                    var calendar : Calendar = Calendar.getInstance()
                    var end_calendar : Calendar = Calendar.getInstance()

                    var end_ms : Long = 0
                    var current_ms : Long = calendar.timeInMillis
                    if(!UtilMethod.instance.isStringNullOrNot(s_validity)){
                        validity = s_validity!!.toInt()
                    }
                    if(end_date!=null){
                        end_ms = end_date.time
                        if(end_ms>current_ms){
                            calendar.time = end_date
                            calendar.add(Calendar.SECOND, 1)

                        }
                        else{

                        }

                    }
                    end_calendar.time = calendar.time
                    if(!UtilMethod.instance.isStringNullOrNot(s_validity)){
                        validity = s_validity!!.toInt()
                    }
                    end_calendar.add(Calendar.DAY_OF_YEAR, validity)
                    var s_start_date : String? = UtilMethod.instance.getDateFormat(calendar.time)
                    var s_end_date : String? = UtilMethod.instance.getDateFormat(end_calendar.time)
                    var s_purchased_date : String? = UtilMethod.instance.getDateFormat(purchase_calendar.time)
                    Log.v("Call Appi", "yes")
                    callAddTransaction(paymentDetails, s_start_date!!, s_end_date!!, s_purchased_date!!, "Razorpay")

                }
                else if(!UtilMethod.instance.isStringNullOrNot(paymentDetails)){
                    var calendar : Calendar = Calendar.getInstance()
                    var end_calendar : Calendar = Calendar.getInstance()
                    end_calendar.time = calendar.time
                    if(!UtilMethod.instance.isStringNullOrNot(s_validity)){
                        validity = s_validity!!.toInt()
                    }
                    end_calendar.add(Calendar.DAY_OF_YEAR, validity)
                    var s_start_date : String? = UtilMethod.instance.getDateFormat(calendar.time)
                    var s_end_date : String? = UtilMethod.instance.getDateFormat(end_calendar.time)
                    var s_purchased_date : String? = UtilMethod.instance.getDateFormat(purchase_calendar.time)
                    Log.v("Call Appi", "yes")
                    callAddTransaction(paymentDetails, s_start_date!!, s_end_date!!, s_purchased_date!!, "Razorpay")
                }
            }
        }
    }


    fun callAddTransaction(trans_id: String, plan_start_date : String, plan_end_date : String, purchased_on : String, type : String){

        var progressDialog : ProgressDialog = ProgressDialog.show(requireContext(), "", "")
        progressDialog.setContentView(R.layout.progress_loader)
        progressDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        progressDialog.setCancelable(false)

        val service = Retrofit.Builder()
            .baseUrl(""+ AppConstants.BASE_URL_NEW)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(AppService::class.java)


        service.callAddTransaction("Bearer "+access_token,
            ""+s_plan_id,
            ""+s_plan_amount,
            ""+plan_start_date,
            ""+plan_end_date,
                trans_id,
            ""+purchased_on,
            ""+type).enqueue(object :
            Callback<ResponseBody> {

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

}