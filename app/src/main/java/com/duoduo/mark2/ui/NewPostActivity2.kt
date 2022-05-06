package com.duoduo.mark2.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.FrameLayout
import com.duoduo.mark2.R

class NewPostActivity2 : AppCompatActivity() {
    lateinit var editor: EditorFragment
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_post2)

        editor = EditorFragment.newInstance(object : EditorFragment.Listener {
            override fun onLoaded() {
                editor.setMarkdownContent("在此输入正文...")
            }
        })
        supportFragmentManager
            .beginTransaction()
            .add(R.id.framelayout, editor)
            .commit()
    }

}