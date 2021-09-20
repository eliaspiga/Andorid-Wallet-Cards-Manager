package com.overapp.cardlayout

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.overapp.cardlayout.databinding.ActivityMainBinding
import com.overapp.walletcardsmanager.CardsManager
import com.overapp.walletcardsmanager.controllers.FrontController
import com.overapp.walletcardsmanager.controllers.MiddleController

class MainActivity : AppCompatActivity(), CardsManager.MainInterfaceListener {

    //region variables
    private lateinit var binding: ActivityMainBinding

    private lateinit var middleController: MiddleController
    private lateinit var frontController: FrontController
    //endregion

    //region lifecycle methods
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater, null, false)
        CardsManager.registerMainControll(this)
        setContentView(binding.root)

        binding.root.post {
            CardsManager.setRootHeight(binding.root.height)
        }

        binding.middleShadow.post {
            CardsManager.setMiddleShadowHeight(binding.middleShadow.height)
        }

        binding.frontShadow.post {
            CardsManager.setFrontShadowHeight(binding.frontShadow.height)
        }
    }
    //endregion

    //region middle card commands
    override fun initialSetupMiddleCard(
        middleCardStartMargin: Int,
        middleCardTopLimitSwitch: Float,
        middleCardLowerLimitSwitch: Float
    ) {
        //setup middle card border
        val newLayoutParams = binding.middleDragLayout.layoutParams as ConstraintLayout.LayoutParams
        newLayoutParams.setMargins(0, middleCardStartMargin, 0, 0)
        binding.middleDragLayout.layoutParams = newLayoutParams

        //setup middle card controller
        middleController = MiddleController(
            binding.middleDragLayout,
            middleCardTopLimitSwitch,
            middleCardLowerLimitSwitch
        )
    }

    override fun middleCardGoAnimated(from: Float, to: Float) {
        middleController.goAnimated(from, to)
    }

    override fun middleCardGo(to: Float) {
        middleController.go(to)
    }
    //endregion


    //region front card commands
    override fun initialSetupFrontCard(
        frontCardStartMargin: Int,
        frontCardTopLimitSwitch: Float, frontCardLowerLimitSwitch: Float
    ) {
        //setup front card border
        val newLayoutParams = binding.frontDragLayout.layoutParams as ConstraintLayout.LayoutParams
        newLayoutParams.setMargins(0, frontCardStartMargin, 0, 0)
        binding.frontDragLayout.layoutParams = newLayoutParams

        //setup front card controller
        frontController = FrontController(
            binding.frontDragLayout,
            frontCardTopLimitSwitch,
            frontCardLowerLimitSwitch
        )
    }

    override fun frontCardGoAnimated(from: Float, to: Float) {
        frontController.goAnimated(from, to)
    }

    override fun frontCardGo(to: Float) {
        frontController.go(to)
    }
    //endregion


    //region other commands
    override fun finishedSetup() {
        //do other task...
    }
    //endregion
}