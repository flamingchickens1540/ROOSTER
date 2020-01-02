package org.team1540.rooster.datastructures.twod;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;

/**
 * 2D twist data structure class
 */
public class Twist2D {

    public static final Twist2D ZERO = new Twist2D(0, 0, 0);

    private final double x;
    private final double y;
    private final double omega;

    /**
     * @param x Linear velocity in X direction in meters per second
     * @param y Linear velocity in Y direction in meters per second
     * @param omega Angular velocity counter-clockwise in radians per second
     */
    public Twist2D(double x, double y, double omega) {
        this.x = x;
        this.y = y;
        this.omega = omega;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getOmega() {
        return omega;
    }

    public void putToNetworkTable(String networkTablesPath) {
        NetworkTable table = NetworkTableInstance.getDefault().getTable(networkTablesPath);
        table.getEntry("velocity/x").setNumber(getX());
        table.getEntry("velocity/y").setNumber(getY());
        table.getEntry("angular/z").setNumber(getOmega());
    }
}
