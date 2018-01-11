package org.team1540.base.adjustables;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Indicates that a field should be tunable from the SmartDashboard/Shuffleboard. Changes on the
 * SmartDashboard/Shuffleboard will be reflected in this field, and as such it's useful for fields
 * like PID tuning values.
 */
@Retention(RUNTIME)
@Target(FIELD)
public @interface Tunable {
  /**
   * Label for the entry on the SmartDashboard/Shuffleboard.
   *
   * @return the SmartDashboard/Shuffleboard label.
   */
  String value();
}
