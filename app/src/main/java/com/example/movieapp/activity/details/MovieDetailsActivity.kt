package com.example.movieapp.activity.details

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.example.movieapp.R
import com.example.movieapp.data.api.POSTER_BASE_URL
import com.example.movieapp.data.api.TheMovieDBClient
import com.example.movieapp.data.api.TheMovieDBInterface
import com.example.movieapp.data.repository.NetworkState
import com.example.movieapp.data.repository.details.MovieDetailsRepository
import com.example.movieapp.data.vo.MovieDetails
import kotlinx.android.synthetic.main.activity_details.*
import java.text.NumberFormat
import java.util.*

class MovieDetailsActivity : AppCompatActivity() {

    private lateinit var viewModel : MovieDetailsViewModel
    private lateinit var movieRepository: MovieDetailsRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            this.supportActionBar!!.hide()
        } catch (e: NullPointerException) { }

        setContentView(R.layout.activity_details)

        val movieId: Int = intent.getIntExtra("id", 1)

        val apiService : TheMovieDBInterface = TheMovieDBClient.getClient()
        movieRepository =
            MovieDetailsRepository(
                apiService
            )

        viewModel = getViewModel(movieId)

        viewModel.movieDetails.observe(this, Observer {
            bindUI(it)
        })

        viewModel.networkState.observe(this, Observer {
            progress_bar.visibility = if (it == NetworkState.LOADING) View.VISIBLE else View.GONE
            error_text.visibility = if (it == NetworkState.ERROR) View.VISIBLE else View.GONE
        })
    }

    fun bindUI( it: MovieDetails){
        tv_movie_title.text = it.title
        tv_movie_releaseDate.text = it.releaseDate
        tv_movie_rating.text = it.rating.toString()
        tv_movie_overview.text = it.overview

        val formatCurrency = NumberFormat.getCurrencyInstance(Locale.US)

        val moviePosterURL = POSTER_BASE_URL + it.posterPath
        Glide.with(this)
            .load(moviePosterURL)
            .into(iv_movie_backdrop);
    }

    private fun getViewModel(movieId: Int) : MovieDetailsViewModel {
        return ViewModelProviders.of(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return MovieDetailsViewModel(movieRepository, movieId) as T
            }
        })[MovieDetailsViewModel::class.java]
    }
}