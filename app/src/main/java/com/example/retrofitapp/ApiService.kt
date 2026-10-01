package com.example.retrofitapp

import retrofit2.http.*
import retrofit2.Call

interface ApiService {

    @GET("todos.json")
    suspend fun getTodos(): Map<String, Task>?  // Returns Map<firebaseKey, Task>

    @GET("todos/{id}.json")
    suspend fun getTodo(@Path("id") id: String): Task

    @POST("todos.json")
    suspend fun addTodo(@Body todo: Task): PushResponse

    @PUT("todos/{id}.json")
    suspend fun updateTodo(@Path("id") id: String, @Body todo: Task): Task

    @PATCH("todos/{id}.json")
    suspend fun toggleTodo(@Path("id") id: String, @Body fields: Map<String, Boolean>): Task

    @DELETE("todos/{id}.json")
    suspend fun deleteTodo(@Path("id") id: String): retrofit2.Response<Unit>
}