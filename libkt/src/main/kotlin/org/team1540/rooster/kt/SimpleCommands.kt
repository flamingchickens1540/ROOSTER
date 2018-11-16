package org.team1540.rooster.kt

import edu.wpi.first.wpilibj.command.Subsystem
import org.team1540.rooster.util.Executable
import org.team1540.rooster.util.SimpleCommand
import org.team1540.rooster.util.SimpleLoopCommand

fun command(name: String, vararg reqs: Subsystem, action: () -> Unit) = SimpleCommand(name, Executable(action), *reqs)

fun loopCommand(name: String, vararg reqs: Subsystem, action: () -> Unit) = SimpleLoopCommand(name, Executable(action), *reqs)
