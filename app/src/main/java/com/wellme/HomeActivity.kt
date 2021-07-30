package com.wellme

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.DataType
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.wellme.utils.OnPermissonResult1

class HomeActivity : FragmentActivity() {

    val TAG = "StepCounter"
    private val REQUEST_OAUTH_REQUEST_CODE = 0x1001



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val fitnessOptions = FitnessOptions.builder()
            .addDataType(DataType.TYPE_STEP_COUNT_CUMULATIVE)
            .addDataType(DataType.TYPE_STEP_COUNT_DELTA)
            .addDataType(DataType.TYPE_BASAL_METABOLIC_RATE)
            .addDataType(DataType.TYPE_DISTANCE_DELTA)
            .addDataType(DataType.TYPE_MOVE_MINUTES)
            .build()
        if (!GoogleSignIn.hasPermissions(GoogleSignIn.getLastSignedInAccount(this), fitnessOptions)) {
            GoogleSignIn.requestPermissions(

                this,
                REQUEST_OAUTH_REQUEST_CODE,
                GoogleSignIn.getLastSignedInAccount(this),
                fitnessOptions)
        } else {
            subscribe()
        }

        /*var fragmentTransaction : FragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.container_home, HomeFragment())
        fragmentTransaction.commit()*/
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_OAUTH_REQUEST_CODE) {
                subscribe()
            }
        }else if (resultCode==Activity.RESULT_CANCELED){

            try {
                val onPermissonResult: OnPermissonResult1 = object : OnPermissonResult1 {
                    override fun onPermissionResult(result: Boolean) {
                        Log.v("Result ", " $result")
                        if (result) {
                            val intent : Intent = Intent(this@HomeActivity, LeftSideMenuActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                        else{
                            finish()
                        }

                    }
                }
                checkLocationPermission(this, onPermissonResult)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

    }

    /** Records step data by requesting a subscription to background step data.  */
    fun subscribe() { // To create a subscription, invoke the Recording API. As soon as the subscription is
// active, fitness data will start recording.
        try {

            Fitness.getRecordingClient(this, GoogleSignIn.getLastSignedInAccount(this)!!)
                .subscribe(DataType.TYPE_STEP_COUNT_CUMULATIVE)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.i(TAG, "Successfully subscribed!")
                        newSubscribe()
                    } else {
                        Log.w(TAG, "There was a problem subscribing.", task.exception)
                    }
                }

        }catch (e:Exception){

            e?.printStackTrace()
        }

    }

    fun newSubscribe(){
        Fitness.getRecordingClient(this, GoogleSignIn.getLastSignedInAccount(this)!!)
            .subscribe(DataType.TYPE_CALORIES_EXPENDED)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.i(TAG, "Successfully subscribed!")
                    subscribeMove()
                } else {
                    Log.w(TAG, "There was a problem subscribing.", task.exception)
                }
            }
    }

    fun subscribeMove(){
        Fitness.getRecordingClient(this, GoogleSignIn.getLastSignedInAccount(this)!!)
            .subscribe(DataType.TYPE_MOVE_MINUTES)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.i(TAG, "Successfully subscribed!")
                    subscribeDistance()
                } else {
                    Log.w(TAG, "There was a problem subscribing.", task.exception)
                }
            }
    }

    fun subscribeDistance(){
        Fitness.getRecordingClient(this, GoogleSignIn.getLastSignedInAccount(this)!!)
            .subscribe(DataType.TYPE_DISTANCE_DELTA)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.i(TAG, "Successfully subscribed!")
                    /*var fragmentTransaction : FragmentTransaction = supportFragmentManager.beginTransaction()
                    fragmentTransaction.replace(R.id.container_home, HomeFragment())
                    fragmentTransaction.commit()*/
                    val intent : Intent = Intent(this@HomeActivity, LeftSideMenuActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Log.w(TAG, "There was a problem subscribing.", task.exception)
                }
            }
    }

    fun checkLocationPermission(
        activityCompat: Activity?,
        result: OnPermissonResult1
    ) {

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P){
            Dexter.withActivity(activityCompat)
                .withPermissions(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACTIVITY_RECOGNITION
                ).withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                        if (report.deniedPermissionResponses.size == 0) {
                            result.onPermissionResult(true)
                        }
                        else{
                            result.onPermissionResult(false)
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        permissions: List<PermissionRequest?>?,
                        token: PermissionToken
                    ) {
                        token.continuePermissionRequest()
                    }
                }).onSameThread().check()
        }
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { // only for gingerbread and newer versions
            Dexter.withActivity(activityCompat)
                .withPermissions(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ).withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                        if (report.deniedPermissionResponses.size == 0) {
                            result.onPermissionResult(true)
                        }
                        else{
                            result.onPermissionResult(false)
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        permissions: List<PermissionRequest?>?,
                        token: PermissionToken
                    ) {
                        token.continuePermissionRequest()
                    }
                }).onSameThread().check()
        } else {
            result.onPermissionResult(true)
            //settingsrequest();
        }
    }
}