package org.team1540.rooster.datastructures.twod;


import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.team1540.rooster.datastructures.threed.Transform3D;

/**
 * 2D pose data structure class
 */
public class Transform2D {

    public static final Transform2D ZERO = new Transform2D(0, 0, 0);

    private final double x;
    private final double y;
    private final double theta;

    /**
     * @param x Distance in meters in the X direction
     * @param y Distance in meters in the Y direction
     * @param theta Angle in radians between -PI and PI
     */
    public Transform2D(double x, double y, double theta) {
        this.x = x;
        this.y = y;
        this.theta = theta;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getTheta() {
        return theta;
    }

    public Vector2D getPositionVector() {
        return new Vector2D(x, y);
    }

    public Transform3D toTransform3D() {
        return new Transform3D(x, y, theta);
    }

    public Transform2D add(Transform2D other) {
        Transform3D thisTransform3D = this.toTransform3D();
        Transform3D otherTransform3D = other.toTransform3D();
        Transform3D transform3D = new Transform3D(thisTransform3D.getOrientation().applyInverseTo(otherTransform3D.getPosition()).add(thisTransform3D.getPosition()),
            thisTransform3D.getOrientation().applyTo(otherTransform3D.getOrientation()));
        return transform3D.toTransform2D();
    }

    public Transform2D subtract(Transform2D other) {
        Transform3D thisTransform3D = this.toTransform3D();
        Transform3D otherTransform3D = other.toTransform3D();
        Transform3D transform3D = new Transform3D(thisTransform3D.getPosition().subtract(thisTransform3D.getOrientation().applyInverseTo(otherTransform3D.getPosition())),
            thisTransform3D.getOrientation().applyInverseTo(otherTransform3D.getOrientation()));
        return transform3D.toTransform2D();
    }

    public void putToNetworkTable(String networkTablesPath) {
        NetworkTable table = NetworkTableInstance.getDefault().getTable(networkTablesPath);
        table.getEntry("position/x").setNumber(getX());
        table.getEntry("position/y").setNumber(getY());
        table.getEntry("orientation/z").setNumber(getTheta());
    }
}
