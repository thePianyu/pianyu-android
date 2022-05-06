package com.duoduo.mark2.adapter.delegate

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.drakeet.multitype.ItemViewDelegate
import com.duoduo.mark2.R
import com.duoduo.mark2.models.Article
import com.duoduo.mark2.models.Question

/**
 * 单行问题（主页的热门问题列表）
 */
class SQuestionViewDelegate(val listener: OnClickListener) : ItemViewDelegate<Question, SQuestionViewDelegate.ViewHolder>() {

    interface OnClickListener {
        fun onClick(item: Question)
    }

    override fun onCreateViewHolder(context: Context, parent: ViewGroup): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_single_line, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, item: Question) {
        holder.title.text = item.title
        Glide.with(holder.itemView).load(item.relationships.user.avatar.small).into(holder.avatar)
        holder.itemView.setOnClickListener {
            listener.onClick(item)
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.title)
        val avatar: ImageView = itemView.findViewById(R.id.icon)
    }

}