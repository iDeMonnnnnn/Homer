package com.demon.apport.service

import android.content.Context
import com.demon.qfsolution.utils.getExternalOrFilesDir
import com.demon.apport.service.WebHelper.FileUploadHolder
import com.koushikdutta.async.http.server.AsyncHttpServer
import com.koushikdutta.async.AsyncServer
import com.koushikdutta.async.http.server.HttpServerRequestCallback
import com.koushikdutta.async.http.server.AsyncHttpServerRequest
import com.koushikdutta.async.http.server.AsyncHttpServerResponse
import org.json.JSONArray
import org.json.JSONObject
import org.json.JSONException
import com.koushikdutta.async.http.body.UrlEncodedFormBody
import com.jeremyliao.liveeventbus.LiveEventBus
import com.koushikdutta.async.http.body.MultipartFormDataBody
import com.koushikdutta.async.http.body.MultipartFormDataBody.MultipartCallback
import com.koushikdutta.async.DataEmitter
import com.koushikdutta.async.ByteBufferList
import com.koushikdutta.async.callback.CompletedCallback
import com.demon.apport.service.WebHelper
import kotlin.Throws
import android.text.TextUtils
import kotlin.jvm.Volatile
import com.demon.apport.App
import android.os.Environment
import android.util.Log
import com.demon.apport.data.Constants
import com.koushikdutta.async.callback.DataCallback
import com.koushikdutta.async.http.body.Part
import java.io.*
import java.lang.Exception
import java.net.URLDecoder
import java.net.URLEncoder
import java.text.DecimalFormat

/**
 * @author DeMonnnnnn
 * @date 2022/6/29
 * @email liu_demon@qq.com
 * @desc
 */
class WebHelper private constructor() {
    private val fileUploadHolder: FileUploadHolder
    private val server: AsyncHttpServer?
    private val mAsyncServer: AsyncServer?
    private val dir: File

    init {
        fileUploadHolder = FileUploadHolder()
        server = AsyncHttpServer()
        mAsyncServer = AsyncServer()
        dir = App.appContext.getExternalOrFilesDir(Environment.DIRECTORY_DCIM)
    }

