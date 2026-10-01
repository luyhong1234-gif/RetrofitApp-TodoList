package com.example.retrofitapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class TaskViewModel : ViewModel() {
    private val repository = TaskRepository()

    private val _uiState = MutableStateFlow<TaskUiState>(TaskUiState.Loading)
    val uiState: StateFlow<TaskUiState> = _uiState.asStateFlow()

    // Dialog states
    private val _showAddDialog = MutableStateFlow(false)
    val showAddDialog: StateFlow<Boolean> = _showAddDialog.asStateFlow()

    private val _showEditDialog = MutableStateFlow(false)
    val showEditDialog: StateFlow<Boolean> = _showEditDialog.asStateFlow()

    private val _editingTask = MutableStateFlow<Task?>(null)
    val editingTask: StateFlow<Task?> = _editingTask.asStateFlow()

    init {
        loadTasks()
    }

    fun loadTasks() {
        viewModelScope.launch {
            _uiState.value = TaskUiState.Loading
            try {
                val tasks = repository.fetchTasks()
                _uiState.value = if (tasks.isEmpty()) {
                    TaskUiState.Empty
                } else {
                    TaskUiState.Success(tasks)
                }
            } catch (e: Exception) {
                _uiState.value = TaskUiState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    fun showAddDialog(show: Boolean) {
        _showAddDialog.value = show
    }

    fun showEditDialog(task: Task?) {
        _editingTask.value = task
        _showEditDialog.value = task != null
    }

    fun addTask(title: String) {
        if (title.isBlank()) return
        viewModelScope.launch {
            val newTask = Task(title = title, completed = false)
            try {
                repository.addTask(newTask)
                _showAddDialog.value = false
                loadTasks()
            } catch (e: Exception) {
                _uiState.value = TaskUiState.Error("Failed to add task: ${e.message}")
            }
        }
    }

    fun updateTask(id: String, newTitle: String) {
        if (newTitle.isBlank()) return
        viewModelScope.launch {
            try {
                val currentState = _uiState.value
                if (currentState is TaskUiState.Success) {
                    val task = currentState.tasks.find { it.id == id }
                    task?.let {
                        val updatedTask = it.copy(title = newTitle)
                        repository.updateTask(id, updatedTask)
                        _showEditDialog.value = false
                        _editingTask.value = null
                        loadTasks()
                    }
                }
            } catch (e: Exception) {
                _uiState.value = TaskUiState.Error("Failed to update task: ${e.message}")
            }
        }
    }

    fun toggleTask(task: Task) {
        task.id?.let { id ->
            viewModelScope.launch {
                try {
                    repository.toggleTask(id, !task.completed)
                    loadTasks()
                } catch (e: Exception) {
                    _uiState.value = TaskUiState.Error("Failed to toggle task: ${e.message}")
                }
            }
        }
    }

    fun deleteTask(task: Task) {
        task.id?.let { id ->
            viewModelScope.launch {
                try {
                    repository.deleteTask(id)
                    loadTasks()
                } catch (e: Exception) {
                    _uiState.value = TaskUiState.Error("Failed to delete task: ${e.message}")
                }
            }
        }
    }
    fun deleteAllTasks() {
        val currentState = _uiState.value
        if (currentState !is TaskUiState.Success) return

        viewModelScope.launch {
            val result = repository.deleteAllTasks(currentState.tasks)
            if (result.isSuccess) {
                loadTasks()
            } else {
                _uiState.value = TaskUiState.Error(
                    "Failed to delete all tasks: ${result.exceptionOrNull()?.message}"
                )
            }
        }
    }

}

sealed class TaskUiState {
    object Loading : TaskUiState()
    object Empty : TaskUiState()
    data class Success(val tasks: List<Task>) : TaskUiState()
    data class Error(val message: String) : TaskUiState()
}