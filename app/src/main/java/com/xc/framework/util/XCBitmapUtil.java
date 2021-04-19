package com.xc.framework.util;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;

import com.xc.framework.bitmap.BitmapDisplayConfig;
import com.xc.framework.bitmap.BitmapLoader;
import com.xc.framework.bitmap.callback.BitmapLoadCallBack;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;

/**
 * @author ZhangXuanChen
 * @date 2015-9-25
 * @package com.example.androiddemo
 * @description 图片异步加载工具
 */
public class XCBitmapUtil {
    // --------------------------------------------------------------------显示----------------------------------------------------------------

    /**
     * 显示view图片
     *
     * @param context   上下文
     * @param container 容器
     * @param url       地址
     */
    public static <T extends android.view.View> void display(Context context, T container, String url) {
        BitmapLoader.getInstance(context).display(container, url, null, null);
    }

    /**
     * 显示view图片
     *
     * @param context       上下文
     * @param container     容器
     * @param url           地址
     * @param diskCachePath 本地缓存路径
     */
    public static <T extends android.view.View> void display(Context context, T container, String url, String diskCachePath) {
        BitmapLoader.getInstance(context, diskCachePath).display(container, url, null, null);
    }

    /**
     * 显示view图片
     *
     * @param context   上下文
     * @param container 容器
     * @param url       地址
     * @param callBack  加载回调
     */
    public static <T extends android.view.View> void display(Context context, T container, String url, BitmapLoadCallBack<T> callBack) {
        BitmapLoader.getInstance(context).display(container, url, null, callBack);
    }

    /**
     * 显示view图片
     *
     * @param context       上下文
     * @param container     容器
     * @param url           地址
     * @param diskCachePath 本地缓存路径
     * @param callBack      加载回调
     */
    public static <T extends android.view.View> void display(Context context, T container, String url, String diskCachePath, BitmapLoadCallBack<T> callBack) {
        BitmapLoader.getInstance(context, diskCachePath).display(container, url, null, callBack);
    }

    /**
     * 显示view图片
     *
     * @param context       上下文
     * @param container     容器
     * @param url           地址
     * @param diskCachePath 本地缓存路径
     * @param displayConfig 显示配置
     */
    public static <T extends android.view.View> void display(Context context, T container, String url, String diskCachePath, BitmapDisplayConfig displayConfig) {
        BitmapLoader.getInstance(context, diskCachePath).display(container, url, displayConfig, null);
    }

    /**
     * 显示view图片
     *
     * @param context       上下文
     * @param container     容器
     * @param url           地址
     * @param diskCachePath 本地缓存路径
     * @param displayConfig 显示配置
     * @param callBack      加载回调
     */
    public static <T extends android.view.View> void display(Context context, T container, String url, String diskCachePath, BitmapDisplayConfig displayConfig, BitmapLoadCallBack<T> callBack) {
        BitmapLoader.getInstance(context, diskCachePath).display(container, url, displayConfig, callBack);
    }

    /**
     * 清除缓存
     *
     * @param context 上下文
     */
    public static void clearCache(Context context) {
        clearCache(context, null);
    }

    /**
     * 清除缓存
     *
     * @param context       上下文
     * @param diskCachePath 本地缓存路径
     */
    public static void clearCache(Context context, String diskCachePath) {
        BitmapLoader.getInstance(context, diskCachePath).clearCache();
    }

