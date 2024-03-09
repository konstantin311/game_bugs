package com.example.game_bugs

import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class GameOver :  AppCompatActivity() {
    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_over)

        val restartButton = findViewById<Button>(R.id.restart)
        val textView = findViewById<TextView>(R.id.textView)

        restartButton.setOnClickListener{
            setContentView(GameView(this))
            mediaPlayer?.stop()
        }

        val mes = intent.getStringExtra("message")

        textView.text = mes
        if(mes.equals("Жуки победили(((")) {
            mediaPlayer = MediaPlayer.create(this, R.raw.lose_music)
            mediaPlayer?.let {
                it.isLooping = false
                it.start()
            }
        } else {
            mediaPlayer = MediaPlayer.create(this, R.raw.win_music)
            mediaPlayer?.let {
                it.isLooping = true
                it.start()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
    }

}