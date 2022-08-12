package com.demon.apport.util

import com.tencent.mmkv.MMKV
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

/**
 * @author DeMon
 * Created on 2021/11/22.
 * E-mail idemon_liu@qq.com
 * Desc: 别名
 */

val scopeMain = CoroutineScope((SupervisorJob() + Dispatchers.Main))

val scopeIO = CoroutineScope((SupervisorJob() + Dispatchers.IO))