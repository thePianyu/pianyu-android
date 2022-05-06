package com.duoduo.mark2.ui.topic

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
import com.duoduo.mark2.R
import com.duoduo.mark2.adapter.QuestionAdapter
import com.duoduo.mark2.api.ApiClient
import com.duoduo.mark2.api.TopicService
import com.duoduo.mark2.ui.QuestionActivity
import com.duoduo.mark2.utils.ErrorState
import com.zy.multistatepage.MultiStateContainer
import com.zy.multistatepage.bindMultiState
import com.zy.multistatepage.state.LoadingState
import com.zy.multistatepage.state.SuccessState
import kotlinx.coroutines.launch

class QuestionsFragment : Fragment() {
    private var topicService: TopicService? = null
    private var topic_id = 0
    private var page = 1
    private var recyclerView: RecyclerView? = null
    private var adapter: QuestionAdapter? = null

    private lateinit var multiState: MultiStateContainer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        topic_id = arguments!!.getInt("TOPIC_ID", 0)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val client = ApiClient.getClient(this.context)
        topicService = client.create(TopicService::class.java)

        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView?.layoutManager = LinearLayoutManager(this.context)
        adapter = QuestionAdapter()
        recyclerView?.adapter = adapter


        multiState = recyclerView!!.bindMultiState()
        multiState.show(LoadingState())

        loadMore()

        // 加载更多
        recyclerView!!.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!recyclerView.canScrollVertically(1)) loadMore()
            }
        })

        adapter!!.setOnClickListener {
            val intent = Intent(this.context, QuestionActivity::class.java)
            intent.putExtra("QUESTION_ID", it.question_id)
            startActivity(intent)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_questions, container, false)
    }

    /**
     * 加载更多文章
     */
    private fun loadMore() {
        if (page <= 0) return


        lifecycleScope.launch {
            try {
                val questions = topicService!!.getQuestions(topic_id, page, "-update_time")
                adapter!!.addQuestions(questions!!.data!!)
                page = questions.pagination?.next ?: 0

                multiState.show(SuccessState())
            } catch (e: Exception) {
                Log.e("ArticlesFragment", "loadMore", e)
                multiState.show(ErrorState().also {
                    it.setMessage(e.message ?: e.toString())
                    it.retry {
                        loadMore()
                    }
                })
            }
        }

    }

    companion object {
        @JvmStatic
        fun newInstance(topic_id: Int): QuestionsFragment {
            val fragment = QuestionsFragment()
            val args = Bundle()
            args.putInt("TOPIC_ID", topic_id)
            fragment.arguments = args
            return fragment
        }
    }
}