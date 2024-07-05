package com.example.myapplication

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
object ImageHolder {
    var mouse: Bitmap? = null
    var cat: Bitmap? = null
    var icon: Bitmap? = null
    var imagesLoaded = false
}
class Character(left: Float=0f, top: Float=0f, right: Float=0f, bottom: Float=0f) : RectF(left,top,right,bottom) {
    var lane=0
    var colDur: Int = 0
    var isOut: Boolean = false
    var paint3: Paint = Paint()
    var lives: Int=0
}
open class Obstacles(width: Float, size: Float): RectF() {
    companion object{
        var speed=15f
    }
    var sizx: Float = 0f
    private var isOut = false
    var lane: Int = 0
    var paint: Paint? = null
    var solution: ArrayList<Int> = ArrayList()
    init {
        this.sizx = size
        this.top=0f
        this.bottom = this.top + size
        this.right = width - size / 2
        this.left = width + size / 2
    }

    fun getTop(): Float {
        return top
    }
    fun setPosition(dim: Float, lane:Int) {
        this.left = dim - sizx / 2
        this.right = dim + sizx / 2
        this.lane=lane
    }
    fun isOut():Boolean{
        return isOut
    }
    fun draw(canvas: Canvas):RectF{
        if (canvas.height < top) {
            isOut = true
        }
        return this
    }
    fun updatePosition(){
        this.top+=speed
        this.bottom+=speed
    }
}
class Cheese(width:Float, size:Float):Obstacles(width, size) {

}
class Additives(width:Float, size:Float, lucky:Int,amount:Int):Obstacles(width, size) {
    var luck:Int
    var amnt:Int
    init {
        amnt=amount
        luck=lucky
    }
}
class StringBox(width:Float, size:Float):Obstacles(width, size){
    var text:Char?=null
}