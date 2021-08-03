package com.wellme.receiver

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.job.JobInfo
import android.app.job.JobParameters
import android.app.job.JobScheduler
import android.app.job.JobService
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.gson.Gson
import com.wellme.MainActivity
import com.wellme.R
import com.wellme.dto.ReminderDTO
import com.wellme.utils.UtilMethod
import java.util.*
import kotlin.collections.ArrayList

class DrinkWaterSchedular : JobService(){
    var notifManager : NotificationManager? = null
    val gson : Gson? = Gson()
    var reminder_list : List<ReminderDTO> = ArrayList()
    var s_reminder : String? = ""
    var reminder_old_list : ArrayList<ReminderDTO> = ArrayList()
    var reminderDTO : ReminderDTO? = null
    var s_time : String? = ""
    var s_repeating_time : String? = ""

    override fun onStopJob(params: JobParameters?): Boolean {

        return true
    }

    override fun onStartJob(params: JobParameters?): Boolean {

        s_reminder = UtilMethod.instance.getReminderList(applicationContext)
        if(!UtilMethod.instance.isStringNullOrNot(s_reminder)) {
            reminder_list = gson!!.fromJson(s_reminder, Array<ReminderDTO>::class.java).toList()
            reminder_old_list = ArrayList()
            reminder_old_list.addAll(reminder_list)
            if(reminder_old_list!=null){
                reminderDTO = reminder_old_list.get(4)
                if(reminderDTO!=null){
                    s_time = reminderDTO!!.time
                    s_repeating_time = reminderDTO!!.repeating_val
                }
            }

        }

        if(!UtilMethod.instance.isStringNullOrNot(s_time)){
            var diff : Long = 0
            if(s_repeating_time.equals("day")){
                diff = 24 * 60 * 60 * 1000
            }
            else if(s_repeating_time.equals("week")){
                diff = 7 * 24 * 60 * 60 * 1000
            }
            else if(s_repeating_time.equals("time") || s_repeating_time.equals("hour")){
                var s1 : String? = UtilMethod.instance.getReminderTimeList(applicationContext)
                if(!UtilMethod.instance.isStringNullOrNot(s1)){
                    var list : List<String> = gson!!.fromJson(s1, Array<String>::class.java).toList()
                    var list1 : ArrayList<String> = ArrayList()
                    list1.addAll(list)
                    var calendar : Calendar = Calendar.getInstance()
                    var current_calendar : Calendar = Calendar.getInstance()
                    if(list1!=null){
                        for(i in 0..list1.size){
                            var date : Date? = UtilMethod.instance.getDateWithoutDate(list1.get(i))
                            if(date!=null) {
                                calendar.set(Calendar.HOUR_OF_DAY, date!!.hours)
                                calendar.set(Calendar.MINUTE, date!!.minutes)
                                calendar.set(Calendar.SECOND, 0)
                                if(current_calendar.timeInMillis < calendar.timeInMillis ){
                                    diff = calendar.timeInMillis - current_calendar.timeInMillis
                                    break
                                }
                            }
                        }

                        if(diff <= 0){
                            calendar.add(Calendar.DAY_OF_YEAR, 1)
                            calendar.set(Calendar.HOUR_OF_DAY, 8)
                            calendar.set(Calendar.MINUTE, 0)
                            calendar.set(Calendar.SECOND, 0)
                            diff = calendar.timeInMillis - current_calendar.timeInMillis
                        }

                    }
                }
            }

            if(diff>0) {
                var componentName: ComponentName =
                    ComponentName(applicationContext, DrinkWaterSchedular::class.java)
                var info: JobInfo? = null
                if (Build.VERSION.SDK_INT > 23) {
                    info = JobInfo.Builder(567, componentName)
                        .setRequiresCharging(false)
                        .setRequiredNetworkType(JobInfo.NETWORK_TYPE_NONE)
                        .setPersisted(false)
                        .setMinimumLatency(diff)
                        .build()
                } else {
                    info = JobInfo.Builder(567, componentName)
                        .setRequiresCharging(false)
                        .setRequiredNetworkType(JobInfo.NETWORK_TYPE_NONE)
                        .setPersisted(false)
                        .setPeriodic(diff)
                        .build()
                }
                var scheduler: JobScheduler =
                    getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
                scheduler!!.schedule(info)
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                createNotification("Drink Water")
            } else {
                sendNotification(Intent(), "Drink Water")
            }
        }


        return true
    }


