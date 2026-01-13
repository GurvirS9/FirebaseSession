package com.example.todofcrud.data

data class Todo(
    val id: String = "",
    val title: String = "",
    var completed: Boolean = false
)
