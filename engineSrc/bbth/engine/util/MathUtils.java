package bbth.engine.util;

import java.util.Random;

import android.util.FloatMath;

public final class MathUtils {
	public static final float PI = (float)Math.PI;
	public static final float TWO_PI = 2 * PI;
	private static final Random _random = new Random();
	
	public static void resetRandom(long seed) {
		_random.setSeed(seed);
	}
	
	// random number in the range [min, max)
	public static float randInRange(float min, float max) {
		return (max - min) * _random.nextFloat() + min;
	}
	
	public static float getAngle(float x, float y, float x2, float y2) {
		return (float)Math.atan2(y2 - y, x2 - x);
	}
	
	public static float normalizeAngle(float a, float center) {
		return a - TWO_PI * FloatMath.floor((a + PI - center) / TWO_PI);
	}

	public static float getDist(float x1, float y1, float x2, float y2) {
		return FloatMath.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
	}
	
	public static float getDistSqr(float x1, float y1, float x2, float y2) {
		return (x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2);
	}
	
	public static float clamp(float min, float max, float val) {
		return Math.max(min, Math.min(val, max));
	}
}