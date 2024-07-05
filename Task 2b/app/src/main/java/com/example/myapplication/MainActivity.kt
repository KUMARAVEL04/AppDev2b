package com.example.myapplication

import android.app.ActivityOptions
import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import retrofit2.http.Body
import java.util.Arrays


class MainActivity : AppCompatActivity() {
    private val FPS = (1000 / 30).toLong()
    private var ifPause = false
    var infinite: Boolean =false
    var word:String = ""
    var tempWord =ArrayList<String>()
    lateinit var canvasView: MyView
    var luck:Int=-1
    var amount:Int=0
    var trueList:Array<Boolean>
    init {
        trueList = arrayOf(false,false,false)
        val first: Int = (0..2).random()
        val second: Int = (0..2).random()
        trueList[first] = true
        trueList[second] = true
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        canvasView = MyView(this)
        val backgroundView = BackgroundView(this)
        val frame:FrameLayout = FrameLayout(this)
        var layoutparams: FrameLayout.LayoutParams? = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT,
            Gravity.CENTER_HORIZONTAL or Gravity.CENTER_VERTICAL
        )
        frame.layoutParams=layoutparams
        frame.addView(backgroundView)
        frame.addView(canvasView)
        setContentView(frame)
        infinite= intent.getBooleanExtra("Infinite",false)
        hideUI()
        runBlocking {
            if (NetworkUtils.isInternetAvailable(this@MainActivity)) {
                fetchCollison()
                fetchWord()
                fetchCourse(2)
                luck=getHinderance().type-1
                amount=getHinderance().amount
            }
            else{
                canvasView.collisionLimit=2
                luck=-1
                amount=(1..4).random()
                trueList = arrayOf(false,false,false)
                val first: Int = (0..2).random()
                val second: Int = (0..2).random()
                trueList[first] = true
                trueList[second] = true
            }
        }
    }
    suspend fun fetchCollison() {
        val response = RetrofitInstance.api.getCollision()
        if (response.isSuccessful) {
            response.body()?.let {
                val obstacleLimit = it.obstacleLimit
                canvasView.collisionLimit=obstacleLimit
            }
        }
    }
    suspend fun fetchWord() {
        val length:Int = (7..15).random()
        val response = RetrofitInstance.api.getWord(mapOf("length" to length))
        if (response.isSuccessful) {
            response.body()?.let {
                word = it.word
                tempWord.add(word.uppercase())
            }
        }
    }
    suspend fun fetchCourse(length:Int):Array<Boolean> {
        try{
            val response = RetrofitInstance.api.getCourse(mapOf("extent" to length))
            if (response.isSuccessful) {
                response.body()?.let {
                    val word = it.obstacleCourse
                    val combo = arrayOf(false, false, false)
                    for (i in word) {
                        if (i == "R") {
                            combo[0] = true
                        } else if (i == "L") {
                            combo[2] = true
                        } else {
                            combo[1] = true
                        }
                    }

                    return combo
                }
            }
        }
        catch (e:Exception){

        }
        var combo = arrayOf(false,false,false)
        val first: Int = (0..2).random()
        val second: Int = (0..2).random()
        combo[first] = true
        combo[second] = true
        return combo
    }
    suspend fun getHinderance():LuckEffect{
        val response = RetrofitInstance.api.getLuck()
        if (response.isSuccessful) {
            response.body()?.let {
                return it
            }
        }
        return LuckEffect(-1, (1..4).random(), "")
    }



    fun hideUI(){
        actionBar?.hide()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowCompat.setDecorFitsSystemWindows(window, false)
            val controller = window.insetsController

            if (controller != null) {
                controller.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
                controller.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            window.decorView.systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_FULLSCREEN
                    )
        }
    }
    override fun onPause() {
        ifPause=true
        val musicIntent = Intent(this, BackgroundMuiscService::class.java)
        musicIntent.action="PAUSE"
        startService(musicIntent)
        super.onPause()
    }
    override fun onResume() {
        super.onResume()
        lifecycleScope.launch(Dispatchers.Default) {
            delay(500L)
            updateView(canvasView)
        }
        val musicIntent = Intent(this, BackgroundMuiscService::class.java).apply {
            action="PLAY"
        }
        startService(musicIntent)
        ifPause = false
    }
    private fun updateView(canvasView: MyView) {
        lifecycleScope.launch(Dispatchers.Default) {
            canvasView.update(infinite)
            if(canvasView.lifeLine.uppercase()in tempWord){
                canvasView.collisionLimit++
                tempWord.remove(canvasView.lifeLine.uppercase())
                canvasView.lifeLine=""
                launch {
                    fetchWord()
                }
            }
            val ab: ArrayList<Obstacles> = canvasView.getObsList()
            if (ab.size < 15 && ab.isNotEmpty() && canvasView.startCutscene) {
                if (ab[ab.size - 1].getTop() > canvasView.tom.height() * 1.7f) {
                    canvasView.addObstacle(trueList)
                    launch(Dispatchers.IO) {
                        trueList=fetchCourse(2)
                    }
                }
                else if(ab[ab.size-1].getTop()>=canvasView.tom.height()*1-5f && ab[ab.size-1].getTop()<=canvasView.tom.height()*1+5f && (0..4).random()in(1..2)){

                    when((0..2).random()){
                        0->{
                            canvasView.addAdditive(luck,amount)
                            launch(Dispatchers.IO) {
                                if (NetworkUtils.isInternetAvailable(this@MainActivity)){
                                    luck=getHinderance().type-1
                                    amount=getHinderance().amount
                                }
                                else{
                                    luck=(0..2).random()
                                    amount=(1..4).random()
                                }
                            }
                        }
                        in 1..2-> {
                            if(word.isNotBlank()){
                                canvasView.addWord(word.first().uppercaseChar())
                                word=word.drop(1)
                            }
                        }
                    }
                }
            }

            if (!ifPause && !canvasView.end){
                delay(FPS)
                updateView(canvasView)
            }
            else if (canvasView.end) {
                withContext(Dispatchers.Main){
                    winDialog(canvasView.score.toInt())
                }
            }
        }
    }
    fun winDialog(scorx: Int) {
        val winBox = Dialog(this)
        winBox.setContentView(R.layout.win_dialog)
        winBox.setCancelable(false)
        winBox.window!!.setBackgroundDrawable(getDrawable(R.drawable.dialog_bg))
        val score = winBox.window!!.findViewById<TextView>(R.id.score)
        val plyAg = winBox.window!!.findViewById<Button>(R.id.playButton)
        val homeBut = winBox.window!!.findViewById<Button>(R.id.homeButton)
        val textBut = winBox.window!!.findViewById<Button>(R.id.nameField)
        textBut.text = "GAME OVER"
        score.text = "Score: $scorx"
        val options = ActivityOptions.makeCustomAnimation(baseContext, androidx.appcompat.R.anim.abc_fade_in, androidx.appcompat.R.anim.abc_fade_out).toBundle()
        plyAg.setOnClickListener { v: View? ->
            winBox.dismiss()
            finish()
            startActivity(intent,options)
        }
        homeBut.setOnClickListener { v: View? ->
            val home = Intent(this@MainActivity, MainActivity2::class.java)
            winBox.dismiss()
            finish()
            startActivity(home,options)
        }
        winBox.window!!.setLayout(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        val mp: MediaPlayer = MediaPlayer.create(baseContext, R.raw.sound3)
        mp.setOnCompletionListener { mp -> mp.release() }
        mp.start()
        winBox.show()
    }
}