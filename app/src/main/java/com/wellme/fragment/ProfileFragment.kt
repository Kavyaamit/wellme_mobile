package com.wellme.fragment

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.content.ActivityNotFoundException
import android.content.DialogInterface
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.gson.Gson
import com.wellme.Camera
import com.wellme.LeftSideMenuActivity
import com.wellme.R
import com.wellme.adapter.GalleryImageAdapter
import com.wellme.databinding.FragmentProfileBinding
import com.wellme.dto.UserDTO
import com.wellme.utils.AppConstants
import com.wellme.utils.AppService
import com.wellme.utils.OnPermissonResult1
import com.wellme.utils.UtilMethod
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.File
import java.util.*

class ProfileFragment : Fragment(), View.OnClickListener{

    var binding : FragmentProfileBinding? = null
    var userDTO : UserDTO? = null
    var s_goal_name : String? = ""
    var s_city_name : String? = ""
    var s_state_name : String? = ""
    var s_end_date : String? = ""
    var s_access_token : String? = ""
    var s_first_name  : String? = ""
    var s_last_name  : String? = ""
    var s_name  : String? = ""
    var activity1 : Activity? = null
    private val REQUEST_PERMISSION = 100
    private val REQUEST_IMAGE_CAPTURE = 1
    private val REQUEST_PICK_IMAGE = 2
    var picturePath : String = ""
    var cropPicturePath : String? = ""
    var CROP : Int = 113
    var bioDialog : Dialog? = null
    var s_bio : String? = ""
    var s_medical_condition_id : String? = ""


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        this.activity1 = activity
    }

    override fun onResume() {
        super.onResume()
        (activity1 as LeftSideMenuActivity).enableBottomBar()
        (activity1 as LeftSideMenuActivity).setActiveSection(4)

    }

    fun initView(){
        var regular : Typeface? = ResourcesCompat.getFont(requireContext(), R.font.poppins_regular)
        binding?.tvBio?.setTypeface(regular)
        binding?.tvBioDetail?.setTypeface(regular)
        binding?.tvCoach?.setTypeface(regular)
        binding?.tvCoachDetail?.setTypeface(regular)
        binding?.tvBasicInformation?.setTypeface(regular)
        binding?.tvBasicInformationDetail?.setTypeface(regular)
        binding?.tvFoodPreference?.setTypeface(regular)
        binding?.tvFoodPreferenceDetail?.setTypeface(regular)
        binding?.tvBodyMeasurement?.setTypeface(regular)
        binding?.tvBodyMeasurementDetail?.setTypeface(regular)
        binding?.tvPayment?.setTypeface(regular)
        binding?.tvPaymentDetail?.setTypeface(regular)
        binding?.header?.setTypeface(regular)
        binding?.tvUserName?.setTypeface(regular)
        binding?.tvMedicalCondition?.setTypeface(regular)
        binding?.tvLlMedicalConditionDate?.setTypeface(regular)
        binding?.tvAddress?.setTypeface(regular)
        binding?.tvGoPro?.setTypeface(regular)
        binding?.tvPassword?.setTypeface(regular)
        binding?.tvPasswordDetail?.setTypeface(regular)
        binding?.ivEdit?.setOnClickListener(this)
        binding?.llPaymentDetails?.setOnClickListener(this)
        binding?.llFoodPreference?.setOnClickListener(this)
        binding?.llBasicInformation?.setOnClickListener(this)
        binding?.llBodyMeasurement?.setOnClickListener(this)
        binding?.back!!.setOnClickListener(this)
        binding?.llCoach?.setOnClickListener(this)
        binding?.llBio?.setOnClickListener(this)
        binding?.ivProfile?.setOnClickListener(this)
        binding?.llChangePassword?.setOnClickListener(this)
        binding?.llMedicalCondition?.setOnClickListener(this)
        userDTO = UtilMethod.instance.getUser(requireContext())
        s_access_token = UtilMethod.instance.getAccessToken(requireContext())

        callGetProfileTask()
    }

    fun showBioDialog(){
        var regular : Typeface? = ResourcesCompat.getFont(requireContext(), R.font.poppins_regular)
        bioDialog = Dialog(requireContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        bioDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        bioDialog!!.setContentView(R.layout.dialog_update_bio)
        bioDialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        bioDialog!!.show()
        var tv_header : TextView = bioDialog!!.findViewById(R.id.tv_header)
        var et_bio : EditText = bioDialog!!.findViewById(R.id.et_bio)
        var btn_update : Button = bioDialog!!.findViewById(R.id.btn_update)
        tv_header.setTypeface(regular)
        btn_update.setTypeface(regular)
        et_bio.setTypeface(regular)
        if(!UtilMethod.instance.isStringNullOrNot(s_bio)){
            et_bio.setText(s_bio)
        }
        btn_update.setOnClickListener(View.OnClickListener {view ->
            s_bio = et_bio.text.toString()
            if(UtilMethod.instance.isStringNullOrNot(s_bio)){
                UtilMethod.instance.dialogOK(requireContext(), "", "Please enter bio", requireContext().resources.getString(R.string.ok), false)
            }
            else{
                bioDialog!!.dismiss()
                callUpdateBioTask()
            }

        })



    }

    fun callUpdateBioTask(){

        var progressDialog : ProgressDialog = ProgressDialog.show(requireContext(), "", "")
        progressDialog.setContentView(R.layout.progress_loader)
        progressDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        progressDialog.setCancelable(false)

        val service = Retrofit.Builder()
            .baseUrl(""+ AppConstants.BASE_URL_NEW)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(AppService::class.java)

        service.updateBio("Bearer "+s_access_token, s_bio).enqueue(object :
            Callback<ResponseBody> {

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                progressDialog.dismiss()
                t.printStackTrace()
                dialogForCheckNetworkError()
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                progressDialog.dismiss()
                var code : Int = response.raw().code()
                if(code >199 && code < 300) {
                    var response1 : String = response.body()!!.string()
                    if(!UtilMethod.instance.isStringNullOrNot(response1)){
                        var obj : JSONObject = JSONObject(response1)
                        var success : Int = obj.getInt("success")
                        if(success == 200){
                            userDTO = Gson().fromJson(obj.getJSONObject("user_profile").toString(), UserDTO::class.java)
                            if(userDTO!=null){
                                UtilMethod.instance.setUser(requireContext(), userDTO)
                                setData()

                            }
                        }
                        else{

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
                DialogInterface.OnClickListener { dialog, which -> callGetProfileTask() })
            alertDialog.setNegativeButton(context!!.resources!!.getString(R.string.network_cancel), DialogInterface.OnClickListener { dialog, which -> requireActivity().supportFragmentManager.popBackStack() })
            var alertDialog1 = alertDialog.create()
            alertDialog1.show()
        }
        catch (e: Exception){

        }
    }

    fun callGetProfileTask(){

        var progressDialog : ProgressDialog = ProgressDialog.show(requireContext(), "", "")
        progressDialog.setContentView(R.layout.progress_loader)
        progressDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        progressDialog.setCancelable(false)

        val service = Retrofit.Builder()
            .baseUrl(""+ AppConstants.BASE_URL_NEW)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(AppService::class.java)

        Log.v("Token", "==> "+s_access_token)

        service.callGetProfileTask("Bearer "+s_access_token).enqueue(object :
            Callback<ResponseBody> {

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                progressDialog.dismiss()
                t.printStackTrace()
                dialogForCheckNetworkError()
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                progressDialog.dismiss()
                var code : Int = response.raw().code()
                Log.v("API Code ", "==> "+code)
                if(code >199 && code < 300) {
                    var response1 : String = response.body()!!.string()
                    if(!UtilMethod.instance.isStringNullOrNot(response1)){
                        var obj : JSONObject = JSONObject(response1)
                        var success : Int = obj.getInt("success")
                        Log.v("Success Code ", "==> "+success)
                        if(success == 200){
                            Log.v("User Response ", "==> "+response1)
                            var userDTO : UserDTO = Gson().fromJson(obj.getJSONObject("user_profile").toString(), UserDTO::class.java)
                            if(userDTO!=null){
                                UtilMethod.instance.setUser(requireContext(), userDTO)
                                s_end_date = obj.getString("end_date")
                                if(!UtilMethod.instance.isStringNullOrNot(s_end_date)){
                                    var date : Date = UtilMethod.instance.getDate1(s_end_date)
                                    Log.v("Date Bhai ", "==> "+date)
                                    if(date!=null){
                                        var calendar : Calendar = Calendar.getInstance()
                                        var cur_date : Date = calendar.time
                                        var date_ms : Long = date.time
                                        var cur_date_ms : Long = cur_date.time
                                        if(cur_date_ms>date_ms){
                                            binding?.llCoach!!.visibility = View.GONE
                                            binding?.v1!!.visibility = View.GONE
                                        }
                                        else{
                                            binding?.llCoach!!.visibility = View.VISIBLE
                                            binding?.v1!!.visibility = View.VISIBLE
                                        }
                                    }
                                    else{
                                        binding?.llCoach!!.visibility = View.GONE
                                        binding?.v1!!.visibility = View.GONE
                                    }
                                }
                                else{
                                    binding?.llCoach!!.visibility = View.GONE
                                    binding?.v1!!.visibility = View.GONE
                                }

                                setData()

                            }
                        }
                        else{
                            var fragmentTransaction : FragmentTransaction? = activity?.supportFragmentManager?.beginTransaction()
                            var fragment : Fragment = RegistrationWithPersonalDetailsFragment()
                            fragmentTransaction?.replace(R.id.container_splash, fragment)
                            fragmentTransaction?.addToBackStack(null)
                            fragmentTransaction?.commit()

                        }

                    }
                }

            }
        })
    }

    fun setData(){
        if(userDTO!=null){
            s_goal_name = userDTO!!.goals
            s_city_name = userDTO!!.city
            s_state_name = userDTO!!.state
            s_bio = userDTO!!.bio
            s_medical_condition_id = userDTO!!.medical_condition_id
            var location : String? = ""
            if(!UtilMethod.instance.isStringNullOrNot(s_city_name)){
                location = s_city_name
            }
            if(!UtilMethod.instance.isStringNullOrNot(location) && !UtilMethod.instance.isStringNullOrNot(s_state_name)){
                location+=", "+s_state_name
            }
            else if(!UtilMethod.instance.isStringNullOrNot(s_state_name)){
                location = s_state_name
            }

            if(!UtilMethod.instance.isStringNullOrNot(location)){
                binding!!.tvAddress.setText(location)
            }
            else{
                binding!!.tvAddress.visibility = View.GONE
            }

            s_first_name = userDTO?.first_name
            s_last_name = userDTO?.last_name
            if(!UtilMethod.instance.isStringNullOrNot(s_first_name)){
                s_name = s_first_name
            }
            if(!UtilMethod.instance.isStringNullOrNot(s_last_name) && !UtilMethod.instance.isStringNullOrNot(s_name)){
                s_name += " "+s_last_name
            }
            else{
                s_name = s_last_name
            }
            if(!UtilMethod.instance.isStringNullOrNot(s_name)){
                binding?.tvUserName?.setText(s_name)
            }
            else{
                //binding?.tvUserName?.setText("")
            }

        }
    }

    override fun onClick(v: View?) {
        if(v == binding?.llBasicInformation || v == binding?.ivEdit){
            var fragmentTransaction : FragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.container_home, UpdateProfileFragment())
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }
        else if(v == binding?.llFoodPreference){
            var fragmentTransaction : FragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.container_home, UpdateFoodPreferenceFragment())
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }
        else if(v == binding?.llMedicalCondition){
            var fragmentTransaction : FragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
            //fragmentTransaction.replace(R.id.container_home, UpdateBodyMeasurementFragment())


            var fragment : Fragment = MedicalDiscriptionFragment()

            Log.d("medicalId",">>>"+s_medical_condition_id)
            var bun : Bundle = Bundle()
            bun.putString("medical_condition_id",s_medical_condition_id)

            fragment.arguments = bun


            fragmentTransaction.replace(R.id.container_home,fragment )
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }
        else if(v == binding?.llChangePassword){
            var fragmentTransaction : FragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.container_home, ChangePasswordFragment())
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }
        else if(v == binding?.llBodyMeasurement){
            var fragmentTransaction : FragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.container_home, BodyMeasurementFragment())
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }
        else if(v == binding?.llPaymentDetails){
            var fragmentTransaction : FragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.container_home, PaymentFragment())
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }
        else if(v == binding?.llCoach){
            var fragmentTransaction : FragmentTransaction? = requireActivity().supportFragmentManager.beginTransaction()
            fragmentTransaction?.replace(R.id.container_home, PickCoachFragment())
            fragmentTransaction?.addToBackStack(null)
            fragmentTransaction?.commit()
        }
        else if(v == binding?.ivProfile){
            openGallery()
        }
        else if(v == binding?.llBio){
            showBioDialog()
        }
        else if(v == binding?.back){
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    private fun openGallery() {
        var image_list : ArrayList<String> = getAllShownImagesPath(activity!!)
        Log.v("Image Size ", "==> "+image_list.size)
        showGalleryDialog(image_list)
    }

    fun showGalleryDialog(list : ArrayList<String>){
        var dialog : Dialog = Dialog(context!!,android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        dialog!!.setContentView(R.layout.dialog_grid)
        var image_grid : GridView = dialog!!.findViewById(R.id.image_grid)
        var back : ImageView = dialog!!.findViewById(R.id.back)
        var galleryImageAdapter : GalleryImageAdapter = GalleryImageAdapter(context!!, list)
        image_grid.adapter = galleryImageAdapter
        dialog!!.show()
        back.setOnClickListener(View.OnClickListener { view ->
            dialog!!.dismiss()
        })

        image_grid.onItemClickListener = object : AdapterView.OnItemClickListener{
            override fun onItemClick(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                dialog!!.dismiss()
                var uriImage : Uri? = null
                picturePath = list.get(position)
                var cursor : Cursor? = context!!.contentResolver!!
                    .query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        arrayOf(MediaStore.Images.Media._ID),
                        MediaStore.Images.Media.DATA + "=? ",
                        arrayOf(picturePath), null)
                if(cursor!=null && cursor.moveToFirst()){
                    var id1 : Int = cursor!!.getInt(cursor!!.getColumnIndex(MediaStore.MediaColumns._ID))
                    uriImage = Uri.parse("content://media/external/images/media/" + id1)
                }
                performCrop( uriImage )
            }

        }
    }

    fun performCrop(picUri : Uri?){
        try {
            var cropIntent : Intent  = Intent( "com.android.camera.action.CROP" )
            cropIntent.setDataAndType( picUri, "image/*" )
            cropIntent.putExtra( "crop", "true" )
            cropIntent.putExtra( MediaStore.EXTRA_OUTPUT, getTempUri())
            Log.v("Image Path 1 ", "==> "+getTempUri())
            cropIntent.putExtra( "outputFormat",
                Bitmap.CompressFormat.JPEG.toString() )
            startActivityForResult( cropIntent, CROP )
        } catch (e : ActivityNotFoundException) {
            e.printStackTrace()
            Log.v("Exception1234 ", "==> "+e)
            Toast.makeText( context,
                getString( R.string.crop_action_support ), Toast.LENGTH_SHORT )
                .show()
        } catch (e : Exception ) {
            e.printStackTrace()
            Log.v("Exception12 ", "==> "+e)
            Toast.makeText( context,
                getString( R.string.crop_action_support ), Toast.LENGTH_SHORT )
                .show()
        }
    }

    fun getTempUri() : Uri{
        return Uri.fromFile(getTempFile())
    }

    fun getTempFile() : File {
        var imageName : String = "CROP_" + System.currentTimeMillis() + ".jpg"
        var tempFile : File = UtilMethod.instance.getNewFile(context!!, UtilMethod.instance.IMAGE_DIRECTORY_CROP, imageName)
        cropPicturePath = tempFile!!.path

        Log.v("File Path", "==> "+cropPicturePath)

        UtilMethod.instance.setCropImagePath(context!!, cropPicturePath)
        return tempFile
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        Log.v("Data ", " "+data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                val bitmap = data?.extras?.get("data") as Bitmap
                binding!!.ivProfile.setImageBitmap(bitmap)
            }
            else if (requestCode == REQUEST_PICK_IMAGE) {
                val uri = data?.getData()
                Log.v("ImageUri", "==> "+uri)
                binding!!.ivProfile.setImageURI(uri)
            }
            else if(requestCode == CROP &&  data!=null){
                Log.v("Hello ", "I am here")
                if(UtilMethod.instance.isStringNullOrNot(cropPicturePath) && !File( cropPicturePath ).isFile()){
                    cropPicturePath = UtilMethod.instance.getCropImagePath(context!!)
                }

                if(!UtilMethod.instance.isStringNullOrNot(cropPicturePath)){
                    Log.v("Bitmap Path", "==> "+cropPicturePath)
                    binding!!.ivProfile.setImageURI(Uri.parse(cropPicturePath))
                  //  bitmap = UtilMethod.instance.decodeFile( File( cropPicturePath ), 640, 640 )

                   // cropPicturePath = UtilMethod.instance.getFilePath( bitmap!!, context!!, cropPicturePath!! )

                    //callUploadDataAPI()
                }
                else{
                    Log.v("Hello ", "I am here3")
                }
            }
        }
    }





    private fun renderImage(imagePath: String?){
        if (imagePath != null) {
            val bitmap = BitmapFactory.decodeFile(imagePath)
            binding?.ivProfile?.setImageBitmap(bitmap)
        }
        else {
//            show("ImagePath is null")
        }
    }

    fun getAllShownImagesPath(activity : Activity) : ArrayList<String>{
        var uri : Uri
        var cursor : Cursor?
        var column_index_data : Int
        var column_index_folder_name : Int
        var listOfAllImages : ArrayList<String> = ArrayList()
        var absolutePathOfImage : String = ""
        uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(MediaStore.MediaColumns.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
        var orderBy : String = MediaStore.Images.Media.DATE_TAKEN;//order data by date
        cursor = activity!!.contentResolver.query(uri, projection, null,
            null,  orderBy + " DESC")
        if(cursor!=null) {
            column_index_data = cursor!!.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
            column_index_folder_name = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
            while (cursor.moveToNext()) {
                absolutePathOfImage = cursor.getString(column_index_data);

                listOfAllImages.add(absolutePathOfImage)
            }
        }

        return listOfAllImages

    }
}