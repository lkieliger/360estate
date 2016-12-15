package ch.epfl.sweng.project.tests3d;

import android.util.Log;

import org.rajawali3d.math.Quaternion;

import java.lang.reflect.Field;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

public final class TestUtils {
    private static final double ERROR_EPSILON = 0.1;

    public static void assertQuaternionEquals(Quaternion q1, Quaternion q2) {
        System.out.println("Expected: " + q1);
        System.out.println("Got     : " + q2);

        assertTrue(q1.equals(q2, ERROR_EPSILON));
    }

    public static void assertQuaternionNotEquals(Quaternion q1, Quaternion q2) {
        System.out.println("Expected: " + q1);
        System.out.println("Got     : " + q2);

        assertFalse(q1.equals(q2, ERROR_EPSILON));
    }

    public static <E, S> E inject(E intoObj, S injection, String fieldName) {
        try {
            Field valueField = intoObj.getClass().getDeclaredField(fieldName);
            valueField.setAccessible(true);
            try {
                valueField.set(intoObj, injection);
            } catch (IllegalAccessException e) {
                Log.e("INJECTOR", e.getMessage());
            }
        } catch (NoSuchFieldException e) {
            Log.e("INJECTOR", e.getMessage());
        }

        return intoObj;
    }
}
