package com.lateinit.rightweight.ui.routine.detail

import android.os.Bundle
import android.view.*
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.lateinit.rightweight.R
import com.lateinit.rightweight.databinding.FragmentRoutineDetailBinding
import com.lateinit.rightweight.ui.home.UserViewModel
import com.lateinit.rightweight.ui.dialog.CommonDialogFragment
import com.lateinit.rightweight.ui.dialog.CommonDialogFragment.Companion.ROUTINE_REMOVE_DIALOG_TAG
import com.lateinit.rightweight.ui.routine.editor.RoutineDayAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RoutineDetailFragment : Fragment(){
    private var _binding: FragmentRoutineDetailBinding? = null
    private val binding
        get() = checkNotNull(_binding) { "binding was accessed outside of view lifecycle" }

    private val args: RoutineDetailFragmentArgs by navArgs()

    private val userViewModel: UserViewModel by activityViewModels()
    private val routineDetailViewModel: RoutineDetailViewModel by viewModels()

    private lateinit var routineDayAdapter: RoutineDayAdapter
    private lateinit var exerciseAdapter: DetailExerciseAdapter
    private val dialog: CommonDialogFragment by lazy {
        CommonDialogFragment{
            when (dialog.tag) {
                ROUTINE_REMOVE_DIALOG_TAG -> {
                    routineDetailViewModel.removeRoutine(args.routineId)
                    userViewModel.setUser(routineId = null)
                    findNavController().navigateUp()
                }
            }
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRoutineDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        routineDetailViewModel.getRoutine(args.routineId)
        setMenu()
        setBinding()
        setRoutineDayAdapter()
        setDayUiModelsObserve()
        setExerciseAdapter()
        setCurrentDayPositionObserve()
    }

    private fun setMenu() {
        (requireActivity() as MenuHost).addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.clear()
                menuInflater.inflate(R.menu.menu_routine_detail, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.action_item_edit -> {
                        val routineId = routineDetailViewModel.routine.value?.routineId ?: return false
                        val action =
                            RoutineDetailFragmentDirections.actionNavigationRoutineDetailToNavigationRoutineEditor(
                                routineId
                            )
                        findNavController().navigate(action)
                        return true
                    }
                    R.id.action_item_remove -> {
                        removeRoutine(args.routineId)
                        return true
                    }
                    R.id.action_item_share -> {

                        routineDetailViewModel.deleteSharedRoutineAndDays()
                        return true
                    }
                    else -> {
                        return false
                    }
                }
            }

        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun setBinding() {
        binding.lifecycleOwner = viewLifecycleOwner
        binding.userViewModel = userViewModel
        binding.routineDetailViewModel = routineDetailViewModel
    }

    private fun setRoutineDayAdapter() {
        routineDayAdapter =
            RoutineDayAdapter { position -> routineDetailViewModel.clickDay(position) }
        binding.recyclerViewDay.adapter = routineDayAdapter
    }

    private fun setDayUiModelsObserve() {
        routineDetailViewModel.dayUiModels.observe(viewLifecycleOwner) {
            routineDayAdapter.submitList(it)
        }
    }

    private fun setExerciseAdapter() {
        exerciseAdapter = DetailExerciseAdapter { position ->
            routineDetailViewModel.clickExercise(position)
        }
        binding.recyclerViewExercise.adapter = exerciseAdapter
    }

    private fun setCurrentDayPositionObserve() {
        routineDetailViewModel.currentDayPosition.observe(viewLifecycleOwner) {
            val exercises =
                routineDetailViewModel.dayUiModels.value?.get(it)?.exercises ?: return@observe
            exerciseAdapter.submitList(exercises)
        }
    }
    
    private fun removeRoutine(routineId: String) {
        val selectedRoutineId = userViewModel.userInfo.value?.routineId ?: return
        if (selectedRoutineId != routineId) {
            routineDetailViewModel.removeRoutine(routineId)
            findNavController().navigateUp()
        } else {
            dialog.show(parentFragmentManager, ROUTINE_REMOVE_DIALOG_TAG, R.string.routine_remove_message)
        }
    }
    
    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}