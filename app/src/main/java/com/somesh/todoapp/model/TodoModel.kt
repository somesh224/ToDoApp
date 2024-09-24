package com.somesh.todoapp.model


data class TodoModel(
    val id: String,
    val title: String,
    val description: String,
    var isExpanded: Boolean = false
)