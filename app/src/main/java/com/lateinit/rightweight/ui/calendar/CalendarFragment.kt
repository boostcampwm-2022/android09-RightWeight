package com.lateinit.rightweight.ui.calendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ConcatAdapter
import com.lateinit.rightweight.databinding.FragmentCalendarBinding
import com.lateinit.rightweight.ui.home.ExpandableItemAnimator
import com.lateinit.rightweight.ui.home.HomeAdapter
import com.lateinit.rightweight.util.collectOnLifecycle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import java.time.LocalDate

@AndroidEntryPoint
class CalendarFragment : Fragment() {

    private var _binding: FragmentCalendarBinding? = null
    private val binding
        get() = checkNotNull(_binding) { "binding was accessed outside of view lifecycle" }

    private val viewModel: CalendarViewModel by viewModels()

    private lateinit var completedDayDecorator: CompletedDayDecorator

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCalendarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setBinding()
        setCalendarView()
        collectMonthHistories()
        collectSelectedDayInfo()
    }

    private fun setBinding() {
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
    }

    private fun setCalendarView() {
        completedDayDecorator = CompletedDayDecorator(requireContext())
        setCalendarListeners()
        binding.calendarView.apply {
            val today = LocalDate.now()

            state().edit().setMaximumDate(today).commit()
            addDecorators(DayDecorator(requireContext()), completedDayDecorator)
            setSelectedDate(today)
        }
    }

    private fun setCalendarListeners() {
        binding.calendarView.setOnDateChangedListener { _, day, _ ->
            viewModel.selectDay(day.date)
        }

        binding.calendarView.setOnMonthChangedListener { _, day ->
            viewModel.changeMonth(day.date)
        }
    }

    private fun collectMonthHistories() {
        viewLifecycleOwner.collectOnLifecycle {
            viewModel.dateToExerciseHistories.collectLatest {
                completedDayDecorator.changeCompletedDates(it.keys)
                binding.calendarView.invalidateDecorators()
            }
        }
    }

    private fun collectSelectedDayInfo() {
        viewLifecycleOwner.collectOnLifecycle {
            viewModel.selectedDayInfo.collectLatest { dayInfo ->
                dayInfo?.let {
                    val adapters = it.exercises.map { exerciseUiModel ->
                        HomeAdapter(exerciseUiModel)
                    }
                    val adapter = ConcatAdapter(adapters)

                    binding.layoutDayExercises.apply {
                        recyclerViewTodayRoutine.adapter = adapter
                        recyclerViewTodayRoutine.itemAnimator = ExpandableItemAnimator()
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}