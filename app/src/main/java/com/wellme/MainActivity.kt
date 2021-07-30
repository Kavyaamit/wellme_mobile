package com.wellme

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import com.google.firebase.FirebaseApp
import com.wellme.fragment.SplashFragment

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
}
