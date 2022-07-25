package com.example.movieapp.data.vo


import com.google.gson.annotations.SerializedName

data class MovieDetails(
    val homepage: String,
    val id: Int,
    val overview: String,
    @SerializedName("poster_path")
    val posterPath: String,
    @SerializedName("release_date")
    val releaseDate: String,
    val title: String,
    @SerializedName("vote_average")
    val rating: Double
)