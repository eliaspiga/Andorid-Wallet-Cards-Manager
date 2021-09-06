package com.overapp.walletcardsmanager.controllers

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.util.Log
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import com.overapp.walletcardsmanager.CardsManager
import com.overapp.walletcardsmanager.values.Direction
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

class FrontController(val scrollLayout: View, val topMargin: Int, val bottomMargin: Int) {

    //region drag variables
    private var initialY = 0
    private var lastReadedY = 0
    private var lastRootY = 0
    private var lastVelocity = 0f

    private var direction = Direction.DOWN

    private var velocityTracker: VelocityTracker? = null
    private var frontTouchListener: View.OnTouchListener
    //endregion

    init {
        frontTouchListener = setupFrontTouchListener()
        scrollLayout.setOnTouchListener(frontTouchListener)
    }

    //region public methods
    fun goAnimated(from: Int, to: Int) {
        autoDragCardLayout(from.toFloat(), to.toFloat())
    }

    fun go(to: Int) {
        scrollLayout.translationY = to.toFloat()
    }
    //endregion

    //region motion
    private fun setupFrontTouchListener() = object : View.OnTouchListener {
        @SuppressLint("BinaryOperationInTimber", "ClickableViewAccessibility")
        override fun onTouch(view: View, event: MotionEvent): Boolean {

            when (event.action) {
                MotionEvent.ACTION_DOWN -> {

                    lastReadedY = event.rawY.toInt()
                    initialY = lastReadedY

                    if (velocityTracker == null) {
                        velocityTracker = VelocityTracker.obtain()
                    } else {
                        velocityTracker?.clear()
                    }
                }
                MotionEvent.ACTION_MOVE -> {

                    val actualRawY = event.rawY.toInt()
                    val deltaY = actualRawY - lastReadedY
                    val positionResult = scrollLayout.translationY + deltaY

                    if (positionResult.toInt() in topMargin..bottomMargin) {

                        direction = if (deltaY >= 0) {
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
                        lastReadedY = actualRawY
                        lastRootY = scrollLayout.translationY.toInt()
                        CardsManager.setFrontCardPosition(lastRootY)
                    }

                    //setup velocity tracker
                    velocityTracker?.apply {
                        val pointerId: Int = event.getPointerId(event.actionIndex)
                        addMovement(event)
                        computeCurrentVelocity(1000)

                        //get the last velocity also taking into account the x param = (Vyˆ2 + Vxˆ2)ˆ(1/2)
                        val xV = this.getXVelocity(pointerId).pow(2)
                        val yV = this.getYVelocity(pointerId).pow(2)
                        lastVelocity = sqrt((xV + yV).toDouble()).toFloat()
                    }
                }
                MotionEvent.ACTION_UP -> {
                    //manage motion by velocity
                    if (abs(lastVelocity) > 1800) {
                        //compare actualY with lastY to get the direciton of the drag
                        if (initialY < event.rawY.toInt()) {
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
        autoDragCardLayout(lastRootY.toFloat(), bottomMargin.toFloat())
        //set lastY
        lastReadedY = bottomMargin
        //notify on card opened
        CardsManager.onFrontCardOpened()
        //change open status
        CardsManager.changeBackOpenStatus()
    }

    //function called when card go up
    private fun goUp() {

        Log.d("CONTROLLER", "Front controller -> goUp : $lastRootY -> $topMargin")

        //autodrag to go up
        autoDragCardLayout(lastRootY.toFloat(), topMargin.toFloat())
        //set lastY
        lastReadedY = topMargin
        //notify con card closed
        CardsManager.onFrontCardClosed()
        //notify on card closed
        CardsManager.changeBackOpenStatus()
    }

    //region private methods
    private fun autoDragCardLayout(from: Float, to: Float) {
        val va = ValueAnimator.ofFloat(from, to)
        val mDuration = 250 //in millis

        va.duration = mDuration.toLong()
        va.addUpdateListener { animation ->
            val value = (animation.animatedValue as Float).toInt()
            CardsManager.setFrontCardPosition(value)
            scrollLayout.translationY = value.toFloat()
        }
        va.start()
    }
    //endregion
}