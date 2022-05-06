package com.duoduo.mark2.utils

import androidx.recyclerview.widget.RecyclerView

interface LoadMoreListener {
    fun onLoadMore()
}

fun RecyclerView.addLoadMoreListener(listener: LoadMoreListener) {
    this.addOnScrollListener(object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            if (!recyclerView.canScrollVertically(1))
                listener.onLoadMore()
        }
    })
}