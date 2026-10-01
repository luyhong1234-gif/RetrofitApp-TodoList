package com.example.retrofitapp

// Unified data model
data class Task(
    val id: String? = null,
    val title: String = "",
    val completed: Boolean = false
)

// Firebase response wrapper
data class PushResponse(
    val name: String // Firebase generates key in "name"
)