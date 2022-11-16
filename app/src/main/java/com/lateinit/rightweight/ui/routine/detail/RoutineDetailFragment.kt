package com.lateinit.rightweight.ui.routine.detail

import android.os.Bundle
import android.view.*
import android.widget.Button
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.navigation.findNavController
import com.lateinit.rightweight.R
import com.lateinit.rightweight.ui.home.HomeActivity

class RoutineDetailFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (requireActivity() as HomeActivity).supportActionBar?.setTitle(R.string.detail)
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_routine_detail, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

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
}