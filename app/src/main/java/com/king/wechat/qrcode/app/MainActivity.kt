package com.king.wechat.qrcode.app

import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.text.InputType
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.processing.SurfaceProcessorNode.In
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.lifecycleScope
import com.google.zxing.Result
import com.huawei.hms.hmsscankit.ScanUtil
import com.huawei.hms.ml.scan.HmsScan
import com.huawei.hms.ml.scan.HmsScanFrame
import com.huawei.hms.ml.scan.HmsScanFrameOptions
import com.king.camera.scan.CameraScan
import com.king.logx.LogX
import com.king.opencv.qrcode.OpenCVQRCodeDetector
import com.king.wechat.qrcode.WeChatQRCodeDetector
import com.king.wechat.qrcode.app.dialog.CommonResultDialog
import com.king.wechat.qrcode.app.dialog.DialogConfig
import com.king.wechat.qrcode.app.huawei.HuaweiQrCodeActivity
import com.king.wechat.qrcode.app.zxing.QRCodeScanActivity
import com.king.zxing.app.FullScreenQRCodeScanActivity
import com.king.zxing.util.CodeUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.opencv.OpenCV

/**
 * 示例
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 * <p>
 * <a href="https://github.com/jenly1314">Follow me</a>
 */
class MainActivity : AppCompatActivity() {

    /**
     * OpenCVQRCodeDetector
     */
    private val openCVQRCodeDetector by lazy {
        OpenCVQRCodeDetector()
    }

    /**
     * 是否使用 WeChatQRCodeDetector 进行检测二维码
     */
    private var type = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // 初始化OpenCV
        OpenCV.initOpenCV()
        // 初始化WeChatQRCodeDetector
        WeChatQRCodeDetector.init(this)
    }

    private fun getContext() = this

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                REQUEST_CODE_QRCODE -> processQRCodeResult(data)
                REQUEST_CODE_PICK_PHOTO -> processPickPhotoResult(data)
            }
        }
    }

    /**
     * 处理选择图片后，从图片中检测二维码结果
     */
    @Suppress("DEPRECATION")
    private fun processPickPhotoResult(data: Intent?) {
        data?.let {
            try {
                startTime = System.currentTimeMillis()
                lifecycleScope.launch {
                    val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, it.data)
                    if (type == TYPE_WEIXIN) {
                        val result = withContext(Dispatchers.IO) {
                            // 通过WeChatQRCodeDetector识别图片中的二维码
                            WeChatQRCodeDetector.detectAndDecode(bitmap)
                        }
                        if (result.isNotEmpty()) {// 不为空，则表示识别成功
                            // 打印所有结果
                            for ((index, text) in result.withIndex()) {
                                LogX.d("result$index:$text")
                            }
                            // 一般需求都是识别一个码，所以这里取第0个就可以；有识别多个码的需求，可以取全部
                            Toast.makeText(getContext(), result[0], Toast.LENGTH_SHORT).show()
                            showResultDialog(result[0], true)
                        } else {
                            // 为空表示识别失败
                            LogX.d("result = null")
                        }
                    } else if (type == TYPE_HUAWEI) {
                        val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, it.data)

                        val frame: HmsScanFrame = HmsScanFrame(bitmap)

                        // “QRCODE_SCAN_TYPE”和“PDF417_SCAN_TYPE”表示只扫描QR和PDF417的码
                        val option = HmsScanFrameOptions.Creator().setHmsScanTypes(HmsScan.QRCODE_SCAN_TYPE)
                                .setMultiMode(false).setParseResult(true).setPhotoMode(true).create()
                        val result = ScanUtil.decode(getContext(), frame, option)
                        val hmsScans = result.hmsScans

                        // 扫码成功时处理解码结果
                        if (hmsScans != null && hmsScans.size > 0 && !TextUtils.isEmpty(hmsScans[0].getOriginalValue())) {
                            // 展示扫码结果
                            for (i in hmsScans.indices) {
                                Toast.makeText(getContext(), hmsScans[0].getOriginalValue(), Toast.LENGTH_SHORT).show()
                                showResultDialog(hmsScans[0].getOriginalValue(), true)
                            }
                        }

                    }else{
                        // zxing 识别
                        try {
                            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, data.data)
                            val result = CodeUtils.parseCode(bitmap)
                            LogX.d("result$result:$result")
                            if (result != null) {
                                Toast.makeText(getContext(), result, Toast.LENGTH_SHORT).show()
                                showResultDialog(result, true)
                            }
                        } catch (e: java.lang.Exception) {
                            e.printStackTrace()
                        }
                    }

                }

            } catch (e: Exception) {
                LogX.w(e)
            }

        }
    }

    private fun processQRCodeResult(intent: Intent?) {
        // 扫码结果
        CameraScan.parseScanResult(intent)?.let {
            Log.d(CameraScan.SCAN_RESULT, it)
            Toast.makeText(getContext(), it, Toast.LENGTH_SHORT).show()
            showResultDialog(it, false)
        }
    }

    private fun showResultDialog(result: String?, isFromCameraGallery: Boolean) {
        result?.let{
            // 弹窗
            CommonResultDialog(getContext()).apply {
                setConfig(DialogConfig().apply {
                    this.isFromCameraGallery = isFromCameraGallery
                    content = it
                    costTime = System.currentTimeMillis() - startTime
                    Log.d("percyspeed", "耗时: " + costTime)
                })
                show()
            }
        }

    }

    private fun pickPhotoClicked(type:  Int) {
        this.type = type
        startPickPhoto()
    }

    private fun startPickPhoto() {
        val pickIntent = Intent(Intent.ACTION_PICK)
        pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
        startActivityForResult(pickIntent, REQUEST_CODE_PICK_PHOTO)
    }

    private fun startActivityForResult(clazz: Class<*>) {
        val options = ActivityOptionsCompat.makeCustomAnimation(this, R.anim.alpha_in, R.anim.alpha_out)
        startActivityForResult(Intent(this, clazz), REQUEST_CODE_QRCODE, options.toBundle())
    }

    fun onClick(view: View) {
        when (view.id) {
            R.id.btnWeChatQRCodeScan -> startActivityForResult(WeChatQRCodeActivity::class.java)
            R.id.btnZXingFullScreenQRCode -> startActivityForResult(FullScreenQRCodeScanActivity::class.java)
            R.id.btnWeChatQRCodeDecode -> pickPhotoClicked(TYPE_WEIXIN)
            R.id.btnZXingQRCode -> startActivityForResult(QRCodeScanActivity::class.java)
            R.id.btnOpenCVQRCodeDecode -> pickPhotoClicked(TYPE_ZXING)
            R.id.btnHuaweiQRCodeDecode -> startActivityForResult(HuaweiQrCodeActivity::class.java)
            R.id.btnHuaweiGalleryDecode -> pickPhotoClicked(TYPE_HUAWEI)
        }
    }

    companion object {

        @JvmField
        var startTime: Long = 0L
        const val REQUEST_CODE_QRCODE = 0x10
        const val REQUEST_CODE_PICK_PHOTO = 0x11

        val TYPE_WEIXIN = 0
        val TYPE_HUAWEI = 1
        val TYPE_ZXING = 2
    }
}
