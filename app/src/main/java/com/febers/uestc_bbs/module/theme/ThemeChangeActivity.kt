package com.febers.uestc_bbs.module.theme

import android.content.Intent
import com.febers.uestc_bbs.R
import com.febers.uestc_bbs.base.BaseActivity
import com.febers.uestc_bbs.base.DAY_NIGHT_THEME_CHANGE
import com.febers.uestc_bbs.home.SplashActivity

class ThemeChangeActivity: BaseActivity() {

    override fun enableThemeHelper(): Boolean = false

    override fun setView(): Int = R.layout.activity_theme_change

    override fun initView() {
        startActivity(Intent(mContext, SplashActivity::class.java).apply {
            putExtra(DAY_NIGHT_THEME_CHANGE, true)
        })
        overridePendingTransition(0, 0)
    }
}