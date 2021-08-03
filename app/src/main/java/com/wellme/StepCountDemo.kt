package com.wellme

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.data.Field
import com.wellme.fragment.HomeFragment

class StepCountDemo : AppCompatActivity(){

    val TAG = "StepCounter"
    private val REQUEST_OAUTH_REQUEST_CODE = 0x1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fitnessOptions = FitnessOptions.builder()
                .addDataType(DataType.TYPE_STEP_COUNT_CUMULATIVE)
                .addDataType(DataType.TYPE_STEP_COUNT_DELTA)
                .addDataType(DataType.AGGREGATE_CALORIES_EXPENDED)
                .addDataType(DataType.AGGREGATE_DISTANCE_DELTA)
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
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_OAUTH_REQUEST_CODE) {
                subscribe()
            }
        }
    }

    /** Records step data by requesting a subscription to background step data.  */
    fun subscribe() { // To create a subscription, invoke the Recording API. As soon as the subscription is
// active, fitness data will start recording.
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
                    var fragmentTransaction : FragmentTransaction = supportFragmentManager.beginTransaction()
                    fragmentTransaction.replace(R.id.container_splash, HomeFragment())
                    fragmentTransaction.addToBackStack(null)
                    fragmentTransaction.commit()
                } else {
                    Log.w(TAG, "There was a problem subscribing.", task.exception)
                }
            }
    }

    /**
     * Reads the current daily step total, computed from midnight of the current day on the device's
     * current timezone.
     */
    private fun readData() {
        Fitness.getHistoryClient(this, GoogleSignIn.getLastSignedInAccount(this)!!)
                .readDailyTotal(DataType.TYPE_STEP_COUNT_DELTA)
                .addOnSuccessListener { dataSet ->
                    val total = if (dataSet.isEmpty) 0 else dataSet.dataPoints[0].getValue(Field.FIELD_STEPS).asInt().toLong()
                    Log.i(TAG, "Total steps: $total")
                }
                .addOnFailureListener { e -> Log.w(TAG, "There was a problem getting the step count.", e) }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean { // Inflate the main; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_read_data) {
            readData()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

}