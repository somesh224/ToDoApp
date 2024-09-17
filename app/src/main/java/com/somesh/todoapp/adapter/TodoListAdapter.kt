package com.somesh.todoapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.somesh.todoapp.R
import com.somesh.todoapp.databinding.TodoListItemBinding
import com.somesh.todoapp.model.TodoModel
import java.util.Random

class TodoListAdapter(private val context: Context,
                      private val arrayList: ArrayList<TodoModel>) :
    RecyclerView.Adapter<TodoListAdapter.ModelViewHolder>() {
    private lateinit var view: View
    private lateinit var binding: TodoListItemBinding

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ModelViewHolder {
        binding = TodoListItemBinding.inflate(LayoutInflater.from(context), parent, false)
        view = LayoutInflater.from(context).inflate(R.layout.todo_list_item, parent, false)
        val androidColors = view.resources.getIntArray(R.array.android_colors)
        val randomAndroidColor = androidColors[Random().nextInt(androidColors.size)]
        view.setBackgroundColor(randomAndroidColor)
        binding.accordianTitle.setBackgroundColor(randomAndroidColor)
        return ModelViewHolder(view)
    }

    override fun onBindViewHolder(holder: TodoListAdapter.ModelViewHolder, position: Int) {
        val title = arrayList[position].title
        val description = arrayList[position].description
        val id = arrayList[position].id

        holder.binding.taskTitle.text = title
        if (description != "") {
            holder.binding.taskDescription.text = description
        }
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    class ModelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = TodoListItemBinding.bind(itemView)
    }
}