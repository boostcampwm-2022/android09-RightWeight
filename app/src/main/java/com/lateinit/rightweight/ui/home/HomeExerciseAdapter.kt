package com.lateinit.rightweight.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.lateinit.rightweight.databinding.ItemExerciseViewHomeBinding
import com.lateinit.rightweight.ui.model.ExerciseUiModel

class HomeExerciseAdapter : ListAdapter<ExerciseUiModel, HomeExerciseAdapter.ExerciseViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
        println("onCreateViewHolder")
        return ExerciseViewHolder(
            ItemExerciseViewHomeBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        println("onBindViewHolder")
        getItem(position)?.let {
            holder.bind(it)
        }
    }

    inner class ExerciseViewHolder(val binding: ItemExerciseViewHomeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(exercise: ExerciseUiModel) {
            println("bind")
            binding.exercise = exercise
        }
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<ExerciseUiModel>() {
            override fun areItemsTheSame(oldItem: ExerciseUiModel, newItem: ExerciseUiModel): Boolean {
                return oldItem.exerciseId == newItem.exerciseId
            }

            override fun areContentsTheSame(oldItem: ExerciseUiModel, newItem: ExerciseUiModel): Boolean {
                return oldItem.exerciseId == newItem.exerciseId
            }

        }
    }
}