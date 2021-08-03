package com.wellme

import android.annotation.TargetApi
import android.app.Activity
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.webkit.MimeTypeMap
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.wellme.adapter.GalleryImageListAdapter
import com.wellme.utils.OnItemClickListener
import com.wellme.utils.UtilMethod
import java.io.ByteArrayOutputStream
import java.io.File


class GalleryImageListActivity : FragmentActivity(), View.OnClickListener {

    var rv_menu : RecyclerView? = null
    var ll_back : LinearLayout? = null
    var header : TextView? = null

    var typeface : Typeface? = null
    var galleryImageAdapter : GalleryImageListAdapter? = null
    var linearLayoutManager : LinearLayoutManager? = null
    var image_list : ArrayList<String?> = ArrayList()
    var bottomNavigationView : BottomNavigationView? = null
    var picturePath : String? = ""
    var type:String = ""

    var imageDialog : BottomSheetDialog? = null

    private var mUri: Uri? = null
    //Our constants
    private val OPERATION_CAPTURE_PHOTO = 1
    private val OPERATION_CHOOSE_PHOTO = 2



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.fragment_gallary_image_list)
        rv_menu = findViewById(R.id.rv_gallery)
        ll_back = findViewById(R.id.ll_back)
        header = findViewById(R.id.header)

        initView()

    }

    fun initView(){
        typeface = ResourcesCompat.getFont(this, R.font.poppins_regular)
        val intent = intent

        if (intent!=null){
            type = intent.getStringExtra("type")
            Log.d(">>   ",">>>>"+type)
            showimageDialog()
        }
    }

   fun setGridAdapter(type:String?){
        linearLayoutManager = GridLayoutManager(this,2, RecyclerView.HORIZONTAL, false)
        linearLayoutManager?.orientation = LinearLayoutManager.VERTICAL
        rv_menu?.layoutManager = linearLayoutManager

       galleryImageAdapter = GalleryImageListAdapter(this, image_list,onItemClickCallback,onItemClickCallbackVideo,onItemClickCallbackFile,type)
       rv_menu?.adapter = galleryImageAdapter
    }


    fun setAdapter(){
        linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager?.orientation = LinearLayoutManager.VERTICAL
        rv_menu?.layoutManager = linearLayoutManager

        galleryImageAdapter = GalleryImageListAdapter(this, image_list,onItemClickCallback,onItemClickCallbackVideo,onItemClickCallbackFile,"pdf")
        rv_menu?.adapter = galleryImageAdapter
    }

    var onItemClickCallback : OnItemClickListener.OnItemClickCallback = object : OnItemClickListener.OnItemClickCallback{
        override fun onItemClicked(view: View?, position: Int) {

            var path : String? = image_list.get(position)
            var intent : Intent = Intent()
            intent.putExtra("imagePath", path)
            setResult(101, intent)
            finish()

            }

        }

    var onItemClickCallbackVideo : OnItemClickListener.OnItemClickCallback = object : OnItemClickListener.OnItemClickCallback{
        override fun onItemClicked(view: View?, position: Int) {

            var path : String? = image_list.get(position)
            var intent : Intent = Intent()
            intent.putExtra("imagePath", path)
            setResult(102, intent)
            finish()

        }

    }

    var onItemClickCallbackFile : OnItemClickListener.OnItemClickCallback = object : OnItemClickListener.OnItemClickCallback{
        override fun onItemClicked(view: View?, position: Int) {

            var path : String? = image_list.get(position)

            var intent : Intent = Intent()
            intent.putExtra("imagePath", path)
            setResult(103, intent)
            finish()

        }

    }

    override fun onClick(v: View?) {
        TODO("Not yet implemented")
    }


    private fun getAllShownImagesPath(activity: Activity): ArrayList<String?>? {
        val uri: Uri
        val cursor: Cursor?
        val column_index_data: Int
        val column_index_folder_name: Int
        val listOfAllImages = ArrayList<String?>()
        var absolutePathOfImage: String? = null
        uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.MediaColumns.DATA,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME

        )
        cursor = activity.contentResolver.query(
            uri, projection, null,
            null, null
        )
        column_index_data = cursor!!.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
        column_index_folder_name = cursor
            .getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
        while (cursor.moveToNext()) {
            absolutePathOfImage = cursor.getString(column_index_data)
            listOfAllImages.add(absolutePathOfImage)
        }
        Log.d("data",">>>>"+listOfAllImages)

        image_list =listOfAllImages

        ll_back?.visibility = View.VISIBLE
        header?.setText(R.string.select_image)

        if(image_list!=null){
            setGridAdapter("image")
        }
        return listOfAllImages
    }



    private fun getAllShownVideoPath(activity: Activity): ArrayList<String?>? {
        val uri: Uri
        val cursor: Cursor?
        val column_index_data: Int
        val column_index_folder_name: Int
        val listOfAllImages = ArrayList<String?>()
        var absolutePathOfImage: String? = null
        uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.MediaColumns.DATA,
            MediaStore.Video.Media.BUCKET_DISPLAY_NAME

        )
        cursor = activity.contentResolver.query(
            uri, projection, null,
            null, null
        )
        column_index_data = cursor!!.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
        column_index_folder_name = cursor
            .getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_DISPLAY_NAME)
        while (cursor.moveToNext()) {
            absolutePathOfImage = cursor.getString(column_index_data)
            listOfAllImages.add(absolutePathOfImage)
        }

        Log.d("data",">>>>"+listOfAllImages)

        ll_back?.visibility = View.VISIBLE
        header?.setText(R.string.select_video)

        image_list =listOfAllImages
