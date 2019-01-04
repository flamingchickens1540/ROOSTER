package org.team1540.rooster.functional;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Extension of a {@link Function} adding additional composition methods.
 *
 * {@code Processor} can be used exactly like {@link Function} (and library functions should not
 * take {@code Processors} as method parameters, instead using {@link Function}). However, library
 * functions should return a {@code Processor} where they would normally return a {@link Function}.
 *
 * @param <T> The type of the output.
 */
@FunctionalInterface
public interface Processor<T, R> extends Function<T, R> {

  /**
   * Creates a new {@link Input} that applies this {@code Processor} to the output of the supplied
   * {@link Supplier}.
   *
   * @param i The {@link Supplier} (or {@link Input}) to process in the returned {@link Input}.
   * @return An {@link Input} as described above.
   */
  default Input<R> process(Supplier<T> i) {
    return () -> apply(i.get());
  }

  /**
   * Creates a new {@link Output} that applies this {@code Processor} to the input before passing it
   * to the provided {@link Consumer}.
   *
   * @param o The {@link Consumer} (or {@link Output}) to pass the processed results to.
   * @return A new {@link Output} as described above.
   */
  default Output<T> followedBy(Consumer<R> o) {
    return t -> o.accept(apply(t));
  }
}
