package com.king.wechat.qrcode.app;

import android.content.Context;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/**
 * ScreenUtils
 * <ul>
 * <strong>Convert between dp and sp</strong>
 * </ul>
 *
 * @author <a href="http://www.trinea.cn" target="_blank">Trinea</a> 2014-2-14
 */
public class ScreenUtils {

    private static int mScreenWidth = -1;
    private static int mScreenHeight = -1;

    private ScreenUtils() {
        throw new AssertionError();
    }

    /**
     * 获取设置的屏幕宽度，如无设置则返回{@link DisplayMetrics#widthPixels}
     *
     * @return
     */
    public static int getScreenWidth(Context context) {
        if(mScreenWidth <= 0){
            DisplayMetrics dm = context.getResources().getDisplayMetrics();
            mScreenWidth = dm.widthPixels;
        }
        return mScreenWidth;
    }

    /**
     * 获取设置的屏幕高度，如无设置则返回{@link DisplayMetrics#heightPixels}
     *
     * @return
     */
    public static int getScreenHeight(Context context) {
        if(mScreenHeight <= 0){
            DisplayMetrics dm = context.getResources().getDisplayMetrics();
            mScreenHeight = dm.heightPixels;
        }
        return mScreenHeight;
    }

    /**
     * 设置屏幕高度
     * @param height
     */
    public static void setScreenHeight(int height){
        mScreenHeight = height;
    }

    /**
     * 设置屏幕宽度
     * @param width
     */
    public static void setScreenWidth(int width){
        mScreenWidth = width;
    }

    /**
     * 通过{@link android.view.Display#getRealSize(Point)}获取当前屏幕尺寸
     * @param context
     * @return
     */
    public static Point getRealDisplaySize(Context context) {
        Point point = new Point();
        if(context != null){
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            wm.getDefaultDisplay().getRealSize(point);
        }
        return point;
    }
}

