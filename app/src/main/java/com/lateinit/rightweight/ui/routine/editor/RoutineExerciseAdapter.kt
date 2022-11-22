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

class RoutineExerciseAdapter(
    private val routineEventListener: RoutineDayAdapter.RoutineEventListener
) : ListAdapter<Exercise, RoutineExerciseAdapter.ExerciseViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
        return with(parent) {
            val exerciseParts = ExercisePartType.values().map { exercisePart ->
                context.getString(exercisePart.partName)
            }
            val exercisePartAdapter =
                ArrayAdapter(context, R.layout.item_exercise_part, exerciseParts)
            ExerciseViewHolder(this, exercisePartAdapter, routineEventListener)
        }
    }

    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        holder.bind(getItem(position) ?: return)
    }

    class ExerciseViewHolder(
        parent: ViewGroup,
        exercisePartAdapter: ArrayAdapter<String>,
        routineEventListener: RoutineDayAdapter.RoutineEventListener
    ) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_exercise, parent, false)
    ) {
        private val binding = ItemExerciseBinding.bind(itemView)
        private val routineSetAdapter = RoutineSetAdapter(routineEventListener)

        private lateinit var exercise: Exercise

        init {
            binding.textViewExercisePart.setAdapter(exercisePartAdapter)

            binding.textViewExercisePart.setOnItemClickListener { _, _, position, _ ->
                routineEventListener.onExercisePartChange(
                    exercise.dayId,
                    layoutPosition,
                    ExercisePartType.values()[position]
                )
            }

            binding.buttonExerciseRemove.setOnClickListener {
                routineEventListener.onExerciseRemove(exercise.dayId, layoutPosition)
            }

            binding.buttonSetAdd.setOnClickListener {
                routineEventListener.onSetAdd(exercise.exerciseId)
            }
        }

        fun bind(exercise: Exercise) {
            this.exercise = exercise
            binding.exercise = exercise

            val exercisePartName = binding.root.context.getString(exercise.part.partName)
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