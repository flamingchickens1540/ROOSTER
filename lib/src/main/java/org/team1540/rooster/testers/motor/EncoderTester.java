package org.team1540.rooster.testers.motor;

import com.ctre.phoenix.motorcontrol.IMotorController;
import edu.wpi.first.wpilibj.Sendable;
import edu.wpi.first.wpilibj.smartdashboard.SendableBuilder;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.team1540.rooster.testers.AbstractTester;
import org.team1540.rooster.testers.ResultWithMetadata;

/**
 * Reports if an encoder appears to be non-functional by checking to see if a motor is running
 * and if the corresponding encoder is moving.
 */
@SuppressWarnings("unused")
public class EncoderTester extends AbstractTester<IMotorController, Boolean> implements Sendable {

  private String name = "EncoderTester";

  private double currentThreshold = 1;
  private double velocityThreshold = 5;

  /**
   * Construct a new instance with the default logTime of 150 seconds and an update delay of 500
   * ms. Equivalent to {@link EncoderTester#EncoderTester(List) EncoderTester(Arrays.asList
   * (motorsToTest))}.
   *
   * @param motorsToTest The {@link IMotorController IMotorControllers} to compare to each other.
   */
  public EncoderTester(IMotorController... motorsToTest) {
    this(Arrays.asList(motorsToTest));
  }

  /**
   * Construct a new instance with the default logTime of 150 seconds and an update delay of 500 ms.
   *
   * @param motorsToTest The {@link IMotorController IMotorControllers} to compare to each other.
   */
  @SuppressWarnings("WeakerAccess")
  public EncoderTester(List<IMotorController> motorsToTest) {
    // Because passing in a reference to a non-static method in the constructor doesn't work.
    // Test will run if the motor is drawing over 1A of current.
    super((stupid) -> null, motorsToTest, null, 150, 500);
    this.setTest(this::testEncoder);
    this.setRunConditions(
        Collections.singletonList((motor) -> (motor).getOutputCurrent() > currentThreshold));
  }

  /**
   * Tests to see if the encoder is working by checking to see if the controller is drawing more
   * than 1 amp and if the selected {@link IMotorController} is moving at a velocity of less than 5.
   *
   * @param controller The {@link IMotorController} to test for burnout.
   * @return Boolean indicating if the encoder is encoder has failed: true if it is not suspected
   * of failure, false if it is suspected of failure.
   */
  @SuppressWarnings("WeakerAccess")
  public Boolean testEncoder(IMotorController controller) {
    // Do the wrappers provide pidIdx nicely? Yes. Can we just use zero? Also probably yes.
    return !(controller.getOutputCurrent() > currentThreshold
        && controller.getSelectedSensorVelocity(0) < velocityThreshold);
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
   * Gets the threshold under which a motor will not be tested for movement. Defaults to 1A.
   *
   * @return A double representing the current in amps.
   */
  public double getCurrentThreshold() {
    return currentThreshold;
  }

  /**
   * Sets the threshold under which a motor will not be tested for movement
   *
   * @param currentThreshold A double representing the current in amps.
   */
  public void setCurrentThreshold(double currentThreshold) {
    this.currentThreshold = currentThreshold;
  }

  /**
   * Gets the encoder velocity under which an encody failure will be reported. Defaults to 5.
   *
   * @return A double representing the velocity in whatever units are set.
   */
  public double getVelocityThreshold() {
    return velocityThreshold;
  }

  /**
   * Sets the encoder velocity under which an encody failure will be reported.
   *
   * @param velocityThreshold A double representing the velocity in whatever units are set.
   */
  public void setVelocityThreshold(double velocityThreshold) {
    this.velocityThreshold = velocityThreshold;
  }

  /**
   * Displays the current status of each {@link IMotorController}.
   *
   * @param builder The {@link SendableBuilder} to use.
   */
  @Override
  public void initSendable(SendableBuilder builder) {
    for (IMotorController t : getItemsToTest()) {
      // Get the most recent value if present, else simply don't add it to the builder
      //noinspection Duplicates
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
  }
}
