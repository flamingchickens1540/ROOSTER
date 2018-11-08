package org.team1540.base.testing;

import edu.wpi.first.wpilibj.Sendable;
import edu.wpi.first.wpilibj.smartdashboard.SendableBuilder;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.apache.commons.math3.stat.descriptive.rank.Median;
import org.team1540.base.wrappers.ChickenTalon;

@SuppressWarnings("unused")
public class BurnoutTester extends AbstractTester<ChickenTalon, Boolean> implements Sendable {

  private static final Median medianCalculator = new Median();
  private static final StandardDeviation stdDevCalculator = new StandardDeviation();
  private double medianPower = 0;
  private double stdDevPower = 0;

  private String name = "BurnoutTester";

  public BurnoutTester(ChickenTalon... motorsToTest) {
    super(1, Arrays.asList(motorsToTest),
        Collections.singletonList(() -> true));
    this.getTests().add(this::testBurnout);
  }

  public BurnoutTester(List<ChickenTalon> motorsToTest) {
    // Because passing in a reference to a non-static method in the constructor doesn't work.
    super(1, motorsToTest,
        Collections.singletonList(() -> true));
    this.getTests().add(this::testBurnout);
  }

  public Boolean testBurnout(ChickenTalon manageable) {
    return manageable.getOutputCurrent() < (this.medianPower - 2 * this.stdDevPower);
  }

  @Override
  void periodic() {
    double[] currents = itemsToTest.stream().mapToDouble(ChickenTalon::getOutputCurrent).toArray();
    medianPower = medianCalculator.evaluate(currents);
    stdDevPower = stdDevCalculator.evaluate(currents);
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
    // Because the method reference by itself isn't an object apparently
    Function<ChickenTalon, Boolean> lmao = this::testBurnout;
    for (ChickenTalon t : getAllQueuedResults().keySet()) {
      // Get the most recent value if present, else return false
      builder.addBooleanProperty(t.toString(),
          () -> Optional.ofNullable(getAllQueuedResults().get(t).get(lmao).peek())
              .map(ResultWithMetadata::getResult).orElse(false), null);
    }
  }
}
