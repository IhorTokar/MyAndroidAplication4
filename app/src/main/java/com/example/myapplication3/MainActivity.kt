package com.example.myapplication3

import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.ProgressBar
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import android.widget.ToggleButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    private var textView: TextView? = null
    private var scoreTextView: TextView? = null
    private var buttonYes: Button? = null
    private var buttonNo: Button? = null
    private var livesTextView: TextView? = null
    private var spinnerDfTheme: Spinner? = null
    private var timer_bar: ProgressBar? = null
    private var timer_timeTogle: ToggleButton? = null
    private var gameTime: Long = 3 * 60 * 1000
    private var buttonStart: Button? = null
    private var buttonEndGame: Button? = null
    private var infinityLivesCheckBox: CheckBox? = null
    private lateinit var radioGroup: RadioGroup
    private lateinit var easyRadio: RadioButton
    private lateinit var mediumRadio: RadioButton
    private lateinit var hardRadio: RadioButton
    private var timerTextView: TextView? = null

    private var isDarkTheme = false
    private var score = 0
    private var lives = 3
    private val colors = arrayOf("Червоний", "Зелений", "Синій", "Жовтий", "Оранжевий", "Фіолетовий")
    private val colorValues = intArrayOf(Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW, Color.rgb(252, 165, 3), Color.rgb(115, 7, 217))
    private var timer: CountDownTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setAppTheme()
        setContentView(R.layout.activity_main)

        textView = findViewById(R.id.textView)
        scoreTextView = findViewById(R.id.scoreTextView)
        buttonYes = findViewById(R.id.buttonYes)
        buttonNo = findViewById(R.id.buttonNo)
        livesTextView = findViewById(R.id.livesTextView)
        spinnerDfTheme = findViewById(R.id.theme_spinner)
        timer_bar = findViewById(R.id.timer_bar)
        buttonStart = findViewById(R.id.buttonStart)
        radioGroup = findViewById(R.id.radioGroup)
        easyRadio = findViewById(R.id.Easy_mode)
        mediumRadio = findViewById(R.id.Medium_mode)
        hardRadio = findViewById(R.id.Hard_mode)
        timer_timeTogle = findViewById(R.id.toggleButton)
        infinityLivesCheckBox = findViewById(R.id.theme_checkBox)
        buttonEndGame = findViewById(R.id.buttonEndGame)
        timerTextView = findViewById(R.id.timer_count)

        setupThemeSpinner()
        setupDifficultyRadioGroup()
        setTimerSpeedToggle()

        infinityLivesCheckBox?.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                lives = Int.MAX_VALUE
                livesTextView?.text = "Без життів"
            } else {
                setupDifficultyRadioGroup()
                livesTextView?.text = "Життя: $lives"
            }
        }
        buttonStart?.setOnClickListener {
            startGame()
            buttonStart?.visibility = View.GONE
        }

        timer_timeTogle?.setTextOn("Швидкий режим")
        timer_timeTogle?.setTextOff("Звичайний режим")
        scoreTextView?.text = "Рекорд: $score"
        livesTextView?.text = "Життя: $lives"
        buttonYes?.isEnabled = false
        buttonNo?.isEnabled = false

        buttonYes?.setOnClickListener { checkAnswer(true) }
        buttonNo?.setOnClickListener { checkAnswer(false) }
        buttonEndGame?.setOnClickListener {
            endGame()
        }
    }

    private fun setupDifficultyRadioGroup() {
        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.Easy_mode -> {
                    lives = 5
                }
                R.id.Medium_mode -> {
                    lives = 3
                }
                R.id.Hard_mode -> {
                    lives = 1
                }
            }
            livesTextView?.text = "Життя: $lives"
        }
    }
    private fun setTimerSpeedToggle(){
        timer_timeTogle?.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                gameTime = 2 * 60 * 1000
            } else {
                gameTime = 3 * 60 * 1000
            }
        }
    }

    private var currentTheme = AppCompatDelegate.MODE_NIGHT_NO

    private fun setAppTheme() {
        val newTheme = if (isDarkTheme) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO

        if (newTheme != currentTheme) {
            AppCompatDelegate.setDefaultNightMode(newTheme)
            currentTheme = newTheme
        }
    }

    private fun endGame() {
        buttonEndGame?.visibility = View.GONE
        showGameOverMessage()
        resetGame()
    }

    private fun hideGameSetupElements() {
        timer_timeTogle?.visibility = View.GONE
        infinityLivesCheckBox?.visibility = View.GONE
        radioGroup.visibility = View.GONE
        spinnerDfTheme?.visibility = View.GONE
    }

    private fun showGameSetupElements() {
        timer_timeTogle?.visibility = View.VISIBLE
        infinityLivesCheckBox?.visibility = View.VISIBLE
        radioGroup.visibility = View.VISIBLE
        spinnerDfTheme?.visibility = View.VISIBLE
    }

    private fun setupThemeSpinner() {
        val themeOptions = arrayOf("Світла", "Темна")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, themeOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerDfTheme?.adapter = adapter

        spinnerDfTheme?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                isDarkTheme = position == 1
                setAppTheme()
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }
    }
    private fun startGame() {
        score = 0
        scoreTextView?.text = "Рекорд: $score"
        if(lives != Int.MAX_VALUE){
            livesTextView?.text = "Життя: $lives"}
        buttonYes?.isEnabled = true
        buttonNo?.isEnabled = true

        hideGameSetupElements()

        buttonEndGame?.visibility = View.VISIBLE

        restartTimer()
        updateColor()
    }

    private fun restartTimer() {
        timer?.cancel()
        timer = object : CountDownTimer(gameTime, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val progress = ((millisUntilFinished.toFloat() / gameTime) * 100).toInt()
                timer_bar?.progress = progress
                val secondsRemaining = millisUntilFinished / 1000
                timerTextView?.text = "Час: $secondsRemaining с"
            }

            override fun onFinish() {
                showGameOverMessage()
                resetGame()
            }
        }.start()
    }

    private fun resetGame() {
        score = 0
        setupDifficultyRadioGroup()
        scoreTextView?.text = "Рекорд: $score"
        livesTextView?.text = "Життя: $lives"
        buttonStart?.visibility = View.VISIBLE

        showGameSetupElements()
        buttonEndGame?.visibility = View.GONE

        buttonYes?.isEnabled = false
        buttonNo?.isEnabled = false
        updateColor()
        timer?.cancel()
    }


    private fun updateColor() {
        val randomIndex = Random.nextInt(colors.size)
        val colorName = colors[randomIndex]
        val colorValue = colorValues[Random.nextInt(colorValues.size)]

        textView?.text = colorName
        textView?.setTextColor(colorValue)
    }


    private fun checkAnswer(isYesSelected: Boolean) {
        val actualColorName = textView?.text.toString()
        val actualColorValue = textView?.currentTextColor ?: 0
        val expectedColorIndex = getColorIndex(actualColorName)

        val isMatching = if (expectedColorIndex >= 0) {
            colorValues[expectedColorIndex] == actualColorValue
        } else {
            false
        }
        if (isYesSelected == isMatching) {
            score++
            scoreTextView?.text = "Рекорд: $score"
        } else {
            if (lives != Int.MAX_VALUE) {
                lives--
                livesTextView?.text = "Життя: $lives"
            }
        }
        updateColor()

        if (lives == 0 && lives != Int.MAX_VALUE) {
            showGameOverMessage()
            resetGame()
        }
    }

    private fun showGameOverMessage() {
        Toast.makeText(this, "Кінець гри! Рекорд: $score", Toast.LENGTH_LONG).show()
    }

    private fun getColorIndex(colorName: String): Int {
        return colors.indexOf(colorName).takeIf { it >= 0 } ?: -1
    }
}