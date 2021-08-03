package com.wellme.fragment

import android.app.Activity
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.wellme.LeftSideMenuActivity
import com.wellme.R
import com.wellme.databinding.FragmentFilterBinding

class FilterFragment : Fragment(), View.OnClickListener {
    var binding : FragmentFilterBinding? = null
    var activity : Activity? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_filter, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        this.activity = activity
    }

    override fun onResume() {
        super.onResume()
        (activity as LeftSideMenuActivity).disableBottomBar()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (activity as LeftSideMenuActivity).enableBottomBar()
    }

    fun initView(){
        var regular : Typeface? = ResourcesCompat.getFont(requireContext(), R.font.poppins_regular)
        binding?.cbTime1?.setTypeface(regular)
        binding?.cbTime2?.setTypeface(regular)
        binding?.cbTime3?.setTypeface(regular)
        binding?.cbTime4?.setTypeface(regular)
        binding?.rbAll?.setTypeface(regular)
        binding?.rbLifestyle?.setTypeface(regular)
        binding?.rbMedical?.setTypeface(regular)
        binding?.rbPrePostNatal?.setTypeface(regular)
        binding?.rbSportsNutrition?.setTypeface(regular)
        binding?.rbTherapeutic?.setTypeface(regular)
        binding?.tvSpeciality?.setTypeface(regular)
        binding?.tvWorkingHours?.setTypeface(regular)
        binding?.header?.setTypeface(regular)
        binding?.btnApply?.setTypeface(regular)
        binding?.btnCancel?.setTypeface(regular)
        binding?.back?.setOnClickListener(this)
        binding?.btnApply?.setOnClickListener(this)
        binding?.btnCancel?.setOnClickListener(this)
    }


    override fun onClick(v: View?) {
        if(v == binding?.btnApply){
            activity?.onBackPressed()
        }
        else if(v == binding?.back){
            activity?.onBackPressed()
        }
        else if(v == binding?.btnCancel){
            activity!!.onBackPressed()
        }
    }
}