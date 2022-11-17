package com.lateinit.rightweight.ui.routine.editor

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.lateinit.rightweight.R
import com.lateinit.rightweight.data.database.entity.Set
import com.lateinit.rightweight.databinding.ItemSetBinding


class RoutineSetAdapter(val routineEventListener: RoutineDayAdapter.RoutineEventListener) :
    ListAdapter<Set, RoutineSetAdapter.SetViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SetViewHolder {
        return SetViewHolder(parent)
    }

    override fun onBindViewHolder(holder: SetViewHolder, position: Int) {
        holder.bind(getItem(position) ?: return)
    }

    inner class SetViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_set, parent, false)
    ) {
        private val binding = ItemSetBinding.bind(itemView)

        fun bind(set: Set) {
            binding.set = set
            binding.buttonSetRemove.setOnClickListener {
                routineEventListener.onSetRemove(set.exerciseId, layoutPosition)
                notifyItemRemoved(layoutPosition)
            }
        }
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<Set>() {
            override fun areItemsTheSame(oldItem: Set, newItem: Set): Boolean {
                return oldItem.setId == newItem.setId
            }

            override fun areContentsTheSame(oldItem: Set, newItem: Set): Boolean {
                return oldItem == newItem
            }

        }
    }
}