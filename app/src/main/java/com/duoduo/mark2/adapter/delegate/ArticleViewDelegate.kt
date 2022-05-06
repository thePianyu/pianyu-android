package com.duoduo.mark2.adapter.delegate

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.target.Target
import com.drakeet.multitype.ItemViewDelegate
import com.duoduo.mark2.R
import com.duoduo.mark2.models.Article
import com.duoduo.mark2.models.Topic
import com.duoduo.mark2.utils.MarkdownUtils
import com.duoduo.mark2.utils.fixMarkdownImageLabel
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import io.noties.markwon.Markwon
import io.noties.markwon.image.AsyncDrawable
import io.noties.markwon.image.AsyncDrawableSpan
import io.noties.markwon.image.glide.GlideImagesPlugin
import java.text.SimpleDateFormat
import java.util.*

/**
 * 文章正文
 * @param listener 话题点击监听器
 * @param imageClickListener 图片点击监听器
 */
class ArticleViewDelegate(val listener: OnTopicClickListener, val imageClickListener: MarkdownUtils.ImageClickListener) :
    ItemViewDelegate<Article, ArticleViewDelegate.ViewHolder>() {


    interface OnTopicClickListener {
        fun onClick(topic: Topic)
    }

    override fun onCreateViewHolder(context: Context, parent: ViewGroup): ViewHolder {
        // 解析markdown
        val markdown = MarkdownUtils.newMarkwon(context, imageClickListener)
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_article_content, parent, false), markdown)
    }

    override fun onBindViewHolder(holder: ViewHolder, item: Article) {
        val node = holder.markwon.parse(item.content_markdown)
        val spanned = holder.markwon.render(node)
        holder.markwon.setParsedMarkdown(holder.textView, spanned)

        holder.username.text = item.relationships.user.username // 用户名

        Glide
            .with(holder.itemView)
            .load(item.relationships.user.avatar.small)
            .transition(DrawableTransitionOptions.withCrossFade(500))
            .into(holder.avatar) // 头像

        val format = SimpleDateFormat.getDateInstance()
        holder.date.text = format.format(Date(item.update_time * 1000)) // 发布日期

        holder.chipGroup.removeAllViews()
        item.relationships.topics?.forEach { topic ->
            holder.chipGroup.addView(Chip(holder.itemView.context).also {
                it.text = topic.name
                it.clipToOutline = true
                it.setOnClickListener {
                    listener.onClick(topic)
                }
            })
        }
    }

    class ViewHolder(itemView: View, markdown: Markwon) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.textview)
        val markwon: Markwon = markdown
        val username: TextView = itemView.findViewById(R.id.username)
        val avatar: ImageView = itemView.findViewById(R.id.avatar)
        val date: TextView = itemView.findViewById(R.id.date)
        val chipGroup: ChipGroup = itemView.findViewById(R.id.chip_group)
    }

}