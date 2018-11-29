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

  // Do not manually update either of these values. Instead, call setCurrentController()
  private IMotorController currentController;

  private SendableChooser<Integer> controllerChooser = new SendableChooser<>();

  public SimpleControllersTester(IMotorController... controllers) {
    this(new Joystick(DEFAULT_JOYSTICK_ID), DEFAULT_AXIS_ID, DEFAULT_NEXT_BUTTON_ID,
        DEFAULT_PREVIOUS_BUTTON_ID, controllers);
  }

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

  @Override
  protected void execute() {
    if (controllerChooser.getSelected() != null) {
      setCurrentController(controllerChooser.getSelected());
    }
    currentController.set(ControlMode.PercentOutput, joystick.getRawAxis(axisId));
  }

  @SuppressWarnings("WeakerAccess")
  public void setCurrentController(int newId) {
    if (currentController != null) {
      currentController.set(ControlMode.PercentOutput, 0);
    }
    currentController = controllers.get(newId);
  }

  @Override
  protected boolean isFinished() {
    return false;
  }

  @SuppressWarnings("WeakerAccess")
  public SendableChooser<Integer> getControllerChooser() {
    return controllerChooser;
  }

  @Override
  public void initSendable(SendableBuilder builder) {
    builder.addDoubleProperty("Controller ID", () -> this.currentController.getDeviceID(),
        (id) -> setCurrentController((int) id));
  }

  public void addAllSendables() {
    SmartDashboard.putData("ya", this);
    SmartDashboard.putData("yeet", getControllerChooser());
  }


}
