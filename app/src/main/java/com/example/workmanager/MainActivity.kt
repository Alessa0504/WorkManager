package com.example.workmanager

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.work.*
import com.example.workmanager.databinding.ActivityMainBinding
import com.example.workmanager.multiply.Worker1
import com.example.workmanager.multiply.Worker2
import com.example.workmanager.multiply.Worker3
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    @SuppressLint("IdleBatteryChargingConstraints")
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /**
         * 单任务
         */
        binding.btnUploadWork1.setOnClickListener {
            // 定义request，对任务进行包装 - 单次执行  看到 UploadWorker.class，内部就是反射
            val oneTimeWorkRequest = OneTimeWorkRequest.Builder(UploadWorker::class.java).build()
            // 入队，执行任务，回调onWork()
            WorkManager.getInstance(this.applicationContext).enqueue(oneTimeWorkRequest)
        }

        /**
         * 传递数据到任务 & 监听
         */
        binding.btnUploadWork2.setOnClickListener {
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

        /**
         * 多个任务 & 执行顺序
         */
        binding.btnUploadWork3.setOnClickListener {
            // 定义request
            val oneTimeWorkRequest1 = OneTimeWorkRequest.Builder(Worker1::class.java)
                .build()
            val oneTimeWorkRequest2 = OneTimeWorkRequest.Builder(Worker2::class.java)
                .build()
            val oneTimeWorkRequest3 = OneTimeWorkRequest.Builder(Worker3::class.java)
                .build()
            // 任务集合
            val onTimeWorkRequests = ArrayList<OneTimeWorkRequest>()
//            onTimeWorkRequests.add(oneTimeWorkRequest1)
            onTimeWorkRequests.add(oneTimeWorkRequest2)
            onTimeWorkRequests.add(oneTimeWorkRequest3)
            // 控制执行顺序 -先执行 任务2&3，再执行 任务1
            WorkManager.getInstance(this).beginWith(onTimeWorkRequests)
                .then(oneTimeWorkRequest1)
                .enqueue()
        }

        /**
         * 轮询任务
         */
        binding.btnUploadWork4.setOnClickListener {
            // OneTimeWorkRequest -单任务request
            // PeriodicWorkRequest -轮询，多任务request
            // 重复的任务，多次循环，参数repeatInterval: xx分钟轮询一次，<15时就是15，最少15分钟才能轮询一次
            val periodicWorkRequest =
                PeriodicWorkRequest.Builder(Worker1::class.java, 10, TimeUnit.SECONDS).build()
            // 监听状态
            WorkManager.getInstance(this).getWorkInfoByIdLiveData(periodicWorkRequest.id)
                .observe(this) { workInfo ->
                    Log.i(Worker1.TAG, "状态=${workInfo.state.name}")
                }
            WorkManager.getInstance(this).enqueue(periodicWorkRequest)
        }

        /**
         * 约束条件
         */
        binding.btnUploadWork5.setOnClickListener {
            // 约束条件：必须满足以下条件才能执行后台任务(网络连接 & 插入电源 & 处于空闲状态)，内部做了电量优化(Android App不耗电)
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)  //网络连接
                .setRequiresCharging(true)  //充电中
                .setRequiresDeviceIdle(true)  //空闲时(没有玩游戏)
                .build()

            // 定义request
            val oneTimeWorkRequest = OneTimeWorkRequest.Builder(Worker1::class.java)
                .setConstraints(constraints)   //设置约束
                .build()
            // 加入队列
            WorkManager.getInstance(this).enqueue(oneTimeWorkRequest)
        }
    }
}