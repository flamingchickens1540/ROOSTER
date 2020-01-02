package org.team1540.rooster.datastructures;


import org.team1540.rooster.datastructures.threed.Transform3D;
import org.team1540.rooster.datastructures.twod.Twist2D;

public class Odometry {

    public static final Odometry IDENTITY = new Odometry(Transform3D.IDENTITY, Twist2D.ZERO);

    private final Transform3D pose;
    private final Twist2D twist;

    public Odometry(Transform3D pose, Twist2D twist) {
        this.pose = pose;
        this.twist = twist;
    }

    public Transform3D getPose() {
        return pose;
    }

    public Twist2D getTwist() {
        return twist;
    }
}
