package com.lateinit.rightweight.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.findNavController
import androidx.recyclerview.widget.ConcatAdapter
import com.lateinit.rightweight.R
import com.lateinit.rightweight.databinding.FragmentHomeBinding
import com.lateinit.rightweight.ui.MainActivity
import com.lateinit.rightweight.ui.dialog.CommonDialogFragment
import com.lateinit.rightweight.ui.dialog.CommonDialogFragment.Companion.RESET_DIALOG_TAG
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding
        get() = checkNotNull(_binding) { "binding was accessed outside of view lifecycle" }
    private val homeViewModel: HomeViewModel by viewModels()
    private lateinit var adapter: ConcatAdapter
    private val dialog: CommonDialogFragment by lazy {
        CommonDialogFragment{ tag ->
            when (tag) {
                RESET_DIALOG_TAG -> {
//                    userViewModel.resetRoutine()
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

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                homeViewModel.loadTodayHistory().collect() { todayHistories ->

                    if (todayHistories.isEmpty()) {
                        setHomeInfoText(getString(R.string.home_description))
                        binding.floatingActionButtonStartExercise.show()
                        binding.floatingActionButtonStartExercise.setOnClickListener {
                            homeViewModel.saveHistory()
                            it.findNavController()
                                .navigate(R.id.action_navigation_home_to_navigation_exercise)
                        }
                    } else {
                        if (todayHistories.size == 1) {
                            binding.floatingActionButtonStartExercise.setOnClickListener {
                                it.findNavController()
                                    .navigate(R.id.action_navigation_home_to_navigation_exercise)
                            }
                            if (todayHistories[0].completed) {
                                binding.floatingActionButtonStartExercise.hide()
                                setHomeInfoText(
                                    getString(R.string.home_end_exercise_description) +
                                            " (" + todayHistories[0].time + ")"
                                )
                            } else {
                                binding.floatingActionButtonStartExercise.show()
                                setHomeInfoText(getString(R.string.home_run_exercise_description))
                            }
                        }
                    }
                }
            }
        }

        binding.cardViewHomeRoutineTitleContainer.setOnClickListener {
            (requireActivity() as MainActivity).navigateBottomNav(R.id.navigation_routine_management)
        }
        binding.cardViewHomeRoutineResetContainer.setOnClickListener {
            dialog.show(parentFragmentManager, RESET_DIALOG_TAG, R.string.reset_message)
        }

        homeViewModel.dayUiModel.observe(viewLifecycleOwner) { dayUiModel ->
            val exercises = dayUiModel?.exercises ?: emptyList()
            val homeAdapters = exercises.map { exerciseUiModel ->
                HomeAdapter(exerciseUiModel)
            }

            adapter = ConcatAdapter(homeAdapters)
            binding.layoutDayExercises.recyclerViewTodayRoutine.adapter = adapter
            binding.layoutDayExercises.recyclerViewTodayRoutine.itemAnimator = ExpandableItemAnimator()
        }

        homeViewModel.loadDayWithExercises()
        homeViewModel.loadSelectedRoutine()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun setBinding() {
        binding.lifecycleOwner = viewLifecycleOwner
        binding.homeViewModel = homeViewModel
    }

    private fun setHomeInfoText(description: String) {
        binding.textViewHomeInfo.text = String.format(
            description,
            LocalDateTime.now().format(
                DateTimeFormatter.ofPattern(getString(R.string.date_format))
            )
        )
    }
}