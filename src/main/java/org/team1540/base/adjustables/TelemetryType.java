package org.team1540.base.adjustables;

import edu.wpi.first.wpilibj.CounterBase;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.interfaces.Accelerometer;
import edu.wpi.first.wpilibj.interfaces.Gyro;
import edu.wpi.first.wpilibj.interfaces.Potentiometer;

enum TelemetryType {
  STRING(String.class),
  INT(Integer.TYPE),
  DOUBLE(Double.TYPE),
  BOOLEAN(Boolean.TYPE),
  //TODO: Implement more possible types
  SOLENOID(Solenoid.class),
  // DOUBLE_SOLENOID(DoubleSolenoid.class),
  SPEED_CONTROLLER(SpeedController.class),
  ACCELEROMETER(Accelerometer.class),
  GYRO(Gyro.class),
  POTENTIOMETER(Potentiometer.class),
  COUNTER(CounterBase.class);
  /*
  TALON(CANTalon.class),
  NAVX(AHRS.class);
  */

  final Class<?> cls;

  TelemetryType(Class<?> cls) {this.cls = cls;}
}
