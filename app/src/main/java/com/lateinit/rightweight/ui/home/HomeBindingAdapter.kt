package com.lateinit.rightweight.ui.home

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.lateinit.rightweight.R

@BindingAdapter(
    value = ["imageUrl"]
)
fun ImageView.setImage(imageUrl: String){
    Glide.with(this)
        .load(imageUrl)
        .error(R.drawable.ic_launcher_foreground)
        .into(this)
}