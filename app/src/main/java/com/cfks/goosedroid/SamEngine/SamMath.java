/**
 * @Author 
 * @AIDE AIDE+
*/
package com.cfks.goosedroid.SamEngine;
import java.util.*;

public class SamMath {

    // Constants
    public static final float Deg2Rad = 0.0174532924f;
    public static final float Rad2Deg = 57.2957764f;

    // Random object
    public static Random Rand = new Random();

    // RandomRange function equivalent to C# version
    public static float RandomRange(float min, float max) {
        return min + Rand.nextFloat() * (max - min);
    }

    // Lerp function equivalent to C# version
    public static float Lerp(float a, float b, float p) {
        return a * (1f - p) + b * p;
    }

    // Clamp function equivalent to C# version
    public static float Clamp(float a, float min, float max) {
        return Math.min(Math.max(a, min), max);
    }
}

