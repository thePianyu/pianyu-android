package com.duoduo.mark2.api

import retrofit2.http.GET
import com.duoduo.mark2.models.BaseResponse
import com.duoduo.mark2.models.User

interface UserService {
    @GET("/api/user")
    suspend fun getMine(): BaseResponse<User?>?
}