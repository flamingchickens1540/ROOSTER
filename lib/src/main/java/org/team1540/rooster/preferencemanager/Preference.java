package org.team1540.rooster.preferencemanager;

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
  String value() default "";

  /**
   * Whether the value should be persisted between code restarts. If {@code true}, value is saved
   * through the {@link edu.wpi.first.wpilibj.Preferences} API; if false, value is set through the
   * {@link edu.wpi.first.wpilibj.smartdashboard.SmartDashboard} API.
   * @return Whether the value should be persistent.
   */
  boolean persistent() default true;
}
