package com.king.zxing.analyze;

import android.graphics.ImageFormat;
import android.graphics.YuvImage;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.qrcode.QRCodeReader;
import com.huawei.hms.hmsscankit.ScanUtil;
import com.huawei.hms.ml.scan.HmsScan;
import com.huawei.hms.ml.scan.HmsScanFrame;
import com.huawei.hms.ml.scan.HmsScanFrameOptions;
import com.huawei.hms.ml.scan.HmsScanResult;
import com.king.zxing.DecodeConfig;

public class HuaweiAnalyzer extends AreaRectAnalyzer {
    private Reader mReader;
    public HuaweiAnalyzer(@Nullable DecodeConfig config) {
        super(config);
        initReader();
    }
    private void initReader() {
        mReader = createReader();
    }

    public Reader createReader() {
        return new QRCodeReader();
    }

    @Nullable
    @Override
    public Result analyze(byte[] data, int dataWidth, int dataHeight, int left, int top, int width, int height) {
        YuvImage yuvImage = new YuvImage(data, ImageFormat.NV21, width, height, null);
        HmsScanFrame frame= new HmsScanFrame(yuvImage);
        // “QRCODE_SCAN_TYPE”和“PDF417_SCAN_TYPE”表示只扫描QR和PDF417的码
        HmsScanFrameOptions option = new HmsScanFrameOptions.Creator().setHmsScanTypes(HmsScan.QRCODE_SCAN_TYPE).setMultiMode(true).setParseResult(true).setPhotoMode(true).create();
        HmsScanResult result = ScanUtil.decode(mContext, frame, option);
        HmsScan[] hmsScans = result.getHmsScans();
        // 扫码成功时处理解码结果
        if (hmsScans != null && hmsScans.length > 0 && !TextUtils.isEmpty(hmsScans[0].getOriginalValue())) {
            // 展示扫码结果
            for (int i = 0; i < hmsScans.length; i++) {
                Log.d("zcrzcrzcr", "analyze: " + hmsScans[i].getOriginalValue());
            }
            return new Result(hmsScans[0].getOriginalValue(), null, null, null);
        }

        return null;
    }
}
