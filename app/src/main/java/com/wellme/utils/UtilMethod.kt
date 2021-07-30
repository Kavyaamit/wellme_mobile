package com.wellme.utils

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.os.Build
import android.os.Environment
import android.text.Html
import android.util.Log
import android.util.Patterns
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.wellme.dto.CurrentDateDTO
import com.wellme.dto.UserDTO
import java.io.*
import java.lang.reflect.Type
import java.net.URL
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern
import kotlin.collections.ArrayList


class UtilMethod {


    var IMAGE_DIRECTORY_CROP : String = "/DCIM/CROP_PICTURES"

    companion object {
        val instance = UtilMethod()
    }

    fun setAccessToken(token : String?, context: Context){
        val sharedPreferences : SharedPreferences = context.getSharedPreferences("User_login", Context.MODE_PRIVATE)
        var editor : SharedPreferences.Editor = sharedPreferences.edit()
        editor.putString("accesstoken", token)
        editor.commit()
    }

    fun getDatenew(date : String?) : String? {
        var s1 : String? = ""
        val simpleDateFormat : SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val myDate : Date = simpleDateFormat.parse(date!!.replace("T", " ").replace("Z", ""))
        if(myDate!=null){
            val simpleDateFormat = SimpleDateFormat("dd MMM yyyy")
            s1 = simpleDateFormat.format(myDate)
        }

        return s1

    }

    fun setString(token : String?, context: Context, key : String?){
        val sharedPreferences : SharedPreferences = context.getSharedPreferences("User_login", Context.MODE_PRIVATE)
        var editor : SharedPreferences.Editor = sharedPreferences.edit()
        editor.putString(key, token)
        editor.commit()
    }

    fun getString(context: Context, key : String?) : String?{
        val sharedPreferences : SharedPreferences = context.getSharedPreferences("User_login", Context.MODE_PRIVATE)
        return sharedPreferences.getString(key, "")
    }



    fun checkPermission(context: Activity):Boolean{

        val checkCameraPermission = ContextCompat.checkSelfPermission(context,android.Manifest.permission.CAMERA)
        val checkwriteStoragePermission = ContextCompat.checkSelfPermission(context,android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        val checkReadStoragePermission = ContextCompat.checkSelfPermission(context,android.Manifest.permission.READ_EXTERNAL_STORAGE)
        val checkAccessLocationPermission = ContextCompat.checkSelfPermission(context,android.Manifest.permission.ACCESS_FINE_LOCATION)
//        val checkSelfPermission = ContextCompat.checkSelfPermission(context,android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
//        val checkSelfPermission = ContextCompat.checkSelfPermission(context,android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (checkCameraPermission != PackageManager.PERMISSION_GRANTED){
            return false;
        }
        else if (checkwriteStoragePermission != PackageManager.PERMISSION_GRANTED){
            return false;
        } else if (checkReadStoragePermission != PackageManager.PERMISSION_GRANTED){
            return false;
        } else if (checkAccessLocationPermission != PackageManager.PERMISSION_GRANTED){
            return false;
        }
        else{
            return true;
        }

    }

    fun getAccessToken(context: Context) : String?{
        val sharedPreferences : SharedPreferences = context.getSharedPreferences("User_login", Context.MODE_PRIVATE)
        return sharedPreferences.getString("accesstoken", "")
    }

    fun setMobileNumber(token : String, context: Context){
        val sharedPreferences : SharedPreferences = context.getSharedPreferences("User_login", Context.MODE_PRIVATE)
        var editor : SharedPreferences.Editor = sharedPreferences.edit()
        editor.putString("phone", token)
        editor.commit()
    }

    fun getTimenew(date : String?) : String{
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"))
        val myDate: Date = simpleDateFormat.parse(date!!.replace("T", " "))
        val simpleDateFormat2 = SimpleDateFormat("hh:mm a")
        return simpleDateFormat2.format(myDate)
    }

    fun setReminderTimeList(reminder : String, context: Context){
        val sharedPreferences : SharedPreferences = context.getSharedPreferences("User_login", Context.MODE_PRIVATE)
        var editor : SharedPreferences.Editor = sharedPreferences.edit()
        editor.putString("reminder_time", reminder)
        editor.commit()
    }

