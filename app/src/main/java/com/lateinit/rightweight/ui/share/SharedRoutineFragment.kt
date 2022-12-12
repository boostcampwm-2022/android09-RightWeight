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
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import com.google.android.material.snackbar.Snackbar
import com.lateinit.rightweight.R
import com.lateinit.rightweight.databinding.FragmentSharedRoutineBinding
import com.lateinit.rightweight.ui.model.shared.SharedRoutineSortTypeUiModel
import com.lateinit.rightweight.util.collectOnLifecycle
import dagger.hilt.android.AndroidEntryPoint

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

        binding.swipeRefreshLayoutSharedRoutines.setOnRefreshListener {
            sharedRoutinePagingAdapter.refresh()
        }

        viewLifecycleOwner.collectOnLifecycle {
            viewModel.uiState.collect { uiState ->
                when (uiState) {
                    is LatestSharedRoutineUiState.Success -> {
                        sharedRoutinePagingAdapter.submitData(uiState.sharedRoutines)
                        binding.swipeRefreshLayoutSharedRoutines.isRefreshing = false
                        binding.recyclerViewSharedRoutines.smoothScrollToPosition(0)
                    }
                    is LatestSharedRoutineUiState.Error -> {
                        Snackbar.make(
                            binding.root,
                            R.string.wrong_connection,
                            Snackbar.LENGTH_LONG
                        ).apply {
                            anchorView = binding.guideLineBottom
                        }.show()
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
        setFragmentResultListener("routineCopy") { _, _ ->
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

    private fun initSharedRoutineSortSelection() {
        sortTypes = SharedRoutineSortTypeUiModel.values().map { sharedRoutineSortType ->
            getString(sharedRoutineSortType.sortTypeName)
        }
        binding.textViewSharedRoutineSortType.setText(sortTypes[0])
    }

    private fun setSharedRoutineSortSelection() {
        val sortTypeAdapter =
            ArrayAdapter(requireContext(), R.layout.item_shared_routine_sort_type, sortTypes)

        binding.textViewSharedRoutineSortType.setAdapter(sortTypeAdapter)
        binding.textViewSharedRoutineSortType.setOnItemClickListener { _, _, position, _ ->
            viewModel.setSharedRoutineSortType(SharedRoutineSortTypeUiModel.values()[position])
            sharedRoutinePagingAdapter.refresh()
        }
    }

    override fun gotoSharedRoutineDetailFragment(routineId: String) {
        val bundle = bundleOf("routineId" to routineId)
        findNavController(this).navigate(
            R.id.action_navigation_shared_routine_to_navigation_shared_routine_detail,
            bundle
        )
    }
}