package com.somesh.todoapp

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.volley.*
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.somesh.todoapp.UtilsService.UtilService
import com.somesh.todoapp.databinding.ActivityRegisterBinding
import org.json.JSONException
import org.json.JSONObject

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private val utilService = UtilService()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.loginBtn.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }

        binding.registerBtn.setOnClickListener {
            utilService.closeKeyBoard(this)
            binding.nameET.text.toString()
            binding.emailET.text.toString()
            binding.passwordET.text.toString()
            if (validate(view)) {
                registerUser(view)
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun registerUser(view: View) {
        binding.progressBar.visibility = View.VISIBLE
        val params: HashMap<String, String> = HashMap()
        params["name"] = binding.nameET.text.toString()
        params["email"] = binding.emailET.text.toString()
        params["password"] = binding.passwordET.text.toString()

        val apiKey = "https://todoapi-zs2j.onrender.com/api/todo/auth/register"
        val queue = Volley.newRequestQueue(this)
        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, apiKey, null,
            { response: JSONObject ->

            }, { error: VolleyError ->
                val networkResponse: NetworkResponse? = error.networkResponse
                if (error is ServerError && networkResponse != null) {
                    try {
                        val res = String(networkResponse.data)
                        val jsonObject = JSONObject(res)
                        val message = jsonObject.getString("msg")
                        utilService.showSnackBar(view, message)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
            }
        )

        fun Header(): MutableMap<String, String> {
            val headers = HashMap<String, String>()
            headers["Content-Type"] = "application/json"
            return headers
        }

        // set retry policy
        val socketTime = 3000
        val policy: RetryPolicy = DefaultRetryPolicy(
            socketTime,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        jsonObjectRequest.setRetryPolicy(policy)

        // add it to the RequestQueue
        queue.add(jsonObjectRequest)

    }

    fun validate(view: View): Boolean {
        val isValid: Boolean
        if (!TextUtils.isEmpty(binding.nameET.text.toString())) {
            if (!TextUtils.isEmpty(binding.emailET.text.toString())) {
                if (!TextUtils.isEmpty(binding.passwordET.text.toString())) {
                    isValid = true
                } else {
                    utilService.showSnackBar(view, "Please enter a password")
                    isValid = false
                }
            } else {
                utilService.showSnackBar(view, "Please enter an email")
                isValid = false
            }
        } else {
            utilService.showSnackBar(view, "Please enter a name")
            isValid = false
        }
        return isValid
    }
}