//        galleryImageAdapter = GalleryImageListAdapter(this, image_list,onItemClickCallback,onItemClickCallbackVideo,"video")
        if(image_list!=null){
//            rv_menu?.adapter = galleryImageAdapter
            setGridAdapter("video")
        }


        return listOfAllImages
    }


    private fun getAllShownFilePath(activity: Activity): ArrayList<String?>? {
        val pdfList: ArrayList<String?> = ArrayList()
        val collection: Uri
        val projection = arrayOf(
            MediaStore.Files.FileColumns.DISPLAY_NAME,
            MediaStore.Files.FileColumns.DATE_ADDED,
            MediaStore.Files.FileColumns.DATA,
            MediaStore.Files.FileColumns.MIME_TYPE
        )
        val sortOrder = MediaStore.Files.FileColumns.DATE_ADDED + " DESC"
        val selection = MediaStore.Files.FileColumns.MIME_TYPE + " = ?"+
                " OR " +MediaStore.Files.FileColumns.MIME_TYPE + "=?"+
                " OR " +MediaStore.Files.FileColumns.MIME_TYPE + "=?"
        val pdf = MimeTypeMap.getSingleton().getMimeTypeFromExtension("pdf")
        val doc= MimeTypeMap.getSingleton().getMimeTypeFromExtension("doc")
        val docx= MimeTypeMap.getSingleton().getMimeTypeFromExtension("docx")
//        val selectionArgs = arrayOf(mimeType)


        collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } else {
            MediaStore.Files.getContentUri("external")
        }

        //args
        //args
        val args = arrayOf(
            pdf!!,
            doc!!, docx!!
        )

        contentResolver.query(collection, projection, selection, args, sortOrder)
            .use { cursor ->
                assert(cursor != null)
                if (cursor!!.moveToNext()) {
                    val columnData = cursor!!.getColumnIndex(MediaStore.Files.FileColumns.DATA)
                    val columnName =
                        cursor!!.getColumnIndex(MediaStore.Files.FileColumns.DISPLAY_NAME)
                    do {
                        pdfList.add(cursor!!.getString(columnData))
                        Log.d(
                            "",
                            "getPdf: " + cursor!!.getString(columnData)
                        )
                        //you can get your pdf files
                    } while (cursor!!.moveToNext())
                }
            }


        Log.d("pdfList",">>>>"+pdfList)





        image_list =pdfList

        ll_back?.visibility = View.VISIBLE
        header?.setText(R.string.select_file)


        if(image_list!=null){
            setAdapter()
        }
        return pdfList;
    }





    fun showimageDialog(){
        var muscle_mass : Int = 0

        imageDialog = BottomSheetDialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        imageDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        imageDialog!!.setContentView(R.layout.dialog_select_image)
        imageDialog!!.window!!.setBackgroundDrawable(ColorDrawable(this.resources.getColor(R.color.transparent_black)))


        var llGallery : ImageView? = imageDialog!!.findViewById(R.id.iv_gallery)
        var llCamera : ImageView? = imageDialog!!.findViewById(R.id.iv_camera)
        var llVideo : LinearLayout? = imageDialog!!.findViewById(R.id.ll_video)
        var llFile : LinearLayout? = imageDialog!!.findViewById(R.id.ll_file)
        var llclose : LinearLayout? = imageDialog!!.findViewById(R.id.ll_close)
        var llMain : LinearLayout? = imageDialog!!.findViewById(R.id.ll_main)

        var animation : Animation = AnimationUtils.loadAnimation(this,R.anim.bottom_up_animation)
        var animation1 : Animation = AnimationUtils.loadAnimation(this,R.anim.bottom_down_animation)


        llMain?.animation = animation

        if (!UtilMethod.instance.isStringNullOrNot(type)){
            if (type.equals("1")){
                llGallery?.visibility = View.GONE
            }
            if (type.equals("2")){
                llCamera?.visibility = View.GONE
            }

            if (type.equals("3")){
                Log.d("sdfs","enter")
                llVideo?.visibility = View.GONE
            }

            if (type.equals("4")){
                llFile?.visibility = View.GONE
            }


        }
        imageDialog!!.show()

//         tvGallery!!.setTypeface(regular)
//         tvCamera!!.setTypeface(regular)

        llGallery?.setOnClickListener(View.OnClickListener { view ->



            if (!UtilMethod.instance.checkPermission(this)){
                //Requests permissions to be granted to this application at runtime
                ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.CAMERA,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE,
                        android.Manifest.permission.ACCESS_FINE_LOCATION), 1)
            }
            else{
                getAllShownImagesPath(this)
            }
            imageDialog!!.dismiss()









            /*if (checkSelfPermission(Manifest.permission.READ_CONTACTS)
            != PackageManager.PERMISSION_GRANTED) {
        requestPermissions(new String[]{Manifest.permission.READ_CONTACTS},
                MY_PERMISSIONS_REQUEST_READ_CONTACTS);

        // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
        // app-defined int constant

        return;
    }*/











        })
        llCamera?.setOnClickListener(View.OnClickListener { view ->


            if (!UtilMethod.instance.checkPermission(this)){
                //Requests permissions to be granted to this application at runtime
                ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.CAMERA,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE,
                        android.Manifest.permission.ACCESS_FINE_LOCATION), 3)
            }else{

//                capturePhoto()

                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(cameraIntent, OPERATION_CAPTURE_PHOTO)

            }



        })
        llVideo?.setOnClickListener(View.OnClickListener { view ->
            Log.d(">>>",">>>"+3)
            if (!UtilMethod.instance.checkPermission(this)){
                //Requests permissions to be granted to this application at runtime
                ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.CAMERA,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE,
                        android.Manifest.permission.ACCESS_FINE_LOCATION), 2)
            }
            else{
                getAllShownVideoPath(this)
            }

            imageDialog!!.dismiss()
        })
        llFile?.setOnClickListener(View.OnClickListener { view ->
            Log.d(">>>",">>>"+4)
            getAllShownFilePath(this)

            imageDialog!!.dismiss()
        })

        llclose?.setOnClickListener(View.OnClickListener { view ->
            Log.d(">>>",">>>"+1)
            llMain?.animation = animation1
            imageDialog!!.dismiss();
            finish()


        })
    }

    private fun capturePhoto(){
        val capturedImage = File(externalCacheDir, "My_Captured_Photo.jpg")
//        if(capturedImage.exists()) {
//            capturedImage.delete()
//        }
//        capturedImage.createNewFile()
        mUri = if(Build.VERSION.SDK_INT >= 24){
            FileProvider.getUriForFile(this, "com.wellme.fitness2.fileprovider",
                capturedImage)
        } else {
            Uri.fromFile(capturedImage)
        }

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mUri)
        startActivityForResult(intent, OPERATION_CAPTURE_PHOTO)

