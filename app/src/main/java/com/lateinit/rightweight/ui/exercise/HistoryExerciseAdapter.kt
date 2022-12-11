package com.lateinit.rightweight.ui.exercise

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.lateinit.rightweight.databinding.ItemExerciseBinding
import com.lateinit.rightweight.ui.model.routine.ExercisePartTypeUiModel
import com.lateinit.rightweight.ui.model.history.HistoryExerciseUiModel

class HistoryExerciseAdapter(
    private val exercisePartAdapter: ArrayAdapter<String>,
    private val historyEventListener: HistoryEventListener
) : ListAdapter<HistoryExerciseUiModel, HistoryExerciseAdapter.HistoryExerciseViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryExerciseViewHolder {
        return HistoryExerciseViewHolder(
            exercisePartAdapter,
            historyEventListener,
            ItemExerciseBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: HistoryExerciseViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class HistoryExerciseViewHolder(
        exercisePartAdapter: ArrayAdapter<String>,
        historyEventListener: HistoryEventListener,
        private val binding: ItemExerciseBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private val historySetAdapter = HistorySetAdapter(historyEventListener)
        private lateinit var historyExerciseUiModel: HistoryExerciseUiModel

        init {
            binding.textViewExercisePart.setAdapter(exercisePartAdapter)

            binding.buttonSetAdd.setOnClickListener {
                historyEventListener.addHistorySet(historyExerciseUiModel.exerciseId)
            }

            binding.textViewExercisePart.setOnItemClickListener { _, _, position, _ ->
                historyEventListener.updateHistoryExercise(
                    historyExerciseUiModel.copy(part = ExercisePartTypeUiModel.values()[position])
                )
            }

            binding.buttonExerciseRemove.setOnClickListener {
                historyEventListener.removeHistoryExercise(historyExerciseUiModel.exerciseId)
            }

            // doAfterTextChanged 사용시 한 글자 입력 시마다 Flow로 인해 exercise 데이터를 갱신하므로 정상적인 입력을 하기 어렵다.
            // setOnFocusChangeListener를 사용하여 focus가 해제되었을 때 데이터를 갱신한다.
            binding.editTextExerciseTitle.setOnFocusChangeListener { _, _ ->
                historyEventListener.updateHistoryExercise(historyExerciseUiModel)
            }
        }

        fun bind(historyExerciseUiModel: HistoryExerciseUiModel) {
            this.historyExerciseUiModel = historyExerciseUiModel
            binding.exerciseUiModel = historyExerciseUiModel

            val exercisePartName =
                binding.root.context.getString(historyExerciseUiModel.part.partName)
            binding.textViewExercisePart.setText(exercisePartName, false)

            binding.recyclerViewSet.apply {
                adapter = historySetAdapter
                itemAnimator = null
            }
            historySetAdapter.submitList(historyExerciseUiModel.exerciseSets)

            binding.executePendingBindings()
        }
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<HistoryExerciseUiModel>() {
            override fun areItemsTheSame(
                oldItem: HistoryExerciseUiModel,
                newItem: HistoryExerciseUiModel
            ): Boolean {
                return oldItem.exerciseId == newItem.exerciseId
            }

            override fun areContentsTheSame(
                oldItem: HistoryExerciseUiModel,
                newItem: HistoryExerciseUiModel
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}