package com.febers.uestc_bbs.module.user.view

import androidx.annotation.UiThread
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.appcompat.widget.Toolbar
import com.febers.uestc_bbs.R
import com.febers.uestc_bbs.base.*
import com.febers.uestc_bbs.entity.UserPostBean
import com.febers.uestc_bbs.module.user.contract.UserContract
import com.febers.uestc_bbs.module.user.presenter.UserPresenterImpl
import com.febers.uestc_bbs.module.context.ClickContext
import com.febers.uestc_bbs.view.adapter.UserPostAdapter
import com.febers.uestc_bbs.utils.finishFail
import com.febers.uestc_bbs.utils.finishSuccess
import com.febers.uestc_bbs.utils.initAttrAndBehavior
import kotlinx.android.synthetic.main.activity_user_post.*
import kotlinx.android.synthetic.main.layout_toolbar_common.*

class UserPostActivity: BaseActivity(), UserContract.View {

    private val userPostList: MutableList<UserPostBean.ListBean> = ArrayList()
    private lateinit var userPListPresenter: UserContract.Presenter
    private lateinit var userPListAdapter: UserPostAdapter
    private var page = 1
    private var uid: Int = 0
    private var type = USER_START_POST

    override fun setToolbar(): Toolbar? = toolbar_common


    override fun setView(): Int {
        uid = intent.getIntExtra(USER_ID, 0)
        type = intent.getIntExtra(USER_POST_TYPE, 0)
        return R.layout.activity_user_post
    }

    override fun initView() {
        setToolbarTitle()
        userPListPresenter = UserPresenterImpl(this)
        userPListAdapter = UserPostAdapter(this@UserPostActivity, userPostList).apply {
            setOnItemClickListener { viewHolder, listBean, i ->
                ClickContext.clickToPostDetail(context, listBean.topic_id, listBean.title, listBean.user_nick_name) }
            setOnItemChildClickListener(R.id.image_view_item_user_post_avatar) {
                viewHolder, listBean, i ->  ClickContext.clickToUserDetail(this@UserPostActivity, listBean.user_id)
            }
            setEmptyView(getEmptyViewForRecyclerView(recyclerview_user_post))
        }
        recyclerview_user_post.apply {
            adapter = userPListAdapter
            layoutManager = LinearLayoutManager(context)
            //addItemDecoration(DividerItemDecoration(ctx, LinearLayoutManager.VERTICAL))
        }
        refresh_layout_user_post.apply {
            initAttrAndBehavior()
            setOnRefreshListener {
                page = 1
                getUserPost()
            }
            setOnLoadMoreListener {
                ++page
                getUserPost()
            }
        }
    }

    private fun getUserPost() {
        refresh_layout_user_post.setNoMoreData(false)
        userPListPresenter.userPostRequest(uid, type, page)
    }

    @UiThread
    override fun showUserPost(event: BaseEvent<UserPostBean>) {
        if (event.code == BaseCode.LOCAL) {
            runOnUiThread {
                userPListAdapter.setNewData(event.data.list)
            }
            return
        }
        refresh_layout_user_post?.finishSuccess()
        if (page == 1) {
            userPListAdapter.setNewData(event.data.list)
            return
        }
        if (event.code == BaseCode.SUCCESS_END) {
            refresh_layout_user_post?.finishLoadMoreWithNoMoreData()
        }
        userPListAdapter.setLoadMoreData(event.data.list)
    }

    private fun setToolbarTitle() {
        if (type == USER_START_POST) {
            toolbar_common.title = "发表"
        }
        if (type == USER_REPLY_POST) {
            toolbar_common.title = "回复"
        }
        if (type == USER_FAV_POST) {
            toolbar_common.title = "收藏"
        }
    }

    override fun showError(msg: String) {
        runOnUiThread {
            showHint(msg)
            refresh_layout_user_post?.finishFail()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        userPostList.clear()
    }
}