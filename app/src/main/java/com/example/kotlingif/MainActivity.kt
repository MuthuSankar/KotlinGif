package com.example.kotlingif

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.ScrollView
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.*
import java.io.IOException
import java.lang.reflect.Array.get

class MainActivity : AppCompatActivity() {

    //lateinit var scrollListener: RecyclerView.OnScrollListener


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView.layoutManager = LinearLayoutManager(this@MainActivity)

        fetchJson("happy", 0)

       // val lastVisibleItemPosition: Int = (recyclerView.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
       // println(lastVisibleItemPosition)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean{

        menuInflater.inflate(R.menu.main, menu)
        val searchItem = menu.findItem(R.id.menu_search)
        if(searchItem!=null){
            val searchView = searchItem.actionView as SearchView

            //searchView.suggestionsAdapter

            searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{


                override fun onQueryTextSubmit(query: String?): Boolean{

                    return true
                }

                override fun onQueryTextChange(newText: String): Boolean {
                    if(newText.isNotEmpty()){
                        fetchJson(newText, 0)
                    }
                    return true
                    }
          })
        }

        return super.onCreateOptionsMenu(menu)

    }

    fun fetchJson(SearchString: String, OffsetValue: Int){


        var searchVar: String = SearchString

        var offset: Int = OffsetValue

        val API_KEY: String = BuildConfig.API_KEY

        val url ="https://api.giphy.com/v1/gifs/search?api_key="+API_KEY+"&q=" + searchVar +"=&limit=25&offset="+offset+"&rating=G&lang=en"

        val request = Request.Builder().url(url).build()

        val client = OkHttpClient()

        client.newCall(request).enqueue(object: Callback {

            override fun onResponse(call: Call, response: Response){
                val body = response?.body?.string()
                println(body)

                val gson = GsonBuilder().create()

                val HomeFeed = gson.fromJson(body, Models.HomeFeed::class.java)

                runOnUiThread {
                    recyclerView.adapter = MainAdapter(HomeFeed)
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                println("Failed to execute request")

            }

        })
    }

}


