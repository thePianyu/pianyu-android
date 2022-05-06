package com.duoduo.mark2.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import com.duoduo.mark2.R
import com.google.android.material.textview.MaterialTextView
import com.zy.multistatepage.MultiState
import com.zy.multistatepage.MultiStateContainer

/**
 * 加载失败
 */
class ErrorState : MultiState() {

    private var msg = "Error"
    private var retry: OnRetryClickListener? = null

    fun setMessage(msg: String) {
        this.msg = msg
    }

    override fun onCreateMultiStateView(context: Context, inflater: LayoutInflater, container: MultiStateContainer): View {
        return inflater.inflate(R.layout.view_error_state, container, false)
    }

    override fun onMultiStateViewCreate(view: View) {
        view.findViewById<MaterialTextView>(R.id.textview).text = msg
        view.setOnClickListener {
            retry?.retry()
            view.setOnClickListener(null)
        }
    }

    fun retry(retry: OnRetryClickListener) {
        this.retry = retry
    }

    fun interface OnRetryClickListener {
        fun retry()
    }

}