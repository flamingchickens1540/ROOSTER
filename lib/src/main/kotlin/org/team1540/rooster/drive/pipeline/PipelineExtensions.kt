@file:JvmName("PipelineExtensions")

package org.team1540.rooster.drive.pipeline

import org.team1540.rooster.util.Executable
import java.util.function.Consumer
import java.util.function.Supplier

// somewhat unfortunate names
operator fun <T> (() -> T).plus(f: (T) -> Unit): () -> Unit = { f(this()) }

operator fun <T> Supplier<T>.plus(f: Consumer<T>): Executable = Executable { f.accept(this.get()) }

@JvmName("plusExecutable")
operator fun <T, R> (() -> T).plus(f: (T) -> R): () -> R = { f(this()) }

operator fun <T, R> Supplier<T>.plus(f: java.util.function.Function<T, R>): Supplier<R> = Supplier { f.apply(this.get()) }
