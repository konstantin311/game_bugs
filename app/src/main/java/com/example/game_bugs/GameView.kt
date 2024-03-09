package com.example.game_bugs

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.media.AudioManager
import android.media.SoundPool
import android.view.MotionEvent
import android.view.View
import java.util.Timer
import java.util.Vector
import kotlinx.coroutines.*
import kotlin.concurrent.timerTask



class GameView(context: Context) : View(context) {

    private val bugsBitmaps: MutableMap<String, Array<Bitmap?>> = mutableMapOf()
    private val bugs: Vector<Bug> = Vector()
    private val bugFramesCount: Int = 2
    private var timer: Timer? = null
    private var score: Int = 0
    private var attempts: Int = 10
    private val sounds = SoundPool(20, AudioManager.STREAM_MUSIC, 0);
    private val hit = sounds.load(context, R.raw.hit, 1);
    private val miss = sounds.load(context, R.raw.miss, 1);
    private var displayWidth: Int = 0
    private var displayHeight: Int = 0
    private val background: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.background)
    override fun onDraw(canvas: Canvas) {

        super.onDraw(canvas)
        postInvalidate()
        val paint = Paint()
        canvas.drawBitmap(background, 0f, 0f, null)
        paint.textSize = 50f
        paint.color = Color.GREEN
        canvas.drawText("Счет убитых жуков: $score", 50f, 110f, paint)
        canvas.drawText("Осталось промахов: $attempts", 50f, 50f, paint)
        synchronized(bugs){
            for (bug in bugs) {
                canvas.drawBitmap(bug.getBugBitmap(bug.frameIndex)!!, bug.x, bug.y, null)


                ++bug.frameIndex

                if(bug.frameIndex >= bugFramesCount) {
                    bug.frameIndex = 0
                }

            }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            synchronized(bugs) {
                for (bug in bugs) {
                    if (event.x >= bug.x && event.x < bug.x + bug.getBugWidth()
                        && event.y >= bug.y && event.y < bug.y + bug.getBugHeight() && bug.isAlive
                    ) {
                        bug.kill()
                        sounds.play(hit, 1.0f, 1.0f, 0, 0, 1.5f)
                        score++
                        if (score == 30 || attempts <= 0) gameOver()

                        handler.postDelayed({
                            synchronized(bugs) {
                                bugs.remove(bug)
                            }
                        }, 5000)

                        return true
                    }
                }
            }
            sounds.play(miss, 1.0f, 1.0f, 0, 0, 1.5f)
            attempts--
            if (attempts <= 0) gameOver()
        }
        return super.onTouchEvent(event)
    }

    @SuppressLint("NewApi")
    private fun spawnRandBug() {
        val bugBitmap: Array<Bitmap?> = arrayOfNulls<Bitmap>(3)
        val crashedBitmap: Bitmap?
        when ((0..2).random()) {
            0 -> {
                bugBitmap[0] = bugsBitmaps["spider"]!![0]?.let { Bitmap.createScaledBitmap(it, 200, 300, true) }
                bugBitmap[1] = bugsBitmaps["spider"]!![1]?.let { Bitmap.createScaledBitmap(it, 200, 300, true) }
                crashedBitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(resources, R.drawable.killspiders), 200, 300, true)
            }
            1 -> {
                bugBitmap[0] = bugsBitmaps["honeybee"]!![0]?.let { Bitmap.createScaledBitmap(it, 200, 300, true) }
                bugBitmap[1] = bugsBitmaps["honeybee"]!![1]?.let { Bitmap.createScaledBitmap(it, 200, 300, true) }
                crashedBitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(resources, R.drawable.killhoneybeeys), 200, 300, true)            }
            else -> {
                bugBitmap[0] = bugsBitmaps["beetle"]!![0]?.let { Bitmap.createScaledBitmap(it, 200, 300, true) }
                bugBitmap[1] = bugsBitmaps["beetle"]!![1]?.let { Bitmap.createScaledBitmap(it, 200, 300, true) }
                crashedBitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(resources, R.drawable.killbeetle), 200, 300, true)            }
        }
        val bug = Bug(bugBitmap, crashedBitmap, displayWidth, displayHeight)
        this.bugs.add(bug)
        bug.start()
    }

    init {
        val displayMetrics = resources.displayMetrics
        displayWidth = displayMetrics.widthPixels
        displayHeight = displayMetrics.heightPixels
        val bug1Bitmap: Array<Bitmap?> = arrayOfNulls(2)

        bug1Bitmap[0] = BitmapFactory.decodeResource(resources, R.drawable.spiders)
        bug1Bitmap[1] = BitmapFactory.decodeResource(resources, R.drawable.spiders2)
        bugsBitmaps["spider"] = bug1Bitmap

        val bug2Bitmap: Array<Bitmap?> = arrayOfNulls(2)
        bug2Bitmap[0] = BitmapFactory.decodeResource(resources,R.drawable.honeybee)
        bug2Bitmap[1] = BitmapFactory.decodeResource(resources, R.drawable.honeybee2)
        bugsBitmaps["honeybee"] = bug2Bitmap

        val bug3Bitmap: Array<Bitmap?> = arrayOfNulls(2)
        bug3Bitmap[0] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(resources,R.drawable.beetle), 200, 300, true)
        bug3Bitmap[1] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(resources, R.drawable.beetle2), 200, 300, true)
        bugsBitmaps["beetle"] = bug3Bitmap

        synchronized(bugs){
            for (i in 0..5) {
                spawnRandBug()
            }
        }

        this.timer = Timer()
        synchronized(bugs){
            timer?.scheduleAtFixedRate(timerTask {

                if (bugs.size < 13) spawnRandBug()
            }, 0, 500)
        }
    }

    private fun gameOver() {
        for (bug in bugs) {
            bug.kill()
        }
        bugs.clear()
        val intent = Intent(context, GameOver::class.java)
        val message = if (attempts <= 0) "Жуки победили(((" else "Ты грохнул всех жуков!\nКрасавчик!!!"
        intent.putExtra("message", message)
        context.startActivity(intent)
        (context as Activity).finish()
    }
}