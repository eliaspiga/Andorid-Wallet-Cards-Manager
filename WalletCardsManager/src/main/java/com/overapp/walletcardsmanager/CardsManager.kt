package com.overapp.walletcardsmanager

import android.util.Log

object CardsManager {

    const val tagLog = "CARDS_MANAGER"

    //region controll system
    private lateinit var mainControll: MainInterfaceListener
    private lateinit var middleControll: MiddleInterfaceListener
    private lateinit var backControll: BackInterfaceListener
    //endregion

    //region borders
    private var backBorderHeight: Int = 0
    private var middleCardBorderHeight: Int = 0
    private var frontCardBorderHeight: Int = 0
    //endregion

    //region shadows
    private var middleShadow: Int = 0
    private var frontShadow: Int = 0
    //endregion

    //region height
    private var rootHeight: Int = 0
    //endregion

    //region sigital card variables
    private var middleCardStartMargin: Int = 0
    private var middleCardTopLimitSwitch: Int = 0
    private var middleCardLowerLimitSwitch: Int = 0
    private var middleCardActualPosition: Int = 0
    //endregion

    //region frontCard variables
    private var frontCardStartMargin: Int = 0
    private var frontCardTopLimitSwitch: Int = 0
    private var frontCardLowerLimitSwitch: Int = 0
    private var frontCardActualPosition: Int = 0
    //endregion

    //region middle card - front card open ratio variation
    private val middleFrontMaxDistance: Int
        get() = (frontCardLowerLimitSwitch - middleCardTopLimitSwitch)

    private val middleFrontActualDistance: Int
        get() = (frontCardActualPosition - middleCardActualPosition)

    private val actualOpenRatio: Float
        get() = (((middleFrontActualDistance * 100).toFloat() / middleFrontMaxDistance.toFloat()) / 100)
    //endregion

    //region methods set control system
    fun registerMainControll(controll: MainInterfaceListener) {
        mainControll = controll
    }

    fun registerMiddleControll(controll: MiddleInterfaceListener) {
        middleControll = controll
    }

    fun registerBackControll(controll: BackInterfaceListener) {
        backControll = controll
    }
    //endregion


    //region methods set limit switch
    fun setBackBorder(value: Int) {
        backBorderHeight = value.also { Log.d(tagLog, "BackBorder : $it") }
        initialSetup()
    }

    fun setMiddleCardBorder(value: Int) {
        middleCardBorderHeight =
            value.also { Log.d(tagLog, "MiddleBorder : $it") }
        initialSetup()
    }

    fun setFrontCardBorder(value: Int) {
        frontCardBorderHeight =
            value.also { Log.d(tagLog, "FrontBorder : $it") }
        initialSetup()
    }

    fun setRootHeight(value: Int) {
        rootHeight = value.also { Log.d(tagLog, "RootHeight : $it") }
        initialSetup()
    }

    fun setMiddleShadowHeight(value: Int) {
        middleShadow = value.also { Log.d(tagLog, "Middle Shadow : $it") }
        initialSetup()
    }

    fun setFrontShadowHeight(value: Int) {
        frontShadow = value.also { Log.d(tagLog, "Front Shadow : $it") }
        initialSetup()
    }
    //endregion


    //region layout interface listener
    interface MainInterfaceListener {

        //frontCard initial setup
        fun initialSetupFrontCard(
            frontCardStartMargin: Int,
            frontCardTopLimitSwitch: Int,
            frontCardLowerLimitSwitch: Int,
        )

        //frontCard motion
        fun frontCardGoAnimated(from: Int, to: Int)
        fun frontCardGo(to: Int)

        //middle card initial setup
        fun initialSetupMiddleCard(
            middleCardStartMargin: Int,
            middleCardTopLimitSwitch: Int,
            middleCardLowerLimitSwitch: Int,
        )

        //middle card motion
        fun middleCardGoAnimated(from: Int, to: Int)
        fun middleCardGo(to: Int)

        fun finishedSetup()
    }

    interface BackInterfaceListener {
        fun initialSetup(bottomMargin: Int)
        fun onOpenStatusChanged(openStatus: Boolean)
    }

    interface MiddleInterfaceListener {
        fun onCardsChangeOpenRatio(ratio: Float)
    }
    //endregion


    //region update position methods
    fun setMiddleCardPosition(value: Int) {
        middleCardActualPosition = value
        updateOpenRatioValue()
    }

    fun setFrontCardPosition(value: Int) {
        frontCardActualPosition = value
        updateOpenRatioValue()
    }
    //endregion


    //region movement methods
    fun onMiddleCardDown(delta: Int) {
        val positionResult = frontCardActualPosition + delta
        if (positionResult <= frontCardLowerLimitSwitch) {
            mainControll.frontCardGo(positionResult)
            frontCardActualPosition = positionResult
        }
    }

    fun onMiddleCardUp(delta: Int) {
        if (frontCardActualPosition != frontCardLowerLimitSwitch) {
            val positionResult = frontCardActualPosition + delta
            if (positionResult >= frontCardTopLimitSwitch) {
                mainControll.frontCardGo(positionResult)
                frontCardActualPosition = positionResult
            }
        }
    }

    fun onMiddleCardOpened() {
        middleCardActualPosition = middleCardLowerLimitSwitch
        mainControll.frontCardGoAnimated(frontCardActualPosition, frontCardLowerLimitSwitch)
        frontCardActualPosition = frontCardLowerLimitSwitch
    }

