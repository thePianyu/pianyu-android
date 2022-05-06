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
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.drakeet.multitype.ItemViewDelegate
import com.duoduo.mark2.R
import com.duoduo.mark2.models.Comment
import com.google.android.material.button.MaterialButton
import java.text.SimpleDateFormat
import java.util.*


class CommentViewDelegate(val listener: CommentActionListener) : ItemViewDelegate<Comment, CommentViewDelegate.ViewHolder>() {

    interface CommentActionListener {
        fun onVote(action: String, item: Comment)
        fun onComment(item: Comment)
    }

    override fun onCreateViewHolder(context: Context, parent: ViewGroup): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_comment, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, item: Comment) {
        val drawableUp: BadgeDrawable? = when (item.vote_up_count == 0) {
            false -> BadgeDrawable.Builder()
                .type(BadgeDrawable.TYPE_NUMBER)
                .number(item.vote_up_count)
                .textSize(sp2px(8f))
                .build()
            else -> null
        }
        holder.badge_vote_up.setImageDrawable(drawableUp) // 点赞数

        val drawableDown: BadgeDrawable? = when (item.vote_down_count == 0) {
            false -> BadgeDrawable.Builder()
                .type(BadgeDrawable.TYPE_NUMBER)
                .number(item.vote_down_count)
                .textSize(sp2px(8f))
                .build()
            else -> null
        }
        holder.badge_vote_down.setImageDrawable(drawableDown) // 点踩数

        holder.content.text = item.content // 评论正文
        val format = SimpleDateFormat.getDateInstance()
        holder.date.text = format.format(Date(item.update_time * 1000)) // 发布日期
        Glide
            .with(holder.itemView)
            .load(item.relationships.user.avatar.small)
            .transition(DrawableTransitionOptions.withCrossFade(500))
            .into(holder.avatar) // 头像

        holder.username.text = item.relationships.user.username // 用户名

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

        // 评论
        holder.comment.setOnClickListener {
            listener.onComment(item)
        }
        val drawableComment: BadgeDrawable? = when (item.reply_count == 0) {
            false -> BadgeDrawable.Builder()
                .type(BadgeDrawable.TYPE_NUMBER)
                .number(item.reply_count)
                .textSize(sp2px(8f))
                .build()
            else -> null
        }
        holder.badge_comment.setImageDrawable(drawableComment)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val vote_up: MaterialButton = itemView.findViewById(R.id.vote_up)
        val badge_vote_up: ImageView = itemView.findViewById(R.id.badge_vote_up)
        val vote_down: MaterialButton = itemView.findViewById(R.id.vote_down)
        val badge_vote_down: ImageView = itemView.findViewById(R.id.badge_vote_down)

        val content: TextView = itemView.findViewById(R.id.content)
        val username: TextView = itemView.findViewById(R.id.username)
        val date: TextView = itemView.findViewById(R.id.date)
        val avatar: ImageView = itemView.findViewById(R.id.avatar)

        val comment: MaterialButton = itemView.findViewById(R.id.comment)
        val badge_comment: ImageView = itemView.findViewById(R.id.badge_comment)
    }

    private fun sp2px(spValue: Float): Float {
        val fontScale = Resources.getSystem().displayMetrics.scaledDensity
        return spValue * fontScale + 0.5f
    }

}