package com.mcgars.basekitk.tools

import android.os.CountDownTimer

/**
 * Created by Владимир on 30.07.2015.
 * @param millisInFuture    The number of millis in the future from the call
 * *                          to [.start] until the countdown is done and [.onFinish]
 * *                          is called.
 * *
 * @param countDownInterval The interval along the way to receive
 * *                          [.onTick] callbacks.
 */
class Timer(millisInFuture: Long, countDownInterval: Long = millisInFuture, val action: (()->Unit)? = null) :
        CountDownTimer(countDownInterval, countDownInterval) {
    override fun onTick(millisUntilFinished: Long) { }
    override fun onFinish() { action?.invoke()}
}

