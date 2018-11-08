package org.team1540.base.util;

import edu.wpi.first.wpilibj.Sendable;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import org.team1540.base.preferencemanager.PreferenceManager;

/**
 * A collection of utilities for the {@link SmartDashboard}.
 *
 * @see SmartDashboard
 * @see PreferenceManager
 */
public class DashboardUtils {

  // should never be instantiated
  private DashboardUtils() {
  }

  /**
   * Adds a button to the {@link SmartDashboard} to run the provided code.
   * <p>
   * Internally, this method creates a {@link SimpleCommand} with the provided action and
   * requirements and adds it to the dashboard via {@link SmartDashboard#putData(String, Sendable)
   * putData()} with the provided label. As this uses the command system, calling {@code
   * Scheduler.getInstance().run()} is necessary in your main robot loop for the action to be run
   * properly. Like any other command, the action will be run in the main robot thread and thus no
   * synchronization is necessary.
   *
   * @param label The label for the entry on the {@link SmartDashboard}.
   * @param buttonAction This {@code Executable}'s {@link Executable#execute() execute()} method
   * will be called when the button is pressed.
   * @param reqs The {@link Subsystem Subsystems} to be passed into the internal command as
   * requirements.
   * @see SimpleCommand
   * @see Command
   * @throws NullPointerException If any parameters are {@code null}.
   */
  public static void addDashboardButton(@NotNull String label, @NotNull Executable buttonAction, @NotNull Subsystem... reqs) {
    for (Subsystem req : reqs) {
      Objects.requireNonNull(req);
    }
    SmartDashboard.putData(label, new SimpleCommand(Objects.requireNonNull(label), Objects.requireNonNull(buttonAction), reqs));
  }
}
