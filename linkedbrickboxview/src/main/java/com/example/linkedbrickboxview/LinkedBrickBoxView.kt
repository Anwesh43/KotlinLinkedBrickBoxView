package com.example.linkedbrickboxview

/**
 * Created by anweshmishra on 30/05/18.
 */

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.MotionEvent
import android.graphics.*

class LinkedBrickBoxView (ctx : Context) : View(ctx) {

    private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val renderer : Renderer = Renderer(this)

    override fun onDraw(canvas : Canvas) {
        renderer.render(canvas, paint)
    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                renderer.handleTap()
            }
        }
        return true
    }

    data class State (var scale : Float = 0f, var dir : Float = 0f, var prevScale : Float = 0f) {

        fun update(stopcb : (Float) -> Unit) {
            scale += 0.1f * dir
            if (Math.abs(scale - prevScale) > 1) {
                scale = prevScale + dir
                dir = 0f
                prevScale = scale
                stopcb(prevScale)
            }
        }

        fun startUpdating(startcb : () -> Unit) {
            if (dir == 0f) {
                dir = 1 - 2 * prevScale
                startcb()
            }
        }

    }

    data class Animator(var view : View, var animated : Boolean = false) {

        fun animate(updatecb : () -> Unit) {
            if (animated) {
                updatecb()
                try {
                    Thread.sleep(50)
                    view.invalidate()
                } catch (ex : Exception) {

                }
            }
        }

        fun start() {
            if (!animated) {
                animated = true
                view.postInvalidate()
            }
        }

        fun stop() {
            if (animated) {
                animated = false
            }
        }
    }

    class LLBNode(var i : Int, val state : State = State(), val cb : (Canvas, Paint, Float) -> Unit = DrawMethodContainer.pop()) {

        var next : LLBNode? = null

        var prev : LLBNode? = null

        init {
            addNeighbor()
        }

        fun addNeighbor() {
            if (!DrawMethodContainer.isEmpty()) {
                next = LLBNode(i+1)
                next?.prev = this
            }
        }

        fun draw(canvas : Canvas, paint : Paint) {
            prev?.draw(canvas, paint)
            cb(canvas, paint, state.scale)
        }

        fun update(stopcb : (Float) -> Unit) {
            state.update(stopcb)
        }

        fun startUpdating(startcb : () -> Unit) {
            state.startUpdating(startcb)
        }

        fun getNext(dir : Int, cb : () -> Unit) : LLBNode {
            var curr : LLBNode? = prev
            if (dir == 1) {
                curr = next
            }
            if (curr != null) {
                return curr
            }
            cb()
            return this
        }
    }

    data class LinkedBrickBox(var i : Int) {

        var curr : LLBNode = LLBNode(0)

        var dir : Int = 1

        fun draw(canvas : Canvas, paint : Paint) {
            curr.draw(canvas, paint)
        }

        fun update(stopcb : (Float) -> Unit) {
            curr.update {
                curr = curr.getNext(dir) {
                    dir *= -1
                }
                stopcb(it)
            }
        }

        fun startUpdating(startcb : () -> Unit) {
            curr.startUpdating(startcb)
        }
    }

    data class Renderer(var view : LinkedBrickBoxView) {

        private val animator : Animator = Animator(view)

        private val linkedBrickBox : LinkedBrickBox = LinkedBrickBox(0)

        fun render(canvas : Canvas, paint : Paint) {
            canvas.drawColor(Color.parseColor("#212121"))
            linkedBrickBox.draw(canvas, paint)
            animator.animate {
                linkedBrickBox.update {
                    animator.stop()
                }
            }
        }

        fun handleTap() {
            linkedBrickBox.startUpdating {
                animator.start()
            }
        }
    }

    companion object {
        fun create(activity : Activity) : LinkedBrickBoxView {
            val view : LinkedBrickBoxView = LinkedBrickBoxView(activity)
            activity.setContentView(view)
            return view
        }
    }
}