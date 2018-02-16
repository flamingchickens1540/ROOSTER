
package org.team1540.base.testing.zuko;

import edu.wpi.first.wpilibj.GenericHID.RumbleType;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.team1540.base.power.PowerManager;
import org.team1540.base.testing.zuko.commands.CancelShooter;
import org.team1540.base.testing.zuko.commands.Eject;
import org.team1540.base.testing.zuko.commands.FireShooter;
import org.team1540.base.testing.zuko.commands.Intake;
import org.team1540.base.testing.zuko.commands.SpinupFlywheel;
import org.team1540.base.testing.zuko.subsystems.DriveTrain;
import org.team1540.base.testing.zuko.subsystems.IntakeArm;
import org.team1540.base.testing.zuko.subsystems.IntakeRollers;
import org.team1540.base.testing.zuko.subsystems.PortcullisArms;
import org.team1540.base.testing.zuko.subsystems.Shooter;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot {

  public static final DriveTrain driveTrain = new DriveTrain();
  public static final Shooter shooter = new Shooter();
  public static final IntakeRollers intakeRollers = new IntakeRollers();
  public static final IntakeArm intakeArm = new IntakeArm();
  public static final PortcullisArms portcullisArms = new PortcullisArms();
  public static final SendableChooser<Command> driveModeChooser = new SendableChooser<Command>();
  public static Tuning tuning;

//	private static Command autoCommand = new Command() {
//		@Override
//		protected boolean isFinished() {
//			return true;
//		}
//	};
//	private PowerDistributionPanel pdp = new PowerDistributionPanel();

  /**
   * This function is run when the robot is first started up and should be
   * used for any initialization code.
   */
  @Override
  public void robotInit() {
    tuning = new Tuning();

//    UsbCamera camera = CameraServer.getInstance().startAutomaticCapture("ze camera", 0);
//    camera.setResolution(640, 480);
//    MjpegServer mjpegServer0 = new MjpegServer("Front Server", 1181);
//    mjpegServer0.setSource(camera);

    OI.buttonIntake.whenPressed(new Intake());
    OI.buttonEject.whileHeld(new Eject());
    OI.buttonSpinup.whenPressed(new SpinupFlywheel());
    OI.buttonFire.whenPressed(new FireShooter());
    OI.buttonCancelShooter.whenPressed(new CancelShooter());

//    driveModeChooser.addDefault("Tank Drive", new TankDrive());
//    driveModeChooser.addObject("Arcade Drive", new ArcadeDrive());
    SmartDashboard.putData("Drive Mode", driveModeChooser);
    SmartDashboard.putData(PowerManager.getInstance());
    SmartDashboard.putData(Robot.driveTrain);

//		PowerManager.getInstance().setVoltageMargin(10);
//		PowerManager.getInstance().setVoltageDipLow(12);
//		PowerManager.getInstance().setCurrentSpikePeak(10);
//		PowerManager.getInstance().setCurrentTarget(10);
//		PowerManager.getInstance().setCurrentSpikeLength(0.2);
  }

  @Override
  public void disabledInit() {

  }

  @Override
  public void disabledPeriodic() {
//		Scheduler.getInstance().run();
  }

  @Override
  public void autonomousInit() {
//		autoCommand = new PositionTest();
//		Scheduler.getInstance().add(autoCommand);
//		Trajectory[] trajectories = PathfinderPlayground.getModifiedTrajectory();
//    HashSet<Properties> mps = new HashSet<>();
//		Properties lProperties = new Properties(Robot.driveTrain::getDriveLeftTalonVelocity,
//				Robot.driveTrain::setLeftVelocity, Robot.driveTrain::getDriveLeftTalonPosition,
//				trajectories[0]);
//		Properties rProperties = new Properties(Robot.driveTrain::getDriveRightTalonVelocity,
//				Robot.driveTrain::setRightVelocity, Robot.driveTrain::getDriveRightTalonPosition,
//				trajectories[1]);
//		lProperties.setEncoderTicksPerRev(1556);
//		rProperties.setEncoderTicksPerRev(1556);
//		lProperties.setWheelDiameter(.2);
//		rProperties.setWheelDiameter(.2);
//    mps.add(lProperties);
//    mps.add(rProperties);
//		Robot.driveTrain.prepareForMotionProfiling();
//		autoCommand = new MotionProfile(mps);
//		Scheduler.getInstance().add(autoCommand);
  }

  @Override
  public void autonomousPeriodic() {
//		Scheduler.getInstance().run();
//		SmartDashboard.putData(Scheduler.getInstance());
//		Robot.driveTrain.displayAutoInfo();
//		SmartDashboard.putBoolean("Is Limiting", PowerManager.getInstance().isLimiting());
  }

  @Override
  public void teleopInit() {
//		autoCommand.cancel();
    driveTrain.actuallyInitDefaultCommand();
  }

  @Override
  public void teleopPeriodic() {

    Scheduler.getInstance().run();

//		SmartDashboard.putNumber("Flywheel Current", shooter.getCurrent());
//		SmartDashboard.putNumber("Flywheel Setpoint", shooter.getSetpoint());
//		SmartDashboard.putNumber("Flywheel Speed", shooter.getSpeed());
//		SmartDashboard.putBoolean("Flywheel Up To Speed", shooter.upToSpeed(
//				tuning.getFlywheelTargetSpeed()));
//		SmartDashboard.putNumber("Intake Arm Current", intakeArm.getCurrent());
//		SmartDashboard.putBoolean("Is Limiting", PowerManager.getInstance().isLimiting());
//		SmartDashboard.putBoolean("Is Spiking/Dipping", PowerManager.getInstance().currentIsSpiking() ||
//				PowerManager.getInstance().voltageIsDipping());
//		SmartDashboard.putNumber("Battery Voltage", RobotController.getBatteryVoltage());
//		SmartDashboard.putNumber("Total Current", pdp.getTotalCurrent());

    OI.driver.setRumble(RumbleType.kRightRumble,
        shooter.upToSpeed(Robot.tuning.getFlywheelTargetSpeed()) ? 0.5 : 0);
    OI.copilot.setRumble(RumbleType.kRightRumble,
        shooter.upToSpeed(Robot.tuning.getFlywheelTargetSpeed()) ? 0.5 : 0);
  }

  @Override
  public void testPeriodic() {
    LiveWindow.run();
  }
}
