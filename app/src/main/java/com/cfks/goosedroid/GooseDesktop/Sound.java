/**
 * @Author 
 * @AIDE AIDE+
*/
package com.cfks.goosedroid.GooseDesktop;

import android.content.*;
import android.content.res.*;
import android.media.*;
import android.util.*;
import java.io.*;
import java.util.*;
import jiesheng.*;

public class Sound {

    private static MediaPlayer honkBiteSoundPlayer;
    private static MediaPlayer musicPlayer;
    private static MediaPlayer environmentSoundsPlayer;
    private static MediaPlayer[] patSoundPool;
    private static Context context;

    // 初始化方法
    public static void Init(Context appContext) {
        context = appContext;

        // 初始化 honkBiteSoundPlayer
        honkBiteSoundPlayer = createMediaPlayerFromAssets("Sound/NotEmbedded/Honk1.mp3");

        // 初始化 patSoundPool
        String[] patSources = new String[]{
			"Sound/Pat1.mp3",
			"Sound/Pat2.mp3",
			"Sound/Pat3.mp3"
        };
        patSoundPool = new MediaPlayer[patSources.length];
        for (int i = 0; i < patSources.length; i++) {
            patSoundPool[i] = createMediaPlayerFromAssets(patSources[i]);
        }

        // 初始化环境音效播放器
        environmentSoundsPlayer = createMediaPlayerFromAssets("Sound/NotEmbedded/MudSquith.mp3");

        // 初始化音乐播放器
        String musicPath = "Sound/Music/Music.mp3";
        musicPlayer = createMediaPlayerFromAssets(musicPath);
        if (musicPlayer != null) {
            musicPlayer.setLooping(true);
            setVolume(musicPlayer, 0.5f);
            musicPlayer.start();
        }
    }

    // 播放 Pat 声音
    public static void PlayPat() {
        int num = new Random().nextInt(patSoundPool.length);
        MediaPlayer soundPlayer = patSoundPool[num];
        if (soundPlayer != null) {
            soundPlayer.seekTo(0);
            soundPlayer.start();
        }
    }

    // 播放 HONCC 声音
    public static void HONCC() {
        int num = new Random().nextInt(4);  // 假设 honkSource 中有 4 个音效
        if (honkBiteSoundPlayer != null) {
            honkBiteSoundPlayer.stop();
            honkBiteSoundPlayer.release();
        }
        honkBiteSoundPlayer = createMediaPlayerFromAssets("Sound/NotEmbedded/Honk" + (num + 1) + ".mp3");
        if (honkBiteSoundPlayer != null) {
            setVolume(honkBiteSoundPlayer, 0.8f);
            honkBiteSoundPlayer.start();
        }
    }

    // 播放 CHOMP 声音
    public static void CHOMP() {
        if (honkBiteSoundPlayer != null) {
            honkBiteSoundPlayer.stop();
            honkBiteSoundPlayer.release();
        }
        honkBiteSoundPlayer = createMediaPlayerFromAssets("Sound/NotEmbedded/BITE.mp3");
        if (honkBiteSoundPlayer != null) {
            setVolume(honkBiteSoundPlayer, 0.07f);
            honkBiteSoundPlayer.start();
        }
    }

    // 播放 MudSquith 声音
    public static void PlayMudSquith() {
        if (environmentSoundsPlayer != null) {
            environmentSoundsPlayer.seekTo(0);
            environmentSoundsPlayer.start();
        }
    }

    // 设置音量
    private static void setVolume(MediaPlayer mediaPlayer, float volume) {
        if (mediaPlayer != null) {
            mediaPlayer.setVolume(volume, volume);
        }
    }

    // 创建 MediaPlayer 从 assets 中加载音频文件
    private static MediaPlayer createMediaPlayerFromAssets(String filePath) {
        try {
            AssetFileDescriptor fd = context.getAssets().openFd(filePath);
			MediaPlayer mediaPlayer = new MediaPlayer();
			mediaPlayer.setDataSource(fd.getFileDescriptor(), fd.getStartOffset(), fd.getLength());
			mediaPlayer.prepare();
            return mediaPlayer;
        } catch (IOException e) {
            Log.e("Sound", "Error loading sound file: " + filePath, e);
			e.printStackTrace();
			//应用操作.信息框(context,e.toString());
            return null;
        }
    }
}

