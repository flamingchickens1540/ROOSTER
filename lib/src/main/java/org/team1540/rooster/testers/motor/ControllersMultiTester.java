package org.team1540.rooster.testers.motor;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.IMotorController;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.github.oxo42.stateless4j.StateMachine;
import com.github.oxo42.stateless4j.StateMachineConfig;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Command;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import org.team1540.rooster.testers.AbstractTester;
import org.team1540.rooster.testers.ResultWithMetadata;
import org.team1540.rooster.wrappers.ChickenTalon;

/**
 * Simple automated testing for motors. Add the motors with the specified test using the builder
 * methods, then run the command.
 */
public class ControllersMultiTester extends Command {

  private Timer timer = new Timer();
  private StateMachine<State, Trigger> stateMachine;
  private List<TesterAndCommand> tests = new LinkedList<>();
  private int index = 0;

  /**
   * Default constructor. Note that this class follows a builder pattern; use
   * {@link #addControllerGroup(IMotorController...)} and
   * {@link #addEncoderGroup(ChickenTalon...)} to add the motors.
   */
  public ControllersMultiTester() {
    StateMachineConfig<State, Trigger> stateMachineConfig = new StateMachineConfig<>();
    stateMachineConfig.configure(State.INITIALIZING)
        .permit(Trigger.TIME_HAS_PASSED, State.SPIN_UP);
    stateMachineConfig.configure(State.SPIN_UP)
        .permit(Trigger.TIME_HAS_PASSED, State.EXECUTING)
        .onEntry(() -> {
          for (IMotorController motor : tests.get(index).getTest().getItemsToTest()) {
            tests.get(index).getFunction().accept(motor);
          }
        });
    stateMachineConfig.configure(State.EXECUTING)
        .permit(Trigger.TIME_HAS_PASSED, State.SPIN_DOWN)
        .onEntry(() -> new Thread(tests.get(index).getTest()).start())
        .onExit(() -> tests.get(index).getTest().setRunning(false));
    stateMachineConfig.configure(State.SPIN_DOWN)
        .permit(Trigger.TIME_HAS_PASSED, State.SPIN_UP)
        .permit(Trigger.FINISHED, State.FINISHED)
        .onEntry(() -> {
          for (IMotorController motor : tests.get(index).getTest().getItemsToTest()) {
            motor.set(ControlMode.PercentOutput, 0);
          }
          index++;
        });
    this.stateMachine = new StateMachine<>(State.INITIALIZING, stateMachineConfig);
    this.stateMachine.setShouldLog(false);
  }

  /**
   * Add a group of motors to test together with the default function (disables braking and sets
   * the motors to full.) Currently uses only {@link BurnoutTester}.
   *
   * @param controllerGroup The motors to add.
   * @return this
   */
  public ControllersMultiTester addControllerGroup(IMotorController... controllerGroup) {
    addControllerGroup(this::setMotorToFull, controllerGroup);
    return this;
  }

  /**
   * Add a group of motors to test together with the specified function. Currently uses only
   * {@link BurnoutTester}.
   * @param function The function to apply before running the tests.
   * @param controllerGroup The motors to add.
   * @return this
   */
  @SuppressWarnings({"UnusedReturnValue", "WeakerAccess"})
  public ControllersMultiTester addControllerGroup(Consumer<IMotorController> function,
      IMotorController... controllerGroup) {
    tests.add(new TesterAndCommand(new BurnoutTester(controllerGroup), function));
    return this;
  }

  /**
   * Add a group of motors with encoders to test together with the default function (disables
   * braking and sets the motors to full.) Currently uses only {@link EncoderTester}.
   * @param controllerGroup The motors to add.
   * @return this
   */
  public ControllersMultiTester addEncoderGroup(ChickenTalon... controllerGroup) {
    addEncoderGroup(this::setMotorToFull, controllerGroup);
    return this;
  }

  /**
   * Add a group of motors with encoders to test together with the specified function. Currently
   * uses only {@link EncoderTester}.
   * @param function The function to apply before running the tests.
   * @param controllerGroup The motors to add.
   * @return this
   */
  @SuppressWarnings({"UnusedReturnValue", "WeakerAccess"})
  public ControllersMultiTester addEncoderGroup(Consumer<IMotorController> function,
      ChickenTalon... controllerGroup) {
    tests.add(new TesterAndCommand(new EncoderTester(controllerGroup), function));
    return this;
  }

  @Override
  protected void initialize() {
    timer.reset();
    timer.start();
  }

  @Override
  protected void execute() {
    if (stateMachine.getState().timeToComplete != null) {
      if (timer.hasPeriodPassed(stateMachine.getState().getTimeToComplete())) {
        if (index >= tests.size()) {
          stateMachine.fire(Trigger.FINISHED);
        } else {
          stateMachine.fire(Trigger.TIME_HAS_PASSED);
        }
      }
    }
  }

  @Override
  protected void end() {
    // Very optimized yes no duplicate code either
    for (TesterAndCommand testerAndCommand : tests) {
      for (IMotorController controller : testerAndCommand.getTest().getItemsToTest()) {
        int failureCount = 0;
        for (ResultWithMetadata<Boolean> result : testerAndCommand.getTest()
            .getStoredResults(controller)) {
          if (result.getResult().equals(Boolean.TRUE)) {
            failureCount++;
          }
        }
        if (failureCount > 0) {
          DriverStation
              .reportError("Motor " + controller.getDeviceID() + " reported " + failureCount +
                  " failures of type " + testerAndCommand.getTest(), false);
        }
      }
    }
    System.out.println("Finished testing");
  }

  @Override
  protected boolean isFinished() {
    return stateMachine.getState().equals(State.FINISHED);
  }

  private void setMotorToFull(IMotorController motor) {
    motor.setNeutralMode(NeutralMode.Coast);
    motor.set(ControlMode.PercentOutput, 1.0);
  }

  private enum State {
    INITIALIZING(0), SPIN_UP(0.25), EXECUTING(1), SPIN_DOWN(0), FINISHED;

    private final Double timeToComplete;

    State() {
      this.timeToComplete = null;
    }

    State(double timeToComplete) {
      this.timeToComplete = timeToComplete;
    }

    public Double getTimeToComplete() {
      return timeToComplete;
    }
  }

  private enum Trigger {
    TIME_HAS_PASSED, FINISHED
  }

  private class TesterAndCommand {

    private AbstractTester<IMotorController, Boolean> test;
    private Consumer<IMotorController> function;

    private TesterAndCommand(
        AbstractTester<IMotorController, Boolean> test,
        Consumer<IMotorController> function) {
      this.test = test;
      this.function = function;
    }

    private AbstractTester<IMotorController, Boolean> getTest() {
      return test;
    }

    private Consumer<IMotorController> getFunction() {
      return function;
    }
  }

}
