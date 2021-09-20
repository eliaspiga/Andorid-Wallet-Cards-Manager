package com.overapp.walletcardsmanager.controllers

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import com.overapp.walletcardsmanager.CardsManager
import com.overapp.walletcardsmanager.values.Direction
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

class MiddleController(val scrollLayout: View, val topMargin: Float, val bottomMargin: Float) {

    //region drag variables
    private var initialY: Float = 0f
    private var lastY:Float = 0f
    private var lastCardY:Float = 0f
    private var lastVelocity:Float = 0f
    private var direction = Direction.DOWN

    private var velocityTracker: VelocityTracker? = null
    private var middleCardTouchListener: View.OnTouchListener
    //endregion

    init {
        middleCardTouchListener = setupTouchListener()
        scrollLayout.setOnTouchListener(middleCardTouchListener)
    }

    //region public methods
    fun goAnimated(from: Float, to: Float) {
        autoDrag(from, to)
    }

    fun go(to: Float) {
        scrollLayout.translationY = to
    }
    //endregion

    //region private methods
    //setup touch listener
    private fun setupTouchListener() = object : View.OnTouchListener {
        @SuppressLint("ClickableViewAccessibility")
        override fun onTouch(view: View, event: MotionEvent): Boolean {

            when (event.action) {

                MotionEvent.ACTION_DOWN -> {

                    lastY = event.rawY
                    initialY = lastY

                    if (velocityTracker == null)
                        velocityTracker = VelocityTracker.obtain()
                    else
                        velocityTracker?.clear()
                }

                MotionEvent.ACTION_MOVE -> {

                    val actualRawY : Float = event.rawY
                    val deltaY : Float = actualRawY - lastY
                    lastY = actualRawY
                    val positionResult : Float = scrollLayout.translationY + deltaY

                    if (positionResult in topMargin..bottomMargin) {

                        direction = if (deltaY >= 0f) {
                            //going down
                            CardsManager.onMiddleCardDown(deltaY)
                            Direction.DOWN
                        } else {
                            //going up

                            //if I was going down call the opposite direction func
                            if (direction == Direction.DOWN)
                                CardsManager.onMiddleCardDirectionRevertToUp()

                            CardsManager.onMiddleCardUp(deltaY)
                            Direction.UP
                        }

                        scrollLayout.translationY += deltaY
                        lastCardY = scrollLayout.translationY
                        CardsManager.setMiddleCardPosition(lastCardY)
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
                    if (abs(lastVelocity) > 1800f) {
                        //compare actualY with lastY to get the direciton of the drag
                        if (initialY < event.rawY) {
                            goDown()
                        } else {
                            goUp()
                        }
                    } else {
                        //manage motion by positions
                        if (scrollLayout.translationY > (bottomMargin + topMargin) / 2) {
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
                    velocityTracker?.recycle()
                    velocityTracker = null
                }
            }
            return true
        }
    }


    //function called when the card go down
    private fun goDown() {
        //autodrag to go down
        autoDrag(lastCardY, bottomMargin)
        //update lastY
        lastY = bottomMargin
        //notify o MiddleCard opened
        CardsManager.onMiddleCardOpened()
        //change open status
        CardsManager.changeBackOpenStatus()
    }

    //function called when the card go up
    private fun goUp() {
        //autodrag to go up
        autoDrag(lastCardY, topMargin)
        //set the lastY
        lastY = topMargin
        //notify on MiddleCard close
        CardsManager.onMiddleCardClosed()
        //change open status
        CardsManager.changeBackOpenStatus()
    }

    private fun autoDrag(from: Float, to: Float) {
        val va = ValueAnimator.ofFloat(from, to)
        val mDuration = 250 //in millis

        va.duration = mDuration.toLong()
        va.addUpdateListener { animation ->
            val value = (animation.animatedValue as Float)
            CardsManager.setMiddleCardPosition(value)
            scrollLayout.translationY = value
        }
        va.start()
    }
    //endregion
}