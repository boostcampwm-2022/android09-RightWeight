package com.lateinit.rightweight.ui.share

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.lateinit.rightweight.data.database.entity.SharedRoutine
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
        val bind = ItemSharedRoutineBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SharedRoutineViewHolder(bind)
    }

    override fun onBindViewHolder(holder: SharedRoutineViewHolder, position: Int) {
        getItem(position)?.let {
            holder.setItem(getItem(position), sharedRoutineClickHandler)
        }
    }

    class SharedRoutineViewHolder(val bind: ItemSharedRoutineBinding) :
        RecyclerView.ViewHolder(bind.root) {
        fun setItem(item: SharedRoutineUiModel?, sharedRoutineClickHandler: SharedRoutineClickHandler) {
            bind.sharedRoutineUiModel = item
            bind.sharedRoutineClickHandler = sharedRoutineClickHandler
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