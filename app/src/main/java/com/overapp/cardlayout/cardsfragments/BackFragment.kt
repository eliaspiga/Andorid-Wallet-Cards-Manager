package com.overapp.cardlayout.cardsfragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.overapp.walletcardsmanager.CardsManager
import com.overapp.cardlayout.databinding.LayoutFragmentBackBinding

class BackFragment : Fragment(), CardsManager.BackInterfaceListener {

    //region variables
    private var openStatus: Boolean = false

    /**
     * Binding
     */
    private lateinit var binding: LayoutFragmentBackBinding
    //endregion

    //region Lyfe Cycler Methods
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        registerHeaderManager()
        binding = LayoutFragmentBackBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backFragmentContatiner.post {
            CardsManager.setBackBorder(binding.backFragmentHeader.height)
        }

        binding.backFragmentHeader.setOnClickListener {
            onHeaderClick()
        }
    }
    //endregion

    //region controll commands
    override fun onOpenStatusChanged(openStatus: Boolean) {
        this.openStatus = openStatus
    }

    override fun initialSetup(bottomMargin: Int) {
        //do other task...
    }
    //endregion

    //region private function
    private fun onHeaderClick() {
        openStatus = !openStatus
        CardsManager.onBackHeaderClicked(openStatus)
    }

    private fun registerHeaderManager() {
        CardsManager.registerBackControll(this)
    }
    //endregion
}