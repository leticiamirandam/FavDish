package com.leticiamirandam.favdish.notification

import android.content.Context
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters

class OneTimeRequestWorker(context: Context, params: WorkerParameters) : Worker(context, params) {

    override fun doWork(): Result {
        val inputValue = inputData.getInt("timer", 0)
        return Result.success(createOutputData())
    }

    private fun createOutputData() : Data {
        return Data.Builder().putString("outputKey", "Output Value").build()
    }
}