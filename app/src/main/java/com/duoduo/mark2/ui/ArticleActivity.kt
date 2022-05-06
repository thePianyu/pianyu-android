package com.duoduo.mark2.ui

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.drakeet.multitype.MultiTypeAdapter
import com.duoduo.mark2.R
import com.duoduo.mark2.adapter.delegate.ArticleViewDelegate
import com.duoduo.mark2.adapter.delegate.CommentViewDelegate
import com.duoduo.mark2.api.ApiClient
import com.duoduo.mark2.api.ArticleService
import com.duoduo.mark2.api.CommentService
import com.duoduo.mark2.models.*
import com.duoduo.mark2.ui.dialogs.NewCommentBottomSheet
import com.duoduo.mark2.ui.topic.TopicActivity
import com.duoduo.mark2.utils.ErrorState
import com.duoduo.mark2.utils.LoadMoreListener
import com.duoduo.mark2.utils.MarkdownUtils
import com.duoduo.mark2.utils.addLoadMoreListener
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.zy.multistatepage.MultiStateContainer
import com.zy.multistatepage.bindMultiState
import com.zy.multistatepage.state.LoadingState
import com.zy.multistatepage.state.SuccessState
import kotlinx.coroutines.launch


class ArticleActivity : AppCompatActivity() {

    lateinit var swipeRefresh: SwipeRefreshLayout
    private var recycler: RecyclerView? = null
    private val adapter = MultiTypeAdapter()
    lateinit var items: MutableList<Any>
    lateinit var multiState: MultiStateContainer

    private var articleService: ArticleService? = null
    private lateinit var commentService: CommentService

    private var article_id: Int? = null

    private var comment_page = 1
    private var isLoading = false

    private var newCommentBottomSheet: NewCommentBottomSheet? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_article)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        article_id = intent.getIntExtra("ARTICLE_ID", -1)

        recycler = findViewById(R.id.recycler_view)
        recycler!!.layoutManager = LinearLayoutManager(this)

        swipeRefresh = findViewById(R.id.swipe)

        // 分割线
        val divider = MaterialDividerItemDecoration(this, LinearLayoutManager.VERTICAL)
        recycler!!.addItemDecoration(divider)

        // 文章正文
        adapter.register(ArticleViewDelegate(object : ArticleViewDelegate.OnTopicClickListener {
            override fun onClick(topic: Topic) {
                val intent = Intent(this@ArticleActivity, TopicActivity::class.java)
                intent.putExtra("TOPIC_ID", topic.id)
                startActivity(intent)
            }
        }, object : MarkdownUtils.ImageClickListener {
            override fun onClick(url: String) {
                Log.d("ArticleActivity", "imgOnClick $url")
                val intent = Intent(this@ArticleActivity, ImageActivity::class.java)
                intent.putExtra("URL", url)
                startActivity(intent)
            }
        }))

        // 评论
        adapter.register(CommentViewDelegate(object : CommentViewDelegate.CommentActionListener {
            override fun onVote(action: String, item: Comment) { // 点赞或点踩
                lifecycleScope.launch {
                    try {
                        val vote: VoteCount?
                        if (action.isNotEmpty()) {
                            vote = commentService.addVote(item.comment_id, VoteAction().also {
                                it.type = action
                            })?.data
                        } else { // 取消投票
                            vote = commentService.deleteVote(item.comment_id)?.data
                        }
                        item.vote_up_count = vote?.vote_up_count!!
                        item.vote_down_count = vote.vote_down_count
                        item.relationships.voting = action
                        adapter.notifyItemChanged(items.indexOf(item))
                    } catch (e: Exception) {
                        Toast.makeText(this@ArticleActivity, e.message, Toast.LENGTH_SHORT).show()
                        Log.e("ArticleActivity", "vote", e);
                    }
                }
            }

            override fun onComment(item: Comment) {
                val intent = Intent(this@ArticleActivity, CommentActivity::class.java)
                intent.putExtra("COMMENTABLE_TYPE", "comment")
                intent.putExtra("COMMENTABLE_ID", item.comment_id)
                startActivity(intent)
            }
        }))

        recycler!!.adapter = adapter
        items = ArrayList()
        adapter.items = items

        // 加载更多
        recycler!!.addLoadMoreListener(object : LoadMoreListener {
            override fun onLoadMore() {
                lifecycleScope.launch {
                    try {
                        loadComment()
                    } catch (e: Exception) {
                        Toast.makeText(this@ArticleActivity, e.message, Toast.LENGTH_SHORT).show()
                        Log.e("ArticleActivity", "loadMoreComment", e)
                    }
                }
            }
        })


        articleService = ApiClient.getClient(this).create(ArticleService::class.java)
        commentService = ApiClient.getClient(this).create(CommentService::class.java)

        // 加载动画
        multiState = recycler!!.bindMultiState()

        // 下拉刷新
        swipeRefresh.setOnRefreshListener {
            reload()
            swipeRefresh.isRefreshing = false
        }

        reload()
    }

    /**
     * 加载正文/评论
     */
    fun reload() {
        multiState.show(LoadingState())

        items.clear()
        adapter.notifyDataSetChanged()
        comment_page = 1

        // 加载文章正文
        lifecycleScope.launch {
            try {
                val article = articleService!!.get(article_id!!)
                items.add(article!!.data!!)
                adapter.notifyItemInserted(0)
                title = article.data!!.title

                multiState.show(SuccessState())

                loadComment()
            } catch (e: Exception) {
                multiState.show(ErrorState().also {
                    it.setMessage(e.message ?: e.toString())
                })
                Log.e("ArticleActivity", "loadArticle", e)
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_article, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.new_comment -> {
                newCommentBottomSheet = NewCommentBottomSheet(object : NewCommentBottomSheet.OnPostCommentListener {
                    override fun onPostComment(content: String) {
                        postComment(content)
                    }
                })
                newCommentBottomSheet!!.show(supportFragmentManager, "NEW_COMMENT")
            }
            android.R.id.home -> {
                this.finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * 发布评论
     */
    fun postComment(content: String) {
        lifecycleScope.launch {
            val progressDialog = ProgressDialog(this@ArticleActivity)
            progressDialog.setTitle("提交中...")
            progressDialog.show()

            try {
                articleService!!.createComment(article_id!!, CreateCommentRequest().also {
                    it.content = content
                })
                newCommentBottomSheet?.dismiss()
            } catch (e: Exception) {
                this@ArticleActivity.toast("发布失败:${e.message}")
            }

            progressDialog.dismiss()
        }
    }

    /**
     * 加载更多评论
     */
    private suspend fun loadComment() {
        if (comment_page <= 0 || isLoading) return
        isLoading = true
        try {
            val old = items.size
            val comments = articleService!!.getComments(article_id!!, page = comment_page)
            comments.data.forEach {
                items.add(it)
            }
            comment_page = comments.pagination.next
            adapter.notifyItemRangeInserted(old, comments.data.size)
        } catch (e: Exception) {
            this@ArticleActivity.toast("加载评论失败: " + e.message)
            Log.e("ArticleActivity", "loadComment", e)
        } finally {
            isLoading = false
        }
    }

    private fun dp2px(dp: Float): Float {
        val metrics = this.resources.displayMetrics
        return dp * (metrics.densityDpi / 160f)
    }

}

private fun AppCompatActivity.toast(s: String) {
    Toast.makeText(this, s, Toast.LENGTH_SHORT).show()
}

