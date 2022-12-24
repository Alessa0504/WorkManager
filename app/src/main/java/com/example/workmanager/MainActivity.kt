package com.example.workmanager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.example.workmanager.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnUploadWork1.setOnClickListener {
            // 定义request，对任务进行包装 - 单次执行  看到 UploadWorker.class，内部就是反射
            val oneTimeWorkRequest = OneTimeWorkRequest.Builder(UploadWorker::class.java).build()
            // 入队，执行任务，回调onWork()
            WorkManager.getInstance(this.applicationContext).enqueue(oneTimeWorkRequest)
        }

        binding.btnUploadWork2.setOnClickListener {
            // 传递数据到任务
            // 1.定义数据
            val sendData = Data.Builder().putString("key2", "activity data").build()
            // 2.定义request
            val oneTimeWorkRequest = OneTimeWorkRequest.Builder(UploadWorker2::class.java)
                .setInputData(sendData)  //携带数据
                .build()
            // 获取workManager反馈的数据 - 监听，内部有livedata -> lifecycle
            WorkManager.getInstance(this).getWorkInfoByIdLiveData(oneTimeWorkRequest.id)
                .observe(this, Observer { workInfo ->
                    val outputData = workInfo.outputData.getString("key_feedback")

                    Log.i(UploadWorker2.TAG, "状态=${workInfo.state.name}")
                    //状态机，防止拿到null
                    if (workInfo.state.isFinished) {
                        Log.i(UploadWorker2.TAG, "后台任务已经完成了，work feed data=$outputData")
                    }
                })
            WorkManager.getInstance(this.applicationContext).enqueue(oneTimeWorkRequest)
        }
    }
}