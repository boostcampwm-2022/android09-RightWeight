package com.lateinit.rightweight.ui.share.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.lateinit.rightweight.databinding.FragmentSharedRoutineDetailBinding
import com.lateinit.rightweight.ui.model.DayUiModel
import com.lateinit.rightweight.ui.routine.detail.DetailExerciseAdapter
import com.lateinit.rightweight.ui.routine.editor.RoutineDayAdapter
import com.lateinit.rightweight.util.collectOnLifecycle
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SharedRoutineDetailFragment : Fragment() {

    private var _binding: FragmentSharedRoutineDetailBinding? = null
    private val binding
        get() = checkNotNull(_binding) { "binding was accessed outside of view lifecycle" }

    val sharedRoutineDetailViewModel: SharedRoutineDetailViewModel by viewModels()

    private lateinit var routineDayAdapter: RoutineDayAdapter
    private lateinit var exerciseAdapter: DetailExerciseAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSharedRoutineDetailBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val routineId = arguments?.getString("routineId")
        routineId?.let {
            sharedRoutineDetailViewModel.getSharedRoutineDetail(routineId)
        }

        setRoutineDayAdapter()
        setExerciseAdapter()
        setSharedRoutineDetailCollect()

    }

    private fun setRoutineDayAdapter() {
        routineDayAdapter =
            RoutineDayAdapter { position ->
                sharedRoutineDetailViewModel.clickDay(position)
            }
        binding.recyclerViewDay.adapter = routineDayAdapter
    }

    private fun setExerciseAdapter() {
        exerciseAdapter = DetailExerciseAdapter { position ->
            sharedRoutineDetailViewModel.clickExercise(position)
        }
        binding.recyclerViewExercise.adapter = exerciseAdapter
    }

    private fun setSharedRoutineDetailCollect() {
        collectOnLifecycle {
            sharedRoutineDetailViewModel.uiState.collect() { uiState ->
                when (uiState) {
                    is LatestSharedRoutineDetailUiState.Success -> {
                        binding.sharedRoutineUiModel = uiState.sharedRoutineUiModel
                        routineDayAdapter.submitList(uiState.dayUiModels)
                        setCurrentDayPositionObserve(uiState.dayUiModels)

                        binding.buttonRoutineImport.setOnClickListener() {
                            sharedRoutineDetailViewModel.importSharedRoutineToMyRoutines(
                                uiState.sharedRoutineUiModel,
                                uiState.dayUiModels
                            )

                        }
                    }
                    is LatestSharedRoutineDetailUiState.Error -> Exception()
                }
            }
        }
    }

    private fun setCurrentDayPositionObserve(dayUiModels: List<DayUiModel>) {
        sharedRoutineDetailViewModel.currentDayPosition.observe(viewLifecycleOwner) {
            if (dayUiModels.size > it) {
                val exercises = dayUiModels.get(it).exercises.sortedBy { it.order }
                exerciseAdapter.submitList(exercises)
            }
        }
    }


}