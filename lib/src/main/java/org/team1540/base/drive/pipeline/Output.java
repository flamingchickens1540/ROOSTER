package org.team1540.base.drive.pipeline;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Extension of a {@link Consumer} adding an additional composition method.
 *
 * {@code Output} can be used exactly like {@link Consumer} (and library functions should not take
 * {@code Outputs} as method parameters, instead using {@link Consumer}). However, library functions
 * pertaining to drive pipelines should return an {@code Output} where they would normally return a
 * {@link Consumer}.
 *
 * @param <T> The type of the output.
 */
@FunctionalInterface
public interface Output<T> extends Consumer<T> {

  /**
   * Creates a new {@code Output} that applies the provided {@link Function} to the input before
   * passing it to this {@code Output}.
   *
   * @param f The {@link Function} (or {@link Processor}) to apply.
   * @param <I> The input type of the {@link Function} (and thus the {@link Input} type of the
   * resulting {@code Output}.
   * @return A new {@code Output} as described above.
   */
  public default <I> Output<I> after(Function<I, T> f) {
    return i -> accept(f.apply(i));
  }
}
