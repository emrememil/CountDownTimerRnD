package com.example.countdowntimerrnd

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import cn.iwgang.countdownview.CountdownView
import io.paperdb.Paper
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var IS_START_KEY = "IS_START"
    private var LAST_TIME_SAVE_KEY = "LAST_TIME_SAVE"
    private var TIME_REMAIN_KEY = "TIME_REMAIN"
    private var TIME_LIMIT: Long = 15 * 1000 // 15 Sec

    private var isStart = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initTimer()

        setupView()


    }

    private fun setupView() {
        btnStart.setOnClickListener {
            if (!isStart) {
                countdownView.start(TIME_LIMIT)
                Paper.book().write(IS_START_KEY, true)
                btnStart.isEnabled = false
            }

        }

        countdownView.setOnCountdownEndListener { cv ->
            Toast.makeText(this, "Finish!!", Toast.LENGTH_LONG).show()
            reset()
        }

        countdownView.setOnCountdownIntervalListener(1000) { cv, remainTime ->
            Log.d("TIMER",remainTime.toString())
        }
    }

    private fun reset() {
        btnStart.isEnabled = true
        Paper.book().delete(IS_START_KEY)
        Paper.book().delete(LAST_TIME_SAVE_KEY)
        Paper.book().delete(TIME_REMAIN_KEY)

        isStart = false
    }

    fun initTimer() {
        Paper.init(this)
        isStart = Paper.book().read(IS_START_KEY, false)

        //Check time

        if (isStart){
            btnStart.isEnabled = false
            checkTime()
        }else{
            btnStart.isEnabled = true
        }
    }

    private fun checkTime() {
        val currentTime: Long = System.currentTimeMillis()
        val lastTimeSaved: Long = Paper.book().read<Long>(LAST_TIME_SAVE_KEY)
        val timeRemain: Long = Paper.book().read<Long>(TIME_REMAIN_KEY)
        val result: Long = timeRemain + (lastTimeSaved - currentTime)
        if (result > 0){
            countdownView.start(result)
        }else {
            countdownView.stop()
            reset()
        }
    }

    override fun onStop() {
        Paper.book().write(TIME_REMAIN_KEY, countdownView.remainTime)
        Paper.book().write(LAST_TIME_SAVE_KEY, System.currentTimeMillis())
        super.onStop()
    }
}