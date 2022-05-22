package com.raywenderlich.android.taskie.networking

import com.raywenderlich.android.taskie.model.Task
import com.raywenderlich.android.taskie.model.request.AddTaskRequest
import com.raywenderlich.android.taskie.model.request.UserDataRequest
import com.raywenderlich.android.taskie.model.response.CompleteTaskResponse
import com.raywenderlich.android.taskie.model.response.GetTasksResponse
import com.raywenderlich.android.taskie.model.response.LoginResponse
import com.raywenderlich.android.taskie.model.response.RegisterResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface RemoteApiService {

    @POST("/api/register")
    fun registerUser(@Body request: UserDataRequest): Call<RegisterResponse>

    @GET("/api/note")
    fun getNotes(@Header("Authorization") token: String): Call<GetTasksResponse>

    @POST("/api/login")
    fun loginUser(@Body request: UserDataRequest): Call<LoginResponse>

    @GET("/api/user/profile")
    fun getMyProfile(@Header("Authorization") token: String): Call<ResponseBody>

    @POST("/api/note/complete")
    fun completeTask(@Header("Authorization") token: String,
                     @Query("id") noteId: String): Call<CompleteTaskResponse>

    @POST("/api/note")
    fun addTask(@Header("Authorization") token: String,
                @Body request: AddTaskRequest): Call<Task>
}