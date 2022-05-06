package com.duoduo.mark2.api

import retrofit2.http.GET
import com.duoduo.mark2.models.BaseResponse
import com.duoduo.mark2.models.Topic
import com.duoduo.mark2.models.Article
import com.duoduo.mark2.models.Question
import retrofit2.Call
import retrofit2.http.Path
import retrofit2.http.Query

interface TopicService {
    @GET("/api/topics")
    suspend fun getList(
        @Query(value = "page", encoded = true) page: Int,
        @Query(value = "order", encoded = true) order: String = "topic_id"
    ): BaseResponse<List<Topic?>?>?

    @GET("/api/topics/{topic_id}")
    suspend fun getTopic(
        @Path(value = "topic_id") id: Int
    ): BaseResponse<Topic?>?

    @GET("/api/topics/{topic_id}/articles")
    suspend fun getArticles(
        @Path("topic_id") topic_id: Int,
        @Query("page") page: Int,
        @Query("order") order: String?,
        @Query("include") include: String = "user"
    ): BaseResponse<List<Article?>?>?

    @GET("/api/topics/{topic_id}/questions")
    suspend fun getQuestions(
        @Path("topic_id") topic_id: Int,
        @Query("page") page: Int,
        @Query("order") order: String?,
        @Query("include") include: String = "user"
    ): BaseResponse<List<Question?>?>?
}