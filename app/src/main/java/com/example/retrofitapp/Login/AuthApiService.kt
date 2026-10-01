package com.example.retrofitapp.Login

import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface AuthApiService {

    // Firebase Auth REST API — sign up
    @POST("v1/accounts:signUp")
    suspend fun signUp(
        @Query("key") apiKey: String,
        @Body body: AuthRequest
    ): SignUpResponse

    // Firebase Auth REST API — sign in
    @POST("v1/accounts:signInWithPassword")
    suspend fun signIn(
        @Query("key") apiKey: String,
        @Body body: AuthRequest
    ): AuthResponse
}