    fun getReminderTimeList(context: Context) : String?{
        val sharedPreferences : SharedPreferences = context.getSharedPreferences("User_login", Context.MODE_PRIVATE)
        return sharedPreferences.getString("reminder_time", "")
    }

    fun setReminderList(reminder : String, context: Context){
        val sharedPreferences : SharedPreferences = context.getSharedPreferences("User_login", Context.MODE_PRIVATE)
        var editor : SharedPreferences.Editor = sharedPreferences.edit()
        editor.putString("reminder", reminder)
        editor.commit()
    }

    fun getMobileNumber(context: Context) : String?{
        val sharedPreferences : SharedPreferences = context.getSharedPreferences("User_login", Context.MODE_PRIVATE)
        return sharedPreferences.getString("phone", "")
    }

    fun getReminderList(context: Context) : String?{
        val sharedPreferences : SharedPreferences = context.getSharedPreferences("User_login", Context.MODE_PRIVATE)
        return sharedPreferences.getString("reminder", "")
    }

    fun appInstalledOrNot(context : Context, uri : String) : Boolean{
        val pm: PackageManager = context!!.getPackageManager()
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES)
            return true
        } catch (e: PackageManager.NameNotFoundException) {
        }

        return false
    }






    fun getFormatedAmountString(amt: Float?): String? {
        val decimalFormat = DecimalFormat("#")
        decimalFormat.setMinimumFractionDigits(2)
        var value: String = decimalFormat.format(amt)
        val str = "" + value[0]
        if (str == ".") {
            value = "0$value"
        }
        return value
    }

    fun getFormatedDoubleAmountString(amt: Double?): String? {
        val decimalFormat = DecimalFormat("#")
        decimalFormat.setMinimumFractionDigits(2)
        var value: String = decimalFormat.format(amt)
        val str = "" + value[0]
        if (str == ".") {
            value = "0$value"
        }
        return value
    }


    fun getReminderThroughTime(i1 : Int, context: Context){
        var cal : Calendar = Calendar.getInstance()
        var cal1 : Calendar = Calendar.getInstance()
        var diffence_ms : Long = 0
        cal.set(Calendar.HOUR_OF_DAY, 8)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal1.set(Calendar.HOUR_OF_DAY, 22)
        cal1.set(Calendar.MINUTE, 0)
        cal1.set(Calendar.SECOND, 0)
        diffence_ms = cal1.timeInMillis - cal.timeInMillis
        var min : Int = (diffence_ms / (60 * 1000)).toInt()

        val gson : Gson? = Gson()
        var old_list : ArrayList<String> = ArrayList()
        old_list.add("08:00 AM")
        if(i1>2) {
            var total_ms = min - (min%(i1-1))
            var add_min: Int = total_ms/(i1-1)
            for (i in 1..i1 - 2) {
                cal.add(Calendar.MINUTE, add_min)
                old_list.add(""+getTimeFormat(cal.time))
            }
        }
        if(i1>1){
            old_list.add("10:00 PM")
        }

        var s1 : String = gson!!.toJson(old_list)
        setReminderTimeList(s1, context)

    }


    fun getReminderThroughHour(i1 : Int, context: Context){
        var cal : Calendar = Calendar.getInstance()
        var cal1 : Calendar = Calendar.getInstance()
        var diffence_ms : Long = 0
        cal.set(Calendar.HOUR_OF_DAY, 8)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal1.set(Calendar.HOUR_OF_DAY, 22)
        cal1.set(Calendar.MINUTE, 0)
        cal1.set(Calendar.SECOND, 0)
        diffence_ms = cal1.timeInMillis - cal.timeInMillis
        val gson : Gson? = Gson()
        var old_list : ArrayList<String> = ArrayList()
        old_list.add("08:00 AM")
            for (i in 1..14) {
                cal.add(Calendar.HOUR_OF_DAY, i1)
                if(cal1.timeInMillis >= cal.timeInMillis){
                    old_list.add(""+getTimeFormat(cal.time))
                }
                else{
                    break
                }
            }

        var s1 : String = gson!!.toJson(old_list)
        setReminderTimeList(s1, context)

    }

    fun getDate(i1 : Int) : String{
        var s1 : String = ""
        if(i1>3 && i1<21){
            var i2 : String = ""+i1
            s1 = i2+"th"
        }
        else if(i1%10 == 1){
            var i2 : String = ""+i1
            s1 = i2+"st"
        }
        else if(i1%10 == 2){
            var i2 : String = ""+i1
            s1 = i2+"nd"
        }
        else if(i1%10 == 3){
            var i2 : String = ""+i1
            s1 = i2+"rd"
        }
        else{
            var i2 : String = ""+i1
            s1 = i2+"th"
        }


        return s1
    }

    fun getDate(date : String?) : String{
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"))
        val myDate: Date = simpleDateFormat.parse(date!!.replace("T", " "))
        val simpleDateFormat2 = SimpleDateFormat("dd MMM yyyy, hh:mm:ss a")
        return simpleDateFormat2.format(myDate)
    }

    fun getDateWithoutSecond(date : String?) : String{
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"))
        val myDate: Date = simpleDateFormat.parse(date!!.replace("T", " "))
        val simpleDateFormat2 = SimpleDateFormat("dd MMM yyyy, hh:mm a")
        return simpleDateFormat2.format(myDate)
    }

    fun getDate12(date : String?) : String{
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
        val myDate: Date = simpleDateFormat.parse(date!!.replace("T", " "))
        val simpleDateFormat2 = SimpleDateFormat("yy-MM-dd")
        return simpleDateFormat2.format(myDate)
    }

    fun getTime(time : String?) : String{
        val simpleDateFormat = SimpleDateFormat("HH:mm")
        val myDate: Date = simpleDateFormat.parse(time)
        val simpleDateFormat2 = SimpleDateFormat("hh:mm a")
        return simpleDateFormat2.format(myDate)
    }

    fun getDate13(date : String?) : Date{
        val simpleDateFormat = SimpleDateFormat("yy-MM-dd")
        val myDate: Date = simpleDateFormat.parse(date!!.replace("T", " "))
        return myDate
    }

    fun getDate1(date : String?) : Date{
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"))
        val myDate: Date = simpleDateFormat.parse(date!!.replace("T", " "))
        return myDate
    }

    fun getDay(date : String?) : Int{
        var calendar : Calendar = Calendar.getInstance()
        val simpleDateFormat = SimpleDateFormat("EEEE")
        val myDate: Date = simpleDateFormat.parse(date!!)
        calendar.time = myDate

        return calendar.get(Calendar.DAY_OF_WEEK)


    }

    fun getVideoIdFromYoutubeUrl(url: String?): String? {
        var videoId: String? = null
        try {
            val regex =
                "http(?:s)?:\\/\\/(?:m.)?(?:www\\.)?youtu(?:\\.be\\/|be\\.com\\/(?:watch\\?(?:feature=youtu.be\\&)?v=|v\\/|embed\\/|user\\/(?:[\\w#]+\\/)+))([^&#?\\n]+)"
            val pattern =
                Pattern.compile(regex, Pattern.CASE_INSENSITIVE)
            val matcher: Matcher = pattern.matcher(url)
            if (matcher.find()) {
                videoId = matcher.group(1)
            }
        }
        catch(e : Exception){

        }
        return videoId
    }

    fun getDateWithoutLocalTime(date : String?) : Date?{
        try {
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val myDate: Date = simpleDateFormat.parse(date!!.replace("T", " "))
            return myDate
        }catch(e :java.lang.Exception){
            return null
        }

    }

    fun getDateTime(date : String?) : String? {
        var s1 : String? = ""
        val simpleDateFormat : SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val myDate : Date = simpleDateFormat.parse(date!!.replace("T", " ").replace("Z", ""))
        if(myDate!=null){
            val simpleDateFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a")
            s1 = simpleDateFormat.format(myDate)
        }

        return s1

    }

    /*fun getConnectivity(context : Context) : Boolean{
        var connectivityManager : ConnectivityManager? = null
        //connectivityManager = context!!.getSystemService()


    }*/

    fun getDateWithoutTime(date : String?) : Date?{
        try {
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"))
            val myDate: Date = simpleDateFormat.parse(date!!.replace("T", " "))
            return myDate
        }catch(e :java.lang.Exception){
            return null
        }

    }

    fun getDateWithoutDate(date : String?) : Date?{
        try {
            val simpleDateFormat = SimpleDateFormat("hh:mm a")
            val myDate: Date = simpleDateFormat.parse(date!!.replace("T", " "))
            return myDate
        }catch(e :java.lang.Exception){
            return null
        }

    }

    fun getDateWithoutDate2(date : String?) : Date?{
        try {
            val simpleDateFormat = SimpleDateFormat("hh:mm a")
            val calendar : Calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_YEAR, 1)
            val myDate: Date = Date()
            val myDate1: Date = simpleDateFormat.parse(date!!.replace("T", " "))
            myDate.hours = myDate1.hours
            myDate.minutes = myDate1.minutes
            myDate.seconds = 0
            return myDate
        }catch(e :java.lang.Exception){
            return null
        }

    }

    fun getDateWithoutDate1(date : String?) : Date?{
        try {
            val simpleDateFormat = SimpleDateFormat("hh:mm a")
            val myDate: Date = Date()
            val myDate1: Date = simpleDateFormat.parse(date!!.replace("T", " "))
            myDate.hours = myDate1.hours
            myDate.minutes = myDate1.minutes
            myDate.seconds = 0
            return myDate
        }catch(e :java.lang.Exception){
            return null
        }

    }

    fun getDateWithoutTime1(date : String?) : String?{
        var d1 : String? = ""
        try{
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm")
            val myDate: Date = Date()
            val myDate1: Date = simpleDateFormat.parse(date!!.replace("T", " "))
            val simpleDateFormat1 = SimpleDateFormat("dd-MM-yyyy")
            d1 = simpleDateFormat1.format(myDate1)
        }
        catch(e : java.lang.Exception){

        }

        return d1
    }

    fun getDateWithTime1(date : String?) : String?{
        var d1 : String? = ""
        try{
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm")
            val myDate: Date = Date()
            val myDate1: Date = simpleDateFormat.parse(date!!.replace("T", " "))
            val simpleDateFormat1 = SimpleDateFormat("dd-MM-yyyy HH:mm")
            d1 = simpleDateFormat1.format(myDate1)
        }
        catch(e : java.lang.Exception){

        }

        return d1
    }

    fun getDayFormat(date : Date?) : String?{
        var s1 : String? = ""
        try {
            val simpleDateFormat = SimpleDateFormat("EEEE")
            s1 = simpleDateFormat.format(date)
        }catch(e :java.lang.Exception){

        }

        return s1

    }

    fun getTimeFormat(date : Date?) : String?{
        var s1 : String? = ""
        try {
            val simpleDateFormat = SimpleDateFormat("hh:mm a")
            s1 = simpleDateFormat.format(date)
        }catch(e :java.lang.Exception){

        }

        return s1

    }

    fun getDateFormat(date : Date?) : String?{
        var s1 : String? = ""
        try {
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
            s1 = simpleDateFormat.format(date)
        }catch(e :java.lang.Exception){

        }

        return s1

    }

    fun getDateTimeFormat(date : Date?) : String?{
        var s1 : String? = ""
        try {
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm")
            s1 = simpleDateFormat.format(date)
        }catch(e :java.lang.Exception){

        }

        return s1

    }

    fun getDateFormat1(date : Date?) : String?{
        var s1 : String? = ""
        try {
            val simpleDateFormat = SimpleDateFormat("dd MMM, yyyy")
            s1 = simpleDateFormat.format(date)
        }catch(e :java.lang.Exception){

        }

        return s1

    }

    fun getTime1(date : Date?) : String?{
        var s1 : String? = ""
        try {
            val simpleDateFormat = SimpleDateFormat("hh:mm a")
            s1 = simpleDateFormat.format(date)
        }catch(e :java.lang.Exception){

        }

        return s1

    }

    fun setConnectWithfitness(context : Context, flag : Boolean){
        val sharedPreferences : SharedPreferences = context.getSharedPreferences("User_login", Context.MODE_PRIVATE)
        var editor : SharedPreferences.Editor = sharedPreferences.edit()
        editor.putBoolean("isConnectedFitness", flag)
        editor.commit()
    }

    fun isConnnectWithFitness(context: Context) : Boolean?{
        val sharedPreferences : SharedPreferences = context.getSharedPreferences("User_login", Context.MODE_PRIVATE)
        var flag : Boolean? = sharedPreferences.getBoolean("isConnectedFitness", false)
        return flag

    }



    fun getNewFile(context: Context, directoryName : String, imageName : String) : File{
        var root : String = (Environment.getExternalStorageDirectory()).toString()+ directoryName
        var file : File
        if (isSDCARDMounted()) {
            File( root ).mkdirs()
            file = File( root, imageName )
        } else {
            file = File( context.getFilesDir(), imageName )
        }
        return file;
    }

    fun getFilePath(bitmap : Bitmap, context : Context, path : String) : String{
        var file : File

        try{
            if(bitmap != null){
                var file_name : String = "dnd_" + Date().time+".jpg"
                file = File(path)
                var bytes : ByteArrayOutputStream = ByteArrayOutputStream()
                bitmap.compress( Bitmap.CompressFormat.JPEG, 100, bytes )
                var fo : FileOutputStream
                fo = FileOutputStream( file )
                fo.write(bytes.toByteArray())
                fo.close()

                return file.absolutePath
            }
        }
        catch(e : Exception){

        }
        return ""
    }



    fun decodeFile(f : File, REQUIRED_WIDTH : Int, REQUIRED_HEIGHT : Int) : Bitmap?{
        try{
            var exif : ExifInterface = ExifInterface(f.path)
            var orientation : Int = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL)
            var angle : Float = 0f

            if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
                angle = 90f
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
                angle = 180f
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
                angle = 270f
            }
            var matrix : Matrix = Matrix()
            matrix.postRotate(angle)
            var o : BitmapFactory.Options = BitmapFactory.Options()
            o.inJustDecodeBounds = true
            BitmapFactory.decodeStream( FileInputStream( f ), null, o )
            var REQUIRED_SIZE : Int = 100
            var width_tmp : Int = o.outWidth
            var height_tmp : Int = o.outHeight
            var scale : Int = 1
            var h1 : Int = REQUIRED_HEIGHT
            var w1 : Int = REQUIRED_WIDTH
            if (width_tmp > height_tmp) {
                REQUIRED_SIZE = h1
                h1 = w1
                w1 = REQUIRED_SIZE
            }
            while (true) {
                if (width_tmp / 2 < w1
                    && height_tmp / 2 < h1)
                    break
                width_tmp /= 2
                height_tmp /= 2
                scale *= 2
            }
            var o2 : BitmapFactory.Options = BitmapFactory.Options()
            o2.inSampleSize = scale
            o2.inPurgeable = true

            var correctBmp : Bitmap? = BitmapFactory.decodeStream( FileInputStream(
                    f ), null, o2 )
            correctBmp = Bitmap.createBitmap( correctBmp!!, 0, 0,
                correctBmp.width, correctBmp.height, matrix, true )

            return correctBmp

        }
        catch(e : FileNotFoundException){

        }
        catch(e : OutOfMemoryError){

        }
        catch(e : Exception){

        }

        return null
    }

    fun isSDCARDMounted() : Boolean{
        var status : String = Environment.getExternalStorageState()
        return status.equals( Environment.MEDIA_MOUNTED )
    }

    fun setUser(context: Context, dto : UserDTO?){
        val sharedPreferences : SharedPreferences = context.getSharedPreferences("User_login", Context.MODE_PRIVATE)
        var editor : SharedPreferences.Editor = sharedPreferences.edit()
        if(dto == null){
            editor.putString("getUser", "")
        }
        else{
            var userJSONString : String = Gson().toJson(dto)
            editor.putString("getUser", userJSONString)
        }
        editor.commit()
    }


    fun setCropImagePath(context: Context, cropImagePath : String?){
        val sharedPreferences : SharedPreferences = context.getSharedPreferences("User_login", Context.MODE_PRIVATE)
        var editor : SharedPreferences.Editor = sharedPreferences.edit()
        editor.putString("getCropImagePath", cropImagePath)
        editor.commit()
    }

    fun getCropImagePath(context: Context) : String?{
        val sharedPreferences : SharedPreferences = context.getSharedPreferences("User_login", Context.MODE_PRIVATE)
        var userJSONString : String? = sharedPreferences.getString("getCropImagePath", "")
        return userJSONString

    }


    fun getUser(context: Context) : UserDTO?{
        val sharedPreferences : SharedPreferences = context.getSharedPreferences("User_login", Context.MODE_PRIVATE)
        var userJSONString : String? = sharedPreferences.getString("getUser", "")
        if(!UtilMethod.instance.isStringNullOrNot(userJSONString)){
            val type: Type = object : TypeToken<UserDTO?>() {}.type
            var userDTO : UserDTO = Gson().fromJson(userJSONString, type)
            return userDTO
        }
        else{
            return null
        }

    }

    fun getTime(i1 : Int) : String?{
        var s1 : String? = ""
        var sec : Int = 0
        var min : Int = 0
        var hour : Int = 0
        if(i1>60){
            hour = i1/60
            min = i1%60
        }
        else{
            min = i1

        }
        s1 = getValue(hour)+":"+getValue(min)+":"+getValue(sec)
        return s1
    }



     fun isStringNullOrNot(s1: String?): Boolean{
        if(s1==null){
            return true
        }
        else if(s1.length == 0){
            return true
        }
        else if(s1.equals("")){
            return true
        }
        else if(s1.equals(null)){
            return true
        }
        return false
    }


    fun getDateList() : ArrayList<CurrentDateDTO>{
        var list : ArrayList<CurrentDateDTO> = ArrayList()
        var cal : Calendar = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR , -7)
        for(i in 0..6){
            cal.add(Calendar.DAY_OF_YEAR, 1)
            var date : Date = cal.time
            var s1 : String = SimpleDateFormat("EEE").format(date)
            list.add(CurrentDateDTO(date, s1))

        }

        return list

    }



    fun isEmailValid(s1: String?) : Boolean{
        var pattern : Pattern = Patterns.EMAIL_ADDRESS
        return pattern.matcher(s1).matches()
    }

    fun getValue(i1 : Int) : String?{
        var s1 : String? = ""
        if(i1<10){
            s1 = "0"+i1
        }
        else{
            s1 = ""+i1
        }

        return s1
    }

    fun dialogOK(cxt: Context?, title : String?, msg: String?, btnText : String?, isFinish : Boolean){
        try{
            var alertDialog : AlertDialog.Builder
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                alertDialog = AlertDialog.Builder(cxt, android.R.style.Theme_Material_Light_Dialog_Alert)
            }
            else{
               alertDialog = AlertDialog.Builder(cxt)
            }
            alertDialog.setTitle("")
            alertDialog.setMessage(Html.fromHtml(msg))
            alertDialog.setCancelable(false)
            alertDialog.setPositiveButton(btnText,
                DialogInterface.OnClickListener { dialog, which -> if (isFinish) (cxt as Activity).finish() })
            var alertDialog1 = alertDialog.create()
            alertDialog1.show()
        }
        catch (e: Exception){

        }
    }

    fun dialogForCheckNetworkError(cxt : Context?, onDialogClickListener: OnDialogClickListener, onDialogNegativeClickListener: OnDialogClickListener){
        try{
            var alertDialog : AlertDialog.Builder
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                alertDialog = AlertDialog.Builder(cxt, android.R.style.Theme_Material_Light_Dialog_Alert)
            }
            else{
                alertDialog = AlertDialog.Builder(cxt)
            }
            alertDialog.setTitle("")
            alertDialog.setMessage(Html.fromHtml("Oops!! Internet connection is very slow. Please try again"))
            alertDialog.setCancelable(false)
            alertDialog.setPositiveButton("Try Again", onDialogClickListener)
            alertDialog.setNegativeButton("Cancel", onDialogNegativeClickListener)
            var alertDialog1 = alertDialog.create()
            alertDialog1.show()
        }
        catch (e: Exception){

        }
    }


}