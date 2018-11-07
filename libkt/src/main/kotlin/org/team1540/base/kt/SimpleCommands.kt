package org.team1540.base.kt

import edu.wpi.first.wpilibj.command.Subsystem
import org.team1540.base.util.Executable
import org.team1540.base.util.SimpleCommand
import org.team1540.base.util.SimpleLoopCommand

fun command(name: String, vararg reqs: Subsystem, action: () -> Unit) = SimpleCommand(name, Executable(action), *reqs)

fun loopCommand(name: String, vararg reqs: Subsystem, action: () -> Unit) = SimpleLoopCommand(name, Executable(action), *reqs)
