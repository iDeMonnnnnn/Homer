package com.demon.apport.util

import android.content.res.Resources
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.Toast
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import com.demon.apport.App
import com.demon.apport.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import java.lang.reflect.ParameterizedType

/**
 * @author DeMonnnnnn
 * @date 2022/6/20
 * @email liu_demon@qq.com
 * @desc
 */
inline val <T : Any> T.Tag: String
    get() = this.javaClass.simpleName


val Any.dp2px
    get() = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, "$this".parseFloat(), Resources.getSystem().displayMetrics).toInt()

/**
 * 12.sp2px = 12sp
 */
val Any.sp2px
    get() = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, "$this".parseFloat(), Resources.getSystem().displayMetrics).toInt()


/**
 * String转成Float
 */
fun String?.parseFloat(defaultValue: Float = 0f): Float {
    if (this.isNullOrEmpty()) return defaultValue
    return tryCatch(defaultValue) {
        this.toFloat()
    }
}


/**
 * 当需要返回值try的时候使用
 */
inline fun <T> tryCatch(default: T, block: () -> T): T {
    return try {
        block()
    } catch (e: Exception) {
        Log.e("TryExt", "tryCatch: ", e)
        default
    }
}

fun String?.toast() {
    this ?: return
    Toast.makeText(App.appContext, this, Toast.LENGTH_SHORT).show()
}

/**
 * 反射执行ViewBinding的inflate静态方法，主要在Activity中使用
 *
 * @param inflater LayoutInflater参数
 * @param index 表示第几个泛型
 */
inline fun <VB : ViewBinding> Any.inflateViewBinding(inflater: LayoutInflater, index: Int = 0): VB {
    val cla = (this.javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[index] as Class<VB>
    return cla.getMethod("inflate", LayoutInflater::class.java).invoke(null, inflater) as VB
}


fun ImageView.loadImg(path: String?) {
    Glide.with(this)
        .load(path)
        .error(R.mipmap.icon_logo)
        .placeholder(R.mipmap.icon_logo)
        .into(this)
}