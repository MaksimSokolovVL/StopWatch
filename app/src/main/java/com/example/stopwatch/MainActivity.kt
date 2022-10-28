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
private var job: Job? = null

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
        binding.buttonStart.isEnabled = false

        binding.sliderBar.addOnChangeListener { _, value, _ ->
            val counterTimeBar = value.toInt()
            binding.textClock.text = counterTimeBar.toString()
            progressBar.max = counterTimeBar
            maxProcessBar = counterTimeBar
            binding.buttonStart.isEnabled = value != 0f
        }

        binding.buttonStart.setOnClickListener {
            progressBar.progress = maxProcessBar
            binding.buttonStart.visibility = View.GONE
            binding.buttonPause.visibility = View.VISIBLE
            binding.buttonStop.visibility = View.VISIBLE
            binding.sliderBar.isEnabled = false
            job = timerScope.launch { timeCounter(progressBar, binding) }
        }

        var pause = 0
        binding.buttonPause.setOnClickListener {
            when (pause) {
                0 -> {
                    pause = 1
                    binding.buttonPause.text = getString(R.string.pause_resume)
                    job?.cancel()
                }
                1 -> {
                    pause = 0
                    binding.buttonPause.text = getString(R.string.button_pause)
                    job = timerScope.launch { timeCounter(progressBar, binding) }
                }
            }
        }

        binding.buttonStop.setOnClickListener {
            job?.cancel()
            progressBar.progress = MIN.toInt()
            binding.sliderBar.isEnabled = true
            binding.buttonStart.visibility = View.VISIBLE
            binding.buttonPause.visibility = View.GONE
            binding.buttonStop.visibility = View.GONE
            binding.sliderBar.value = MIN
            pause=0
            binding.buttonPause.text = getString(R.string.button_pause)
        }
    }

    private suspend fun timeCounter(
        progressBar: ContentLoadingProgressBar,
        binding: ActivityMainBinding
    ) {
        repeat(maxProcessBar) {
            delay(1000)
            maxProcessBar--
            progressBar.progress = maxProcessBar
            binding.textClock.text = maxProcessBar.toString()
        }
    }
}