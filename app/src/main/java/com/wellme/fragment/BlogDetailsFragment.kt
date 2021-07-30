package com.wellme.fragment

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import com.wellme.R
import com.wellme.VideoActivity
import com.wellme.adapter.BlogCommentAdapter
import com.wellme.databinding.FragmentBlogDetailsBinding
import com.wellme.dto.BlogCommentDTO
import com.wellme.dto.BlogDTO
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

class BlogDetailsFragment : Fragment(), View.OnClickListener {
    var binding : FragmentBlogDetailsBinding? = null
    var regular : Typeface? = null
    var bold : Typeface? = null
    var bundle : Bundle? = null
    var dto : BlogDTO? = null
    var s_title : String? = ""
    var s_description : String? = ""
    var s_created_on : String? = ""
    var s_type : String? = ""
    var s_blog_image : String? = ""
    var s_link : String? = ""
    var s_status : String? = ""
    var s_id : String? = ""
    var access_token : String? = ""
    var s_comment : String? = ""
    var linearLayoutManager : LinearLayoutManager? = null
    var blogCommentAdapter : BlogCommentAdapter? = null
    var comment_list : List<BlogCommentDTO>? = null
    var gson : Gson? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_blog_details, container, false)
        return binding!!.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getView1()
    }

    fun getView1(){
        bundle = arguments
        if(bundle!=null){
            dto = bundle!!.getSerializable("blog_object") as BlogDTO
        }
        regular = ResourcesCompat.getFont(requireContext(), R.font.poppins_regular)
        bold = ResourcesCompat.getFont(requireContext(), R.font.poppins_bold)
        access_token = UtilMethod.instance.getAccessToken(requireContext())
        binding?.tvTitle!!.setTypeface(bold)
        binding?.tvDescription!!.setTypeface(regular)
        binding?.etComment!!.setTypeface(regular)
        binding?.header!!.setTypeface(regular)
        binding?.tvPostedTime!!.setTypeface(regular)
        binding!!.ivLike.setOnClickListener(this)
        binding!!.back.setOnClickListener(this)
        binding!!.ivPlay.setOnClickListener(this)
        binding!!.ivSend.setOnClickListener(this)
        linearLayoutManager = LinearLayoutManager(requireContext())
        linearLayoutManager!!.orientation = LinearLayoutManager.VERTICAL
        binding!!.rvChatlist.layoutManager = linearLayoutManager
        if(dto!=null){
            s_title = dto!!.blog_title
            s_description = dto!!.blog_description
            s_created_on = dto!!.added_on
            s_type = dto!!.blog_type
            s_blog_image = dto!!.blog_image
            s_link = dto!!.blog_link
            s_status = dto!!.like_status
            s_id = dto!!.id
            setData(binding?.tvTitle, s_title)
            setData(binding?.tvDescription, s_description)
            setData1(binding?.tvPostedTime, s_created_on)
            if(!UtilMethod.instance.isStringNullOrNot(s_type)){
                binding?.cvYoutube!!.visibility
                if(s_type.equals("image", true)){
                    Picasso.get().load(AppConstants.IMAGE_URL_NEW1+""+s_blog_image).into(binding!!.ivBlog)
                    binding!!.ivBlog.visibility = View.VISIBLE
                    binding?.rlImgVideo!!.visibility = View.GONE
                }
                else{
                    setYoutoube(binding!!.ivVideo, s_link)
                    binding?.rlImgVideo!!.visibility = View.VISIBLE
                }
            }
            else{
                binding?.cvYoutube!!.visibility = View.GONE
            }

            if(!UtilMethod.instance.isStringNullOrNot(s_status)){
                if (s_status.equals("1")){
                    binding!!.ivLike.setImageDrawable(context!!.resources.getDrawable(R.drawable.like_blue))
                }
                else {
                    binding!!.ivLike.setImageDrawable(context!!.resources.getDrawable(R.drawable.like))
                }
            }
            else{
                binding!!.ivLike.setImageDrawable(context!!.resources.getDrawable(R.drawable.like))
            }

            callBlogCommentListAPI()
        }

    }

    fun setYoutoube(iv : ImageView, url : String?){
        var id : String? = UtilMethod.instance.getVideoIdFromYoutubeUrl(url)
        Log.v("Blod Video ID ", "==> "+id)
        Picasso.get().load("https://img.youtube.com/vi/"+id+"/0.jpg").error(R.drawable.default_image).into(iv)
    }

    fun setData(tv : TextView?, value : String?){
        if(!UtilMethod.instance.isStringNullOrNot(value)){
            tv!!.setText(value)
            tv!!.visibility = View.VISIBLE
        }
        else{
            tv!!.visibility = View.GONE
        }
    }

    fun setData1(tv : TextView?, value : String?){
        if(!UtilMethod.instance.isStringNullOrNot(value)){
            tv!!.setText("Posted On : "+UtilMethod.instance.getDate(value))
            tv!!.visibility = View.VISIBLE
        }
        else{
            tv!!.visibility = View.GONE
        }
    }

    override fun onClick(p0: View?) {
        if(p0 == binding!!.ivPlay){
            var intent : Intent = Intent(context, VideoActivity::class.java)
            intent.putExtra("url", s_link)
            context!!.startActivity(intent)
        }
        else if(p0 == binding!!.back){
            requireActivity().supportFragmentManager.popBackStack()
        }
        else if(p0 == binding!!.ivLike){
            if(!UtilMethod.instance.isStringNullOrNot(s_status)){
                if(s_status.equals("0", true)){
                    binding!!.ivLike.setImageDrawable(context!!.resources.getDrawable(R.drawable.like_blue))
                    s_status = "1"
                }
                else{
                    binding!!.ivLike.setImageDrawable(context!!.resources.getDrawable(R.drawable.like))
                    s_status = "0"
                }
            }
            else{
                binding!!.ivLike.setImageDrawable(context!!.resources.getDrawable(R.drawable.like_blue))
                s_status = "1"
            }
            callBlogLike()
        }
        else if(p0 == binding!!.ivSend){
            validData()
        }
    }

    fun validData(){
        s_comment = binding!!.etComment.text.toString()
        if(!UtilMethod.instance.isStringNullOrNot(s_comment)){
            callBlogCommentAPI()
        }
    }

    fun callBlogCommentListAPI(){
        binding!!.etComment.setText("")

        var progressDialog : ProgressDialog = ProgressDialog.show(requireContext(), "", "")
        progressDialog.setContentView(R.layout.progress_loader)
        progressDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        progressDialog.setCancelable(false)

        val service = Retrofit.Builder()
            .baseUrl(""+ AppConstants.BASE_URL_NEW)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(AppService::class.java)


        service.callBlogCommentList("Bearer "+access_token, s_id).enqueue(object :
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
                var response1 : String = response.body()!!.string()
                gson = Gson()
                var object1 : JSONObject = JSONObject(response1)
                comment_list = gson!!.fromJson(object1.getJSONArray("user_list").toString(), Array<BlogCommentDTO>::class.java).toList()
                if(comment_list!=null){
                    blogCommentAdapter = BlogCommentAdapter(requireContext(), comment_list)
                    binding?.rvChatlist!!.adapter = blogCommentAdapter
                    if(comment_list!!.size>1){
                        binding?.rvChatlist!!.scrollToPosition(comment_list!!.size-1)
                    }
                }
                Log.v("Comment List", response1)



            }
        })
    }

    fun callBlogCommentAPI(){
        binding!!.etComment.setText("")

        var progressDialog : ProgressDialog = ProgressDialog.show(requireContext(), "", "")
        progressDialog.setContentView(R.layout.progress_loader)
        progressDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        progressDialog.setCancelable(false)

        val service = Retrofit.Builder()
            .baseUrl(""+ AppConstants.BASE_URL_NEW)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(AppService::class.java)


        service.callBlogComment("Bearer "+access_token, s_id, "0", s_comment).enqueue(object :
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
                var response1 : String = response.body()!!.string()
                callBlogCommentListAPI()


            }
        })
    }



    fun callBlogLike(){

        var progressDialog : ProgressDialog = ProgressDialog.show(requireContext(), "", "")
        progressDialog.setContentView(R.layout.progress_loader)
        progressDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        progressDialog.setCancelable(false)

        val service = Retrofit.Builder()
            .baseUrl(""+ AppConstants.BASE_URL_NEW)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(AppService::class.java)


        service.callBlogLike("Bearer "+access_token, s_id, s_status).enqueue(object :
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
                var response1 : String = response.body()!!.string()
                Log.v("Notification Response ", "==> "+response1)
                //callTestinomialAPI()

            }
        })
    }

}