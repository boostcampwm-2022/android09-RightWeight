package com.lateinit.rightweight.ui.routine.management

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.lateinit.rightweight.data.database.entity.Routine

@BindingAdapter(value = [
    "setItems",
    "excludeItem"
])
fun setItems(recyclerView: RecyclerView, items: List<Routine>, excludeItem: Routine?) {
    if (recyclerView.adapter == null) {
        recyclerView.adapter = RoutineManagementAdapter()
    }
    val routineManagementAdapter = recyclerView.adapter as RoutineManagementAdapter
    val filteredItems = items.filter { it.routineId != excludeItem?.routineId }
    routineManagementAdapter.submitList(filteredItems)
}