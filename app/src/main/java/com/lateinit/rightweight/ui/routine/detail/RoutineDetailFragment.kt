package com.lateinit.rightweight.ui.routine.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.lateinit.rightweight.R
import com.lateinit.rightweight.databinding.FragmentRoutineDetailBinding
import com.lateinit.rightweight.ui.dialog.CommonDialogFragment
import com.lateinit.rightweight.ui.dialog.CommonDialogFragment.Companion.ROUTINE_REMOVE_DIALOG_TAG
import com.lateinit.rightweight.ui.dialog.CommonDialogFragment.Companion.SELECTED_ROUTINE_REMOVE_DIALOG_TAG
import com.lateinit.rightweight.ui.login.NetworkState
import com.lateinit.rightweight.ui.routine.editor.RoutineDayAdapter
import com.lateinit.rightweight.util.CenterSmoothScroller
import com.lateinit.rightweight.util.collectOnLifecycle
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RoutineDetailFragment : Fragment() {

    private var _binding: FragmentRoutineDetailBinding? = null
    private val binding
        get() = checkNotNull(_binding) { "binding was accessed outside of view lifecycle" }

    private val viewModel: RoutineDetailViewModel by viewModels()

    private lateinit var routineDayAdapter: RoutineDayAdapter
    private lateinit var exerciseAdapter: DetailExerciseAdapter
    private val dialog: CommonDialogFragment by lazy {
        CommonDialogFragment {
            when (dialog.tag) {
                SELECTED_ROUTINE_REMOVE_DIALOG_TAG -> {
                    viewModel.deselectRoutine()
                    viewModel.removeRoutine()
                }
                ROUTINE_REMOVE_DIALOG_TAG -> {
                    viewModel.removeRoutine()
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRoutineDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setMenu()
        setBinding()
        setRoutineDayAdapter()
        setDayUiModelsObserve()
        setExerciseAdapter()
        setCurrentDayPositionObserve()
        handleNavigationEvent()
        handleNetworkResultEvent()
    }

    private fun setMenu() {
        (requireActivity() as MenuHost).addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.clear()
                menuInflater.inflate(R.menu.menu_routine_detail, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.action_item_edit -> {
                        val action =
                            RoutineDetailFragmentDirections.actionNavigationRoutineDetailToNavigationRoutineEditor(
                                routineId = viewModel.routineId
                            )
                        findNavController().navigate(action)
                        return true
                    }
                    R.id.action_item_remove -> {
                        removeRoutine()
                        return true
                    }
                    R.id.action_item_share -> {
                        viewModel.shareRoutine()
                        return true
                    }
                    else -> {
                        return false
                    }
                }
            }

        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun setBinding() {
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
    }

    private fun setRoutineDayAdapter() {
        routineDayAdapter =
            RoutineDayAdapter { position ->
                viewModel.clickDay(position)
                val centerSmoothScroller =
                    CenterSmoothScroller(binding.recyclerViewDay.context)
                centerSmoothScroller.targetPosition = position
                binding.recyclerViewDay.layoutManager?.startSmoothScroll(centerSmoothScroller)
            }
        binding.recyclerViewDay.apply {
            adapter = routineDayAdapter
            itemAnimator = null
        }
    }

    private fun setDayUiModelsObserve() {
        viewModel.dayUiModels.observe(viewLifecycleOwner) {
            routineDayAdapter.submitList(it)
        }
    }

    private fun setExerciseAdapter() {
        exerciseAdapter = DetailExerciseAdapter { position ->
            viewModel.clickExercise(position)
        }
        binding.recyclerViewExercise.apply {
            adapter = exerciseAdapter
            itemAnimator = null
        }
    }

    private fun setCurrentDayPositionObserve() {
        viewModel.currentDayPosition.observe(viewLifecycleOwner) {
            val exercises =
                viewModel.dayUiModels.value?.get(it)?.exercises ?: return@observe
            exerciseAdapter.submitList(exercises)
        }
    }

    private fun handleNavigationEvent() {
        viewLifecycleOwner.collectOnLifecycle {
            viewModel.navigationEvent.collect { event ->
                when (event) {
                    is RoutineDetailViewModel.NavigationEvent.SelectEvent -> {
                        if (event.isSelected) {
                            val routineTitle = viewModel.routineUiModel.value?.title
                            setFragmentResult("management", bundleOf("select" to routineTitle))
                            findNavController().navigateUp()
                        }
                    }
                    is RoutineDetailViewModel.NavigationEvent.RemoveEvent -> {
                        if (event.isRemoved) {
                            findNavController().navigateUp()
                        }
                    }
                }
            }
        }
    }

    private fun handleNetworkResultEvent() {
        viewLifecycleOwner.collectOnLifecycle {
            viewModel.networkState.collect { state ->
                if (state == NetworkState.SUCCESS) {
                    Snackbar.make(
                        binding.root,
                        R.string.complete_share_message,
                        Snackbar.LENGTH_LONG
                    ).apply {
                        anchorView = binding.buttonRoutineSelect
                    }.show()
                } else {
                    Snackbar.make(
                        binding.root,
                        R.string.wrong_connection,
                        Snackbar.LENGTH_LONG
                    ).apply {
                        anchorView = binding.buttonRoutineSelect
                    }.show()
                }
            }
        }
    }


    private fun removeRoutine() {
        val selectedRoutineId = viewModel.userInfo.value?.routineId
        if (selectedRoutineId != viewModel.routineId) {
            dialog.show(
                parentFragmentManager,
                ROUTINE_REMOVE_DIALOG_TAG,
                R.string.routine_remove_message
            )
        } else {
            dialog.show(
                parentFragmentManager,
                SELECTED_ROUTINE_REMOVE_DIALOG_TAG,
                R.string.selected_routine_remove_message
            )
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}