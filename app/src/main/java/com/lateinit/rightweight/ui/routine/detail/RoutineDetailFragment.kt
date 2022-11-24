package com.lateinit.rightweight.ui.routine.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.*
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.lateinit.rightweight.R
import com.lateinit.rightweight.databinding.FragmentRoutineDetailBinding
import com.lateinit.rightweight.ui.home.UserViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RoutineDetailFragment : Fragment() {
    private var _binding: FragmentRoutineDetailBinding? = null
    private val binding
        get() = checkNotNull(_binding) { "binding was accessed outside of view lifecycle" }
    private val viewModel: UserViewModel by activityViewModels()
    private val args: RoutineDetailFragmentArgs by navArgs()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        _binding = FragmentRoutineDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.userViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        val modiBtn = view.findViewById<Button>(R.id.modi)

        modiBtn.setOnClickListener {
            it.findNavController()
                .navigate(R.id.action_navigation_routine_detail_to_navigation_routine_editor)
        }

        val routineId = args.routineId
        binding.select.setText(routineId)
        binding.select.setOnClickListener {
            viewModel.setUser(routineId)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_routine_detail, menu)
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}