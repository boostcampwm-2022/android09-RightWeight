package com.lateinit.rightweight.ui.routine.management

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.lateinit.rightweight.data.database.entity.Routine
import com.lateinit.rightweight.databinding.ItemRoutineBinding

class RoutineManagementAdapter :
    ListAdapter<Routine, RoutineManagementAdapter.RoutineViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoutineViewHolder {
        return RoutineViewHolder(
            ItemRoutineBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: RoutineViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it)
        }
    }

    private fun moveUp(pos: Int) {
        if (pos == 0) {
            return
        }

        val value = currentList[pos]
        val list = currentList.toMutableList()
        list.removeAt(pos)
        list.add(pos - 1, value)
        submitList(list)
    }

    private fun moveDown(pos: Int) {
        if (pos == itemCount - 1) {
            return
        }
        val value = currentList[pos]
        val list = currentList.toMutableList()
        list.removeAt(pos)
        list.add(pos + 1, value)
        submitList(list)
    }

    inner class RoutineViewHolder(val binding: ItemRoutineBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(routine: Routine) {
            binding.routine = routine

            binding.imageButtonUp.setOnClickListener {
                moveUp(layoutPosition)
            }
            binding.imageButtonDown.setOnClickListener {
                moveDown(layoutPosition)
            }
            binding.cardViewRoutineItemContainer.setOnClickListener { view ->
                val action =
                    RoutineManagementFragmentDirections.actionNavigationRoutineManagementToNavigationRoutineDetail(
                        routine.routineId
                    )
                view.findNavController().navigate(action)
            }
        }
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<Routine>() {
            override fun areItemsTheSame(oldItem: Routine, newItem: Routine): Boolean {
                return oldItem.routineId == newItem.routineId
            }

            override fun areContentsTheSame(oldItem: Routine, newItem: Routine): Boolean {
                return oldItem.routineId == newItem.routineId
            }

        }
    }
}