    /**
     * 根据url地址获取Bitmap
     *
     * @param url 地址
     * @return bitmap对象
     */
    public synchronized Bitmap getBitMap(final String url) {
        java.net.URL fileUrl = null;
        java.io.InputStream is = null;
        Bitmap bitmap = null;
        try {
            fileUrl = new java.net.URL(url);
            HttpURLConnection conn = (HttpURLConnection) fileUrl.openConnection();
            conn.setDoInput(true);
            conn.connect();
            is = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != is) {
                    is.close();
                }
            } catch (IOException e) {
            }
            is = null;
        }
        return bitmap;
    }

    /**
     * 根据本地路径获取Bitmap
     *
     * @param filePath 本地路径
     * @return bitmap对象
     */
    public synchronized static Bitmap getBitmap(String filePath) {
        Bitmap bitmap = null;
        if (!XCStringUtil.isEmpty(filePath)) {
            try {
                File file = new File(filePath);
                // 判断文件是否存在
                if (file.exists()) {
                    bitmap = BitmapFactory.decodeFile(filePath);
                }
            } catch (Exception e) {
            }
        }
        return bitmap;
    }

    /**
     * 根据uri获取Bitmap
     *
     * @param context 上下文
     * @param uri     uri对象
     * @return bitmap对象
     * @deprecated 默认缩略图是原图大小的1/4
     */
    public static Bitmap getBitmap(Context context, Uri uri) {
        return getBitmap(context, uri, 4);
    }

    /**
     * 根据uri获取Bitmap
     *
     * @param context      上下文
     * @param uri          uri对象
     * @param inSampleSize 缩略图缩小倍数
     * @return bitmap对象
     */
    public static Bitmap getBitmap(Context context, Uri uri, int inSampleSize) {
        Bitmap bitmap = null;
        if (context != null && uri != null) {
            try {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                options.inSampleSize = inSampleSize;
                options.inPreferredConfig = Config.RGB_565;
                options.inPurgeable = true;
                options.inInputShareable = true;
                options.inJustDecodeBounds = false;
                bitmap = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri), null, options);
            } catch (Exception e) {
            }
        }
        return bitmap;
    }

    /**
     * 将Bitmap转化为Drawable
     *
     * @param bitmap bitmap对象
     * @return drawable对象
     */
    @SuppressWarnings("deprecation")
    public static android.graphics.drawable.Drawable toDrawable(Bitmap bitmap) {
        BitmapDrawable bitmapDrawable = null;
        if (bitmap != null) {
            bitmapDrawable = new BitmapDrawable(bitmap);
        }
        return bitmapDrawable;
    }

    /**
     * 换字节数组
     *
     * @param bitmap bitmap对象
     * @return 字节数组
     */
    public static byte[] toBytes(Bitmap bitmap) {
        if (bitmap != null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            return baos.toByteArray();
        }
        return null;
    }

    /**
     * Author：ZhangXuanChen
     * Time：2020/4/9 11:55
     * Description：转Base64
     */
    public static String toBase64(Bitmap bitmap) {
        if (bitmap != null) {
            return XCByteUtil.toBase64(toBytes(bitmap));
        }
        return null;
    }

    /**
     * 缩放图片
     *
     * @param bitmap bitmap对象
     * @param width  宽
     * @param height 高
     * @return bitmap对象
     */
    public static Bitmap zoomBitmap(Bitmap bitmap, int width, int height) {
        Bitmap createBitmap = null;
        if (bitmap != null && width > 0 && height > 0) {
            int w = bitmap.getWidth();
            int h = bitmap.getHeight();
            android.graphics.Matrix matrix = new android.graphics.Matrix();
            float scaleWidth = ((float) width / w);
            float scaleHeight = ((float) height / h);
            matrix.postScale(scaleWidth, scaleHeight);
            createBitmap = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
            bitmap.recycle();
            bitmap = null;
        }
        return createBitmap;
    }

    /**
     * 缩放图片
     *
     * @param bitmap   bitmap对象
     * @param multiple 缩放比例
     * @return bitmap对象
     */
    public static Bitmap zoomBitmap(Bitmap bitmap, float multiple) {
        Bitmap resizeBmp = null;
        if (bitmap != null && multiple > 0) {
            android.graphics.Matrix matrix = new android.graphics.Matrix();
            matrix.postScale(multiple, multiple); // 长和宽放大缩小的比例
            resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        }
        return resizeBmp;
    }

    /**
     * 旋转图片
     *
     * @param bitmap  bitmap对象
     * @param degrees 角度
     * @return bitmap对象
     */
    public static Bitmap rotateBitmap(Bitmap bitmap, float degrees) {
        Bitmap resizeBmp = null;
        if (bitmap != null && degrees > 0) {
            android.graphics.Matrix matrix = new android.graphics.Matrix();
            matrix.setRotate(degrees);
            resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        }
        return resizeBmp;
    }

    /**
     * 根据图片路径读取图片旋转角度
     *
     * @param filePath 图片路径
     * @return 旋转角度
     */
    public static int readPictureDegree(String filePath) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(filePath);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (Exception e) {
        }
        return degree;
    }

    /**
     * 保存图片到本地
     *
     * @param bitmap    bitmap对象
     * @param imagePath 图片绝对路径（含后缀名）
     * @return 保存路径
     */
    public static boolean saveBitmap(Bitmap bitmap, String imagePath) {
        try {
            if (bitmap != null && !XCStringUtil.isEmpty(imagePath)) {
                File file = new File(imagePath);
                if (file.exists() && file.isFile()) {
                    FileOutputStream os = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 90, os);
                    os.flush();
                    os.close();
                    return true;
                }
            }
        } catch (Exception e) {
        }
        return false;
    }

    /**
     * 从URI获取本地路径
     *
     * @param context 上下文
     * @param uri     uri对象
     * @return 本地路径
     */
    public static String getAbsoluteImagePath(Context context, Uri uri) {
        ContentResolver cr = context.getContentResolver();
        android.database.Cursor cursor = cr.query(uri, null, null, null, null);
        if (cursor != null) {// 相册
            try {
                cursor.moveToFirst();
                String filePath = cursor.getString(cursor.getColumnIndex("_data"));// 获取图片路
                return filePath;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        } else {// 拍照
            return uri.getPath();
        }
        return null;
    }

    /**
     * Bitmap添加水印
     *
     * @param src       原图bitmap对象
     * @param watermark 水印bitmap对象
     * @return 完成bitmap对象
     */
    public static Bitmap addWatermark(Bitmap src, Bitmap watermark) {
        if (src == null || watermark == null) {
            return src;
        }
        int sWid = src.getWidth();
        int sHei = src.getHeight();
        int wWid = watermark.getWidth();
        int wHei = watermark.getHeight();
        if (sWid == 0 || sHei == 0) {
            return null;
        }
        if (sWid < wWid || sHei < wHei) {
            return src;
        }
        Bitmap bitmap = Bitmap.createBitmap(sWid, sHei, Config.ARGB_8888);// Config可修改,改变内存占用
        try {
            Canvas cv = new Canvas(bitmap);
            cv.drawBitmap(src, 0, 0, null);
            cv.drawBitmap(watermark, sWid - wWid - 5, sHei - wHei - 5, null);
            cv.save();
            cv.restore();
        } catch (Exception e) {
            bitmap = null;
            e.getStackTrace();
        } finally {
            src.recycle();
            src = null;
            watermark.recycle();
            watermark = null;
        }
        return bitmap;
    }

    /**
     * 获取模糊Bitmap
     *
     * @param sentBitmap bitmap对象
     * @param radius     模糊半径
     * @return 完成bitmap对象
     */
    public static Bitmap getBlurBitmap(Bitmap sentBitmap, int radius) {
        if (sentBitmap != null && radius > 1) {
            Bitmap bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);
            //
            int w = bitmap.getWidth();
            int h = bitmap.getHeight();
            //
            int[] pix = new int[w * h];
            bitmap.getPixels(pix, 0, w, 0, 0, w, h);
            //
            int wm = w - 1;
            int hm = h - 1;
            int wh = w * h;
            int div = radius + radius + 1;
            //
            int r[] = new int[wh];
            int g[] = new int[wh];
            int b[] = new int[wh];
            int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
            int vmin[] = new int[Math.max(w, h)];
            //
            int divsum = (div + 1) >> 1;
            divsum *= divsum;
            int dv[] = new int[256 * divsum];
            for (i = 0; i < 256 * divsum; i++) {
                dv[i] = (i / divsum);
            }
            //
            yw = yi = 0;
            //
            int[][] stack = new int[div][3];
            int stackpointer;
            int stackstart;
            int[] sir;
            int rbs;
            int r1 = radius + 1;
            int routsum, goutsum, boutsum;
            int rinsum, ginsum, binsum;
            //
            for (y = 0; y < h; y++) {
                rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
                for (i = -radius; i <= radius; i++) {
                    p = pix[yi + Math.min(wm, Math.max(i, 0))];
                    sir = stack[i + radius];
                    sir[0] = (p & 0xff0000) >> 16;
                    sir[1] = (p & 0x00ff00) >> 8;
                    sir[2] = (p & 0x0000ff);
                    rbs = r1 - Math.abs(i);
                    rsum += sir[0] * rbs;
                    gsum += sir[1] * rbs;
                    bsum += sir[2] * rbs;
                    if (i > 0) {
                        rinsum += sir[0];
                        ginsum += sir[1];
                        binsum += sir[2];
                    } else {
                        routsum += sir[0];
                        goutsum += sir[1];
                        boutsum += sir[2];
                    }
                }
                stackpointer = radius;
                //
                for (x = 0; x < w; x++) {
                    r[yi] = dv[rsum];
                    g[yi] = dv[gsum];
                    b[yi] = dv[bsum];
                    //
                    rsum -= routsum;
                    gsum -= goutsum;
                    bsum -= boutsum;
                    //
                    stackstart = stackpointer - radius + div;
                    sir = stack[stackstart % div];
                    //
                    routsum -= sir[0];
                    goutsum -= sir[1];
                    boutsum -= sir[2];
                    //
                    if (y == 0) {
                        vmin[x] = Math.min(x + radius + 1, wm);
                    }
                    p = pix[yw + vmin[x]];
                    //
                    sir[0] = (p & 0xff0000) >> 16;
                    sir[1] = (p & 0x00ff00) >> 8;
                    sir[2] = (p & 0x0000ff);
                    //
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                    //
                    rsum += rinsum;
                    gsum += ginsum;
                    bsum += binsum;
                    //
                    stackpointer = (stackpointer + 1) % div;
                    sir = stack[(stackpointer) % div];
                    //
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                    //
                    rinsum -= sir[0];
                    ginsum -= sir[1];
                    binsum -= sir[2];
                    //
                    yi++;
                }
                yw += w;
            }
            for (x = 0; x < w; x++) {
                rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
                yp = -radius * w;
                for (i = -radius; i <= radius; i++) {
                    yi = Math.max(0, yp) + x;
                    //
                    sir = stack[i + radius];
                    //
                    sir[0] = r[yi];
                    sir[1] = g[yi];
                    sir[2] = b[yi];
                    //
                    rbs = r1 - Math.abs(i);
                    //
                    rsum += r[yi] * rbs;
                    gsum += g[yi] * rbs;
                    bsum += b[yi] * rbs;
                    //
                    if (i > 0) {
                        rinsum += sir[0];
                        ginsum += sir[1];
                        binsum += sir[2];
                    } else {
                        routsum += sir[0];
                        goutsum += sir[1];
                        boutsum += sir[2];
                    }
                    //
                    if (i < hm) {
                        yp += w;
                    }
                }
                yi = x;
                stackpointer = radius;
                for (y = 0; y < h; y++) {
                    // Preserve alpha channel: ( 0xff000000 & pix[yi] )
                    pix[yi] = (0xff000000 & pix[yi]) | (dv[rsum] << 16) | (dv[gsum] << 8) | dv[bsum];
                    //
                    rsum -= routsum;
                    gsum -= goutsum;
                    bsum -= boutsum;
                    //
                    stackstart = stackpointer - radius + div;
                    sir = stack[stackstart % div];
                    //
                    routsum -= sir[0];
                    goutsum -= sir[1];
                    boutsum -= sir[2];
                    //
                    if (x == 0) {
                        vmin[y] = Math.min(y + r1, hm) * w;
                    }
                    p = x + vmin[y];
                    //
                    sir[0] = r[p];
                    sir[1] = g[p];
                    sir[2] = b[p];
                    //
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                    //
                    rsum += rinsum;
                    gsum += ginsum;
                    bsum += binsum;
                    //
                    stackpointer = (stackpointer + 1) % div;
                    sir = stack[stackpointer];
                    //
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                    //
                    rinsum -= sir[0];
                    ginsum -= sir[1];
                    binsum -= sir[2];
                    //
                    yi += w;
                }
            }
            //
            bitmap.setPixels(pix, 0, w, 0, 0, w, h);
            return bitmap;
        }
        return null;
    }
}
