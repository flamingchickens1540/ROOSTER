package org.team1540.base.testing;

import org.jetbrains.annotations.NotNull;

public class ResultWithMetadata<R> {

  @NotNull
  private final R result;
  private final long timestampMillis;

  public ResultWithMetadata(@NotNull R result, long timestampMillis) {
    this.result = result;
    this.timestampMillis = timestampMillis;
  }

  @NotNull
  public R getResult() {
    return result;
  }

  public long getTimestampMillis() {
    return timestampMillis;
  }

}
