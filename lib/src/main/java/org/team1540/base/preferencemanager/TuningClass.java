package org.team1540.base.preferencemanager;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that all fields in a class should be placed in RobotPreferences.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface TuningClass {

  /**
   * The prefix for entries in the robot's preferences. Defaults to no prefix.
   *
   * @return The prefix.
   */
  String value() default "";
}
