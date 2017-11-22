package org.team1540.base.adjustables;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Indicates that a field's value should be shown on the SmartDashboard. This field can be applied
 * to fields of the following types: <ul> <li>int</li> <li>double</li> <li>String</li>
 * <li>Boolean</li> <li>Solenoid</li> <li>SpeedController</li> <li>Accelerometer</li> <li>Gyro</li>
 * <li>Potentiometer</li> <li>CounterBase</li> </ul>
 */
@Retention(RUNTIME)
@Target(FIELD)
public @interface Telemetry {
  String value();
}
