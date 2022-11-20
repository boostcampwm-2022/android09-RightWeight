package com.lateinit.rightweight.ui.routine.editor

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.lateinit.rightweight.R
import com.lateinit.rightweight.data.database.entity.ExerciseSet
import com.lateinit.rightweight.databinding.ItemSetBinding


class RoutineSetAdapter(val routineEventListener: RoutineDayAdapter.RoutineEventListener) :
    ListAdapter<ExerciseSet, RoutineSetAdapter.SetViewHolder>(diffUtil) {

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

        fun bind(exerciseSet: ExerciseSet) {
            binding.set = exerciseSet
            binding.buttonSetRemove.setOnClickListener {
                routineEventListener.onSetRemove(exerciseSet.exerciseId, layoutPosition)
            }
        }
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<ExerciseSet>() {
            override fun areItemsTheSame(oldItem: ExerciseSet, newItem: ExerciseSet): Boolean {
                return oldItem.setId == newItem.setId
            }

            override fun areContentsTheSame(oldItem: ExerciseSet, newItem: ExerciseSet): Boolean {
                return oldItem == newItem
            }

        }
    }
}