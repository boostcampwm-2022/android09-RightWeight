package com.lateinit.rightweight.ui.share

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.lateinit.rightweight.data.database.entity.SharedRoutine
import com.lateinit.rightweight.databinding.ItemSharedRoutineBinding

class SharedRoutinePagingAdapter(
    private val context: Context,
    val sharedRoutineClickHandler: SharedRoutineClickHandler
) : PagingDataAdapter<SharedRoutine, SharedRoutinePagingAdapter.SharedRoutineViewHolder>(
    diffUtil
) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SharedRoutineViewHolder {
        val bind = ItemSharedRoutineBinding.inflate(LayoutInflater.from(context), parent, false)
        return SharedRoutineViewHolder(bind)
    }

    override fun onBindViewHolder(holder: SharedRoutineViewHolder, position: Int) {
        getItem(position)?.let {
            holder.setItem(getItem(position), sharedRoutineClickHandler)
        }
    }

    class SharedRoutineViewHolder(val bind: ItemSharedRoutineBinding) :
        RecyclerView.ViewHolder(bind.root) {
        fun setItem(item: SharedRoutine?, sharedRoutineClickHandler: SharedRoutineClickHandler) {
            bind.sharedRoutine = item
            bind.sharedRoutineClickHandler = sharedRoutineClickHandler
        }
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<SharedRoutine>() {
            override fun areItemsTheSame(
                oldItem: SharedRoutine,
                newItem: SharedRoutine
            ): Boolean {
                return oldItem.routineId == newItem.routineId
            }

            override fun areContentsTheSame(
                oldItem: SharedRoutine,
                newItem: SharedRoutine
            ): Boolean {
                return oldItem == newItem
            }

        }
    }

}