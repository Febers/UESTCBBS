package com.febers.uestc_bbs.module.image

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Environment
import com.febers.uestc_bbs.io.DownloadHelper
import com.febers.uestc_bbs.io.FileHelper.appImageDir
import com.febers.uestc_bbs.io.tryClose
import com.febers.uestc_bbs.utils.log
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.CountDownLatch

object ImageHelper {

    private var fileForShare: Boolean = false
    private var imgFile: File? = null

    /**
     * 保存image
     *
     * @param bitmap
     */
    fun saveImage(context: Context, bitmap: Bitmap, forShare: Boolean): Uri? {
        fileForShare = forShare
        if (Environment.getExternalStorageState() != Environment.MEDIA_MOUNTED) { //内存未挂载
            return null
        }
        try {
            val dir: File = File(appImageDir)
            if (!dir.exists()) {
                if (!dir.mkdirs()) {
                    throw IOException()
                }
            }
            imgFile = File("$appImageDir${getImageFileName()}.png")
            val fos: FileOutputStream = FileOutputStream(imgFile)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
            fos.flush()
            fos.close()

            insertImageToGallery(context)
            return Uri.parse(imgFile!!.absolutePath)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    /**
     * 保存gif格式的图片
     *
     * @deprecated 保存之后无法读取为gif
     */
    fun saveGif(context: Context, gifByte: ByteArray?, forShare: Boolean): Uri? {
        fileForShare = forShare
        if (Environment.getExternalStorageState() != Environment.MEDIA_MOUNTED) { //内存未挂载
            return null
        }
        if (gifByte == null) {
            return null
        }
        try {
            val dir: File = File(appImageDir)
            if (!dir.exists()) {
                if (!dir.mkdirs()) {
                    throw IOException()
                }
            }
            imgFile = File("$appImageDir${getImageFileName()}.gif")
            val fos: FileOutputStream = FileOutputStream(imgFile)
            fos.write(gifByte, 0, gifByte.size)
            fos.flush()
            fos.tryClose()

            insertImageToGallery(context)
            return Uri.parse(imgFile!!.absolutePath)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    /**
     * 保存gif图片，原来的保存之后无法读取为gif
     * 典型的头像链接为:http://bbs.uestc.edu.cn/uc_server/avatar.php?uid=171264&size=big
     * 由于调用的DownloadHelper的download方法使用了回调接口，无法在回调方法直接返回，使用CountDownLatch
     * 使并发变成阻塞，相关知识Google
     *
     * @param context
     * @param gifUrl
     * @param forShare
     */
    fun saveGif(context: Context, gifUrl: String?, forShare: Boolean): Uri? {
        fileForShare = forShare
        if (Environment.getExternalStorageState() != Environment.MEDIA_MOUNTED) { //内存未挂载
            return null
        }
        if (gifUrl == null) {
            return null
        }
        val countDownLatch: CountDownLatch = CountDownLatch(1)
        try {
            DownloadHelper().download(url = gifUrl, fileName = getImageFileName() + ".gif", filePath = appImageDir,
                    listener = object : DownloadHelper.OnDownloadListener {
                        override fun onDownloadSuccess(file: File) {
                            imgFile = file
                            countDownLatch.countDown()
                        }

                        override fun onDownloading(progress: Int) {
                        }

                        override fun onDownloadFailed() {
                            countDownLatch.countDown()
                        }
                    })
            countDownLatch.await()

            insertImageToGallery(context)
            return Uri.parse(imgFile!!.absolutePath)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }

    }


    /**
     * 给图片命名，方式简单粗暴，直接用时间字符串
     */
    private fun getImageFileName(): String {
        val now: Calendar = GregorianCalendar()
        val simpleDate: SimpleDateFormat = SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault())
        return simpleDate.format(now.time)
    }

    /**
     * 保存图片之后插入到系统相册并且通知图库更新
     * 参考 三种方法，刷新 Android 的 MediaStore！让你保存的图片立即出现在相册里！ - 简书
     * https://www.jianshu.com/p/bc8b04bffddf
     *
     * @param context
     */
    private fun insertImageToGallery(context: Context) {
//        MediaStore.Images.Media.insertImage(mContext.contentResolver, bitmap, imgFile?.name, null)
//        val intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri)
//        intent.data = uri
//        mContext.sendBroadcast(intent)
        MediaScannerConnection.scanFile(context, arrayOf(imgFile!!.absolutePath), arrayOf("image/jpeg")) { path, uri -> log("save completed")}
    }

    /**
     * 在调用saveImage的函数的视图销毁之后，调用此方法
     * 删除图片文件
     */
    fun onImageViewDestroy() {
        imgFile ?: return
        if (fileForShare) {
            if (imgFile!!.exists()) {
                imgFile!!.delete()
            }
        }
    }
}