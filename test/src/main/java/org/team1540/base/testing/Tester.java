package org.team1540.base.testing;

import com.google.common.collect.EvictingQueue;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public interface Tester<T, R> extends Runnable {

  @NotNull
  Function<T, R> getTest();

  void setTest(@NotNull Function<T, R> tests);

  @NotNull
  List<Supplier<Boolean>> getRunConditions();

  @NotNull
  Map<T, EvictingQueue<ResultWithMetadata<R>>> getStoredResults();

  int getUpdateDelay();

  float setUpdateDelay(int delay);

  boolean setRunning(boolean status);
}
