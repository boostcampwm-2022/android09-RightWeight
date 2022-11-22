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
    ): View? {
        binding = FragmentExerciseBinding.inflate(layoutInflater, container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonStartAndPauseExercise.setOnClickListener(){
            Log.d("isTimerRunning", binding.isTimerRunning.toString())
            when(binding.isTimerRunning){
                true -> pauseTimer()
                false -> startTimer()
                else -> startTimer()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        getTimerStatus()

        val timeFilter = IntentFilter()
        timeFilter.addAction("timer_moment")
        timerMomentReceiver = object : BroadcastReceiver() {
            override fun onReceive(p0: Context?, p1: Intent?) {
                val timeCount = p1?.getIntExtra("time_count", 0)!!
                val timeString = getTimeString(timeCount)

                binding.timeString = timeString
            }
        }
        requireContext().registerReceiver(timerMomentReceiver, timeFilter)

        val statusFilter = IntentFilter()
        statusFilter.addAction("timer_status")
        timerStatusReceiver = object : BroadcastReceiver() {
            override fun onReceive(p0: Context?, p1: Intent?) {
                p1?.let{
                    val isTimerRunning = p1.getBooleanExtra("is_timer_running", false)
                    val timeCount = p1.getIntExtra("time_count", 0)
                    val timeString = getTimeString(timeCount)

                    binding.timeString = timeString
                    binding.isTimerRunning = isTimerRunning
                    Log.d("isTimerRunning2", binding.isTimerRunning.toString())
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
        timerService.putExtra("timer_manage_action", "start")
        requireActivity().startService(timerService)
    }

    fun pauseTimer() {
        val timerService = Intent(requireContext(), TimerService::class.java)
        timerService.putExtra("timer_manage_action", "pause")
        requireActivity().startService(timerService)
    }

    fun getTimerStatus() {
        val timerService = Intent(requireContext(), TimerService::class.java)
        timerService.putExtra("timer_manage_action", "status")
        requireActivity().startService(timerService)
    }

}