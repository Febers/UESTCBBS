package com.febers.uestc_bbs.io

import android.content.Context
import com.febers.uestc_bbs.MyApp
import com.febers.uestc_bbs.base.SP_NOW_UID
import com.febers.uestc_bbs.base.SP_USER_IDS
import com.febers.uestc_bbs.base.ThreadMgr
import com.febers.uestc_bbs.entity.UserSimpleBean
import com.febers.uestc_bbs.utils.PreferenceUtils
import com.google.gson.Gson
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.lang.StringBuilder

/**
 * 当用户登录的时候，获取sp中user_uid的字符串
 * 其中的规则应该为:字符串由uid和,组成，如@123@132@321
 * 使用的时候将其变成数组
 * 同时sp应该存储当前使用的uid(now_uid)
 */
object UserManager {

    /**
     * 此方法只会在登录成功的时候调用，登录成功之后，保存User的原始资料
     * 然后添加新的uid到user_ids的字符串中，然后将uid设置为nowUId
     *
     * @param uid 用户id
     * @param userSimple 自定义的简单userBean
     */
    fun addUser(uid: Int, userSimple: UserSimpleBean) {
        addUserToFile(uid, userSimple)
        var userIds by PreferenceUtils(context(), SP_USER_IDS, "")
        /*
            ！！！从v1.1.1到1.1.2过渡时，由于无法读取之前版本的sp用户信息，默认没有任何登录状态，但是记录uid的sp读取不变
            下面的if就是防止这一过渡过程中重复添加uid的问题，该问题会造成设置界面读取出两个相同的账户
         */
        if (!userIds.contains(uid.toString())) {
            userIds = "$userIds@$uid"
        }
        setNowUid(uid)
    }

    /**
     * 删除用户，首先在user_ids清除uid
     * 如果属于当前用户，则替换当前id
     *
     * @param uid 需要删除的用户的id
     */
    fun deleteUser(uid: Int) {
        var userIds by PreferenceUtils(context(), SP_USER_IDS, "")
        userIds = userIds.replace("@$uid", "")
        ThreadMgr.io {
            deleteUserInFile(uid)
        }
        if (getNowUid() == uid) {
            if (getAllUser().isNotEmpty()) {
                setNowUid(getAllUser().last().uid)
            } else {
                setNowUid(0)
            }
        }
    }

    fun containUser(name: String): Boolean {
        val list: MutableList<String> = ArrayList()
        getAllUser().forEach {
            list.add(it.name)
        }
        return list.contains(name)
    }

    private fun deleteUserInFile(uid: Int) {
        try {
            val file = File(FileHelper.appFileDir+"/$uid")
            file.delete()
        } catch (e: Exception) {
        }
    }

    /**
     * 获取当前正在使用的User，一般由App初始化时调用
     */
    fun  getNowUser(): UserSimpleBean {
        //return getUserBySp(getNowUid())
        return getUserByFile(getNowUid())
    }

    fun getNowUid(): Int {
        val nowUid by PreferenceUtils(context(), SP_NOW_UID, 0)
        return nowUid
    }

    fun setNowUid(uid: Int) {
        var nowUid by PreferenceUtils(context(), SP_NOW_UID, 0)
        nowUid = uid
    }

    private fun addUserToFile(uid: Int, userSimple: UserSimpleBean) {
        val file: File = (FileHelper.appFileDir+"/$uid").checkFileSafely()
        val fileWriter: FileWriter = FileWriter(file)
        try {
            fileWriter.write(Gson().toJson(userSimple))
            fileWriter.flush()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            fileWriter.tryClose()
        }
    }

    private fun getUserByFile(uid: Int): UserSimpleBean {
        val file: File = (FileHelper.appFileDir+"/$uid").checkFileSafely()
        var fileReader: FileReader? = null
        try {
            fileReader = FileReader(file)
            val result = StringBuilder()
            val charArray: CharArray = CharArray(1)
            while (fileReader.read(charArray) != -1) {
                result.append(charArray)
            }
            if (result.isEmpty()) return UserSimpleBean()
             return Gson().fromJson(result.toString(), UserSimpleBean::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            fileReader?.tryClose()
        }
        return UserSimpleBean()
    }

    /**
     * 返回本机上登录的所有用户
     * 解析存储的字符串
     */
    fun getAllUser(): List<UserSimpleBean> {
        try {
            val userIds by PreferenceUtils(context(), SP_USER_IDS, "")
//            log { "userIds: $userIds" }
            if (userIds.isEmpty()) return emptyList()
            val uidList = userIds.removePrefix("@").split("@")
            val userList: MutableList<UserSimpleBean> = ArrayList(uidList.size)
            uidList.forEach {
                userList.add(getUserByFile(it.toInt()))
            }
            return userList
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return emptyList()
    }

    private fun context(): Context = MyApp.context()
}