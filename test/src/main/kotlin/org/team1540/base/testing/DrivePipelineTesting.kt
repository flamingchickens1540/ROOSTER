package org.team1540.base.testing

import com.ctre.phoenix.motorcontrol.ControlMode
import edu.wpi.first.wpilibj.GenericHID
import edu.wpi.first.wpilibj.IterativeRobot
import edu.wpi.first.wpilibj.Joystick
import edu.wpi.first.wpilibj.XboxController
import edu.wpi.first.wpilibj.command.Scheduler
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard
import org.team1540.base.Utilities
import org.team1540.base.drive.DrivePipeline
import org.team1540.base.drive.pipeline.*
import org.team1540.base.preferencemanager.Preference
import org.team1540.base.preferencemanager.PreferenceManager
import org.team1540.base.util.Executable
import org.team1540.base.util.SimpleCommand
import org.team1540.base.wrappers.ChickenTalon
import java.util.function.DoubleSupplier
import java.util.function.Function

abstract class DrivePipelineTestRobot : IterativeRobot() {
    protected abstract val pipeline: DrivePipeline<TankDriveData, TankDriveData>

    override fun teleopPeriodic() {
        pipeline.execute()
    }

    override fun robotPeriodic() {
        Scheduler.getInstance().run()
    }
}

class SimpleDrivePipelineTestRobot : DrivePipelineTestRobot() {
    override val pipeline = DrivePipeline<TankDriveData, TankDriveData>(
            SimpleJoystickInput(Joystick(0), 1, 5, 3, 2, false, false),
            Function.identity<TankDriveData>(),
            TalonSRXOutput(PipelineDriveTrain.left1, PipelineDriveTrain.right1)
    )
}

class AdvancedJoystickInputPipelineTestRobot : DrivePipelineTestRobot() {
    @JvmField
    @Preference(persistent = false)
    var maxVelocity = 1.0

    private val joystick = XboxController(0)

    override fun robotInit() {
        PreferenceManager.getInstance().add(this)
        val reset = SimpleCommand("reset", Executable {
            _pipeline = DrivePipeline(
                    AdvancedArcadeJoystickInput(
                            maxVelocity,
                            DoubleSupplier { -Utilities.processDeadzone(joystick.getY(GenericHID.Hand.kLeft), 0.1) },
                            DoubleSupplier { Utilities.processDeadzone(joystick.getX(GenericHID.Hand.kRight), 0.1) },
                            DoubleSupplier {
                                (Utilities.processDeadzone(joystick.getTriggerAxis(GenericHID.Hand.kRight), 0.1)
                                        - Utilities.processDeadzone(joystick.getTriggerAxis(GenericHID.Hand.kLeft), 0.1))
                            }
                    ),
                    OpenLoopFeedForwardProcessor(1 / maxVelocity, 0.0, 0.0),
                    TalonSRXOutput(PipelineDriveTrain.left1, PipelineDriveTrain.right1, false)
            )
        }).apply {
            setRunWhenDisabled(true)
            start()
        }

        SmartDashboard.putData(reset)
    }

    private lateinit var _pipeline: DrivePipeline<TankDriveData, TankDriveData>
    override val pipeline get() = _pipeline
}

private object PipelineDriveTrain {
    val left1 = ChickenTalon(1).apply {
        setBrake(true)
        configClosedloopRamp(0.0)
        configOpenloopRamp(0.0)
        configPeakOutputForward(1.0)
        configPeakOutputReverse(-1.0)
        enableCurrentLimit(false)
        inverted = false
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

