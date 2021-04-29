package com.example.crypytocoins

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Html
import android.widget.SearchView
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
import java.util.*

class MainActivity : AppCompatActivity() {
    // Create a list with type "Coin" to keep coins data
    private val coinList = mutableListOf<Coin>()
    private var canScroll = true

    lateinit var recyclerView: RecyclerView
    lateinit var linearLayoutManager: LinearLayoutManager
    lateinit var recyclerViewAdapter: RecyclerViewAdapter
    lateinit var searchView: SearchView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.hide()

        // Initialize requestQueue
        VolleySingleton.getInstance(this).requestQueue

        recyclerView = findViewById(R.id.recycler_view)
        recyclerViewAdapter = RecyclerViewAdapter(this)
        linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        fetchCoinsData()

        // Set scrolling behavior
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                if (canScroll && !recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (recyclerViewAdapter.itemCount != coinList.size) {
                        Toast.makeText(applicationContext, "Loading...", Toast.LENGTH_SHORT).show()
                        recyclerViewAdapter.getMoreItems(coinList)
                    }
                }
            }
        })

        // Set pull to refresh behavior
        val swipeRefreshLayout: SwipeRefreshLayout = findViewById(R.id.swipe_refresh_layout)
        swipeRefreshLayout.setOnRefreshListener {
            searchView.setQuery("", false)
            searchView.clearFocus()
            canScroll = true
            fetchCoinsData()
            Handler(Looper.getMainLooper()).postDelayed({
                swipeRefreshLayout.isRefreshing = false
            }, 300)
        }

        searchView = findViewById(R.id.search_view)

        // Set searching behavior
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String): Boolean {
                return search(query)
            }

            override fun onQueryTextChange(query: String): Boolean {
                return search(query)
            }

            fun search(query: String?): Boolean {
                canScroll = false

                if (query != null && query != "") {
                    val foundCoins = coinList.filter {
                        it.name.toLowerCase(Locale.ROOT).startsWith(query) ||
                                it.symbol.toLowerCase(Locale.ROOT).startsWith(query) ||
                                it.slug.toLowerCase(Locale.ROOT).startsWith(query) ||
                                it.id.toString() == query
                    }
                    recyclerViewAdapter.setList(foundCoins as MutableList<Coin>)
                }
                return true
            }

        })
    }

    // Fetch first 10 items
    private fun fetchCoinsData() {
        coinList.clear()

        // Show circle progress loading
        val progress: ContentLoadingProgressBar = findViewById(R.id.progress_circular)
        progress.show()

        // Network operation, get all coins data to coinList
        val url = "https://api.coinranking.com/v1/public/coins"
        val requestString = StringRequest(
            Request.Method.GET,
            url,
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
                    var description = coinObject.getString("description")

                    description = if (description.isEmpty() || description == "null") {
                        "No description provided."
                    } else {
                        Html.fromHtml(description, Html.FROM_HTML_MODE_COMPACT).toString()
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

                // Set first 10 coins to adapter
                recyclerViewAdapter.clearList()
                recyclerViewAdapter.getMoreItems(coinList)

                // Set adapter to RecyclerView
                recyclerView.apply {
                    layoutManager = linearLayoutManager
                    isNestedScrollingEnabled = false
                    adapter = recyclerViewAdapter
                    onFlingListener = null
                }

                progress.hide()
            },
            {
                Toast.makeText(applicationContext, "Error! cannot get data", Toast.LENGTH_LONG)
                    .show()
            })

        // Add request to Volley queue
        VolleySingleton.getInstance(this).addToRequestQueue(requestString)
    }
}