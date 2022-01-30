package com.example.ikigai

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.ikigai.databinding.ActivityMainBinding
import com.example.ikigai.service.AnimeService
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var database : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        var count = 0
        setContentView(binding.root)

        fun View.hideKeyboard() {
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(windowToken, 0)
        }

        binding.apply {
            val animeService = AnimeService.create()

            btnSearch.setOnClickListener {
                val searchedAnime = searchInputEditText.text.toString()
                val callSearchedAnime = animeService.getSearchedAnime(searchedAnime)
                count = count + 1

                database = FirebaseDatabase.getInstance().getReference("History")
                database.child(count.toString()).setValue(searchedAnime).addOnSuccessListener {
                    Toast.makeText(this@MainActivity,"Anime added to History", Toast.LENGTH_SHORT).show()
                }.addOnFailureListener {
                    Toast.makeText(this@MainActivity,"Failed to add to History", Toast.LENGTH_SHORT).show()
                }


                binding.root.hideKeyboard()

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
