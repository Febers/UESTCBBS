package com.febers.uestc_bbs.view.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.View
import android.widget.CheckBox
import androidx.core.widget.CompoundButtonCompat
import com.febers.uestc_bbs.R
import com.febers.uestc_bbs.entity.SettingItemBean
import com.febers.uestc_bbs.lib.baseAdapter.ViewHolder
import com.febers.uestc_bbs.lib.baseAdapter.base.CommonBaseAdapter
import com.febers.uestc_bbs.utils.colorAccent

class SettingAdapter(val context: Context, data: List<SettingItemBean>):
        CommonBaseAdapter<SettingItemBean>(context, data, false) {

    override fun getItemLayoutId(): Int {
        return R.layout.item_layout_setting
    }

    override fun convert(p0: ViewHolder?, p1: SettingItemBean?, p2: Int) {
        p1 ?: return
        p0?.setText(R.id.text_view_item_setting_first, p1.title)
        p0?.setText(R.id.text_view_item_setting_second, p1.tip)
        if (p1.showCheck) {
            (p0?.getView(R.id.check_box_item_setting) as CheckBox).apply {
                visibility = View.VISIBLE
                isChecked = p1.checked
                CompoundButtonCompat.setButtonTintList(this,
                        ColorStateList(arrayOf(intArrayOf(android.R.attr.state_checked), intArrayOf()), intArrayOf(colorAccent(), Color.GRAY)))
            }
        } else {
            (p0?.getView(R.id.check_box_item_setting) as CheckBox).apply {
                visibility = View.GONE
                isChecked = p1.checked
            }
        }
    }
}