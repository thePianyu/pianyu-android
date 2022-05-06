package com.duoduo.mark2.ui.home

import com.google.android.material.button.MaterialButton
import androidx.appcompat.widget.LinearLayoutCompat
import com.duoduo.mark2.api.UserService
import android.os.Bundle
import com.duoduo.mark2.api.ApiClient
import com.duoduo.mark2.R
import android.content.Intent
import android.util.Log
import com.duoduo.mark2.ui.LoginActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.android.material.textview.MaterialTextView
import kotlinx.coroutines.launch
import java.lang.Exception

class MyFragment : Fragment() {
    private var btn_login: MaterialButton? = null
    private var root: LinearLayoutCompat? = null
    private var userService: UserService? = null
    private var username: MaterialTextView? = null
    private var headline: MaterialTextView? = null
    private var avatar: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val client = ApiClient.getClient(this.context)
        userService = client.create(UserService::class.java)

        root = view.findViewById(R.id.userinfo)
        btn_login = view.findViewById(R.id.login)
        username = view.findViewById(R.id.username)
        headline = view.findViewById(R.id.headline)
        avatar = view.findViewById(R.id.avatar)

        btn_login?.setOnClickListener {
            val intent = Intent(this.context, LoginActivity::class.java)
            startActivity(intent)
        }

        updateUserInfo()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my, container, false)
    }

    private fun updateUserInfo() {
        lifecycleScope.launch {
            try {
                val my = userService!!.getMine()
                root!!.visibility = View.VISIBLE
                btn_login!!.visibility = View.GONE

                Glide
                    .with(this@MyFragment)
                    .load(my!!.data!!.avatar?.original!!)
                    .into(avatar!!) // 头像
                username?.text = my.data?.username!! // 用户名

            } catch (e: Exception) {
                Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                Log.e("MyFragment", "", e)
                btn_login?.visibility = View.VISIBLE
                root?.visibility = View.GONE
            }

        }
    }

    companion object {
        @JvmStatic
        fun newInstance(): MyFragment {
            val fragment = MyFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }
}