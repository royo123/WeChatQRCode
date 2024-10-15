package com.king.wechat.qrcode.app.dialog

class DialogConfig {
    var title: String? = null
    var content: String? = null
    var costTime: Long = 0L
    var isFromCameraGallery = false // 是否是从相册
    var click : (()-> Unit)? = null
    var cancel : (()-> Unit)? = null
}
