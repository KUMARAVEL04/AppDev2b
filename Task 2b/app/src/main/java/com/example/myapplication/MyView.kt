package com.example.myapplication

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.RectF
import android.media.MediaPlayer
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import java.util.Random
import kotlin.math.max
import kotlin.math.min

class MyView:View {
    var collisionLimit = 2
    lateinit var paint1: Paint
    lateinit var paintx: Paint
    var end: Boolean = false
    var ifStart: Boolean = true
    var jerry: Character = Character()
    var tom:Character = Character()
    var widths: FloatArray = FloatArray(3)
    var time: Int = 0

    private var obsList: ArrayList<Obstacles> = ArrayList()
    private var addList: ArrayList<Additives> = ArrayList()
//    var cheeseList:  ArrayList<Cheese> = ArrayList()
    var stringList: ArrayList<StringBox> = ArrayList()

    var collision: Int = 0
    var immFram: Boolean = true
    var cutscene: Boolean = true
    var startCutscene: Boolean = false
    var random: Random = Random()
    var immobile: Int = 0
//
//    var mouse: Bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.frame_3)
//    var cat: Bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.frame_4)
//    var icon: Bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.group_2)
    var add: Bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.group_10)
    var cheese: Bitmap = BitmapFactory.decodeResource(context.resources,R.drawable.cheese_png)

    var score: Float = 0f
    lateinit var context1: Context
    var lifeLine:String=""

    constructor(context: Context) : super(context) {
        init(null, 0)
        context1=context
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs, 0)
        context1=context
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init(attrs, defStyle)
        context1=context
    }

    private fun init(attrs: AttributeSet?, defStyle: Int) {
        obsList.clear()
        addList.clear()
        paint1 = Paint()
        paint1.setColor(Color.parseColor("#f9eee0"))
        paint1.strokeWidth=10f
        paintx = Paint()
    }
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val width = widths[1] * 2
        if (immobile < 1 && event!=null && time<1) {
            if (0 <= event.x && event.x < width / 2) {
                jerry.lane--
                if (jerry.lane > -2) {
                    jerry.offset(-width * 5 / 16, 0f)
                    val mp = MediaPlayer.create(context, R.raw.sweep)
                    mp.setOnCompletionListener { mp -> mp.release() }
                    mp.start()
                    vibrate(80)
                } else {
                    jerry.lane++
                }
            } else {
                jerry.lane++
                if (jerry.lane < 2) {
                    jerry.offset(width * 5 / 16, 0f)
                    val mp = MediaPlayer.create(context, R.raw.sweep)
                    mp.setOnCompletionListener { mp -> mp.release() }
                    mp.start()
                    vibrate(80)
                } else {
                    jerry.lane--
                }
            }
        }
        return super.onTouchEvent(event)
    }

    fun vibrate(a:Long){
        val vibrator: Vibrator
        vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager: VibratorManager = context1.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator

        } else {
            context1.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(a,VibrationEffect.DEFAULT_AMPLITUDE))

        } else {
            vibrator.vibrate(a)
        }
    }

    override fun onDraw(canvas: Canvas) {
        val width = width.toFloat()
        val height= height.toFloat()
        widths[1] = width / 2
        widths[0] = width * 3 / 16
        widths[2] = width * 13 / 16
        val newWidth = width / 4
        val smallWidth = newWidth / 4
        if (ifStart) {
            ifStart = false
            jerry = Character(
                (width / 2 - smallWidth),
                (height),
                (width / 2 + smallWidth),
                (height + 2 * smallWidth)
            )
            tom = Character(
                (width / 2 - width / 8),
                (height * 1.2f),
                (width / 2 + width / 8),
                (height * 1.2f + width / 4)
            )
        }
        val paint4 = Paint()
        paint4.color = Color.WHITE
        paint4.style = Paint.Style.STROKE
        paint4.strokeWidth = 2f
        canvas.drawBitmap(ImageHolder.mouse!!, null, jerry, jerry.paint3)
        if (collision > 0 || cutscene || !startCutscene) {
            canvas.drawBitmap(ImageHolder.cat!!, null, tom, tom.paint3)
        }
        val iterator21: Iterator<Obstacles> = obsList.iterator()
        while (iterator21.hasNext()) {
            val ab = iterator21.next()
            canvas.drawBitmap(ImageHolder.icon!!, null, ab.draw(canvas), ab.paint)
        }
        val iterator22: Iterator<Additives> = addList.iterator()
        while (iterator22.hasNext()) {
            val ab = iterator22.next()
            canvas.drawBitmap(add, null,ab.draw(canvas), ab.paint)
        }
//        val iterator23: Iterator<Cheese> = cheeseList.iterator()
//        while (iterator23.hasNext()) {
//            val ab = iterator23.next()
//            canvas.drawBitmap(cheese, null, ab.draw(canvas), ab.paint)
//        }
        val iterator24:Iterator<StringBox> = stringList.iterator()
        while(iterator24.hasNext()){
            val paint = Paint()
            paint.textAlign=Paint.Align.CENTER
            paint.color=Color.DKGRAY
            paint.textSize=newWidth/2
            val ab = iterator24.next()
            canvas.drawText(ab.text.toString(),ab.centerX(),ab.centerY(),paint)
        }
        var txtscore = score.toInt()
        var textpaint = Paint()
        textpaint.isAntiAlias
        textpaint.textSize = height * 0.025f
        textpaint.color = Color.WHITE
        canvas.drawCircle(getWidth() * 0.55f+getWidth()*0.05f, height * 0.08f, height * 0.03f, textpaint)
        canvas.drawCircle(getWidth() * 0.85f+getWidth()*0.05f, height * 0.08f, height * 0.03f, textpaint)
        canvas.drawRect(
            getWidth() * 0.55f+getWidth()*0.05f,
            height * 0.08f - height * 0.03f,
            getWidth() * 0.85f+getWidth()*0.05f,
            height * 0.08f + height * 0.03f,
            textpaint
        )
        textpaint.color = Color.DKGRAY
        canvas.drawCircle(getWidth() * 0.55f+getWidth()*0.05f, height * 0.08f, height * 0.025f, textpaint)
        canvas.drawCircle(getWidth() * 0.85f+getWidth()*0.05f, height * 0.08f, height * 0.025f, textpaint)
        canvas.drawRect(
            getWidth() * 0.55f+getWidth()*0.05f,
            height * 0.08f - height * 0.025f,
            getWidth() * 0.85f+getWidth()*0.05f,
            height * 0.08f + height * 0.025f,
            textpaint
        )
        textpaint.color = Color.WHITE
        canvas.drawText(
            "Score: $txtscore",
            getWidth() * 0.576f+getWidth()*0.05f,
            height * 0.08f + height * 0.01f,
            textpaint
        )

        canvas.drawCircle(getWidth() *( 0.05f+0.05f), height * 0.08f, height * 0.03f, textpaint)
        canvas.drawCircle(getWidth() *( 0.35f+0.05f), height * 0.08f, height * 0.03f, textpaint)
        canvas.drawRect(
            getWidth() *( 0.05f+0.05f),
            height * 0.08f - height * 0.03f,
            getWidth() *( 0.35f+0.05f),
            height * 0.08f + height * 0.03f,
            textpaint
        )
        textpaint.color = Color.DKGRAY
        canvas.drawCircle(getWidth() *( 0.05f+0.05f), height * 0.08f, height * 0.025f, textpaint)
        canvas.drawCircle(getWidth() *( 0.35f+0.05f), height * 0.08f, height * 0.025f, textpaint)
        canvas.drawRect(
            getWidth() *( 0.05f+0.05f),
            height * 0.08f - height * 0.025f,
            getWidth() *( 0.35f+0.05f),
            height * 0.08f + height * 0.025f,
            textpaint
        )
        val newPaint = Paint()
        val rectCheese:RectF = RectF(
            getWidth() * (0.051f+0.035f),
            height * 0.08f - height * 0.022f,
            getWidth() * (0.085f+0.092f),
            height * 0.08f + height * 0.022f)
        canvas.drawBitmap(cheese,null,rectCheese,newPaint)
        textpaint.color = Color.WHITE
        canvas.drawText(
            "Lives: ${collisionLimit-collision}",
            getWidth() * (0.088f+0.12f),
            height * 0.08f + height * 0.01f,
            textpaint
        )
        super.onDraw(canvas)
    }

    fun addObstacle(combo:Array<Boolean>) {
        val soln = ArrayList<Int>()
        for (i in 0..2) {
            if (!combo[i]) {
                soln.add(i - 1)
            }
        }
        for (i in 0..2) {
            if (combo[i]) {
                val ab = Obstacles(widths[1], widths[1] / 4)
                ab.setPosition(widths[i], i - 1)
                ab.solution.addAll(soln)
                obsList.add(ab)
            }
        }
    }
    fun addObstacle() {
        var combo = arrayOf(false,false,false)
        val first: Int = (0..2).random()
        val second: Int = (0..2).random()
        combo[first] = true
        combo[second] = true
        val soln = ArrayList<Int>()
        for (i in 0..2) {
            if (!combo[i]) {
                soln.add(i - 1)
            }
        }
        for (i in 0..2) {
            if (combo[i]) {
                val ab = Obstacles(widths[1], widths[1] / 4)
                ab.setPosition(widths[i], i - 1)
                ab.solution.addAll(soln)
                obsList.add(ab)
            }
        }
    }

    fun getObsList(): ArrayList<Obstacles> {
        return obsList
    }

    fun update(infinite:Boolean) {
        if (!startCutscene) {
            tom.offset(0f, -6f)
            jerry.offset(0f, -6f)
            if (jerry.centerY() <= height * 0.7f) {
                startCutscene = true
                addObstacle()
            }
        } else if (cutscene) {
            tom.offset(0f, 2f)
            if (tom.top > height) {
                cutscene = false
            }
        }
        score += 0.1f
        Obstacles.speed = min(Obstacles.speed + 0.001f, 75f)
        val iterator1: Iterator<Obstacles> = obsList.iterator()
        while (iterator1.hasNext()) {
            iterator1.next().updatePosition()
        }
        var additerator = addList.iterator()
        while (additerator.hasNext()) {
            additerator.next().updatePosition()
        }
//        var cheeseIterator = cheeseList.iterator()
//        while (cheeseIterator.hasNext()) {
//            cheeseIterator.next().updatePosition()
//        }
        var stringIterator= stringList.iterator()
        while (stringIterator.hasNext()) {
            stringIterator.next().updatePosition()
        }
        additerator = addList.iterator()
        while (additerator.hasNext()) {
            if (additerator.next().isOut()) {
                additerator.remove()
            }
        }
        stringIterator= stringList.iterator()
        while (stringIterator.hasNext()) {
            if (stringIterator.next().isOut()) {
                stringIterator.remove()
            }
        }
        val iterator = obsList.iterator()
        while (iterator.hasNext()) {
            if (iterator.next().isOut()) {
                iterator.remove()
                tomMove(tom)
            }
        }
//        cheeseIterator = cheeseList.iterator()
//        while (cheeseIterator.hasNext()) {
//            if(cheeseIterator.next().isOut()){
//                cheeseIterator.remove()
//            }
//        }
        jerryIntersects()
        tomMove(tom)
        if(time>0){
            tomMove(jerry)
            time--
            if(time==0){
                jerry.paint3.setColorFilter(null)
            }
        }
        if (collision >= collisionLimit || (score > 200 && !infinite)) {
            if (score < 200) {
                tom.offset(jerry.centerX() - tom.centerX(), -50f)
                invalidate()
            }
            end = true
        }
        if (jerry.colDur > 0) {
            jerry.colDur-=  1
            if (jerry.colDur % 10 == 0) {
                if (immFram) {
                    jerry.paint3.alpha = 150
                } else {
                    jerry.paint3.alpha = 255
                }
                immFram = !immFram
            }
        } else {
            jerry.paint3.alpha = 255
            immFram = true
        }
        if (collision > 0 && tom.top > jerry.bottom + 20) {
            tom.offset(0f, -5f)
        }
        invalidate()
    }

    fun tomMove(tom:Character) {
        var move = false
        var right = tom.lane != 1
        var left = tom.lane != -1
        val iteratorx: Iterator<Obstacles> = obsList.iterator()
        val iteratorx1: Iterator<Obstacles> = obsList.iterator()
        val path = java.util.ArrayList<Int>()

        while (iteratorx.hasNext()) {
            val ab = iteratorx.next()
            if (ab.centerX() == tom.centerX() && ab.bottom > tom.top * 0.85f && ab.top < tom.top) {
                move = true
                path.addAll(ab.solution)
                break
            }
        }
        if (move) {
            if (path[0] > tom.lane) {
                left = false
            }
            if (path[0] < tom.lane) {
                right = false
            }
            while (iteratorx1.hasNext()) {
                val ab = iteratorx1.next()
                if (ab.lane == tom.lane - 1 && ab.bottom > tom.top && ab.top < tom.bottom) {
                    left = false
                }
                if (ab.lane == tom.lane + 1 && ab.bottom > tom.top && ab.top < tom.bottom) {
                    right = false
                }
            }
        } else {
            right = false
            left = false
        }
        if (left) {
            tom.lane--
            if (tom.lane > -2) {
                tom.offset(-widths[1] * 5 / 8, 0f)
            } else {
                tom.lane++
            }
        } else if (right) {
            tom.lane++
            if (tom.lane < 2) {
                tom.offset(widths[1] * 5 / 8, 0f)
            } else {
                tom.lane--
            }
        }
    }


    fun jerryIntersects() {
        val iterator2 = obsList.iterator()
        while (iterator2.hasNext() && jerry.colDur == 0) {
            if (iterator2.next().intersect(jerry)) {
                if (collision < collisionLimit) {
                    val mp = MediaPlayer.create(context, R.raw.wrong)
                    mp.setOnCompletionListener { mp -> mp.release() }
                    mp.start()
                    vibrate(500)
                }
                iterator2.remove()
                jerry.colDur=100
                cutscene = false
                collision++
                break
            }
        }
        val iterator4 = stringList.iterator()
        while (iterator4.hasNext()) {
            val ab =iterator4.next()
            if (ab.intersect(jerry)) {
                if (collision < collisionLimit) {
                    val mp = MediaPlayer.create(context, R.raw.jump)
                    mp.setOnCompletionListener { mp -> mp.release() }
                    mp.start()
                    vibrate(500)
                }
                lifeLine += ab.text
                iterator4.remove()
            }
        }

//        val iterator3 = cheeseList.iterator()
//        while (iterator3.hasNext() && jerry.colDur == 0) {
//            if (iterator3.next().intersect(jerry)) {
//                    val mp = MediaPlayer.create(context, R.raw.wrong)
//                    mp.setOnCompletionListener { mp -> mp.release() }
//                    mp.start()
//                    vibrate(500)
//                iterator3.remove()
//                collisionLimit++
//                break
//            }
//        }

        val additivesIterator = addList.iterator()
        while (additivesIterator.hasNext() && jerry.colDur == 0) {
            val additives = additivesIterator.next()
            if (additives.intersect(jerry)) {
                    when(additives.luck){
                        0->{
                            val mp = MediaPlayer.create(context, R.raw.badluck)
                            mp.setOnCompletionListener { mp -> mp.release() }
                            mp.start()
                            Obstacles.speed += additives.amnt
                        }
                        1->{
                            val mp = MediaPlayer.create(context, R.raw.goodluck)
                            mp.setOnCompletionListener { mp -> mp.release() }
                            mp.start()
                            time=additives.amnt*100
                            jerry.paint3.setColorFilter(
                                PorterDuffColorFilter(
                                    0x78ffaf03  ,
                                    PorterDuff.Mode.SRC_IN
                                ))
                        }
                        2->{
                            val mp = MediaPlayer.create(context, R.raw.badluck)
                            mp.setOnCompletionListener { mp -> mp.release() }
                            mp.start()
                            collision+= max(additives.amnt/2,1)
                    }
                }
                additivesIterator.remove()
            }
        }
    }
    fun addAdditive(luck:Int,amount:Int) {
        val lane = random.nextInt(3)
        val ad = Additives(widths[1], widths[1] / 4, luck, amount)
        ad.setPosition(widths[lane], lane - 1)
        addList.add(ad)
    }
//    fun addCheese() {
//        val lane = random.nextInt(3)
//        val ad = Cheese(widths[1], widths[1] / 4)
//        ad.setPosition(widths[lane], lane - 1)
//        cheeseList.add(ad)
//    }
    fun addWord(char:Char){
        val lane = random.nextInt(3)
        val ad = StringBox(widths[1], widths[1] / 4)
        ad.setPosition(widths[lane], lane - 1)
        ad.text=char
        stringList.add(ad)
    }
}