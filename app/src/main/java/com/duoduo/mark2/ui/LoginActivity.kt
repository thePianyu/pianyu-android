package com.duoduo.mark2.ui

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.duoduo.mark2.api.LoginService
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.duoduo.mark2.R
import com.duoduo.mark2.api.ApiClient
import com.duoduo.mark2.models.LoginRequest
import com.duoduo.mark2.utils.SHA1Utils
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import kotlinx.coroutines.launch
import java.lang.Exception
import java.util.*

class LoginActivity : AppCompatActivity() {

    private var service: LoginService? = null
    private var fab: ExtendedFloatingActionButton? = null
    private var username: TextInputEditText? = null
    private var password: TextInputEditText? = null
    private var captcha: TextInputEditText? = null
    private var captcha_img: ImageView? = null
    private var token: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        fab = findViewById(R.id.fab)
        username = findViewById(R.id.username)
        password = findViewById(R.id.password)
        captcha = findViewById(R.id.captcha)
        captcha_img = findViewById(R.id.captcha_img)
        captcha_img?.setOnClickListener {
            loadCaptcha()
        }

        service = ApiClient.getClient(this).create(LoginService::class.java)
        loadCaptcha()

        fab?.setOnClickListener {
            if(token.isEmpty()) {
                Toast.makeText(this@LoginActivity, "验证码获取失败", Toast.LENGTH_SHORT).show();
                return@setOnClickListener
            }
            lifecycleScope.launch {
                try {

                    val loginResponse = service?.login(LoginRequest().also {
                        it.name = username?.text.toString()
                        it.password = SHA1Utils.SHA1(password?.text.toString()).lowercase(Locale.getDefault())
                        it.captcha_token = token
                        it.captcha_code = captcha?.text.toString()
                        it.device = "Android"
                    })

                    val sp = this@LoginActivity.getSharedPreferences("MDCLUB", Context.MODE_PRIVATE).edit()
                    sp.putString("TOKEN", loginResponse!!.data!!.token!!)
                    sp.commit()

                    Toast.makeText(this@LoginActivity, "登录成功", Toast.LENGTH_SHORT).show();
                    this@LoginActivity.finish()
                } catch (e: Exception) {
                    Toast.makeText(this@LoginActivity, e.message, Toast.LENGTH_SHORT).show()
                    Log.e("LoginActivity", "login", e)
                } finally {
                    this@LoginActivity.loadCaptcha()
                }
            }
        }
    }

    private fun loadCaptcha() {
        lifecycleScope.launch {
            try {
                val captcha = service?.generateCaptcha()
                token = captcha!!.data!!.captcha_token!!
                val base64 = captcha.data!!.captcha_image!!.replace("data:image/jpeg;base64,", "")
                Glide
                    .with(this@LoginActivity)
                    .load(Base64.decode(base64, Base64.DEFAULT))
                    .into(captcha_img!!)
            } catch (e: Exception) {
                Toast.makeText(this@LoginActivity, "获取验证码失败: " + e.message, Toast.LENGTH_SHORT).show();
                Log.e("LoginActivity", "load captcha", e)
            }
        }
    }
}