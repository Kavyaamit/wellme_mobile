 package com.wellme.fragment

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
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
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import com.wellme.LeftSideMenuActivity
import com.wellme.R
import com.wellme.VideoActivity
import com.wellme.adapter.CommentListAdapter
import com.wellme.databinding.FragmentTestinomialDetailsBinding
import com.wellme.dto.BlogDTO
import com.wellme.dto.CommentListDTO
import com.wellme.dto.TestimonialDTO
import com.wellme.utils.AppConstants
import com.wellme.utils.AppService
import com.wellme.utils.UtilMethod
import kotlinx.android.synthetic.main.item_subscribe.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

 class ViewAllTestinomialsDetails : Fragment(), View.OnClickListener{
    var binding : FragmentTestinomialDetailsBinding? = null
    var regular : Typeface? = null
    var activity : Activity? = null
    var linearLayoutManager : LinearLayoutManager? = null
    var comment_list : List<CommentListDTO> = ArrayList()
    var commentListAdapter : CommentListAdapter? = null
    var bundle : Bundle? = null
    var access_token : String? = ""
    var s_first_name : String? = ""
    var s_last_name : String? = ""
    var s_name : String? = ""
    var s_short_description : String? = ""
    var s_description : String? = ""
    var s_created_on : String? = ""
    var s_media_type : String? = ""
    var s_youtube : String? = ""
    var s_image : String? = ""
    var s_like_status : String? = ""
    var s_testinomials_id : String? = ""

    var s_comment : String? = ""
    var s_comment_type : String? = ""
    var s_comment_image : String? = ""
    var s_comment_youtube_link : String? = ""
    var s_comment_video : String? = ""

     var imageDialog : BottomSheetDialog? = null
     var dto : TestimonialDTO? = null




    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_testinomial_details, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        linearLayoutManager = LinearLayoutManager(requireContext())
        linearLayoutManager!!.orientation = LinearLayoutManager.VERTICAL
        binding!!.rvChatlist.layoutManager = linearLayoutManager

        regular = ResourcesCompat.getFont(requireContext(), R.font.poppins_regular)
        binding!!.header.setTypeface(regular)
        binding!!.etComment.setTypeface(regular)
        binding!!.tvName.setTypeface(regular)
        binding!!.tvDiscription.setTypeface(regular)
        binding!!.tvPostedTime.setTypeface(regular)
        binding?.back!!.setOnClickListener(this)
        binding?.tvSend?.setOnClickListener(this)
        binding?.ivPin?.setOnClickListener(this)
        binding?.ivLike?.setOnClickListener(this)



        access_token = UtilMethod.instance.getAccessToken(requireContext())

        bundle = arguments



        if(bundle!=null){
            dto = bundle!!.getSerializable("testinomial_object") as TestimonialDTO

            setData()
        }
        callGetCommentListAPI()

    }

    fun setData(){


        s_first_name = dto?.first_name
        s_last_name = dto?.last_name
        s_short_description = dto?.short_description
        s_description = dto?.description
        s_created_on = dto?.created_on
        s_image = dto?.image
        s_media_type = dto?.media_type
        s_youtube = dto?.youtube
        s_like_status = dto?.like_status
        s_testinomials_id = dto?.id


        if(!UtilMethod.instance.isStringNullOrNot(s_first_name)){
            s_name = s_first_name
        }
        if(!UtilMethod.instance.isStringNullOrNot(s_last_name)){
            if(UtilMethod.instance.isStringNullOrNot(s_name)){
                s_name = s_last_name
            }
            else{
                s_name+=" "+s_last_name
            }
        }
        if(!UtilMethod.instance.isStringNullOrNot(s_media_type)){
            if(s_media_type.equals("image", true)){
                if(!UtilMethod.instance.isStringNullOrNot(s_image)){
                    Picasso.get().load(AppConstants.IMAGE_URL_NEW1+""+s_image).into(binding?.testinomialImage)
                    binding?.testinomialImage?.visibility =View.VISIBLE

                }
                else{
                    binding?.testinomialImage?.visibility =View.GONE
                }
                binding?.rlImgVideo?.visibility = View.GONE
                binding?.testinomialImage?.visibility = View.VISIBLE
            }
            else{
                binding?.rlImgVideo?.visibility = View.VISIBLE
                binding?.testinomialImage?.visibility = View.GONE
                binding?.testinomialVideo?.let { setImage(it, s_youtube) }
            }
        }
        else{
            binding?.rlImgVideo?.visibility = View.GONE
            binding?.testinomialImage?.visibility = View.VISIBLE
            if(!UtilMethod.instance.isStringNullOrNot(s_image)){
                Picasso.get().load(AppConstants.IMAGE_URL_NEW+""+s_image).into(binding?.testinomialImage)
                binding?.testinomialImage?.visibility =View.VISIBLE
            }
            else{
                binding?.testinomialImage?.visibility =View.GONE
            }
        }

        if(!UtilMethod.instance.isStringNullOrNot(s_name)){
            binding?.tvName?.setText(""+s_name)
            binding?.tvName!!.visibility = View.VISIBLE
        }
        else{
            binding?.tvName!!.visibility = View.GONE
        }
            if(!UtilMethod.instance.isStringNullOrNot(s_short_description)){
                binding?.tvTitle?.setText(s_short_description)
                binding?.tvTitle!!.visibility = View.VISIBLE
            }
            else{
                binding?.tvTitle!!.visibility = View.GONE
            }

            if (!UtilMethod.instance.isStringNullOrNot(s_description)){
                binding?.tvDiscription!!.setText(s_description)
                binding?.tvDiscription!!.visibility = View.VISIBLE
            }
            else{
                binding?.tvDiscription!!.visibility = View.GONE
            }

        if(!UtilMethod.instance.isStringNullOrNot(s_created_on)){
            binding?.tvPostedTime?.setText("Posted on : "+UtilMethod.instance.getDate(s_created_on))
            binding?.tvPostedTime?.visibility = View.VISIBLE
        }
        else{
            binding?.tvPostedTime?.visibility = View.GONE
        }


if(!UtilMethod.instance.isStringNullOrNot(s_like_status)){
    if (s_like_status.equals("1")){
        binding?.ivLike?.setImageDrawable(context?.resources?.getDrawable(R.drawable.like_blue_round))
    }else {
        binding?.ivLike?.setImageDrawable(context?.resources?.getDrawable(R.drawable.thumb_blue_rounded))
    }
        }

    }


    fun setImage(iv : ImageView, url : String?){
        var id : String? = UtilMethod.instance.getVideoIdFromYoutubeUrl(url)
        Log.v("Blod Video ID ", "==> "+id)
        Picasso.get().load("https://img.youtube.com/vi/"+id+"/0.jpg").error(R.drawable.default_image).into(iv)
        iv.setOnClickListener(View.OnClickListener { var intent : Intent = Intent(context, VideoActivity::class.java)

            intent.putExtra("url", url)
            context?.startActivity(intent) })
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
                DialogInterface.OnClickListener { dialog, which -> callGetCommentListAPI() })
            alertDialog.setNegativeButton(context!!.resources!!.getString(R.string.network_cancel), DialogInterface.OnClickListener { dialog, which -> requireActivity().supportFragmentManager.popBackStack() })
            var alertDialog1 = alertDialog.create()
            alertDialog1.show()
        }
        catch (e: Exception){

        }
    }


     fun showimageDialog(){
         var muscle_mass : Int = 0

         imageDialog = BottomSheetDialog(requireContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen)
         imageDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
         imageDialog!!.setContentView(R.layout.dialog_select_image)
         imageDialog!!.window!!.setBackgroundDrawable(ColorDrawable(requireContext().resources.getColor(R.color.transparent_black)))
         imageDialog!!.show()

         var llGallery : LinearLayout? = imageDialog!!.findViewById(R.id.ll_gallery)
         var llCamera : LinearLayout? = imageDialog!!.findViewById(R.id.ll_camera)
         var llVideo : LinearLayout? = imageDialog!!.findViewById(R.id.ll_video)
         var llFile : LinearLayout? = imageDialog!!.findViewById(R.id.ll_file)
         var llclose : LinearLayout? = imageDialog!!.findViewById(R.id.ll_close)
         var llMain : LinearLayout? = imageDialog!!.findViewById(R.id.ll_main)

         var animation : Animation = AnimationUtils.loadAnimation(requireContext(),R.anim.bottom_up_animation)
         var animation1 : Animation = AnimationUtils.loadAnimation(requireContext(),R.anim.bottom_down_animation)

         llMain?.animation = animation




//         tvGallery!!.setTypeface(regular)
//         tvCamera!!.setTypeface(regular)




         llGallery?.setOnClickListener(View.OnClickListener { view ->


             var intent : Intent = Intent(requireActivity(), com.wellme.GalleryImageListActivity::class.java)
            startActivityForResult(intent, 101)

             imageDialog!!.dismiss()
         })
         llCamera?.setOnClickListener(View.OnClickListener { view ->

//             var intent : Intent = Intent(requireActivity(), com.wellme.GalleryImageListActivity::class.java)
//            startActivityForResult(intent, 102)


         })
         llVideo?.setOnClickListener(View.OnClickListener { view ->

//             var intent : Intent = Intent(requireActivity(), com.wellme.GalleryImageListActivity::class.java)
//            startActivityForResult(intent, 103)


         })
         llFile?.setOnClickListener(View.OnClickListener { view ->

//             var intent : Intent = Intent(requireActivity(), com.wellme.GalleryImageListActivity::class.java)
//            startActivityForResult(intent, 104)


         })

         llclose?.setOnClickListener(View.OnClickListener { view ->

             llMain?.animation = animation1
             var handler: Handler = Handler()

             var dismissRunner: Runnable = Runnable {

                 kotlin.run {
                     if (imageDialog != null)
                         imageDialog!!.dismiss();
                 }
             };handler.postDelayed(dismissRunner, 2000)


//             var intent : Intent = Intent(requireActivity(), com.wellme.GalleryImageListActivity::class.java)
//            startActivityForResult(intent, 104)


         })
     }




    fun callGetCommentListAPI(){

        var progressDialog : ProgressDialog = ProgressDialog.show(requireContext(), "", "")
        progressDialog.setContentView(R.layout.progress_loader)
        progressDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        progressDialog.setCancelable(false)

        val service = Retrofit.Builder()
            .baseUrl(""+ AppConstants.BASE_URL_NEW)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(AppService::class.java)


        service.callTestimonialCommentList("Bearer "+access_token,s_testinomials_id).enqueue(object :
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
                        comment_list = gson.fromJson(object1.getJSONArray("user_list").toString(), Array<CommentListDTO>::class.java).toList()
                        commentListAdapter = CommentListAdapter(requireContext(), comment_list)
                        if(comment_list!=null){
                            binding?.rvChatlist?.adapter = commentListAdapter
                            if(comment_list.size > 1) {
                                binding?.rvChatlist!!.scrollToPosition(comment_list.size - 1)

                            }
                        }
                    }
                }
                else{

                }
            }
        })
    }




    fun callTestinomialComment(){

        var progressDialog : ProgressDialog = ProgressDialog.show(requireContext(), "", "")
        progressDialog.setContentView(R.layout.progress_loader)
        progressDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        progressDialog.setCancelable(false)
Log.d("image",">>"+s_comment_image)
        val service = Retrofit.Builder()
            .baseUrl(""+ AppConstants.BASE_URL_NEW)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(AppService::class.java)



        var f1 : File = File(s_comment_image)
        var f2 : File = File(s_comment_video)
        Log.v("File Name ", "====> "+f1.name)
        Log.v("File Name ", "====> "+f2.name)


        //pass it like this
        val requestFile :RequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), f1)
        val body : MultipartBody.Part = MultipartBody.Part.createFormData("comment_image", f1.name, requestFile)

        val requestVideo :RequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), f2)
        val videoBody : MultipartBody.Part = MultipartBody.Part.createFormData("comment_video", f2.name, requestVideo)

        val testinomialsId :RequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), s_testinomials_id)
        val commentType :RequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), s_comment_type)
        val commentText :RequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), s_comment)
