package com.lateinit.rightweight.ui.home

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide

@BindingAdapter(
    value = ["imageUrl"]
)
fun ImageView.setImage(imageUrl: String){
    Glide.with(this)
        .load(imageUrl)
        .into(this)
}