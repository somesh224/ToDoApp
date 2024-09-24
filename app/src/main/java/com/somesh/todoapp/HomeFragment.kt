package com.somesh.todoapp

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.DefaultRetryPolicy
import com.android.volley.NetworkResponse
import com.android.volley.Request
import com.android.volley.RetryPolicy
import com.android.volley.ServerError
import com.android.volley.VolleyError
import com.android.volley.toolbox.HttpHeaderParser
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.somesh.todoapp.adapter.TodoListAdapter
import com.somesh.todoapp.interfaces.RecyclerViewClickListener
import com.somesh.todoapp.model.TodoModel
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class HomeFragment : Fragment() {
    private lateinit var floatingActionButton: FloatingActionButton
    private lateinit var token: String
    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyTv: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var arrayList: ArrayList<TodoModel>
    private lateinit var todoListAdapter: TodoListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.fragment_home, container, false)
        val sharedPreferences =
            requireContext().getSharedPreferences(getString(R.string.USER_PREF), MODE_PRIVATE)
        token = sharedPreferences.getString("token", "")!!
        floatingActionButton = view.findViewById(R.id.add_task_button)
        floatingActionButton.setOnClickListener { showAlertDialog() }

        recyclerView = view.findViewById(R.id.recycler_view)
        progressBar = view.findViewById(R.id.progress_Bar)
        emptyTv = view.findViewById(R.id.empty_tv)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setHasFixedSize(true)
        getTask()
        return view
    }

    private fun getTask() {
        arrayList = ArrayList()
        progressBar.visibility = View.VISIBLE
        val url = "https://todoapi-zs2j.onrender.com/api/todo"
        val queue = Volley.newRequestQueue(requireContext())

        val jsonObjectRequest =
            object :
                JsonObjectRequest(
                    Request.Method.GET,
                    url,
                    null,
                    { response: JSONObject ->
                        try {
                            if (response.getBoolean("success")) {
                                val jsonArray: JSONArray = response.getJSONArray("todos")
                                for (i in 0 until jsonArray.length()) {
                                    val jsonObject = jsonArray.getJSONObject(i)
                                    val todoModel = TodoModel(
                                        jsonObject.getString("_id"),
                                        jsonObject.getString("title"),
                                        jsonObject.getString("description")
                                    )
                                    arrayList.add(todoModel)
                                }
                                todoListAdapter = TodoListAdapter(
                                    requireContext(),
                                    arrayList,
                                    object : RecyclerViewClickListener {
                                        override fun onItemClick(position: Int) {
                                            Toast.makeText(
                                                requireActivity(),
                                                "position $position",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }

                                        override fun onLongItemClick(position: Int) {
                                            Toast.makeText(
                                                requireActivity(),
                                                "position $position",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }

                                        override fun onEditButtonClick(position: Int) {
                                            showUpdateDialog(
                                                arrayList[position].id,
                                                arrayList[position].title,
                                                arrayList[position].description
                                            )
                                            Toast.makeText(
                                                requireActivity(),
                                                "position ${arrayList[position].title}",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }

                                        override fun onDoneButtonClick(position: Int) {
                                            Toast.makeText(
                                                requireActivity(),
                                                "position $position",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }

                                        override fun onDeleteButtonClick(position: Int) {
                                            Toast.makeText(
                                                requireActivity(),
                                                "position $position",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }

                                    })
                                recyclerView.adapter = todoListAdapter
                            }
                            progressBar.visibility = View.GONE
                        } catch (e: JSONException) {
                            e.printStackTrace()
                            progressBar.visibility = View.GONE
                        }
                    },
                    { error: VolleyError ->
                        Toast.makeText(requireContext(), error.toString(), Toast.LENGTH_SHORT)
                            .show()
                        val networkResponse: NetworkResponse? = error.networkResponse
                        if (error is ServerError && networkResponse != null) {
                            try {
                                val res =
                                    networkResponse.data?.let { data ->
                                        val charset =
                                            HttpHeaderParser.parseCharset(
                                                networkResponse.headers,
                                                "utf-8"
                                            )
                                        String(data, charset(charset))
                                    }
                                if (res != null) {
                                    val jsonObject2 = JSONObject(res)
                                    val message = jsonObject2.getString("msg")
                                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT)
                                        .show()
                                } else {
                                    // Handle the null case
                                    Toast.makeText(
                                        requireContext(),
                                        "Error parsing response",
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                }
                                progressBar.visibility = View.GONE
                            } catch (e: JSONException) {
                                e.printStackTrace()
                                progressBar.visibility = View.GONE
                            }
                        }
                        progressBar.visibility = View.GONE
                    }) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Content-Type"] = "application/json"
                    headers["Authorization"] = token
                    return headers
                }
            }

        // set retry policy
        val socketTime = 3000
        val policy: RetryPolicy =
            DefaultRetryPolicy(
                socketTime,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            )
        jsonObjectRequest.setRetryPolicy(policy)

        // add it to the RequestQueue
        queue.add(jsonObjectRequest)
    }

    private fun showAlertDialog() {
        val alertLayout = layoutInflater.inflate(R.layout.custom_dialog_layout, null)
        val titleField = alertLayout.findViewById<EditText>(R.id.title)
        val descriptionField = alertLayout.findViewById<EditText>(R.id.description)

        val alertDialogBuilder =
            AlertDialog.Builder(requireContext())
                .setView(alertLayout)
                .setTitle("Add Task")
                .setPositiveButton("Add", null)
                .setNegativeButton("Cancel", null)
                .create()

        alertDialogBuilder.setOnShowListener {
            val positiveBtn = alertDialogBuilder.getButton(AlertDialog.BUTTON_POSITIVE)
            val negativeBtn = alertDialogBuilder.getButton(AlertDialog.BUTTON_NEGATIVE)
            positiveBtn.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorAccent))
            negativeBtn.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
            positiveBtn.setOnClickListener {
                val title = titleField.text.toString()
                val description = descriptionField.text.toString()
                if (!TextUtils.isEmpty(title)) {
                    addTask(title, description)
                    alertDialogBuilder.dismiss()
                } else {
                    Toast.makeText(activity, "Please enter a title", Toast.LENGTH_SHORT).show()
                }
            }
        }
        alertDialogBuilder.show()
    }

    private fun showUpdateDialog(id: String, title: String, description: String) {
        val alertLayout = layoutInflater.inflate(R.layout.custom_dialog_layout, null)
        val titleField = alertLayout.findViewById<EditText>(R.id.title)
        val descriptionField = alertLayout.findViewById<EditText>(R.id.description)

        val alertDialogBuilder =
            AlertDialog.Builder(requireContext())
                .setView(alertLayout)
                .setTitle("Update Task")
                .setPositiveButton("Update", null)
                .setNegativeButton("Cancel", null)
                .create()

        alertDialogBuilder.setOnShowListener {
            val positiveBtn = alertDialogBuilder.getButton(AlertDialog.BUTTON_POSITIVE)
            val negativeBtn = alertDialogBuilder.getButton(AlertDialog.BUTTON_NEGATIVE)
            positiveBtn.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorAccent))
            negativeBtn.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
            positiveBtn.setOnClickListener {
                val updatedTitle = titleField.getText().toString()
                val updatedDescription = descriptionField.getText().toString()
                Toast.makeText(
                    requireActivity(),
                    "$updatedTitle  $updatedDescription",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        alertDialogBuilder.show()
    }

    // Add ToDo task method
    private fun addTask(title: String, description: String) {
        val queue = Volley.newRequestQueue(requireContext())
        val url = "https://todoapi-zs2j.onrender.com/api/todo"
        val body: HashMap<String, String> = HashMap()
        body["title"] = title
        body["description"] = description
        val jsonObject = JSONObject(body as Map<*, *>)

        // creating a json object request
        val jsonObjectRequest =
            object :
                JsonObjectRequest(
                    Method.POST,
                    url,
                    jsonObject,
                    { response: JSONObject ->
                        try {
                            if (response.getBoolean("success")) {
                                Toast.makeText(
                                    requireContext(),
                                    "Added Successfully",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                                getTask()
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    },
                    { error: VolleyError ->
                        val networkResponse: NetworkResponse? = error.networkResponse
                        if (error is ServerError && networkResponse != null) {
                            try {
                                val res =
                                    networkResponse.data?.let { data ->
                                        val charset =
                                            HttpHeaderParser.parseCharset(
                                                networkResponse.headers,
                                                "utf-8"
                                            )
                                        String(data, charset(charset))
                                    }
                                if (res != null) {
                                    val jsonObject2 = JSONObject(res)
                                    val message = jsonObject2.getString("msg")
                                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT)
                                        .show()
                                } else {
                                    // Handle the null case
                                    Toast.makeText(
                                        requireContext(),
                                        "Error Parsing Response",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } catch (e: JSONException) {
                                e.printStackTrace()
                            }
                        }
                    }) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Content-Type"] = "application/json"
                    headers["Authorization"] = token
                    return headers
                }
            }

        // set retry policy
        val socketTime = 3000
        val policy: RetryPolicy =
            DefaultRetryPolicy(
                socketTime,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            )
        jsonObjectRequest.setRetryPolicy(policy)

        // add it to the RequestQueue
        queue.add(jsonObjectRequest)
    }
}
