package com.king.wechat.qrcode.app.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.TextView
import com.king.wechat.qrcode.app.R


/**
 * 通用的弹窗
 */
class CommonResultDialog(val mContext: Context) : Dialog(mContext) {
    var btnCancel: TextView? = null
    var btnConfirm: TextView? = null

    var tvResult: TextView? = null
    var tvCostTime: TextView? = null
    var mDialogConfig: DialogConfig? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.qrcode_tips_dialog)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        btnCancel = findViewById(R.id.btnDialogCancel)
        btnConfirm = findViewById(R.id.btnDialogConfirm)
        tvResult = findViewById(R.id.tvDialogContent)
        tvCostTime = findViewById(R.id.tv_cost_time)

        mDialogConfig?.let{
            btnConfirm?.setOnClickListener{
                dismiss()
            }

            btnCancel?.setOnClickListener {
                dismiss()
            }

            tvResult?.text = it.content
            if (it.isFromCameraGallery) {
                tvCostTime?.text = "耗时" + it.costTime.toString() + "(从获取到图片bitmap计算)"
            } else {
                tvCostTime?.text = "耗时" + it.costTime.toString() + "(从activity onCreate计算)"
            }
        }
    }


    fun setConfig(config: DialogConfig?) {
        this.mDialogConfig = config
    }

}
