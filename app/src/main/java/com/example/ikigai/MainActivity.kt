package com.example.ikigai

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.ikigai.databinding.ActivityMainBinding
import com.example.ikigai.service.AnimeService
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            val animeService = AnimeService.create()

            btnSearch.setOnClickListener {
                val searchedAnime = searchInputEditText.text.toString()
                val callSearchedAnime = animeService.getSearchedAnime(searchedAnime)

                callSearchedAnime.enqueue(object : Callback<SearchedAnime> {

                    override fun onResponse(
                        call: Call<SearchedAnime>,
                        response: Response<SearchedAnime>
                    ) {
                        if (response.body() != null) {
                            val searchedAnimes = response.body()!!.results
                            animeRecyclerView.adapter =
                                AnimeAdapter(this@MainActivity, searchedAnimes)
                            animeRecyclerView.layoutManager =
                                GridLayoutManager(this@MainActivity, 1)
                        }
                    }

                    override fun onFailure(call: Call<SearchedAnime>, t: Throwable) {
                    }
                })

            }
        }
    }

    class AnimeAdapter(
        private val parentActivity: AppCompatActivity,
        private val animes: List<Result>
    ) : RecyclerView.Adapter<AnimeAdapter.CustomViewHolder>() {

        inner class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.anime_item_layout, parent, false)
            return CustomViewHolder(view)
        }

        override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
            val anime = animes[position]
            val view = holder.itemView

            val name = view.findViewById<TextView>(R.id.name)
            val image = view.findViewById<ImageView>(R.id.image)
            val description = view.findViewById<TextView>(R.id.description)

            name.text = anime.title
            Picasso.get().load(anime.imageUrl).into(image)
            description.text = anime.synopsis

        }

        override fun getItemCount(): Int {
            return animes.size
        }
    }
}
