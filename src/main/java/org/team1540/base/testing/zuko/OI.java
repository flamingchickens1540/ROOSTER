package org.team1540.base.testing.zuko;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.buttons.JoystickButton;

/**
 * This class is the glue that binds the controls on the physical operator
 * interface to the commands and command groups that allow control of the robot.
 */
public class OI {

  /* ACTUAL COPILOT CONTROLS
   * button A -> spinup
   * button B -> cancel shooter
   * button X -> intake
   * button Y -> outtake
   * right bumper -> fire
   * left joystick thing -> intake arm up/down
   * right joystick thing -> portcullis arms up/down
   */

  public static double deadzone = 0.1;

  public static final Joystick driver = new Joystick(0);
  public static final Joystick copilot = new Joystick(1);

  public static final JoystickButton buttonIntake = new JoystickButton(copilot, 3);
  public static final JoystickButton buttonSpinup = new JoystickButton(copilot, 1);
  public static final JoystickButton buttonFire = new JoystickButton(copilot, 6);
  public static final JoystickButton buttonCancelShooter = new JoystickButton(copilot, 2);
  public static final JoystickButton buttonEject = new JoystickButton(copilot, 4);

  private static final int rightAxisY = 5;
  private static final int leftAxisY = 1;
  private static final int leftAxisX = 0;

  private static final int rightTrigger = 3;
  private static final int leftTrigger = 2;

  public static double getDriveLeftAxis() {
    return RobotUtil.deadzone(driver.getRawAxis(leftAxisY), 0.1);
  }

  public static double getDriveRightAxis() {
    return RobotUtil.deadzone(driver.getRawAxis(rightAxisY), 0.1);
  }

  public static double getDriveAccAxis() {
    return RobotUtil.deadzone(driver.getRawAxis(rightAxisY), 0.1);
  }

  public static double getDriveTurnAxis() {
    return RobotUtil.deadzone(driver.getRawAxis(leftAxisX), 0.1);
  }

  public static double getDriveLeftTrigger() {
    return RobotUtil.deadzone(driver.getRawAxis(leftTrigger), 0.1);
  }

  public static double getDriveRightTrigger() {
    return RobotUtil.deadzone(driver.getRawAxis(rightTrigger), 0.1);
  }

  public static double getIntakeArmAxis() {
    return copilot.getRawAxis(leftAxisY);
  }

  public static double getPortcullisArmsAxis() {
    return copilot.getRawAxis(rightAxisY);
  }

  //// CREATING BUTTONS
  // One type of button is a joystick button which is any button on a
  //// joystick.
  // You create one by telling it which joystick it's on and which button
  // number it is.
  // Joystick stick = new Joystick(port);
  // Button button = new JoystickButton(stick, buttonNumber);

  // There are a few additional built in buttons you can use. Additionally,
  // by subclassing Button you can create custom triggers and bind those to
  // commands the same as any other Button.

  //// TRIGGERING COMMANDS WITH BUTTONS
  // Once you have a button, it's trivial to bind it to a button in one of
  // three ways:

  // Start the command when the button is pressed and let it run the command
  // until it is finished as determined by it's isFinished method.
  // button.whenPressed(new ExampleCommand());

  // Run the command while the button is being held down and interrupt it once
  // the button is released.
  // button.whileHeld(new ExampleCommand());

  // Start the command when the button is released and let it run the command
  // until it is finished as determined by it's isFinished method.
  // button.whenReleased(new ExampleCommand());
}
