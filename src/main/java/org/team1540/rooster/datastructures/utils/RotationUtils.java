package org.team1540.rooster.datastructures.utils;

import org.apache.commons.math3.geometry.euclidean.threed.Rotation;
import org.apache.commons.math3.geometry.euclidean.threed.RotationConvention;
import org.apache.commons.math3.geometry.euclidean.threed.RotationOrder;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

public class RotationUtils {

    public static final RotationOrder ROTATION_ORDER = RotationOrder.XYZ;
    public static final RotationConvention ROTATION_CONVENTION = RotationConvention.FRAME_TRANSFORM;

    public static Vector3D getRPYVec(Rotation rot) {
        double[] angles = rot.getAngles(ROTATION_ORDER, ROTATION_CONVENTION);
        return new Vector3D(angles[0], angles[1], angles[2]);
    }

    public static Rotation fromRPY(double roll, double pitch, double yaw) {
        return new Rotation(ROTATION_ORDER, ROTATION_CONVENTION, roll, pitch, yaw);
    }
}
