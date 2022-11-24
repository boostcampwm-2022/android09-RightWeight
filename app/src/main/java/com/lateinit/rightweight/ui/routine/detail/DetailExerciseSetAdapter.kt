package com.lateinit.rightweight.ui.routine.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.lateinit.rightweight.R
import com.lateinit.rightweight.databinding.ItemSetReadBinding
import com.lateinit.rightweight.ui.model.ExerciseSetUiModel

class DetailExerciseSetAdapter :
    ListAdapter<ExerciseSetUiModel, DetailExerciseSetAdapter.DetailExerciseSetViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailExerciseSetViewHolder {
        return DetailExerciseSetViewHolder(parent)
    }

    override fun onBindViewHolder(holder: DetailExerciseSetViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class DetailExerciseSetViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_set_read, parent, false)
    ) {
        private val binding = ItemSetReadBinding.bind(itemView)

        fun bind(exerciseSetUiModel: ExerciseSetUiModel) {
            binding.exerciseSetUiModel = exerciseSetUiModel
        }
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<ExerciseSetUiModel>() {
            override fun areItemsTheSame(
                oldItem: ExerciseSetUiModel,
                newItem: ExerciseSetUiModel
            ): Boolean {
                return oldItem.setId == newItem.setId
            }

            override fun areContentsTheSame(
                oldItem: ExerciseSetUiModel,
                newItem: ExerciseSetUiModel
            ): Boolean {
                return oldItem == newItem
            }

        }
    }
}