//        val requestFile :RequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), f1)
// MultipartBody.Part is used to send also the actual file name


        if(!UtilMethod.instance.isStringNullOrNot(s_comment)){

            service.callTestimonialCommentText("Bearer "+access_token,testinomialsId,commentText,commentType).enqueue(object :
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
                    Log.v("Notification Response ", "==> "+response.body())
                    progressDialog.dismiss()
//                var response1 : String = response.body()!!.string()
//                Log.v("Notification Response ", "==> "+response1)

                    binding?.etComment?.setText("")
                    callGetCommentListAPI()

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
        else if(!UtilMethod.instance.isStringNullOrNot(s_comment_image)){
            Log.d("image",">>>"+body)
            service.callTestimonialImage("Bearer "+access_token,testinomialsId,commentType,body).enqueue(object :
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
                    Log.v("Notification Response ", "==> "+response.body())
                    progressDialog.dismiss()
                    callGetCommentListAPI()
//                var response1 : String = response.body()!!.string()
//                Log.v("Notification Response ", "==> "+response1)

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
        else if(!UtilMethod.instance.isStringNullOrNot(s_comment_video)){
            Log.d("video",">>>"+videoBody)
            service.callTestimonialVideo("Bearer "+access_token,testinomialsId,commentType,videoBody).enqueue(object :
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
                    Log.v("Notification Response ", "==> "+response.body())
                    progressDialog.dismiss()
                    callGetCommentListAPI()
//                var response1 : String = response.body()!!.string()
//                Log.v("Notification Response ", "==> "+response1)



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

                 if(!UtilMethod.instance.isStringNullOrNot(staus)){
                     s_like_status = staus;
                     if (staus.equals("1")){
                         binding?.ivLike?.setImageDrawable(context?.resources?.getDrawable(R.drawable.like_blue_round))
                     }else {
                         binding?.ivLike?.setImageDrawable(context?.resources?.getDrawable(R.drawable.thumb_blue_rounded))

                     }
                 }


//                 callGetCommentListAPI()

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
        }else if(v == binding?.tvSend){
            s_comment_image = ""
            s_comment_type = "text"
            s_comment_video = ""
            s_comment_youtube_link = ""
            s_comment = binding?.etComment!!.text.toString()
            if(!UtilMethod.instance.isStringNullOrNot(s_comment)){
                callTestinomialComment()
            }

        }else if(v == binding?.ivPin){

            var intent : Intent = Intent(requireActivity(), com.wellme.GalleryImageListActivity::class.java)
            startActivityForResult(intent, 1001)

        }else if(v == binding?.ivLike){

            if (s_like_status.equals("1")){
                callTestinomialLike(s_testinomials_id,"0")
            }else if(s_like_status.equals("0")){
                callTestinomialLike(s_testinomials_id,"1")
            }

        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 1001){
            if(resultCode == 101){
                var bundle: Bundle? = data?.extras
                if (bundle!=null) {
                    var imagePath: String? = bundle?.getString("imagePath");
                    s_comment_image = imagePath
                    s_comment_type = "image"
                    s_comment = ""
                    s_comment_video = ""
                    s_comment_youtube_link = ""
                    callTestinomialComment()
                }

            }else  if(resultCode == 102){
                var bundle: Bundle? = data?.extras
                if (bundle!=null) {
                    var imagePath: String? = bundle?.getString("imagePath");
                    s_comment_image = ""
                    s_comment_type = "video"
                    s_comment = ""
                    s_comment_video = imagePath
                    s_comment_youtube_link = ""
                    callTestinomialComment()
                }

            }
        }

    }


}