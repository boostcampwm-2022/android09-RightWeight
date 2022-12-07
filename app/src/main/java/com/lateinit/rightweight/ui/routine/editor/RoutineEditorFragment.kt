package com.lateinit.rightweight.ui.routine.editor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.lateinit.rightweight.R
import com.lateinit.rightweight.data.ExercisePartType
import com.lateinit.rightweight.databinding.FragmentRoutineEditorBinding
import com.lateinit.rightweight.util.collectOnLifecycle
import com.lateinit.rightweight.util.getPartNameRes
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RoutineEditorFragment : Fragment() {

    private var _binding: FragmentRoutineEditorBinding? = null
    private val binding
        get() = checkNotNull(_binding) { "binding was accessed outside of view lifecycle" }

    private val viewModel: RoutineEditorViewModel by viewModels()

    private lateinit var routineDayAdapter: RoutineDayAdapter
    private lateinit var exerciseAdapter: RoutineExerciseAdapter

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

    private fun setExerciseAdapter(){
        val exerciseParts = ExercisePartType.values().map { exercisePart ->
            getString(exercisePart.getPartNameRes())
        }
        val exercisePartAdapter =
            ArrayAdapter(requireContext(), R.layout.item_exercise_part, exerciseParts)
        val exerciseEventListener = object : RoutineExerciseAdapter.ExerciseEventListener{

            override fun onExerciseRemove(dayId: String, position: Int) {
                viewModel.removeExercise(dayId, position)
            }

            override fun onExercisePartChange(
                dayId: String,
                position: Int,
                exercisePartType: ExercisePartType,
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
    }

    private fun setDayExercisesObserve() {
        viewModel.dayExercises.observe(viewLifecycleOwner) {
            exerciseAdapter.submitList(it)
        }
    }

    private fun setRoutineSaveButtonEvent() {
        collectOnLifecycle {
            viewModel.isPossibleSaveRoutine.collect {
                if (it) {
                    Snackbar.make(binding.root, R.string.success_save_routine, Snackbar.LENGTH_SHORT).apply {
                        setAction(R.string.submit) {
                            this.dismiss()
                        }
                    }.show()
                    findNavController().navigateUp()
                } else {
                    Snackbar.make(binding.root, R.string.fail_save_routine, Snackbar.LENGTH_SHORT).apply {
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
