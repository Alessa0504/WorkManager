package com.example.workmanager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.example.workmanager.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnWork.setOnClickListener {
            // 对任务进行包装 - 单次执行  看到 UploadWorker.class，内部就是反射
            val oneTimeWorkRequest = OneTimeWorkRequest.Builder(UploadWorker::class.java).build()
            // 入队，执行任务，回调onWork()
            WorkManager.getInstance(this.applicationContext).enqueue(oneTimeWorkRequest)
        }
    }
}