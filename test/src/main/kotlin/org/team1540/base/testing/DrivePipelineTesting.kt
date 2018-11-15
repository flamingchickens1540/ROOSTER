package org.team1540.base.testing

import com.ctre.phoenix.motorcontrol.ControlMode
import com.kauailabs.navx.frc.AHRS
import edu.wpi.first.wpilibj.*
import edu.wpi.first.wpilibj.command.Command
import edu.wpi.first.wpilibj.command.Scheduler
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard
import org.team1540.base.Utilities
import org.team1540.base.drive.pipeline.*
import org.team1540.base.preferencemanager.Preference
import org.team1540.base.preferencemanager.PreferenceManager
import org.team1540.base.util.Executable
import org.team1540.base.util.SimpleAsyncCommand
import org.team1540.base.util.SimpleCommand
import org.team1540.base.util.SimpleLoopCommand
import org.team1540.base.wrappers.ChickenTalon
import java.util.function.DoubleSupplier

abstract class DrivePipelineTestRobot : IterativeRobot() {
    protected abstract val command: Command

    override fun teleopInit() {
        command.start()
    }

    override fun robotPeriodic() {
        Scheduler.getInstance().run()
    }
}

class SimpleDrivePipelineTestRobot : DrivePipelineTestRobot() {
    override val command = SimpleLoopCommand("Drive",
            SimpleJoystickInput(Joystick(0), 1, 5, 3, 2, false, false) +
                    TalonSRXOutput(PipelineDriveTrain.left1, PipelineDriveTrain.right1)
    )
}

class AdvancedJoystickInputPipelineTestRobot : DrivePipelineTestRobot() {
    @JvmField
    @Preference(persistent = false)
    var maxVelocity = 1.0
    @JvmField
    @Preference(persistent = false)
    var trackWidth = 1.0
    @JvmField
    @Preference(persistent = false)
    var tpu = 1.0

    @JvmField
    @Preference(persistent = false)
    var p = 0.0
    @JvmField
    @Preference(persistent = false)
    var i = 0.0
    @JvmField
    @Preference(persistent = false)
    var d = 0.0
    @JvmField
    @Preference(persistent = false)
    var ramp = 0.0

    private val joystick = XboxController(0)

    override fun robotInit() {
        PreferenceManager.getInstance().add(this)
        val reset = SimpleCommand("reset", Executable {
            _command = SimpleAsyncCommand("Drive", 20, AdvancedArcadeJoystickInput(
                    maxVelocity, trackWidth,
                    DoubleSupplier { -Utilities.processDeadzone(joystick.getY(GenericHID.Hand.kLeft), 0.1) },
                    DoubleSupplier { Utilities.processDeadzone(joystick.getX(GenericHID.Hand.kRight), 0.1) },
                    DoubleSupplier {
                        (Utilities.processDeadzone(joystick.getTriggerAxis(GenericHID.Hand.kRight), 0.1)
                                - Utilities.processDeadzone(joystick.getTriggerAxis(GenericHID.Hand.kLeft), 0.1))
                    })
                    + (OpenLoopFeedForwardProcessor(1 / maxVelocity, 0.0, 0.0))
                    + UnitScaler(tpu, 0.1)
                    + (TalonSRXOutput(PipelineDriveTrain.left1, PipelineDriveTrain.right1)))

            listOf(PipelineDriveTrain.left1, PipelineDriveTrain.right1).forEach {
                it.configClosedloopRamp(ramp)
                it.config_kP(0, p)
                it.config_kI(0, i)
                it.config_kD(0, d)
                it.config_kF(0, 0.0);
            }

        }).apply {
            setRunWhenDisabled(true)
            start()
        }

        SmartDashboard.putData(reset)
    }

    private lateinit var _command: Command
    override val command get() = _command
}

@Suppress("unused")
private object PipelineDriveTrain {
    val left1 = ChickenTalon(1).apply {
        setBrake(true)
        configClosedloopRamp(0.0)
        configOpenloopRamp(0.0)
        configPeakOutputForward(1.0)
        configPeakOutputReverse(-1.0)
        enableCurrentLimit(false)
        inverted = false
        setSensorPhase(true)
    }

    private val left2 = ChickenTalon(2).apply {
        setBrake(true)
        configClosedloopRamp(0.0)
        configOpenloopRamp(0.0)
        configPeakOutputForward(1.0)
        configPeakOutputReverse(-1.0)
        enableCurrentLimit(false)
        inverted = false
        set(ControlMode.Follower, left1.deviceID.toDouble())
    }
    private val left3 = ChickenTalon(3).apply {
        setBrake(true)
        configClosedloopRamp(0.0)
        configOpenloopRamp(0.0)
        configPeakOutputForward(1.0)
        configPeakOutputReverse(-1.0)
        enableCurrentLimit(false)
        inverted = false
        set(ControlMode.Follower, left1.deviceID.toDouble())
    }

    val right1 = ChickenTalon(4).apply {
        setBrake(true)
        configClosedloopRamp(0.0)
        configOpenloopRamp(0.0)
        configPeakOutputForward(1.0)
        configPeakOutputReverse(-1.0)
        enableCurrentLimit(false)
        inverted = true
        setSensorPhase(true)
    }
    private val right2 = ChickenTalon(5).apply {
        setBrake(true)
        configClosedloopRamp(0.0)
        configOpenloopRamp(0.0)
        configPeakOutputForward(1.0)
        configPeakOutputReverse(-1.0)
        enableCurrentLimit(false)
        inverted = true
        set(ControlMode.Follower, right1.deviceID.toDouble())
    }
    private val right3 = ChickenTalon(6).apply {
        setBrake(true)
        configClosedloopRamp(0.0)
        configOpenloopRamp(0.0)
        configPeakOutputForward(1.0)
        configPeakOutputReverse(-1.0)
        enableCurrentLimit(false)
        inverted = true
        set(ControlMode.Follower, right1.deviceID.toDouble())
    }
}

private object PipelineNavx {
    val navx = AHRS(SPI.Port.kMXP)
}
