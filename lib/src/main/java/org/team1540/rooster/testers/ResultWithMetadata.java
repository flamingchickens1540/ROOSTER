package org.team1540.rooster.testers;

import org.jetbrains.annotations.NotNull;

/**
 * Class for encapsulating results. Includes the result and a timestamp.
 *
 * @param <R> The result type.
 */
public class ResultWithMetadata<R> {

  @NotNull
  private final R result;
  private final long timestampMillis;

  ResultWithMetadata(@NotNull R result, long timestampMillis) {
    this.result = result;
    this.timestampMillis = timestampMillis;
  }

  @SuppressWarnings("WeakerAccess")
  @NotNull
  public R getResult() {
    return result;
  }

  @SuppressWarnings("unused")
  public long getTimestampMillis() {
    return timestampMillis;
  }

}
