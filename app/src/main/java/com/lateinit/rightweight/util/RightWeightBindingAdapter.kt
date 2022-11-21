package com.lateinit.rightweight.util

import android.widget.TextView
import androidx.databinding.BindingAdapter

object RightWeightBindingAdapter {
    @BindingAdapter("bind_set_text_number")
    @JvmStatic
    fun setTextFromNumber(textView: TextView, number: Number) {
        textView.text = number.toString()
    }
}