    fun createNotification(title: String) {
        val NOTIFY_ID = 1002
        val name = "my_package_channel"
        val id = "my_package_channel_1" // The user-visible name of the channel.
        val description = "my_package_first_channel" // The user-visible description of the channel.
        var intent: Intent = Intent(applicationContext, MainActivity::class.java)
        val pendingIntent: PendingIntent
        val builder: NotificationCompat.Builder

        if (notifManager == null) {
            notifManager =
                applicationContext!!.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            var mChannel: NotificationChannel = notifManager!!.getNotificationChannel(id)
            if (mChannel == null) {
                mChannel = NotificationChannel(id, name, importance)
                mChannel.description = description
                mChannel.enableVibration(true)
                mChannel.vibrationPattern = longArrayOf(
                    100,
                    200,
                    300,
                    400,
                    500,
                    400,
                    300,
                    200,
                    400
                )
                notifManager!!.createNotificationChannel(mChannel)
            }
            builder = NotificationCompat.Builder(applicationContext, id)
            intent.flags = (Intent.FLAG_ACTIVITY_NEW_TASK
                    or Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
                    or Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
            pendingIntent = PendingIntent.getActivity(applicationContext, 1, intent, PendingIntent.FLAG_ONE_SHOT)
            builder.setContentTitle("Drink Water") // required
                .setSmallIcon(R.drawable.logo) // required
                .setContentText("Please take water for your good health") // required
                .setDefaults(Notification.DEFAULT_ALL)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setTicker("Please take water for your good health")
                .setVibrate(longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400))
        } else {
            builder = NotificationCompat.Builder(applicationContext)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            pendingIntent = PendingIntent.getActivity(applicationContext, 1, intent, PendingIntent.FLAG_ONE_SHOT)
            builder.setContentTitle("Drink Water") // required
                .setSmallIcon(R.drawable.logo) // required
                .setContentText("Please take water for your good health") // required
                .setDefaults(Notification.DEFAULT_ALL)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setTicker("Please take water for your good health")
                .setVibrate(longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400))
                .setPriority(Notification.PRIORITY_HIGH)
        }
        val notification: Notification = builder.build()
        notifManager!!.notify(NOTIFY_ID, notification)
    }

    fun sendNotification(
        intent: Intent,
        title: String
    ) {
        var intent = intent
        try {
            val bundle = Bundle()
            intent = Intent(applicationContext, MainActivity::class.java)
            intent.putExtras(bundle)
        } catch (e: Exception) {
            Log.v("RideData Exception ", " $e")
            e.printStackTrace()
        }
        intent.flags = (Intent.FLAG_ACTIVITY_NEW_TASK
                or Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
                or Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
        val pendingIntent = PendingIntent.getActivity(
            applicationContext, 1, intent,
            PendingIntent.FLAG_ONE_SHOT
        )

        val notificationBuilder = NotificationCompat.Builder(applicationContext)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notificationBuilder.setSmallIcon(R.drawable.logo)
            val bitmap = BitmapFactory.decodeResource(applicationContext!!.getResources(), R.drawable.logo)
            notificationBuilder.color = applicationContext!!.getResources().getColor(R.color.transparent)
            notificationBuilder.setLargeIcon(bitmap)
        } else {
            notificationBuilder.setSmallIcon(R.drawable.logo)
        }
        try {
            notificationBuilder.setContentTitle("Drink Water")
            notificationBuilder.setContentText("Please take water for your good health")
            //             notificationBuilder.setLargeIcon(bitmap);
        } catch (e: Exception) {
            e.printStackTrace()
        }
        notificationBuilder.setAutoCancel(true)
        val pattern = longArrayOf(500, 500, 500, 500, 500, 500, 500, 500, 500)
        notificationBuilder.setVibrate(pattern)
        notificationBuilder.setContentIntent(pendingIntent)
        notificationBuilder.setStyle(NotificationCompat.BigTextStyle().bigText("Please take water for your good health"))
        val notificationManager =
            applicationContext!!.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(11, notificationBuilder.build())
    }
}