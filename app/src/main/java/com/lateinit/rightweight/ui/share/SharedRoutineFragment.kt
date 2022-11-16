package com.lateinit.rightweight.ui.share

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.lateinit.rightweight.R
import com.lateinit.rightweight.ui.home.HomeActivity

class SharedRoutineFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (requireActivity() as HomeActivity).supportActionBar?.setTitle(R.string.share)

        return inflater.inflate(R.layout.fragment_shared_routine, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewBtn = view.findViewById<Button>(R.id.view)

        viewBtn.setOnClickListener {
            it.findNavController()
                .navigate(R.id.action_navigation_shared_routine_to_navigation_shared_routine_detail)
        }
    }
}