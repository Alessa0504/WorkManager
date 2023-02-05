package com.example.workmanager.multiply

import android.content.Context
import android.util.Log
import androidx.annotation.NonNull
import androidx.work.Worker
import androidx.work.WorkerParameters

/**
 * @Description: 多任务执行 -任务3
 * @author zouji
 * @date 2023/1/3
 */
class Worker3(@NonNull context: Context, @NonNull workerParameters: WorkerParameters) :
    Worker(context, workerParameters) {

    companion object {
        val TAG = Worker3::class.java.simpleName
    }

    override fun doWork(): Result {
        Log.i(TAG, "$TAG doWork run...")
        return Result.success()   // 本次任务执行成功
    }
}