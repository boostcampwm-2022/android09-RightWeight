package com.lateinit.rightweight.ui.exercise

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.lateinit.rightweight.databinding.ItemHistorySetBinding
import com.lateinit.rightweight.ui.model.history.HistoryExerciseSetUiModel

class HistorySetAdapter(
    private val historyEventListener: HistoryEventListener
) : ListAdapter<HistoryExerciseSetUiModel, HistorySetAdapter.HistorySetViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistorySetViewHolder {
        return HistorySetViewHolder(
            historyEventListener,
            ItemHistorySetBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: HistorySetViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class HistorySetViewHolder(
        historyEventListener: HistoryEventListener,
        private val binding: ItemHistorySetBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private lateinit var historyExerciseSetUiModel: HistoryExerciseSetUiModel

        init {
            binding.checkboxSet.setOnCheckedChangeListener { _, isChecked ->
                if (historyExerciseSetUiModel.checked != isChecked) {
                    historyEventListener.updateHistorySet(
                        historyExerciseSetUiModel.copy(checked = isChecked)
                    )
                }
            }

            binding.editTextSetWeight.setOnFocusChangeListener { _, _ ->
                historyEventListener.updateHistorySet(historyExerciseSetUiModel)
            }

            binding.editTextSetCount.setOnFocusChangeListener { _, _ ->
                historyEventListener.updateHistorySet(historyExerciseSetUiModel)
            }

            binding.buttonSetRemove.setOnClickListener {
                historyEventListener.removeHistorySet(historyExerciseSetUiModel.setId)
            }
        }

        fun bind(historyExerciseSetUiModel: HistoryExerciseSetUiModel) {
            this.historyExerciseSetUiModel = historyExerciseSetUiModel
            binding.historyExerciseSetUiModel = historyExerciseSetUiModel
            binding.executePendingBindings()
        }
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<HistoryExerciseSetUiModel>() {
            override fun areItemsTheSame(
                oldItem: HistoryExerciseSetUiModel,
                newItem: HistoryExerciseSetUiModel
            ): Boolean {
                return oldItem.setId == newItem.setId
            }

            override fun areContentsTheSame(
                oldItem: HistoryExerciseSetUiModel,
                newItem: HistoryExerciseSetUiModel
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}