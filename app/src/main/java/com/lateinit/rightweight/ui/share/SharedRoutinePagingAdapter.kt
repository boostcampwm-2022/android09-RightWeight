package com.lateinit.rightweight.ui.share

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.lateinit.rightweight.databinding.ItemSharedRoutineBinding
import com.lateinit.rightweight.ui.model.SharedRoutineUiModel

class SharedRoutinePagingAdapter(
    private val sharedRoutineClickHandler: SharedRoutineClickHandler
) : PagingDataAdapter<SharedRoutineUiModel, SharedRoutinePagingAdapter.SharedRoutineViewHolder>(
    diffUtil
) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SharedRoutineViewHolder {
        val binding =
            ItemSharedRoutineBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SharedRoutineViewHolder(binding, sharedRoutineClickHandler)
    }

    override fun onBindViewHolder(holder: SharedRoutineViewHolder, position: Int) {
        getItem(position)?.let {
            holder.setItem(getItem(position))
        }
    }

    class SharedRoutineViewHolder(
        val binding: ItemSharedRoutineBinding,
        private val sharedRoutineClickHandler: SharedRoutineClickHandler
    ) : RecyclerView.ViewHolder(binding.root) {
        fun setItem(item: SharedRoutineUiModel?) {
            binding.sharedRoutineUiModel = item
            binding.sharedRoutineClickHandler = sharedRoutineClickHandler
        }
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<SharedRoutineUiModel>() {
            override fun areItemsTheSame(
                oldItem: SharedRoutineUiModel,
                newItem: SharedRoutineUiModel
            ): Boolean {
                return oldItem.routineId == newItem.routineId
            }

            override fun areContentsTheSame(
                oldItem: SharedRoutineUiModel,
                newItem: SharedRoutineUiModel
            ): Boolean {
                return oldItem == newItem
            }

        }
    }

}