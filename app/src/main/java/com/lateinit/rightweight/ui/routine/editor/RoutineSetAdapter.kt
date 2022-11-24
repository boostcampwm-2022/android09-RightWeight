package com.lateinit.rightweight.ui.routine.editor

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.lateinit.rightweight.R
import com.lateinit.rightweight.databinding.ItemSetBinding
import com.lateinit.rightweight.ui.model.ExerciseSetUiModel

class RoutineSetAdapter(val exerciseEventListener: RoutineExerciseAdapter.ExerciseEventListener) :
    ListAdapter<ExerciseSetUiModel, RoutineSetAdapter.SetViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SetViewHolder {
        return SetViewHolder(parent)
    }

    override fun onBindViewHolder(holder: SetViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class SetViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_set, parent, false)
    ) {
        private val binding = ItemSetBinding.bind(itemView)

        fun bind(exerciseSetUiModel: ExerciseSetUiModel) {
            binding.exerciseSetUiModel = exerciseSetUiModel
            binding.buttonSetRemove.setOnClickListener {
                exerciseEventListener.onSetRemove(exerciseSetUiModel.exerciseId, layoutPosition)
            }
        }
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<ExerciseSetUiModel>() {
            override fun areItemsTheSame(oldItem: ExerciseSetUiModel, newItem: ExerciseSetUiModel): Boolean {
                return oldItem.setId == newItem.setId
            }

            override fun areContentsTheSame(oldItem: ExerciseSetUiModel, newItem: ExerciseSetUiModel): Boolean {
                return oldItem == newItem
            }

        }
    }
}