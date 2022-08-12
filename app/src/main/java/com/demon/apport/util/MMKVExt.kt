package com.demon.apport.util

import android.os.Parcelable
import com.tencent.mmkv.MMKV

/**
 * @author DeMonnnnnn
 * date 2022/8/12
 * email liu_demon@qq.com
 * desc
 */
var mmkv = MMKV.defaultMMKV()


fun <T : Any> MMKV?.put(key: String, value: T) {
    this ?: return
    when (value) {
        is Boolean -> encode(key, value)
        is String -> encode(key, value)
        is Double -> encode(key, value)
        is Float -> encode(key, value)
        is Int -> encode(key, value)
        is Long -> encode(key, value)
        is ByteArray -> encode(key, value)
        is Parcelable -> encode(key, value)
        else -> {
            throw IllegalArgumentException("Not support $value type ${value.javaClass}..")
        }
    }
}

fun <T : Any> MMKV?.get(key: String, default: T): T {
    this ?: return default
    return when (default) {
        is Boolean -> decodeBool(key, default) as T
        is String -> decodeString(key, default) as T
        is Double -> decodeDouble(key, default) as T
        is Float -> decodeFloat(key, default) as T
        is Int -> decodeInt(key, default) as T
        is Long -> decodeLong(key, default) as T
        is ByteArray -> decodeBytes(key, default) as T
        is Parcelable -> decodeParcelable(key, default.javaClass) as T
        else -> {
            throw IllegalArgumentException("Not support $default type ${default.javaClass}..")
        }
    }
}