package com.ominfo.myapplication

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.TokenWatcher
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.work.*
import com.ominfo.myapplication.databinding.ActivityMainBinding
import com.ominfo.myapplication.workmanager.UploadWorker
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding

    companion object{
         lateinit var context: Context
         const val KEY = "key"
         val channelID = "com.ominfo.myapplication.channel1"
         var notificationManager:NotificationManager?=null

        fun displayNotification(date:String){
            val notificationId = 545
            val notification = NotificationCompat.Builder(context)
                .setChannelId(channelID)
                .setContentTitle("demo title")
                .setContentText(date)
                .setSmallIcon(android.R.drawable.btn_star)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build()
            notificationManager?.notify(notificationId,notification)
        }

        private fun createNotificationChannel(id:String, name:String, channelDes:String){
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
                val importance:Int = NotificationManager.IMPORTANCE_HIGH
                val channel = NotificationChannel(id,name,importance).apply {
                    description = channelDes
                }
                notificationManager?.createNotificationChannel(channel)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =  DataBindingUtil.setContentView(this,R.layout.activity_main)
        context = this
        //create notification channel instance
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel(channelID,"Demo","this is test")
        binding.tvClick.setOnClickListener {
            setOnTimeWorkRequest()
        }
    }

    //for periodic work request
    private fun setPeriodicWorkRequest(){
        val periodicWorkRequest:PeriodicWorkRequest = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            PeriodicWorkRequest.Builder(UploadWorker::class.java,16,TimeUnit.MINUTES)
                .build()
        } else {
            TODO("VERSION.SDK_INT < O")
        }
        WorkManager.getInstance(applicationContext).enqueue(periodicWorkRequest)
    }

    private fun setOnTimeWorkRequest(){
        //to get status of work manager
        val workManager = WorkManager.getInstance(applicationContext)
        //to add constraints
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        //to send data
        val data:Data = Data.Builder()
            .putInt(KEY,25)
            .build()
        val oneTimeWorkRequest = OneTimeWorkRequest.Builder(UploadWorker::class.java)
            .setConstraints(constraints)
            .setInputData(data)
            .build()
        workManager.enqueue(oneTimeWorkRequest)
        workManager.getWorkInfoByIdLiveData(oneTimeWorkRequest.id)
            .observe(this, Observer {
                binding.tvClick.text = it.state.name
                if (it.state.isFinished){
                    val data:Data = it.outputData
                    val message:String? = data.getString(UploadWorker.KEYW)
                    if (message != null) {
                        //displayNotification(message)
                    }
                    Toast.makeText(applicationContext,message,Toast.LENGTH_SHORT).show()
                }
            })



        //base code
      /*  val oneTimeWorkRequest = OneTimeWorkRequest.Builder(UploadWorker::class.java)
            .build()
        WorkManager.getInstance(applicationContext)
            .enqueue(oneTimeWorkRequest)*/
    }
}