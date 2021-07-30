package com.wellme

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.internal.NavigationMenu
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.squareup.picasso.Picasso
import com.wellme.adapter.MenuAdapter
import com.wellme.dto.MenuDTO
import com.wellme.dto.UserDTO
import com.wellme.fragment.*
import com.wellme.utils.AppConstants
import com.wellme.utils.OnItemClickListener
import com.wellme.utils.OnPermissonResult1
import com.wellme.utils.UtilMethod

class LeftSideMenuActivity : AppCompatActivity(), View.OnClickListener {

    var rv_menu : RecyclerView? = null
    var iv_cross : ImageView? = null
    var iv_coach : ImageView? = null
    var iv_plan : ImageView? = null
    var iv_offer : ImageView? = null
    var iv_home : ImageView? = null
    var tv_name : TextView? = null
    var tv_home : TextView? = null
    var tv_offer : TextView? = null
    var tv_plan : TextView? = null
    var tv_coach : TextView? = null
    var user_image : ImageView? = null
    var drawer_layout : DrawerLayout? = null
    var tv_location : TextView? = null
    var typeface : Typeface? = null
    var menuAdapter : MenuAdapter? = null
    var linearLayoutManager : LinearLayoutManager? = null
    var menuList : ArrayList<MenuDTO> = ArrayList()
    var bottomNavigationView : BottomNavigationView? = null
    var view1 : View? = null
    var view2 : View? = null
    var view3 : View? = null
    var view4 : View? = null
    var ll_home : LinearLayout? = null
    var ll_plan : LinearLayout? = null
    var ll_offer : LinearLayout? = null
    var ll_coach : LinearLayout? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_navigation_layout)
        initView()

    }

    fun initView(){
        linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager!!.orientation = LinearLayoutManager.VERTICAL
        typeface = ResourcesCompat.getFont(this, R.font.poppins_regular)
        rv_menu = findViewById(R.id.rv_menu)
        tv_name = findViewById(R.id.tv_name)
        tv_location = findViewById(R.id.tv_location)
        ll_home = findViewById(R.id.ll_home)
        ll_plan = findViewById(R.id.ll_plan)
        ll_offer = findViewById(R.id.ll_offer)
        ll_coach = findViewById(R.id.ll_coach)
        drawer_layout = findViewById(R.id.drawer_layout)
        iv_cross = findViewById(R.id.iv_cross)
        iv_coach = findViewById(R.id.iv_coach)
        user_image = findViewById(R.id.user_image)
        iv_plan = findViewById(R.id.iv_plan)
        iv_home = findViewById(R.id.iv_home)
        iv_offer = findViewById(R.id.iv_offer)
        view1 = findViewById(R.id.view1);
        view2 = findViewById(R.id.view2);
        view3 = findViewById(R.id.view3);
        view4 = findViewById(R.id.view4);
        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        tv_home = findViewById(R.id.tv_home)
        tv_plan = findViewById(R.id.tv_plan)
        tv_offer = findViewById(R.id.tv_offer)
        tv_coach = findViewById(R.id.tv_coach)
        tv_name!!.setTypeface(typeface)
        tv_location!!.setTypeface(typeface)
        tv_home!!.setTypeface(typeface)
        tv_offer!!.setTypeface(typeface)
        tv_coach!!.setTypeface(typeface)
        tv_plan!!.setTypeface(typeface)


        rv_menu!!.layoutManager = linearLayoutManager

        menuList.add(MenuDTO(this.resources.getString(R.string.profile_menu), this.resources.getDrawable(R.drawable.profile_menu)))
        menuList.add(MenuDTO(this.resources.getString(R.string.chat_with_us), this.resources.getDrawable(R.drawable.chat_with_us_menu)))
        menuList.add(MenuDTO(this.resources.getString(R.string.my_diet_and_fitness_plan), this.resources.getDrawable(R.drawable.my_diet_fitness)))
        menuList.add(MenuDTO(this.resources.getString(R.string.my_subscriptions), this.resources.getDrawable(R.drawable.plans_active)))
        menuList.add(MenuDTO(this.resources.getString(R.string.reminder), this.resources.getDrawable(R.drawable.reminder_menu)))
        menuList.add(MenuDTO(this.resources.getString(R.string.progress), this.resources.getDrawable(R.drawable.progress_menu)))
        menuList.add(MenuDTO(this.resources.getString(R.string.goals), this.resources.getDrawable(R.drawable.goal_menu)))
        menuList.add(MenuDTO(this.resources.getString(R.string.notification), this.resources.getDrawable(R.drawable.notification)))
        menuList.add(MenuDTO(this.resources.getString(R.string.setting), this.resources.getDrawable(R.drawable.settings_menu)))
        menuList.add(MenuDTO(this.resources.getString(R.string.connect_with_fit), this.resources.getDrawable(R.drawable.connect_googlefit_menu)))
        menuList.add(MenuDTO(this.resources.getString(R.string.privacy_center), this.resources.getDrawable(R.drawable.privacy_center_menu)))
        menuList.add(MenuDTO(this.resources.getString(R.string.refer_earn), this.resources.getDrawable(R.drawable.share)))
        menuList.add(MenuDTO(this.resources.getString(R.string.logout), this.resources.getDrawable(R.drawable.logout_menu)))

        menuAdapter = MenuAdapter(this, menuList, onItemClickCallback)
        rv_menu!!.adapter = menuAdapter
        iv_cross!!.setOnClickListener(this)
        ll_home!!.setOnClickListener(this)
        ll_plan!!.setOnClickListener(this)
        ll_offer!!.setOnClickListener(this)
        ll_coach!!.setOnClickListener(this)
        changeLeftSide()

        var fragmentTransaction : FragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.container_home, HomeFragment())
        fragmentTransaction.commit()

    }

    public fun setActiveSection(i1 : Int){
        if(i1 == 0){
            iv_home!!.setImageDrawable(resources!!.getDrawable(R.drawable.home_active))
            iv_plan!!.setImageDrawable(resources!!.getDrawable(R.drawable.plan_inactive))
            iv_offer!!.setImageDrawable(resources!!.getDrawable(R.drawable.offer_inactive))
            iv_coach!!.setImageDrawable(resources!!.getDrawable(R.drawable.coach_inactive))
            tv_home!!.setTextColor(resources!!.getColor(R.color.base_color))
            tv_plan!!.setTextColor(resources!!.getColor(R.color.black))
            tv_offer!!.setTextColor(resources!!.getColor(R.color.black))
            tv_coach!!.setTextColor(resources!!.getColor(R.color.black))
            view1!!.setBackgroundColor(resources!!.getColor(R.color.base_color))
            view2!!.setBackgroundColor(resources!!.getColor(R.color.grey))
            view3!!.setBackgroundColor(resources!!.getColor(R.color.grey))
            view4!!.setBackgroundColor(resources!!.getColor(R.color.grey))
        }
        else if(i1 == 1){
            iv_home!!.setImageDrawable(resources!!.getDrawable(R.drawable.home_inactive))
            iv_plan!!.setImageDrawable(resources!!.getDrawable(R.drawable.plans_active))
            iv_offer!!.setImageDrawable(resources!!.getDrawable(R.drawable.offer_inactive))
            iv_coach!!.setImageDrawable(resources!!.getDrawable(R.drawable.coach_inactive))
            tv_home!!.setTextColor(resources!!.getColor(R.color.black))
            tv_plan!!.setTextColor(resources!!.getColor(R.color.base_color))
            tv_offer!!.setTextColor(resources!!.getColor(R.color.black))
            tv_coach!!.setTextColor(resources!!.getColor(R.color.black))
            view1!!.setBackgroundColor(resources!!.getColor(R.color.grey))
            view2!!.setBackgroundColor(resources!!.getColor(R.color.base_color))
            view3!!.setBackgroundColor(resources!!.getColor(R.color.grey))
            view4!!.setBackgroundColor(resources!!.getColor(R.color.grey))
        }
        else if(i1 == 2){
            iv_home!!.setImageDrawable(resources!!.getDrawable(R.drawable.home_inactive))
            iv_plan!!.setImageDrawable(resources!!.getDrawable(R.drawable.plan_inactive))
            iv_offer!!.setImageDrawable(resources!!.getDrawable(R.drawable.offer_active))
            iv_coach!!.setImageDrawable(resources!!.getDrawable(R.drawable.coach_inactive))
            tv_home!!.setTextColor(resources!!.getColor(R.color.black))
            tv_plan!!.setTextColor(resources!!.getColor(R.color.black))
            tv_offer!!.setTextColor(resources!!.getColor(R.color.base_color))
            tv_coach!!.setTextColor(resources!!.getColor(R.color.black))
            view1!!.setBackgroundColor(resources!!.getColor(R.color.grey))
            view2!!.setBackgroundColor(resources!!.getColor(R.color.grey))
            view3!!.setBackgroundColor(resources!!.getColor(R.color.base_color))
            view4!!.setBackgroundColor(resources!!.getColor(R.color.grey))
        }
        else if(i1 == 3){
            iv_home!!.setImageDrawable(resources!!.getDrawable(R.drawable.home_inactive))
            iv_plan!!.setImageDrawable(resources!!.getDrawable(R.drawable.plan_inactive))
            iv_offer!!.setImageDrawable(resources!!.getDrawable(R.drawable.offer_inactive))
            iv_coach!!.setImageDrawable(resources!!.getDrawable(R.drawable.coach_active))
            tv_home!!.setTextColor(resources!!.getColor(R.color.black))
            tv_plan!!.setTextColor(resources!!.getColor(R.color.black))
            tv_offer!!.setTextColor(resources!!.getColor(R.color.black))
            tv_coach!!.setTextColor(resources!!.getColor(R.color.base_color))
            view1!!.setBackgroundColor(resources!!.getColor(R.color.grey))
            view2!!.setBackgroundColor(resources!!.getColor(R.color.grey))
            view3!!.setBackgroundColor(resources!!.getColor(R.color.grey))
            view4!!.setBackgroundColor(resources!!.getColor(R.color.base_color))
        }
        else{
            iv_home!!.setImageDrawable(resources!!.getDrawable(R.drawable.home_inactive))
            iv_plan!!.setImageDrawable(resources!!.getDrawable(R.drawable.plan_inactive))
            iv_offer!!.setImageDrawable(resources!!.getDrawable(R.drawable.offer_inactive))
            iv_coach!!.setImageDrawable(resources!!.getDrawable(R.drawable.coach_inactive))
            tv_home!!.setTextColor(resources!!.getColor(R.color.black))
            tv_plan!!.setTextColor(resources!!.getColor(R.color.black))
            tv_offer!!.setTextColor(resources!!.getColor(R.color.black))
            tv_coach!!.setTextColor(resources!!.getColor(R.color.black))
            view1!!.setBackgroundColor(resources!!.getColor(R.color.grey))
            view2!!.setBackgroundColor(resources!!.getColor(R.color.grey))
            view3!!.setBackgroundColor(resources!!.getColor(R.color.grey))
            view4!!.setBackgroundColor(resources!!.getColor(R.color.grey))
        }
    }



    public fun disableBottomBar(){
        bottomNavigationView!!.visibility = View.GONE
    }

    public fun enableBottomBar(){
        bottomNavigationView!!.visibility = View.VISIBLE
    }

    public fun changeLeftSide(){
        var userDTO : UserDTO? = UtilMethod.instance.getUser(applicationContext)
        var s_name : String? = ""
        var s_first_name : String? = ""
        var s_last_name : String? = ""
        var s_city_name : String? = ""
        var s_state_name : String? = ""
        var s_image : String? = ""
        if(userDTO!=null){
            s_city_name = userDTO!!.city
            s_state_name = userDTO!!.state
            s_image = userDTO!!.profile_image
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
                tv_location!!.setText(location)
            }
            else{
                tv_location!!.visibility = View.GONE
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
                tv_name?.setText(s_name)
            }

            if(!UtilMethod.instance.isStringNullOrNot(s_image)){
                Picasso.get().load(AppConstants.BASE_URL_NEW+""+s_image).error(R.drawable.default_image).into(user_image)
            }

        }
    }

    var onItemClickCallback : OnItemClickListener.OnItemClickCallback = object : OnItemClickListener.OnItemClickCallback{
        override fun onItemClicked(view: View?, position: Int) {
            closeDrawer1()
            if(position == 0){
                var fragmentTransaction : FragmentTransaction? = supportFragmentManager.beginTransaction()
                fragmentTransaction?.replace(R.id.container_home, ProfileFragment())
                fragmentTransaction?.addToBackStack(null)
                fragmentTransaction?.commit()
            }
            else if(position == 1){
                var fragmentTransaction : FragmentTransaction? = supportFragmentManager.beginTransaction()
                fragmentTransaction?.replace(R.id.container_home, ChatFragment())
                fragmentTransaction?.addToBackStack(null)
                fragmentTransaction?.commit()
            }
            else if(position == 2){
                var fragmentTransaction : FragmentTransaction? = supportFragmentManager.beginTransaction()
                fragmentTransaction?.replace(R.id.container_home, MyDietFitnessFragment())
                fragmentTransaction?.addToBackStack(null)
                fragmentTransaction?.commit()
            }
            else if(position == 3){
                var fragmentTransaction : FragmentTransaction? = supportFragmentManager.beginTransaction()
                fragmentTransaction?.replace(R.id.container_home, MySubscriptionsFragment())
                fragmentTransaction?.addToBackStack(null)
                fragmentTransaction?.commit()
            }
            else if(position == 4){
                var fragmentTransaction : FragmentTransaction = supportFragmentManager.beginTransaction()
                fragmentTransaction?.replace(R.id.container_home, ReminderFragment())
                fragmentTransaction?.addToBackStack(null)
                fragmentTransaction?.commit()
            }
            else if(position == 5){
                var fragmentTransaction : FragmentTransaction = supportFragmentManager.beginTransaction()
                fragmentTransaction?.replace(R.id.container_home, WeightGoalProgressFragment())
                fragmentTransaction?.addToBackStack(null)
                fragmentTransaction?.commit()
            }
            else if(position == 6){
                var fragmentTransaction : FragmentTransaction = supportFragmentManager.beginTransaction()
                fragmentTransaction.replace(R.id.container_home, UpdateGoalFragment())
                fragmentTransaction.addToBackStack(null)
                fragmentTransaction.commit()
            }
            else if(position == 7){
                var fragmentTransaction : FragmentTransaction? = supportFragmentManager.beginTransaction()
                fragmentTransaction?.replace(R.id.container_home, NotificationFragment())
                fragmentTransaction?.addToBackStack(null)
                fragmentTransaction?.commit()
            }
            else if(position == 8){
                var fragmentTransaction : FragmentTransaction? = supportFragmentManager.beginTransaction()
                fragmentTransaction?.replace(R.id.container_home, SettingFragment())
                fragmentTransaction?.addToBackStack(null)
                fragmentTransaction?.commit()
            }
            else if(position == 9){
                if(UtilMethod.instance.appInstalledOrNot(this@LeftSideMenuActivity, "com.google.android.apps.fitness")){
                    try {
                        val onPermissonResult: OnPermissonResult1 = object : OnPermissonResult1 {
                            override fun onPermissionResult(result: Boolean) {
                                Log.v("Result ", " $result")
                                if (result) {
                                    UtilMethod.instance.setConnectWithfitness(this@LeftSideMenuActivity, true)
                                    val intent : Intent = Intent(this@LeftSideMenuActivity, HomeActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                }
                                else{
                                    finish()
                                }

                            }
                        }
                        checkLocationPermission(this@LeftSideMenuActivity, onPermissonResult)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                else{
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.apps.fitness")));
                }


            }
            else if(position == 11){
                var fragmentTransaction : FragmentTransaction? = supportFragmentManager.beginTransaction()
                fragmentTransaction?.replace(R.id.container_home, ReferAndEarnFragment())
                fragmentTransaction?.addToBackStack(null)
                fragmentTransaction?.commit()
            }
            else if(position == 12){
                showLogoutDialog()
            }

        }
    }

    fun showLogoutDialog(){
        try{
            var alertDialog : AlertDialog.Builder
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                alertDialog = AlertDialog.Builder(this, android.R.style.Theme_Material_Light_Dialog_Alert)
            }
            else{
                alertDialog = AlertDialog.Builder(this)
            }
            alertDialog.setTitle("")
            alertDialog.setMessage(Html.fromHtml(this.resources.getString(R.string.logout_text)))
            alertDialog.setCancelable(false)
            alertDialog.setNegativeButton(this.resources.getString(R.string.no),
                DialogInterface.OnClickListener { dialog, which ->
                    dialog.dismiss()

                })
            alertDialog.setPositiveButton(this.resources.getString(R.string.yes),
                DialogInterface.OnClickListener { dialog, which ->
                    dialog.dismiss()
                    UtilMethod.instance.setAccessToken("", this)
                    UtilMethod.instance.setUser(this, null)
                    val intent : Intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()

                })
            var alertDialog1 = alertDialog.create()
            alertDialog1.show()
        }
        catch (e: Exception){

        }
    }

     public fun closeDrawer1(){
        if (drawer_layout!!.isDrawerOpen(GravityCompat.START)) {
            drawer_layout!!.closeDrawer(GravityCompat.START)
        }else{
            drawer_layout!!.openDrawer(GravityCompat.START)
        }
    }

    override fun onClick(v: View?) {
        if(v!!.id == R.id.iv_cross){
            closeDrawer1()
        }
        else if(v!!.id == R.id.ll_coach){
            setActiveSection(3)
            var fragmentTransaction : FragmentTransaction? = supportFragmentManager.beginTransaction()
            fragmentTransaction?.replace(R.id.container_home, MyCoachFragment())
            fragmentTransaction?.addToBackStack(null)
            fragmentTransaction?.commit()
        }
        else if(v!!.id == R.id.ll_plan){
            setActiveSection(1)
            var fragmentTransaction : FragmentTransaction? = supportFragmentManager.beginTransaction()
            fragmentTransaction?.replace(R.id.container_home, SubscriptionFragment())
            fragmentTransaction?.addToBackStack(null)
            fragmentTransaction?.commit()
        }
        else if(v!!.id == R.id.ll_offer){
            setActiveSection(2)
            var fragmentTransaction : FragmentTransaction? = supportFragmentManager.beginTransaction()
            fragmentTransaction?.replace(R.id.container_home, ViewAllOfferListFragment())
            fragmentTransaction?.addToBackStack(null)
            fragmentTransaction?.commit()
        }
        else if(v!!.id == R.id.ll_home){
            setActiveSection(0)
            try {
                val onPermissonResult: OnPermissonResult1 = object : OnPermissonResult1 {
                    override fun onPermissionResult(result: Boolean) {
                        Log.v("Result ", " $result")
                        if (result) {
                            /*val intent : Intent = Intent(this@LeftSideMenuActivity, HomeActivity::class.java)
                            startActivity(intent)
                            finish()*/
                            var count : Int = supportFragmentManager!!.backStackEntryCount
                            if(count > 0){
                                for(i in 0..count){
                                    supportFragmentManager!!.popBackStack()
                                }
                            }
                            var fragmentTransaction : FragmentTransaction? = supportFragmentManager.beginTransaction()
                            fragmentTransaction?.replace(R.id.container_home, HomeFragment())
                            fragmentTransaction?.addToBackStack(null)
                            fragmentTransaction?.commit()
                        }
                        else{
                           // finish()
                        }

                    }
                }
                checkLocationPermission(this, onPermissonResult)
            } catch (e: Exception) {
                e.printStackTrace()
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