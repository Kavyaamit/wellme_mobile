package com.wellme.fragment

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.wellme.GalleryImageListActivity
import com.wellme.LeftSideMenuActivity
import com.wellme.R
import com.wellme.adapter.*
import com.wellme.databinding.FragmentMedicalDiscriptionBinding
import com.wellme.dto.MedicalConditionListDTO
import com.wellme.utils.AppConstants
import com.wellme.utils.AppService
import com.wellme.utils.OnItemClickListener
import com.wellme.utils.UtilMethod
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

class MedicalDiscriptionFragment : Fragment(), View.OnClickListener{
    var binding : FragmentMedicalDiscriptionBinding? = null
    var linearLayoutManager : LinearLayoutManager? = null
    var medical_condition_list : List<MedicalConditionListDTO> = ArrayList()
    var medicalConditionAdapter : MedicalConditionListAdapter? = null
    var regular : Typeface? = null
    var activity : Activity? = null
    var access_token : String? = ""

    var s_medical_condition_id : String? = null
    var s_document_type : String? = ""
    var s_medical_document : String? = ""

    var bundle : Bundle? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_medical_discription, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        linearLayoutManager = LinearLayoutManager(requireContext())
        linearLayoutManager!!.orientation = LinearLayoutManager.VERTICAL
        binding!!.rvMedicalCondition.layoutManager = linearLayoutManager
        regular = ResourcesCompat.getFont(requireContext(), R.font.poppins_regular)
        binding!!.header.setTypeface(regular)
        binding?.back!!.setOnClickListener(this)
        binding?.ivUpload!!.setOnClickListener(this)
        access_token = UtilMethod.instance.getAccessToken(requireContext())
        Log.d("token",">>>"+access_token);

        bundle = arguments
        if(bundle!=null) {
            s_medical_condition_id = bundle!!.getString("medical_condition_id")

            Log.d("medicalId",">>>"+s_medical_condition_id)
        }

        callMedicalConditionListAPI()

    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        this.activity = activity
    }

    override fun onResume() {
        super.onResume()
        (activity as LeftSideMenuActivity).disableBottomBar()

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
                DialogInterface.OnClickListener { dialog, which -> if(status == 1){callMedicalConditionListAPI()}else if(status==2){callUploadMedicalDoc()} })
            alertDialog.setNegativeButton(context!!.resources!!.getString(R.string.network_cancel), DialogInterface.OnClickListener { dialog, which -> })
            var alertDialog1 = alertDialog.create()
            alertDialog1.show()
        }
        catch (e: Exception){

        }
    }




    fun callMedicalConditionListAPI(){

        var progressDialog : ProgressDialog = ProgressDialog.show(requireContext(), "", "")
        progressDialog.setContentView(R.layout.progress_loader)
        progressDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        progressDialog.setCancelable(false)

        val service = Retrofit.Builder()
            .baseUrl(""+ AppConstants.BASE_URL_NEW)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(AppService::class.java)


        service.getMedicalConditionList("Bearer "+access_token).enqueue(object :
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
                Log.v("Data Response ", "==> "+response1)
                if(!UtilMethod.instance.isStringNullOrNot(response1)){
                    var object1 : JSONObject = JSONObject(response1)
                    if(object1!=null){
                        var gson : Gson = Gson()
                        medical_condition_list = gson.fromJson(object1.getJSONArray("User_medical_condition").toString(), Array<MedicalConditionListDTO>::class.java).toList()
                        if(medical_condition_list!=null){
//                            for(i in 0..medical_condition_list!!.size-1){
//                                callUserBodyMeasurementTask(medical_condition_list!!.get(i).id)
//                            }
                            binding?.rvMedicalCondition!!.adapter = MedicalConditionListAdapter(requireContext(), medical_condition_list, onItemClickCallback)
                        }
                    }
                }
                else{

                }
            }
        })
    }

    private val onItemClickCallback : OnItemClickListener.OnItemClickCallback = object : OnItemClickListener.OnItemClickCallback{

        override fun onItemClicked(view: View?, position: Int) {
           var  medicalDTO: MedicalConditionListDTO= medical_condition_list.get(position)
            Log.d("testimonialDTO>>",">>"+medicalDTO);

            if (medicalDTO.document_type.equals("image")){
                var fragmentTransaction : FragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
                var fragment : Fragment = ViewMedicalConditionReportFragment()
                var bun : Bundle = Bundle()

                bun.putString("image",medicalDTO.medical_document)
                bun.putString("document_type",medicalDTO.document_type)


                fragment.arguments = bun
                fragmentTransaction?.replace(R.id.container_home, fragment)
                fragmentTransaction?.addToBackStack(null)
                fragmentTransaction?.commit()

            }else if (medicalDTO.document_type.equals("file")){

//                val name:String? = AppConstants.IMAGE_URL_NEW1+""+medicalDTO.medical_document;

                val url:String="https://docs.google.com/gview?embedded=true&url="+AppConstants.IMAGE_URL_NEW1+""+medicalDTO.medical_document


                val fullPath: String =
                    java.lang.String.format(Locale.ENGLISH, url, "PDF_URL_HERE")
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(fullPath))
                startActivity(browserIntent)

//                openDocument(name)

            }
        }

    }





    fun callUploadMedicalDoc(){

        var progressDialog : ProgressDialog = ProgressDialog.show(requireContext(), "", "")
        progressDialog.setContentView(R.layout.progress_loader)
        progressDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        progressDialog.setCancelable(false)
//        Log.d("image",">>"+s_comment_image)
        val service = Retrofit.Builder()
            .baseUrl(""+ AppConstants.BASE_URL_NEW)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(AppService::class.java)



        var f1 : File = File(s_medical_document)


        //pass it like this
        val requestFile : RequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), f1)
        val body : MultipartBody.Part = MultipartBody.Part.createFormData("medical_document", f1.name, requestFile)

