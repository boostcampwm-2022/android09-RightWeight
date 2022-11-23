package com.lateinit.rightweight.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.lateinit.rightweight.R
import com.lateinit.rightweight.service.TimerService

class HomeFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val navigationRouteId = requireActivity().intent.getIntExtra(TimerService.SCREEN_MOVE_INTENT_EXTRA, -1)
        Log.d("navigationRouteId", navigationRouteId.toString())
        if(navigationRouteId != -1){
            findNavController().navigate(navigationRouteId)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btn = view.findViewById<Button>(R.id.startEx)
        btn.setOnClickListener {
            it.findNavController().navigate(R.id.action_navigation_home_to_navigation_exercise)
        }

    }

    override fun onStop() {
        super.onStop()
        Log.d("onStop", "")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("onDestroyView", "")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("onDestroy", "")
    }

}