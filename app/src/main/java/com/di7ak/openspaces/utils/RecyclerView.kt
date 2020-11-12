package com.di7ak.openspaces.utils

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

fun RecyclerView.addOnScrollToBottomListener(visibleThreshold: Int, block: () -> Unit) {
    val layoutManager = layoutManager as LinearLayoutManager
    addOnScrollListener(object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            if (layoutManager.itemCount <= (layoutManager.findLastVisibleItemPosition() + visibleThreshold)) block()
        }
    })
}
