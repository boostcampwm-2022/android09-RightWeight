package com.lateinit.rightweight.ui.exercise

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.lateinit.rightweight.R
import com.lateinit.rightweight.data.database.entity.HistoryExercise
import com.lateinit.rightweight.data.database.entity.HistorySet
import com.lateinit.rightweight.databinding.FragmentExerciseBinding
import com.lateinit.rightweight.service.TimeCount
import com.lateinit.rightweight.service.TimerService
import com.lateinit.rightweight.service.TimerService.Companion.MANAGE_ACTION_NAME
import com.lateinit.rightweight.service.TimerService.Companion.MOMENT_ACTION_NAME
import com.lateinit.rightweight.service.TimerService.Companion.PAUSE
import com.lateinit.rightweight.service.TimerService.Companion.START
import com.lateinit.rightweight.service.TimerService.Companion.START_NOTIFICATION
import com.lateinit.rightweight.service.TimerService.Companion.STATUS
import com.lateinit.rightweight.service.TimerService.Companion.STATUS_ACTION_NAME
import com.lateinit.rightweight.service.TimerService.Companion.STOP
import com.lateinit.rightweight.service.TimerService.Companion.STOP_NOTIFICATION
import com.lateinit.rightweight.service.TimerService.Companion.TIME_COUNT_INTENT_EXTRA
import com.lateinit.rightweight.ui.dialog.CommonDialogFragment
import com.lateinit.rightweight.ui.dialog.CommonDialogFragment.Companion.END_EXERCISE_DIALOG_TAG
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ExerciseFragment : Fragment(), HistoryEventListener {

    private val exerciseViewModel: ExerciseViewModel by viewModels()
    private lateinit var timerServiceIntent: Intent
    private var isCompleted = false

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

        // 1. Intent 생성
        timerServiceIntent = Intent(requireContext(), TimerService::class.java)
        requireActivity().startService(timerServiceIntent)

        // 시작, 정지 버튼 동작
        binding.buttonExerciseStartAndPause.setOnClickListener {
            when (binding.isTimerRunning) {
                true -> startTimerServiceWithMode(PAUSE)
                else -> startTimerServiceWithMode(START)
            }
        }

    }

    override fun onStart() {
        super.onStart()

        // 2. 운동화면에선 알림 없음
        startTimerServiceWithMode(STOP_NOTIFICATION)
    }

    override fun onResume() {
        super.onResume()

        // 3. 현재 상태 받아오기, 시간 표시하기 위해 필요함
        startTimerServiceWithMode(STATUS) // 초기값이 있기 때문에 굳이 안받아와도 된다고 생각, 정지하고 나갔다 들어왔을 때 시간표시하려면 있어야한다.

        setBroadcastReceiver()
    }

    override fun onStop() {
        super.onStop()
        // 4. 운동화면 나갈때 알림 표시
        if (isCompleted.not()) {
            startTimerServiceWithMode(START_NOTIFICATION)
        }
    }

    private fun startTimerServiceWithMode(mode: String) {
        timerServiceIntent.putExtra(MANAGE_ACTION_NAME, mode)
        requireActivity().startService(timerServiceIntent)
    }

    private fun setBroadcastReceiver() {
        registerReceiver(MOMENT_ACTION_NAME) { _, intent ->
            val timeCount = intent?.getParcelableExtra<TimeCount>(TIME_COUNT_INTENT_EXTRA)
            binding.timeString = timeCount.toString()
        }

        registerReceiver(STATUS_ACTION_NAME) { _, intent ->
            intent?.let {
                val isTimerRunning =
                    intent.getBooleanExtra(TimerService.IS_TIMER_RUNNING_INTENT_EXTRA, false)
                val timeCount = intent.getParcelableExtra<TimeCount>(TIME_COUNT_INTENT_EXTRA)

                binding.timeString = timeCount.toString()
                binding.isTimerRunning = isTimerRunning
            }
        }

    }

    private fun registerReceiver(
        action: String,
        handler: BroadcastReceiver.(Context?, Intent?) -> Unit
    ) {
        val intentFilter = IntentFilter().apply {
            addAction(action)
        }
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                handler(context, intent)
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
                exerciseViewModel.loadTodayHistory().collect() { todayHistories ->
                    if (todayHistories.size == 1) {
                        val historyId = todayHistories[0].historyId
                        binding.buttonExerciseAdd.setOnClickListener() {
                            exerciseViewModel.addHistoryExercise(historyId)
                            renewTodayHistory()
                        }
                        binding.buttonExerciseEnd.setOnClickListener() {
                            verifyAllHistorySets(historyExerciseAdapter.currentList)
                        }
                        exerciseViewModel.loadHistoryExercises(historyId)
                            .collect() { historyExercises ->
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
                exerciseViewModel.loadHistorySets(historyExerciseId).collect() { historySets ->
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
            Log.d("isAllHistorySetsChecked", isAllHistorySetsChecked.toString())
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
                        isCompleted = true
                        exerciseViewModel.updateHistory(newHistory)
                        startTimerServiceWithMode(STOP)
                        findNavController().navigateUp()
                    }
                }
            }
        }
    }

}