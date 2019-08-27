package com.example.kotlingif

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.AbsListView
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.*
import java.io.IOException

class MainActivity : AppCompatActivity() {

    val mLayoutManager = LinearLayoutManager(this@MainActivity)

    var isScrolling: Boolean = false
    var currentItem: Int = 0
    var scrollOutItems: Int = 0
    var totalItems: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {

      super.onCreate(savedInstanceState)
      setContentView(R.layout.activity_main)

      recyclerView.layoutManager = mLayoutManager

      fetchJson("happy", 0)

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {

      menuInflater.inflate(R.menu.main, menu)
      val searchItem = menu.findItem(R.id.menu_search)
      val waitingTime: Long = 200
      var timerCount: CountDownTimer

      if (searchItem != null) {

        val searchView = searchItem.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

          override fun onQueryTextSubmit(query: String?): Boolean {

            return true
          }

          override fun onQueryTextChange(newText: String): Boolean {

            timerCount = object : CountDownTimer(waitingTime, 500) {

              override fun onTick(millisUntilFinished: Long) {

              }

              override fun onFinish() {

                if (newText.isNotEmpty()) {

                  fetchJson(newText, 0)
                }
              }
            }
            timerCount.start()
            return true
          }
        })
      }
      return super.onCreateOptionsMenu(menu)
    }

    fun fetchJson(SearchString: String, OffsetValue: Int) {


      val searchVar: String = SearchString

      val offset: Int = OffsetValue

      val API_KEY: String = BuildConfig.API_KEY

      val url = "https://api.giphy.com/v1/gifs/search?api_key=$API_KEY&q=$searchVar=&limit=25&offset=$offset&rating=G&lang=en"

      val request = Request.Builder().url(url).build()
      val client = OkHttpClient()

      client.newCall(request).enqueue(object : Callback {

        override fun onResponse(call: Call, response: Response) {

          val body = response.body?.string()

          val gson = GsonBuilder().create()

          val HomeFeed = gson.fromJson(body, Models.HomeFeed::class.java)

          runOnUiThread {   // Avoid Clogging Main Thread

            recyclerView.adapter = MainAdapter(HomeFeed)     // Setting the adapter for Recycler View
            setRecyclerViewScrollListener(searchVar, OffsetValue)   // Setting On Scroll Listener for Recycler View
          }
        }

        override fun onFailure(call: Call, e: IOException) {

          println("Failed to execute request")
        }
      })
    }

    private fun setRecyclerViewScrollListener(SearchString: String, OffsetIncreament: Int) {

      recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {

          super.onScrollStateChanged(recyclerView, newState)

          if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {

            isScrolling = true
            progressBar.visibility = View.VISIBLE   // Progress Bar Set to be visible
          }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {

          super.onScrolled(recyclerView, dx, dy)
          currentItem = mLayoutManager.childCount   // Currently Visible Items in the layout
          totalItems = mLayoutManager.itemCount   //Total Item In the Layout
          scrollOutItems = mLayoutManager.findFirstVisibleItemPosition()  //Scrolled out of the screen its on Top

          if (isScrolling && (currentItem + scrollOutItems == totalItems)) {  //When 2 + 23 = 25

            isScrolling = false   //Set isScrolling to False
            fetchJson(SearchString, OffsetIncreament + 25)   // Call method with Offset value of 25 without changing Search String
          }
        }
      })
    }
}