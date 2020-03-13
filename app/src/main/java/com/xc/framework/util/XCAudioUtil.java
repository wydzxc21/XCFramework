package com.xc.framework.util;

import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;

/**
 * Date：2019/12/17
 * Author：ZhangXuanChen
 * Description：音频工具
 */
public class XCAudioUtil {
    Context context;
    Ringtone mRingtone;

    public XCAudioUtil(Context context) {
        this.context = context;
    }


    /**
     * Author：ZhangXuanChen
     * Time：2019/12/17 10:19
     * Description：播放
     * Return：void
     */
    public void play(int audioRes) {
        try {
            mRingtone = RingtoneManager.getRingtone(context, Uri.parse("android.resource://" + context.getPackageName() + "/" + audioRes));
            mRingtone.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Author：ZhangXuanChen
     * Time：2019/12/17 10:18
     * Description：停止
     */
    public void stop() {
        if (mRingtone != null) {
            mRingtone.stop();
            mRingtone = null;
        }
    }
}
