package com.somesh.todoapp.interfaces

interface RecyclerViewClickListener {
    fun onItemClick(position: Int)
    fun onLongItemClick(position: Int)
    fun onEditButtonClick(position: Int)
    fun onDoneButtonClick(position: Int)
    fun onDeleteButtonClick(position: Int)
}