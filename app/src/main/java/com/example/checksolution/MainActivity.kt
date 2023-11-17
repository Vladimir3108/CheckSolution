package com.example.checksolution

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.checksolution.databinding.ActivityMainBinding
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        serviceIntent = Intent(applicationContext, ServiceTimer::class.java)
        registerReceiver(updateTime, IntentFilter(ServiceTimer.TIMER_UPDATED))
    }

    private var timerStarted = false
    private lateinit var serviceIntent: Intent
    private var all = 0.0
    private var avg = 0.0
    private var max = 0.0
    private var min = 99999.0
    private var time = 0.0

    var win = 0
    var lose = 0
    var examples = 0
    var percentage = 0.0
    var choiceComputer = true

    fun BtnStart(view: View)
    {
        var result = 0
        val operands = arrayOf("+","-","*","/")
        val operand = operands.random()
        var oneOperant = (10..99).random()
        var twoOperator = (10..99).random()
        if (operand == "/")
        {
            while (oneOperant%twoOperator!=0)
            {
                oneOperant = (10..99).random()
                twoOperator = (10..99).random()
            }
        }
        var chance = (1..2).random()
        if (chance == 1)
        {
            choiceComputer = true
            when (operand){
                "+" -> result = oneOperant+twoOperator
                "-" -> result = oneOperant-twoOperator
                "*" -> result = oneOperant*twoOperator
                "/" -> result = oneOperant/twoOperator
            }
        }
        else
        {
            choiceComputer = false
            result = (-100..100).random()
        }
        binding.TxtAllExample.text = oneOperant.toString() + " " + operand + " " + twoOperator.toString() + " = " + result.toString()
        binding.BtnWrong.isEnabled = true
        binding.BtnRight.isEnabled = true
        binding.BtnStart.isEnabled = false
        startTimer()
    }

    fun BtnRight(view: View)
    {
        if (choiceComputer)
        {
            win++
            examples++
        }
        else
        {
            lose++
            examples++
        }
        percentage = (win/examples*100).toDouble()
        binding.TxtRight.text = win.toString()
        binding.TxtWrong.text = lose.toString()
        binding.TxtExamples.text = examples.toString()
        binding.TxtPercentage.text = ("%.2f".format(percentage)).toString()  + "%"
        binding.BtnWrong.isEnabled = false
        binding.BtnRight.isEnabled = false
        binding.BtnStart.isEnabled = true
        resetTimer()
    }

    fun BtnWrong(view: View)
    {
        if (!choiceComputer)
        {
            win++
            examples++
        }
        else
        {
            lose++
            examples++
        }
        percentage = (win/examples*100).toDouble()
        binding.TxtRight.text = win.toString()
        binding.TxtWrong.text = lose.toString()
        binding.TxtExamples.text = examples.toString()
        binding.TxtPercentage.text = ("%.2f".format(percentage)).toString()  + "%"
        binding.BtnWrong.isEnabled = false
        binding.BtnRight.isEnabled = false
        binding.BtnStart.isEnabled = true
        resetTimer()
    }

    private fun resetTimer()
    {
        stopTimer()
        all += time
        if (time < min) {
            min = time
            binding.TxtMinTime.text = time.toString()
        }
        if (time > max) {
            max = time
            binding.TxtMaxTime.text = time.toString()
        }
        avg = all / examples
        binding.TxtAvgTime.text = avg.toString()
        time = 0.0
        binding.TxtTimer.text = getTimeStringFromDouble(time)
    }

    private fun startTimer()
    {
        serviceIntent.putExtra(ServiceTimer.TIME_EXTRA, time)
        startService(serviceIntent)
        timerStarted = true
    }

    private fun stopTimer()
    {
        stopService(serviceIntent)
        timerStarted = false
    }

    private val updateTime: BroadcastReceiver = object : BroadcastReceiver()
    {
        override fun onReceive(context: Context, intent: Intent)
        {
            time = intent.getDoubleExtra(ServiceTimer.TIME_EXTRA, 0.0)
            binding.TxtTimer.text = getTimeStringFromDouble(time)
        }
    }

    private fun getTimeStringFromDouble(time: Double): String
    {
        val resultInt = time.roundToInt()
        val seconds = resultInt

        return seconds.toString()
    }
}