package com.example.crypytocoins

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize requestQueue
        VolleySingleton.getInstance(this.applicationContext).requestQueue

        fetchCoinsData()

        // Set on-refresh behavior
        val swipeRefreshLayout = findViewById<SwipeRefreshLayout>(R.id.swipeRefreshLayout)
        swipeRefreshLayout.setOnRefreshListener {
            fetchCoinsData()
            Handler(Looper.getMainLooper()).postDelayed({
                swipeRefreshLayout.isRefreshing = false
            }, 500)
        }
    }

    private fun fetchCoinsData() {
        val url = "https://api.coinranking.com/v1/public/coins"
        val requestString = StringRequest(
            Request.Method.GET, url,
            { response ->
                val responseString = response.toString()

                // Creating JsonObject from response String, then extracting the "coins" array
                val jsonObject = JSONObject(responseString)
                val dataObject = jsonObject.getJSONObject("data")
                val coinsArray: JSONArray = dataObject.getJSONArray("coins")
                Log.e("Test", coinsArray[0].toString())

//                    for (i in 0 until jsonArray.length()) {
//
//                    }

            }, { Log.e("ERROR", "Can not fetch data.") })

        // Add request to Volley queue
        VolleySingleton.getInstance(this).addToRequestQueue(requestString)
    }
}