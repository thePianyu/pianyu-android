package com.duoduo.mark2.api

import com.duoduo.mark2.models.*
import retrofit2.http.POST
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CommentService {
    @POST("/api/comments/{comment_id}/voters")
    suspend fun addVote(
        @Path("comment_id") data: Int,
        @Body body: VoteAction
    ): BaseResponse<VoteCount?>?

    @DELETE("/api/comments/{comment_id}/voters")
    suspend fun deleteVote(
        @Path("comment_id") data: Int
    ): BaseResponse<VoteCount?>?

    @GET("/api/comments/{comment_id}/replies")
    suspend fun getReplies(
        @Path("comment_id") data: Int,
        @Query("include") include: String = "user,voting",
        @Query("order") order: String = "create_time",
        @Query("per_page") per_page: Int = 10,
        @Query("page") page: Int
    ): BaseResponse<List<Comment>>

    @POST("/api/comments/{comment_id}/replies")
    suspend fun createReply(
        @Path("comment_id") comment_id: Int,
        @Body body: CreateCommentRequest
    ): BaseResponse<Comment?>?

}