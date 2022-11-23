package com.lateinit.rightweight.ui.routine.editor

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.lateinit.rightweight.R
import com.lateinit.rightweight.data.ExercisePartType
import com.lateinit.rightweight.data.database.entity.Exercise
import com.lateinit.rightweight.databinding.ItemExerciseBinding
import com.lateinit.rightweight.util.getPartNameRes

class RoutineExerciseAdapter(
    private val exerciseEventListener: ExerciseEventListener
) : ListAdapter<Exercise, RoutineExerciseAdapter.ExerciseViewHolder>(diffUtil) {

    interface ExerciseEventListener {

        fun onExerciseRemove(dayId: String, position: Int)

        fun onExercisePartChange(dayId: String, position: Int, exercisePartType: ExercisePartType)

        fun onSetAdd(exerciseId: String)

        fun onSetRemove(exerciseId: String, position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
        return with(parent) {
            val exerciseParts = ExercisePartType.values().map { exercisePart ->
                context.getString(exercisePart.getPartNameRes())
            }
            val exercisePartAdapter =
                ArrayAdapter(context, R.layout.item_exercise_part, exerciseParts)
            ExerciseViewHolder(this, exercisePartAdapter, exerciseEventListener)
        }
    }

    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        holder.bind(getItem(position) ?: return)
    }

    class ExerciseViewHolder(
        parent: ViewGroup,
        exercisePartAdapter: ArrayAdapter<String>,
        exerciseEventListener: ExerciseEventListener
    ) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_exercise, parent, false)
    ) {
        private val binding = ItemExerciseBinding.bind(itemView)
        private val routineSetAdapter = RoutineSetAdapter(exerciseEventListener)

        private lateinit var exercise: Exercise

        init {
            binding.textViewExercisePart.setAdapter(exercisePartAdapter)

            binding.textViewExercisePart.setOnItemClickListener { _, _, position, _ ->
                exerciseEventListener.onExercisePartChange(
                    exercise.dayId,
                    layoutPosition,
                    ExercisePartType.values()[position]
                )
            }

            binding.buttonExerciseRemove.setOnClickListener {
                exerciseEventListener.onExerciseRemove(exercise.dayId, layoutPosition)
            }

            binding.buttonSetAdd.setOnClickListener {
                exerciseEventListener.onSetAdd(exercise.exerciseId)
            }
        }

        fun bind(exercise: Exercise) {
            this.exercise = exercise
            binding.exercise = exercise

            val exercisePartName = binding.root.context.getString(exercise.part.getPartNameRes())
            binding.textViewExercisePart.setText(exercisePartName, false)

            binding.recyclerViewSet.adapter = routineSetAdapter
            routineSetAdapter.submitList(exercise.exerciseSets)
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