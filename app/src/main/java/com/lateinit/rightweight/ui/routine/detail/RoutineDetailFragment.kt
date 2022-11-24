package com.lateinit.rightweight.ui.routine.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.View
import android.view.Menu
import android.view.MenuInflater
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.lateinit.rightweight.R
import com.lateinit.rightweight.databinding.FragmentRoutineDetailBinding
import com.lateinit.rightweight.ui.home.UserViewModel
import com.lateinit.rightweight.ui.routine.editor.RoutineDayAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RoutineDetailFragment : Fragment() {
    private var _binding: FragmentRoutineDetailBinding? = null
    private val binding
        get() = checkNotNull(_binding) { "binding was accessed outside of view lifecycle" }

    private val args: RoutineDetailFragmentArgs by navArgs()

    private val userViewModel: UserViewModel by activityViewModels()
    private val routineDetailViewModel: RoutineDetailViewModel by viewModels()

    private lateinit var routineDayAdapter: RoutineDayAdapter

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
        routineDetailViewModel.getRoutine(args.routineId)
        setBinding()
        setRoutineDayAdapter()
        setDayUiModelsObserve()
        setCurrentDayPositionObserve()
    }

    private fun setBinding() {
        binding.lifecycleOwner = viewLifecycleOwner
        binding.userViewModel = userViewModel
        binding.routineDetailViewModel = routineDetailViewModel
    }

    private fun setRoutineDayAdapter() {
        routineDayAdapter = RoutineDayAdapter { position -> routineDetailViewModel.clickDay(position) }
        binding.recyclerViewDay.adapter = routineDayAdapter
    }

    private fun setDayUiModelsObserve() {
        routineDetailViewModel.dayUiModels.observe(viewLifecycleOwner) {
            routineDayAdapter.submitList(it)
        }
    }

    private fun setCurrentDayPositionObserve() {
        routineDetailViewModel.currentDayPosition.observe(viewLifecycleOwner) {
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