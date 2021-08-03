package com.wellme

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import com.google.firebase.FirebaseApp
import com.wellme.fragment.SplashFragment
import io.branch.referral.Branch


class MainActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        FirebaseApp.initializeApp(this)
        var fragment: Fragment = SplashFragment()
        fragment.apply {
            arguments = Bundle().apply {
                putString("id", "123")
                putString("name", "Mukesh")
            }
        }
        val fragmentTransaction : FragmentTransaction? = supportFragmentManager?.beginTransaction()
        fragmentTransaction?.replace(R.id.container_splash, fragment)
        fragmentTransaction?.commit()

    }

    override fun onStart() {
        super.onStart()
        val branch = Branch.getInstance()

        // Branch init
        branch.initSession({ referringParams, error ->
            if (error == null) {
                // params are the deep linked params associated with the link that the user clicked -> was re-directed to this app
                // params will be empty if no data found
                // ... insert custom logic here ...
                Log.i("BRANCH SDK", referringParams.toString())
            } else {
                Log.i("BRANCH SDK", error.message)
            }
        }, this.intent.data, this)
    }




}
