package com.wellme.fragment

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.wellme.R
import com.wellme.adapter.BlogAdapter
import com.wellme.adapter.BodyMeasurementAdapter
import com.wellme.adapter.WeightGoalProgressAdapter
import com.wellme.databinding.FragmentBodyMeasurementBinding
import com.wellme.dto.*
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

class BodyMeasurementFragment : Fragment(), View.OnClickListener{
    var binding : FragmentBodyMeasurementBinding? = null
    var body_type_list : List<BodyTypeDTO>? = ArrayList()
    var body_measurent_list : List<BodyMeasurentDTO>? = ArrayList()
    var linearLayoutManager : LinearLayoutManager? = null
    var access_token : String? = ""
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_body_measurement, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getView1()
    }

    fun getView1(){
        binding!!.ivBack.setOnClickListener(this)
        linearLayoutManager = LinearLayoutManager(requireContext())
        linearLayoutManager!!.orientation = LinearLayoutManager.VERTICAL
        binding?.rvBodyType!!.layoutManager = linearLayoutManager
        access_token = UtilMethod.instance.getAccessToken(requireContext())
        callBodyMeasurementAPI()
    }

    override fun onClick(v: View?) {
        if(v == binding!!.ivBack){
            activity!!.supportFragmentManager.popBackStack()
        }
    }

    fun callBodyMeasurementAPI(){

        var progressDialog : ProgressDialog = ProgressDialog.show(requireContext(), "", "")
        progressDialog.setContentView(R.layout.progress_loader)
        progressDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        progressDialog.setCancelable(false)

        val service = Retrofit.Builder()
            .baseUrl(""+ AppConstants.BASE_URL_NEW)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(AppService::class.java)


        service.getDataList().enqueue(object :
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
                Log.v("Data Response ", "==> "+response1)

                if(!UtilMethod.instance.isStringNullOrNot(response1)){
                    var object1 : JSONObject = JSONObject(response1)
                    Log.v("Data Response ", "==> "+object1.getJSONArray("body_type"))
                    if(object1!=null){
                        var gson : Gson = Gson()
                        body_type_list = gson.fromJson(object1.getJSONArray("body_type").toString(), Array<BodyTypeDTO>::class.java).toList()
                        if(body_type_list!=null){
                            for(i in 0..body_type_list!!.size-1){
                                callUserBodyMeasurementTask(body_type_list!!.get(i).id)
                            }

                        }
                    }
                }
                else{

                }
            }
        })
    }

    fun callUserBodyMeasurementTask(s1 : String){

        var progressDialog : ProgressDialog = ProgressDialog.show(requireContext(), "", "")
        progressDialog.setContentView(R.layout.progress_loader)
        progressDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        progressDialog.setCancelable(false)

        val service = Retrofit.Builder()
            .baseUrl(""+ AppConstants.BASE_URL_NEW)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(AppService::class.java)

        service.callUserBodyMeasurement("Bearer "+access_token, s1).enqueue(object :
            Callback<ResponseBody> {

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                progressDialog.dismiss()
                t.printStackTrace()
                dialogForCheckNetworkError()
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                progressDialog.dismiss()
                var code : Int = response.raw().code()
               // binding?.fullLayout!!.visibility = View.VISIBLE
                if(code >199 && code < 300) {
                    var response1 : String = response.body()!!.string()
                    if(!UtilMethod.instance.isStringNullOrNot(response1)){
                        Log.v("Body Measurement", "==> "+response1)
                        var obj : JSONObject = JSONObject(response1)
                        var success : Int = obj.getInt("success")
                        if(success == 200){
                            var gson : Gson = Gson()
                            //userProfileWeight = gson.fromJson(obj.getJSONObject("user_profile").toString(), UserProfileWeightDTO::class.java)
                            body_measurent_list = gson.fromJson(obj.getJSONArray("body_measurement_data").toString(), Array<BodyMeasurentDTO>::class.java).toList()

                            Log.d("measurent List",">>>"+body_measurent_list)



                            if(body_measurent_list!=null){
                                if(body_measurent_list!!.size>0){
                                    var total_cal : Float = 0f

                                    for(i in 0..body_measurent_list!!.size - 1){
                                        var dto: BodyMeasurentDTO = body_measurent_list!!.get(i)
                                        if(dto!=null && body_type_list!=null){
                                            var s_total_measurent : String = dto.body_measurement_value
                                            var body_type: String = dto.body_measurement_type
                                            if(!UtilMethod.instance.isStringNullOrNot(s_total_measurent)){
                                                total_cal+=s_total_measurent.toFloat()
                                            }
                                            for(j in 0..body_type_list!!.size-1){
                                                var s_type = body_type_list!!.get(j).id
                                                if(!UtilMethod.instance.isStringNullOrNot(body_type) && !UtilMethod.instance.isStringNullOrNot(s_type)){
                                                    if(s_type.equals(body_type, true)){
                                                        var list1 : ArrayList<BodyMeasurentDTO> = body_type_list!!.get(j).measurent_list
                                                        if(list1==null){
                                                            list1 = ArrayList()
                                                        }
                                                        list1.add(dto)
                                                        body_type_list!!.get(j).measurent_list = list1
                                                        break
                                                    }
                                                }
                                            }
                                        }
                                    }





//                                    binding?.tvTotalCal!!.setText(UtilMethod.instance.getFormatedAmountString(total_cal)+" of 2500 Cal")

                                }






                            }



                            if (body_type_list!=null){
                                binding?.rvBodyType!!.adapter = BodyMeasurementAdapter(requireContext(), body_type_list, onItemClickListener)
                            }
                        }

                    }
                }
            }
        })
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
                DialogInterface.OnClickListener { dialog, which -> callBodyMeasurementAPI() })
            alertDialog.setNegativeButton(context!!.resources!!.getString(R.string.network_cancel), DialogInterface.OnClickListener { dialog, which -> requireActivity().supportFragmentManager.popBackStack() })
            var alertDialog1 = alertDialog.create()
            alertDialog1.show()
        }
        catch (e: Exception){

        }
    }

    var onItemClickListener : OnItemClickListener.OnItemClickCallback = object : OnItemClickListener.OnItemClickCallback{
        override fun onItemClicked(view: View?, position: Int) {
            if(body_type_list!=null){

                Log.v("Body Type ", "==> "+body_type_list!!.get(position).id)

                val dto:BodyTypeDTO = body_type_list!!.get(position);
                val list : ArrayList<BodyMeasurentDTO> = dto.measurent_list

                var measurentValue : String = "00.00";

                if (list!=null && list.size>0){
                    measurentValue = list.get(list.size-1).body_measurement_value
                }


                var fragmentTransaction : FragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
                var fragment : Fragment = AddBodyMeasurementFragment()
                var bun : Bundle = Bundle()
                bun.putString("id",dto.id)
                bun.putString("name",dto.name)
                bun.putString("value",measurentValue)


                fragment.arguments = bun
                fragmentTransaction?.replace(R.id.container_home, fragment)
                fragmentTransaction?.addToBackStack(null)
                fragmentTransaction?.commit()



            }
        }

    }
}