package org.team1540.base.drive.pipeline;

import java.util.OptionalDouble;
import org.team1540.base.network.UDPTwistReceiver;

public class UDPVelocityInput extends UDPTwistReceiver implements Input<TankDriveData> {

  private final double radius;

  public UDPVelocityInput(int port, double radius) {
    super(port);
    this.radius = radius;
  }


  @Override
  public TankDriveData get() {
    double[] twist = getTwist();
    double throttle = twist[0];
    double omega = twist[1];
    double lvel = throttle - (omega * radius);
    double rvel = throttle + (omega * radius);

    return new TankDriveData(
        new DriveData(OptionalDouble.of(lvel)),
        new DriveData(OptionalDouble.of(rvel)),
        OptionalDouble.empty(), OptionalDouble.of(omega)
    );
  }
}
