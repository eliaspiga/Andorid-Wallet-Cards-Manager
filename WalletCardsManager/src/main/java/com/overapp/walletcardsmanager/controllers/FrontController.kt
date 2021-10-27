package com.overapp.walletcardsmanager.controllers

import android.animation.ValueAnimator
import android.util.Log
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import com.overapp.walletcardsmanager.CardsManager
import com.overapp.walletcardsmanager.values.Direction
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

class FrontController(val scrollLayout: View, val topMargin: Float, val bottomMargin: Float) {

    //region drag variables
    private var initialY: Float = 0f
    private var lastReadedY: Float = 0f
    private var lastRootY: Float = 0f
    private var lastV = 0f

    private var direction = Direction.DOWN

    private var velocityTracker: VelocityTracker? = null
    private var frontTouchListener: View.OnTouchListener
    //endregion

    init {
        frontTouchListener = setupFrontTouchListener()
        scrollLayout.setOnTouchListener(frontTouchListener)
    }

    //region public methods
    fun goAnimated(from: Float, to: Float) {
        autoDragCardLayout(from, to)
    }

    fun go(to: Float) {
        scrollLayout.translationY = to
    }
    //endregion

    //region motion
    private fun setupFrontTouchListener() = object : View.OnTouchListener {
        override fun onTouch(view: View, event: MotionEvent): Boolean {

            when (event.action) {
                MotionEvent.ACTION_DOWN -> {

                    lastReadedY = event.rawY
                    initialY = lastReadedY

                    if (velocityTracker == null) {
                        velocityTracker = VelocityTracker.obtain()
                    } else {
                        velocityTracker?.clear()
                    }
                }
                MotionEvent.ACTION_MOVE -> {

                    val actualRawY: Float = event.rawY
                    val deltaY: Float = actualRawY - lastReadedY
                    lastReadedY = actualRawY
                    val positionResult: Float = scrollLayout.translationY + deltaY

                    when {
                        (positionResult in topMargin..bottomMargin) -> {

                            Log.d(
                                "COORDINATES",
                                "Top Margin : $topMargin | Bottom Margin : $bottomMargin | " +
                                        "Position result : $positionResult | Raw Y : ${event.rawY} ${event.rawY.toInt()}"
                            )

                            direction = if (deltaY >= 0f) {
                                //going down

                                //if I was going down call the opposite direction func
                                if (direction == Direction.UP)
                                    CardsManager.onFrontCardOppositeDirectionToDown()
                                CardsManager.onFrontCardDown(deltaY)
                                Direction.DOWN
                            } else {
                                //going up
                                CardsManager.onFrontCardUp(deltaY)
                                Direction.UP
                            }

                            scrollLayout.translationY += deltaY
                            lastRootY = scrollLayout.translationY
                            CardsManager.setFrontCardPosition(lastRootY)
                        }

                        positionResult.toInt() < topMargin -> {
                            scrollLayout.translationY = topMargin
                        }

                        positionResult.toInt() > bottomMargin -> {
                            scrollLayout.translationY = bottomMargin
                        }

                    }

                    //setup velocity tracker
                    velocityTracker?.apply {
                        val pointerId: Int = event.getPointerId(event.actionIndex)
                        addMovement(event)
                        computeCurrentVelocity(1000)

                        //get the last velocity also taking into account the x param = (Vyˆ2 + Vxˆ2)ˆ(1/2)
                        val vx = this.getXVelocity(pointerId).pow(2)
                        val vy = this.getYVelocity(pointerId).pow(2)
                        lastV = sqrt((vx + vy).toDouble()).toFloat()
                    }
                }
                MotionEvent.ACTION_UP -> {
                    //manage motion by velocity
                    if (abs(lastV) > 1800f) {
                        //compare actualY with lastY to get the direciton of the drag
                        if (initialY < event.rawY) {
                            goDown()
                        } else {
                            goUp()
                        }
                    } else {
                        //manage motion by positions
                        if (scrollLayout.translationY > (topMargin + bottomMargin) / 2) {
                            goDown()
                        } else {
                            goUp()
                        }
                    }
                    //clear the velocity tracker
                    velocityTracker?.clear()
                    velocityTracker = null
                }
                MotionEvent.ACTION_CANCEL -> {
                    //clear the velocity tracker
                    velocityTracker?.clear()
                    velocityTracker = null
                }
            }
            return true
        }
    }

    //function called when card go down
    private fun goDown() {
        //autodrag to go down
        autoDragCardLayout(lastRootY, bottomMargin.toFloat())
        //set lastY
        lastReadedY = bottomMargin
        //notify on card opened
        CardsManager.onFrontCardOpened()
        //change open status
        CardsManager.changeBackOpenStatus()
    }

    //function called when card go up
    private fun goUp() {
        //autodrag to go up
        autoDragCardLayout(lastRootY, topMargin.toFloat())
        //set lastY
        lastReadedY = topMargin
        //notify con card closed
        CardsManager.onFrontCardClosed()
        //notify on card closed
        CardsManager.changeBackOpenStatus()
    }

    //region private methods
    //autodrag the card with animation
    private fun autoDragCardLayout(from: Float, to: Float) {
        val va = ValueAnimator.ofFloat(from, to)
        val mDuration = 250 //in millis

        va.duration = mDuration.toLong()
        va.addUpdateListener { animation ->
            val value = (animation.animatedValue as Float)
            CardsManager.setFrontCardPosition(value)
            scrollLayout.translationY = value
        }
        va.start()
    }
    //endregion
}