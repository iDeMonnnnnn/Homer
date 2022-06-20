package com.demon.apport.data

import android.graphics.drawable.Drawable

/**
 * Created by cretin on 2018/1/12.
 */
class InfoModel {
    var type: Int = 0 //0.普通文件，1.apk
    var path: String? = null
    var version: String? = null
    var size: String? = null
    var name: String? = null
    var packageName: String? = null
    var isInstalled = false
    var icon: Drawable? = null

    constructor() {

    }

    constructor(type: Int) {
        this.type = type
    }
}