package org.team1540.base.testing;

import java.util.Collections;
import java.util.List;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.apache.commons.math3.stat.descriptive.rank.Median;
import org.team1540.base.wrappers.ChickenTalon;

@SuppressWarnings("unused")
public class BurnoutTester extends AbstractTester<ChickenTalon, Boolean> {

  private static final Median medianCalculator = new Median();
  private static final StandardDeviation stdDevCalculator = new StandardDeviation();
  private double medianPower = 0;
  private double stdDevPower = 0;

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

}
