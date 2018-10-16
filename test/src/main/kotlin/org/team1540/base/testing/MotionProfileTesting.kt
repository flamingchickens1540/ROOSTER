package org.team1540.base.testing

import com.ctre.phoenix.motorcontrol.ControlMode
import com.ctre.phoenix.motorcontrol.FeedbackDevice
import com.kauailabs.navx.frc.AHRS
import edu.wpi.first.wpilibj.IterativeRobot
import edu.wpi.first.wpilibj.Joystick
import edu.wpi.first.wpilibj.SPI
import edu.wpi.first.wpilibj.command.Scheduler
import edu.wpi.first.wpilibj.command.Subsystem
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard
import jaci.pathfinder.Pathfinder
import jaci.pathfinder.Trajectory
import jaci.pathfinder.Waypoint
import jaci.pathfinder.modifiers.TankModifier
import org.team1540.base.Utilities
import org.team1540.base.motionprofiling.FollowProfile
import org.team1540.base.motionprofiling.FollowProfileFactory
import org.team1540.base.motionprofiling.MotionProfileUtils
import org.team1540.base.preferencemanager.Preference
import org.team1540.base.preferencemanager.PreferenceManager
import org.team1540.base.util.Executable
import org.team1540.base.util.SimpleLoopCommand
import org.team1540.base.wrappers.ChickenTalon
import java.util.function.DoubleSupplier
import kotlin.math.PI

private val driver = Joystick(0)

class MotionProfileTestingRobot : IterativeRobot() {
    private lateinit var factory: FollowProfileFactory

    private val navx = AHRS(SPI.Port.kMXP)

    private var command: FollowProfile? = null

    @JvmField
    @Preference("kV", persistent = false)
    var kV = 0.0

    @JvmField
    @Preference("kA", persistent = false)
    var kA = 0.0

    @JvmField
    @Preference("VIntercept", persistent = false)
    var vIntercept = 0.0

    @JvmField
    @Preference("MP Loop Freq", persistent = false)
    var loopFreqMs = 0

    @JvmField
    @Preference("MP Heading P", persistent = false)
    var hdgP = 0.0

    @JvmField
    @Preference("MP Heading I", persistent = false)
    var hdgI = 0.0

    @JvmField
    @Preference("MP Drive P", persistent = false)
    var driveP = 0.0

    @JvmField
    @Preference("MP Drive D", persistent = false)
    var driveD = 0.0

    @JvmField
    @Preference("MP Delta-T", persistent = false)
    var deltaT = 0.0

    @JvmField
    @Preference("MP Max Vel", persistent = false)
    var maxVel = 0.0

    @JvmField
    @Preference("MP Max Accel", persistent = false)
    var maxAccel = 0.0

    @JvmField
    @Preference("MP Max Jerk", persistent = false)
    var maxJerk = 0.0

    @JvmField
    @Preference("Wheelbase", persistent = false)
    var wheelbase = 0.0

    @JvmField
    @Preference("Drive TPU", persistent = false)
    var tpu = 0.0

    override fun robotInit() {
        PreferenceManager.getInstance().add(this)
        DriveTrain

        DriveTrain.brake = false
        SmartDashboard.setDefaultNumber("Test Profile X", 0.0)
        SmartDashboard.setDefaultNumber("Test Profile Y", 0.0)
        SmartDashboard.setDefaultNumber("Test Profile Angle", 0.0)

        factory = FollowProfileFactory(
                MotionProfileUtils.createSetpointConsumer(DriveTrain.left1, tpu),
                MotionProfileUtils.createSetpointConsumer(DriveTrain.left2, tpu),
                DriveTrain
        ).apply {
            velIntercept = vIntercept
            velCoeff = kV
            accelCoeff = kA
            loopFreq = loopFreqMs.toLong()
            headingP = hdgP
            headingI = hdgI
            headingSupplier = DoubleSupplier { processedHeading }
        }
    }

    override fun autonomousPeriodic() {
    }

    override fun robotPeriodic() {
        Scheduler.getInstance().run()
        if (DriveTrain.p != driveP) DriveTrain.p = driveP
        if (DriveTrain.d != driveP) DriveTrain.d = driveD
        SmartDashboard.putNumber("Raw heading", navx.yaw.toDouble())
        SmartDashboard.putNumber("Processed heading", processedHeading)
        SmartDashboard.putNumber("Heading Error", command?.follower?.getGyroError(processedHeading, command?.executionTime
                ?: 0.0) ?: 0.0)
        SmartDashboard.putNumber("Heading IAccum", command?.follower?.gyroIAccum ?: 0.0)
    }

    override fun disabledInit() {
        DriveTrain.brake = false
    }

    override fun disabledPeriodic() {
    }

    override fun teleopPeriodic() {
    }

    val processedHeading: Double
        get() {
            val yaw = Math.toRadians(navx.yaw.toDouble())
            return if (yaw < 0) PI - yaw else PI + yaw
        }

