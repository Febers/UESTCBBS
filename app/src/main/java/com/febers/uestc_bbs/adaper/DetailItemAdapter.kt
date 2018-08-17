/*
 * Created by Febers at 18-8-17 上午2:33.
 * Copyright (c). All rights reserved.
 * Last modified 18-8-17 上午2:33.
 */

package com.febers.uestc_bbs.adaper

import android.content.Context
import com.febers.uestc_bbs.R
import com.febers.uestc_bbs.entity.DetailItemBean
import com.othershe.baseadapter.ViewHolder
import com.othershe.baseadapter.base.CommonBaseAdapter

class DetailItemAdapter(context: Context, data: List<DetailItemBean>, isLoadMore: Boolean):
        CommonBaseAdapter<DetailItemBean>(context, data, isLoadMore){

    override fun convert(p0: ViewHolder?, p1: DetailItemBean?, p2: Int) {
        p0?.setText(R.id.text_view_item_detail_param, p1?.itemParam)
        p0?.setText(R.id.text_view_item_detail_value, p1?.itemValue)
    }

    override fun getItemLayoutId(): Int {
        return R.layout.item_layout_detail
    }
}