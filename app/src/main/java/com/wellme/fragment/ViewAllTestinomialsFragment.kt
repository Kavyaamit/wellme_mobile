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
import android.provider.MediaStore
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.wellme.LeftSideMenuActivity
import com.wellme.R
import com.wellme.adapter.GoalForUpdateAdapter
import com.wellme.adapter.OffersViewAllAdapter
import com.wellme.adapter.TestinomialsLikeListAdapter
import com.wellme.adapter.TestinomialsViewAllAdapter
import com.wellme.databinding.FragmentTestinomialBinding
import com.wellme.dto.OfferListDTO
import com.wellme.dto.TestimonialDTO
import com.wellme.dto.TestimonialLikeDTO
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

class ViewAllTestinomialsFragment : Fragment(), View.OnClickListener{
    var binding : FragmentTestinomialBinding? = null
    var linearLayoutManager : LinearLayoutManager? = null
    var linearLayoutManagerlike : LinearLayoutManager? = null
    var testinomial_list : List<TestimonialDTO> = ArrayList()
    var testinomial_like_list : List<TestimonialLikeDTO> = ArrayList()
    var testinomialAdapter : TestinomialsViewAllAdapter? = null
    var testinomial_like_Adapter : TestinomialsLikeListAdapter? = null
    var regular : Typeface? = null
    var activity : Activity? = null
    var access_token : String? = ""
    var testinomial_id : String? = null

    var rv_like_list : RecyclerView? = null

