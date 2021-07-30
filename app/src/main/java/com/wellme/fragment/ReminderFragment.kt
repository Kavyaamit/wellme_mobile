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
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.wellme.LeftSideMenuActivity
import com.wellme.R
import com.wellme.adapter.ReminderAdapter
import com.wellme.databinding.FragmentReminderBinding
import com.wellme.dto.ReminderDTO
import com.wellme.utils.OnItemClickListener
import com.wellme.utils.UtilMethod

class ReminderFragment : Fragment(), View.OnClickListener{

    var binding : FragmentReminderBinding? = null
    var reminder_list : List<ReminderDTO> = ArrayList()
    var reminder_old_list : ArrayList<ReminderDTO> = ArrayList()
    var s_reminder : String? = ""
    var regular : Typeface? = null
    var linearLayoutManager : LinearLayoutManager? = null
    val gson : Gson? = Gson()
    var activity : Activity? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_reminder, container, false)
        return binding!!.root
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        this.activity = activity
    }

    override fun onResume() {
        super.onResume()
        (activity as LeftSideMenuActivity).enableBottomBar()
        (activity as LeftSideMenuActivity).setActiveSection(0)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        s_reminder = UtilMethod.instance.getReminderList(requireContext())
        regular = ResourcesCompat.getFont(requireContext(), R.font.poppins_regular)
        binding?.header!!.setTypeface(regular)

        if(UtilMethod.instance.isStringNullOrNot(s_reminder)){
            reminder_old_list.add(ReminderDTO("Start Walking", false, "", "",1, 1))
            reminder_old_list.add(ReminderDTO("Start Workout", false, "", "",1, 2))
            reminder_old_list.add(ReminderDTO("Log Weight", false, "", "",1, 3))
            reminder_old_list.add(ReminderDTO("Track Meal", false, "", "",1, 4))
            reminder_old_list.add(ReminderDTO("Drink Water", false, "", "",1, 5))
            var s1 : String = gson!!.toJson(reminder_old_list)
            UtilMethod.instance.setReminderList(s1, requireContext())
        }
        else{
            reminder_list = gson!!.fromJson(s_reminder, Array<ReminderDTO>::class.java).toList()
            reminder_old_list = ArrayList()
            reminder_old_list.addAll(reminder_list)
        }

        linearLayoutManager = LinearLayoutManager(requireContext())
        linearLayoutManager!!.orientation = LinearLayoutManager.VERTICAL
        binding?.rvReminders!!.layoutManager = linearLayoutManager
        binding?.rvReminders!!.adapter = ReminderAdapter(requireContext(), reminder_old_list, onItemClickDetails)


        binding?.back?.setOnClickListener(this)
    }

    private val onItemClickDetails : OnItemClickListener.OnItemClickCallback = object : OnItemClickListener.OnItemClickCallback{
        override fun onItemClicked(view: View?, position: Int) {
            var fragment : Fragment = UpdateReminderFragment()
            var bundle : Bundle = Bundle()
            bundle.putInt("type", reminder_old_list.get(position).type)
            fragment.arguments = bundle
            var fragmentTransaction : FragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.container_home, fragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()

        }

    }


    override fun onClick(v: View?) {

        if (v==binding?.back){

            requireActivity().onBackPressed()
        }

    }

}