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
import com.lateinit.rightweight.R
import com.lateinit.rightweight.data.database.entity.HistoryExercise
import com.lateinit.rightweight.data.database.entity.HistorySet
import com.lateinit.rightweight.databinding.FragmentExerciseBinding
import com.lateinit.rightweight.service.TimerService
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ExerciseFragment : Fragment(), HistoryEventListener {

    private lateinit var timerStatusReceiver: BroadcastReceiver
    private lateinit var timerMomentReceiver: BroadcastReceiver
    private val exerciseViewModel: ExerciseViewModel by viewModels()

    lateinit var binding: FragmentExerciseBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentExerciseBinding.inflate(layoutInflater, container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        renewTodayHistory()

        val timerService = Intent(requireContext(), TimerService::class.java)
        requireActivity().startService(timerService)

        binding.buttonExerciseStartAndPause.setOnClickListener(){
            when(binding.isTimerRunning){
                true -> pauseTimer()
                false -> startTimer()
                else -> startTimer()
            }
        }
        binding.buttonExerciseEnd.setOnClickListener(){
            stopTimer()
        }

    }

    override fun onStart(){
        super.onStart()

        val timerService = Intent(requireContext(), TimerService::class.java)
        timerService.putExtra(TimerService.MANAGE_ACTION_NAME, TimerService.STOP_NOTIFICATION)
        requireActivity().startService(timerService)
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
                p1?.let{
                    val isTimerRunning = p1.getBooleanExtra(TimerService.IS_TIMER_RUNNING_INTENT_EXTRA, false)
                    val timeCount = p1.getIntExtra(TimerService.TIME_COUNT_INTENT_EXTRA, 0)
                    val timeString = getTimeString(timeCount)

                    binding.timeString = timeString
                    binding.isTimerRunning = isTimerRunning
                }
            }
        }
        requireContext().registerReceiver(timerStatusReceiver, statusFilter)
    }

    fun getTimeString(timeCount: Int): String{
        val hours: Int = (timeCount / 60) / 60
        val minutes: Int = timeCount / 60
        val seconds: Int = timeCount % 60
        return "${"%02d".format(hours)}:${"%02d".format(minutes)}:${"%02d".format(seconds)}"
    }

    fun startTimer() {
        val timerService = Intent(requireContext(), TimerService::class.java)
        timerService.putExtra(TimerService.MANAGE_ACTION_NAME, TimerService.START)
        requireActivity().startService(timerService)
    }

    fun pauseTimer() {
        val timerService = Intent(requireContext(), TimerService::class.java)
        timerService.putExtra(TimerService.MANAGE_ACTION_NAME, TimerService.PAUSE)
        requireActivity().startService(timerService)
    }

    fun stopTimer() {
        val timerService = Intent(requireContext(), TimerService::class.java)
        timerService.putExtra(TimerService.MANAGE_ACTION_NAME, TimerService.STOP)
        requireActivity().startService(timerService)
        requireActivity().stopService(timerService)
    }

    fun getTimerStatus() {
        val timerService = Intent(requireContext(), TimerService::class.java)
        timerService.putExtra(TimerService.MANAGE_ACTION_NAME, TimerService.STATUS)
        requireActivity().startService(timerService)
    }

    override fun onStop() {
        super.onStop()

        val timerService = Intent(requireContext(), TimerService::class.java)
        timerService.putExtra(TimerService.MANAGE_ACTION_NAME, TimerService.START_NOTIFICATION)
        requireActivity().startService(timerService)
    }

    override fun applyHistorySets(historyExerciseId: String, adapter: HistorySetAdapter) {
        if(exerciseViewModel.historySets.containsKey(historyExerciseId)) {
            exerciseViewModel.historySets[historyExerciseId]?.observe(viewLifecycleOwner) { historySets ->
                adapter.submitList(historySets)
            }
        }
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

        exerciseViewModel.loadTodayHistory()

        exerciseViewModel.history.observe(viewLifecycleOwner){ history ->
            binding.buttonExerciseAdd.setOnClickListener(){
                addHistoryExercise(history.historyId)
                renewTodayHistory()
            }
        }


        exerciseViewModel.historyExercises.observe(viewLifecycleOwner){ historyExercises ->
            historyExerciseAdapter.submitList(historyExercises)
        }
    }

    override fun addHistorySet(historyExerciseId: String) {
        exerciseViewModel.addHistorySet(historyExerciseId)
    }

    fun addHistoryExercise(historyId:String){
        exerciseViewModel.addHistoryExercise(historyId)
    }

}