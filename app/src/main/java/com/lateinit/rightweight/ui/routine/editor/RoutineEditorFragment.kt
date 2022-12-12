package com.lateinit.rightweight.ui.routine.editor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.activity.OnBackPressedCallback
import androidx.annotation.StringRes
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.lateinit.rightweight.R
import com.lateinit.rightweight.databinding.FragmentRoutineEditorBinding
import com.lateinit.rightweight.ui.dialog.CommonDialogFragment
import com.lateinit.rightweight.ui.dialog.CommonDialogFragment.Companion.EDITOR_BACK_PRESSED_DIALOG_TAG
import com.lateinit.rightweight.ui.model.routine.ExercisePartTypeUiModel
import com.lateinit.rightweight.util.CenterSmoothScroller
import com.lateinit.rightweight.ui.routine.editor.RoutineEditorViewModel.RoutineSaveState
import com.lateinit.rightweight.util.collectOnLifecycle
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RoutineEditorFragment : Fragment() {

    private var _binding: FragmentRoutineEditorBinding? = null
    private val binding
        get() = checkNotNull(_binding) { "binding was accessed outside of view lifecycle" }

    private val viewModel: RoutineEditorViewModel by viewModels()

    private lateinit var routineDayAdapter: RoutineDayAdapter
    private lateinit var exerciseAdapter: RoutineExerciseAdapter

    private val backPressedDialog = CommonDialogFragment { findNavController().navigateUp() }
    private val backPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            backPressedDialog.show(
                parentFragmentManager,
                EDITOR_BACK_PRESSED_DIALOG_TAG, R.string.editor_back_pressed_message
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentRoutineEditorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setBinding()
        setRoutineDayAdapter()
        setRoutineDaysObserve()
        setExerciseAdapter()
        setDayExercisesObserve()
        setBackStackEvent()
        setRoutineSaveButtonEvent()
        setAddDayButtonClickListener()
    }

    private fun setBinding() {
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
    }

    private fun setRoutineDayAdapter() {
        routineDayAdapter = RoutineDayAdapter { position ->
            viewModel.clickDay(position)
            val centerSmoothScroller =
                CenterSmoothScroller(binding.recyclerViewDay.context)
            centerSmoothScroller.targetPosition = position
            binding.recyclerViewDay.layoutManager?.startSmoothScroll(centerSmoothScroller)
        }
        binding.recyclerViewDay.adapter = routineDayAdapter
        binding.recyclerViewDay.itemAnimator = null
    }

    private fun setRoutineDaysObserve() {
        viewModel.days.observe(viewLifecycleOwner) {
            routineDayAdapter.submitList(it)
        }
    }

    private fun setExerciseAdapter() {
        val exerciseParts = ExercisePartTypeUiModel.values().map { exercisePart ->
            getString(exercisePart.partName)
        }
        val exercisePartAdapter =
            ArrayAdapter(requireContext(), R.layout.item_exercise_part, exerciseParts)
        val exerciseEventListener = object : RoutineExerciseAdapter.ExerciseEventListener {

            override fun onExerciseRemove(dayId: String, position: Int) {
                viewModel.removeExercise(dayId, position)
            }

            override fun onExercisePartChange(
                dayId: String,
                position: Int,
                exercisePartType: ExercisePartTypeUiModel,
            ) {
                viewModel.changeExercisePart(dayId, position, exercisePartType)
            }

            override fun onSetAdd(exerciseId: String) {
                viewModel.addExerciseSet(exerciseId)
            }

            override fun onSetRemove(exerciseId: String, position: Int) {
                viewModel.removeExerciseSet(exerciseId, position)
            }

        }

        exerciseAdapter = RoutineExerciseAdapter(exercisePartAdapter, exerciseEventListener)
        binding.recyclerViewExercise.adapter = exerciseAdapter
        binding.recyclerViewExercise.itemAnimator = null
    }

    private fun setDayExercisesObserve() {
        viewModel.dayExercises.observe(viewLifecycleOwner) {
            exerciseAdapter.submitList(it)
        }
    }

    private fun setAddDayButtonClickListener() {
        binding.buttonAddDay.setOnClickListener {
            viewModel.addDay()
            if (routineDayAdapter.itemCount != 0) {
                binding.recyclerViewDay.smoothScrollToPosition(routineDayAdapter.itemCount)
            }
        }
    }

    private fun setBackStackEvent() {
        requireActivity().onBackPressedDispatcher.addCallback(backPressedCallback)
    }

    private fun setRoutineSaveButtonEvent() {
        viewLifecycleOwner.collectOnLifecycle {
            viewModel.isPossibleSaveRoutine.collect {
                when (it) {
                    RoutineSaveState.SUCCESS -> {
                        setFragmentResult("management", bundleOf("save" to true))
                        findNavController().navigateUp()
                    }
                    RoutineSaveState.ROUTINE_TITLE_EMPTY -> {
                        showSnackBar(R.string.routine_title_empty)
                    }
                    RoutineSaveState.ROUTINE_DESCRIPTION_EMPTY -> {
                        showSnackBar(R.string.routine_description_empty)
                    }
                    RoutineSaveState.EXERCISE_TITLE_EMPTY -> {
                        showSnackBar(R.string.exercise_title_empty)
                    }
                    RoutineSaveState.DAY_EMPTY -> {
                        showSnackBar(R.string.day_empty)
                    }
                    RoutineSaveState.EXERCISE_EMPTY -> {
                        showSnackBar(R.string.exercise_empty)
                    }
                    RoutineSaveState.EXERCISE_SET_EMPTY -> {
                        showSnackBar(R.string.exercise_set_empty)
                    }
                    RoutineSaveState.EXCEED_MAX_DAY_SIZE -> {
                        showSnackBar(R.string.exceed_max_day_size)
                    }
                    RoutineSaveState.EXCEED_MAX_EXERCISE_SIZE -> {
                        showSnackBar(R.string.exceed_max_exercise_size)
                    }
                    RoutineSaveState.EXCEED_MAX_EXERCISE_SET_SIZE -> {
                        showSnackBar(R.string.exceed_max_exercise_set_size)
                    }
                }
            }
        }
    }

    private fun showSnackBar(@StringRes messageResId: Int) {
        Snackbar.make(binding.root, messageResId, Snackbar.LENGTH_SHORT).apply {
            anchorView = binding.buttonSave
        }.show()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    override fun onDetach() {
        super.onDetach()
        backPressedCallback.remove()
    }
}
