package com.example.snakegame

import android.widget.LinearLayout
import kotlinx.android.synthetic.main.activity_main.*

object SnakeCore {

    var nextMove: () -> Unit = {}
    var isPlay = true

    fun startTheGame() {
        Thread {
            while (true) {
                Thread.sleep(500)
                if (isPlay) {
                    nextMove()
                }
            }
        }.start()
    }
}