package com.wellme.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.wellme.LeftSideMenuActivity
import com.wellme.MainActivity
import com.wellme.R
import com.wellme.databinding.FragmentSettingsBinding
import com.wellme.utils.UtilMethod

class SettingFragment : Fragment(), View.OnClickListener{
    var binding : FragmentSettingsBinding? = null
    var regular : Typeface ? = null
    var activity1 : Activity? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_settings, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        regular = ResourcesCompat.getFont(requireContext(), R.font.poppins_regular)
        binding?.tvAboutUs!!.setTypeface(regular)
        binding?.tvRateApp!!.setTypeface(regular)
        binding?.header!!.setTypeface(regular)
        binding?.tvPrivacyPolicy!!.setTypeface(regular)
        binding?.tvSupport!!.setTypeface(regular)
        binding?.tvLogout!!.setTypeface(regular)
        binding?.tvGoPro!!.setTypeface(regular)
        binding?.tvComplaint!!.setTypeface(regular)
        binding?.llRateApp!!.setOnClickListener(this)
        binding?.llAboutUs!!.setOnClickListener(this)
        binding?.llPrivacyPolicy!!.setOnClickListener(this)
        binding?.llSupport!!.setOnClickListener(this)
        binding?.llComplaint!!.setOnClickListener(this)
        binding?.llLogout!!.setOnClickListener(this)
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        this.activity1 = activity
    }

    override fun onResume() {
        super.onResume()
        (activity1 as LeftSideMenuActivity).enableBottomBar()
        (activity1 as LeftSideMenuActivity).setActiveSection(4)

    }

    override fun onClick(v: View?) {
        if(v == binding?.llRateApp){
            var uri : Uri = Uri.parse("market://details?id=" + requireContext().packageName)
            val intent : Intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }
        else if(v == binding?.llLogout){
            showDialog()
        }
        else if(v == binding?.llComplaint){
            var fragmentTransaction : FragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
            var fragment : Fragment = ComplaintFragment()
            fragmentTransaction.replace(R.id.container_home, fragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }
        else if(v == binding?.llAboutUs){

            var fragmentTransaction : FragmentTransaction? = activity?.supportFragmentManager?.beginTransaction()
            var fragment : Fragment = AboutusAndPrivacyFragment()
            var bun : Bundle = Bundle()
            bun.putString("type","About_us")
            fragment.arguments = bun

            fragmentTransaction?.replace(R.id.container_home, fragment)
            fragmentTransaction?.addToBackStack(null)
            fragmentTransaction?.commit()

        }
        else if(v == binding?.llPrivacyPolicy){


            var fragmentTransaction : FragmentTransaction? = activity?.supportFragmentManager?.beginTransaction()
            var fragment : Fragment = AboutusAndPrivacyFragment()
            var bun : Bundle = Bundle()
            bun.putString("type","Privacy_policy")
            fragment.arguments=bun

            fragmentTransaction?.replace(R.id.container_home, fragment)
            fragmentTransaction?.addToBackStack(null)
            fragmentTransaction?.commit()

        }
        else if(v == binding?.llSupport){
            var fragmentTransaction : FragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
            var fragment : Fragment = SupportFragment()
            fragmentTransaction.replace(R.id.container_home, fragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }
    }

    fun showDialog(){
        try{
            var alertDialog : AlertDialog.Builder
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                alertDialog = AlertDialog.Builder(requireContext(), android.R.style.Theme_Material_Light_Dialog_Alert)
            }
            else{
                alertDialog = AlertDialog.Builder(requireContext())
            }
            alertDialog.setTitle("")
            alertDialog.setMessage(Html.fromHtml(context!!.resources.getString(R.string.logout_text)))
            alertDialog.setCancelable(false)
            alertDialog.setNegativeButton(requireContext().resources.getString(R.string.no),
                DialogInterface.OnClickListener { dialog, which ->
                    dialog.dismiss()

                })
            alertDialog.setPositiveButton(requireContext().resources.getString(R.string.yes),
                DialogInterface.OnClickListener { dialog, which ->
                    dialog.dismiss()
                    UtilMethod.instance.setAccessToken("", requireContext())
                    UtilMethod.instance.setUser(requireContext(), null)
                    val intent : Intent = Intent(requireContext(), MainActivity::class.java)
                    startActivity(intent)
                    requireActivity().finish()

                })
            var alertDialog1 = alertDialog.create()
            alertDialog1.show()
        }
        catch (e: Exception){

        }
    }

}