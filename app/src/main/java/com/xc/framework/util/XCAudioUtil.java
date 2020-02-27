package com.xc.framework.util;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;

/**
 * Date：2019/12/17
 * Author：ZhangXuanChen
 * Description：音频工具
 */
public class XCAudioUtil {
    Context context;
    MediaPlayer mediaPlayer;

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
        play(audioRes, false);
    }

    /**
     * Author：ZhangXuanChen
     * Time：2019/12/17 10:19
     * Description：播放
     * Param：isLoop 是否循环播放
     * Return：void
     */
    public void play(int audioRes, final boolean isLoop) {
        try {
            AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            am.setStreamVolume(AudioManager.STREAM_MUSIC, am.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
            am.setMode(AudioManager.MODE_NORMAL);
            am.setSpeakerphoneOn(true);
            //
            mediaPlayer = MediaPlayer.create(context, audioRes);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    if (isLoop) {
                        mediaPlayer.reset();
                        mediaPlayer.start();
                    } else {
                        stop();
                    }
                }
            });
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
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
