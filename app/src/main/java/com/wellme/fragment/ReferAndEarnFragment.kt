package com.wellme.fragment

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.wellme.R
import com.wellme.databinding.FragmentWalletBinding
import com.wellme.dto.UserDTO
import com.wellme.utils.UtilMethod

class ReferAndEarnFragment : Fragment(), View.OnClickListener{
    var binding : FragmentWalletBinding? = null
    var regular : Typeface? = null
    var bold : Typeface? = null
    var userID: String? = ""
    var share_link: String? = ""

    override fun onClick(p0: View?) {
        if(p0 == binding!!.back){
            requireActivity().supportFragmentManager.popBackStack()
        }else if(p0 == binding!!.ivWhatsapp){
          shareDeepLink("com.whatsapp")
        }else if(p0 == binding!!.ivInsta){
          shareDeepLink("com.instagram.android")
        }else if(p0 == binding!!.ivFacebook){
          shareDeepLink("com.facebook.katana")
        }else if(p0 == binding!!.btnReferEarn){
          shareDeepLink("")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_wallet,  container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getView1()
    }

    fun getView1(){
        regular = ResourcesCompat.getFont(context!!, R.font.poppins_regular)
        bold = ResourcesCompat.getFont(context!!, R.font.poppins_bold)
        binding!!.tvWalletAmount.setTypeface(bold)
        binding!!.tvTransaction.setTypeface(bold)
        binding!!.header.setTypeface(regular)
        binding!!.btnReferEarn.setTypeface(regular)
        binding!!.tvError.setTypeface(regular)
        binding!!.back.setOnClickListener(this)

        binding!!.ivWhatsapp.setOnClickListener(this)
        binding!!.ivInsta.setOnClickListener(this)
        binding!!.ivFacebook.setOnClickListener(this)
        binding!!.ivShareLink.setOnClickListener(this)
        binding!!.btnReferEarn.setOnClickListener(this)

        if (UtilMethod.instance.appInstalledOrNot(requireContext(),"com.whatsapp")){
            binding!!.ivWhatsapp.visibility = View.VISIBLE
        }else{
            binding!!.ivWhatsapp.visibility = View.GONE
        }

        if (UtilMethod.instance.appInstalledOrNot(requireContext(),"com.facebook.katana")){
            binding!!.ivFacebook.visibility = View.VISIBLE
        }else{
            binding!!.ivFacebook.visibility = View.GONE
        }

        if (UtilMethod.instance.appInstalledOrNot(requireContext(),"com.instagram.android")){
            binding!!.ivInsta.visibility = View.VISIBLE
        }else{
            binding!!.ivInsta.visibility = View.GONE
        }


        var userDto : UserDTO? = UtilMethod.instance.getUser(requireContext())

        userID = userDto?.user_id
        share_link = "hey install the app" + " " + Uri.parse("https://wellme.app.link/q192RZ6aLZ?category_id=" + userID)

    }

    fun shareDeepLink(packageName:String){
        val whatsappIntent = Intent(Intent.ACTION_SEND)
        if (packageName!=""){
            whatsappIntent.`package` = packageName
        }
        whatsappIntent.type = "text/plain"
//            whatsappIntent.putExtra(Intent.EXTRA_TEXT, context.getResources().getString(R.string.share_text) + " " + Uri.parse("https://play.google.com/store/apps/details?id=" + context.getPackageName()));
        //            whatsappIntent.putExtra(Intent.EXTRA_TEXT, context.getResources().getString(R.string.share_text) + " " + Uri.parse("https://play.google.com/store/apps/details?id=" + context.getPackageName()));
        whatsappIntent.putExtra(
            Intent.EXTRA_TEXT,
            share_link
        )
        whatsappIntent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        try {
            startActivity(whatsappIntent)
        } catch (ex: ActivityNotFoundException) {
            //ToastHelper.MakeShortText("Whatsapp have not been installed.");
        }
    }
}