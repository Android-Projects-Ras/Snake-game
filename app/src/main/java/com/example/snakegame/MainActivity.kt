package com.example.snakegame

import android.app.AlertDialog
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.snakegame.SnakeCore.gameSpeed
import com.example.snakegame.SnakeCore.isPlay
import com.example.snakegame.SnakeCore.startTheGame
import kotlinx.android.synthetic.main.activity_main.*

const val HEAD_SIZE = 100
const val CELLS_ON_FIELD = 11

class MainActivity : AppCompatActivity() {

    private val allTale = mutableListOf<PartOfTale>()
    private var currentDirection: Directions = Directions.BOTTOM
    private val apple by lazy {
        ImageView(this).apply {
            this.layoutParams = FrameLayout.LayoutParams(HEAD_SIZE, HEAD_SIZE)
            this.setImageResource(R.drawable.apple)
        }
    }
    private val head by lazy {
        ImageView(this)
            .apply {
                this.layoutParams = FrameLayout.LayoutParams(HEAD_SIZE, HEAD_SIZE)
                this.setImageResource(R.drawable.dot)
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        container.layoutParams = LinearLayout.LayoutParams(HEAD_SIZE * CELLS_ON_FIELD, HEAD_SIZE * CELLS_ON_FIELD)

        startTheGame()
        generateNewHuman()
        SnakeCore.nextMove = { move(Directions.BOTTOM) }

        iv_arrow_up.setOnClickListener {
            SnakeCore.nextMove = { checkIfCurrentDirectionIsNotOpposite(Directions.UP, Directions.BOTTOM) }
        }
        iv_arrow_down.setOnClickListener {
            SnakeCore.nextMove = { checkIfCurrentDirectionIsNotOpposite(Directions.BOTTOM, Directions.UP) }
        }
        iv_arrow_left.setOnClickListener {
            SnakeCore.nextMove = { checkIfCurrentDirectionIsNotOpposite(Directions.LEFT, Directions.RIGHT) }
        }
        iv_arrow_right.setOnClickListener {
            SnakeCore.nextMove = { checkIfCurrentDirectionIsNotOpposite(Directions.RIGHT, Directions.LEFT) }
        }
        iv_pause.setOnClickListener {
            if (isPlay) {
                iv_pause.setImageResource(R.drawable.ic_play_arrow)
            } else {
                iv_pause.setImageResource(R.drawable.ic_pause)
            }
            SnakeCore.isPlay = !isPlay
        }
    }

    private fun checkIfCurrentDirectionIsNotOpposite(rightDirection: Directions, oppositeDirection: Directions) {
        if (currentDirection == oppositeDirection) {
            move(currentDirection)
        } else {
            move(rightDirection)
        }
    }

    private fun generateNewHuman() {
        val viewCoordinate = generateHumanCoordinates()
        (apple.layoutParams as FrameLayout.LayoutParams).topMargin = viewCoordinate.top
        (apple.layoutParams as FrameLayout.LayoutParams).leftMargin = viewCoordinate.left
        container.removeView(apple)
        container.addView(apple)
    }

    private fun generateHumanCoordinates(): ViewCoordinate {
        val viewCoordinate = ViewCoordinate(
            (0 until CELLS_ON_FIELD).random() * HEAD_SIZE,
            (0 until CELLS_ON_FIELD).random() * HEAD_SIZE
        )
        for (partTale in allTale) {
            if (partTale.viewCoordinate == viewCoordinate) {
                return generateHumanCoordinates()
            }
        }
        if (head.top == viewCoordinate.top && head.left == viewCoordinate.left) {
            return generateHumanCoordinates()
        }
        return viewCoordinate
    }

    private fun checkIfSnakeEatsPerson() {
        if (head.left == apple.left && head.top == apple.top) {
            generateNewHuman()
            addPartOfTale(head.top, head.left)
            increaseDifficult()
        }
    }

    private fun increaseDifficult() {
        if (gameSpeed <= MINIMUM_GAME_SPEED) {
            return
        }
        if (allTale.size % 5 == 0) {
            gameSpeed -= 100
        }
    }

    private fun addPartOfTale(top: Int, left: Int) {
        val talePart = drawPartOfTale(top, left)
        allTale.add(PartOfTale(ViewCoordinate(top, left), talePart))
    }

    private fun drawPartOfTale(top: Int, left: Int): ImageView {
        val taleImage = ImageView(this)
        taleImage.setImageResource(R.drawable.apple)
        taleImage.layoutParams = FrameLayout.LayoutParams(HEAD_SIZE, HEAD_SIZE)
        (taleImage.layoutParams as FrameLayout.LayoutParams).topMargin = top
        (taleImage.layoutParams as FrameLayout.LayoutParams).leftMargin = left

        container.addView(taleImage)
        return taleImage
    }

    fun move(direction: Directions) {
        when (direction) {
            Directions.UP -> {
                moveHeadAndRotate(Directions.UP, 90f, -HEAD_SIZE)
            }
            Directions.BOTTOM -> {
                moveHeadAndRotate(Directions.BOTTOM, 270f, HEAD_SIZE)
            }
            Directions.LEFT -> {
                moveHeadAndRotate(Directions.LEFT, 0f, -HEAD_SIZE)
            }
            Directions.RIGHT -> {
                moveHeadAndRotate(Directions.RIGHT, 180f, HEAD_SIZE)
            }
        }
        runOnUiThread {
            if (checkIfSnakeSmash()) {
                isPlay = false
                showScore()
                return@runOnUiThread
            }
            makeTaleMove()
            checkIfSnakeEatsPerson()
            container.removeView(head)
            container.addView(head)
        }
    }

    private fun moveHeadAndRotate(direction: Directions, angle: Float, coordinates: Int) {
        head.rotation = angle
        when (direction) {
            Directions.UP, Directions.BOTTOM -> {
                (head.layoutParams as FrameLayout.LayoutParams).topMargin += coordinates
            }
            Directions.LEFT, Directions.RIGHT -> {
                (head.layoutParams as FrameLayout.LayoutParams).leftMargin += coordinates
            }
        }
        currentDirection = direction
    }

    private fun showScore() {
        AlertDialog.Builder(this)
            .setTitle("Your score: ${allTale.size} items")
            .setPositiveButton("ok") { _, _ ->
                this.recreate()
            }
            .setCancelable(false)
            .create()
            .show()
    }

    private fun checkIfSnakeSmash(): Boolean {
        for (talePart in allTale) {
            if (talePart.viewCoordinate.left == head.left && talePart.viewCoordinate.top == head.top) {
                return true
            }
        }
        if (head.top < 0
            || head.left < 0
            || head.top >= HEAD_SIZE * CELLS_ON_FIELD
            || head.left >= HEAD_SIZE * CELLS_ON_FIELD
        ) {
            return true
        }
        return false
    }

    private fun makeTaleMove() {
        var tempTalePart: PartOfTale? = null
        for (index in 0 until allTale.size) {
            val talePart = allTale[index]
            container.removeView(talePart.imageView)
            if (index == 0) {
                tempTalePart = talePart
                allTale[index] = PartOfTale(ViewCoordinate(head.top, head.left), drawPartOfTale(head.top, head.left))
            } else {
                val anotherTempPartOfTale = allTale[index]
                tempTalePart?.let {
                    allTale[index] =
                        PartOfTale(it.viewCoordinate, drawPartOfTale(it.viewCoordinate.top, it.viewCoordinate.left))
                }
                tempTalePart = anotherTempPartOfTale
            }
        }
    }
}

enum class Directions {
    UP,
    RIGHT,
    BOTTOM,
    LEFT
}