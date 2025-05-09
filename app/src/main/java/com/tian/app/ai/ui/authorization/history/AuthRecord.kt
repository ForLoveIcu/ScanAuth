package com.tian.app.ai.ui.authorization.history

data class AuthRecord(
    val id: String,
    val packageNo: String,
    val authNo: String,
    val robotNo: String,
    val serialNo: String,
    val callbackTime: String,
    val createTime: String,
    val remark: String,
    val status: Boolean,
    var userName: String = "" // 添加使用人字段，默认为空
) 