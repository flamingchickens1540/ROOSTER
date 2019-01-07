package org.team1540.rooster.testers.motor;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import edu.wpi.first.wpilibj.Sendable;
import edu.wpi.first.wpilibj.smartdashboard.SendableBuilder;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.apache.commons.math3.stat.descriptive.rank.Median;
import org.team1540.rooster.testers.AbstractTester;
import org.team1540.rooster.testers.ResultWithMetadata;

/**
 * Reports motor burnouts by comparing the current draw across a series of similarly-purposed
 * motors and reporting low outliers or checking if a single motor is below the cutoff.
 */
@SuppressWarnings("unused")
public class BurnoutTester extends AbstractTester<TalonSRX, Boolean> implements Sendable {

  private static final Median medianCalculator = new Median();
  private static final StandardDeviation stdDevCalculator = new StandardDeviation();
  private double medianCurrent = 0;
  private double stdDevCurrent = 0;

  private double currentCutoff = 1;
  private double percentOutputCutoff = 0.5;

  private String name = "BurnoutTester";

  /**
   * Construct a new instance with the default logTime of 150 seconds and an update delay of 500
   * ms, using the {@link BurnoutTester#testBurnoutMultiMotor(TalonSRX)} if there is more
   * than one motor and {@link BurnoutTester#testBurnoutSingleMotor(TalonSRX)} if there is
   * one or few motors. Equivalent to {@link BurnoutTester#BurnoutTester(List) EncoderTester
   * (Arrays.asList(motorsToTest))}.
   *
   * @param motorsToTest The motors to compare to each other.
   */
  public BurnoutTester(TalonSRX... motorsToTest) {
    this(Arrays.asList(motorsToTest));
  }

  /**
   * Construct a new instance with the default logTime of 150 seconds and an update delay of 500
   * ms, using the {@link BurnoutTester#testBurnoutMultiMotor(TalonSRX)} if there are more
   * than two motor sand {@link BurnoutTester#testBurnoutSingleMotor(TalonSRX)} if there two
   * two or few motors.
   *
   * @param motorsToTest The motors to compare to each other.
   */
  @SuppressWarnings("WeakerAccess")
  public BurnoutTester(List<TalonSRX> motorsToTest) {
    // Because passing in a reference to a non-static method in the constructor doesn't work.
    super((stupid) -> null, motorsToTest, null, 150, 500);
    this.setTest(motorsToTest.size() > 2 ? this::testBurnoutMultiMotor :
        this::testBurnoutSingleMotor);
  }

  /**
   * Tests to see if a motor is burned out by checking to see if the current being drawn is at
   * least one standard deviation below the median.
   * @param controller The motor to test for burnout.
   * @return Boolean indicating burnout.
   */
  @SuppressWarnings("WeakerAccess")
  public boolean testBurnoutMultiMotor(TalonSRX controller) {
    return controller.getOutputCurrent() < (this.medianCurrent - 1 * this.stdDevCurrent);
  }

  /**
   * Test to see if a motor is burned out by checking to see if the current being drawn is below
   * the current cutoff.
   *
   * @param controller The motor to test for burnout.
   * @return Boolean indicating burnout.
   */
  @SuppressWarnings("WeakerAccess")
  public boolean testBurnoutSingleMotor(TalonSRX controller) {
    return controller.getMotorOutputPercent() > percentOutputCutoff
        && controller.getOutputCurrent() < currentCutoff;
  }

  /**
   * Gets the currents, calculates the median and standard deviation, then calls super.
   */
  @Override
  protected void periodic() {
    double[] currents = getItemsToTest().stream().mapToDouble(TalonSRX::getOutputCurrent)
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
   * Gets the current cutoff. Defaults to 1A.
   *
   * @return The current cutoff in amps.
   */
  public double getCurrentCutoff() {
    return currentCutoff;
  }

  /**
   * Sets the current cutoff.
   *
   * @param currentCutoff The current cutoff in amps.
   */
  public void setCurrentCutoff(double currentCutoff) {
    this.currentCutoff = currentCutoff;
  }

  /**
   * Gets the percent output cutoff. Defaults to 50%.
   *
   * @return The percent output cutoff as a percentage.
   */
  public double getPercentOutputCutoff() {
    return percentOutputCutoff;
  }

  /**
   * Sets the percent output cutoff.
   *
   * @param percentOutputCutoff The output cutoff as a percentage.
   */
  public void setPercentOutputCutoff(double percentOutputCutoff) {
    this.percentOutputCutoff = percentOutputCutoff;
  }

  /**
   * Displays the current status of each motor and the median current draw.
   * @param builder The {@link SendableBuilder} to use.
   */
  @Override
  public void initSendable(SendableBuilder builder) {
    for (TalonSRX t : getItemsToTest()) {
      // Get the most recent value if present, else simply don't add it to the builder
      builder.addBooleanProperty(t.getDeviceID() + "",
          () -> Optional.ofNullable(peekMostRecentResult(t))
              .map(ResultWithMetadata::getResult).orElse(false), null);
    }
    builder.addDoubleProperty("Median current", () -> medianCurrent, null);
  }
}
