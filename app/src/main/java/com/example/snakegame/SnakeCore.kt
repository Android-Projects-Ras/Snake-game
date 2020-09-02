package com.example.snakegame

import android.widget.LinearLayout
import kotlinx.android.synthetic.main.activity_main.*

object SnakeCore {

    var nextMove:() -> Unit = {}

    fun startTheGame() {
        Thread(Runnable {
            while (true) {
                Thread.sleep(500)
                nextMove()


            }
        }).start()
    }
}