    override fun autonomousInit() {
        DriveTrain.brake = true
        DriveTrain.zero()
        factory.apply {
            velIntercept = vIntercept
            velCoeff = kV
            accelCoeff = kA
            loopFreq = loopFreqMs.toLong()
            headingP = hdgP
            headingI = hdgI
            leftSetpointConsumer = MotionProfileUtils.createSetpointConsumer(DriveTrain.left1, tpu)
            rightSetpointConsumer = MotionProfileUtils.createSetpointConsumer(DriveTrain.right1, tpu)
        }

        // generate new trajectory
        val (leftTrajectory, rightTrajectory) = TankModifier(Pathfinder.generate(arrayOf(
                Waypoint(0.0, 0.0, 0.0),
                Waypoint(
                        SmartDashboard.getNumber("Test Profile X", 0.0),
                        SmartDashboard.getNumber("Test Profile Y", 0.0),
                        SmartDashboard.getNumber("Test Profile Angle", 0.0)
                )
        ), Trajectory.Config(
                Trajectory.FitMethod.HERMITE_CUBIC,
                Trajectory.Config.SAMPLES_FAST,
                deltaT,
                maxVel,
                maxAccel,
                maxJerk
        ))).modify(wheelbase).trajectories

        command = factory.create(MotionProfileUtils.createProfile(leftTrajectory), MotionProfileUtils.createProfile(rightTrajectory))

        command?.start()
    }

    override fun teleopInit() {
        DriveTrain.brake = true
    }

}

private val TankModifier.trajectories get() = leftTrajectory to rightTrajectory

private object DriveTrain : Subsystem() {
    val left1 = ChickenTalon(1)
    val left2 = ChickenTalon(2)
    val left3 = ChickenTalon(3)

    val right1 = ChickenTalon(4)
    val right2 = ChickenTalon(5)
    val right3 = ChickenTalon(6)

    val masters = arrayOf(left1, right1)
    val lefts = arrayOf(left1, left2, left3)
    val rights = arrayOf(right1, right2, right3)

    val all = arrayOf(*lefts, *rights)

    var p = 0.0
        set(value) {
            field = value
            for (talon in masters) talon.config_kP(0, value)
        }

    var d = 0.0
        set(value) {
            field = value
            for (talon in masters) talon.config_kD(0, value)
        }

    var brake = true
        set(value) {
            field = value
            for (talon in all) talon.setBrake(value)
        }

    override fun initDefaultCommand() {
        defaultCommand = SimpleLoopCommand("Teleop Drive", Executable {
            left1.set(ControlMode.PercentOutput, Utilities.processDeadzone(driver.getRawAxis(1), 0.1))
            right1.set(ControlMode.PercentOutput, Utilities.processDeadzone(driver.getRawAxis(5), 0.1))
        }, this)
    }

    fun zero() {
        left1.setSelectedSensorPosition(0)
        right1.setSelectedSensorPosition(0)
    }

    private fun reset() {
        left1.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder)
        right1.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder)

        left1.setSensorPhase(true)

        for (talon in lefts) {
            talon.inverted = false
        }

        right1.setSensorPhase(true)

        for (talon in rights) {
            talon.inverted = true
        }

        left2.set(ControlMode.Follower, left1.deviceID.toDouble())
        left3.set(ControlMode.Follower, left1.deviceID.toDouble())

        right2.set(ControlMode.Follower, right1.deviceID.toDouble())
        right3.set(ControlMode.Follower, right1.deviceID.toDouble())

        for (talon in all) {
            talon.setBrake(brake)
        }

        for (talon in masters) {
            talon.config_kP(0, p)
            talon.config_kI(0, 0.0)
            talon.config_kD(0, d)
            talon.config_kF(0, 0.0)
            talon.config_IntegralZone(0, 0)
        }
    }

    override fun periodic() {
        SmartDashboard.putNumber("LPOS", left1.selectedSensorPosition)
        SmartDashboard.putNumber("RPOS", right1.selectedSensorPosition)
        SmartDashboard.putNumber("LVEL", left1.selectedSensorVelocity)
        SmartDashboard.putNumber("RVEL", right1.selectedSensorVelocity)
        SmartDashboard.putNumber("LTHROT", left1.motorOutputPercent)
        SmartDashboard.putNumber("RTHROT", right1.motorOutputPercent)
        SmartDashboard.putNumber("LCURR", left1.outputCurrent + left2.outputCurrent + left3.outputCurrent)
        SmartDashboard.putNumber("RCURR", right1.outputCurrent + right2.outputCurrent + right3.outputCurrent)
        SmartDashboard.putNumber("LVOLT", left1.motorOutputVoltage)
        SmartDashboard.putNumber("RVOLT", right1.motorOutputVoltage)
        SmartDashboard.putNumber("LERR", left1.closedLoopError.toDouble())
        SmartDashboard.putNumber("RERR", right1.closedLoopError.toDouble())
    }

    init {
        reset()
    }
}

