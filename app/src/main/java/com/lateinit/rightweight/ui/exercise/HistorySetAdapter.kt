package com.lateinit.rightweight.ui.exercise

import android.content.Context
import android.text.Layout
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.lateinit.rightweight.R
import com.lateinit.rightweight.data.database.entity.HistorySet
import com.lateinit.rightweight.databinding.ItemHistorySetBinding

class HistorySetAdapter(
    val context: Context,
    val historyEventListener: HistoryEventListener
    ) :
    ListAdapter<HistorySet, HistorySetAdapter.HistorySetViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistorySetViewHolder {
        val bind = ItemHistorySetBinding.inflate(LayoutInflater.from(context), parent, false)
        return HistorySetViewHolder(bind)
    }

    override fun onBindViewHolder(holder: HistorySetViewHolder, position: Int) {
        holder.setItem(getItem(position) ?: return)
    }

    inner class HistorySetViewHolder(val bind: ItemHistorySetBinding) : RecyclerView.ViewHolder(
        bind.root
    ) {

        fun setItem(historySet: HistorySet) {
            bind.historySet = historySet
            bind.checkboxSet.setOnCheckedChangeListener { buttonView, isChecked ->
                historySet.checked = isChecked
                historyEventListener.saveHistorySet(historySet)
            }
            bind.editTextSetWeight.doAfterTextChanged {
                // two way databinding을 사용했기 때문에 historySet이 자동으로 변경됨
                historyEventListener.saveHistorySet(historySet)
            }
            bind.editTextSetCount.doAfterTextChanged {
                // two way databinding을 사용했기 때문에 historySet이 자동으로 변경됨
                historyEventListener.saveHistorySet(historySet)
            }
            bind.buttonSetRemove.setOnClickListener {
                historyEventListener.removeHistorySet(historySet.setId)
                historyEventListener.renewTodayHistory()
            }
        }
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<HistorySet>() {
            override fun areItemsTheSame(oldItem: HistorySet, newItem: HistorySet): Boolean {
                return oldItem.setId == newItem.setId
            }

            override fun areContentsTheSame(oldItem: HistorySet, newItem: HistorySet): Boolean {
                return oldItem == newItem
            }

        }
    }
}