//        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//        startActivityForResult(cameraIntent, OPERATION_CAPTURE_PHOTO)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            OPERATION_CAPTURE_PHOTO ->
                if (resultCode == Activity.RESULT_OK) {

                    val photo = data?.extras
                        ?.get("data") as Bitmap

                    Log.d(">>>",">>"+photo)

                    mUri = getImageUri(this,photo)

                    Log.d("uri>>>",">>"+photo)

//                    val bitmap = BitmapFactory.decodeStream(
//                        getContentResolver().openInputStream(mUri!!))



                    val path = getRealPathFromURI(mUri!!)
                    Log.d("uri",">>>"+path);
                    var intent : Intent = Intent()
                    intent.putExtra("imagePath", path)
                    setResult(101, intent)
                    finish()



                }
        }
    }



    @TargetApi(19)
    private fun handleImageOnKitkat(data: Intent?) {
        var imagePath: String? = null
        val uri = data!!.data
        //DocumentsContract defines the contract between a documents provider and the platform.
        if (DocumentsContract.isDocumentUri(this, uri)){
            val docId = DocumentsContract.getDocumentId(uri)
            if ("com.android.providers.media.documents" == uri?.authority){
                val id = docId.split(":")[1]
                val selsetion = MediaStore.Images.Media._ID + "=" + id
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    selsetion)
            }
            else if ("com.android.providers.downloads.documents" == uri?.authority){
                val contentUri = ContentUris.withAppendedId(Uri.parse(
                    "content://downloads/public_downloads"), java.lang.Long.valueOf(docId))
                imagePath = getImagePath(contentUri, null)
            }
        }
        else if ("content".equals(uri?.scheme, ignoreCase = true)){
            imagePath = getImagePath(uri, null)
        }
        else if ("file".equals(uri?.scheme, ignoreCase = true)){
            imagePath = uri?.path
        }

        val intentMessage = Intent()
        // put the message in Intent
        intentMessage.putExtra("imagePath", imagePath)
        // Set The Result in Intent
        setResult(101, intentMessage)
        // finish The activity
        finish()

        Log.d("image",">>>"+imagePath)


