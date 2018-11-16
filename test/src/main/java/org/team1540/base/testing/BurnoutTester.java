package org.team1540.base.testing;

import edu.wpi.first.wpilibj.Sendable;
import edu.wpi.first.wpilibj.smartdashboard.SendableBuilder;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.apache.commons.math3.stat.descriptive.rank.Median;
import org.team1540.base.wrappers.ChickenTalon;

@SuppressWarnings("unused")
public class BurnoutTester extends AbstractTester<ChickenTalon, Boolean> implements Sendable {

  private static final Median medianCalculator = new Median();
  private static final StandardDeviation stdDevCalculator = new StandardDeviation();
  private double medianCurrent = 0;
  private double stdDevCurrent = 0;

  private String name = "BurnoutTester";

  public BurnoutTester(ChickenTalon... motorsToTest) {
    super((stupid) -> null, Arrays.asList(motorsToTest),
        Collections.singletonList(() -> true));
    this.setTest(this::testBurnout);
  }

  public BurnoutTester(List<ChickenTalon> motorsToTest) {
    // Because passing in a reference to a non-static method in the constructor doesn't work.
    super((stupid) -> null, motorsToTest,
        Collections.singletonList(() -> true));
    this.setTest(this::testBurnout);
  }

  public Boolean testBurnout(ChickenTalon manageable) {
    return manageable.getOutputCurrent() < (this.medianPower - 2 * this.stdDevPower);
  }

  @Override
  void periodic() {
    double[] currents = itemsToTest.stream().mapToDouble(ChickenTalon::getOutputCurrent).toArray();
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

  @Override
  public void initSendable(SendableBuilder builder) {
    for (ChickenTalon t : getItemsToTest()) {
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
