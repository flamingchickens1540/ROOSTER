package org.team1540.base.adjustables;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Indicates that a field should be tunable from the SmartDashboard. Changes on the SmartDashboard
 * will be reflected in this field, and as such it's useful for fields like PID tuning values.
 */
@Retention(RUNTIME)
@Target(FIELD)
public @interface Tunable {
  /**
   * Label for the entry on the SmartDashboard.
   */
  String value();
}
