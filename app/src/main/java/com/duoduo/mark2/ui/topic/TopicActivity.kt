package com.duoduo.mark2.ui.topic

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.ImageView
import android.widget.Toast
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.duoduo.mark2.R
import com.duoduo.mark2.api.ApiClient
import com.duoduo.mark2.api.TopicService
import com.duoduo.mark2.models.BaseResponse
import com.duoduo.mark2.models.Topic
import com.duoduo.mark2.ui.NewPostActivity
import com.duoduo.mark2.ui.NewPostActivity2
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import jp.wasabeef.glide.transformations.BlurTransformation
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.lang.Exception

class TopicActivity : AppCompatActivity() {

    private var topicService: TopicService? = null
    private var img: ImageView? = null
    lateinit var collapsingToolbarLayout: CollapsingToolbarLayout
    lateinit var fab: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_topic)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar_layout)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val viewPager = findViewById<ViewPager2>(R.id.view_pager)
        viewPager.adapter = FragmentAdapter(this, intent.getIntExtra("TOPIC_ID", 0))

        val tabLayout = findViewById<TabLayout>(R.id.tab_layout)
        TabLayoutMediator(
            tabLayout, viewPager
        ) { tab: TabLayout.Tab, position: Int ->
            tab.text = when (position) {
                0 -> "文章"
                1 -> "提问"
                else -> ""
            }
        }.attach()

        img = findViewById(R.id.img)

        val client = ApiClient.getClient(this)
        topicService = client.create(TopicService::class.java)

        // 加载话题信息（标题 背景图）
        lifecycleScope.launch {
            try {
                val topic = topicService!!.getTopic(intent.getIntExtra("TOPIC_ID", 0))

                collapsingToolbarLayout.title = topic!!.data?.name // 标题

                Glide.with(this@TopicActivity)
                    .load(topic.data?.cover?.original)
                    .apply(RequestOptions.bitmapTransform(BlurTransformation(50, 3)))
                    .into(img!!) // 背景图
            } catch (e: Exception) {
                Toast.makeText(this@TopicActivity, e.message, Toast.LENGTH_SHORT).show()
                Log.e("TopicActivity", "topicInfo", e);
            }
        }

        // 发布按钮
        fab = findViewById(R.id.fab)
        fab.setOnClickListener {
            val intent = Intent(this, NewPostActivity2::class.java)
            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.topic_activity, menu)
        return super.onPrepareOptionsMenu(menu)
    }
}