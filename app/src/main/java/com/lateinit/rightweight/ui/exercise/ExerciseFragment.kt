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
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.lateinit.rightweight.R
import com.lateinit.rightweight.data.database.entity.Exercise
import com.lateinit.rightweight.data.database.entity.HistoryExercise
import com.lateinit.rightweight.data.database.entity.HistorySet
import com.lateinit.rightweight.databinding.FragmentExerciseBinding
import com.lateinit.rightweight.service.TimerService
import com.lateinit.rightweight.ui.dialog.CommonDialogFragment
import com.lateinit.rightweight.ui.dialog.CommonDialogFragment.Companion.END_EXERCISE_DIALOG_TAG
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ExerciseFragment : Fragment(), HistoryEventListener{

    private lateinit var timerStatusReceiver: BroadcastReceiver
    private lateinit var timerMomentReceiver: BroadcastReceiver
    private val exerciseViewModel: ExerciseViewModel by viewModels()
    private lateinit var timerServiceIntent: Intent

    lateinit var binding: FragmentExerciseBinding
    private val dialog: CommonDialogFragment by lazy {
        CommonDialogFragment{ tag ->
            when (tag) {
                CommonDialogFragment.END_EXERCISE_DIALOG_TAG -> {
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

        timerServiceIntent = Intent(requireContext(), TimerService::class.java)
        requireActivity().startService(timerServiceIntent)

        binding.buttonExerciseStartAndPause.setOnClickListener() {
            when (binding.isTimerRunning) {
                true -> pauseTimer()
                false -> startTimer()
                else -> startTimer()
            }
        }

    }

    override fun onStart() {
        super.onStart()

        timerServiceIntent.putExtra(TimerService.MANAGE_ACTION_NAME, TimerService.STOP_NOTIFICATION)
        requireActivity().startService(timerServiceIntent)
    }

    override fun onResume() {
        super.onResume()
        getTimerStatus()

        val timeFilter = IntentFilter()
        timeFilter.addAction(TimerService.MOMENT_ACTION_NAME)
        timerMomentReceiver = object : BroadcastReceiver() {
            override fun onReceive(p0: Context?, p1: Intent?) {
                val timeCount = p1?.getIntExtra(TimerService.TIME_COUNT_INTENT_EXTRA, 0)!!
                val timeString = getTimeString(timeCount)

                binding.timeString = timeString
            }
        }
        requireContext().registerReceiver(timerMomentReceiver, timeFilter)

        val statusFilter = IntentFilter()
        statusFilter.addAction(TimerService.STATUS_ACTION_NAME)
        timerStatusReceiver = object : BroadcastReceiver() {
            override fun onReceive(p0: Context?, p1: Intent?) {
                p1?.let {
                    val isTimerRunning =
                        p1.getBooleanExtra(TimerService.IS_TIMER_RUNNING_INTENT_EXTRA, false)
                    val timeCount = p1.getIntExtra(TimerService.TIME_COUNT_INTENT_EXTRA, 0)
                    val timeString = getTimeString(timeCount)

                    binding.timeString = timeString
                    binding.isTimerRunning = isTimerRunning
                }
            }
        }
        requireContext().registerReceiver(timerStatusReceiver, statusFilter)
    }

    override fun onStop() {
        super.onStop()
        timerServiceIntent.putExtra(TimerService.MANAGE_ACTION_NAME, TimerService.START_NOTIFICATION)
        requireActivity().startService(timerServiceIntent)
    }

    fun getTimeString(timeCount: Int): String {
        val hours: Int = (timeCount / 60) / 60
        val minutes: Int = timeCount / 60
        val seconds: Int = timeCount % 60
        return "${"%02d".format(hours)}:${"%02d".format(minutes)}:${"%02d".format(seconds)}"
    }

    private fun startTimer() {
        timerServiceIntent.putExtra(TimerService.MANAGE_ACTION_NAME, TimerService.START)
        requireActivity().startService(timerServiceIntent)
    }

    private fun pauseTimer() {
        timerServiceIntent.putExtra(TimerService.MANAGE_ACTION_NAME, TimerService.PAUSE)
        requireActivity().startService(timerServiceIntent)
    }

    private fun stopTimerService(){
        timerServiceIntent.putExtra(TimerService.MANAGE_ACTION_NAME, TimerService.STOP)
        requireActivity().startService(timerServiceIntent)
    }

    private fun getTimerStatus() {
        timerServiceIntent.putExtra(TimerService.MANAGE_ACTION_NAME, TimerService.STATUS)
        requireActivity().startService(timerServiceIntent)
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
            repeatOnLifecycle(Lifecycle.State.STARTED){
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
                        exerciseViewModel.loadHistoryExercises(historyId).collect() { historyExercises ->
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

    private fun verifyAllHistorySets(historyExercises: List<HistoryExercise>){
        exerciseViewModel.verifyAllHistorySets(historyExercises)
        exerciseViewModel.isAllHistorySetsChecked.observe(viewLifecycleOwner){ isAllHistorySetsChecked ->
            Log.d("isAllHistorySetsChecked", isAllHistorySetsChecked.toString())
            if(isAllHistorySetsChecked.not()){
                if(!dialog.isAdded){
                    dialog.show(
                        parentFragmentManager,
                        END_EXERCISE_DIALOG_TAG, R.string.end_exercise_message
                    )
                }
            }
            else{
                endExercise()
            }
        }
    }

    private fun endExercise(){
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                exerciseViewModel.loadTodayHistory().collect() { todayHistories ->
                    if (todayHistories.size == 1) {
                        val newHistory = todayHistories[0].copy()
                        newHistory.time = binding.timeString.toString()
                        newHistory.completed = true
                        exerciseViewModel.updateHistory(newHistory)
                        stopTimerService()
                        findNavController().navigateUp()
                    }
                }
            }
        }
    }

}