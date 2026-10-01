package com.example.retrofitapp

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TaskRepository {

    suspend fun fetchTasks(): List<Task> {
        return try {
            val response = RetrofitClient.api.getTodos()
            response?.map { (key, task) ->
                // Merge the Firebase key into the Task object
                task.copy(id = key)
            } ?: emptyList()
        } catch (e: Exception) {
            Log.e("TaskRepository", "Exception: ${e.localizedMessage}", e)
            emptyList()
        }
    }

    // Additional CRUD operations
    suspend fun addTask(task: Task): Result<PushResponse> {
        return try {
            val response = RetrofitClient.api.addTodo(task)
            Result.success(response)
        } catch (e: Exception) {
            Log.e("TaskRepository", "Add failed: ${e.localizedMessage}", e)
            Result.failure(e)
        }
    }

    suspend fun updateTask(id: String, task: Task): Result<Task> {
        return try {
            val response = RetrofitClient.api.updateTodo(id, task)
            Result.success(response)
        } catch (e: Exception) {
            Log.e("TaskRepository", "Update failed: ${e.localizedMessage}", e)
            Result.failure(e)
        }
    }

    suspend fun toggleTask(id: String, completed: Boolean): Result<Task> {
        return try {
            val response = RetrofitClient.api.toggleTodo(id, mapOf("completed" to completed))
            Result.success(response)
        } catch (e: Exception) {
            Log.e("TaskRepository", "Toggle failed: ${e.localizedMessage}", e)
            Result.failure(e)
        }
    }

    suspend fun deleteTask(id: String): Result<Unit> {
        return try {
            RetrofitClient.api.deleteTodo(id)
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("TaskRepository", "Delete failed: ${e.localizedMessage}", e)
            Result.failure(e)
        }
    }

    suspend fun deleteAllTasks(tasks: List<Task>): Result<Unit> {
        return try {
            tasks.forEach { task ->
                task.id?.let { id ->
                    RetrofitClient.api.deleteTodo(id)
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("TaskRepository", "Delete all failed: ${e.localizedMessage}", e)
            Result.failure(e)
        }
    }
}