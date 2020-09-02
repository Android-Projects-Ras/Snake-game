package com.example.snakegame

import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.snakegame.SnakeCore.isPlay
import com.example.snakegame.SnakeCore.nextMove
import com.example.snakegame.SnakeCore.startTheGame
import kotlinx.android.synthetic.main.activity_main.*

const val HEAD_SIZE = 100

class MainActivity : AppCompatActivity() {

    private val apple by lazy {
        ImageView(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val head = View(this)
        head.layoutParams = FrameLayout.LayoutParams(HEAD_SIZE,HEAD_SIZE)
        head.background = ContextCompat.getDrawable(this, R.drawable.dot)
        startTheGame()
        nextMove= {move(Directions.DOWN, head)}

        ic_arrow_up.setOnClickListener { nextMove= {move(Directions.UP, head)} }
        ic_arrow_down.setOnClickListener { nextMove= {move(Directions.DOWN, head)} }
        ic_arrow_left.setOnClickListener { nextMove = {move(Directions.LEFT, head)} }
        ic_arrow_right.setOnClickListener { nextMove = {move(Directions.RIGHT, head)} }
        iv_pause.setOnClickListener {
            if (isPlay) {
                iv_pause.setImageResource(R.drawable.ic_play_arrow)
            } else {
                iv_pause.setImageResource(R.drawable.ic_pause)

            }
            isPlay = !isPlay
        }

    }

    private fun checkIfSnakeEatApple(head: View) {
        if (head.left == apple.left && head.top == apple.top) {
            container.removeView(apple)
            generateApple()
        }

    }

    private fun generateApple() {
        Thread{
        apple.layoutParams = FrameLayout.LayoutParams(HEAD_SIZE, HEAD_SIZE)
            apple.setImageResource(R.drawable.apple)
        (apple.layoutParams as FrameLayout.LayoutParams).topMargin = (1..10).random() * HEAD_SIZE
        (apple.layoutParams as FrameLayout.LayoutParams).leftMargin = (1..10).random() * HEAD_SIZE
            runOnUiThread {
                container.addView(apple)
            }
        }.start()

    }

    private fun move(directions: Directions, head: View) {

        when(directions) {
            Directions.UP -> (head.layoutParams as FrameLayout.LayoutParams).topMargin -=HEAD_SIZE
            Directions.DOWN -> (head.layoutParams as FrameLayout.LayoutParams).topMargin +=HEAD_SIZE
            Directions.LEFT -> (head.layoutParams as FrameLayout.LayoutParams).leftMargin -=HEAD_SIZE
            Directions.RIGHT -> (head.layoutParams as FrameLayout.LayoutParams).leftMargin +=HEAD_SIZE
        }

        runOnUiThread{
            checkIfSnakeEatApple(head)
            container.removeView(head)
            container.addView(head)
        }

    }
}



enum class Directions{
    UP,
    RIGHT,
    DOWN,
    LEFT
}
