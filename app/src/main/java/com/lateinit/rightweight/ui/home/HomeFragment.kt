package com.lateinit.rightweight.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.lateinit.rightweight.service.TimerService
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.ConcatAdapter
import com.lateinit.rightweight.R
import com.lateinit.rightweight.databinding.FragmentHomeBinding
import com.lateinit.rightweight.ui.home.dialog.CommonDialogFragment
import com.lateinit.rightweight.ui.home.dialog.CommonDialogFragment.Companion.RESET_DIALOG_TAG
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment(), CommonDialogFragment.NoticeDialogListener {

    private var _binding: FragmentHomeBinding? = null
    private val binding
        get() = checkNotNull(_binding) { "binding was accessed outside of view lifecycle" }
    private val userViewModel: UserViewModel by activityViewModels()
    private val homeViewModel: HomeViewModel by viewModels()
    private lateinit var adapter: ConcatAdapter
    private val dialog: CommonDialogFragment by lazy {
        CommonDialogFragment()
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val navigationRouteId = requireActivity().intent.getIntExtra(TimerService.SCREEN_MOVE_INTENT_EXTRA, -1)
        if(navigationRouteId != -1){
            findNavController().navigate(navigationRouteId)
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

        binding.floatingActionButtonStartExercise.setOnClickListener {
            homeViewModel.checkTodayHistory()
            it.findNavController().navigate(R.id.action_navigation_home_to_navigation_exercise)
        }
        binding.cardViewHomeRoutineTitleContainer.setOnClickListener {
            val item =
                (requireActivity() as HomeActivity).binding.bottomNavigation.menu.findItem(R.id.navigation_routine_management)
            NavigationUI.onNavDestinationSelected(item, findNavController())
        }
        binding.cardViewHomeRoutineResetContainer.setOnClickListener {
            dialog.show(parentFragmentManager, RESET_DIALOG_TAG, R.string.reset_message)
        }

        homeViewModel.dayUiModel.observe(viewLifecycleOwner) { dayUiModel ->
            val homeAdapters = dayUiModel.exercises.map { exerciseUiModel ->
                HomeAdapter(exerciseUiModel)
            }

            adapter = ConcatAdapter(homeAdapters)
            binding.recyclerViewTodayRoutine.adapter = adapter
        }

        userViewModel.userInfo.observe(viewLifecycleOwner) {
            homeViewModel.getDayWithExercisesByDayId(it.dayId)
        }
    }

    override fun onResume() {
        userViewModel.getUser()
        super.onResume()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun setBinding() {
        binding.lifecycleOwner = viewLifecycleOwner
        binding.userViewModel = userViewModel
        binding.homeViewModel = homeViewModel
    }

    override fun onDialogPositiveClick(dialog: DialogFragment) {
        when (dialog.tag) {
            RESET_DIALOG_TAG -> {
                userViewModel.resetRoutine()
            }
        }
    }

}