package com.example.snakegame

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.example.snakegame.SnakeCore.startTheGame
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val head = View(this)
        head.layoutParams = LinearLayout.LayoutParams(100,100)
        head.background = ContextCompat.getDrawable(this, R.drawable.dot)
        //startTheGame()

        ic_arrow_up.setOnClickListener { move(Directions.UP, head) }
        ic_arrow_down.setOnClickListener { move(Directions.DOWN, head) }
        ic_arrow_left.setOnClickListener { move(Directions.LEFT, head) }
        ic_arrow_right.setOnClickListener { move(Directions.RIGHT, head) }


    }

    fun move(directions: Directions, head: View) {

        when(directions) {
            Directions.UP -> (head.layoutParams as LinearLayout.LayoutParams).topMargin -=100
            Directions.DOWN -> (head.layoutParams as LinearLayout.LayoutParams).topMargin +=100
            Directions.LEFT -> (head.layoutParams as LinearLayout.LayoutParams).leftMargin -=100
            Directions.RIGHT -> (head.layoutParams as LinearLayout.LayoutParams).leftMargin +=100
        }

        runOnUiThread{
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
