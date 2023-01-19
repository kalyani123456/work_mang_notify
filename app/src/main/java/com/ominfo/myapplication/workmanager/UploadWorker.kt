package com.ominfo.myapplication.workmanager

import android.content.Context
import android.util.Log
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.ominfo.myapplication.MainActivity
import com.ominfo.myapplication.MainActivity.Companion.displayNotification
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class UploadWorker(context: Context,parameters: WorkerParameters) : Worker(context,parameters) {

   companion object{
       const val KEYW = "keyw"
   }
    override fun doWork(): Result {
        try {
            val list:ArrayList<Upload> = ArrayList<Upload>()
            list.add(Upload("19-01-2023 04:15:00",true))
            list.add(Upload("19-01-2023 04:16:00",true))
            val count = inputData.getInt(MainActivity.KEY,0)
            for (i in 0..list.size-1) {
               displayNotification(list[i].date)
            }

            val time = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
            val currDate = time.format(Date())

            val outputMsg = Data.Builder()
                .putString(KEYW,currDate).build()

            return Result.success(outputMsg)
        }catch (e:Exception){
            return Result.failure()
        }
    }

}