package com.hhilan.flarum

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val usernameEditText = findViewById<EditText>(R.id.username)
        val passwordEditText = findViewById<EditText>(R.id.password)
        val loginButton = findViewById<Button>(R.id.login_button)
        val resultTextView = findViewById<TextView>(R.id.result)

        loginButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()
            loginUser(username, password, resultTextView)
        }
    }

    private fun loginUser(username: String, password: String, resultTextView: TextView) {
        val client = OkHttpClient()

        val json = JSONObject().apply {
            put("identification", username)
            put("password", password)
        }

        val body = RequestBody.create(
            MediaType.parse("application/json; charset=utf-8"),
            json.toString()
        )

        val request = Request.Builder()
            .url("http://rootes.oocc.cn/api/token")
            .post(body)
            .addHeader("Content-Type", "application/json")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    resultTextView.text = "Login failed: ${e.message}"
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseBody = response.body()?.string()
                    val jsonResponse = JSONObject(responseBody)
                    val token = jsonResponse.getString("token")
                    val userId = jsonResponse.getString("userId")

                    runOnUiThread {
                        resultTextView.text = "Login successful! Token: $token, UserId: $userId"
                    }
                } else {
                    runOnUiThread {
                        resultTextView.text = "Login failed: ${response.message()}"
                    }
                }
            }
        })
    }
}
