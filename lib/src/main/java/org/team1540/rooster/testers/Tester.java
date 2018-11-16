package org.team1540.rooster.testers;

import com.google.common.collect.EvictingQueue;
import java.util.List;
import java.util.function.Function;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * An interface for testing things.
 *
 * @param <T> The type of item being tested.
 * @param <R> The return type of the test results.
 */
@SuppressWarnings("unused")
public interface Tester<T, R> extends Runnable {

  @NotNull
  Function<T, R> getTest();

  void setTest(@NotNull Function<T, R> tests);

  /**
   * Gets the conditions that must be met before the test will be executed on an item.
   *
   * @return An {@link EvictingQueue} of the run conditions.
   */
  @SuppressWarnings("UnstableApiUsage")
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

  /**
   * Get the results of the test being run.
   *
   * @param key The item to get the results for.
   * @return An {@link EvictingQueue} of {@link ResultWithMetadata} that encapsulate the returned
   * values.
   */
  @SuppressWarnings("UnstableApiUsage")
  @NotNull
  EvictingQueue<ResultWithMetadata<R>> getStoredResults(T key);

  /**
   * Gets the most recent result of the the test without impacting the stored results.
   *
   * @param key The item to get the results for.
   * @return A {@link ResultWithMetadata} that encapsulate the returned value.
   */
  @Nullable
  ResultWithMetadata<R> peekMostRecentResult(T key);

  /**
   * Gets the delay between the test being run on the items.
   * @return The delay in milliseconds.
   */
  int getUpdateDelay();

  /**
   * Set the delay between the test being run on the items.
   * @param delay The new delay in milliseconds.
   * @return The previous delay in milliseconds.
   */
  @SuppressWarnings("UnusedReturnValue")
  int setUpdateDelay(int delay);

  /**
   * Set if the tests should be running. Note that this does not stop the thread, only the actual
   * execution of the tests.
   * @param status The new status.
   * @return The old status.
   */
  boolean setRunning(boolean status);
}
