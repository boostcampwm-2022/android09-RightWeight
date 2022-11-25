package com.lateinit.rightweight.ui.exercise

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.lateinit.rightweight.R
import com.lateinit.rightweight.data.ExercisePartType
import com.lateinit.rightweight.data.database.entity.HistoryExercise
import com.lateinit.rightweight.databinding.ItemHistoryExerciseBinding
import com.lateinit.rightweight.util.getPartNameRes

class HistoryExerciseAdapter(
    val context: Context,
    private val historyEventListener: HistoryEventListener
) : ListAdapter<HistoryExercise, HistoryExerciseAdapter.HistoryExerciseViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryExerciseViewHolder {
        val exerciseParts = ExercisePartType.values().map { exercisePart ->
            context.getString(exercisePart.getPartNameRes())
        }
        val exercisePartAdapter =
            ArrayAdapter(context, R.layout.item_exercise_part, exerciseParts)

        val bind = ItemHistoryExerciseBinding.inflate(LayoutInflater.from(context), parent, false)
        return HistoryExerciseViewHolder(context, historyEventListener, exercisePartAdapter, bind)
    }

    override fun onBindViewHolder(holder: HistoryExerciseViewHolder, position: Int) {
        holder.setItem(getItem(position) ?: return)
    }

    class HistoryExerciseViewHolder(
        val context: Context,
        val historyEventListener: HistoryEventListener,
        val exercisePartAdapter: ArrayAdapter<String>,
        val bind: ItemHistoryExerciseBinding
    ) : RecyclerView.ViewHolder(
        bind.root
    ) {

        init {
            bind.textViewExercisePart.setAdapter(exercisePartAdapter)
        }

        fun setItem(historyExercise: HistoryExercise) {
            bind.historyExercise = historyExercise

            val exercisePartName = bind.root.context.getString(historyExercise.part.getPartNameRes())
            bind.textViewExercisePart.setText(exercisePartName, false)

            val historySetAdapter = HistorySetAdapter(context, historyEventListener)
            bind.recyclerViewSet.adapter = historySetAdapter
            historyEventListener.applyHistorySets(historyExercise.exerciseId, historySetAdapter)

            bind.buttonSetAdd.setOnClickListener() {
                historyEventListener.addHistorySet(historyExercise.exerciseId)
                // Flow 사용할 경우 따로 renewTodayHistory를 부를 필요가 없음
                //historyEventListener.renewTodayHistory()
            }

            bind.textViewExercisePart.setOnItemClickListener { _, _, position, _ ->
                historyExercise.part = ExercisePartType.values()[position]
                historyEventListener.updateHistoryExercise(historyExercise)
                // Flow 사용할 경우 따로 renewTodayHistory를 부를 필요가 없음
                //historyEventListener.renewTodayHistory()
            }

            bind.buttonExerciseRemove.setOnClickListener {
                historyEventListener.removeHistoryExercise(historyExercise.exerciseId)
                // Flow 사용할 경우 따로 renewTodayHistory를 부를 필요가 없음
                //historyEventListener.renewTodayHistory()
            }

            bind.editTextExerciseTitle.doAfterTextChanged {
                // two way databinding을 사용했기 때문에 historyExercise가 자동으로 변경됨
                historyEventListener.updateHistoryExercise(historyExercise)
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