package com.lateinit.rightweight.ui.routine.management

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.lateinit.rightweight.data.database.entity.Routine
import com.lateinit.rightweight.databinding.FragmentRoutineManagementBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RoutineManagementFragment : Fragment() {

    private var _binding: FragmentRoutineManagementBinding? = null
    private val binding
        get() = checkNotNull(_binding) { "binding was accessed outside of view lifecycle" }
    private val routineManagementViewModel: RoutineManagementViewModel by viewModels()

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
    }

    override fun onResume() {
        routineManagementViewModel.getRoutineList()
        routineManagementViewModel.loadSelectedRoutine()
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
        updateRoutines()
    }

    private fun updateRoutines() {
        val routineManagementAdapter =
            binding.recyclerViewRoutineManagement.adapter as RoutineManagementAdapter
        val currentList = routineManagementAdapter.currentList.toMutableList()
        routineManagementViewModel.selectedRoutine.value?.let {
            currentList.add(0, it)
        }
        val list = currentList.mapIndexed { index, routine ->
            routine.copy(order = index)
        }
        routineManagementViewModel.updateRoutines(list)
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun setBinding() {
        binding.lifecycleOwner = viewLifecycleOwner
        binding.routineManagementViewModel = routineManagementViewModel
        binding.fragment = this
    }

    fun navigateTo(id: Int) {
        findNavController().navigate(id)
    }

    fun navigateTo(routine: Routine) {
        val action =
            RoutineManagementFragmentDirections.actionNavigationRoutineManagementToNavigationRoutineDetail(
                routine.routineId
            )
        findNavController().navigate(action)
    }
}