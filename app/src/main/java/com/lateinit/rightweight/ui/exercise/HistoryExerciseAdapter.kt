package com.lateinit.rightweight.ui.exercise

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.lateinit.rightweight.R
import com.lateinit.rightweight.data.ExercisePartType
import com.lateinit.rightweight.data.database.entity.HistoryExercise
import com.lateinit.rightweight.databinding.ItemHistoryExerciseBinding

class HistoryExerciseAdapter(
    val context: Context,
    private val historyEventListener: HistoryEventListener
) : ListAdapter<HistoryExercise, HistoryExerciseAdapter.HistoryExerciseViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryExerciseViewHolder {
//            val exerciseParts = ExercisePartType.values().map { exercisePart ->
//                context.getString(exercisePart.partName)
//            }
//            val exercisePartAdapter =
//                ArrayAdapter(context, R.layout.item_exercise_part, exerciseParts)

        val bind = ItemHistoryExerciseBinding.inflate(LayoutInflater.from(context), parent, false)
        return HistoryExerciseViewHolder(context, historyEventListener, bind)
    }

    override fun onBindViewHolder(holder: HistoryExerciseViewHolder, position: Int) {
        holder.setItem(getItem(position) ?: return)
    }

    class HistoryExerciseViewHolder(
        val context: Context,
        val historyEventListener: HistoryEventListener,
        val bind: ItemHistoryExerciseBinding
    ) : RecyclerView.ViewHolder(
        bind.root
    ) {

        init {
//            bind.textViewExercisePart.setAdapter(exercisePartAdapter)
//
//            bind.textViewExercisePart.setOnItemClickListener { _, _, position, _ ->
//                routineEventListener.onExercisePartChange(
//                    exercise.dayId,
//                    layoutPosition,
//                    ExercisePartType.values()[position]
//                )
//            }
//
//            bind.buttonExerciseRemove.setOnClickListener {
//                routineEventListener.onExerciseRemove(exercise.dayId, layoutPosition)
//            }
//
//            bind.buttonSetAdd.setOnClickListener {
//                routineEventListener.onSetAdd(exercise.exerciseId)
//            }
        }

        fun setItem(historyExercise: HistoryExercise) {
            bind.historyExercise = historyExercise

            val exercisePartName = bind.root.context.getString(historyExercise.part.partName)
            bind.textViewExercisePart.setText(exercisePartName, false)

            val historySetAdapter = HistorySetAdapter(context, historyEventListener)
            bind.recyclerViewSet.adapter = historySetAdapter
            historyEventListener.applyHistorySets(historyExercise.exerciseId, historySetAdapter)

            bind.buttonSetAdd.setOnClickListener(){
                historyEventListener.addHistorySet(historyExercise.exerciseId)
                historyEventListener.renewTodayHistory()
            }
        }
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<HistoryExercise>() {
            override fun areItemsTheSame(
                oldItem: HistoryExercise,
                newItem: HistoryExercise
            ): Boolean {
                return oldItem.exerciseId == newItem.exerciseId
            }

            override fun areContentsTheSame(
                oldItem: HistoryExercise,
                newItem: HistoryExercise
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}