package org.team1540.rooster.datastructures.utils;

public class UnitsUtils {

    private static final double INCHES_PER_METER = 39.3701;

    public static double inchesToMeters(double inches) {
        return inches / INCHES_PER_METER;
    }

    public static double metersToInches(double meters) {
        return meters * INCHES_PER_METER;
    }
}
