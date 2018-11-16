package org.team1540.rooster.drive.pipeline;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import org.team1540.rooster.util.Executable;

/**
 * Extension of a {@link Supplier} adding additional composition methods.
 *
 * {@code Input} can be used exactly like {@link Supplier} (and library functions should not take
 * {@code Outputs} as method parameters, instead using {@link Supplier}). However, library functions
 * pertaining to drive pipelines should return an {@code Input} where they would normally return a
 * {@link Supplier}.
 *
 * @param <T> The type of the input.
 */
@FunctionalInterface
public interface Input<T> extends Supplier<T> {

  /**
   * Creates a new {@code Input} that applies the provided {@link Function} to this {@code Input}'s
   * output.
   *
   * @param f The {@link Function} (or {@link Processor}) to apply.
   * @param <R> The return type of the provided {@link Function}, and thus the return type of the
   * returned {@code Input}.
   * @return A new {@code Input} as described above.
   */
  public default <R> Input<R> then(Function<T, R> f) {
    return () -> f.apply(get());
  }

  /**
   * Creates a new {@link Executable} that, when run, applies the provided {@link Consumer} (or
   * {@link Output}) to this {@code Input}'s output.
   *
   * @param c The {@link Consumer} (or {@link Output} to use.
   * @return A new {@link Executable} as described above.
   */
  public default Executable then(Consumer<T> c) {
    return () -> c.accept(get());
  }
}
