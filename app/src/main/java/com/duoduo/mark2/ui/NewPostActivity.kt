package com.duoduo.mark2.ui

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.duoduo.mark2.R
import com.duoduo.mark2.api.ApiClient
import com.duoduo.mark2.api.ArticleService
import com.duoduo.mark2.api.QuestionService
import com.duoduo.mark2.api.TopicService
import com.duoduo.mark2.models.PostArticleRequest
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch


class NewPostActivity : AppCompatActivity() {

    val selectedTopics: ArrayList<Int> = ArrayList()

    lateinit var articleService: ArticleService
    lateinit var questionService: QuestionService
    lateinit var topicService: TopicService

    lateinit var tagsGroup: ChipGroup
    lateinit var edit_title: TextInputEditText
    lateinit var edit_content: TextInputEditText
    lateinit var fab: ExtendedFloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_post)

        val client = ApiClient.getClient(this)
        articleService = client.create(ArticleService::class.java)
        questionService = client.create(QuestionService::class.java)
        topicService = client.create(TopicService::class.java)

        edit_title = findViewById(R.id.edit_title)
        edit_content = findViewById(R.id.edit_content)
        tagsGroup = findViewById(R.id.tags_group)
        fab = findViewById(R.id.fab)

        lifecycleScope.launch {
            try {
                val topics = topicService.getList(1)
                for (topic in topics!!.data!!) {
                    tagsGroup.addView(Chip(this@NewPostActivity).also {
                        it.text = topic!!.name
                        it.isCheckable = true
                        it.setOnCheckedChangeListener { _, b ->
                            if (b)
                                selectedTopics.add(topic.id)
                            else
                                selectedTopics.remove(topic.id)
                        }
                    })
                }
            } catch (e: Exception) {
                Log.e("NewPostActivity", "load topics", e)
                Toast.makeText(this@NewPostActivity, "加载话题失败: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }

        fab.setOnClickListener {
            lifecycleScope.launch {
                val dialog = MaterialAlertDialogBuilder(this@NewPostActivity)
                    .setTitle("发布")
                    .setMessage("发布中...")
                    .setCancelable(false)
                    .show()
                try {
                    articleService.create(PostArticleRequest().also {
                        it.title = edit_title.text.toString()
                        it.content_markdown = edit_content.text.toString()
                        it.topic_ids = selectedTopics
                    })
                    Toast.makeText(this@NewPostActivity, "发布成功", Toast.LENGTH_SHORT).show()
                    this@NewPostActivity.finish()
                } catch (e: Exception) {
                    Log.e("NewPostActivity", "new post", e)
                    Toast.makeText(this@NewPostActivity, "发布失败:${e.message}", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
        }
    }

}