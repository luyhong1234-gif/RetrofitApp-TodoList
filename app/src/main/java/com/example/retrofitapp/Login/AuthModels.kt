package com.example.retrofitapp.Login

// Request body for sign up / sign in
data class AuthRequest(
    val email: String,
    val password: String,
    val returnSecureToken: Boolean = true
)

// Response from Firebase Auth
data class AuthResponse(
    val idToken: String?,
    val email: String?,
    val refreshToken: String?,
    val expiresIn: String?,
    val localId: String?,
    val error: AuthError? = null
)

data class AuthError(
    val code: Int?,
    val message: String?
)

// Wrapper for sign-up (returns idToken too)
data class SignUpResponse(
    val idToken: String?,
    val email: String?,
    val localId: String?,
    val error: AuthError? = null
)