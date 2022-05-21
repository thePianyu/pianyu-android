package com.duoduo.mark2.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.duoduo.mark2.R
import com.duoduo.mark2.api.ApiClient
import com.duoduo.mark2.api.ArticleService
import com.duoduo.mark2.api.QuestionService
import com.duoduo.mark2.api.TopicService
import com.duoduo.mark2.models.PostArticleRequest
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class NewPostActivity2 : AppCompatActivity() {

    private lateinit var editor: EditorFragment
    private lateinit var fab: ExtendedFloatingActionButton
    private lateinit var edit_title: TextInputEditText

    lateinit var articleService: ArticleService
    lateinit var questionService: QuestionService
    lateinit var topicService: TopicService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_post2)

        val client = ApiClient.getClient(this)
        articleService = client.create(ArticleService::class.java)
        questionService = client.create(QuestionService::class.java)
        topicService = client.create(TopicService::class.java)

        fab = findViewById(R.id.fab)
        edit_title = findViewById(R.id.edit_title)

        editor = EditorFragment.newInstance(object : EditorFragment.Listener {
            override fun onLoaded() {
                editor.setMarkdownContent("在此输入正文...")
            }

            override fun onGetMarkdownContent(content: String) {
                lifecycleScope.launch {
                    try {
                        // get topic list
                        val topics = topicService.getList(1)!!.data
                        val selected = ArrayList<Int>()

                        MaterialAlertDialogBuilder(this@NewPostActivity2)
                            .setTitle("选择话题")
                            .setPositiveButton(
                                "发布"
                            ) { _, _ ->
                                // post
                                Log.d("NewPostActivity2", content)
                                lifecycleScope.launch {
                                    try {
                                        articleService.create(PostArticleRequest().also {
                                            it.title = edit_title.text.toString()
                                            it.content_markdown = content
                                            it.topic_ids = selected.map { index -> topics!![index]!!.id }
                                        })
                                        Toast.makeText(this@NewPostActivity2, "发布成功", Toast.LENGTH_SHORT).show()
                                        this@NewPostActivity2.finish()
                                    } catch (e: Exception) {
                                        Toast.makeText(this@NewPostActivity2, "发布失败: " + e.message, Toast.LENGTH_SHORT)
                                            .show()
                                        e.printStackTrace()
                                    }
                                }

                            }
                            .setMultiChoiceItems(topics!!.map { it!!.name }.toTypedArray(), null) { _, which, checked ->
                                if (checked)
                                    selected.add(which)
                                else
                                    selected.remove(which)
                            }
                            .show()
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Toast.makeText(this@NewPostActivity2, "获取话题失败: " + e.message, Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        })

        supportFragmentManager
            .beginTransaction()
            .add(R.id.framelayout, editor)
            .commit()

        fab.setOnClickListener {
            editor.getMarkdownContent()
        }
    }

}