package com.lateinit.rightweight.ui.share

import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import com.lateinit.rightweight.R
import com.lateinit.rightweight.data.database.mediator.SharedRoutineSortType
import com.lateinit.rightweight.databinding.FragmentSharedRoutineBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SharedRoutineFragment : Fragment(), SharedRoutineClickHandler {

    private var _binding: FragmentSharedRoutineBinding? = null
    private val binding
        get() = checkNotNull(_binding) { "binding was accessed outside of view lifecycle" }

    val sharedRoutineViewModel: SharedRoutineViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSharedRoutineBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setSharedRoutineSortSelection()

        val sharedRoutinePagingAdapter = SharedRoutinePagingAdapter( this)
        binding.recyclerViewSharedRoutines.adapter = sharedRoutinePagingAdapter

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                sharedRoutineViewModel.uiState.collect { uiState ->
                    when (uiState) {
                        is LatestSharedRoutineUiState.Success -> sharedRoutinePagingAdapter.submitData(uiState.sharedRoutines)
                        is LatestSharedRoutineUiState.Error -> Exception()
                    }
                }
            }
        }
    }

    private fun setSharedRoutineSortSelection(){
        val sortTypes = SharedRoutineSortType.values().map { sharedRoutineSortType ->
            getString(sharedRoutineSortType.sortTypeName)
        }
        val sortTypeAdapter =
            ArrayAdapter(requireContext(), R.layout.item_shared_routine_sort_type,sortTypes)
        binding.textViewSharedRoutineSortType.setAdapter(sortTypeAdapter)
    }

    override fun gotoSharedRoutineDetailFragment(routineId: String) {
        val bundle = bundleOf("routineId" to routineId)
        findNavController(this).navigate(R.id.action_navigation_shared_routine_to_navigation_shared_routine_detail, bundle)
    }
}