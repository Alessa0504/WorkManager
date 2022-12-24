package com.example.workmanager

import android.content.Context
import android.util.Log
import androidx.annotation.NonNull
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters

/**
 * @Description: 任务执行，接收&回传数据
 * @author zouji
 * @date 2022/12/24
 */
class UploadWorker2(@NonNull context: Context, @NonNull workerParameters: WorkerParameters) :
    Worker(context, workerParameters) {

    companion object {
        val TAG = UploadWorker2::class.java.simpleName
    }
    private val context: Context
    private val workerParameters: WorkerParameters

    init {
        this.context = context
        this.workerParameters = workerParameters
    }

    @NonNull
    override fun doWork(): Result {
        Log.i(TAG, "doWork run...")
        //接收Activity传递过来的数据
        val data = this.workerParameters.inputData.getString("key2")
        Log.i(TAG, "activity send data=$data")
        //反馈数据给Activity
        val outputData = Data.Builder().putString("key_feedback", "feedback data").build()
        return Result.success(outputData)
    }
}