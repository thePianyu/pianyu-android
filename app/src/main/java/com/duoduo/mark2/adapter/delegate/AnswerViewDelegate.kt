package com.duoduo.mark2.adapter.delegate

import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import cn.nekocode.badge.BadgeDrawable
import com.bumptech.glide.Glide
import com.drakeet.multitype.ItemViewDelegate
import com.duoduo.mark2.R
import com.duoduo.mark2.models.Answer
import com.duoduo.mark2.models.Comment
import com.duoduo.mark2.utils.MarkdownUtils
import com.google.android.material.button.MaterialButton
import io.noties.markwon.Markwon
import java.text.SimpleDateFormat
import java.util.*

class AnswerViewDelegate(
    private val imageClickListener: MarkdownUtils.ImageClickListener,
    private val listener: OnVoteListener
) :
    ItemViewDelegate<Answer, AnswerViewDelegate.ViewHolder>() {

    interface OnVoteListener {
        fun onVote(action: String, item: Answer)
    }

    override fun onCreateViewHolder(context: Context, parent: ViewGroup): ViewHolder {
        val markdown = MarkdownUtils.newMarkwon(context, imageClickListener)
        return AnswerViewDelegate.ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.item_answer, parent, false),
            markdown
        )
    }

    override fun onBindViewHolder(holder: AnswerViewDelegate.ViewHolder, item: Answer) {
        // 回答正文
        holder.content.text = holder.markwon.toMarkdown(item.content_markdown)
        // 头像
        Glide.with(holder.avatar).load(item.relationships.user.avatar.original).into(holder.avatar)
        // 用户名
        holder.username.text = item.relationships.user.username
        // 点赞数
        holder.badge_vote_up.setImageDrawable(
            when (item.vote_up_count == 0) {
                true -> null
                else -> BadgeDrawable.Builder()
                    .type(BadgeDrawable.TYPE_NUMBER)
                    .number(item.vote_up_count)
                    .textSize(sp2px(8f))
                    .build()
            }
        )
        // 点踩数
        holder.badge_vote_down.setImageDrawable(
            when (item.vote_down_count == 0) {
                true -> null
                else -> BadgeDrawable.Builder()
                    .type(BadgeDrawable.TYPE_NUMBER)
                    .number(item.vote_down_count)
                    .textSize(sp2px(8f))
                    .build()
            }
        )
        // 时间
        val format = SimpleDateFormat.getDateInstance()
        holder.date.text = format.format(Date(item.update_time * 1000)) // 发布日期

        holder.vote_up.setOnClickListener {
            listener.onVote(
                when (item.relationships.voting.equals("up")) {
                    true -> "" // 取消投票
                    else -> "up"
                },
                item
            )
        }
        holder.vote_down.setOnClickListener {
            listener.onVote(
                when (item.relationships.voting.equals("down")) {
                    true -> "" // 取消投票
                    else -> "down"
                },
                item
            )
        }

        holder.vote_up.icon = ResourcesCompat.getDrawable(
            holder.vote_up.resources,
            when (item.relationships.voting == "up") {
                true -> R.drawable.ic_thumb_up_black_48dp_filled
                else -> R.drawable.ic_thumb_up_black_48dp
            },
            null
        )
        holder.vote_down.icon = ResourcesCompat.getDrawable(
            holder.vote_down.resources,
            when (item.relationships.voting == "down") {
                true -> R.drawable.ic_thumb_down_black_48dp_filled
                else -> R.drawable.ic_thumb_down_black_48dp
            },
            null
        )
    }

    class ViewHolder(itemView: View, markwon: Markwon) : RecyclerView.ViewHolder(itemView) {
        val markwon = markwon
        val vote_up: MaterialButton = itemView.findViewById(R.id.vote_up)
        val badge_vote_up: ImageView = itemView.findViewById(R.id.badge_vote_up)
        val vote_down: MaterialButton = itemView.findViewById(R.id.vote_down)
        val badge_vote_down: ImageView = itemView.findViewById(R.id.badge_vote_down)

        val content: TextView = itemView.findViewById(R.id.content)
        val username: TextView = itemView.findViewById(R.id.username)
        val date: TextView = itemView.findViewById(R.id.date)
        val avatar: ImageView = itemView.findViewById(R.id.avatar)
    }

    private fun sp2px(spValue: Float): Float {
        val fontScale = Resources.getSystem().displayMetrics.scaledDensity
        return spValue * fontScale + 0.5f
    }

}