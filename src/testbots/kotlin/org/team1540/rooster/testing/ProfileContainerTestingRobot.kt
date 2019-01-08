package org.team1540.rooster.testing

import edu.wpi.first.wpilibj.TimedRobot
import org.team1540.rooster.motionprofiling.PathfinderProfileContainer
import org.team1540.rooster.motionprofiling.ProfileContainer
import java.io.File
import kotlin.system.measureTimeMillis

class ProfileContainerTestingRobot : TimedRobot() {
    private lateinit var container: ProfileContainer

    override fun robotInit() {
        val time = measureTimeMillis {
            container = PathfinderProfileContainer(File("/home/lvuser/roostertest"))
        }
        println("Initialized profile container in $time ms")
        println("Current profiles loaded: ${container.profileNames}")
    }
}
