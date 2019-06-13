package com.example.simplemusicplayer

import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MusicRecordAdapter(private val musicList: List<RecordData>):
    RecyclerView.Adapter<MusicRecordAdapter.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.music_entry, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = musicList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val record = musicList[position]
        holder.title.text = record.title
        holder.artist.text = record.artist
        setCover(record.path, holder.cover)
    }

    private fun setCover(path: String, image: ImageView) {
        val dataRetriever = MediaMetadataRetriever()
        dataRetriever.setDataSource(path)
        val cover = dataRetriever.embeddedPicture
        if (cover != null) {
            image.setImageBitmap(BitmapFactory.decodeByteArray(cover, 0, cover.size))
        } else {
            image.setImageResource(R.drawable.ic_launcher_background)
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.titleTV)
        val artist: TextView= itemView.findViewById(R.id.artistTV)
        val cover: ImageView = itemView.findViewById(R.id.coverIV)

        init {
            itemView.setOnClickListener {
                (itemView.context as MainActivity).musicController.songChosen(adapterPosition)
            }
        }
    }

}
