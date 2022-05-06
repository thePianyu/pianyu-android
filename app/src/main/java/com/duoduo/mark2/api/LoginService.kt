package com.duoduo.mark2.api

import com.duoduo.mark2.models.*
import retrofit2.http.POST
import retrofit2.Call
import retrofit2.http.Body

interface LoginService {
    @POST("/api/tokens")
    suspend fun login(@Body data: LoginRequest?): BaseResponse<LoginResponse?>?

    @POST("/api/captchas")
    suspend fun generateCaptcha(): BaseResponse<Captcha?>?
}