    fun startServer(context: Context) {
        server?.get("/images/.*") { request: AsyncHttpServerRequest, response: AsyncHttpServerResponse -> sendResources(context, request, response) }
        server?.get("/scripts/.*") { request: AsyncHttpServerRequest, response: AsyncHttpServerResponse -> sendResources(context, request, response) }
        server?.get("/css/.*") { request: AsyncHttpServerRequest, response: AsyncHttpServerResponse -> sendResources(context, request, response) }
        //index page
        server?.get("/") { request: AsyncHttpServerRequest?, response: AsyncHttpServerResponse ->
            try {
                response.send(getIndexContent(context))
            } catch (e: IOException) {
                e.printStackTrace()
                response.code(500).end()
            }
        }
        //query upload list
        server?.get("/files") { request: AsyncHttpServerRequest?, response: AsyncHttpServerResponse ->
            val array = JSONArray()
            if (dir.exists() && dir.isDirectory) {
                val fileNames = dir.list()
                if (fileNames != null) {
                    for (fileName in fileNames) {
                        val file = File(dir, fileName)
                        if (file.exists() && file.isFile) {
                            try {
                                val jsonObject = JSONObject()
                                jsonObject.put("name", fileName)
                                val fileLen = file.length()
                                val df = DecimalFormat("0.00")
                                if (fileLen > 1024 * 1024) {
                                    jsonObject.put("size", df.format((fileLen * 1f / 1024 / 1024).toDouble()) + "MB")
                                } else if (fileLen > 1024) {
                                    jsonObject.put("size", df.format((fileLen * 1f / 1024).toDouble()) + "KB")
                                } else {
                                    jsonObject.put("size", fileLen.toString() + "B")
                                }
                                array.put(jsonObject)
                            } catch (e: JSONException) {
                                e.printStackTrace()
                            }
                        }
                    }
                }
            }
            response.send(array.toString())
        }
        //delete
        server?.post("/files/.*") { request: AsyncHttpServerRequest, response: AsyncHttpServerResponse ->
            val body = request.body as UrlEncodedFormBody
            if ("delete".equals(body.get().getString("_method"), ignoreCase = true)) {
                var path: String? = request.path.replace("/files/", "")
                try {
                    path = URLDecoder.decode(path, "utf-8")
                } catch (e: UnsupportedEncodingException) {
                    e.printStackTrace()
                }
                val file = File(dir, path)
                if (file.exists() && file.isFile) {
                    file.delete()
                    LiveEventBus.get<Any>(Constants.LOAD_BOOK_LIST).post(0)
                }
            }
            response.end()
        }
        //download
        server?.get("/files/.*") { request: AsyncHttpServerRequest, response: AsyncHttpServerResponse ->
            var path: String? = request.path.replace("/files/", "")
            try {
                path = URLDecoder.decode(path, "utf-8")
            } catch (e: UnsupportedEncodingException) {
                e.printStackTrace()
            }
            val file = File(dir, path)
            if (file.exists() && file.isFile) {
                try {
                    response.headers.add("Content-Disposition", "attachment;filename=" + URLEncoder.encode(file.name, "utf-8"))
                } catch (e: UnsupportedEncodingException) {
                    e.printStackTrace()
                }
                response.sendFile(file)
            } else {
                response.code(404).send("Not found!")
            }
        }
        //upload
        server?.post(
            "/files"
        ) { request: AsyncHttpServerRequest, response: AsyncHttpServerResponse ->
            val body = request.body as MultipartFormDataBody
            body.multipartCallback = MultipartCallback { part: Part ->
                if (part.isFile) {
                    body.dataCallback = DataCallback { emitter: DataEmitter?, bb: ByteBufferList ->
                        fileUploadHolder.write(bb.allByteArray)
                        bb.recycle()
                    }
                } else {
                    if (body.dataCallback == null) {
                        body.dataCallback = DataCallback { emitter: DataEmitter?, bb: ByteBufferList ->
                            try {
                                val fileName = URLDecoder.decode(String(bb.allByteArray), "UTF-8")
                                fileUploadHolder.setFilename(fileName)
                            } catch (e: UnsupportedEncodingException) {
                                e.printStackTrace()
                            }
                            bb.recycle()
                        }
                    }
                }
            }
            request.endCallback = CompletedCallback { e: Exception? ->
                fileUploadHolder.reset()
                response.end()
                LiveEventBus.get<Any>(Constants.LOAD_BOOK_LIST).post(0)
            }
        }
        server?.get("/progress/.*") { request: AsyncHttpServerRequest, response: AsyncHttpServerResponse ->
            val res = JSONObject()
            val path = request.path.replace("/progress/", "")
            if (path == fileUploadHolder.fileName) {
                try {
                    res.put("fileName", fileUploadHolder.fileName)
                    res.put("size", fileUploadHolder.totalSize)
                    res.put("progress", if (fileUploadHolder.fileOutPutStream == null) 1 else 0.1)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
            response.send(res)
        }
        server?.errorCallback = CompletedCallback { ex: Exception? -> Log.e(TAG, "startServer: " + Log.getStackTraceString(ex)) }
        server?.listen(mAsyncServer, Constants.HTTP_PORT)
    }

    @Throws(IOException::class)
    private fun getIndexContent(context: Context): String {
        var bInputStream: BufferedInputStream? = null
        return try {
            bInputStream = BufferedInputStream(context.assets.open("wifi/index.html"))
            val baos = ByteArrayOutputStream()
            var len = 0
            val tmp = ByteArray(10240)
            while (bInputStream.read(tmp).also { len = it } > 0) {
                baos.write(tmp, 0, len)
            }
            baos.toString("utf-8")
        } catch (e: IOException) {
            e.printStackTrace()
            throw e
        } finally {
            if (bInputStream != null) {
                try {
                    bInputStream.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun sendResources(context: Context, request: AsyncHttpServerRequest, response: AsyncHttpServerResponse) {
        try {
            var fullPath = request.path
            fullPath = fullPath.replace("%20", " ")
            var resourceName = fullPath
            if (resourceName.startsWith("/")) {
                resourceName = resourceName.substring(1)
            }
            if (resourceName.indexOf("?") > 0) {
                resourceName = resourceName.substring(0, resourceName.indexOf("?"))
            }
            if (!TextUtils.isEmpty(getContentTypeByResourceName(resourceName))) {
                response.setContentType(getContentTypeByResourceName(resourceName))
            }
            val bInputStream = BufferedInputStream(context.assets.open("wifi/$resourceName"))
            response.sendStream(bInputStream, bInputStream.available().toLong())
        } catch (e: IOException) {
            e.printStackTrace()
            response.code(404).end()
            return
        }
    }

    private fun getContentTypeByResourceName(resourceName: String): String {
        if (resourceName.endsWith(".css")) {
            return CSS_CONTENT_TYPE
        } else if (resourceName.endsWith(".js")) {
            return JS_CONTENT_TYPE
        } else if (resourceName.endsWith(".swf")) {
            return SWF_CONTENT_TYPE
        } else if (resourceName.endsWith(".png")) {
            return PNG_CONTENT_TYPE
        } else if (resourceName.endsWith(".jpg") || resourceName.endsWith(".jpeg")) {
            return JPG_CONTENT_TYPE
        } else if (resourceName.endsWith(".woff")) {
            return WOFF_CONTENT_TYPE
        } else if (resourceName.endsWith(".ttf")) {
            return TTF_CONTENT_TYPE
        } else if (resourceName.endsWith(".svg")) {
            return SVG_CONTENT_TYPE
        } else if (resourceName.endsWith(".eot")) {
            return EOT_CONTENT_TYPE
        } else if (resourceName.endsWith(".mp3")) {
            return MP3_CONTENT_TYPE
        } else if (resourceName.endsWith(".mp4")) {
            return MP4_CONTENT_TYPE
        }
        return ""
    }

    inner class FileUploadHolder {
        var fileName: String = ""
        private var recievedFile: File? = null
        var fileOutPutStream: BufferedOutputStream? = null
            private set
        var totalSize: Long = 0

        fun setFilename(fileName: String) {
            this.fileName = fileName
            totalSize = 0
            if (!dir.exists()) {
                dir.mkdirs()
            }
            recievedFile = File(dir, fileName)
            Log.d(TAG, "recievedFile=" + recievedFile?.absolutePath)
            try {
                fileOutPutStream = BufferedOutputStream(FileOutputStream(recievedFile))
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }
        }

        fun reset() {
            try {
                fileOutPutStream?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            fileOutPutStream = null
        }

        fun write(data: ByteArray) {
            try {
                fileOutPutStream?.write(data)
            } catch (e: IOException) {
                e.printStackTrace()
            }
            totalSize += data.size.toLong()
        }
    }

    fun stopService() {
        server?.stop()
        mAsyncServer?.stop()
    }

    /**
     * 通过running判断是否连接
     */
    fun isConnected(): Boolean = mAsyncServer?.isRunning == true

    companion object {
        private const val TAG = "WebHelper"


        val instance: WebHelper by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            WebHelper()
        }

        private const val TEXT_CONTENT_TYPE = "text/html;charset=utf-8"
        private const val CSS_CONTENT_TYPE = "text/css;charset=utf-8"
        private const val BINARY_CONTENT_TYPE = "application/octet-stream"
        private const val JS_CONTENT_TYPE = "application/javascript"
        private const val PNG_CONTENT_TYPE = "application/x-png"
        private const val JPG_CONTENT_TYPE = "application/jpeg"
        private const val SWF_CONTENT_TYPE = "application/x-shockwave-flash"
        private const val WOFF_CONTENT_TYPE = "application/x-font-woff"
        private const val TTF_CONTENT_TYPE = "application/x-font-truetype"
        private const val SVG_CONTENT_TYPE = "image/svg+xml"
        private const val EOT_CONTENT_TYPE = "image/vnd.ms-fontobject"
        private const val MP3_CONTENT_TYPE = "audio/mp3"
        private const val MP4_CONTENT_TYPE = "video/mpeg4"
    }

}