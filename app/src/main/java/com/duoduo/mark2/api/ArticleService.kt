package com.duoduo.mark2.api

import com.duoduo.mark2.models.*
import retrofit2.Call
import retrofit2.http.*

interface ArticleService {
    @GET("/api/articles/{article_id}")
    suspend fun get(
        @Path("article_id") article_id: Int,
        @Query("include") include: String = "user,topics"
    ): BaseResponse<Article?>?

    @GET("/api/articles")
    suspend fun getList(
        @Query("page") page: Int,
        @Query("order") order: String = "-create_time",
        @Query("per_page") perPage: Int = 15,
        @Query("include") include: String = "user"
    ): BaseResponse<List<Article>>

    @GET("/api/articles/{article_id}/comments")
    suspend fun getComments(
        @Path("article_id") article_id: Int,
        @Query("page") page: Int,
        @Query("order") order: String = "create_time",
        @Query("include") include: String = "user,voting",
        @Query("per_page") per_page: Int = 10
    ): BaseResponse<List<Comment>>

    @POST("/api/articles")
    suspend fun create(@Body data: PostArticleRequest?): BaseResponse<Article?>?

    @POST("/api/articles/{article_id}/comments")
    suspend fun createComment(@Path("article_id") article_id: Int, @Body data: CreateCommentRequest?): BaseResponse<Comment>
}