package org.team1540.base.preferencemanager;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Indicates that a field is set through the robot's preferences.
 */
@Retention(RUNTIME)
@Target(FIELD)
public @interface Preference {

  /**
   * Label for the entry on the SmartDashboard/Shuffleboard.
   *
   * @return the SmartDashboard/Shuffleboard label.
   */
  String value();
}
