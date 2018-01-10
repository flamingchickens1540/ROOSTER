package org.team1540.base.templates;

import edu.wpi.first.wpilibj.command.Subsystem;

/**
 * Interface for classes that should be Subsystems. Super-interface to all template classes.
 */
public interface SubsystemAttached {
  /**
   * Gets a subsystem instance related to this class. In almost all cases, the class extending this
   * interface is a subsystem anyway, and in that case the implementation will literally just be
   * "{@code return this;}". Theoretically you could have more complicated implementations but
   * there's no good reason to. You'll usually be implementing this through a sub-interface such as
   * {@link Drive} which are all for subsystem-like things anyway.
   *
   * @return A subsystem instance. The result of this method should be equal across all invocations
   *     of this method in one program. Or, in English, this should return a constant value.
   */
  public Subsystem getAttachedSubsystem();
}
