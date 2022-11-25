package com.lateinit.rightweight.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lateinit.rightweight.databinding.ItemExerciseViewHomeBinding
import com.lateinit.rightweight.databinding.ItemSetReadBinding
import com.lateinit.rightweight.ui.model.ExerciseSetUiModel
import com.lateinit.rightweight.ui.model.ExerciseUiModel

class HomeAdapter(private val exerciseUiModel: ExerciseUiModel) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return when (viewType) {
            VIEW_TYPE_EXERCISE -> {
                ExerciseViewHolder(
                    ItemExerciseViewHomeBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    )
                )
            }
            else -> {
                SetViewHolder(
                    ItemSetReadBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    )
                )
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ExerciseViewHolder -> {
                holder.bind(exerciseUiModel)
            }
            is SetViewHolder -> {
                holder.bind(exerciseUiModel.exerciseSets[position - 1])
            }
        }
    }

    override fun getItemCount(): Int {
        return exerciseUiModel.exerciseSets.size + 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) VIEW_TYPE_EXERCISE else VIEW_TYPE_SET
    }

    inner class ExerciseViewHolder(val binding: ItemExerciseViewHomeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(exercise: ExerciseUiModel) {
            binding.exercise = exercise
        }
    }

    inner class SetViewHolder(val binding: ItemSetReadBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(exerciseSetUiModel: ExerciseSetUiModel) {
            binding.exerciseSetUiModel = exerciseSetUiModel
        }
    }

    companion object {
        private const val VIEW_TYPE_EXERCISE = 0
        private const val VIEW_TYPE_SET = 1
    }

}