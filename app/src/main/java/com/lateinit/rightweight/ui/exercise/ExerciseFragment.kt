package com.lateinit.rightweight.ui.exercise

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavDeepLinkBuilder
import androidx.navigation.fragment.findNavController
import com.lateinit.rightweight.R
import com.lateinit.rightweight.data.database.entity.HistoryExercise
import com.lateinit.rightweight.data.database.entity.HistorySet
import com.lateinit.rightweight.databinding.FragmentExerciseBinding
import com.lateinit.rightweight.service.TimerService
import com.lateinit.rightweight.service.TimerService.Companion.MANAGE_ACTION_NAME
import com.lateinit.rightweight.service.TimerService.Companion.PAUSE
import com.lateinit.rightweight.service.TimerService.Companion.PENDING_INTENT_TAG
import com.lateinit.rightweight.service.TimerService.Companion.START
import com.lateinit.rightweight.service.TimerService.Companion.STATUS
import com.lateinit.rightweight.service.TimerService.Companion.STATUS_ACTION_NAME
import com.lateinit.rightweight.service.TimerService.Companion.STOP
import com.lateinit.rightweight.service.TimerService.Companion.TIME_COUNT_INTENT_EXTRA
import com.lateinit.rightweight.service.convertTimeStamp
import com.lateinit.rightweight.ui.dialog.CommonDialogFragment
import com.lateinit.rightweight.ui.dialog.CommonDialogFragment.Companion.END_EXERCISE_DIALOG_TAG
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ExerciseFragment : Fragment(), HistoryEventListener {

    private val exerciseViewModel: ExerciseViewModel by viewModels()
    private lateinit var timerServiceIntent: Intent

    lateinit var binding: FragmentExerciseBinding
    private val dialog: CommonDialogFragment by lazy {
        CommonDialogFragment { tag ->
            when (tag) {
                END_EXERCISE_DIALOG_TAG -> {
                    endExercise()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startServiceWithDeeplink()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentExerciseBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        renewTodayHistory()

        binding.buttonExerciseStartAndPause.setOnClickListener {
            if (binding.isTimerRunning) {
                startTimerServiceWithMode(PAUSE)
            } else {
                startTimerServiceWithMode(START)
            }
        }
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
    }

    private fun setBroadcastReceiver() {
        val intentFilter = IntentFilter().apply {
            addAction(STATUS_ACTION_NAME)
        }

        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                intent?.let {
                    val isTimerRunning =
                        intent.getBooleanExtra(TimerService.IS_TIMER_RUNNING_INTENT_EXTRA, false)
                    val timeCount = intent.getIntExtra(TIME_COUNT_INTENT_EXTRA, 0)

                    binding.timeString = convertTimeStamp(timeCount)
                    binding.isTimerRunning = isTimerRunning
                }
            }
        }

        requireActivity().registerReceiver(receiver, intentFilter)
    }

    override fun updateHistorySet(historySet: HistorySet) {
        exerciseViewModel.updateHistorySet(historySet)
    }

    override fun updateHistoryExercise(historyExercise: HistoryExercise) {
        exerciseViewModel.updateHistoryExercise(historyExercise)
    }

    override fun removeHistorySet(historySetId: String) {
        exerciseViewModel.removeHistorySet(historySetId)
    }

    override fun removeHistoryExercise(historyExerciseId: String) {
        exerciseViewModel.removeHistoryExercise(historyExerciseId)
    }

    override fun renewTodayHistory() {
        val historyExerciseAdapter = HistoryExerciseAdapter(requireContext(), this)
        binding.recyclerViewHistory.adapter = historyExerciseAdapter

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                exerciseViewModel.loadTodayHistory().collect { todayHistories ->
                    if (todayHistories.size == 1) {
                        val historyId = todayHistories[0].historyId
                        binding.buttonExerciseAdd.setOnClickListener {
                            exerciseViewModel.addHistoryExercise(historyId)
                            renewTodayHistory()
                        }
                        binding.buttonExerciseEnd.setOnClickListener {
                            verifyAllHistorySets(historyExerciseAdapter.currentList)
                        }
                        exerciseViewModel.loadHistoryExercises(historyId)
                            .collect { historyExercises ->
                                historyExerciseAdapter.submitList(historyExercises)
                            }
                    }
                }
            }
        }

    }

    override fun applyHistorySets(historyExerciseId: String, adapter: HistorySetAdapter) {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                exerciseViewModel.loadHistorySets(historyExerciseId).collect { historySets ->
                    adapter.submitList(historySets)
                }
            }
        }
    }

    override fun addHistorySet(historyExerciseId: String) {
        exerciseViewModel.addHistorySet(historyExerciseId)
    }

    private fun verifyAllHistorySets(historyExercises: List<HistoryExercise>) {
        exerciseViewModel.verifyAllHistorySets(historyExercises)
        exerciseViewModel.isAllHistorySetsChecked.observe(viewLifecycleOwner) { isAllHistorySetsChecked ->
            if (isAllHistorySetsChecked.not()) {
                if (!dialog.isAdded) {
                    dialog.show(
                        parentFragmentManager,
                        END_EXERCISE_DIALOG_TAG, R.string.end_exercise_message
                    )
                }
            } else {
                endExercise()
            }
        }
    }

    private fun endExercise() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                exerciseViewModel.loadTodayHistory().collect { todayHistories ->
                    if (todayHistories.size == 1) {
                        val newHistory = todayHistories[0].copy()
                        newHistory.time = binding.timeString.toString()
                        newHistory.completed = true
                        exerciseViewModel.updateHistory(newHistory)
                        startTimerServiceWithMode(STOP)
                        findNavController().navigateUp()
                    }
                }
            }
        }
    }

}