package com.example.game_bugs

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setContentView(GameView(this))
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

    }
}