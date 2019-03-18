package org.team1540.rooster.drive;

import static java.lang.Math.abs;

import com.ctre.phoenix.motorcontrol.ControlMode;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.command.Command;
import java.util.Objects;
import org.team1540.rooster.ChickenSubsystem;
import org.team1540.rooster.util.MathUtils;
import org.team1540.rooster.util.OIUtils;
import org.team1540.rooster.wrappers.ChickenController;

public class PidDrive extends Command {

  private ChickenController left;
  private ChickenController right;

  private double maxVel;
  private JoystickScaling scaling;
  private double maxBrakePct;
  private boolean invertLeftBrakeDirection;
  private boolean invertRightBrakeDirection;
  private double brakingStopZone;

  private Joystick joystick;
  private int leftAxis;
  private boolean invertLeft;
  private int rightAxis;
  private boolean invertRight;
  private int forwardTrigger;
  private int backTrigger;
  private double deadzone;
  private double brakeOverrideThresh;
  private TriConsumer<ChickenController, Double, Double> configPeakOutput;

  PidDrive(PidDriveConfiguration pidDriveConfiguration) {
    requires(pidDriveConfiguration.subsystem);
    // If the subsystem is a ChickenSubsystem, use setRelativePercentOutputLimit
    // If not, use the good old configPeakOutput

    configPeakOutput = pidDriveConfiguration.subsystem instanceof ChickenSubsystem ?
        (motor, forward, reverse) -> {
          ((ChickenSubsystem) pidDriveConfiguration.subsystem)
              .setAbsolutePeakOutputCeilingForward(motor, forward);
          ((ChickenSubsystem) pidDriveConfiguration.subsystem)
              .setAbsolutePeakOutputCeilingReverse(motor, reverse);
        }
        : (motor, forward, reverse) -> {
          motor.configPeakOutputForward(forward);
          motor.configPeakOutputReverse(-Math.abs(reverse));
        };

    this.left = pidDriveConfiguration.left;
    this.right = pidDriveConfiguration.right;
    this.maxVel = pidDriveConfiguration.maxVel;
    this.scaling = pidDriveConfiguration.scaling;
    this.maxBrakePct = pidDriveConfiguration.maxBrakePct;
    this.invertLeftBrakeDirection = pidDriveConfiguration.invertLeftBrakeDirection;
    this.invertRightBrakeDirection = pidDriveConfiguration.invertRightBrakeDirection;
    this.brakingStopZone = pidDriveConfiguration.brakingStopZone;
    this.joystick = pidDriveConfiguration.joystick;
    this.leftAxis = pidDriveConfiguration.leftAxis;
    this.invertLeft = pidDriveConfiguration.invertLeft;
    this.rightAxis = pidDriveConfiguration.rightAxis;
    this.invertRight = pidDriveConfiguration.invertRight;
    this.forwardTrigger = pidDriveConfiguration.forwardTrigger;
    this.backTrigger = pidDriveConfiguration.backTrigger;
    this.deadzone = pidDriveConfiguration.deadzone;
    this.brakeOverrideThresh = pidDriveConfiguration.brakeOverrideThresh;
  }

  @Override
  protected void initialize() {
  }

  @Override
  protected void execute() {
    // inputs
    double fwdTriggerInput = OIUtils
        .processDeadzone(joystick.getRawAxis(forwardTrigger), deadzone);

    double backTriggerInput = OIUtils.processDeadzone(joystick.getRawAxis(backTrigger), deadzone);

    double triggerInput = fwdTriggerInput - backTriggerInput;

    double leftInput = MathUtils
        .negateDoubleIf(invertLeft, OIUtils.processDeadzone(joystick.getRawAxis(leftAxis), deadzone));

    double rightInput = MathUtils
        .negateDoubleIf(invertRight, OIUtils.processDeadzone(joystick.getRawAxis(rightAxis), deadzone));

    double leftSetpoint = MathUtils.constrain(scaling.scale(leftInput + triggerInput), 1);
    double rightSetpoint = MathUtils.constrain(scaling.scale(rightInput + triggerInput), 1);

    doPeakOutput(left, leftSetpoint, invertLeftBrakeDirection);
    doPeakOutput(right, rightSetpoint, invertRightBrakeDirection);

    left.set(ControlMode.Velocity, leftSetpoint * maxVel);
    right.set(ControlMode.Velocity, rightSetpoint * maxVel);
  }

