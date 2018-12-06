package org.team1540.rooster.testing

import com.ctre.phoenix.motorcontrol.ControlMode
import com.kauailabs.navx.frc.AHRS
import edu.wpi.first.wpilibj.*
import edu.wpi.first.wpilibj.command.Command
import edu.wpi.first.wpilibj.command.Scheduler
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard
import org.team1540.rooster.Utilities
import org.team1540.rooster.drive.pipeline.*
import org.team1540.rooster.functional.Executable
import org.team1540.rooster.functional.Input
import org.team1540.rooster.motionprofiling.ProfileContainer
import org.team1540.rooster.preferencemanager.Preference
import org.team1540.rooster.preferencemanager.PreferenceManager
import org.team1540.rooster.util.SimpleAsyncCommand
import org.team1540.rooster.util.SimpleCommand
import org.team1540.rooster.util.SimpleLoopCommand
import org.team1540.rooster.wrappers.ChickenTalon
import java.io.File
import java.util.OptionalDouble
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
    @JvmField
    @Preference(persistent = false)
    var revBack = false


    private val joystick = XboxController(0)

    override fun robotInit() {
        PreferenceManager.getInstance().add(this)
        val reset = SimpleCommand("reset", Executable {
            _command = SimpleAsyncCommand("Drive", 20, AdvancedArcadeJoystickInput(
                    maxVelocity, trackWidth, revBack,
                    DoubleSupplier { Utilities.scale(-Utilities.processDeadzone(joystick.getY(GenericHID.Hand.kLeft), 0.1), power) },
                    DoubleSupplier { Utilities.scale(Utilities.processDeadzone(joystick.getX(GenericHID.Hand.kRight), 0.1), power) },
                    DoubleSupplier {
                        Utilities.scale((Utilities.processDeadzone(joystick.getTriggerAxis(GenericHID.Hand.kRight), 0.1)
                                - Utilities.processDeadzone(joystick.getTriggerAxis(GenericHID.Hand.kLeft), 0.1)), power)
                    })
                    + (FeedForwardProcessor(1 / maxVelocity, 0.0, 0.0))
                    + UnitScaler(tpu, 0.1)
                    + (CTREOutput(PipelineDriveTrain.left1, PipelineDriveTrain.right1)))

            PipelineDriveTrain.masters {
                configClosedloopRamp(ramp)
                config_kP(0, p)
                config_kI(0, i)
                config_kD(0, d)
                config_kF(0, 0.0)
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

class HeadingPIDPipelineTestRobot : DrivePipelineTestRobot() {

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
    var hdgSet = 0.0
    @JvmField
    @Preference(persistent = false)
    var invertSides = false

    private var headingPIDProcessor: HeadingPIDProcessor? = null

    override fun robotInit() {
        PreferenceManager.getInstance().add(this)
        val reset = SimpleCommand("reset", Executable {
            headingPIDProcessor = HeadingPIDProcessor(p, i, d,
                    { Math.toRadians(PipelineNavx.navx.yaw.toDouble()) },
                    false, invertSides)
            _command = SimpleAsyncCommand("Drive", 20,
                    Input {
                        TankDriveData(
                                DriveData(OptionalDouble.empty()),
                                DriveData(OptionalDouble.empty()),
                                OptionalDouble.of(hdgSet),
                                OptionalDouble.empty())
                    } + headingPIDProcessor!! + (CTREOutput(PipelineDriveTrain.left1, PipelineDriveTrain.right1))
            )


        }).apply {
            setRunWhenDisabled(true)
            start()
        }

        SmartDashboard.putData(reset)
    }

    override fun robotPeriodic() {
        super.robotPeriodic()

        headingPIDProcessor?.error?.let { SmartDashboard.putNumber("Error", it) }
        headingPIDProcessor?.iAccum?.let { SmartDashboard.putNumber("Iaccum", it) }
        SmartDashboard.putNumber("hdg", Math.toRadians(PipelineNavx.navx.yaw.toDouble()))
    }

    private lateinit var _command: Command
    override val command get() = _command
}

class TurningRatePIDPipelineTestRobot : DrivePipelineTestRobot() {

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
    var turnSet = 0.0
    @JvmField
    @Preference(persistent = false)
    var invertSides = false

    private var turningRatePIDProcessor: TurningRatePIDProcessor? = null

    override fun robotInit() {
        PreferenceManager.getInstance().add(this)
        val reset = SimpleCommand("reset", Executable {
            turningRatePIDProcessor = TurningRatePIDProcessor({ Math.toRadians(PipelineNavx.navx.rate) }, p, i, d, invertSides)
            _command = SimpleAsyncCommand("Drive", 20,
                    Input {
                        TankDriveData(
                                DriveData(OptionalDouble.empty()),
                                DriveData(OptionalDouble.empty()),
                                OptionalDouble.empty(),
                                OptionalDouble.of(turnSet))
                    } + turningRatePIDProcessor!! + FeedForwardProcessor(1.0, 0.0, 0.0) + (CTREOutput(PipelineDriveTrain.left1, PipelineDriveTrain.right1))
            )


        }).apply {
            setRunWhenDisabled(true)
            start()
        }

        SmartDashboard.putData(reset)
    }

    override fun robotPeriodic() {
        super.robotPeriodic()

        turningRatePIDProcessor?.error?.let { SmartDashboard.putNumber("Error", it) }
        turningRatePIDProcessor?.iAccum?.let { SmartDashboard.putNumber("Iaccum", it) }
        SmartDashboard.putNumber("hdg", Math.toRadians(PipelineNavx.navx.yaw.toDouble()))
    }

    private lateinit var _command: Command
    override val command get() = _command
}

class MotionProfilePipelineTestRobot : DrivePipelineTestRobot() {
    @JvmField
    @Preference(persistent = false)
    var hdgP = 0.0
    @JvmField
    @Preference(persistent = false)
    var hdgI = 0.0
    @JvmField
    @Preference(persistent = false)
    var hdgD = 0.0
    @JvmField
    @Preference(persistent = false)
    var driveP = 0.0
    @JvmField
    @Preference(persistent = false)
    var driveI = 0.0
    @JvmField
    @Preference(persistent = false)
    var driveD = 0.0
    @JvmField
    @Preference(persistent = false)
    var invertSides = false
    @JvmField
    @Preference(persistent = false)
    var kV = 0.0
    @JvmField
    @Preference(persistent = false)
    var kA = 0.0
    @JvmField
    @Preference(persistent = false)
    var vIntercept = 0.0
    @JvmField
    @Preference(persistent = false)
    var profileName = ""
    @JvmField
    @Preference(persistent = false)
    var tpu = 1.0
    @JvmField
    @Preference(persistent = false)
    var ramp = 0.0;

    private var hdgPIDProcessor: HeadingPIDProcessor? = null
    private var profileInput: ProfileInput? = null
    private var hdgTgt = 0.0

    override fun robotInit() {
        PreferenceManager.getInstance().add(this)
        val reset = SimpleCommand("reset", Executable {
            val container = ProfileContainer(File("/home/lvuser/roostertest"))
            println("Loaded profiles: ${container.profileNames}")

            val profile = container[profileName]
            if (profile == null) {
                System.err.println("Motion profile not found, aborting reset")
                return@Executable
            }

            PipelineNavx.navx.zeroYaw()

            profileInput = ProfileInput(profile.left, profile.right)

            hdgPIDProcessor = HeadingPIDProcessor(hdgP, hdgI, hdgD, { Math.toRadians(PipelineNavx.navx.yaw.toDouble()) }, true, invertSides)
            _command = SimpleAsyncCommand("Drive", 20, profileInput!!
                    + FeedForwardProcessor(kV, vIntercept, kA)
                    + HeadingTransformProcessor(false) // pathfinder motprofs have headings from 0 to 2pi and we need to convert those
                    + hdgPIDProcessor!!
                    + UnitScaler(tpu, 0.1)
                    + CTREOutput(PipelineDriveTrain.left1, PipelineDriveTrain.right1)
            )

            PipelineDriveTrain.masters {
                config_kP(0, driveP)
                config_kI(0, driveI)
                config_kD(0, driveD)
                config_kF(0, 0.0)
            }
            PipelineDriveTrain.all {
                configClosedloopRamp(ramp)
            }

        }).apply {
            setRunWhenDisabled(true)
            start()
        }

        SmartDashboard.putData(reset)
    }

    private lateinit var _command: Command
    override val command get() = _command

    override fun robotPeriodic() {
        super.robotPeriodic()

        hdgPIDProcessor?.let {
            SmartDashboard.putNumber("HDG ERR", Math.toDegrees(it.error))
            SmartDashboard.putNumber("HDG IACC", it.iAccum)
        }

        profileInput?.get()?.let {
            SmartDashboard.putNumber("LPOSTGT", it.left.position.orElse(0.0))
            SmartDashboard.putNumber("LVELTGT", it.left.velocity.orElse(0.0))
            SmartDashboard.putNumber("LACCTGT", it.left.acceleration.orElse(0.0))
            SmartDashboard.putNumber("RPOSTGT", it.right.position.orElse(0.0))
            SmartDashboard.putNumber("RVELTGT", it.right.velocity.orElse(0.0))
            SmartDashboard.putNumber("RACCTGT", it.right.acceleration.orElse(0.0))
            SmartDashboard.putNumber("HTGT", Math.toDegrees(hdgTgt))
        }

        SmartDashboard.putNumber("HDG", PipelineNavx.navx.yaw.toDouble())
        SmartDashboard.putNumber("LPOS", PipelineDriveTrain.left1.selectedSensorPosition / tpu)
        SmartDashboard.putNumber("RPOS", PipelineDriveTrain.right1.selectedSensorPosition / tpu)
        SmartDashboard.putNumber("LVEL", (PipelineDriveTrain.left1.selectedSensorVelocity * 10) / tpu)
        SmartDashboard.putNumber("RVEL", (PipelineDriveTrain.right1.selectedSensorVelocity * 10) / tpu)
        SmartDashboard.putNumber("LERR", PipelineDriveTrain.left1.closedLoopError / tpu)
        SmartDashboard.putNumber("RERR", PipelineDriveTrain.right1.closedLoopError / tpu)
    }
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

    fun all(block: ChickenTalon.() -> Unit) {
        for (talon in listOf(left1, left2, left3, right1, right2, right3)) {
            talon.block()
        }
    }

    fun masters(block: ChickenTalon.() -> Unit) {
        for (talon in listOf(left1, right1)) {
            talon.block()
        }
    }
}

/**
 * Just an object to hold a NavX for pipeline testers.
 */
private object PipelineNavx {
    val navx = AHRS(SPI.Port.kMXP)
}
