package com.example.simplemusicplayer

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), ActivityCompat.OnRequestPermissionsResultCallback {

    private lateinit var musicList: List<RecordData>
    private lateinit var musicAdapter: MusicRecordAdapter
    lateinit var musicController: MusicController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkDiskPermission()
        searchMusic()

        val manager = LinearLayoutManager(this)
        this.musicLibraryRV.layoutManager = manager
        musicAdapter = MusicRecordAdapter(musicList)
        musicLibraryRV.adapter = this.musicAdapter

        musicController = MusicController(this, musicList)
    }

    override fun onStart() {
        super.onStart()

        Intent(this, MusicService::class.java).also { playMusicIntent ->
            bindService(playMusicIntent, musicController.serviceConnection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unbindService(musicController.serviceConnection)
        musicController.disconnectService()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_CODE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED))
                else finish()
                return
            }
            else -> {
                // Log.wtf("Permission read", "Permission to read not granted.")
            }
        }
    }

    private fun checkDiskPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSION_CODE)
        }
    }

    @SuppressLint("Recycle")
    private fun searchMusic() {
        val songsFetched: MutableList<RecordData> = mutableListOf()
        val resolver = contentResolver
        val musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

        val columns = arrayOf(
            android.provider.MediaStore.Audio.Media._ID,
            android.provider.MediaStore.Audio.Media.TITLE,
            android.provider.MediaStore.Audio.Media.ARTIST,
            android.provider.MediaStore.Audio.Media.DATA
        )

        val selectionData = "${android.provider.MediaStore.Audio.AudioColumns.IS_MUSIC} = 1"
        val cursor = resolver.query(musicUri, columns, selectionData, null, null)!!

        if (cursor.moveToFirst()) {
            val idColumn = cursor.getColumnIndex(android.provider.MediaStore.Audio.Media._ID)
            val titleColumn = cursor.getColumnIndex(android.provider.MediaStore.Audio.Media.TITLE)
            val artistColumn = cursor.getColumnIndex(android.provider.MediaStore.Audio.Media.ARTIST)
            val pathColumn = cursor.getColumnIndex(android.provider.MediaStore.Audio.Media.DATA)

            do {
                val id = cursor.getLong(idColumn)
                val title = cursor.getString(titleColumn)
                val artist = cursor.getString(artistColumn)
                val path = cursor.getString(pathColumn)
                songsFetched.add(RecordData(id, title, artist, path))

            } while (cursor.moveToNext())
        }
        musicList = songsFetched.toList()
    }

    companion object{
        const val PERMISSION_CODE = 450
    }
}
