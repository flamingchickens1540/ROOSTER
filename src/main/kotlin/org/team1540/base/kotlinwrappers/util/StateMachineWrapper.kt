package org.team1540.base.kotlinwrappers.util

import org.team1540.base.util.Executable
import org.team1540.base.util.StateMachine
import java.util.*

fun <E : Enum<E>> stateMachine(initialState: E, init: StateMachineConfiguration<E>.() -> Unit): StateMachine<E> {
    val config = StateMachineConfiguration(initialState)
    config.init()
    return config.stateMachine
}

class StateMachineConfiguration<E : Enum<E>>(initialState: E) {
    val stateMachine = StateMachine<E>(initialState)

    fun state(state: E, init: StateConfiguration<E>.() -> Unit) {
        val stateConfig = StateConfiguration(state)
        stateConfig.init()
        stateConfig.addState(stateMachine)
    }
}

class StateConfiguration<E : Enum<E>>(private val state: E) {
    private var onEntry: Executable? = null
    private var onExit: Executable? = null
    private var periodic: Executable? = null

    private val transitions: MutableList<StateMachine.Transition<E>> = LinkedList()

    fun onEntry(entryFunction: () -> Unit) {
        onEntry = Executable { entryFunction() }
    }

    fun onExit(exitFunction: () -> Unit) {
        onExit = Executable { exitFunction() }
    }

    fun periodic(periodicFunction: () -> Unit) {
        periodic = Executable { periodicFunction() }
    }

    fun transition(to: E, onCondition: () -> Boolean) {
        transitions.add(StateMachine.Transition(onCondition, to))
    }

    internal fun addState(machine: StateMachine<E>) {
        machine.putState(state, onEntry, onExit, periodic, *transitions.toTypedArray())
    }
}
