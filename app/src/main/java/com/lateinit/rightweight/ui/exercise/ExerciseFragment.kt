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
import com.lateinit.rightweight.R
import com.lateinit.rightweight.databinding.FragmentExerciseBinding
import com.lateinit.rightweight.service.TimerService

class ExerciseFragment : Fragment() {

    private lateinit var timerStatusReceiver: BroadcastReceiver
    private lateinit var timerMomentReceiver: BroadcastReceiver

    lateinit var binding: FragmentExerciseBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val timerService = Intent(requireContext(), TimerService::class.java)
        timerService.putExtra(TimerService.MANAGE_ACTION_NAME, TimerService.STOP_NOTIFICATION)
        requireActivity().startService(timerService)

        binding = FragmentExerciseBinding.inflate(layoutInflater, container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonStartAndPauseExercise.setOnClickListener(){
            when(binding.isTimerRunning){
                true -> pauseTimer()
                false -> startTimer()
                else -> startTimer()
            }
        }
        binding.buttonEndExercise.setOnClickListener(){
            stopTimer()
        }
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

    override fun onDestroyView() {
        super.onDestroyView()

        val timerService = Intent(requireContext(), TimerService::class.java)
        timerService.putExtra(TimerService.MANAGE_ACTION_NAME, TimerService.START_NOTIFICATION)
        requireActivity().startService(timerService)
    }

}