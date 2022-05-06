package com.duoduo.mark2.ui

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.drakeet.multitype.MultiTypeAdapter
import com.duoduo.mark2.R
import com.duoduo.mark2.adapter.delegate.AnswerViewDelegate
import com.duoduo.mark2.adapter.delegate.ArticleViewDelegate
import com.duoduo.mark2.adapter.delegate.QuestionViewDelegate
import com.duoduo.mark2.api.ApiClient
import com.duoduo.mark2.api.QuestionService
import com.duoduo.mark2.models.Answer
import com.duoduo.mark2.models.Topic
import com.duoduo.mark2.models.VoteAction
import com.duoduo.mark2.models.VoteCount
import com.duoduo.mark2.ui.topic.TopicActivity
import com.duoduo.mark2.utils.ErrorState
import com.duoduo.mark2.utils.MarkdownUtils
import com.zy.multistatepage.bindMultiState
import com.zy.multistatepage.state.LoadingState
import com.zy.multistatepage.state.SuccessState
import kotlinx.coroutines.launch

@SuppressLint("NonConstantResourceId")
class QuestionActivity : AppCompatActivity() {

    private var recyclerView: RecyclerView? = null
    private lateinit var adapter: MultiTypeAdapter

    private var questionService: QuestionService? = null

    private var questionId: Int = 0

    lateinit var items: MutableList<Any>

    private var page = 1
    private var isLoading = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_question)

        recyclerView = findViewById(R.id.recycler_view)

        questionId = intent.getIntExtra("QUESTION_ID", 0)

        questionService = ApiClient.getClient(this).create(QuestionService::class.java)

        // 加载动画
        val multiState = recyclerView!!.bindMultiState()
        multiState.show(LoadingState())

        // adapter
        adapter = MultiTypeAdapter()
        // 正文
        adapter.register(QuestionViewDelegate(object : QuestionViewDelegate.OnTopicClickListener {
            override fun onClick(topic: Topic) {
                val intent = Intent(this@QuestionActivity, TopicActivity::class.java)
                intent.putExtra("TOPIC_ID", topic.id)
                startActivity(intent)
            }
        }, object : MarkdownUtils.ImageClickListener {
            override fun onClick(url: String) {
                Log.d("QuestionActivity", "imgOnClick $url")
                val intent = Intent(this@QuestionActivity, ImageActivity::class.java)
                intent.putExtra("URL", url)
                startActivity(intent)
            }
        }))
        // 回答
        adapter.register(AnswerViewDelegate(object : MarkdownUtils.ImageClickListener {
            override fun onClick(url: String) {
                Log.d("QuestionActivity", "imgOnClick $url")
                val intent = Intent(this@QuestionActivity, ImageActivity::class.java)
                intent.putExtra("URL", url)
                startActivity(intent)
            }
        }, object : AnswerViewDelegate.OnVoteListener {
            override fun onVote(action: String, item: Answer) {
                lifecycleScope.launch {
                    try {
                        val vote: VoteCount?
                        if (action.isNotEmpty()) {
                            vote = questionService!!.addVote(item.answer_id, VoteAction().also {
                                it.type = action
                            })?.data

                        } else { // 取消投票
                            vote = questionService!!.deleteVote(item.answer_id)?.data

                        }
                        item.vote_up_count = vote?.vote_up_count!!
                        item.vote_down_count = vote.vote_down_count
                        item.relationships.voting = action
                        adapter.notifyItemChanged(items.indexOf(item))
                    } catch (e: Exception) {
                        Toast.makeText(this@QuestionActivity, e.message, Toast.LENGTH_SHORT).show()
                        Log.e("QuestionActivity", "vote", e);
                    }
                }
            }
        }))

        recyclerView!!.adapter = adapter
        items = ArrayList()
        adapter.items = items

        recyclerView!!.layoutManager = LinearLayoutManager(this)

        lifecycleScope.launch {
            try {
                val question = questionService!!.get(questionId, include = "user,topics")!!.data!!
                items.add(question)
                adapter.notifyItemInserted(0)
                title = question.title
                multiState.show(SuccessState())
            } catch (e: Exception) {
                multiState.show(ErrorState().also {
                    it.setMessage(e.message ?: e.toString())
                })
                Log.e("QuestionActivity", "loadQuestion", e)
            }
            loadMoreAnswers()
        }
    }

    private fun loadMoreAnswers() {
        if (isLoading || page < 1) return
        lifecycleScope.launch {
            try {
                val answers = questionService!!.getAnswers(questionId, page, include = "user,voting")
                for (answer in answers.data!!) {
                    items.add(answer)
                    adapter.notifyItemInserted(items.size - 1)
                }
            } catch (e: Exception) {
                Toast.makeText(this@QuestionActivity, "加载评论失败: " + e.message, Toast.LENGTH_SHORT).show()
                Log.e("ArticleActivity", "loadComment", e)
            }
        }
    }
}