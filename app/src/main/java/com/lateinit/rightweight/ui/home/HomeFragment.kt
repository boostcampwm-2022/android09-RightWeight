package com.lateinit.rightweight.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ConcatAdapter
import com.lateinit.rightweight.R
import com.lateinit.rightweight.databinding.FragmentHomeBinding
import com.lateinit.rightweight.ui.MainActivity
import com.lateinit.rightweight.ui.dialog.CommonDialogFragment
import com.lateinit.rightweight.ui.dialog.CommonDialogFragment.Companion.RESET_DIALOG_TAG
import com.lateinit.rightweight.ui.model.routine.DayUiModel
import com.lateinit.rightweight.util.collectOnLifecycle
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding
        get() = checkNotNull(_binding) { "binding was accessed outside of view lifecycle" }

    private val viewModel: HomeViewModel by viewModels()

    private lateinit var adapter: ConcatAdapter
    private val dialog: CommonDialogFragment by lazy {
        CommonDialogFragment{ tag ->
            when (tag) {
                RESET_DIALOG_TAG -> {
                    viewModel.resetRoutine()
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setBinding()
        setListeners()
        collectSelectedDay()
    }

    private fun setBinding() {
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
    }

    private fun setListeners() {
        binding.cardViewHomeRoutineTitleContainer.setOnClickListener {
            (requireActivity() as MainActivity).navigateBottomNav(R.id.navigation_routine_management)
        }
        binding.cardViewHomeRoutineResetContainer.setOnClickListener {
            dialog.show(parentFragmentManager, RESET_DIALOG_TAG, R.string.reset_message)
        }
        binding.floatingActionButtonStartExercise.setOnClickListener {
            if (hasHistory().not()) {
                viewModel.saveHistory()
            }
            findNavController().navigate(R.id.action_navigation_home_to_navigation_exercise)
        }
    }

    private fun hasHistory(): Boolean {
        return viewModel.todayHistory.value != null
    }

    private fun collectSelectedDay() {
        viewLifecycleOwner.collectOnLifecycle {
            viewModel.selectedDay.collect { dayUiModel ->
                setAdapter(dayUiModel)
            }
        }
    }

    private fun setAdapter(dayUiModel: DayUiModel?) {
        val exercises = dayUiModel?.exercises ?: emptyList()
        val homeAdapters = exercises.map { exerciseUiModel ->
            HomeAdapter(exerciseUiModel)
        }

        adapter = ConcatAdapter(homeAdapters)
        binding.layoutDayExercises.recyclerViewTodayRoutine.adapter = adapter
        binding.layoutDayExercises.recyclerViewTodayRoutine.itemAnimator = ExpandableItemAnimator()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}