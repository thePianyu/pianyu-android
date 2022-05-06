package com.duoduo.mark2.api

import com.duoduo.mark2.models.*
import retrofit2.Call
import retrofit2.http.*

interface QuestionService {
    @GET("/api/questions/{question_id}")
    suspend fun get(@Path("question_id") question_id: Int, @Query("include") include: String = "user"): BaseResponse<Question?>?

    @GET("/api/questions")
    suspend fun getList(
        @Query("page") page: Int,
        @Query("order") order: String = "-create_time",
        @Query("per_page") perPage: Int = 15,
        @Query("include") include: String = "user"
    ): BaseResponse<List<Question>>

    @GET("/api/questions/{question_id}/answers")
    suspend fun getAnswers(
        @Path("question_id") question_id: Int,
        @Query("page") page: Int,
        @Query("order") order: String = "-create_time",
        @Query("include") include: String = "user"
    ): BaseResponse<List<Answer>>

    @POST("/api/questions/{question_id}/voters")
    suspend fun addVote(
        @Path("question_id") data: Int,
        @Body body: VoteAction
    ): BaseResponse<VoteCount?>?

    @DELETE("/api/questions/{question_id}/voters")
    suspend fun deleteVote(
        @Path("question_id") data: Int
    ): BaseResponse<VoteCount?>?
}