package com.lateinit.rightweight.ui.routine.editor


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.lateinit.rightweight.R
import com.lateinit.rightweight.data.database.entity.Day
import com.lateinit.rightweight.databinding.ItemDayBinding

class RoutineDayAdapter(
    val routineEventListener: RoutineEventListener
) : ListAdapter<Day, RoutineDayAdapter.DayViewHolder>(diffUtil) {

    private var lastPosition = -1
    private var selectedPosition = 0

    interface RoutineEventListener {

        fun onDayMoveUp(position: Int)

        fun onDayMoveDown(position: Int)

        fun onDayClick(position: Int)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DayViewHolder {
        return DayViewHolder(parent)
    }

    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        holder.bind(getItem(position))
        if (selectedPosition == position) {
            holder.itemView.apply {
                background = getDrawable(context, R.drawable.bg_day_order_seleted)
            }
        } else {
            holder.itemView.apply {
                background = getDrawable(context, R.drawable.bg_day_order)
            }
        }
    }

    inner class DayViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_day, parent, false)
    ) {
        private val binding = ItemDayBinding.bind(itemView)

        fun bind(day: Day) {
            binding.day = day

            itemView.setOnClickListener {
                routineEventListener.onDayClick(layoutPosition)
                lastPosition = selectedPosition
                selectedPosition = layoutPosition
                notifyItemChanged(lastPosition)
                notifyItemChanged(selectedPosition)
            }
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