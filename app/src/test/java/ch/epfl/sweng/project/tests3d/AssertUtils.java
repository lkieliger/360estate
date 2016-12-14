package ch.epfl.sweng.project.tests3d;

import org.rajawali3d.math.Quaternion;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

public final class AssertUtils {
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
}
