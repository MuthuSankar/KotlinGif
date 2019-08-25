package com.example.kotlingif

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.Menu
import android.widget.AbsListView
import android.widget.ScrollView
import androidx.annotation.MainThread
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.*
import java.io.IOException
import java.lang.reflect.Array.get

class MainActivity : AppCompatActivity() {

        val mLayoutManager= LinearLayoutManager(this@MainActivity)

        val lastVisibleItemPosition: Int = mLayoutManager.findLastVisibleItemPosition()

        var isScrolling: Boolean = false
        var currentItem: Int = 0
        var scrollOutItems: Int = 0
        var totalItems: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {

            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_main)

            recyclerView.setLayoutManager(mLayoutManager)

            fetchJson("happy", 0)

            }

            override fun onCreateOptionsMenu(menu: Menu): Boolean{

                menuInflater.inflate(R.menu.main, menu)
                val searchItem = menu.findItem(R.id.menu_search)
                val waitingTime: Long = 200
                var cntr: CountDownTimer

                if(searchItem!=null){
                    val searchView = searchItem.actionView as SearchView

                    searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{


                        override fun onQueryTextSubmit(query: String?): Boolean{

                            return true
                        }

                        override fun onQueryTextChange(newText: String): Boolean {

                            cntr = object : CountDownTimer(waitingTime, 500) {

                                override fun onTick(millisUntilFinished: Long) {
                                    Log.d("TIME", "seconds remaining: " + millisUntilFinished / 1000)
                                }

                                override fun onFinish() {
                                    if(newText.isNotEmpty()){
                                        fetchJson(newText, 0)

                                    }
                                    Log.d("FINISHED", "DONE")
                                }
                            }
                            cntr.start()
                            return true
                            }
                  })
                }

                return super.onCreateOptionsMenu(menu)

            }

            fun fetchJson(SearchString: String, OffsetValue: Int) {


                var searchVar: String = SearchString

                var offset: Int = OffsetValue

                val API_KEY: String = BuildConfig.API_KEY

                val url =
                    "https://api.giphy.com/v1/gifs/search?api_key=" + API_KEY + "&q=" + searchVar + "=&limit=25&offset=" + offset + "&rating=G&lang=en"

                val request = Request.Builder().url(url).build()

                val client = OkHttpClient()

                client.newCall(request).enqueue(object : Callback {

                    override fun onResponse(call: Call, response: Response) {
                        val body = response?.body?.string()
                        println(body)

                        val gson = GsonBuilder().create()

                        val HomeFeed = gson.fromJson(body, Models.HomeFeed::class.java)

                        runOnUiThread {
                            recyclerView.adapter = MainAdapter(HomeFeed)
                            setRecyclerViewScrollListener(searchVar)

                        }
                    }

                override fun onFailure(call: Call, e: IOException) {
                    println("Failed to execute request")
                  }
                 })
                }

            fun newcall(offsetvalue: Int){
                fetchJson("happy", offsetvalue)
            }

            private fun setRecyclerViewScrollListener(SearchString: String) {

                recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {

                    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {

                        super.onScrollStateChanged(recyclerView, newState)

                        if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                            isScrolling = true
                        }
                    }

                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                        super.onScrolled(recyclerView, dx, dy)
                        currentItem = mLayoutManager.childCount
                        totalItems = mLayoutManager.itemCount
                        scrollOutItems = mLayoutManager.findFirstVisibleItemPosition()

                        if(isScrolling && (currentItem + scrollOutItems == totalItems)){
                            isScrolling = false
                            fetchJson(SearchString,25)
                        }
                    }
                })
            }

}