package com.example.retrofitapp
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.activity.ComponentActivity

class MainActivity : ComponentActivity() {
    private val viewModel: TaskViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                TaskListScreen(viewModel)
            }
        }
    }
}

@Composable
fun TaskListScreen(viewModel: TaskViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showAddDialog by remember { mutableStateOf(false) }
    var newTaskTitle by remember { mutableStateOf("") }
    var showEditDialog by remember { mutableStateOf(false) }
    var editingTask by remember { mutableStateOf<Task?>(null) }
    var editTaskTitle by remember { mutableStateOf("") }
    var showDeleteAllDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(16.dp)
            .padding(top = 50.dp)
    ) {
        // Header
        Text(
            text = "Todo List",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1A1A1A),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Add Todo Button
        Button(
            onClick = {
                newTaskTitle = ""
                showAddDialog = true
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF2196F3),
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add",
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Add Todo",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Delete All Button — only show when there are tasks
        if (uiState is TaskUiState.Success) {
            OutlinedButton(
                onClick = { showDeleteAllDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color(0xFFD32F2F)
                ),
                border = BorderStroke(1.dp, Color(0xFFD32F2F))
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteSweep,
                        contentDescription = "Delete All",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Delete All",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Task List
        when (val state = uiState) {
            is TaskUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is TaskUiState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = " ${state.message}",
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { viewModel.loadTasks() }) {
                            Text("Retry")
                        }
                    }
                }
            }
            is TaskUiState.Empty -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No todos yet. Add one!",
                        color = Color.Gray,
                        fontSize = 16.sp
                    )
                }
            }
            is TaskUiState.Success -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        items = state.tasks,
                        key = { task -> task.id ?: System.identityHashCode(task) }
                    ) { task ->
                        TaskItem(
                            task = task,
                            onToggle = { viewModel.toggleTask(task) },
                            onEdit = {
                                editingTask = task
                                editTaskTitle = task.title
                                showEditDialog = true
                            },
                            onDelete = { viewModel.deleteTask(task) }
                        )
                    }
                }
            }
        }
    }

    // Add Todo Dialog
    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = {
                Text(
                    text = "Add new todo",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
            },
            text = {
                OutlinedTextField(
                    value = newTaskTitle,
                    onValueChange = { newTaskTitle = it },
                    placeholder = { Text("Add new todo", color = Color.Gray) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newTaskTitle.isNotBlank()) {
                            viewModel.addTask(newTaskTitle)
                            newTaskTitle = ""
                            showAddDialog = false
                        }
                    },
                    enabled = newTaskTitle.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2196F3)
                    )
                ) {
                    Text("Add")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showAddDialog = false
                        newTaskTitle = ""
                    }
                ) {
                    Text("Cancel", color = Color.Gray)
                }
            }
        )
    }

    // Edit Todo Dialog
    if (showEditDialog && editingTask != null) {
        AlertDialog(
            onDismissRequest = {
                showEditDialog = false
                editingTask = null
            },
            title = {
                Text(
                    text = "Edit Todo",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
            },
            text = {
                OutlinedTextField(
                    value = editTaskTitle,
                    onValueChange = { editTaskTitle = it },
                    placeholder = { Text("Update todo", color = Color.Gray) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (editTaskTitle.isNotBlank() && editingTask?.id != null) {
                            viewModel.updateTask(editingTask!!.id!!, editTaskTitle)
                            showEditDialog = false
                            editingTask = null
                        }
                    },
                    enabled = editTaskTitle.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2196F3)
                    )
                ) {
                    Text("Update")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showEditDialog = false
                        editingTask = null
                    }
                ) {
                    Text("Cancel", color = Color.Gray)
                }
            }
        )
    }

    // Delete All Confirmation Dialog
    if (showDeleteAllDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteAllDialog = false },
            title = {
                Text(
                    text = "Delete all todos?",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
            },
            text = {
                Text(
                    text = "This will permanently delete every task. This action cannot be undone.",
                    color = Color(0xFF555555)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteAllTasks()
                        showDeleteAllDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFD32F2F),
                        contentColor = Color.White
                    )
                ) {
                    Text("Delete All")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteAllDialog = false }) {
                    Text("Cancel", color = Color.Gray)
                }
            }
        )
    }
}

@Composable
fun TaskItem(
    task: Task,
    onToggle: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(8.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Checkbox
            Checkbox(
                checked = task.completed,
                onCheckedChange = { if (task.id != null) onToggle() },
                colors = CheckboxDefaults.colors(
                    checkedColor = Color(0xFF2196F3),
                    uncheckedColor = Color(0xFFBDBDBD)
                )
            )

            // Task Text
            Text(
                text = task.title,
                fontSize = 16.sp,
                color = if (task.completed) Color.Gray else Color(0xFF1A1A1A),
                modifier = Modifier.weight(1f)
            )
        }

        // Action Buttons
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            if (task.id != null) {
                // Edit Button
                IconButton(
                    onClick = onEdit,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = Color(0xFF757575),
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Delete Button
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = Color(0xFF757575),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}