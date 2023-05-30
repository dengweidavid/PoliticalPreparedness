package com.example.android.politicalpreparedness.election

import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.network.CivicsApiStatus
import java.util.*

@BindingAdapter("civicsApiStatus")
fun bindApiStatus(statusImageView: ImageView, status: CivicsApiStatus?) {
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
