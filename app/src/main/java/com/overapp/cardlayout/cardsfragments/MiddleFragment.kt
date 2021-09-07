package com.overapp.cardlayout.cardsfragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import com.overapp.walletcardsmanager.CardsManager
import com.overapp.cardlayout.databinding.LayoutFragmentMiddleBinding

class MiddleFragment : Fragment(), CardsManager.MiddleInterfaceListener {

    //region variables
    private lateinit var binding: LayoutFragmentMiddleBinding

    private var backgroundViewHeight : Int = 0
    private var horizontalMarginRefer : Int = 0
    private var minContentFontSize: Int = 20
    private var maxContentFontSize: Int = 80
    //endregion

    //region lifecycle
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LayoutFragmentMiddleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        CardsManager.registerMiddleControll(this)

        binding.middleFragmentContainer.post {
            CardsManager.setMiddleCardBorder(binding.middleFragmentHeaderContent.height)
        }

        binding.middleFragmentBackground.post {
            backgroundViewHeight = binding.middleFragmentBackground.height
        }

        binding.middleHorizontalMarginRefer.post {
            horizontalMarginRefer = binding.middleHorizontalMarginRefer.width
        }

    }

    override fun onCardsChangeOpenRatio(ratio: Float) {

        //calculates the font size with normalized values
        val fontSize: Float = ((maxContentFontSize - minContentFontSize) * ratio) + minContentFontSize

        //calculates margins
        val topMargin: Int = (backgroundViewHeight * ratio).toInt()
        val horizontalMargin: Int = (horizontalMarginRefer * ratio).toInt()

        //assign margins params to the view
        val contentParams =
            binding.dragCardContent.layoutParams as FrameLayout.LayoutParams
        contentParams.setMargins(horizontalMargin, topMargin, horizontalMargin, contentParams.bottomMargin)
        binding.dragCardContent.layoutParams = contentParams

        //update the font size
        binding.middleFragmentHeaderContent.textSize = fontSize

    }
    //endregion
}