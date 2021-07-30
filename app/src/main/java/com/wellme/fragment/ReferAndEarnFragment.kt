package com.wellme.fragment

import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.wellme.R
import com.wellme.databinding.FragmentWalletBinding

class ReferAndEarnFragment : Fragment(), View.OnClickListener{
    var binding : FragmentWalletBinding? = null
    var regular : Typeface? = null
    var bold : Typeface? = null

    override fun onClick(p0: View?) {
        if(p0 == binding!!.back){
            requireActivity().supportFragmentManager.popBackStack()
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
    }
}