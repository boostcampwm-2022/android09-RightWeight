package com.lateinit.rightweight.ui.routine.management

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.lateinit.rightweight.databinding.ItemRoutineBinding
import com.lateinit.rightweight.ui.model.RoutineUiModel

class RoutineAdapter(
    private val routineEventListener: RoutineEventListener
) : ListAdapter<RoutineUiModel, RoutineAdapter.RoutineViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoutineViewHolder {
        return RoutineViewHolder(
            ItemRoutineBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            routineEventListener
        )
    }

    override fun onBindViewHolder(holder: RoutineViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class RoutineViewHolder(
        private val binding: ItemRoutineBinding,
        private val routineEventListener: RoutineEventListener
    ) : RecyclerView.ViewHolder(binding.root) {

        private lateinit var routineUiModel: RoutineUiModel

        init {
            binding.imageButtonUp.setOnClickListener {
                routineEventListener.moveUp(layoutPosition)
            }
            binding.imageButtonDown.setOnClickListener {
                routineEventListener.moveDown(layoutPosition)
            }
            binding.cardViewRoutineItemContainer.setOnClickListener {
                routineEventListener.onClick(routineUiModel.routineId)
            }
        }

        fun bind(routineUiModel: RoutineUiModel) {
            this.routineUiModel = routineUiModel
            binding.routineUiModel = routineUiModel
        }
    }

    interface RoutineEventListener {

        fun moveUp(routinePosition: Int)

        fun moveDown(routinePosition: Int)

        fun onClick(routineId: String)
    }

    companion object {
        private val diffUtil = object : DiffUtil.ItemCallback<RoutineUiModel>() {
            override fun areItemsTheSame(oldItem: RoutineUiModel, newItem: RoutineUiModel): Boolean {
                return oldItem.routineId == newItem.routineId
            }

            override fun areContentsTheSame(oldItem: RoutineUiModel, newItem: RoutineUiModel): Boolean {
                return oldItem == newItem
            }
        }
    }
}