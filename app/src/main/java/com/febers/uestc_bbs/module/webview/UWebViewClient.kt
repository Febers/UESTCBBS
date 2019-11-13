package com.febers.uestc_bbs.module.webview

import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.net.http.SslError
import android.webkit.*
import com.febers.uestc_bbs.module.context.ClickContext
import com.febers.uestc_bbs.utils.log

class UWebViewClient(private val context: Context,
                     private var acceptAllRequest: Boolean = true,
                     private var openUrlOut: Boolean = false,
                     private var processImageClick: Boolean = false): WebViewClient() {

    override fun onReceivedSslError(view: WebView, handler: SslErrorHandler, error: SslError) {
        if (acceptAllRequest) {
            handler.proceed()
        }
    }

    /**
     * webveiw只能打开以http/https开头的网页
     * 遇到其他开头的url，比如一些scheme，比如打开其他应用
     * 以openapp开头的url时，就会报错，需要手动处理
     */
    override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
        if (openUrlOut) {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            if (intent.resolveActivity(context.packageManager) != null) {
                context.startActivity(intent)
            }
            return true
        } else {
            if (url.startsWith("openapp")) {
                return true
            }
            //view.loadUrl(url);
            ClickContext.linkClick(url, view.context)
            return true
        }
    }

    @TargetApi(21)
    override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
        if (openUrlOut) {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(request.url.toString()))
            if (intent.resolveActivity(context.packageManager) != null) {
                context.startActivity(intent)
            }
            return true
        } else {
            if (request.url.toString().startsWith("openapp")) {
                return true
            }
            //view.loadUrl(request.getUrl().toString());
            ClickContext.linkClick(request.url.toString(), view.context)
            return true
        }
    }

    override fun onPageFinished(view: WebView, url: String) {
        view.settings.javaScriptEnabled = true
        super.onPageFinished(view, url)
        if (processImageClick) {
            log { "pageFinish, 注入js方法" }
            view.loadUrl("javascript:(function(){" +
                    "var objs = document.getElementsByTagName(\"img\"); " +
                    "for(var i=0;i<objs.length;i++)  " +
                    "{"
                    + "    objs[i].onclick=function()  " +
                    "    {  "
                    + "        window.imagelistener.openImage(this.src);  " +//通过js代码找到标签为img的代码块，设置点击的监听方法与本地的openImage方法进行连接
                    "    }  " +
                    "}" +
                    "})()")
        }
    }
}