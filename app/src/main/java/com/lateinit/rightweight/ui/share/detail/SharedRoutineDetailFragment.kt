package com.lateinit.rightweight.ui.share.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.*
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.lateinit.rightweight.R
import com.lateinit.rightweight.ui.home.HomeActivity

class SharedRoutineDetailFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_shared_routine_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val copyBtn = view.findViewById<Button>(R.id.copy)

        copyBtn.setOnClickListener {
            it.findNavController()
                .navigate(R.id.action_navigation_shared_routine_detail_to_navigation_routine_management)
        }
    }
}