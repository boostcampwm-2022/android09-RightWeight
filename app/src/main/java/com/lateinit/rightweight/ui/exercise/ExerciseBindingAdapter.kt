package com.lateinit.rightweight.ui.exercise

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.lateinit.rightweight.R


@BindingAdapter(
    value = ["isTimerRunning"]
)
fun ImageView.setImage(isTimerRunning: Boolean){
    when(isTimerRunning){
        true -> this.setImageResource(R.drawable.ic_baseline_pause)
        false -> this.setImageResource(R.drawable.ic_baseline_play_arrow)
    }
}