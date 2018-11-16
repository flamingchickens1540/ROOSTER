package org.team1540.rooster.testers;

import com.google.common.collect.EvictingQueue;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public interface Tester<T, R> extends Runnable {

  @NotNull
  Function<T, R> getTest();

  void setTest(@NotNull Function<T, R> tests);

  @NotNull // TODO how to annotate as unmodifiable?
  List<Function<T, Boolean>> getRunConditions();

  /**
   * Gets the items that the tests are being applied to.
   *
   * @return An {@link EvictingQueue} of the items that are being tested.
   */
  @SuppressWarnings("UnstableApiUsage")
  @NotNull // TODO how to annotate as unmodifiable?
  List<T> getItemsToTest();

  @SuppressWarnings("UnstableApiUsage")
  @NotNull
  EvictingQueue<ResultWithMetadata<R>> getStoredResults(T key);

  @Nullable
  ResultWithMetadata<R> peekMostRecentResult(T key);

  int getUpdateDelay();

  @SuppressWarnings("UnusedReturnValue")
  int setUpdateDelay(int delay);

  boolean setRunning(boolean status);
}
