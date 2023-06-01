package com.example.android.politicalpreparedness.util

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.network.CivicsApiStatus
import java.util.*

@BindingAdapter("civicsApiStatus")
fun bindApiStatus(statusImageView: ImageView, status: CivicsApiStatus?) {
    Log.d("binding debug - civicsApiStatus", "$status")
    when (status) {
        CivicsApiStatus.LOADING -> {
            statusImageView.visibility = View.VISIBLE
            statusImageView.setImageResource(R.drawable.loading_animation)
        }
        CivicsApiStatus.ERROR -> {
            statusImageView.visibility = View.VISIBLE
            statusImageView.setImageResource(R.drawable.ic_connection_error)
        }
        CivicsApiStatus.DONE -> {
            statusImageView.visibility = View.GONE
        }
    }
}

@BindingAdapter("fadeVisible")
fun View.bindFadeVisible(visible: Boolean? = true) {
    Log.d("binding debug - fadeVisible", "$visible")
    if (tag == null) {
        tag = true
        visibility = if (visible == true) View.VISIBLE else View.GONE
    } else {
        animate().cancel()
        if (visible == true) {
            if (visibility == View.GONE)
                fadeIn()
        } else {
            if (visibility == View.VISIBLE)
                fadeOut()
        }
    }
}

fun View.fadeIn() {
    this.visibility = View.VISIBLE
    this.alpha = 0f
    this.animate().alpha(1f).setListener(object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator) {
            this@fadeIn.alpha = 1f
        }
    })
}

fun View.fadeOut() {
    this.animate().alpha(0f).setListener(object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator) {
            this@fadeOut.alpha = 1f
            this@fadeOut.visibility = View.GONE
        }
    })
}

