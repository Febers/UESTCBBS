package com.febers.uestc_bbs.module.post.view.content

import android.graphics.Color
import android.view.View
import android.webkit.WebView
import com.febers.uestc_bbs.entity.PostDetailBean
import com.febers.uestc_bbs.http.WebViewConfiguration
import com.febers.uestc_bbs.module.theme.ThemeManager
import com.febers.uestc_bbs.utils.encodeSpaces
import com.febers.uestc_bbs.utils.log
import org.jetbrains.anko.collections.forEachWithIndex

/**
 * const val CONTENT_TYPE_TEXT = 0
 * const val CONTENT_TYPE_IMG = 1
 * const val CONTENT_TYPE_AUDIO = 3
 * const val CONTENT_TYPE_URL = 4
 * const val CONTENT_TYPE_FILE = 5
 */
object ContentTransfer {

    private val backgroundColor = if (ThemeManager.isNightTheme()) "#00363636" else "#00ffffff"
    private val textColor = if (ThemeManager.isNightTheme()) "white" else "black"

    fun transfer(webView: WebView, contents: List<PostDetailBean.ContentBean>) {
        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        webView.setBackgroundColor(Color.parseColor(backgroundColor))
        WebViewConfiguration.Configuration(webView.context, webView)
                .setJavaScriptEnabled(true)
                .setSupportZoom(false)
                .configure()
        //主贴图文视图的绘制
        webView.loadDataWithBaseURL(null, json2Html(contents), "text/html; charset=UTF-8", "UTF-8", null)
    }

    fun json2Html(contents: List<PostDetailBean.ContentBean>): String {
        val sb = StringBuilder()
        contents.forEachWithIndex { index, content ->
            when(content.type) {
                CONTENT_TYPE_TEXT -> {
                    sb.append(""" <font color="$textColor" style="word-break:break-all">${emotionTrans2Url(content.infor).encodeSpaces()} </font>""")
                }
                CONTENT_TYPE_IMG -> {
                    sb.append("""<p style="text-align:center;"><img style="max-width:90%;height:auto" src="${content.infor}"/></p>""")
                }
                CONTENT_TYPE_URL -> {
                    if (!content.url!!.matchImageUrl()) {
                        sb.append("""<a href="${content.url}" style="word-break:break-all">${content.infor}</a>""")
                    }
                }
                CONTENT_TYPE_AUDIO -> {
                    sb.append("""<embed height="100" width="100" src="${content.url}" />""")
                }
                CONTENT_TYPE_FILE -> {
                    sb.append("""<a href="${content.originalInfo}" download="${content.originalInfo.simplifyDownloadUrl()}">${content.infor}</a>""")
                }
            }
        }
        log { "content html: ${sb.toString()}" }
        return sb.toString()
    }



    private fun String?.simplifyDownloadUrl(): String {
        if (this.isNullOrEmpty()) return ""
        return "1"
    }

    /**
     * 将content.infor中的表情gif图片变成超链接，
     * 然后交给{@link #ImageTextHelper.setImageText(TextView tv, String html)} 处理
     * raw比如:
     * ...[mobcent_phiz=http://bbs.uestc.edu.cn/static/image/smiley/first/1.gif]...
     * 转换成:
     * <img src="http://bbs.uestc.edu.cn/static/image/smiley/first/1.gif">
     */
    private fun emotionTrans2Url(raw: String?, lastBegin: Int = 0): String{
        if (raw == null) {
            return ""
        }
        val begin: Int
        var newContent: String = raw
        try {
            begin = raw.indexOf("""[mobcent_phiz=""", lastBegin)
            if (begin == -1) {
                return newContent
            }
            val end = raw.indexOf("""]""", begin)
            val rawFormatString = raw.substring(begin, end+1) //需要包括]
            val rawUrlString = raw.substring(begin+14, end) //不需要包括]
            val imgString = """<img src="$rawUrlString">"""
            newContent = raw.replace(rawFormatString, imgString)
        } catch (e:Exception) {
            return newContent
        }
        return emotionTrans2Url(newContent, begin)
    }

    private fun String.matchImageUrl(): Boolean = endsWith(".jpg") || endsWith(".png") ||
            endsWith(".jpeg") || endsWith(".bmp")
            || endsWith(".JPG") || endsWith(".PNG") ||
            endsWith(".JPEG") || endsWith(".BMP") ||
            endsWith(".GIF") || endsWith(".gif")
}