package com.example.todofcrud.todo.model

data class Todo(
    val id: String = "",
    val title: String = "",
    var completed: Boolean = false
)