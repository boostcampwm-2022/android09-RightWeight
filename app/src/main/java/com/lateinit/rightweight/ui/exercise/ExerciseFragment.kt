package com.lateinit.rightweight.ui.exercise

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavDeepLinkBuilder
import androidx.navigation.fragment.findNavController
import com.lateinit.rightweight.R
import com.lateinit.rightweight.databinding.FragmentExerciseBinding
import com.lateinit.rightweight.service.TimerService
import com.lateinit.rightweight.service.TimerService.Companion.IS_TIMER_RUNNING_INTENT_EXTRA
import com.lateinit.rightweight.service.TimerService.Companion.MANAGE_ACTION_NAME
import com.lateinit.rightweight.service.TimerService.Companion.PAUSE
import com.lateinit.rightweight.service.TimerService.Companion.PENDING_INTENT_TAG
import com.lateinit.rightweight.service.TimerService.Companion.SET_TIME_COUNT
import com.lateinit.rightweight.service.TimerService.Companion.START
import com.lateinit.rightweight.service.TimerService.Companion.STATUS
import com.lateinit.rightweight.service.TimerService.Companion.STATUS_ACTION_NAME
import com.lateinit.rightweight.service.TimerService.Companion.STOP
import com.lateinit.rightweight.service.TimerService.Companion.TIME_COUNT_INTENT_EXTRA
import com.lateinit.rightweight.ui.dialog.CommonDialogFragment
import com.lateinit.rightweight.ui.dialog.CommonDialogFragment.Companion.END_EXERCISE_DIALOG_TAG
import com.lateinit.rightweight.ui.model.history.HistoryExerciseSetUiModel
import com.lateinit.rightweight.ui.model.history.HistoryExerciseUiModel
import com.lateinit.rightweight.ui.model.routine.ExercisePartTypeUiModel
import com.lateinit.rightweight.util.collectOnLifecycle
import com.lateinit.rightweight.util.convertTimeStamp
import com.lateinit.rightweight.util.convertTimeStampToTimeCount
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ExerciseFragment : Fragment() {

    private var _binding: FragmentExerciseBinding? = null
    private val binding
        get() = checkNotNull(_binding) { "binding was accessed outside of view lifecycle" }

    private val viewModel: ExerciseViewModel by viewModels()
    private lateinit var timerServiceIntent: Intent

    private val dialog = CommonDialogFragment { endExercise() }
    private lateinit var historyExerciseAdapter: HistoryExerciseAdapter

    private var timeCount = 0
    private var isTimerRunning = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startServiceWithDeeplink()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExerciseBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHistoryExerciseAdapter()
        setButtonClickListeners()
        collectHistory()
        handleNavigationEvent()
    }

    override fun onResume() {
        super.onResume()
        startTimerServiceWithMode(STATUS)
        setBroadcastReceiver()
    }

    private fun startServiceWithDeeplink() {
        val pendingIntent = NavDeepLinkBuilder(requireActivity())
            .setGraph(R.navigation.nav_graph)
            .setDestination(R.id.navigation_exercise)
            .createPendingIntent()

        timerServiceIntent = Intent(requireContext(), TimerService::class.java)
        timerServiceIntent.putExtra(PENDING_INTENT_TAG, pendingIntent)
        requireActivity().startService(timerServiceIntent)
        timerServiceIntent.removeExtra(PENDING_INTENT_TAG)
    }

    private fun startTimerServiceWithMode(mode: String) {
        timerServiceIntent.putExtra(MANAGE_ACTION_NAME, mode)
        requireActivity().startService(timerServiceIntent)
        timerServiceIntent.removeExtra(MANAGE_ACTION_NAME)
    }

    private fun setBroadcastReceiver() {
        val intentFilter = IntentFilter().apply {
            addAction(STATUS_ACTION_NAME)
        }

        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                intent?.let {
                    isTimerRunning = intent.getBooleanExtra(IS_TIMER_RUNNING_INTENT_EXTRA, false)
                    timeCount = intent.getIntExtra(TIME_COUNT_INTENT_EXTRA, 0)
                    updateViews()
                }
            }
        }

        requireActivity().registerReceiver(receiver, intentFilter)
    }

    private fun updateViews() {
        _binding ?: return
        binding.timeString = convertTimeStamp(timeCount)
        binding.isTimerRunning = isTimerRunning
    }

    private fun setButtonClickListeners() {
        binding.buttonExerciseStartAndPause.setOnClickListener {
            if (isTimerRunning) {
                startTimerServiceWithMode(PAUSE)
            } else {
                startTimerServiceWithMode(START)
            }
        }

        binding.buttonExerciseAdd.setOnClickListener {
            viewModel.addHistoryExercise()
        }

        binding.buttonExerciseEnd.setOnClickListener {
            if (viewModel.isAllHistorySetsChecked.not() && dialog.isAdded.not()) {
                dialog.show(
                    parentFragmentManager,
                    END_EXERCISE_DIALOG_TAG, R.string.end_exercise_message
                )
            } else {
                endExercise()
            }
        }
    }

    private fun setHistoryExerciseAdapter() {
        val exerciseParts = ExercisePartTypeUiModel.values().map { exercisePart ->
            getString(exercisePart.partName)
        }
        val exercisePartAdapter =
            ArrayAdapter(requireContext(), R.layout.item_exercise_part, exerciseParts)
        val historyEventListener = object : HistoryEventListener {

            override fun addHistorySet(historyExerciseId: String) {
                viewModel.addHistorySet(historyExerciseId)
            }

            override fun updateHistorySet(historyExerciseSetUiModel: HistoryExerciseSetUiModel) {
                viewModel.updateHistorySet(historyExerciseSetUiModel)
            }

            override fun updateHistoryExercise(historyExerciseUiModel: HistoryExerciseUiModel) {
                viewModel.updateHistoryExercise(historyExerciseUiModel)
            }

            override fun removeHistorySet(historySetId: String) {
                viewModel.removeHistorySet(historySetId)
            }

            override fun removeHistoryExercise(historyExerciseId: String) {
                viewModel.removeHistoryExercise(historyExerciseId)
            }
        }

        historyExerciseAdapter = HistoryExerciseAdapter(exercisePartAdapter, historyEventListener)
        binding.recyclerViewHistory.apply {
            adapter = historyExerciseAdapter
            itemAnimator = null
        }
    }

    private fun collectHistory() {
        viewLifecycleOwner.collectOnLifecycle {
            viewModel.historyUiModel.collect {
                it ?: return@collect

                sendSavedTimeCountToService(convertTimeStampToTimeCount(it.time))
                historyExerciseAdapter.submitList(it.exercises)
            }
        }
    }

    private fun sendSavedTimeCountToService(timeCount: Int) {
        timerServiceIntent.putExtra(SET_TIME_COUNT, timeCount)
        requireActivity().startService(timerServiceIntent)
        timerServiceIntent.removeExtra(SET_TIME_COUNT)
        startTimerServiceWithMode(STATUS)
    }

    private fun handleNavigationEvent() {
        viewLifecycleOwner.collectOnLifecycle {
            viewModel.navigationEvent.collect {
                findNavController().navigateUp()
            }
        }
    }


    private fun endExercise() {
        viewModel.endExercise(binding.timeString ?: "")
        startTimerServiceWithMode(STOP)
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}