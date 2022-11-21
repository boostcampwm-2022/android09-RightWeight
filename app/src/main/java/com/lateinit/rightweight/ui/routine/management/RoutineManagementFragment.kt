package com.lateinit.rightweight.ui.routine.management

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.lateinit.rightweight.R
import com.lateinit.rightweight.ui.home.HomeActivity

class RoutineManagementFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_routine_management, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val createBtn = view.findViewById<Button>(R.id.create)
        val viewBtn = view.findViewById<Button>(R.id.view)

        createBtn.setOnClickListener {
            it.findNavController()
                .navigate(R.id.action_navigation_routine_management_to_navigation_routine_editor)
        }
        viewBtn.setOnClickListener {
            it.findNavController()
                .navigate(R.id.action_navigation_routine_management_to_navigation_routine_detail)
        }
    }
}