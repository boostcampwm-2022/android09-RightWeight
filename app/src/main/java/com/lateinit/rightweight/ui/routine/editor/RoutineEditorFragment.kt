package com.lateinit.rightweight.ui.routine.editor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.lateinit.rightweight.data.ExercisePartType
import com.lateinit.rightweight.databinding.FragmentRoutineEditorBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RoutineEditorFragment : Fragment() {

    private var _binding: FragmentRoutineEditorBinding? = null
    private val binding
        get() = checkNotNull(_binding) { "binding was accessed outside of view lifecycle" }

    private val viewModel: RoutineEditorViewModel by viewModels()

    private lateinit var routineDayAdapter: RoutineDayAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentRoutineEditorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setRoutineDayAdapter()
        setRoutineDaysObserve()
    }

    private fun setRoutineDayAdapter() {
        routineDayAdapter = RoutineDayAdapter(object : RoutineDayAdapter.RoutineEventListener {
            override fun onDayAdd() {
                viewModel.addDay()
            }

            override fun onDayRemove(position: Int) {
                viewModel.removeDay(position)
            }

            override fun onDayMoveUp(position: Int) {
                viewModel.moveUpDay(position)
            }

            override fun onDayMoveDown(position: Int) {
                viewModel.moveDownDay(position)
            }

            override fun onExerciseAdd(position: Int) {
                viewModel.addExercise(position)
            }

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

        })
        binding.recyclerViewDay.adapter = routineDayAdapter
    }

    private fun setRoutineDaysObserve() {
        viewModel.days.observe(viewLifecycleOwner) {
            routineDayAdapter.submitList(it)
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
