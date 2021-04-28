package com.example.crypytocoins

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.ContentLoadingProgressBar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.example.crypytocoins.adapters.RecyclerViewAdapter
import com.example.crypytocoins.models.Coin
import com.example.crypytocoins.models.ViewType
import com.example.crypytocoins.services.VolleySingleton
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    // Create a list with type "Coin" to keep coins data
    private val coinList = mutableListOf<Coin>()

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
        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.adapter = null
        coinList.clear()

        // Create circular progressbar to show when fetching data
        val progressBar = findViewById<ContentLoadingProgressBar>(R.id.progress_circular)
        progressBar.show()

        val url = "https://api.coinranking.com/v1/public/coins"
        val requestString = StringRequest(
            Request.Method.GET, url,
            { response ->
                val responseString = response.toString()

                // Creating JsonObject from response String, then extracting the "coins" array
                val jsonObject = JSONObject(responseString)
                val dataObject = jsonObject.getJSONObject("data")
                val coinsArray = dataObject.getJSONArray("coins")

                for (i in 0 until coinsArray.length()) {
                    val coinObject = coinsArray.getJSONObject(i)
                    val viewType =
                        if ((i + 1) % 5 == 0 && i != 0) ViewType.TITLE_ONLY else ViewType.DESCRIPTION
                    val id = coinObject.getInt("id")
                    val rank = coinObject.getInt("rank")
                    val uuid = coinObject.getString("uuid")
                    val slug = coinObject.getString("slug")
                    val symbol = coinObject.getString("symbol")
                    val name = coinObject.getString("name")
                    var description = coinObject.getString("description").replace("<p>", "")
                    if (description.isEmpty() || description == "null") {
                        description = "No description provided."
                    }
                    val iconType = coinObject.getString("iconType")
                    val iconUrl = coinObject.getString("iconUrl")

                    val coin = Coin(
                        viewType,
                        id,
                        rank,
                        uuid,
                        slug,
                        symbol,
                        name,
                        description,
                        iconType,
                        iconUrl
                    )
                    coinList.add(coin)
                }

                // Set coin list to RecyclerViewAdapter
                val recyclerViewAdapter = RecyclerViewAdapter(applicationContext)
                recyclerViewAdapter.setList(coinList)

                // Set adapter to RecyclerView
                recyclerView.apply {
                    layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                    isNestedScrollingEnabled = false
                    adapter = recyclerViewAdapter
                    onFlingListener = null
                }

                // Added all coins, hide progress bar
                progressBar.hide()
            }, { Toast.makeText(applicationContext, "Error! cannot get data", Toast.LENGTH_LONG).show() })

        // Add request to Volley queue
        VolleySingleton.getInstance(this).addToRequestQueue(requestString)
    }
}