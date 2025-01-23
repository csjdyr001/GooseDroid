/**
 * @Author 
 * @AIDE AIDE+
*/
package com.cfks.goosedroid.SamEngine;


public class Vector2
 {
    // 定义 x 和 y 坐标
    public float x;
    public float y;

    // 定义零向量
    public static final Vector2 zero = new Vector2(0f, 0f);

    // 构造函数
    public Vector2(float _x, float _y) {
        this.x = _x;
        this.y = _y;
    }

    // 向量加法
    public static Vector2 add(Vector2 a, Vector2 b) {
        return new Vector2(a.x + b.x, a.y + b.y);
    }

    // 向量减法
    public static Vector2 subtract(Vector2 a, Vector2 b) {
        return new Vector2(a.x - b.x, a.y - b.y);
    }

    // 向量取反
    public static Vector2 negate(Vector2 a) {
        return new Vector2(-a.x, -a.y);
    }

    // 向量与向量的乘法
    public static Vector2 multiply(Vector2 a, Vector2 b) {
        return new Vector2(a.x * b.x, a.y * b.y);
    }

    // 向量与标量的乘法
    public static Vector2 multiply(Vector2 a, float b) {
        return new Vector2(a.x * b, a.y * b);
    }

    // 向量与标量的除法
    public static Vector2 divide(Vector2 a, float b) {
        return new Vector2(a.x / b, a.y / b);
    }

    // 通过角度获取单位向量
    public static Vector2 GetFromAngleDegrees(float angle) {
        float radians = angle * 0.0174532924f;  // 角度转弧度
        return new Vector2((float)Math.cos(radians), (float)Math.sin(radians));
    }

    // 计算两个向量的距离
    public static float Distance(Vector2 a, Vector2 b) {
        Vector2 diff = new Vector2(a.x - b.x, a.y - b.y);
        return (float)Math.sqrt(diff.x * diff.x + diff.y * diff.y);
    }

    // 线性插值
    public static Vector2 Lerp(Vector2 a, Vector2 b, float p) {
        return new Vector2(SamMath.Lerp(a.x, b.x, p), SamMath.Lerp(a.y, b.y, p));
    }

    // 计算点积
    public static float Dot(Vector2 a, Vector2 b) {
        return a.x * b.x + a.y * b.y;
    }

    // 向量归一化
    public static Vector2 Normalize(Vector2 a) {
        if (a.x == 0f && a.y == 0f) {
            return Vector2.zero;  // 防止除以零
        }
        float magnitude = (float)Math.sqrt(a.x * a.x + a.y * a.y);
        return new Vector2(a.x / magnitude, a.y / magnitude);
    }

    // 计算向量的大小（模）
    public static float Magnitude(Vector2 a) {
        return (float)Math.sqrt(a.x * a.x + a.y * a.y);
    }
}

