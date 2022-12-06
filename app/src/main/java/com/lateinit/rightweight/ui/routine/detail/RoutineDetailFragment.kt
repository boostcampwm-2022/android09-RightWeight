package com.lateinit.rightweight.ui.routine.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.lateinit.rightweight.R
import com.lateinit.rightweight.databinding.FragmentRoutineDetailBinding
import com.lateinit.rightweight.ui.dialog.CommonDialogFragment
import com.lateinit.rightweight.ui.dialog.CommonDialogFragment.Companion.ROUTINE_REMOVE_DIALOG_TAG
import com.lateinit.rightweight.ui.routine.editor.RoutineDayAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RoutineDetailFragment : Fragment(){

    private var _binding: FragmentRoutineDetailBinding? = null
    private val binding
        get() = checkNotNull(_binding) { "binding was accessed outside of view lifecycle" }

    private val args: RoutineDetailFragmentArgs by navArgs()

    private val viewModel: RoutineDetailViewModel by viewModels()

    private lateinit var routineDayAdapter: RoutineDayAdapter
    private lateinit var exerciseAdapter: DetailExerciseAdapter
    private val dialog: CommonDialogFragment by lazy {
        CommonDialogFragment{
            when (dialog.tag) {
                ROUTINE_REMOVE_DIALOG_TAG -> {
                    viewModel.removeRoutine(args.routineId)
                    viewModel.deselectRoutine()
                    findNavController().navigateUp()
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
        viewModel.getRoutine(args.routineId)
        setMenu()
        setBinding()
        setRoutineDayAdapter()
        setDayUiModelsObserve()
        setExerciseAdapter()
        setCurrentDayPositionObserve()
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
                                args.routineId
                            )
                        findNavController().navigate(action)
                        return true
                    }
                    R.id.action_item_remove -> {
                        removeRoutine(args.routineId)
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
            RoutineDayAdapter { position -> viewModel.clickDay(position) }
        binding.recyclerViewDay.adapter = routineDayAdapter
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
        binding.recyclerViewExercise.adapter = exerciseAdapter
    }

    private fun setCurrentDayPositionObserve() {
        viewModel.currentDayPosition.observe(viewLifecycleOwner) {
            val exercises =
                viewModel.dayUiModels.value?.get(it)?.exercises ?: return@observe
            exerciseAdapter.submitList(exercises)
        }
    }
    
    private fun removeRoutine(routineId: String) {
        val selectedRoutineId = viewModel.userInfo.value?.routineId
        if (selectedRoutineId != routineId) {
            viewModel.removeRoutine(routineId)
            findNavController().navigateUp()
        } else {
            dialog.show(parentFragmentManager, ROUTINE_REMOVE_DIALOG_TAG, R.string.routine_remove_message)
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}