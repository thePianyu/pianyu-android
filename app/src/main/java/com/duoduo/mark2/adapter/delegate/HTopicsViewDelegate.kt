package com.duoduo.mark2.adapter.delegate

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.drakeet.multitype.ItemViewDelegate
import com.duoduo.mark2.R
import com.duoduo.mark2.adapter.TopicAdapter
import com.duoduo.mark2.models.Topic


class HTopicsViewDelegate(listener: OnItemClickListener) :
    ItemViewDelegate<List<Topic>, HTopicsViewDelegate.ViewHolder>() {

    val listener = listener

    interface OnItemClickListener {
        fun onClick(topic: Topic)
    }

    override fun onCreateViewHolder(context: Context, parent: ViewGroup): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_htopics, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, item: List<Topic>) {
        holder.topicAdapter.addTopics(item)
        holder.topicAdapter.setClickListener { position, _ ->
            listener.onClick(item[position])
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val recyclerView: RecyclerView = itemView.findViewById(R.id.recy_topic)
        var topicAdapter: TopicAdapter = TopicAdapter()

        init {
            recyclerView.adapter = topicAdapter
            recyclerView.layoutManager = LinearLayoutManager(itemView.context).also {
                it.orientation = LinearLayoutManager.HORIZONTAL
            }
        }
    }

}