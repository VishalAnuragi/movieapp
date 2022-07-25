package com.example.movieapp.activity.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.movieapp.R
import com.example.movieapp.activity.Splashscreen
import com.example.movieapp.data.api.TheMovieDBClient
import com.example.movieapp.data.api.TheMovieDBInterface
import com.example.movieapp.data.api.language
import com.example.movieapp.data.repository.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MovieViewModel
    lateinit var movieRepository: MovieRepository

    val movieAdapter = MovieAdapter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        refreshLayout()
    }

    private fun refreshLayout() {
        val apiService : TheMovieDBInterface = TheMovieDBClient.getClient()
        movieRepository = MovieRepository(apiService)
        viewModel = getViewModel()

        val layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.VERTICAL,
            false
        )

        rv_movie_now_playing.layoutManager = layoutManager
        rv_movie_now_playing.setHasFixedSize(true)
        rv_movie_now_playing.adapter = movieAdapter

        viewModel.moviePagedList.observe(this, Observer {
            movieAdapter.submitList(it)
        })

        viewModel.networkState.observe(this, Observer {
            progress_bar_now_playing.visibility = if (viewModel.isEmpty() && it == NetworkState.LOADING) View.VISIBLE else View.GONE
            error_text_now_playing.visibility = if (viewModel.isEmpty() && it == NetworkState.ERROR) View.VISIBLE else View.GONE

            if (!viewModel.isEmpty()) {
                movieAdapter.setNetworkState(it)
            }
        })
    }

    private fun getViewModel(): MovieViewModel {
        return ViewModelProviders.of(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return MovieViewModel(
                    movieRepository
                ) as T
            }
        })[MovieViewModel::class.java]
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.nav_back -> {
                val intent = Intent(this, Splashscreen::class.java)
                startActivity(intent)
                finish()
            }
        }

        return super.onOptionsItemSelected(item)
    }
}