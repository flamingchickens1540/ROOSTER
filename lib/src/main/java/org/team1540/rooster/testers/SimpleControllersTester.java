package org.team1540.rooster.testers;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.IMotorController;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Sendable;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SendableBuilder;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import java.util.NavigableMap;
import java.util.Optional;
import java.util.TreeMap;
import org.team1540.rooster.util.SimpleCommand;

/**
 * A simple command for testing a series of {@link IMotorController}s. Simply add the
 * controllers, specify control bindings or use the defaults, and you're ready! Select the motor in
 * the SmartDashboard or move next/previous with buttons and control the percent output of the
 * motor with a joystick axis.
 */
public class SimpleControllersTester extends Command implements Sendable {

  private static final int DEFAULT_JOYSTICK_ID = 0;
  private static final int DEFAULT_AXIS_ID = 5;
  private static final int DEFAULT_NEXT_BUTTON_ID = 1;
  private static final int DEFAULT_PREVIOUS_BUTTON_ID = 2;

  private Joystick joystick;
  private int axisId;

  // Is the retrieval time poor, despite the need for a lot of retrievals? Yes. Are we ignoring that
  // because the size of the map is small? Also yes.
  private NavigableMap<Integer, IMotorController> controllers = new TreeMap<>();

  // Do not manually update this value. Instead, call setCurrentController()
  private IMotorController currentController;

  private SendableChooser<Integer> controllerChooser = new SendableChooser<>();

  /**
   * Construct a new instance with the default bindings on {@link Joystick} 0. On an Xbox
   * controller, the right thumbstick y-axis controls {@link ControlMode}.PERCENT_OUTPUT, the A
   * button goes to the next {@link IMotorController}, and the B button goes to the previous
   * {@link IMotorController}.
   *
   * @param controllers The {@link IMotorController}s to test.
   */
  public SimpleControllersTester(IMotorController... controllers) {
    this(new Joystick(DEFAULT_JOYSTICK_ID), DEFAULT_AXIS_ID, DEFAULT_NEXT_BUTTON_ID,
        DEFAULT_PREVIOUS_BUTTON_ID, controllers);
  }

  /**
   * Construct a new instance with the specified bindings.
   * @param joystick The joystick port to use.
   * @param axisId The axis on the joystick to use.
   * @param nextButtonId The button to use to proceed to the next {@link IMotorController}.
   * @param previousButtonId The button to use to proceed to the nex {@link IMotorController}.
   * @param controllers The {@link IMotorController}s to test.
   */
  @SuppressWarnings("WeakerAccess")
  public SimpleControllersTester(Joystick joystick, int axisId, int nextButtonId,
      int previousButtonId,
      IMotorController... controllers) {
    this.joystick = joystick;
    this.axisId = axisId;

    // Make the buttons go the next and previous controller, or loop around if not available
    new JoystickButton(this.joystick, nextButtonId).whenPressed(new SimpleCommand("Next "
        + "controller ID",
        () -> setCurrentController(
            Optional.ofNullable(this.controllers.higherKey(this.currentController.getDeviceID()))
                .orElse(this.controllers.firstKey()))));
    new JoystickButton(this.joystick, previousButtonId).whenPressed(new SimpleCommand("Previous "
        + "controller ID",
        () -> setCurrentController(
            Optional.ofNullable(this.controllers.lowerKey(this.currentController.getDeviceID()))
                .orElse(this.controllers.lastKey()))));

    // Add a chooser that you can use to select the controller and initialize it to null,
    // corresponding with buttons should be used
    this.controllerChooser.addObject("Use buttons", null);
    for (IMotorController controller : controllers) {
      this.controllers.put(controller.getDeviceID(), controller);
      this.controllerChooser.addObject(String.valueOf(controller.getDeviceID()),
          controller.getDeviceID());
    }

    // Set the active controller to the first controller
    setCurrentController(controllers[0].getDeviceID());

  }

  /**
   * Checks every tick if the chooser is set to a controller or to use buttons, then sets the
   * output according to the value of the active joystick.
   */
  @Override
  protected void execute() {
    if (controllerChooser.getSelected() != null) {
      setCurrentController(controllerChooser.getSelected());
    }
    currentController.set(ControlMode.PercentOutput, joystick.getRawAxis(axisId));
  }

  /**
   * Updates the currently active {@link IMotorController}.
   * @param newId The ID of the new {@link IMotorController}.
   */
  @SuppressWarnings("WeakerAccess")
  public void setCurrentController(int newId) {
    if (currentController != null) {
      currentController.set(ControlMode.PercentOutput, 0);
    }
    currentController = controllers.get(newId);
  }

  /**
   * Prevents the command from finishing.
   * @return false.
   */
  @Override
  protected boolean isFinished() {
    return false;
  }

  /**
   * Gets the chooser used for selecting the current {@link IMotorController}.
   * @return A chooser.
   */
  @SuppressWarnings("WeakerAccess")
  public SendableChooser<Integer> getControllerChooser() {
    return controllerChooser;
  }

  /**
   * Adds a single property showing the current active controller.
   * @param builder The thing to add the things to.
   */
  @Override
  public void initSendable(SendableBuilder builder) {
    builder.addDoubleProperty("Controller ID", () -> this.currentController.getDeviceID(),
        (id) -> setCurrentController((int) id));
  }

  /**
   * Add all the sendables to the {@link SmartDashboard}. Includes chooser for selecting the
   * active {@link IMotorController} and information about the tester.
   */
  public void addAllSendables() {
    SmartDashboard.putData("Controller tester info", this);
    SmartDashboard.putData("Controller choose", getControllerChooser());
  }


}
