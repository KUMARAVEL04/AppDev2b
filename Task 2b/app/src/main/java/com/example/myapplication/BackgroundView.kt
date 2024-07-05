package com.example.myapplication

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class BackgroundView:View {
    var paint1:Paint = Paint()
    constructor(context: Context) : super(context) {
        init(null, 0)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init(attrs, defStyle)
    }
    fun init(attrs: AttributeSet?, defStyle: Int){
        paint1.setColor(Color.parseColor("#f9eee0"))
        paint1.strokeWidth=10f
    }
    override fun onDraw(canvas: Canvas) {
        val width = width.toFloat()
        val height= height.toFloat()
        val newWidth = width / 4
        val smallWidth = newWidth / 4
        canvas.drawColor(Color.parseColor("#2e2d2d"))
        canvas.drawRect(smallWidth, 0f, newWidth + smallWidth, height, paint1)
        canvas.drawRect(
            newWidth + smallWidth * 2,
            0f,
            newWidth * 2 + smallWidth * 2,
            height,
            paint1
        )
        canvas.drawRect(
            newWidth * 2 + smallWidth * 3,
            0f,
            newWidth * 3 + smallWidth * 3,
            height,
            paint1
        )
        super.onDraw(canvas)
    }
}