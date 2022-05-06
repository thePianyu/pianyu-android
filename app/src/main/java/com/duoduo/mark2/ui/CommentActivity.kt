package com.duoduo.mark2.ui

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.drakeet.multitype.MultiTypeAdapter
import com.duoduo.mark2.R
import com.duoduo.mark2.adapter.delegate.CommentViewDelegate
import com.duoduo.mark2.api.ApiClient
import com.duoduo.mark2.api.CommentService
import com.duoduo.mark2.models.Comment
import com.duoduo.mark2.models.CreateCommentRequest
import com.duoduo.mark2.models.VoteAction
import com.duoduo.mark2.models.VoteCount
import com.duoduo.mark2.ui.dialogs.NewCommentBottomSheet
import com.duoduo.mark2.utils.LoadMoreListener
import com.duoduo.mark2.utils.addLoadMoreListener
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.zy.multistatepage.MultiStateContainer
import com.zy.multistatepage.bindMultiState
import kotlinx.coroutines.launch

class CommentActivity : AppCompatActivity() {

    lateinit var fab: FloatingActionButton
    lateinit var swipeRefreshLayout: SwipeRefreshLayout
    lateinit var recyclerView: RecyclerView
    lateinit var adapter: MultiTypeAdapter

    lateinit var items: MutableList<Any>
    lateinit var commentable_type: String
    var commentable_id: Int = 0

    var page = 1
    var isLoading = false

    lateinit var commentService: CommentService

    var bottomSheet: NewCommentBottomSheet? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comment)

        title = "查看评论"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        commentable_type = intent.getStringExtra("COMMENTABLE_TYPE")!!
        commentable_id = intent.getIntExtra("COMMENTABLE_ID", 0)

        fab = findViewById(R.id.fab)
        swipeRefreshLayout = findViewById(R.id.swipe)
        adapter = MultiTypeAdapter()
        recyclerView = findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter.register(CommentViewDelegate(object : CommentViewDelegate.CommentActionListener {
            override fun onVote(action: String, item: Comment) {
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
                        Toast.makeText(this@CommentActivity, e.message, Toast.LENGTH_SHORT).show()
                        Log.e("CommentActivity", "vote", e);
                    }
                }
            }

            override fun onComment(item: Comment) {

            }
        }))

        recyclerView.adapter = adapter
        items = ArrayList()
        adapter.items = items

        // 分割线
        val divider = MaterialDividerItemDecoration(this, LinearLayoutManager.VERTICAL)
        recyclerView.addItemDecoration(divider)

        commentService = ApiClient.getClient(this).create(CommentService::class.java)
        loadMore()

        // 下拉刷新
        swipeRefreshLayout.setOnRefreshListener {
            page = 1
            items.clear()
            adapter.notifyDataSetChanged()
            loadMore()
        }

        // 加载更多
        recyclerView.addLoadMoreListener(object : LoadMoreListener {
            override fun onLoadMore() {
                loadMore()
            }
        })

        // 发布评论
        fab.setOnClickListener {
            bottomSheet = NewCommentBottomSheet(object : NewCommentBottomSheet.OnPostCommentListener {
                override fun onPostComment(content: String) {
                    lifecycleScope.launch {
                        val dialog = ProgressDialog(this@CommentActivity)
                        dialog.show()

                        try {
                            when (commentable_type.lowercase()) {
                                "comment" -> {
                                    commentService.createReply(commentable_id, CreateCommentRequest().also {
                                        it.content = content
                                    })
                                }
                            }
                            Toast.makeText(this@CommentActivity, "回复成功", Toast.LENGTH_SHORT).show()
                        } catch (e: Exception) {
                            Toast.makeText(this@CommentActivity, "回复失败:" + e.message, Toast.LENGTH_SHORT).show()
                            Log.e("CommentActivity", "create reply", e)
                        }

                        dialog.dismiss()
                        bottomSheet?.dismiss()
                    }
                }
            })
            bottomSheet?.show(supportFragmentManager, "NewCommentBottomSheet")
        }
    }

    fun loadMore() {
        if (isLoading || page < 1) return
        lifecycleScope.launch {
            isLoading = true
            swipeRefreshLayout.isRefreshing = true
            try {
                when (commentable_type.lowercase()) {
                    "comment" -> {
                        val resp = commentService.getReplies(commentable_id, page = page)
                        val comments = resp.data
                        for (comment in comments) {
                            items.add(comment)
                            adapter.notifyItemInserted(items.size - 1)
                        }
                        page = resp.pagination.next
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(this@CommentActivity, e.message, Toast.LENGTH_SHORT).show()
                Log.e("CommentActivity", "load more", e)
            }
            isLoading = false
            swipeRefreshLayout.isRefreshing = false
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }
}