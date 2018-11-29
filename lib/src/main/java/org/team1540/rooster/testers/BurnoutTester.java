package org.team1540.rooster.testers;

import com.ctre.phoenix.motorcontrol.IMotorController;
import edu.wpi.first.wpilibj.Sendable;
import edu.wpi.first.wpilibj.smartdashboard.SendableBuilder;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.apache.commons.math3.stat.descriptive.rank.Median;

/**
 * Reports motor burnouts by comparing the current draw across a series of similarly-purposed
 * motors and reporting low outliers.
 */
@SuppressWarnings("unused")
public class BurnoutTester extends AbstractTester<IMotorController, Boolean> implements Sendable {


  private static final Median medianCalculator = new Median();
  private static final StandardDeviation stdDevCalculator = new StandardDeviation();
  private double medianCurrent = 0;
  private double stdDevCurrent = 0;

  private String name = "BurnoutTester";

  /**
   * Construct a new instance.
   *
   * @param motorsToTest The motors to compare to each other.
   */
  public BurnoutTester(IMotorController... motorsToTest) {
    // Because passing in a reference to a non-static method in the constructor doesn't work.
    super((stupid) -> null, Arrays.asList(motorsToTest),
        Collections.singletonList((ignore) -> true), 150, 500);
    this.setTest(this::testBurnout);
    this.setUpdateDelay(500);
  }

  /**
   * Construct a new instance.
   *
   * @param motorsToTest The motors to compare to each other.
   */
  public BurnoutTester(List<IMotorController> motorsToTest) {
    // Because passing in a reference to a non-static method in the constructor doesn't work.
    super((stupid) -> null, motorsToTest,
        Collections.singletonList((ignore) -> true), 150, 500);
    this.setTest(this::testBurnout);
  }

  /**
   * Tests to see if a motor is burned out by checking to see if it is at least one standard
   * deviation below the median.
   * @param manageable The motor to test for burnout.
   * @return Boolean indicating burnout.
   */
  @SuppressWarnings("WeakerAccess")
  public Boolean testBurnout(IMotorController manageable) {
    return manageable.getOutputCurrent() < (this.medianCurrent - 1 * this.stdDevCurrent);
  }

  /**
   * Gets the currents, calculates the median and standard deviation, then calls super.
   */
  @Override
  void periodic() {
    double[] currents = itemsToTest.stream().mapToDouble(IMotorController::getOutputCurrent)
        .toArray();
    medianCurrent = medianCalculator.evaluate(currents);
    stdDevCurrent = stdDevCalculator.evaluate(currents);
    super.periodic();
  }

  @Override
  public String getName() {
    return this.name;
  }

  @Override
  public void setName(String name) {
    this.name = name;
  }

  @Override
  public String getSubsystem() {
    return this.name;
  }

  @Override
  public void setSubsystem(String subsystem) {
    this.name = subsystem;
  }

  /**
   * Displays the current status of each motor and the median current draw.
   * @param builder The {@link SendableBuilder} to use.
   */
  @Override
  public void initSendable(SendableBuilder builder) {
    for (IMotorController t : getItemsToTest()) {
      // Get the most recent value if present, else simply don't add it to the builder
      builder.addBooleanProperty(t.getDeviceID() + "", () -> {
        // TODO probably cleaner version of this, at the least ifPresentOrElse() in Java 9
        Optional<ResultWithMetadata<Boolean>> result = Optional.ofNullable(peekMostRecentResult(t));
        if (result.isPresent()) {
          return result.get().getResult();
        } else {
          return false;
        }
      }, null);
    }
    builder.addDoubleProperty("Median current", () -> medianCurrent, null);
  }
}