    fun onMiddleCardClosed() {
        if (frontCardActualPosition != frontCardLowerLimitSwitch) {
            mainControll.frontCardGoAnimated(frontCardActualPosition, frontCardTopLimitSwitch)
            frontCardActualPosition = frontCardTopLimitSwitch
        }
    }

    fun onMiddleCardDirectionRevertToUp() {
        if (frontCardActualPosition + frontCardStartMargin + frontShadow > rootHeight / 2) {
            mainControll.frontCardGoAnimated(frontCardActualPosition, frontCardLowerLimitSwitch)
            frontCardActualPosition = frontCardLowerLimitSwitch
        }
    }


    fun onFrontCardDown(delta: Int) {
        if (middleCardActualPosition != middleCardTopLimitSwitch) {
            val positionResult = middleCardActualPosition + delta
            if (positionResult <= middleCardLowerLimitSwitch) {
                mainControll.middleCardGo(positionResult)
                middleCardActualPosition = positionResult
            }
        }
    }

    fun onFrontCardUp(delta: Int) {
        val positionResult = middleCardActualPosition + delta
        if (positionResult >= middleCardTopLimitSwitch) {
            mainControll.middleCardGo(positionResult)
            middleCardActualPosition = positionResult
        }
    }

    fun onFrontCardOpened() {
        if (middleCardActualPosition != middleCardTopLimitSwitch) {
            mainControll.middleCardGoAnimated(
                middleCardActualPosition,
                middleCardLowerLimitSwitch
            )
            middleCardActualPosition = middleCardLowerLimitSwitch
        }
    }

    fun onFrontCardClosed() {
        mainControll.middleCardGoAnimated(
            middleCardActualPosition,
            middleCardTopLimitSwitch
        )
        middleCardActualPosition = middleCardTopLimitSwitch
    }

    fun onFrontCardOppositeDirectionToDown() {
        if (middleCardActualPosition + middleCardStartMargin + middleShadow < rootHeight / 2) {
            mainControll.middleCardGoAnimated(middleCardActualPosition, middleCardTopLimitSwitch)
            middleCardActualPosition = middleCardTopLimitSwitch
        }
    }
    //endregion


    //region private methods
    private fun initialSetup() {

        if (rootHeight != 0 || backBorderHeight != 0 || middleCardBorderHeight != 0
            || frontCardBorderHeight != 0 || middleShadow != 0 || frontShadow != 0
        ) return

        //calcs for the upper margins
        middleCardStartMargin = backBorderHeight - middleShadow
        frontCardStartMargin = middleCardStartMargin + middleCardBorderHeight

        //calcs for upper limit switches
        middleCardTopLimitSwitch = 0
        frontCardTopLimitSwitch = 0

        //calcs for the lower limit switches
        frontCardLowerLimitSwitch =
            rootHeight - frontCardStartMargin - frontCardBorderHeight - frontShadow
        middleCardLowerLimitSwitch = frontCardLowerLimitSwitch

        //initial setups for mainFragment
        mainControll.initialSetupFrontCard(
            frontCardStartMargin,
            frontCardTopLimitSwitch,
            frontCardLowerLimitSwitch,
        )
        mainControll.initialSetupMiddleCard(
            middleCardStartMargin,
            middleCardTopLimitSwitch,
            middleCardLowerLimitSwitch,
        )

        //initial setups for Back
        backControll.initialSetup(frontCardBorderHeight + middleCardBorderHeight)

        //notify finisched setup
        mainControll.finishedSetup()
    }
    //region


    //region Back
    fun changeBackOpenStatus() {
        backControll.onOpenStatusChanged(
            middleCardActualPosition == middleCardLowerLimitSwitch
                    && frontCardActualPosition == frontCardLowerLimitSwitch
        )
    }
    //endregion


    //region methods event - command
    fun onBackHeaderClicked(openStatus: Boolean) {
        //send the positions
        if (openStatus) {
            //go down
            //send the go signal at the cards
            mainControll.frontCardGoAnimated(frontCardActualPosition, frontCardLowerLimitSwitch)
            mainControll.middleCardGoAnimated(middleCardActualPosition, middleCardLowerLimitSwitch)
            //set the new position of the cards
            frontCardActualPosition = frontCardLowerLimitSwitch
            middleCardActualPosition = middleCardLowerLimitSwitch
        } else {
            //go up
            //send the go signal at the cards
            mainControll.middleCardGoAnimated(middleCardActualPosition, middleCardTopLimitSwitch)
            mainControll.frontCardGoAnimated(frontCardActualPosition, frontCardTopLimitSwitch)
            //set the new position of the cards
            middleCardActualPosition = middleCardTopLimitSwitch
            frontCardActualPosition = frontCardTopLimitSwitch
        }
    }

    fun closeAll() {
        //send the go signal at the cards
        mainControll.frontCardGoAnimated(frontCardActualPosition, frontCardTopLimitSwitch)
        mainControll.middleCardGoAnimated(middleCardActualPosition, middleCardTopLimitSwitch)
        //set the new position of the cards
        frontCardActualPosition = frontCardTopLimitSwitch
        middleCardActualPosition = middleCardTopLimitSwitch
    }
    //endregion


    //region private methods
    private fun updateOpenRatioValue() {
        middleControll.onCardsChangeOpenRatio(actualOpenRatio)
    }
    //endregion
}