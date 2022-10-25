package com.example.stopwatch

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.widget.ContentLoadingProgressBar
import com.example.stopwatch.databinding.ActivityMainBinding
import kotlinx.coroutines.*


private const val MIN = 0f
private const val MAX = 60f
private val timerScope = CoroutineScope(Job() + Dispatchers.Main)

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    private var maxProcessBar: Int = MAX.toInt()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val progressBar = binding.progressBar
        binding.sliderBar.valueTo = MAX
        binding.sliderBar.valueFrom = MIN

        binding.sliderBar.addOnChangeListener { _, value, _ ->
            val g = value.toInt()
            binding.textClock.text = g.toString()
            progressBar.max = g
            maxProcessBar = g
        }

        binding.buttonStart.setOnClickListener {
            progressBar.progress = maxProcessBar
            binding.buttonStart.visibility = View.GONE
            binding.buttonPause.visibility = View.VISIBLE
            binding.buttonStop.visibility = View.VISIBLE
            binding.sliderBar.isEnabled = false
            timeCounter(progressBar, binding)

        }

        var pause = 0
        binding.buttonPause.setOnClickListener {
            when (pause) {
                0 -> {
                    pause = 1
                    binding.buttonPause.text = getString(R.string.pause_resume)
                    timerScope.cancel()
                }
                1 -> {
                    pause = 0
                    binding.buttonPause.text = getString(R.string.button_pause)
                    runBlocking {
                        launch(Dispatchers.Main) {
                            timeCounter(progressBar, binding)
                        }
                    }
                }
            }
        }
    }

    private fun timeCounter(progressBar: ContentLoadingProgressBar, binding: ActivityMainBinding) {
        timerScope.launch {
            repeat(maxProcessBar) {
                delay(1000)
                maxProcessBar--
                progressBar.progress = maxProcessBar
                binding.textClock.text = maxProcessBar.toString()
            }
        }
    }
}