package org.team1540.rooster.util;

import edu.wpi.first.wpilibj.GyroBase;

/**
 * Mock gyro with user-controllable angle. Useful for displaying an angle measure on Shuffleboard.
 * Displays whatever angle (in degrees) is passed into {@link #setAngle(double)}.
 */
public class FakeGyro extends GyroBase {

    private double angle = 0;

    @Override
    public void calibrate() {

    }

    @Override
    public void reset() {

    }

    public void setAngle(double angle) {
        this.angle = angle;
    }

    @Override
    public double getAngle() {
        return angle;
    }

    @Override
    public double getRate() {
        return 0;
    }

    @Override
    public void close() {

    }
}
