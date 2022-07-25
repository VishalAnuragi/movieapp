package com.example.movieapp.data.api

import com.example.movieapp.data.vo.MovieDetails
import com.example.movieapp.data.vo.MovieResponse
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TheMovieDBInterface {

    @GET("movie/now_playing")
    fun getNowPlayingMovie(
        @Query("page") page: Int) : Single<MovieResponse>

    @GET("movie/{movie_id}")
    fun getMovieDetails(
        @Path("movie_id") id: Int): Single<MovieDetails>

    @GET("movie/upcoming")
    fun getUpcomingMovie(
        @Query("page") page: Int) : Single<MovieResponse>
}