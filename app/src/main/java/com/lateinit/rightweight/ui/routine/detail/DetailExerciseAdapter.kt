package com.lateinit.rightweight.ui.routine.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.lateinit.rightweight.R
import com.lateinit.rightweight.databinding.ItemExerciseWithSetsBinding
import com.lateinit.rightweight.ui.model.ExerciseUiModel

class DetailExerciseAdapter(
    private val onClickExercise: (Int) -> Unit
) :
    ListAdapter<ExerciseUiModel, DetailExerciseAdapter.DetailExerciseViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailExerciseViewHolder {
        return DetailExerciseViewHolder(parent, onClickExercise)
    }

    override fun onBindViewHolder(holder: DetailExerciseViewHolder, position: Int) {
        holder.bind(getItem(position) ?: return)
    }

    class DetailExerciseViewHolder(
        parent: ViewGroup,
        onClickExercise: (Int) -> Unit
    ) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_exercise_with_sets, parent, false)
    ) {
        private val binding = ItemExerciseWithSetsBinding.bind(itemView)

        private lateinit var exerciseUiModel: ExerciseUiModel
        private val detailExerciseSetAdapter = DetailExerciseSetAdapter()

        init {
            itemView.setOnClickListener {
                if (exerciseUiModel.expanded) {
                    binding.imageExpandedState.animate().setDuration(200).rotation(0f)
                } else {
                    binding.imageExpandedState.animate().setDuration(200).rotation(180f)
                }
                onClickExercise(layoutPosition)
            }
        }

        fun bind(exerciseUiModel: ExerciseUiModel) {
            this.exerciseUiModel = exerciseUiModel
            binding.exerciseUiModel = exerciseUiModel

            binding.recyclerViewSet.adapter = detailExerciseSetAdapter
            val exerciseSets = exerciseUiModel.exerciseSets
            detailExerciseSetAdapter.submitList(exerciseSets)
        }
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<ExerciseUiModel>() {
            override fun areItemsTheSame(
                oldItem: ExerciseUiModel,
                newItem: ExerciseUiModel
            ): Boolean {
                return oldItem.exerciseId == newItem.exerciseId
            }

            override fun areContentsTheSame(
                oldItem: ExerciseUiModel,
                newItem: ExerciseUiModel
            ): Boolean {
                println()
                return false
            }
        }
    }
}