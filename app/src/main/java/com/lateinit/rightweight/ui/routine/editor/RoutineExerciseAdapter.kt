package com.lateinit.rightweight.ui.routine.editor

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.lateinit.rightweight.R
import com.lateinit.rightweight.data.database.entity.Exercise
import com.lateinit.rightweight.databinding.ItemExerciseBinding

class RoutineExerciseAdapter(
    val routineEventListener: RoutineDayAdapter.RoutineEventListener
) : ListAdapter<Exercise, RoutineExerciseAdapter.ExerciseViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
        return ExerciseViewHolder(parent)
    }

    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        holder.bind(getItem(position) ?: return)
    }

    inner class ExerciseViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_exercise, parent, false)
    ) {
        private val binding = ItemExerciseBinding.bind(itemView)
        private val routineSetAdapter = RoutineSetAdapter(routineEventListener)

        fun bind(exercise: Exercise) {

            binding.exercise = exercise

            binding.recyclerViewSet.adapter = routineSetAdapter
            routineSetAdapter.submitList(exercise.exerciseSets)

            binding.buttonExerciseRemove.setOnClickListener {
                routineEventListener.onExerciseRemove(exercise.dayId, layoutPosition)
            }
            binding.buttonSetAdd.setOnClickListener {
                routineEventListener.onSetAdd(exercise.exerciseId)
            }
        }
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<Exercise>() {
            override fun areItemsTheSame(oldItem: Exercise, newItem: Exercise): Boolean {
                return oldItem.exerciseId == newItem.exerciseId
            }

            override fun areContentsTheSame(oldItem: Exercise, newItem: Exercise): Boolean {
                return oldItem == newItem
            }

        }
    }
}