package com.wellme.fragment

import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.AdapterView
import android.widget.GridView
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.wellme.LeftSideMenuActivity
import com.wellme.MyApplication
import com.wellme.R
import com.wellme.adapter.ChatAdapter
import com.wellme.adapter.GalleryImageAdapter
import com.wellme.databinding.FragmentChatBinding
import com.wellme.dto.ChatMessageDTO
import com.wellme.dto.UserDTO
import com.wellme.utils.AppConstants
import com.wellme.utils.AppService
import com.wellme.utils.UtilMethod
import droidninja.filepicker.FilePickerBuilder
import droidninja.filepicker.FilePickerConst
import droidninja.filepicker.models.sort.SortingTypes
import io.socket.client.Ack
import io.socket.client.Socket
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


class ChatFragment : Fragment(), View.OnClickListener{
    var binding : FragmentChatBinding? = null
    var socket : Socket? = null
    var userDTO : UserDTO? = null
    var user_id : String? = ""
    var receiver_id : String? = "1"
    var receiver_name : String? = ""
    var name : String? = ""
    var first_name : String? = ""
    var last_name : String? = ""
    var chat_list : ArrayList<ChatMessageDTO> = ArrayList()
    var adapter : ChatAdapter? = null
    var linearLayoutManager : LinearLayoutManager? = null
    var messageText : String? = ""
    var type : String? = ""
    var encImage : String? = ""
    var TAKE_PICTURE_REQUEST_B : Int = 100
    var CUSTOM_REQUEST_CODE : Int = 532
    var MY_CAMERA_PERMISSION_CODE : Int = 100
    var CAMERA_REQUEST : Int = 1888
    var picturePath : String = ""
    var cropPicturePath : String? = ""
    var CROP : Int = 113
    var bitmap : Bitmap? = null
    var s_access_token : String? = ""
    var regular : Typeface? = null
    var activity : Activity? = null
    var bundle : Bundle? = null

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        this.activity = activity;
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_chat, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)




        initView()


    }

    fun initView(){
        userDTO = UtilMethod.instance.getUser(context!!)
        regular = ResourcesCompat.getFont(context!!, R.font.poppins_regular)
        binding?.tvUserName!!.setTypeface(regular)
        binding?.tvOnline!!.setTypeface(regular)
        (activity as LeftSideMenuActivity).disableBottomBar();
        if(userDTO!=null){
            user_id = userDTO!!.user_id
            first_name = userDTO!!.first_name
            last_name = userDTO!!.last_name
            if(!UtilMethod.instance.isStringNullOrNot(first_name)){
                name = first_name
            }
            if(!UtilMethod.instance.isStringNullOrNot(last_name)){
                if(!UtilMethod.instance.isStringNullOrNot(name)){
                    name+=" "+last_name
                }
                else{
                    name = last_name
                }
            }

        }

        bundle = arguments
        if(bundle!=null){
            receiver_id = bundle?.getString("receiver_id")
            receiver_name = bundle?.getString("receiver_name")
            binding?.tvUserName!!.setText(receiver_name)
        }

        s_access_token = UtilMethod.instance.getAccessToken(requireContext())
        linearLayoutManager = LinearLayoutManager(context!!)
        linearLayoutManager!!.orientation = LinearLayoutManager.VERTICAL
        linearLayoutManager!!.stackFromEnd = true
        binding?.rvChat!!.layoutManager = linearLayoutManager
        socket = MyApplication.applicationContext().getSocket()
        if(socket!=null){
            socket!!.connect()
        }
        binding?.ivSend!!.setOnClickListener(this)
        //binding?.ivPin!!.setOnClickListener(this)
        binding?.back!!.setOnClickListener(this)
        binding?.documentLayout!!.setOnClickListener(this)
        binding?.galleryLayout!!.setOnClickListener(this)
        binding!!.etChat.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence,
                start: Int,
                before: Int,
                count: Int
            ) {
                binding?.shareLayout!!.visibility = View.GONE
                messageText = binding!!.etChat.text.toString().trim { it <= ' ' }
                if (!UtilMethod.instance.isStringNullOrNot(messageText)) {
                    binding!!.ivRecord.visibility = View.GONE
                    binding!!.ivSend.visibility = View.VISIBLE
                    binding!!.ivCamera.visibility = View.GONE
                } else {
                    binding!!.ivRecord.visibility = View.VISIBLE
                    binding!!.ivSend.visibility = View.GONE
                    binding!!.ivCamera.visibility = View.VISIBLE
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })


        getSocketFunction()
    }

    override fun onDestroy() {
        super.onDestroy()
        (activity as LeftSideMenuActivity).enableBottomBar();
    }

    fun getSocketFunction(){
        socket!!.on("connect") {
            Log.v("Socket Connected ", "Connected")
            connectUser()
        }



        socket!!.on(Socket.EVENT_CONNECT_ERROR) { args ->
            Log.v("Error ", "==> " + args[0])
            socket!!.connect()
        }

        socket!!.on(Socket.EVENT_CONNECT_ERROR) { args ->
            Log.v("Error ", "==> " + args[0])
            socket!!.connect()
        }

        socket!!.on("receive_message"){ args ->
            Log.v("Chat Response ", "==> "+args[0])
            val jobj = JSONObject(args[0].toString())
            if(jobj!=null){
                val data_obj = jobj.getJSONObject("data")
                var gson : Gson = Gson()
                var dto : ChatMessageDTO = gson.fromJson(data_obj.toString(), ChatMessageDTO::class.java)
                if(chat_list==null){
                    chat_list = ArrayList()
                }
                if(dto!=null){
                    chat_list.add(dto)
                }

                activity!!.runOnUiThread(Runnable {
                    if(adapter!=null){
                        adapter!!.notifyDataSetChanged()
                    }
                    else{
                        adapter = ChatAdapter(context!!, chat_list)
                        var linearLayoutManager1 : LinearLayoutManager = LinearLayoutManager(context!!)
                        linearLayoutManager1.orientation = LinearLayoutManager.VERTICAL
                        linearLayoutManager1.stackFromEnd = true
                        binding?.rvChat!!.layoutManager = linearLayoutManager1
                        binding?.rvChat!!.adapter  = adapter

                        adapter!!.notifyDataSetChanged()

                    }

                    binding?.rvChat?.smoothScrollToPosition(binding?.rvChat?.adapter!!.itemCount - 1)
                })
            }

        }


    }

    fun connectUser(){
        val jobject = JSONObject()
        jobject.put("user_id", user_id)
        jobject.put("name", name)
        socket!!.emit("login", jobject, Ack { args ->
            val repues = args[0] as JSONObject
            val jobj = JSONObject()
            jobj.put("from_id", user_id)
            jobj.put("to_id", "1")
            jobj.put("page_no", "1")
            Log.v("Object", jobj.toString())
            socket!!.emit("history", jobj, Ack { args1 ->
                Log.v("Chat Response", "==> "+args1[0])
                val obj1 = JSONObject(args1[0].toString())
                var gson : Gson = Gson()
                var list : List<ChatMessageDTO> = gson.fromJson(obj1.getJSONArray("data").toString(), Array<ChatMessageDTO>::class.java).toList()
                chat_list = ArrayList()
                if(list!=null){
                    chat_list.addAll(list)
                    if(chat_list!=null) {
                        adapter = ChatAdapter(context!!, chat_list)


                        activity!!.runOnUiThread(Runnable {
                            binding?.rvChat!!.adapter = adapter
                            if (chat_list != null && chat_list.size > 0) {
                                binding?.rvChat?.smoothScrollToPosition(binding?.rvChat?.adapter!!.itemCount - 1)
                            }
                        })
                    }
                }
            })

        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.v("Hello ", " yes "+requestCode)
        Log.v("Hello Result", " yes "+resultCode)
        if(requestCode == CUSTOM_REQUEST_CODE && resultCode == Activity.RESULT_OK){
            var list : ArrayList<Uri> = data!!.getParcelableArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS)

            //imagePathprofile = ""+list.get(0)6


        }
        else if(requestCode == CROP && resultCode == Activity. RESULT_OK &&  data!=null){
            Log.v("Hello ", "I am here")
            if(UtilMethod.instance.isStringNullOrNot(cropPicturePath) && !File( cropPicturePath ).isFile()){
                cropPicturePath = UtilMethod.instance.getCropImagePath(context!!)
            }

            if(!UtilMethod.instance.isStringNullOrNot(cropPicturePath)){
                    Log.v("Bitmap Path", "==> "+cropPicturePath)
                    bitmap = UtilMethod.instance.decodeFile( File( cropPicturePath ),
                    640, 640 )
                Log.v("Bitmap Value", "==> "+bitmap)
                cropPicturePath = UtilMethod.instance.getFilePath( bitmap!!, context!!, cropPicturePath!! )
                Log.v("Hello ", "I am here2")
                callUploadDataAPI()
            }
            else{
                Log.v("Hello ", "I am here3")
            }
        }
    }





    fun callUploadDataAPI(){

        var progressDialog : ProgressDialog = ProgressDialog.show(requireContext(), "", "")
        progressDialog.setContentView(R.layout.progress_loader)
        progressDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        progressDialog.setCancelable(false)

        val service = Retrofit.Builder()
            .baseUrl(""+ AppConstants.BASE_URL_NEW)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(AppService::class.java)

        var f1 : File = File(cropPicturePath)
        Log.v("File Name ", "==> "+f1.path)
        var requestFile : RequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), f1)

        var body : MultipartBody.Part = MultipartBody.Part.createFormData("filename", f1.getName(), requestFile)

        service.uploadData("Bearer "+s_access_token,
            body).enqueue(object :
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
                Log.v("Response Code ", "==> "+response.raw().code())
                Log.v("Response Code ", "==> "+response.raw().body())
                var response1 : String = response.body()!!.string()
                Log.v("Subscription Response ", "==> "+response1)

            }
        })
    }


    override fun onDestroyView() {
        super.onDestroyView()
        if(socket!=null){
            socket!!.disconnect()
        }
    }





    override fun onClick(v: View?) {

        if(v == binding?.ivSend){
            binding!!.shareLayout!!.visibility = View.GONE
            if (!messageText.equals("")) {
                type = "text"
                encImage=""
                sendMessage("")
            }

        }
        else if(v == binding?.ivPin){
            val animation: Animation =
                AnimationUtils.loadAnimation(activity, R.anim.bottom_up_animation)
            animation.duration = 500
            binding!!.shareLayout.visibility = View.VISIBLE
            binding!!.shareLayout.animation = animation
            binding!!.shareLayout.animate()
            animation.start()
        }
        else if(v == binding?.documentLayout){
            binding?.shareLayout!!.visibility = View.GONE

            FilePickerBuilder.getInstance()
                .setMaxCount(1)
                .setActivityTheme(R.style.LibAppTheme)
                .setActivityTitle("Please select doc")
                .enableDocSupport(true)
                .enableSelectAll(true)
                .sortDocumentsBy(SortingTypes.name)
                .pickFile(this, CUSTOM_REQUEST_CODE)
        }
        else if(v == binding?.galleryLayout){
            binding?.shareLayout!!.visibility = View.GONE
            var image_list : ArrayList<String> = getAllShownImagesPath(activity!!)
            Log.v("Image Size ", "==> "+image_list.size)
                showGalleryDialog(image_list)
        }
        else if(v == binding?.back){
            activity!!.onBackPressed()
        }
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

    fun sendMessage(img: String) {
        try {
            val to_object = JSONObject()
            to_object.put("id", "1")
            to_object.put("name", "Admin")
            val from_object = JSONObject()
            from_object.put("id", user_id)
            from_object.put("name",name)
            val full_object = JSONObject()
            full_object.put("from", from_object)
            full_object.put("to", to_object)
            full_object.put("type", type)
            if (!UtilMethod.instance.isStringNullOrNot(messageText)) {
                full_object.put("message", messageText + "")
            }
            if (!UtilMethod.instance.isStringNullOrNot(img)) {
                val mimetype = img.substring(img.lastIndexOf(".") + 1)
                full_object.put("mimetype", "" + mimetype)
                full_object.put("message", "" + img)
                full_object.put("image", "" + img)
            }
            Log.v("Request ", "==> $full_object")
            socket!!.emit("send_message", full_object, Ack { args ->
                Log.v("SEnding Response ", "==> " + args[0])
                try {
                    val obj = JSONObject(args[0].toString())
                    if (obj != null) {
                        val data_obj = obj.getJSONObject("data")
                        if (data_obj != null) {
                            val gson = Gson()
                            val dto = gson.fromJson(
                                data_obj.toString(),
                                ChatMessageDTO::class.java
                            )
                            if (dto != null) {
                                if (chat_list == null) {
                                    chat_list = ArrayList()
                                }
                                chat_list.add(dto)
                                activity!!.runOnUiThread {
                                    try {
                                        val mLinearLayoutManager = LinearLayoutManager(context)
                                        mLinearLayoutManager.orientation = LinearLayoutManager.VERTICAL
                                        mLinearLayoutManager.stackFromEnd = true
                                        binding!!.rvChat.layoutManager =
                                            mLinearLayoutManager
                                        if (adapter == null) {
                                            adapter = ChatAdapter(
                                                context!!,
                                                chat_list
                                            )
                                            binding!!.rvChat.adapter = adapter
                                        }
                                        binding!!.rvChat.smoothScrollToPosition(binding!!.rvChat.adapter!!.itemCount - 1)
                                        adapter!!.notifyDataSetChanged()
                                    } catch (e: Exception) {
                                    }
                                }
                            }
                        }
                    }
                } catch (e: Exception) {
                }
            })
            binding!!.etChat.setText("")
            messageText = ""
        } catch (e: Exception) {
            Log.v("Exception ", " $e")
        }
    }
}