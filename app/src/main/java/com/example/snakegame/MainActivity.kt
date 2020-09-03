package com.example.snakegame

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.snakegame.SnakeCore.isPlay
import com.example.snakegame.SnakeCore.nextMove
import com.example.snakegame.SnakeCore.startTheGame
import kotlinx.android.synthetic.main.activity_main.*

const val HEAD_SIZE = 100
const val CELL_ON_FIELD = 11

class MainActivity : AppCompatActivity() {

    private val allTale = mutableListOf<PartOfTale>()

    private val apple by lazy {
        ImageView(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val head = View(this)
        head.layoutParams = FrameLayout.LayoutParams(HEAD_SIZE, HEAD_SIZE)
        head.background = ContextCompat.getDrawable(this, R.drawable.dot)
        container.layoutParams =
            LinearLayout.LayoutParams(HEAD_SIZE * CELL_ON_FIELD, HEAD_SIZE * CELL_ON_FIELD)

        startTheGame()
        generateApple()
        nextMove = { move(Directions.DOWN, head) }

        ic_arrow_up.setOnClickListener { nextMove = { move(Directions.UP, head) } }
        ic_arrow_down.setOnClickListener { nextMove = { move(Directions.DOWN, head) } }
        ic_arrow_left.setOnClickListener { nextMove = { move(Directions.LEFT, head) } }
        ic_arrow_right.setOnClickListener { nextMove = { move(Directions.RIGHT, head) } }
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
            generateApple()
            addPartOfTale(head.top, head.left)
        }

    }

    private fun addPartOfTale(top: Int, left: Int) {
        val talePart = drawPartOfTale(top, left)
        allTale.add(PartOfTale(top, left, talePart))

    }

    private fun drawPartOfTale(top: Int, left: Int): ImageView {
        val taleImage = ImageView(this)


        taleImage.setImageResource(R.drawable.apple)
        taleImage.layoutParams = FrameLayout.LayoutParams(HEAD_SIZE, HEAD_SIZE)// size of tale part
        (taleImage.layoutParams as FrameLayout.LayoutParams).topMargin = top
        (taleImage.layoutParams as FrameLayout.LayoutParams).leftMargin = left
        container.addView(taleImage)
        return taleImage

    }

    private fun generateApple() {
        apple.layoutParams = FrameLayout.LayoutParams(HEAD_SIZE, HEAD_SIZE)
        apple.setImageResource(R.drawable.apple)
        (apple.layoutParams as FrameLayout.LayoutParams).topMargin =
            (0 until CELL_ON_FIELD).random() * HEAD_SIZE
        (apple.layoutParams as FrameLayout.LayoutParams).leftMargin =
            (0 until CELL_ON_FIELD).random() * HEAD_SIZE
        container.removeView(apple)
        container.addView(apple)
    }

    private fun move(directions: Directions, head: View) {

        when (directions) {
            Directions.UP -> (head.layoutParams as FrameLayout.LayoutParams).topMargin -= HEAD_SIZE
            Directions.DOWN -> (head.layoutParams as FrameLayout.LayoutParams).topMargin += HEAD_SIZE
            Directions.LEFT -> (head.layoutParams as FrameLayout.LayoutParams).leftMargin -= HEAD_SIZE
            Directions.RIGHT -> (head.layoutParams as FrameLayout.LayoutParams).leftMargin += HEAD_SIZE
        }

        runOnUiThread {
            if (checkIfSnakeSmash(head)) {
                isPlay = false
                showScore()
                return@runOnUiThread
            }
            makeTaleMove(head.top, head.left)
            checkIfSnakeEatApple(head)
            container.removeView(head)
            container.addView(head)
        }

    }

    private fun showScore() {
        AlertDialog.Builder(this)
            .setTitle("Your score: ${allTale.size} items")
            .setPositiveButton("Ok") { _, _ ->
                this.recreate()
            }
            .setCancelable(false)
            .create()
            .show()

    }

    private fun checkIfSnakeSmash(head: View): Boolean {
        for (talePart in allTale) {
            if (talePart.top == head.top && talePart.left == head.left) {
                return true
            }
        }
        if (head.top < 0
            || head.left < 0
            || head.top >= HEAD_SIZE * CELL_ON_FIELD
            || head.left >= HEAD_SIZE * CELL_ON_FIELD) {
            return true
        }
        return false
    }

    private fun makeTaleMove(headTop: Int, headLeft: Int) {
        var tempTalePart: PartOfTale? = null
        for (index in 0 until allTale.size) {
            val talePart = allTale[index]
            container.removeView(talePart.imageView)
            if (index == 0) {
                tempTalePart = talePart
                allTale[index] = PartOfTale(headTop, headLeft, drawPartOfTale(headTop, headLeft))
            } else {
                val anotherTempPartOfTale = allTale[index]
                tempTalePart?.let {
                    allTale[index] = PartOfTale(it.top, it.left, drawPartOfTale(it.top, it.left))
                }
                tempTalePart = anotherTempPartOfTale
            }

        }
    }
}


enum class Directions {
    UP,
    RIGHT,
    DOWN,
    LEFT
}
