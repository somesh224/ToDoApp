package com.somesh.todoapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.somesh.todoapp.R
import com.somesh.todoapp.databinding.TodoListItemBinding
import com.somesh.todoapp.interfaces.RecyclerViewClickListener
import com.somesh.todoapp.model.TodoModel
import java.util.Random

class TodoListAdapter(
    private val context: Context,
    private val arrayList: ArrayList<TodoModel>,
    private val clickListener: RecyclerViewClickListener
) :
    RecyclerView.Adapter<TodoListAdapter.ModelViewHolder>() {
    private lateinit var view: View

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ModelViewHolder {
        val binding = TodoListItemBinding.inflate(LayoutInflater.from(context), parent, false)
        view = binding.root
        val androidColors = view.resources.getIntArray(R.array.android_colors)
        val randomAndroidColor = androidColors[Random().nextInt(androidColors.size)]
        view.setBackgroundColor(randomAndroidColor)
        binding.accordianTitle.setBackgroundColor(randomAndroidColor)
        return ModelViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TodoListAdapter.ModelViewHolder, position: Int) {
        val title = arrayList[position].title
        val description = arrayList[position].description
        val id = arrayList[position].id
        val todoItem = arrayList[position]

        holder.binding.taskTitle.text = title
        if (description != "") {
            holder.binding.taskDescription.text = description
        }
        if (todoItem.isExpanded) {
            holder.binding.accordianBody.visibility = View.VISIBLE
        } else {
            holder.binding.accordianBody.visibility = View.GONE
        }
        holder.binding.arrow.setOnClickListener {
            todoItem.isExpanded = !todoItem.isExpanded
            notifyItemChanged(position)  // Notify adapter of the change to update UI
        }
        holder.itemView.setOnClickListener {
            clickListener.onItemClick(position)
        }

        holder.itemView.setOnLongClickListener {
            clickListener.onLongItemClick(position)
            true
        }

        holder.binding.editBtn.setOnClickListener {
            clickListener.onEditButtonClick(position)
        }

        holder.binding.deleteBtn.setOnClickListener {
            clickListener.onDeleteButtonClick(position)
        }

        holder.binding.doneBtn.setOnClickListener {
            clickListener.onDoneButtonClick(position)
        }
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    class ModelViewHolder(val binding: TodoListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {}
}