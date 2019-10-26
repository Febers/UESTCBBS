package com.febers.uestc_bbs.module.theme

import android.app.Activity
import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.graphics.Color
import android.view.View
import android.widget.CheckBox
import androidx.appcompat.widget.SwitchCompat
import com.aurelhubert.ahbottomnavigation.AHBottomNavigation
import com.febers.uestc_bbs.utils.ColorUtils
import com.febers.uestc_bbs.utils.PreferenceUtils
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import java.lang.ref.WeakReference
import android.graphics.PorterDuff
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.widget.CompoundButtonCompat
import com.febers.uestc_bbs.MyApp
import com.febers.uestc_bbs.R
import com.febers.uestc_bbs.lib.header.MaterialHeader
import com.febers.uestc_bbs.utils.log


const val COLOR_PRIMARY_DARK = "color_primary_dark"
const val COLOR_PRIMARY = "my_color_primary"
const val COLOR_ACCENT = "my_color_accent"
const val NIGHT_MODE = "night_mode"

object ThemeManager {

    private val blueIntValue = Color.parseColor("#2196f3")

    private var colorPrimary = Color.WHITE
    private var colorAccent = blueIntValue
    private var colorTextFirst = Color.BLACK
    private var colorTextSecond = Color.parseColor("#888888")
    private var colorBackgroundFirst = Color.WHITE

    private var nightMode = false

    private val themeChangeSubscribers: MutableList<WeakReference<View>> = ArrayList()

    fun init(context: Context) {
        val mode = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        val nightModeValue by PreferenceUtils(context, NIGHT_MODE, false)

        if (mode == Configuration.UI_MODE_NIGHT_YES || nightModeValue) {  //系统或者app为暗黑模式，自动变换为夜间模式
            log { "暗黑模式" }
            onNightTheme(context)
        } else {
            log { "明亮模式" }
            onDayTheme(context)
        }
        val colorAccentValue by PreferenceUtils(context, COLOR_ACCENT, colorAccent)
        colorAccent = colorAccentValue
    }

    fun dayAndNightThemeChange(activity: Activity?) {
        activity ?: return
        if (nightMode) {    //当前为夜间模式
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            onDayTheme(activity)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            onNightTheme(activity)
        }
        activity.window.setWindowAnimations(R.style.WindowAnimationFadeInOut)
        activity.recreate()
    }

    private fun onDayTheme(context: Context) {
        colorPrimary = Color.WHITE
        colorTextFirst = Color.BLACK
        colorTextSecond = Color.parseColor("#888888")
        colorBackgroundFirst = Color.WHITE

        var nightModeValue by PreferenceUtils(context, NIGHT_MODE, false)
        nightModeValue = false
        nightMode = false
    }

    private fun onNightTheme(context: Context) {
        colorPrimary = Color.parseColor("#363636")
        colorTextFirst = Color.parseColor("#ffffff")
        colorTextSecond = Color.parseColor("#9e9e9e")
        colorBackgroundFirst = Color.parseColor("#363636")

        var nightModeValue by PreferenceUtils(context, NIGHT_MODE, true)
        nightModeValue = true
        nightMode = true
    }

    fun setTheme(context: Context, color: Int) {
        colorAccent = color
        var colorAccentValue by PreferenceUtils(context, COLOR_ACCENT, colorAccent)
        colorAccentValue = colorAccent

        val iterable = themeChangeSubscribers.iterator()
        while (iterable.hasNext()) {
            val view = iterable.next().get()
            view ?: continue
            renderViewColor(view)
        }
    }

    fun colorPrimary(): Int = colorPrimary

    fun colorAccent(): Int = colorAccent

    fun colorBackground(): Int = colorBackgroundFirst

    fun isNightTheme(): Boolean = nightMode

    fun colorTextFirst(): Int = colorTextFirst

    fun colorRefreshText(): Int = when {
        ColorUtils.isLightColor(colorAccent) -> Color.GRAY
        else -> Color.WHITE
    }

    fun viewInitAndSubscribe(view: View?) {
        view ?: return
        renderViewColor(view)
        themeChangeSubscribers.add(WeakReference(view))
    }

    private fun renderViewColor(view: View) {
        if (view is FloatingActionButton) {
            view.backgroundTintList = ColorStateList.valueOf(colorAccent)
        }
        if (view is TabLayout) {
            view.setSelectedTabIndicatorColor(colorAccent)
        }
        if (view is SwitchCompat) {
            val thumbStates = ColorStateList(arrayOf(intArrayOf(-android.R.attr.state_enabled), intArrayOf(android.R.attr.state_checked), intArrayOf()),
                    intArrayOf(colorAccent, colorAccent, Color.GRAY))
            view.thumbTintList = thumbStates
//            if (Build.VERSION.SDK_INT >= 24) {
//                val trackStates = ColorStateList(arrayOf(intArrayOf(-android.R.attr.state_enabled), intArrayOf()),
//                        intArrayOf(Color.GRAY, Color.LTGRAY)
//                )
//                view.trackTintList = trackStates
//            }
        }
        if (view is CheckBox) {
            CompoundButtonCompat.setButtonTintList(view, ColorStateList.valueOf(colorAccent))
        }
        if (view is AHBottomNavigation) {
            view.accentColor = colorAccent
            view.defaultBackgroundColor = colorBackground()
        }
        if (view is SmartRefreshLayout) {
            view.setPrimaryColors(colorAccent, colorRefreshText())
            if (view.refreshHeader is MaterialHeader) {
                (view.refreshHeader as MaterialHeader).setColorSchemeColors(colorAccent)
            }
        }
    }

}