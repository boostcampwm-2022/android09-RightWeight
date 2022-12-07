package com.lateinit.rightweight.ui.routine.management

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.lateinit.rightweight.databinding.ItemRoutineBinding
import com.lateinit.rightweight.ui.model.RoutineUiModel

class RoutineManagementAdapter :
    ListAdapter<RoutineUiModel, RoutineManagementAdapter.RoutineViewHolder>(diffUtil) {

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
        fun bind(routineUiModel: RoutineUiModel) {
            binding.routineUiModel = routineUiModel

            binding.imageButtonUp.setOnClickListener {
                moveUp(layoutPosition)
            }
            binding.imageButtonDown.setOnClickListener {
                moveDown(layoutPosition)
            }
            binding.cardViewRoutineItemContainer.setOnClickListener { view ->
                val action =
                    RoutineManagementFragmentDirections.actionNavigationRoutineManagementToNavigationRoutineDetail(
                        routineUiModel.routineId
                    )
                view.findNavController().navigate(action)
            }
        }
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<RoutineUiModel>() {
            override fun areItemsTheSame(oldItem: RoutineUiModel, newItem: RoutineUiModel): Boolean {
                return oldItem.routineId == newItem.routineId
            }

            override fun areContentsTheSame(oldItem: RoutineUiModel, newItem: RoutineUiModel): Boolean {
                return oldItem.routineId == newItem.routineId
            }

        }
    }
}