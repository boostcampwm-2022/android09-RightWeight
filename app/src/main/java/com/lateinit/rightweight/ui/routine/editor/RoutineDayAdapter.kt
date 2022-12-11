package com.lateinit.rightweight.ui.routine.editor


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.lateinit.rightweight.R
import com.lateinit.rightweight.databinding.ItemDayBinding
import com.lateinit.rightweight.ui.model.routine.DayUiModel

class RoutineDayAdapter(
    private val onClickDay: (Int) -> Unit
) : ListAdapter<DayUiModel, RoutineDayAdapter.DayViewHolder>(diffUtil) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DayViewHolder {
        return DayViewHolder(parent)
    }

    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class DayViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_day, parent, false)
    ) {
        private val binding = ItemDayBinding.bind(itemView)

        init {
            itemView.setOnClickListener {
                onClickDay(layoutPosition)
            }
        }

        fun bind(dayUiModel: DayUiModel) {
            binding.dayUiModel = dayUiModel
            binding.executePendingBindings()
        }

    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<DayUiModel>() {
            override fun areItemsTheSame(oldItem: DayUiModel, newItem: DayUiModel): Boolean {
                return oldItem.dayId == newItem.dayId
            }

            override fun areContentsTheSame(oldItem: DayUiModel, newItem: DayUiModel): Boolean {
                return oldItem == newItem
            }

        }
    }

}