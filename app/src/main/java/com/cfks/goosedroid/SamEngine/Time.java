/**
 * @Author 
 * @AIDE AIDE+
*/
package com.cfks.goosedroid.SamEngine;

public class Time {
    // 定义常量 framerate 和 deltaTime
    public static final int framerate = 120;
    public static final float deltaTime = 0.008333334f;

    // 记录时间的变量
    public static long timeStart; 
    public static float time;

    static {
        // 初始化时启动计时器
        timeStart = System.nanoTime();
        TickTime();
    }

    // 每次调用时更新 time
    public static void TickTime() {
        // 计算经过的时间，转换为秒并更新 time
        long elapsedTime = System.nanoTime() - timeStart;
        time = elapsedTime / 1_000_000_000.0f; // 将纳秒转换为秒
    }
}

