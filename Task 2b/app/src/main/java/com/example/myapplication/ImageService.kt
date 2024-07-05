package com.example.myapplication

import android.app.Service
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.IBinder
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class ImageService : Service() {
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }
    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    override fun onCreate() {
        super.onCreate()
        if(!ImageHolder.imagesLoaded){
            scope.launch {
                ImageCollect()
            }
        }
    }
    suspend fun ImageCollect (){
        println("ImageCollect")
        if (NetworkUtils.isInternetAvailable(this)) {
            println("Network Check")
            try{
                val jerry = RetrofitInstance.api.getImage("jerry")
                val tom = RetrofitInstance.api.getImage("tom")
                val obstacle = RetrofitInstance.api.getImage("obstacle")
                if (jerry.isSuccessful) {
                    jerry.body()?.let {
                        val bitmapJerry = BitmapFactory.decodeStream(it.byteStream())
                        ImageHolder.mouse = bitmapJerry
                        println("Bitmaps Loaded")

                    }
                }
                if (tom.isSuccessful) {
                    tom.body()?.let {
                        val bitmapTom = BitmapFactory.decodeStream(it.byteStream())
                        ImageHolder.cat = bitmapTom
                        println("Bitmaps Loaded")

                    }
                }
                if (obstacle.isSuccessful) {
                    obstacle.body()?.let {
                        val bitmapObstacle = BitmapFactory.decodeStream(it.byteStream())
                        ImageHolder.icon = bitmapObstacle
                        println("Bitmaps Loaded")

                    }
                }
                ImageHolder.imagesLoaded=true
            }catch (e:Exception){
                ImageHolder.mouse = BitmapFactory.decodeResource(this.resources, R.drawable.frame_3)
                ImageHolder.cat = BitmapFactory.decodeResource(this.resources, R.drawable.frame_4)
                ImageHolder.icon = BitmapFactory.decodeResource(this.resources, R.drawable.group_2)
                ImageHolder.imagesLoaded=true

            }
        }
        else{
            ImageHolder.mouse = BitmapFactory.decodeResource(this.resources, R.drawable.frame_3)
            ImageHolder.cat = BitmapFactory.decodeResource(this.resources, R.drawable.frame_4)
            ImageHolder.icon = BitmapFactory.decodeResource(this.resources, R.drawable.group_2)
            ImageHolder.imagesLoaded=true

        }
        val intent = Intent("com.example.myapplication.IMAGES_LOADED")
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }
    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }
}