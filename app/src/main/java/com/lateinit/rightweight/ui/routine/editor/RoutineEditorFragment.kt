package com.lateinit.rightweight.ui.routine.editor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.activity.OnBackPressedCallback
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
import com.lateinit.rightweight.util.collectOnLifecycle
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RoutineEditorFragment : Fragment(){

    private var _binding: FragmentRoutineEditorBinding? = null
    private val binding
        get() = checkNotNull(_binding) { "binding was accessed outside of view lifecycle" }

    private val viewModel: RoutineEditorViewModel by viewModels()

    private lateinit var routineDayAdapter: RoutineDayAdapter
    private lateinit var exerciseAdapter: RoutineExerciseAdapter

    private val backPressedDialog = CommonDialogFragment { findNavController().navigateUp() }

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
    }

    private fun setBinding() {
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
    }

    private fun setRoutineDayAdapter() {
        routineDayAdapter = RoutineDayAdapter { position -> viewModel.clickDay(position) }
        binding.recyclerViewDay.adapter = routineDayAdapter
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
        binding.recyclerViewExercise.setItemAnimator(null)
    }

    private fun setDayExercisesObserve() {
        viewModel.dayExercises.observe(viewLifecycleOwner) {
            exerciseAdapter.submitList(it)
        }
    }

    private fun setBackStackEvent() {
        requireActivity().onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                backPressedDialog.show(
                    parentFragmentManager,
                    EDITOR_BACK_PRESSED_DIALOG_TAG, R.string.editor_back_pressed_message
                )
            }
        })
    }

    private fun setRoutineSaveButtonEvent() {
        collectOnLifecycle {
            viewModel.isPossibleSaveRoutine.collect {
                if (it) {
                    setFragmentResult("management", bundleOf("save" to true))
                    findNavController().navigateUp()
                } else {
                    Snackbar.make(binding.root, R.string.fail_save_routine, Snackbar.LENGTH_SHORT)
                        .apply {
                            anchorView = binding.buttonSave
                        }.show()
                }
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
