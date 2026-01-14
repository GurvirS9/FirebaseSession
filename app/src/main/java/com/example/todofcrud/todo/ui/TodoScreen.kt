package com.example.todofcrud.todo.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.todofcrud.todo.viewmodel.TodoViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import com.example.todofcrud.todo.model.Todo

@Composable
fun TodoScreen(
    viewModel: TodoViewModel = viewModel(),
    onLogout: () -> Unit
) {
    val todos by viewModel.todos.collectAsState()
    var text by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    var editingTodo by remember { mutableStateOf<Todo?>(null) }
    var editingText by remember { mutableStateOf("") }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            // Header for title and logout button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 48.dp, start = 24.dp, end = 24.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Basic To-Do List",
                    style = MaterialTheme.typography.headlineMedium
                )
                Button(onClick = onLogout) {
                    Text("Logout")
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // Input area
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    label = { Text("Enter task") },
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        if (text.isNotBlank()) {
                            viewModel.addTodo(text)
                            text = ""
                        }
                    }
                ) {
                    Text("Add")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn {
                items(todos) { todo ->
                    TodoItem(
                        todo = todo,
                        onCheckedChange = { isChecked ->
                            // Update todo
                            viewModel.updateTodo(todo.copy(completed = isChecked))
                        },
                        onEdit = {
                            editingTodo = todo
                            editingText = todo.title
                            showDialog = true
                        },
                        onDelete = {
                            // Delete todo
                            viewModel.deleteTodo(todo.id)
                        }
                    )
                }
            }
        }
    }


    // Edit Dialog
    if (showDialog && editingTodo != null) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Edit Task") },
            text = {
                OutlinedTextField(
                    value = editingText,
                    onValueChange = { editingText = it },
                    label = { Text("Task Name") }
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (editingText.isNotBlank()) {
                            editingTodo?.let { todo ->
                                viewModel.updateTodo(todo.copy(title = editingText))
                            }
                            showDialog = false
                        }
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
