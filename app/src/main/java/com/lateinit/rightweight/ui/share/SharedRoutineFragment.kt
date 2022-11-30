package com.lateinit.rightweight.ui.share

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.navigation.findNavController
import com.lateinit.rightweight.R

class SharedRoutineFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_shared_routine, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setMenu()
        val viewBtn = view.findViewById<Button>(R.id.view)

        viewBtn.setOnClickListener {
            it.findNavController()
                .navigate(R.id.action_navigation_shared_routine_to_navigation_shared_routine_detail)
        }
    }

    private fun setMenu() {
        (requireActivity() as MenuHost).addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_shared_routine, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_item_search -> {
                        Log.d("SharedRoutineFragment", "Search")
                        true
                    }
                    else -> {
                        false
                    }
                }
            }

        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }
}