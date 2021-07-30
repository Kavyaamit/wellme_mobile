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
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.wellme.LeftSideMenuActivity
import com.wellme.R
import com.wellme.adapter.AllBlogAdapter
import com.wellme.databinding.FragmentBlogBinding
import com.wellme.dto.BlogDTO
import com.wellme.dto.TestimonialDTO
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

class ViewAllBlogFragment : Fragment(), View.OnClickListener{
    var binding : FragmentBlogBinding? = null
    var linearLayoutManager : LinearLayoutManager? = null
    var blog_list : List<BlogDTO> = ArrayList()
    var regular : Typeface? = null
    var activity : Activity? = null
    var access_token : String? = ""
    var blog_id : String? = ""
    var blogAdapter : AllBlogAdapter? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_blog, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        linearLayoutManager = LinearLayoutManager(requireContext())
        linearLayoutManager!!.orientation = LinearLayoutManager.VERTICAL
        binding!!.rvBlog.layoutManager = linearLayoutManager
        regular = ResourcesCompat.getFont(requireContext(), R.font.poppins_regular)
        binding!!.header.setTypeface(regular)
        binding!!.back?.setOnClickListener(this)
        access_token = UtilMethod.instance.getAccessToken(context!!)
        callBlogAPI()
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
                DialogInterface.OnClickListener { dialog, which -> callBlogAPI() })
            alertDialog.setNegativeButton(context!!.resources!!.getString(R.string.network_cancel), DialogInterface.OnClickListener { dialog, which -> requireActivity().supportFragmentManager.popBackStack() })
            var alertDialog1 = alertDialog.create()
            alertDialog1.show()
        }
        catch (e: Exception){

        }
    }

    fun callBlogAPI(){

        var progressDialog : ProgressDialog = ProgressDialog.show(requireContext(), "", "")
        progressDialog.setContentView(R.layout.progress_loader)
        progressDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        progressDialog.setCancelable(false)

        val service = Retrofit.Builder()
            .baseUrl(""+ AppConstants.BASE_URL_NEW)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(AppService::class.java)


        service.getBlogList("Bearer "+access_token).enqueue(object :
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
                        blog_list = gson.fromJson(object1.getJSONArray("data").toString(), Array<BlogDTO>::class.java).toList()
                        if(blog_list!=null){
                            blogAdapter = AllBlogAdapter(requireContext(), blog_list,onItemClickCallbackLike , onItemClickCallbackComment,onItemClickCallback, blog_id)
                            binding?.rvBlog!!.adapter = blogAdapter
                        }
                    }
                }
                else{

                }
            }
        })
    }

    private val onItemClickCallbackLike : OnItemClickListener.OnItemClickCallback = object : OnItemClickListener.OnItemClickCallback{
        override fun onItemClicked(view: View?, position: Int) {
            var id : String = blog_list.get(position).id
            var status : String = blog_list.get(position).like_status
            var like_count : Int = 0
            var s_like_count : String = blog_list.get(position).total_like
            if(!UtilMethod.instance.isStringNullOrNot(s_like_count)){
                like_count = s_like_count.toInt()
            }
            if(!UtilMethod.instance.isStringNullOrNot(status)){
                if(status.equals("1")){
                    status = "0"
                    if(like_count>0){
                        like_count-=1
                    }

                }
                else{
                    status = "1"
                    like_count+=1

                }
                callBlogLike(id, status)
                if(blogAdapter!=null){
                    blog_list.get(position).like_status = status
                    blog_list.get(position).total_like = ""+like_count
                    blogAdapter!!.setListValue(blog_list)
                }
            }
        }

    }

    private val onItemClickCallbackComment : OnItemClickListener.OnItemClickCallback = object : OnItemClickListener.OnItemClickCallback{
        override fun onItemClicked(view: View?, position: Int) {
            var fragmentTransaction : FragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
            var fragment : Fragment = BlogDetailsFragment()
            var bundle : Bundle = Bundle()
            var dto : BlogDTO = blog_list.get(position)
            bundle.putSerializable("blog_object", dto)
            fragment.arguments = bundle
            fragmentTransaction?.replace(R.id.container_home, fragment)
            fragmentTransaction?.addToBackStack(null)
            fragmentTransaction?.commit()
        }

    }

    private val onItemClickCallback : OnItemClickListener.OnItemClickCallback = object : OnItemClickListener.OnItemClickCallback{
        override fun onItemClicked(view: View?, position: Int) {

        }

    }


    fun callBlogLike(blogId : String?,staus:String?){

        var progressDialog : ProgressDialog = ProgressDialog.show(requireContext(), "", "")
        progressDialog.setContentView(R.layout.progress_loader)
        progressDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        progressDialog.setCancelable(false)

        val service = Retrofit.Builder()
            .baseUrl(""+ AppConstants.BASE_URL_NEW)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(AppService::class.java)


        service.callBlogLike("Bearer "+access_token, blogId, staus).enqueue(object :
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

            }
        })
    }



    override fun onClick(v: View?) {
        if(v == binding?.back){
            requireActivity().supportFragmentManager.popBackStack()
        }
    }
}