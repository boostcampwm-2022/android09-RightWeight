package com.lateinit.rightweight.ui.share

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.os.bundleOf
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import com.lateinit.rightweight.R
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
    ): View? {
        _binding = FragmentSharedRoutineBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setMenu()

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

    private fun setMenu() {
        (requireActivity() as MenuHost).addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_shared_routine, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_item_search -> {
                        Log.d("SharedRoutineFragment", "Search")
                        true
                    }
                    else -> {
                        false
                    }
                }
            }

        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    override fun gotoSharedRoutineDetailFragment(routineId: String) {
        val bundle = bundleOf("routineId" to routineId)
        findNavController(this).navigate(R.id.action_navigation_shared_routine_to_navigation_shared_routine_detail, bundle)
    }
}