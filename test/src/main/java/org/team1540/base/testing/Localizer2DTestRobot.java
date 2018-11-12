package org.team1540.base.testing;

import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.I2C.Port;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.team1540.base.local.Localizer2D;
import org.team1540.base.preferencemanager.Preference;
import org.team1540.base.preferencemanager.PreferenceManager;
import org.team1540.base.wrappers.ChickenTalon;

public class Localizer2DTestRobot extends IterativeRobot {

  private ChickenTalon right;
  private ChickenTalon rMaster;
  private AHRS ahrs;

  private Localizer2D localizer2D;

  @Preference(persistent = false)
  public double tpu = 1;

  @Override
  public void robotInit() {
    PreferenceManager.getInstance().add(this);

    ahrs = new AHRS(Port.kMXP);
    ahrs.zeroYaw();
    right = new ChickenTalon(1);
    right.setSelectedSensorPosition(0);
    right.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder);
    right.setSensorPhase(false);

    rMaster = new ChickenTalon(4);
    rMaster.setSelectedSensorPosition(0);
    rMaster.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder);
    rMaster.setSensorPhase(true);

    localizer2D = new Localizer2D(
        () -> right.getSelectedSensorPosition() / tpu,
        () -> rMaster.getSelectedSensorPosition() / tpu,
        () -> Math.toRadians(ahrs.getAngle())
    );
  }

  @Override
  public void robotPeriodic() {
    Scheduler.getInstance().run();
    localizer2D.execute();
    SmartDashboard.putNumber("Position X", localizer2D.getX());
    SmartDashboard.putNumber("Position Y", localizer2D.getY());
    SmartDashboard.putNumber("Position L", right.getSelectedSensorPosition());
    SmartDashboard.putNumber("Position R", rMaster.getSelectedSensorPosition());
  }
}
