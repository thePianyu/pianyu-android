package com.duoduo.mark2.ui.home

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.drakeet.multitype.MultiTypeAdapter
import com.duoduo.mark2.R
import com.duoduo.mark2.adapter.delegate.CategoryTextViewDelegate
import com.duoduo.mark2.adapter.delegate.HTopicsViewDelegate
import com.duoduo.mark2.adapter.delegate.SArticleViewDelegate
import com.duoduo.mark2.adapter.delegate.SQuestionViewDelegate
import com.duoduo.mark2.api.ApiClient
import com.duoduo.mark2.api.ArticleService
import com.duoduo.mark2.api.QuestionService
import com.duoduo.mark2.api.TopicService
import com.duoduo.mark2.models.Article
import com.duoduo.mark2.models.Question
import com.duoduo.mark2.models.Topic
import com.duoduo.mark2.ui.ArticleActivity
import com.duoduo.mark2.ui.QuestionActivity
import com.duoduo.mark2.ui.topic.TopicActivity
import com.duoduo.mark2.utils.ErrorState
import com.zy.multistatepage.MultiStateContainer
import com.zy.multistatepage.bindMultiState
import com.zy.multistatepage.state.LoadingState
import com.zy.multistatepage.state.SuccessState
import kotlinx.coroutines.launch

class IndexFragment : Fragment() {

    private var topicService: TopicService? = null
    private var articleService: ArticleService? = null
    private var questionService: QuestionService? = null

    private var recyclerView: RecyclerView? = null
    private val adapter = MultiTypeAdapter()

    lateinit var items: MutableList<Any>
    lateinit var multiState: MultiStateContainer

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val client = ApiClient.getClient(this.context)
        topicService = client.create(TopicService::class.java)
        articleService = client.create(ArticleService::class.java)
        questionService = client.create(QuestionService::class.java)

        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView!!.layoutManager = LinearLayoutManager(this.context)

        adapter.register(CategoryTextViewDelegate()) // 分类标题
        adapter.register(HTopicsViewDelegate(object : HTopicsViewDelegate.OnItemClickListener { // 横向推荐话题列表
            override fun onClick(topic: Topic) {
                val intent = Intent(this@IndexFragment.context, TopicActivity::class.java)
                intent.putExtra("TOPIC_ID", topic.id)
                startActivity(intent)
            }
        }))
        adapter.register(SArticleViewDelegate(object : SArticleViewDelegate.OnItemClickListener { // 热门文章
            override fun onClick(article: Article) {
                val intent = Intent(this@IndexFragment.context, ArticleActivity::class.java)
                intent.putExtra("ARTICLE_ID", article.article_id)
                startActivity(intent)
            }

        }))
        adapter.register(SQuestionViewDelegate(object : SQuestionViewDelegate.OnClickListener { // 热门提问
            override fun onClick(item: Question) {
                val intent = Intent(this@IndexFragment.context, QuestionActivity::class.java)
                intent.putExtra("QUESTION_ID", item.question_id)
                startActivity(intent)
            }
        }))

        items = ArrayList()
        recyclerView!!.adapter = adapter
        adapter.items = items

        multiState = recyclerView!!.bindMultiState()

        load()
    }

    /**
     * 加载首页内容
     */
    private fun load() {
        multiState.show<LoadingState>() // 加载中

        items.clear()
        adapter.notifyDataSetChanged()

        lifecycleScope.launch {
            try {
                val topics = topicService!!.getList(0, "topic_id")
                items.add("推荐话题")
                items.add(topics?.data!!)

                val articles = articleService!!.getList(0, perPage = 5, order = "-vote_count")
                items.add("热门文章")
                articles.data.forEach {
                    items.add(it)
                }

                val questions = questionService!!.getList(0, perPage = 5, order = "-vote_count")
                items.add("热门提问")
                questions.data.forEach {
                    items.add(it)
                }

                adapter.notifyItemRangeInserted(0, items.size)

                multiState.show<SuccessState>()
            } catch (e: Exception) {
                multiState.show(ErrorState().also {
                    it.setMessage(e.message ?: e.toString())
                    it.retry {
                        load()
                    }
                })

                Log.e("IndexFragment", "load", e)
            }
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance(): IndexFragment {
            val fragment = IndexFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }
}