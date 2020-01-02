package org.team1540.rooster.datastructures.threed;


import org.apache.commons.math3.geometry.euclidean.threed.Rotation;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.team1540.rooster.datastructures.twod.Transform2D;
import org.team1540.rooster.datastructures.utils.RotationUtils;

public class Transform3D {

    public static final Transform3D IDENTITY = new Transform3D(Vector3D.ZERO, Rotation.IDENTITY);

    private final Vector3D position;
    private final Rotation orientation;

    public Transform3D(Vector3D position, Rotation orientation) {
        this.position = position;
        this.orientation = orientation;
    }

    public Transform3D(Vector3D position) {
        this(position, Rotation.IDENTITY);
    }

    public Transform3D(Rotation rotation) {
        this(Vector3D.ZERO, rotation);
    }

    public Transform3D(double x, double y, double z, double roll, double pitch, double yaw) {
        this(new Vector3D(x, y, z), RotationUtils.fromRPY(roll, pitch, yaw));
    }

    public Transform3D(double x, double y, double yaw) {
        this(x, y, 0, 0, 0, yaw);
    }

    public Transform2D toTransform2D() {
        return new Transform2D(this.position.getX(), this.position.getY(), RotationUtils.getRPYVec(this.orientation).getZ());
    }

    public Vector3D getPosition() {
        return position;
    }

    public Rotation getOrientation() {
        return orientation;
    }

    public Transform3D add(Transform3D other) {
        return new Transform3D(this.orientation.applyInverseTo(other.position).add(this.position), this.orientation.applyTo(other.orientation));
    }

    public Transform3D subtract(Transform3D other) {
        return add(other.negate());
    }

    public Transform3D negate() {
        return new Transform3D(orientation.applyTo(position.negate()), orientation.revert());
    }
}
