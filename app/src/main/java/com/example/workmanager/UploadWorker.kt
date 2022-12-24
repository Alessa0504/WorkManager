package com.example.workmanager

import android.content.Context
import android.util.Log
import androidx.annotation.NonNull
import androidx.work.Worker
import androidx.work.WorkerParameters

/**
 * @Description:
 * @author zouji
 * @date 2022/12/24
 */
class UploadWorker(@NonNull context: Context, @NonNull workerParameters: WorkerParameters) :
    Worker(context, workerParameters) {

    private val TAG = "UploadWorker"

    // 后台任务且异步
    @NonNull
    override fun doWork(): Result {
        try {
            Thread.sleep(5000)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            Log.i(TAG, "doWork run...")
        }
        return Result.success()   // 本次任务成功
    }

}