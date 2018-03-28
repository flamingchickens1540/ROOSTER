package org.team1540.base.testing;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import java.util.Random;
import java.util.function.IntSupplier;
import org.team1540.base.logging.RobotDataLogger;

public class LoggerTestRobot extends IterativeRobot {

  private RobotDataLogger logger;

  @Override
  public void robotInit() {
    logger = new RobotDataLogger("logs");
    logger.setLogDuringDisabled(true);
    logger.setLogOutsideOfMatches(true);

    Random random = new Random();
    logger.addDataSource("long", random::nextLong);
    logger.addDataSource("boolean", random::nextBoolean);
    logger.addDataSource("double", random::nextDouble);
    logger.addDataSource("int", (IntSupplier) random::nextInt);
    logger.addDataSource("string", () -> "The time is " + System.currentTimeMillis());

    logger.activate();

    SmartDashboard.putData(logger);
    SmartDashboard.putBoolean("enabled", true);
  }

  @Override
  public void robotPeriodic() {
    SmartDashboard.putBoolean("running", logger.isActive());
    if (SmartDashboard.getBoolean("enabled", true)) {
      logger.activate();
    } else {
      logger.deactivate();
    }
  }
}
