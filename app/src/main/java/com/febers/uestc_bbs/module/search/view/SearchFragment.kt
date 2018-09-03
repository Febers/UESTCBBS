package com.febers.uestc_bbs.module.search.view

import android.os.Bundle
import android.support.v7.widget.Toolbar
import com.febers.uestc_bbs.R
import com.febers.uestc_bbs.base.FID
import com.febers.uestc_bbs.base.BaseSwipeFragment
import kotlinx.android.synthetic.main.fragment_search.*

class SearchFragment: BaseSwipeFragment() {

    override fun setToolbar(): Toolbar? {
        return toolbar_search
    }

    override fun setContentView(): Int {
        return R.layout.fragment_search
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String) =
                SearchFragment().apply {
                    arguments = Bundle().apply {
                        putString(FID, param1)
                    }
                }
    }
}