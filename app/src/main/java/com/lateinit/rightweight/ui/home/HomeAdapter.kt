package com.lateinit.rightweight.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lateinit.rightweight.databinding.ItemExerciseViewHomeBinding
import com.lateinit.rightweight.databinding.ItemSetReadBinding
import com.lateinit.rightweight.ui.model.ParentExerciseSetUiModel
import com.lateinit.rightweight.ui.model.ParentExerciseUiModel
import kotlin.properties.Delegates

class HomeAdapter(private val exerciseUiModel: ParentExerciseUiModel) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return when (viewType) {
            VIEW_TYPE_EXERCISE -> {
                ExerciseViewHolder(
                    ItemExerciseViewHomeBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    )
                )
            }
            else -> {
                SetViewHolder(
                    ItemSetReadBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    )
                )
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ExerciseViewHolder -> {
                holder.bind(exerciseUiModel)
            }
            is SetViewHolder -> {
                holder.bind(exerciseUiModel.exerciseSets[position - 1], position)
            }
        }
    }

    override fun getItemCount(): Int {
        return if (isExpanded) exerciseUiModel.exerciseSets.size + 1 else 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) VIEW_TYPE_EXERCISE else VIEW_TYPE_SET
    }

    var isExpanded: Boolean by Delegates.observable(true) { _, _, newExpandedValue: Boolean ->
        if (newExpandedValue) {
            notifyItemRangeInserted(1, exerciseUiModel.exerciseSets.size)
            notifyItemChanged(0)
        } else {
            notifyItemRangeRemoved(1, exerciseUiModel.exerciseSets.size)
            notifyItemChanged(0)
        }
    }

    private val onExerciseClickListener = View.OnClickListener {
        isExpanded = !isExpanded
    }

    inner class ExerciseViewHolder(val binding: ItemExerciseViewHomeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(exercise: ParentExerciseUiModel) {
            binding.exercise = exercise
            binding.imageViewExpand.rotation =
                if (isExpanded) IC_EXPANDED_ROTATION_DEG else IC_COLLAPSED_ROTATION_DEG
            binding.layoutExerciseItem.setOnClickListener(onExerciseClickListener)
        }
    }

    inner class SetViewHolder(val binding: ItemSetReadBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(exerciseSetUiModel: ParentExerciseSetUiModel, position: Int) {
            binding.exerciseSetUiModel = exerciseSetUiModel
            binding.position = position.toString()
        }
    }

    companion object {
        private const val VIEW_TYPE_EXERCISE = 0
        private const val VIEW_TYPE_SET = 1

        private const val IC_EXPANDED_ROTATION_DEG = 0F
        private const val IC_COLLAPSED_ROTATION_DEG = 180F

    }

}