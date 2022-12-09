package com.lateinit.rightweight.ui.share

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.lateinit.rightweight.R
import com.lateinit.rightweight.databinding.FragmentSharedRoutineBinding
import com.lateinit.rightweight.ui.model.SharedRoutineSortTypeUiModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SharedRoutineFragment : Fragment(), SharedRoutineClickHandler {

    private var _binding: FragmentSharedRoutineBinding? = null
    private val binding
        get() = checkNotNull(_binding) { "binding was accessed outside of view lifecycle" }

    private val viewModel: SharedRoutineViewModel by viewModels()
    private lateinit var sharedRoutinePagingAdapter: SharedRoutinePagingAdapter
    private lateinit var sortTypes: List<String>
    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSharedRoutineBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setFragmentResult()
        initSharedRoutineSortSelection()
        sharedRoutinePagingAdapter = SharedRoutinePagingAdapter(this)
        binding.recyclerViewSharedRoutines.adapter = sharedRoutinePagingAdapter

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { uiState ->
                    when (uiState) {
                        is LatestSharedRoutineUiState.Success -> sharedRoutinePagingAdapter.submitData(uiState.sharedRoutines)
                        is LatestSharedRoutineUiState.Error -> Exception()
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        setSharedRoutineSortSelection()
    }

    private fun setFragmentResult() {
        setFragmentResultListener("routineCopy") { _, bundle ->
            Snackbar.make(
                binding.root,
                R.string.success_save_routine,
                Snackbar.LENGTH_SHORT
            ).apply {
                anchorView = binding.guideLineBottom
                setAction(getText(R.string.submit)) {
                    this.dismiss()
                }
            }.show()
        }

    }

    private fun initSharedRoutineSortSelection(){
        sortTypes = SharedRoutineSortTypeUiModel.values().map { sharedRoutineSortType ->
            getString(sharedRoutineSortType.sortTypeName)
        }
        binding.textViewSharedRoutineSortType.setText(sortTypes[0])
    }

    private fun setSharedRoutineSortSelection(){
        val sortTypeAdapter =
            ArrayAdapter(requireContext(), R.layout.item_shared_routine_sort_type, sortTypes)
        binding.textViewSharedRoutineSortType.setAdapter(sortTypeAdapter)
        binding.textViewSharedRoutineSortType.setOnItemClickListener { _, _, position, _ ->
            viewModel.setSharedRoutineSortType(sortTypeUiModel = SharedRoutineSortTypeUiModel.values()[position])
            sharedRoutinePagingAdapter.refresh()
        }
    }

    override fun gotoSharedRoutineDetailFragment(routineId: String) {
        val bundle = bundleOf("routineId" to routineId)
        findNavController(this).navigate(R.id.action_navigation_shared_routine_to_navigation_shared_routine_detail, bundle)
    }
}