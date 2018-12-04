package org.team1540.rooster.testers.motor;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.IMotorController;
import com.github.oxo42.stateless4j.StateMachine;
import com.github.oxo42.stateless4j.StateMachineConfig;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Command;
import java.util.LinkedList;
import java.util.List;
import org.team1540.rooster.testers.AbstractTester;
import org.team1540.rooster.testers.ResultWithMetadata;
import org.team1540.rooster.wrappers.ChickenTalon;

public class ControllersMultiTester extends Command {

  private Timer timer = new Timer();
  private StateMachine<State, Trigger> stateMachine;
  private List<AbstractTester<IMotorController, Boolean>> tests = new LinkedList<>();
  private int index = 0;

  public ControllersMultiTester() {
    StateMachineConfig<State, Trigger> stateMachineConfig = new StateMachineConfig<>();
    stateMachineConfig.configure(State.SPIN_UP).permit(Trigger.TIME_HAS_PASSED, State.EXECUTING)
        .onEntry(() -> {
          for (IMotorController motor : tests.get(index).getItemsToTest()) {
            motor.set(ControlMode.PercentOutput, 1);
          }
        });
    stateMachineConfig.configure(State.EXECUTING).permit(Trigger.TIME_HAS_PASSED,
        State.SPIN_DOWN).onEntry(() -> new Thread(tests.get(0)).start())
        .onExit(() -> tests.get(0).setRunning(false));
    stateMachineConfig.configure(State.SPIN_DOWN).permit(Trigger.TIME_HAS_PASSED, State.SPIN_UP)
        .permit(Trigger.FINISHED, State.FINISHED).onEntry(() -> {
      for (IMotorController motor : tests.get(index).getItemsToTest()) {
        motor.set(ControlMode.PercentOutput, 0);
      }
      index++;
    });
    this.stateMachine = new StateMachine<>(State.SPIN_UP, stateMachineConfig);
  }

  public ControllersMultiTester addControllerGroup(IMotorController... controllerGroup) {
    tests.add(new BurnoutTester(controllerGroup));
    return this;
  }

  public ControllersMultiTester addEncoderGroup(ChickenTalon... controllerGroup) {
    tests.add(new EncoderTester(controllerGroup));
    return this;
  }

  @Override
  protected void initialize() {
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
    for (AbstractTester<IMotorController, Boolean> tester : tests) {
      for (IMotorController controller : tester.getItemsToTest()) {
        int failureCount = 0;
        for (ResultWithMetadata<Boolean> result : tester.getStoredResults(controller)) {
          if (result.getResult().equals(Boolean.TRUE)) {
            failureCount++;
          }
        }
        if (failureCount > 0) {
          DriverStation
              .reportError("Motor " + controller.getDeviceID() + " reported " + failureCount +
                  " failures of type " + tester, false);
        }
      }
    }
    System.out.println("Finished testing");
  }

  @Override
  protected boolean isFinished() {
    return stateMachine.getState().equals(State.FINISHED);
  }

  private enum State {
    SPIN_UP(0.25), EXECUTING(3), SPIN_DOWN(0.25), FINISHED;

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

}