  @Override
  protected void end() {
  }

  @Override
  protected boolean isFinished() {
    return false;
  }

  public ChickenController getLeft() {
    return left;
  }

  public void setLeft(ChickenController left) {
    this.left = left;
  }

  public ChickenController getRight() {
    return right;
  }

  public void setRight(ChickenController right) {
    this.right = right;
  }

  public double getMaxVel() {
    return maxVel;
  }

  public void setMaxVel(double maxVel) {
    this.maxVel = maxVel;
  }

  public JoystickScaling getScaling() {
    return scaling;
  }

  public void setScaling(JoystickScaling scaling) {
    this.scaling = scaling;
  }

  public double getMaxBrakePct() {
    return maxBrakePct;
  }

  public void setMaxBrakePct(double maxBrakePct) {
    this.maxBrakePct = maxBrakePct;
  }

  public boolean isInvertLeftBrakeDirection() {
    return invertLeftBrakeDirection;
  }

  public void setInvertLeftBrakeDirection(boolean invertLeftBrakeDirection) {
    this.invertLeftBrakeDirection = invertLeftBrakeDirection;
  }

  public boolean isInvertRightBrakeDirection() {
    return invertRightBrakeDirection;
  }

  public void setInvertRightBrakeDirection(boolean invertRightBrakeDirection) {
    this.invertRightBrakeDirection = invertRightBrakeDirection;
  }

  public double getBrakingStopZone() {
    return brakingStopZone;
  }

  public void setBrakingStopZone(double brakingStopZone) {
    this.brakingStopZone = brakingStopZone;
  }

  public Joystick getJoystick() {
    return joystick;
  }

  public void setJoystick(Joystick joystick) {
    this.joystick = joystick;
  }

  public int getLeftAxis() {
    return leftAxis;
  }

  public void setLeftAxis(int leftAxis) {
    this.leftAxis = leftAxis;
  }

  public boolean isInvertLeft() {
    return invertLeft;
  }

  public void setInvertLeft(boolean invertLeft) {
    this.invertLeft = invertLeft;
  }

  public int getRightAxis() {
    return rightAxis;
  }

  public void setRightAxis(int rightAxis) {
    this.rightAxis = rightAxis;
  }

  public boolean isInvertRight() {
    return invertRight;
  }

  public void setInvertRight(boolean invertRight) {
    this.invertRight = invertRight;
  }

  public int getForwardTrigger() {
    return forwardTrigger;
  }

  public void setForwardTrigger(int forwardTrigger) {
    this.forwardTrigger = forwardTrigger;
  }

  public int getBackTrigger() {
    return backTrigger;
  }

  public void setBackTrigger(int backTrigger) {
    this.backTrigger = backTrigger;
  }

  public double getDeadzone() {
    return deadzone;
  }

  public void setDeadzone(double deadzone) {
    this.deadzone = deadzone;
  }

  public double getBrakeOverrideThresh() {
    return brakeOverrideThresh;
  }

  public void setBrakeOverrideThresh(double brakeOverrideThresh) {
    this.brakeOverrideThresh = brakeOverrideThresh;
  }

  private void doPeakOutput(ChickenController controller, double setpoint, boolean invertBrake) {
    boolean stopped = abs(controller.getSelectedSensorVelocity()) < abs(brakingStopZone * maxVel);

    if (!stopped && setpoint < brakeOverrideThresh) {
      // process braking
      boolean goingForward =
          MathUtils.negateDoubleIf(invertBrake, controller.getSelectedSensorVelocity()) > 0;
      configPeakOutput.accept(controller, goingForward ? 1 : maxBrakePct, goingForward ?
          -maxBrakePct : -1);
    } else {
      configPeakOutput.accept(controller, 1.0, -1.0);
    }
  }

  @FunctionalInterface
  public interface TriConsumer<T, U, V> {

    void accept(T t, U u, V v);

    default TriConsumer<T, U, V> andThen(
        TriConsumer<? super T, ? super U, ? super V> after) {
      Objects.requireNonNull(after);
      return (a, b, c) -> {
        accept(a, b, c);
        after.accept(a, b, c);
      };
    }
  }
}
