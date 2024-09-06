package com.somesh.todoapp

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.volley.DefaultRetryPolicy
import com.android.volley.NetworkResponse
import com.android.volley.Request
import com.android.volley.RetryPolicy
import com.android.volley.ServerError
import com.android.volley.VolleyError
import com.android.volley.toolbox.HttpHeaderParser
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.somesh.todoapp.UtilsService.UtilService
import com.somesh.todoapp.databinding.ActivityLoginBinding
import org.json.JSONException
import org.json.JSONObject

class Login : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val utilService = UtilService()
    private lateinit var email: String
    private lateinit var password: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val registerBtn = findViewById<Button>(R.id.registerBtn)
        registerBtn.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        binding.loginBtn.setOnClickListener {
            utilService.closeKeyBoard(this)
            email = binding.emailET.text.toString()
            password = binding.passwordET.text.toString()
            if (validate(view)) {
                loginUser(view)
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun loginUser(view: View) {
        email = binding.emailET.text.toString()
        password = binding.passwordET.text.toString()

        binding.progressBar.visibility = View.VISIBLE
        val params: HashMap<String, String> = HashMap()
        params["email"] = email
        params["password"] = password

        //converting above hashmap to json object
        val jsonObject = JSONObject(params as Map<*, *>)

        val apiKey = "https://todoapi-zs2j.onrender.com/api/todo/auth/login"

        //creating volley string request

        val queue = Volley.newRequestQueue(this)

        //creating a json object request
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.POST, apiKey, jsonObject,
            { response: JSONObject ->
                try {
                    if(response.getBoolean("success")) {
                        val token = response.getString("token")
                        val sharedPreferences = getSharedPreferences(getString(R.string.USER_PREF), MODE_PRIVATE).edit()
                        sharedPreferences.putString("token",token)
                        sharedPreferences.apply()
                        Toast.makeText(this,token, Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    }
                    binding.progressBar.visibility = View.GONE
                } catch (e: JSONException) {
                    e.printStackTrace()
                    binding.progressBar.visibility = View.GONE
                }
            }, { error: VolleyError ->
                val networkResponse: NetworkResponse? = error.networkResponse
                if (error is ServerError && networkResponse != null) {
                    try {
                        val res = networkResponse.data?.let { data ->
                            val charset = HttpHeaderParser.parseCharset(networkResponse.headers, "utf-8")
                            String(data, charset(charset))
                        }
                        if (res != null) {
                            val jsonObject2 = JSONObject(res)
                            val message = jsonObject2.getString("msg")
                            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                        } else {
                            // Handle the null case
                            utilService.showSnackBar(view, "Error parsing response")
                        }
                        binding.progressBar.visibility = View.GONE
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        binding.progressBar.visibility = View.GONE
                    }
                }
            }
        )

        //setting headers
        fun Header(): MutableMap<String, String> {
            val headers = HashMap<String, String>()
            headers["Content-Type"] = "application/json"
            return params
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

    private fun validate(view: View): Boolean {
        val isValid: Boolean
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
        return isValid
    }

    override fun onStart() {
        super.onStart()

        val sharedPreferences = getSharedPreferences(getString(R.string.USER_PREF), MODE_PRIVATE)
        if(sharedPreferences.contains("token")) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

    }
}