package com.lateinit.rightweight.ui.routine.management

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.lateinit.rightweight.databinding.FragmentRoutineManagementBinding
import com.lateinit.rightweight.util.DEFAULT_AUTHOR_NAME
import com.lateinit.rightweight.util.collectOnLifecycle
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RoutineManagementFragment : Fragment() {

    private var _binding: FragmentRoutineManagementBinding? = null
    private val binding
        get() = checkNotNull(_binding) { "binding was accessed outside of view lifecycle" }

    private val viewModel: RoutineManagementViewModel by viewModels()

    private lateinit var routineAdapter: RoutineAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRoutineManagementBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setBinding()
        setRoutineAdapter()
        setOnClickListeners()
        collectRoutines()
    }

    private fun setBinding() {
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
    }

    private fun setRoutineAdapter() {
        routineAdapter = RoutineAdapter(object : RoutineAdapter.RoutineEventListener {
            override fun moveUp(routinePosition: Int) {
                viewModel.moveUpRoutine(routinePosition)
            }

            override fun moveDown(routinePosition: Int) {
                viewModel.moveDownRoutine(routinePosition)
            }

            override fun onClick(routineId: String) {
                navigateToRoutineDetail(routineId)
            }
        })
        binding.recyclerViewRoutine.apply {
            adapter = routineAdapter
            setHasFixedSize(true)
        }
    }

    private fun collectRoutines() {
        collectOnLifecycle {
            viewModel.routineUiModels.collect {
                routineAdapter.submitList(it)
            }
        }
    }

    private fun setOnClickListeners() {
        binding.cardViewHomeRoutineTitleContainer.setOnClickListener {
            val routineId = viewModel.selectedRoutineUiModel.value?.routineId ?: return@setOnClickListener
            navigateToRoutineDetail(routineId)
        }

        binding.floatingActionButtonCreateRoutine.setOnClickListener {
            navigateToRoutineEditor()
        }
    }

    private fun navigateToRoutineEditor() {
        val author = viewModel.userInfo.value?.displayName ?: DEFAULT_AUTHOR_NAME
        val action =
            RoutineManagementFragmentDirections.actionNavigationRoutineManagementToNavigationRoutineEditor(
                author = author
            )
        findNavController().navigate(action)
    }

    private fun navigateToRoutineDetail(routineId: String) {
        val action =
            RoutineManagementFragmentDirections.actionNavigationRoutineManagementToNavigationRoutineDetail(
                routineId
            )
        findNavController().navigate(action)
    }

    override fun onPause() {
        super.onPause()
        viewModel.updateRoutines()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}