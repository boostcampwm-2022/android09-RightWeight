package com.lateinit.rightweight.ui.share.detail

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.lateinit.rightweight.R
import com.lateinit.rightweight.databinding.FragmentSharedRoutineDetailBinding
import com.lateinit.rightweight.ui.dialog.LoadingDialogProvider
import com.lateinit.rightweight.ui.model.LoadingState
import com.lateinit.rightweight.ui.model.routine.DayUiModel
import com.lateinit.rightweight.ui.routine.detail.DetailExerciseAdapter
import com.lateinit.rightweight.ui.routine.editor.RoutineDayAdapter
import com.lateinit.rightweight.util.collectOnLifecycle
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SharedRoutineDetailFragment : Fragment() {

    private var _binding: FragmentSharedRoutineDetailBinding? = null
    private val binding
        get() = checkNotNull(_binding) { "binding was accessed outside of view lifecycle" }

    private val viewModel: SharedRoutineDetailViewModel by viewModels()

    private val loadingDialog: Dialog by lazy {
        LoadingDialogProvider().provideLoadingDialog(requireContext(), R.layout.dialog_loading)
    }

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
            viewModel.getSharedRoutineDetail(routineId)
        }

        setRoutineDayAdapter()
        setExerciseAdapter()
        setSharedRoutineDetailCollect()
        setButtonRoutineImportOnClickListener()
        setCollectLoadingState()
        handleNavigationEvent()

    }

    private fun setRoutineDayAdapter() {
        routineDayAdapter =
            RoutineDayAdapter { position ->
                viewModel.clickDay(position)
            }
        binding.recyclerViewDay.apply {
            adapter = routineDayAdapter
            itemAnimator = null
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

    private fun setSharedRoutineDetailCollect() {
        viewLifecycleOwner.collectOnLifecycle {
            viewModel.uiState.collect { uiState ->
                when (uiState) {
                    is LatestSharedRoutineDetailUiState.Success -> {
                        binding.sharedRoutineUiModel = uiState.sharedRoutineUiModel
                        routineDayAdapter.submitList(uiState.dayUiModels)
                        setCurrentDayPositionObserve(uiState.dayUiModels)
                    }
                    is LatestSharedRoutineDetailUiState.Error -> {
                        Snackbar.make(
                            binding.root,
                            R.string.wrong_connection,
                            Snackbar.LENGTH_LONG
                        ).apply {
                            anchorView = binding.guideLineBottom
                        }.show()
                        findNavController().navigateUp()
                    }
                }
            }
        }
    }

    private fun setButtonRoutineImportOnClickListener() {
        binding.buttonRoutineImport.setOnClickListener {
            if (viewModel.importSharedRoutineToMyRoutines().not()) {
                Snackbar.make(
                    binding.root,
                    R.string.none_routine,
                    Snackbar.LENGTH_LONG
                ).apply {
                    anchorView = binding.guideLineBottom
                }.show()
            }
        }
    }

    private fun setCurrentDayPositionObserve(dayUiModels: List<DayUiModel>) {
        viewModel.currentDayPosition.observe(viewLifecycleOwner) {
            if (dayUiModels.size > it) {
                val exercises = dayUiModels[it].exercises
                exerciseAdapter.submitList(exercises)
            }
        }
    }

    private fun handleNavigationEvent() {
        viewLifecycleOwner.collectOnLifecycle {
            viewModel.navigationEvent.collect {
                setFragmentResult("routineCopy", bundleOf())
                findNavController().navigateUp()
            }
        }
    }

    private fun setCollectLoadingState() {
        viewLifecycleOwner.collectOnLifecycle {
            val textViewLoading = loadingDialog.findViewById<TextView>(R.id.text_vitw_loading)
            viewModel.loadingState.collect { state ->
                Log.d("loadingState", "$state")
                when (state) {
                    LoadingState.GET -> {
                        textViewLoading.setText(R.string.get_loading_message)
                        loadingDialog.show()
                    }
                    else -> loadingDialog.cancel()
                }
            }
        }
    }
}