package com.lateinit.rightweight.ui.routine.editor

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.lateinit.rightweight.R
import com.lateinit.rightweight.data.ExercisePartType
import com.lateinit.rightweight.data.database.entity.Exercise
import com.lateinit.rightweight.databinding.ItemExerciseBinding

class RoutineExerciseAdapter(
    val exerciseEventListener: ExerciseEventListener
) : ListAdapter<Exercise, RoutineExerciseAdapter.ExerciseViewHolder>(diffUtil) {

    interface ExerciseEventListener {
        fun onExerciseAdd(position: Int)

        fun onExerciseRemove(dayId: String, position: Int)

        fun onExercisePartChange(dayId: String, position: Int, exercisePartType: ExercisePartType)

        fun onSetAdd(exerciseId: String)

        fun onSetRemove(exerciseId: String, position: Int)
    }

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
        private val routineSetAdapter = RoutineSetAdapter(exerciseEventListener)

        fun bind(exercise: Exercise) {

            binding.exercise = exercise

            binding.recyclerViewSet.adapter = routineSetAdapter
            routineSetAdapter.submitList(exercise.exerciseSets)

            binding.buttonExerciseRemove.setOnClickListener {
                exerciseEventListener.onExerciseRemove(exercise.dayId, layoutPosition)
            }
            binding.buttonSetAdd.setOnClickListener {
                exerciseEventListener.onSetAdd(exercise.exerciseId)
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