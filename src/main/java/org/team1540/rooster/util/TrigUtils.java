package org.team1540.rooster.util;

public class TrigUtils {

    public static double signedAngleError(double target, double source) {
        double diff = (target - source + Math.PI) % (Math.PI * 2) - Math.PI;
        return diff < -Math.PI ? diff + (Math.PI * 2) : diff;
    }

    public static double radiusFromArcAndAngle(double arcLength, double centralAngle) {
        return arcLength / centralAngle;
    }
}
