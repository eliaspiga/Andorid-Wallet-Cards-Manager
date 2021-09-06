package com.overapp.cardlayout.cardsfragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.overapp.walletcardsmanager.CardsManager
import com.overapp.cardlayout.databinding.LayoutFragmentFrontBinding

class FrontFragment: Fragment(){

    //region varaibles
    private lateinit var binding: LayoutFragmentFrontBinding
    //endregion

    //region Lyfe Cycle Methods
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LayoutFragmentFrontBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //send the header height
        binding.frontFragmentContainer.post {
            CardsManager.setFrontCardBorder(binding.frontFragmentHeader.height)
        }
    }
    //endregion
}


