package com.lateinit.rightweight.ui.routine.editor


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.lateinit.rightweight.R
import com.lateinit.rightweight.data.ExercisePartType
import com.lateinit.rightweight.data.database.entity.Day
import com.lateinit.rightweight.databinding.ItemDayBinding

class RoutineDayAdapter(
    val routineEventListener: RoutineEventListener
) : ListAdapter<Day, RoutineDayAdapter.DayViewHolder>(diffUtil) {

    interface RoutineEventListener {

        fun onDayAdd()

        fun onDayRemove(position: Int)

        fun onDayMoveUp(position: Int)

        fun onDayMoveDown(position: Int)

        fun onExerciseAdd(position: Int)

        fun onExerciseRemove(dayId: String, position: Int)

        fun onExercisePartChange(dayId: String, position: Int, exercisePartType: ExercisePartType)

        fun onSetAdd(exerciseId: String)

        fun onSetRemove(exerciseId: String, position: Int)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DayViewHolder {
        return DayViewHolder(parent)
    }

    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        holder.bind(getItem(position) ?: return)
    }

    inner class DayViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_day, parent, false)
    ) {
        private val binding = ItemDayBinding.bind(itemView)
        private val routineExerciseAdapter = RoutineExerciseAdapter(routineEventListener)

        init {
            binding.buttonDayMoveUp.setOnClickListener {
                routineEventListener.onDayMoveUp(layoutPosition)
            }

            binding.buttonDayMoveDown.setOnClickListener {
                routineEventListener.onDayMoveDown(layoutPosition)
            }

            binding.buttonExerciseAdd.setOnClickListener {
                routineEventListener.onExerciseAdd(layoutPosition)
            }

            binding.buttonRemoveDay.setOnClickListener {
                routineEventListener.onDayRemove(layoutPosition)
            }
        }

        fun bind(day: Day) {
            binding.day = day
            binding.recyclerViewExercise.adapter = routineExerciseAdapter
            routineExerciseAdapter.submitList(day.exercises)
        }

    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<Day>() {
            override fun areItemsTheSame(oldItem: Day, newItem: Day): Boolean {
                return oldItem.dayId == newItem.dayId
            }

            override fun areContentsTheSame(oldItem: Day, newItem: Day): Boolean {
                return oldItem == newItem
            }

        }
    }

}