//        val requestVideo : RequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), f2)
//        val videoBody : MultipartBody.Part = MultipartBody.Part.createFormData("comment_video", f2.name, requestVideo)

        val medicalConditionID : RequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), s_medical_condition_id)
        val documentType : RequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), s_document_type)

//        val requestFile :RequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), f1)
// MultipartBody.Part is used to send also the actual file name


        if(!UtilMethod.instance.isStringNullOrNot(s_medical_document)){
            Log.d("image",">>>"+body)
            service.callUploadMedicalDoc("Bearer "+access_token,medicalConditionID,documentType,body).enqueue(object :
                Callback<ResponseBody> {

                /* The HTTP call failed. This method is run on the main thread */
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.d("TAG_", "An error happened!")
                    progressDialog.dismiss()
                    t.printStackTrace()
                    dialogForCheckNetworkError(2)
                }

                /* The HTTP call was successful, we should still check status code and response body
                 * on a production app. This method is run on the main thread */
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    /* This will print the response of the network call to the Logcat */
                    Log.v("Notification Response ", "==> "+response.body())
                    progressDialog.dismiss()
//                    callGetCommentListAPI()
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
//        else if(!UtilMethod.instance.isStringNullOrNot(s_comment_video)){
//            Log.d("video",">>>"+videoBody)
//            service.callTestimonialVideo("Bearer "+access_token,testinomialsId,commentType,videoBody).enqueue(object :
//                Callback<ResponseBody> {
//
//                /* The HTTP call failed. This method is run on the main thread */
//                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
//                    Log.d("TAG_", "An error happened!")
//                    progressDialog.dismiss()
//                    t.printStackTrace()
//                    dialogForCheckNetworkError()
//                }
//
//                /* The HTTP call was successful, we should still check status code and response body
//                 * on a production app. This method is run on the main thread */
//                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
//                    /* This will print the response of the network call to the Logcat */
//                    Log.v("Notification Response ", "==> "+response.body())
//                    progressDialog.dismiss()
////                    callGetCommentListAPI()
////                var response1 : String = response.body()!!.string()
////                Log.v("Notification Response ", "==> "+response1)
//
//
//
////                if(!UtilMethod.instance.isStringNullOrNot(response1)){
////                    var object1 : JSONObject = JSONObject(response1)
////                    if(object1!=null){
////                        var gson : Gson = Gson()
////                        testinomial_list = gson.fromJson(object1.getJSONArray("offer_data").toString(), Array<OfferListDTO>::class.java).toList()
////
////                        if(testinomial_list!=null){
////                            binding?.rvTestimonials?.adapter = OffersViewAllAdapter(requireContext(), testinomial_list)
////                        }
////                    }
////                }
////                else{
////
////                }
//                }
//            })
//
//        }


    }





    override fun onClick(v: View?) {
        if(v == binding?.back){
            requireActivity().supportFragmentManager.popBackStack()
        }else if(v == binding?.ivUpload){
            var intent : Intent = Intent(requireActivity(), GalleryImageListActivity::class.java)
            intent.putExtra("type","3")
            startActivityForResult(intent, 1001)

        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 1001){
            if(resultCode == 101){
                var bundle: Bundle? = data?.extras
                if (bundle!=null) {
                    var imagePath: String? = bundle?.getString("imagePath");
                    s_medical_document = imagePath
                    s_document_type = "image"


                    callUploadMedicalDoc()
                }

            }else  if(resultCode == 102){
                var bundle: Bundle? = data?.extras
                if (bundle!=null) {
                    var imagePath: String? = bundle?.getString("imagePath");
                    s_medical_document = imagePath
                    s_document_type = "video"

                    callUploadMedicalDoc()
                }

            }else  if(resultCode == 103){
                var bundle: Bundle? = data?.extras
                if (bundle!=null) {
                    var imagePath: String? = bundle?.getString("imagePath");
                    s_medical_document = imagePath
                    s_document_type = "file"
                    Log.d("image ",">>>"+imagePath)
                    callUploadMedicalDoc()
                }

            }
        }

    }


    fun openDocument(name: String?) {
        val intent = Intent(Intent.ACTION_VIEW)
        val file = File(name)
        val extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(file).toString())
        val mimetype = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
        if (extension.equals("", ignoreCase = true) || mimetype == null) {
            // if there is no extension or there is no definite mimetype, still try to open the file
            intent.setDataAndType(Uri.fromFile(file), "text/*")
        } else {
            intent.setDataAndType(Uri.fromFile(file), mimetype)
        }
        // custom message for the intent
        startActivity(Intent.createChooser(intent, "Choose an Application:"))
    }


}