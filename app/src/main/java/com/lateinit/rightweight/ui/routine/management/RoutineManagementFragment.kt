package com.lateinit.rightweight.ui.routine.management

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.lateinit.rightweight.databinding.FragmentRoutineManagementBinding
import com.lateinit.rightweight.ui.home.UserViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RoutineManagementFragment : Fragment() {

    private var _binding: FragmentRoutineManagementBinding? = null
    private val binding
        get() = checkNotNull(_binding) { "binding was accessed outside of view lifecycle" }
    private val userViewModel: UserViewModel by activityViewModels()
    private val routineManagementViewModel: RoutineManagementViewModel by viewModels()
    private val routineManagementAdapter: RoutineManagementAdapter by lazy {
        RoutineManagementAdapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRoutineManagementBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setBinding()
        setAdapter()
        routineManagementViewModel.routines.observe(viewLifecycleOwner) {
            routineManagementAdapter.submitList(it)
        }
    }

    override fun onResume() {
        routineManagementViewModel.getRoutineList()
        super.onResume()
    }


    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun setBinding() {
        binding.lifecycleOwner = viewLifecycleOwner
        binding.userViewModel = userViewModel
        binding.fragment = this
    }

    private fun setAdapter() {
        binding.recyclerViewRoutineManagement.adapter = routineManagementAdapter
    }

    fun navigateTo(id: Int) {
        findNavController().navigate(id)
    }
}