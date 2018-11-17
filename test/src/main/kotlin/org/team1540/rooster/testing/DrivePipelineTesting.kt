package org.team1540.rooster.testing

import com.ctre.phoenix.motorcontrol.ControlMode
import com.kauailabs.navx.frc.AHRS
import edu.wpi.first.wpilibj.*
import edu.wpi.first.wpilibj.command.Command
import edu.wpi.first.wpilibj.command.Scheduler
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard
import org.team1540.rooster.Utilities
import org.team1540.rooster.drive.pipeline.*
import org.team1540.rooster.network.UdpPoseTwistTransmitter
import org.team1540.rooster.preferencemanager.Preference
import org.team1540.rooster.preferencemanager.PreferenceManager
import org.team1540.rooster.util.Executable
import org.team1540.rooster.util.SimpleAsyncCommand
import org.team1540.rooster.util.SimpleCommand
import org.team1540.rooster.util.SimpleLoopCommand
import org.team1540.rooster.wrappers.ChickenTalon
import java.net.InetSocketAddress
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
                    DoubleSupplier { Utilities.scale(-Utilities.processDeadzone(joystick.getY(GenericHID.Hand.kLeft), 0.1), 2.0) },
                    DoubleSupplier { Utilities.scale(Utilities.processDeadzone(joystick.getX(GenericHID.Hand.kRight), 0.1), 2.0) },
                    DoubleSupplier {
                        Utilities.scale((Utilities.processDeadzone(joystick.getTriggerAxis(GenericHID.Hand.kRight), 0.1)
                                - Utilities.processDeadzone(joystick.getTriggerAxis(GenericHID.Hand.kLeft), 0.1)), 2.0)
                    })
                    + (OpenLoopFeedForwardProcessor(1 / maxVelocity, 0.0, 0.0))
                    + UnitScaler(tpu, 0.1)
                    + (TalonSRXOutput(PipelineDriveTrain.left1, PipelineDriveTrain.right1)))

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


class UdpTebPipelineTestRobot : DrivePipelineTestRobot() {
    @JvmField
    @Preference(persistent = false)
    var radius = 1.0
    @JvmField
    @Preference(persistent = false)
    var tpu = 1.0
    @JvmField
    @Preference(persistent = false)
    var host = ""
    private var input: UDPVelocityInput? = null
    private var transmitter: UdpPoseTwistTransmitter? = null

    override fun robotInit() {
        PreferenceManager.getInstance().add(this)

        val reset = SimpleCommand("reset", Executable {
            input?.interrupt()
            input?.join()
            PipelineNavx.navx.zeroYaw()
            _command = SimpleAsyncCommand(
                    "Drive",
                    20,
                    UDPVelocityInput(5801, radius).also { input = it }
                            + UnitScaler(tpu, 10.0)
                            + TalonSRXOutput(PipelineDriveTrain.left1, PipelineDriveTrain.right1, true)
            )
            transmitter = UdpPoseTwistTransmitter(
                    { PipelineDriveTrain.left1.selectedSensorPosition / tpu },
                    { PipelineDriveTrain.right1.selectedSensorPosition / tpu },
                    { PipelineDriveTrain.left1.selectedSensorVelocity * 10 / tpu },
                    { PipelineDriveTrain.right1.selectedSensorVelocity * 10 / tpu },
                    { Math.toRadians(PipelineNavx.navx.yaw.toDouble()) },
                    radius * 2,
                    InetSocketAddress(host, 5800))
            PipelineDriveTrain.left1.setSelectedSensorPosition(0)
            PipelineDriveTrain.right1.setSelectedSensorPosition(0)
        })

        reset.setRunWhenDisabled(true)
        reset.start()
        SmartDashboard.putData(reset)
    }

    override fun robotPeriodic() {
        super.robotPeriodic()
        transmitter?.transmit()
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
