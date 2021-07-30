package com.wellme.fragment

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.wellme.LeftSideMenuActivity
import com.wellme.R
import com.wellme.dto.UserDTO
import com.wellme.utils.OnPermissonResult1
import com.wellme.utils.UtilMethod
import java.util.*
import kotlin.concurrent.timerTask



class SplashFragment : Fragment(){
    var id: String? = ""
    var name : String? = ""
    var view1 : View? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        id = arguments?.getString("id")
        name = arguments?.getString("name")
        view1 = inflater.inflate(R.layout.fragment_splash, container, false)
        return view1
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Timer("Setting", false).schedule(timerTask{

            var userDTO : UserDTO? = UtilMethod.instance.getUser(requireContext())
            var accessToken : String? = UtilMethod.instance.getAccessToken(requireContext())
            if(userDTO!=null){
                var fitness_level : String? = userDTO.fitness_level
                var fitness_purpose : String? = userDTO.fitness_purpose
                var target_time : String? = userDTO.target_weight_time
                var fitness_target : String? = userDTO.fitness_target

                if(UtilMethod.instance.isStringNullOrNot(fitness_target)){
                    var fragmentTransaction : FragmentTransaction? = activity!!.supportFragmentManager.beginTransaction()
                    var fragment : Fragment = FitnessPurposeFragment()
                    fragmentTransaction?.replace(R.id.container_splash, fragment)
                    fragmentTransaction?.commit()
                }
                else if(UtilMethod.instance.isStringNullOrNot(fitness_level)){
                    var fragmentTransaction : FragmentTransaction? = activity!!.supportFragmentManager.beginTransaction()
                    var fragment : Fragment = FitnessLevelFragment()
                    fragmentTransaction?.replace(R.id.container_splash, fragment)
                    fragmentTransaction?.commit()
                }
                else if(UtilMethod.instance.isStringNullOrNot(fitness_purpose)){
                    var fragmentTransaction : FragmentTransaction? = activity!!.supportFragmentManager.beginTransaction()
                    var fragment : Fragment = FitnessNextStepFragment()
                    fragmentTransaction?.replace(R.id.container_splash, fragment)
                    fragmentTransaction?.commit()
                }
                else if(UtilMethod.instance.isStringNullOrNot(target_time)){
                    var fragmentTransaction : FragmentTransaction? = activity!!.supportFragmentManager.beginTransaction()
                    var fragment : Fragment = WeightSummaryFragment()
                    fragmentTransaction?.replace(R.id.container_splash, fragment)
                    fragmentTransaction?.commit()
                }
                else{
                    try {
                        val onPermissonResult: OnPermissonResult1 = object : OnPermissonResult1 {
                            override fun onPermissionResult(result: Boolean) {
                                Log.v("Result ", " $result")
                                if (result) {
                                    UtilMethod.instance.setConnectWithfitness(requireContext(), false)
                                    val intent : Intent = Intent(requireContext(), LeftSideMenuActivity::class.java)
                                    startActivity(intent)
                                    requireActivity().finish()
                                }
                                else{
                                    requireActivity().finish()
                                }

                            }
                        }
                        checkLocationPermission(requireActivity(), onPermissonResult)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            else if(!UtilMethod.instance.isStringNullOrNot(accessToken)){
                var fragmentTransaction : FragmentTransaction? =  activity?.supportFragmentManager?.beginTransaction()
                fragmentTransaction?.replace(R.id.container_splash, RegistrationWithPersonalDetailsFragment())
                fragmentTransaction?.commit()
            }
            else{
                var fragmentTransaction : FragmentTransaction? =  activity?.supportFragmentManager?.beginTransaction()
                fragmentTransaction?.replace(R.id.container_splash, LoginFragment())
                fragmentTransaction?.commit()
            }



        }, 5000)
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