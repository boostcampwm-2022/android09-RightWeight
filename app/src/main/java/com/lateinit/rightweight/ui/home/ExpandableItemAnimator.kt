package com.lateinit.rightweight.ui.home

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.view.View
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView

class ExpandableItemAnimator : DefaultItemAnimator() {

    override fun recordPreLayoutInformation(
        state: RecyclerView.State,
        viewHolder: RecyclerView.ViewHolder,
        changeFlags: Int,
        payloads: MutableList<Any>
    ): ItemHolderInfo {
        return if (viewHolder is HomeAdapter.ExerciseViewHolder) {
            ExerciseItemInfo().also {
                it.setFrom(viewHolder)
            }
        } else {
            super.recordPreLayoutInformation(state, viewHolder, changeFlags, payloads)
        }
    }

    override fun recordPostLayoutInformation(
        state: RecyclerView.State,
        viewHolder: RecyclerView.ViewHolder
    ): ItemHolderInfo {
        return if (viewHolder is HomeAdapter.ExerciseViewHolder) {
            ExerciseItemInfo().also {
                it.setFrom(viewHolder)
            }
        } else {
            super.recordPostLayoutInformation(state, viewHolder)
        }
    }

    override fun animateChange(
        oldHolder: RecyclerView.ViewHolder,
        holder: RecyclerView.ViewHolder,
        preInfo: ItemHolderInfo,
        postInfo: ItemHolderInfo
    ): Boolean {
        if (preInfo is ExerciseItemInfo && postInfo is ExerciseItemInfo && holder is HomeAdapter.ExerciseViewHolder) {
            ObjectAnimator
                .ofFloat(
                    holder.binding.imageViewExpand,
                    View.ROTATION,
                    preInfo.arrowRotation,
                    postInfo.arrowRotation
                )
                .also {
                    it.addListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator?) {
                            holder.binding.imageViewExpand.rotation = postInfo.arrowRotation
                            dispatchAnimationFinished(holder)
                        }
                    })
                    it.start()
                }
        }
        return super.animateChange(oldHolder, holder, preInfo, postInfo)
    }

    //It means that for animation we don’t need to have separated objects of ViewHolder (old and new holder)
    override fun canReuseUpdatedViewHolder(viewHolder: RecyclerView.ViewHolder): Boolean {
        return true
    }
}

class ExerciseItemInfo : RecyclerView.ItemAnimator.ItemHolderInfo() {

    internal var arrowRotation: Float = 0F

    override fun setFrom(holder: RecyclerView.ViewHolder): RecyclerView.ItemAnimator.ItemHolderInfo {
        if (holder is HomeAdapter.ExerciseViewHolder) {
            arrowRotation = holder.binding.imageViewExpand.rotation
        }
        return super.setFrom(holder)
    }
}