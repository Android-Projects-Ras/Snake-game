package com.example.snakegame

import android.widget.LinearLayout
import kotlinx.android.synthetic.main.activity_main.*

object SnakeCore {

    var nextMove: () -> Unit = {}
    var isPlay = true
    private val thread: Thread

    init {
        thread = Thread  {
            while (true) {
                Thread.sleep(500)
                if (isPlay) {
                    nextMove()
                }
            }
        }
        thread.start()
    }

    fun startTheGame() {
        isPlay = true
    }
}