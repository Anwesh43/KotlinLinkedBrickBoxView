package com.example.linkedbrickboxview

import android.graphics.*

/**
 * Created by anweshmishra on 30/05/18.
 */

fun Canvas.getSize(): Float {
    return Math.min(width, height).toFloat()/3
}

fun Canvas.goToCenter(cb : () -> Unit) {
    save()
    translate(width * 0.5f, height * 0.5f)
    cb()
    restore()
}

class DrawMethodContainer {
    companion object {
        private val cbs : ArrayList<(Canvas, Paint, Float) -> Unit> = ArrayList()
        init {
            cbs.add {canvas, paint, scale ->
                canvas.goToCenter {
                    val size : Float = 1.3f * canvas.getSize()
                    paint.color = Color.parseColor("#34495e")
                    canvas.scale(scale, scale)
                    canvas.drawRoundRect(RectF(-size, -size, size, size), size/3, size/3, paint)
                }
            }

            cbs.add {canvas, paint, scale ->
                canvas.goToCenter {
                    val size : Float = canvas.getSize()
                    paint.color = Color.parseColor("#9b59b6")
                    for (i in 0..1) {
                        canvas.save()
                        canvas.translate(0f, 2 * size / 3 * (1 - 2 * i))
                        canvas.drawRect(-0.9f * size * scale, -size/7, 0.9f * size * scale, size/7, paint)
                        canvas.restore()
                    }
                }
            }


            cbs.add {canvas, paint, scale ->
                canvas.goToCenter {
                    val size : Float = canvas.getSize()
                    val r : Float = size/3
                    paint.color = Color.parseColor("#2980b9")
                    for (i in 0..1) {
                        canvas.save()
                        canvas.translate(size/2 * (1 - 2 * i), 0f)
                        canvas.drawArc(RectF(-r, -r, r, r), 0f, 360f * scale, true, paint)
                        canvas.restore()
                    }
                }
            }
        }

        fun pop() : (Canvas, Paint, Float) -> Unit {
            val cb : (Canvas, Paint, Float) -> Unit= cbs[0]
            cbs.removeAt(0)
            return cb
        }

        fun isEmpty() : Boolean = cbs.size == 0
    }
}