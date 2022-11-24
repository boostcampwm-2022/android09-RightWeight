package com.lateinit.rightweight.ui.routine.detail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.lateinit.rightweight.R
import com.lateinit.rightweight.databinding.ItemExerciseWithSetsBinding
import com.lateinit.rightweight.ui.model.ExerciseUiModel

class DetailExerciseAdapter :
    ListAdapter<ExerciseUiModel, DetailExerciseAdapter.DetailExerciseViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailExerciseViewHolder {
        return DetailExerciseViewHolder(parent)
    }

    override fun onBindViewHolder(holder: DetailExerciseViewHolder, position: Int) {
        holder.bind(getItem(position) ?: return)
    }

    class DetailExerciseViewHolder(
        parent: ViewGroup,
    ) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_exercise_with_sets, parent, false)
    ) {
        private val binding = ItemExerciseWithSetsBinding.bind(itemView)

        private lateinit var exerciseUiModel: ExerciseUiModel
        private val detailExerciseSetAdapter = DetailExerciseSetAdapter()

        init {
            itemView.setOnClickListener {
                if (binding.layoutExpand.visibility == View.VISIBLE) {
                    binding.layoutExpand.visibility = View.GONE
                    binding.imageExpandedState.animate().setDuration(200).rotation(180f)
                } else {
                    binding.layoutExpand.visibility = View.VISIBLE
                    binding.imageExpandedState.animate().setDuration(200).rotation(180f)
                }
            }
        }

        fun bind(exerciseUiModel: ExerciseUiModel) {
            this.exerciseUiModel = exerciseUiModel
            binding.exerciseUiModel = exerciseUiModel

            binding.recyclerViewSet.adapter = detailExerciseSetAdapter
            detailExerciseSetAdapter.submitList(exerciseUiModel.exerciseSets)
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
                return oldItem == newItem
            }
        }
    }
}