    var dialog : Dialog? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_testinomial, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        linearLayoutManager = LinearLayoutManager(requireContext())
        linearLayoutManager!!.orientation = LinearLayoutManager.VERTICAL
        binding!!.rvTestimonials.layoutManager = linearLayoutManager
        regular = ResourcesCompat.getFont(requireContext(), R.font.poppins_regular)
        binding!!.header.setTypeface(regular)
        binding?.back!!.setOnClickListener(this)
        access_token = UtilMethod.instance.getAccessToken(requireContext())
        Log.d("token",">>>"+access_token);
        callTestinomialAPI()
    }



    fun setLikeListAdapter(){

        linearLayoutManagerlike = LinearLayoutManager(requireContext())
        linearLayoutManagerlike!!.orientation = LinearLayoutManager.VERTICAL
        rv_like_list?.layoutManager = linearLayoutManagerlike

        testinomial_like_Adapter = TestinomialsLikeListAdapter(requireContext(), testinomial_like_list)
        if(testinomial_list!=null){
           rv_like_list?.adapter = testinomial_like_Adapter
        }

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


        service.getTestinomialList("Bearer "+access_token).enqueue(object :
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
                        testinomial_list = gson.fromJson(object1.getJSONArray("result").toString(), Array<TestimonialDTO>::class.java).toList()
                        testinomialAdapter = TestinomialsViewAllAdapter(requireContext(), testinomial_list ,onItemClickCallbackLike , onItemClickCallbackComment,onItemClickCallback,onItemClickLikeCount,onItemClickCommentCount,testinomial_id)
                        if(testinomial_list!=null){
                            binding?.rvTestimonials?.adapter = testinomialAdapter
                        }
                    }
                }
                else{

                }
            }
        })
    }


    fun callGetLikeListAPI(){

        var progressDialog : ProgressDialog = ProgressDialog.show(requireContext(), "", "")
        progressDialog.setContentView(R.layout.progress_loader)
        progressDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        progressDialog.setCancelable(false)

        val service = Retrofit.Builder()
            .baseUrl(""+ AppConstants.BASE_URL_NEW)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(AppService::class.java)


        service.getTestinomialLikeList("Bearer "+access_token,testinomial_id).enqueue(object :
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
                        testinomial_like_list = gson.fromJson(object1.getJSONArray("user_list").toString(), Array<TestimonialLikeDTO>::class.java).toList()
                        showLikeDialog()

                    }
                }
                else{

                }
            }
        })
    }


    private val onItemClickCallback : OnItemClickListener.OnItemClickCallback = object : OnItemClickListener.OnItemClickCallback{

        override fun onItemClicked(view: View?, position: Int) {
           var  testimonialDTO: TestimonialDTO= testinomial_list.get(position)
            Log.d("testimonialDTO>>",">>"+testimonialDTO);

            var fragmentTransaction : FragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
            var fragment : Fragment = ViewAllTestinomialsDetails()
            var bun : Bundle = Bundle()
            bun.putString("first_name",testimonialDTO.first_name)
            bun.putString("last_name",testimonialDTO.last_name)
            bun.putString("created_on",testimonialDTO.created_on)
            bun.putString("image",testimonialDTO.image)
            bun.putString("media_type",testimonialDTO.media_type)
            bun.putString("youtube",testimonialDTO.youtube)
            bun.putString("like_status",testimonialDTO.like_status)
            bun.putString("short_description",testimonialDTO.short_description)
            bun.putString("description",testimonialDTO.description)
            bun.putString("id",testimonialDTO.id)

            fragment.arguments = bun
            fragmentTransaction?.replace(R.id.container_home, fragment)
            fragmentTransaction?.addToBackStack(null)
            fragmentTransaction?.commit()



//            if(testinomialAdapter!=null){
//                testinomialAdapter?.setData(testinomial_id)
//                testinomialAdapter?.notifyDataSetChanged()
//            }
//            callTestinomialLike(testinomial_id,"1")
        }

    }

    private val onItemClickCallbackLike : OnItemClickListener.OnItemClickCallback = object : OnItemClickListener.OnItemClickCallback{

        override fun onItemClicked(view: View?, position: Int) {
            testinomial_id = testinomial_list.get(position).id
            var status = testinomial_list.get(position).like_status
            var s_like_count : String? = testinomial_list.get(position).total_like
            var count : Int = 0
            if(!UtilMethod.instance.isStringNullOrNot(s_like_count)){
                count = s_like_count!!.toInt()
            }

            if(testinomialAdapter!=null){
                testinomialAdapter?.setData(testinomial_id)
                testinomialAdapter?.notifyDataSetChanged()
            }

            if (status.equals("1")){
                count-=1
                status = "0"
                callTestinomialLike(testinomial_id,"0")
            }
            else if(status.equals("0")){
                count+=1
                status = "1"
                callTestinomialLike(testinomial_id,"1")
            }
                testinomial_list.get(position).total_like = ""+count
                testinomial_list.get(position).like_status = status
                if(testinomialAdapter!=null){
                    testinomialAdapter!!.setListValue(testinomial_list)
                }
        }
    }

    private val onItemClickCommentCount : OnItemClickListener.OnItemClickCallback = object : OnItemClickListener.OnItemClickCallback{

        override fun onItemClicked(view: View?, position: Int) {
//            testinomial_id = testinomial_list.get(position).id
//            var status = testinomial_list.get(position).like_status
//            if(testinomialAdapter!=null){
//                testinomialAdapter?.setData(testinomial_id)
//                testinomialAdapter?.notifyDataSetChanged()
//            }
//
//            if (status.equals("1")){
//                callTestinomialLike(testinomial_id,"0")
//            }else if(status.equals("0")){
//                callTestinomialLike(testinomial_id,"1")
//            }
        }
    }

    private val onItemClickLikeCount : OnItemClickListener.OnItemClickCallback = object : OnItemClickListener.OnItemClickCallback{

        override fun onItemClicked(view: View?, position: Int) {
            testinomial_id = testinomial_list.get(position).id

            callGetLikeListAPI()

//            var status = testinomial_list.get(position).like_status
//            if(testinomialAdapter!=null){
//                testinomialAdapter?.setData(testinomial_id)
//                testinomialAdapter?.notifyDataSetChanged()
//            }
//
//            if (status.equals("1")){
//                callTestinomialLike(testinomial_id,"0")
//            }else if(status.equals("0")){
//                callTestinomialLike(testinomial_id,"1")
//            }
        }
    }

    private val onItemClickCallbackComment : OnItemClickListener.OnItemClickCallback = object : OnItemClickListener.OnItemClickCallback{

        override fun onItemClicked(view: View?, position: Int) {


            var  testimonialDTO: TestimonialDTO= testinomial_list.get(position)
            Log.d("testimonialDTO>>",">>"+testimonialDTO);

            var fragmentTransaction : FragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
            var fragment : Fragment = ViewAllTestinomialsDetails()
            var bun : Bundle = Bundle()
            bun.putString("first_name",testimonialDTO.first_name)
            bun.putString("last_name",testimonialDTO.last_name)
            bun.putString("created_on",testimonialDTO.created_on)
            bun.putString("image",testimonialDTO.image)
            bun.putString("media_type",testimonialDTO.media_type)
            bun.putString("youtube",testimonialDTO.youtube)
            bun.putString("like_status",testimonialDTO.like_status)
            bun.putString("short_description",testimonialDTO.short_description)
            bun.putString("description",testimonialDTO.description)
            bun.putString("id",testimonialDTO.id)

            fragment.arguments = bun
            fragmentTransaction?.replace(R.id.container_home, fragment)
            fragmentTransaction?.addToBackStack(null)
            fragmentTransaction?.commit()



        }

    }


    fun callTestinomialLike(testimonialID : String?,staus:String?){

        var progressDialog : ProgressDialog = ProgressDialog.show(requireContext(), "", "")
        progressDialog.setContentView(R.layout.progress_loader)
        progressDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        progressDialog.setCancelable(false)

        val service = Retrofit.Builder()
            .baseUrl(""+ AppConstants.BASE_URL_NEW)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(AppService::class.java)


        service.callTestimonialLike("Bearer "+access_token,testimonialID,staus).enqueue(object :
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
                //callTestinomialAPI()

//                if(!UtilMethod.instance.isStringNullOrNot(response1)){
//                    var object1 : JSONObject = JSONObject(response1)
//                    if(object1!=null){
//                        var gson : Gson = Gson()
//                        testinomial_list = gson.fromJson(object1.getJSONArray("offer_data").toString(), Array<OfferListDTO>::class.java).toList()
//
//                        if(testinomial_list!=null){
//                            binding?.rvTestimonials?.adapter = OffersViewAllAdapter(requireContext(), testinomial_list)
//                        }
//                    }
//                }
//                else{
//
//                }
            }
        })
    }


    override fun onClick(v: View?) {
        if(v == binding?.back){
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    fun showLikeDialog(){
        var muscle_mass : Int = 0

        dialog = Dialog(requireContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog!!.setContentView(R.layout.dialog_testnomials_like_list)
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(this.resources.getColor(R.color.transparent_black)))
        dialog!!.show()

        var ivCross : ImageView? = dialog!!.findViewById(R.id.iv_cross)
        rv_like_list  = dialog!!.findViewById(R.id.rv_like_list)


        setLikeListAdapter()

        ivCross?.setOnClickListener(View.OnClickListener { view ->

            dialog!!.dismiss()

        })

    }


}