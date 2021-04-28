package com.example.crypytocoins

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.example.crypytocoins.models.Coin
import com.example.crypytocoins.models.ViewType
import com.example.crypytocoins.services.VolleySingleton
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
                val coinsArray = dataObject.getJSONArray("coins")

                // Create a list with type "Coin" to keep coins data
                val coinList: MutableList<Coin> = ArrayList()

                for (i in 0 until coinsArray.length()) {
                    val coinObject = coinsArray.getJSONObject(i)
                    val viewType = if ((i+1) % 5 == 0 && i != 0) ViewType.TITLE_ONLY else ViewType.DESCRIPTION
                    val id = coinObject.getInt("id")
                    val rank = coinObject.getInt("rank")
                    val uuid = coinObject.getString("uuid")
                    val slug = coinObject.getString("slug")
                    val symbol = coinObject.getString("symbol")
                    val name = coinObject.getString("name")
                    val description = coinObject.getString("description")
                    val iconType = coinObject.getString("iconType")
                    val iconUrl = coinObject.getString("iconUrl")

                    val coin = Coin(viewType, id, rank, uuid, slug, symbol, name, description, iconType, iconUrl)
                    coinList.add(coin)
                    Log.e("TEST: ", "$rank : $viewType")
                }

            }, { Log.e("ERROR", "Can not fetch data.") })

        // Add request to Volley queue
        VolleySingleton.getInstance(this).addToRequestQueue(requestString)
    }
}