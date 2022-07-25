package com.example.movieapp.data.repository

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.movieapp.data.vo.Movie
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import com.bumptech.glide.Glide
import com.example.movieapp.R
import com.example.movieapp.activity.details.MovieDetailsActivity
import com.example.movieapp.data.api.POSTER_BASE_URL
import io.reactivex.annotations.NonNull
import kotlinx.android.synthetic.main.item_movie.view.*
import kotlinx.android.synthetic.main.networks_state_item.view.*

class MovieAdapter(public val context: Context) : PagedListAdapter<Movie, RecyclerView.ViewHolder>(MovieDiffCallback()){

    val MOVIE_VIEW_TYPE = 1
    val NETWORK_VIEW_TYPE = 2
    var arrayListFiltered : MutableList<Movie?> = mutableListOf()

    private var networkState: NetworkState? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view : View

        if (viewType == MOVIE_VIEW_TYPE) {
            view = layoutInflater.inflate(R.layout.item_movie, parent, false)
            return MovieItemViewViewHolder(view)
        } else {
            view = layoutInflater.inflate(R.layout.networks_state_item, parent, false)
            return NetworkStateItemViewHolder(view)
        }
    }

    override fun onBindViewHolder(@NonNull holder: RecyclerView.ViewHolder, position: Int) {

        val sharedPreference = context.getSharedPreferences("FAV_PREF",Context.MODE_PRIVATE)
        var editor = sharedPreference.edit()



        if (getItemViewType(position) == MOVIE_VIEW_TYPE) {
            var moviePosterURL = ""
            holder.itemView.item_movie_overview.maxLines = 7


                holder.itemView.item_movie_title.text = getItem(position)?.title
                holder.itemView.item_movie_release.text = getItem(position)?.releaseDate
                holder.itemView.item_movie_overview.text = getItem(position)?.overview
                moviePosterURL = POSTER_BASE_URL + getItem(position)?.posterPath

                val fabStatus =  sharedPreference.getString(getItem(position)?.id.toString(),"0")
                if (fabStatus == "1")
                {
                    holder.itemView.item_favorite.setImageResource(R.drawable.heart_fill)
                }
                else
                {
                    holder.itemView.item_favorite.setImageResource(R.drawable.heart)
                }


            Glide.with(holder.itemView.context)
                .load(moviePosterURL)
                .into(holder.itemView.item_movie_poster)


            holder.itemView.setOnClickListener {
                val intent = Intent(context, MovieDetailsActivity::class.java)
                intent.putExtra("id", getItem(position)?.id)
                context.startActivity(intent)
            }

            holder.itemView.item_favorite.setOnClickListener{
               val fabStatus =  sharedPreference.getString(getItem(position)?.id.toString(),"0")

                if ( fabStatus == "null" || fabStatus == "0" )  {
                        editor.putString(getItem(position)?.id.toString(),"1")
                        editor.apply()

                        holder.itemView.item_favorite.setImageResource(R.drawable.heart_fill)
                    Toast.makeText(context, "Added to favorites !", Toast.LENGTH_SHORT).show()
                    } else {

                    editor.putString(getItem(position)?.id.toString(),"0")
                    editor.apply()

                        holder.itemView.item_favorite.setImageResource(R.drawable.heart)
                    Toast.makeText(context, "Removed from favorites !", Toast.LENGTH_SHORT).show()
                    }

                }
        } else {
            (holder as NetworkStateItemViewHolder).bind(networkState)
        }
    }

    private fun resetList() {

        arrayListFiltered.clear()

    }

    private fun createViews(arrayListFiltered: MutableList<Movie?>, holder: RecyclerView.ViewHolder) {

        var moviePosterURL = ""
        holder.itemView.item_movie_overview.maxLines = 7

        for (i in 0..arrayListFiltered.size - 1) {
            holder.itemView.item_movie_title.text = arrayListFiltered.get(i)?.title
            holder.itemView.item_movie_release.text = arrayListFiltered.get(i)?.releaseDate
            holder.itemView.item_movie_overview.text = arrayListFiltered.get(i)?.overview
            moviePosterURL = POSTER_BASE_URL + arrayListFiltered.get(i)?.posterPath
        }

        Glide.with(holder.itemView.context)
            .load(moviePosterURL)
            .into(holder.itemView.item_movie_poster)

        resetList()
    }

    private fun hasExtraRow() : Boolean {
        return networkState != null && networkState != NetworkState.LOADED
    }

    override fun getItemCount(): Int {
        return super.getItemCount()
    }

    override fun getItemViewType(position: Int): Int {
        return if (hasExtraRow() && position == itemCount - 1) {
            NETWORK_VIEW_TYPE
        } else {
            MOVIE_VIEW_TYPE
        }
    }


    class MovieDiffCallback : DiffUtil.ItemCallback<Movie>() {
        override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean {
            return oldItem == newItem
        }
    }

    class MovieItemViewViewHolder (view: View) : RecyclerView.ViewHolder(view) {
    }

    class NetworkStateItemViewHolder (view: View)
        : RecyclerView.ViewHolder(view) {

        fun bind(networkState: NetworkState?) {
            if (networkState != null && networkState == NetworkState.LOADING) {
                itemView.progress_bar_item.visibility = View.VISIBLE
            } else {
                itemView.progress_bar_item.visibility = View.GONE
            }

            if (networkState != null && networkState == NetworkState.ERROR) {
                itemView.error_message_item.visibility = View.VISIBLE
                itemView.error_message_item.text = networkState.msg
            } else if (networkState != null && networkState == NetworkState.ENDOFLIST){
                itemView.error_message_item.visibility = View.VISIBLE
                itemView.error_message_item.text = networkState.msg
            } else {
                itemView.error_message_item.visibility = View.GONE
            }
        }
    }

    fun setNetworkState (newNetworkState: NetworkState) {
        val previousState = this.networkState
        val hadExtraRow = hasExtraRow()
        this.networkState = newNetworkState
        val hasExtraRow = hasExtraRow()

        if (hadExtraRow != hasExtraRow) {
            if (hadExtraRow) {
                notifyItemRemoved(super.getItemCount())
            } else {
                notifyItemInserted(super.getItemCount())
            }
        } else if (hasExtraRow && previousState != newNetworkState) {
            notifyItemChanged(itemCount - 1)
        }
    }
}