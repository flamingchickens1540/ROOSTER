package org.team1540.base.testing;

import com.google.common.collect.EvictingQueue;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings({"unused", "UnstableApiUsage"})
public abstract class AbstractTester<T, R> implements Tester<T, R> {

  private float logTime = 150;

  private int updateDelay = 2500;
  private boolean running = true;
  @NotNull
  private List<Function<T, R>> tests;
  @NotNull
  List<T> itemsToTest;
  @NotNull
  private List<Supplier<Boolean>> runConditions;
  @NotNull
  private Map<T, Map<Function<T, R>, EvictingQueue<ResultWithMetadata<R>>>> allQueuedResults;

  AbstractTester(int numTests, @NotNull List<T> itemsToTest,
      @NotNull List<Supplier<Boolean>> runConditions) {
    realConstructor(new ArrayList<>(numTests), itemsToTest, runConditions,
        (int) logTime / (updateDelay / 1000));
  }

  AbstractTester(@NotNull List<Function<T, R>> tests, @NotNull List<T> itemsToTest,
      @NotNull List<Supplier<Boolean>> runConditions) {
    realConstructor(tests, itemsToTest, runConditions, (int) logTime / (updateDelay / 1000));
  }

  AbstractTester(@NotNull List<Function<T, R>> tests, @NotNull List<T> itemsToTest,
      @NotNull List<Supplier<Boolean>> runConditions, float logTime, int updateDelay) {
    this.logTime = logTime;
    this.updateDelay = updateDelay;
    realConstructor(tests, itemsToTest, runConditions, (int) logTime / (updateDelay / 1000));
  }

  AbstractTester(@NotNull List<Function<T, R>> tests, @NotNull List<T> itemsToTest,
      @NotNull List<Supplier<Boolean>> runConditions, int queueDepth) {
    realConstructor(tests, itemsToTest, runConditions, queueDepth);
  }

  private void realConstructor(@NotNull List<Function<T, R>> tests, @NotNull List<T> itemsToTest,
      @NotNull List<Supplier<Boolean>> runConditions, int queueDepth) {
    this.tests = Collections.unmodifiableList(tests);
    this.itemsToTest = itemsToTest;
    this.runConditions = runConditions;
    this.allQueuedResults = new HashMap<>(itemsToTest.size());
    // TODO I feel like there's a cleaner way of doing this
    for (T t : itemsToTest) {
      allQueuedResults.put(t, new HashMap<>(tests.size()));
      for (Function<T, R> test : tests) {
        allQueuedResults.get(t).put(test, EvictingQueue.create(queueDepth));
      }
    }
  }

  @Override
  @NotNull
  public List<Function<T, R>> getTests() {
    return tests;
  }

  @Override
  @NotNull
  public List<Supplier<Boolean>> getRunConditions() {
    return runConditions;
  }


  @Override
  public int getUpdateDelay() {
    return updateDelay;
  }

  @Override
  public float setUpdateDelay(int delay) {
    float oldUpdateDelay = this.updateDelay;
    this.updateDelay = delay;
    return oldUpdateDelay;
  }

  @Override
  public boolean setRunning(boolean status) {
    boolean oldRunning = this.running;
    this.running = status;
    return status;
  }

  @NotNull
  public Map<T, Map<Function<T, R>, EvictingQueue<ResultWithMetadata<R>>>> getAllQueuedResults() {
    return allQueuedResults;
  }

  void periodic() {
    for (T t : itemsToTest) {
      for (Function<T, R> test : tests) {
        allQueuedResults.get(t).get(test).add(new ResultWithMetadata<>(test.apply(t),
            System.currentTimeMillis()));
      }
    }
  }

  @Override
  public void run() {
    while (true) {

      periodic();

      try {
        Thread.sleep(updateDelay);
      } catch (InterruptedException e) {
        // end the thread
        return;
      }
    }
  }
}
