package org.team1540.base.drive;

import static java.lang.Math.abs;

import com.ctre.phoenix.motorcontrol.ControlMode;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Subsystem;
import org.team1540.base.Utilities;
import org.team1540.base.wrappers.ChickenController;

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

  PidDrive(Subsystem subsystem, ChickenController left, ChickenController right) {
    requires(subsystem);
    this.left = left;
    this.right = right;
  }

  public PidDrive(Subsystem subsystem, ChickenController left, ChickenController right,
      double maxVel, JoystickScaling scaling, double maxBrakePct, boolean invertLeftBrakeDirection,
      boolean invertRightBrakeDirection, double brakingStopZone, Joystick joystick, int leftAxis,
      boolean invertLeft, int rightAxis, boolean invertRight, int forwardTrigger, int backTrigger,
      double deadzone, double brakeOverrideThresh) {
    requires(subsystem);
    this.left = left;
    this.right = right;
    this.maxVel = maxVel;
    this.scaling = scaling;
    this.maxBrakePct = maxBrakePct;
    this.invertLeftBrakeDirection = invertLeftBrakeDirection;
    this.invertRightBrakeDirection = invertRightBrakeDirection;
    this.brakingStopZone = brakingStopZone;
    this.joystick = joystick;
    this.leftAxis = leftAxis;
    this.invertLeft = invertLeft;
    this.rightAxis = rightAxis;
    this.invertRight = invertRight;
    this.forwardTrigger = forwardTrigger;
    this.backTrigger = backTrigger;
    this.deadzone = deadzone;
    this.brakeOverrideThresh = brakeOverrideThresh;
  }

  @Override
  protected void initialize() {
  }

  @Override
  protected void execute() {
    // inputs
    double fwdTriggerInput = scaling.scale(
        (Utilities.processAxisDeadzone(joystick.getRawAxis(forwardTrigger), deadzone)
            - Math.copySign(deadzone, joystick.getRawAxis(forwardTrigger))
        ) * (1 / (1 - deadzone)));

    double backTriggerInput = scaling.scale(
        (Utilities.processAxisDeadzone(joystick.getRawAxis(backTrigger), deadzone)
            - Math.copySign(deadzone, joystick.getRawAxis(backTrigger))
        ) * (1 / (1 - deadzone)));

    double triggerInput = fwdTriggerInput - backTriggerInput;

    double leftInput = Utilities.invertIf(invertLeft,
        scaling.scale((Utilities.processAxisDeadzone(joystick.getRawAxis(leftAxis), deadzone)
            - Math.copySign(deadzone, joystick.getRawAxis(leftAxis))
        ) * (1 / (1 - deadzone)))
    );

    double rightInput = Utilities.invertIf(invertRight,
        scaling.scale((Utilities.processAxisDeadzone(joystick.getRawAxis(rightAxis), deadzone)
            - Math.copySign(deadzone, joystick.getRawAxis(rightAxis))
        ) * (1 / (1 - deadzone)))
    );

    double leftSetpoint = Utilities.constrain(leftInput + triggerInput, 1);
    double rightSetpoint = Utilities.constrain(rightInput + triggerInput, 1);

    doPeakOutput(left, leftSetpoint);
    doPeakOutput(right, rightSetpoint);

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

  private void doPeakOutput(ChickenController controller, double setpoint) {
    boolean stopped = abs(controller.getSelectedSensorVelocity()) < abs(brakingStopZone * maxVel);

    if (!stopped && setpoint < brakeOverrideThresh) {
      // process braking
      boolean goingForward =
          Utilities.invertIf(invertLeftBrakeDirection, left.getSelectedSensorVelocity()) > 0;

      controller.configPeakOutputForward(goingForward ? 1 : maxBrakePct);
      controller.configPeakOutputReverse(goingForward ? -maxBrakePct : -1);
    } else {
      controller.configPeakOutputForward(1);
      controller.configPeakOutputReverse(-1);
    }
  }
}