//        renderImage(imagePath)
    }
    private fun getImagePath(uri: Uri?, selection: String?): String {
        var path: String? = null
        val cursor = contentResolver.query(uri!!, null, selection, null, null )
        if (cursor != null){
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
            }
            cursor.close()
        }
        Log.d("uri",">>>"+path);
        return path!!
    }


    fun getRealPathFromURI(contentUri: Uri): String? {
        return try {
            val proj = arrayOf(MediaStore.Images.Media.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
            val cursor = managedQuery(contentUri, proj, null, null, null)
            val column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            cursor.getString(column_index)
        } catch (e: Exception) {
            contentUri.path
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>
                                            , grantedResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantedResults)
        when(requestCode){
            1 ->
                if (grantedResults.isNotEmpty() && grantedResults.get(0) ==
                    PackageManager.PERMISSION_GRANTED){

                    getAllShownVideoPath(this)

                }else {
                    show("Unfortunately You are Denied Permission to Perform this Operataion.")
                }
            2->
                if (grantedResults.isNotEmpty() && grantedResults.get(0) ==
                    PackageManager.PERMISSION_GRANTED){

                    getAllShownVideoPath(this)

                }else {
                    show("Unfortunately You are Denied Permission to Perform this Operataion.")
                }

            3->
                if (grantedResults.isNotEmpty() && grantedResults.get(0) ==
                    PackageManager.PERMISSION_GRANTED){

//                    capturePhoto()

                    val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    startActivityForResult(cameraIntent, OPERATION_CAPTURE_PHOTO)
                }else {
                    show("Unfortunately You are Denied Permission to Perform this Operataion.")
                }
        }
    }

    private fun show(message: String) {
        Toast.makeText(this,message, Toast.LENGTH_SHORT).show()
    }


    fun getImageUri(inContext: Context, inImage: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(
            inContext.getContentResolver(),
            inImage,
            "Title",
            null
        )

        return Uri.parse(path)
    }


}

