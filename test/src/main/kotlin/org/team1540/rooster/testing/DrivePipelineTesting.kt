package org.team1540.rooster.testing

import com.ctre.phoenix.motorcontrol.ControlMode
import com.kauailabs.navx.frc.AHRS
import edu.wpi.first.wpilibj.*
import edu.wpi.first.wpilibj.command.Command
import edu.wpi.first.wpilibj.command.Scheduler
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard
import org.team1540.rooster.Utilities
import org.team1540.rooster.drive.pipeline.*
import org.team1540.rooster.preferencemanager.Preference
import org.team1540.rooster.preferencemanager.PreferenceManager
import org.team1540.rooster.util.Executable
import org.team1540.rooster.util.SimpleAsyncCommand
import org.team1540.rooster.util.SimpleCommand
import org.team1540.rooster.util.SimpleLoopCommand
import org.team1540.rooster.wrappers.ChickenTalon
import java.util.function.DoubleSupplier

/**
 * Base class that all other testing classes inherit from; just has a command that gets started when
 * teleop starts.
 */
abstract class DrivePipelineTestRobot : IterativeRobot() {
    protected abstract val command: Command

    override fun teleopInit() {
        command.start()
    }

    override fun robotPeriodic() {
        Scheduler.getInstance().run()
    }
}

/**
 * just to test that everything's sane; joystick tank drive
 * */
class SimpleDrivePipelineTestRobot : DrivePipelineTestRobot() {
    override val command = SimpleLoopCommand("Drive",
            SimpleJoystickInput(Joystick(0), 1, 5, 3, 2, false, false) +
                    CTREOutput(PipelineDriveTrain.left1, PipelineDriveTrain.right1)
    )
}

/**
 * Testing class for [AdvancedArcadeJoystickInput].
 */
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
    var power = 0.0

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
                    DoubleSupplier { Utilities.scale(-Utilities.processDeadzone(joystick.getY(GenericHID.Hand.kLeft), 0.1), power) },
                    DoubleSupplier { Utilities.scale(Utilities.processDeadzone(joystick.getX(GenericHID.Hand.kRight), 0.1), power) },
                    DoubleSupplier {
                        Utilities.scale((Utilities.processDeadzone(joystick.getTriggerAxis(GenericHID.Hand.kRight), 0.1)
                                - Utilities.processDeadzone(joystick.getTriggerAxis(GenericHID.Hand.kLeft), 0.1)), power)
                    })
                    + (FeedForwardProcessor(1 / maxVelocity, 0.0, 0.0))
                    + UnitScaler(tpu, 0.1)
                    + (CTREOutput(PipelineDriveTrain.left1, PipelineDriveTrain.right1)))

            listOf(PipelineDriveTrain.left1, PipelineDriveTrain.right1).forEach {
                it.configClosedloopRamp(ramp)
                it.config_kP(0, p)
                it.config_kI(0, i)
                it.config_kD(0, d)
                it.config_kF(0, 0.0)
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

/**
 * Common drive train object to be used by all pipeline test robots.
 */
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

/**
 * Just an object to hold a NavX for pipeline testers.
 */
private object PipelineNavx {
    val navx = AHRS(SPI.Port.kMXP)
}
