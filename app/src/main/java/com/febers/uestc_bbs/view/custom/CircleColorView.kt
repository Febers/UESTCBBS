package com.febers.uestc_bbs.view.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.febers.uestc_bbs.utils.ColorUtils
import com.febers.uestc_bbs.utils.colorAccent

/**
 * 绘制一个带有色彩的圆形View
 */
class CircleColorView : View {

    private lateinit var paint: Paint
    private var color: Int = ColorUtils.toDarkColor(colorAccent())   //默认为该颜色
    private var radius: Int = 0
    private var drawRing: Boolean = false

    constructor(context: Context) : super(context) {init(null)}

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {init(attrs)}

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {init(attrs)}

    private fun init(attrs: AttributeSet?) {
        attrs ?: return
        paint = Paint()
        //val typeArray = ctx.obtainStyledAttributes(attrs, R.styleable.CircleColorView)
        //color = typeArray.getColor(R.styleable.CircleColorView_color, Color.BLUE)
    }

    fun setColor(color: Int) {
        this.color = color
    }

    private fun setDrawRing(drawRing: Boolean) {
        this.drawRing = drawRing
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val w = width /2
        val h = height /2
        radius = Math.min(w, h)
        val c = radius

        paint.style = Paint.Style.FILL
        paint.color = color
        paint.isAntiAlias = true
        canvas?.drawCircle(c.toFloat(), c.toFloat(), radius.toFloat(), paint)

        if (drawRing) {

        }
    }
}
