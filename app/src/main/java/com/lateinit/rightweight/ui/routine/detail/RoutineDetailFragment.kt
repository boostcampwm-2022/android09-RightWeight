package com.lateinit.rightweight.ui.routine.detail

import android.os.Bundle
import android.view.*
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.lateinit.rightweight.R

class RoutineDetailFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_routine_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val modiBtn = view.findViewById<Button>(R.id.modi)

        modiBtn.setOnClickListener {
            it.findNavController()
                .navigate(R.id.action_navigation_routine_detail_to_navigation_routine_editor)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_routine_detail, menu)
    }
}