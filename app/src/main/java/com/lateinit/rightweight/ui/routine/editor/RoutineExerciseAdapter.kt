package com.lateinit.rightweight.ui.routine.editor

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.lateinit.rightweight.R
import com.lateinit.rightweight.databinding.ItemExerciseBinding
import com.lateinit.rightweight.ui.model.routine.ExercisePartTypeUiModel
import com.lateinit.rightweight.ui.model.routine.ExerciseUiModel

class RoutineExerciseAdapter(
    private val exercisePartAdapter: ArrayAdapter<String>,
    private val exerciseEventListener: ExerciseEventListener
) : ListAdapter<ExerciseUiModel, RoutineExerciseAdapter.ExerciseViewHolder>(diffUtil) {

    interface ExerciseEventListener {

        fun onExerciseRemove(dayId: String, position: Int)

        fun onExercisePartChange(dayId: String, position: Int, exercisePartType: ExercisePartTypeUiModel)

        fun onSetAdd(exerciseId: String)

        fun onSetRemove(exerciseId: String, position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
        return ExerciseViewHolder(parent, exercisePartAdapter, exerciseEventListener)
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

        private lateinit var exerciseUiModel: ExerciseUiModel

        init {
            binding.textViewExercisePart.setAdapter(exercisePartAdapter)

            binding.textViewExercisePart.setOnItemClickListener { _, _, position, _ ->
                exerciseEventListener.onExercisePartChange(
                    exerciseUiModel.dayId,
                    layoutPosition,
                    ExercisePartTypeUiModel.values()[position]
                )
            }

            binding.buttonExerciseRemove.setOnClickListener {
                exerciseEventListener.onExerciseRemove(exerciseUiModel.dayId, layoutPosition)
            }

            binding.buttonSetAdd.setOnClickListener {
                exerciseEventListener.onSetAdd(exerciseUiModel.exerciseId)
            }
        }

        fun bind(exerciseUiModel: ExerciseUiModel) {
            this.exerciseUiModel = exerciseUiModel
            binding.exerciseUiModel = exerciseUiModel

            val exercisePartName = binding.root.context.getString(exerciseUiModel.part.partName)
            binding.textViewExercisePart.setText(exercisePartName, false)

            binding.recyclerViewSet.adapter = routineSetAdapter
            routineSetAdapter.submitList(exerciseUiModel.exerciseSets)
        }
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<ExerciseUiModel>() {
            override fun areItemsTheSame(oldItem: ExerciseUiModel, newItem: ExerciseUiModel): Boolean {
                return oldItem.exerciseId == newItem.exerciseId
            }

            override fun areContentsTheSame(oldItem: ExerciseUiModel, newItem: ExerciseUiModel): Boolean {
                return oldItem == newItem
            }
        }
    }
}