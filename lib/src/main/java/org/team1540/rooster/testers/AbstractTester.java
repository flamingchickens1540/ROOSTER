package org.team1540.rooster.testers;

import com.google.common.collect.EvictingQueue;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings({"unused", "UnstableApiUsage"})
public abstract class AbstractTester<T, R> implements Tester<T, R> {

  private float logTime = 150;

  private int updateDelay = 2500;
  private boolean running = true;
  @SuppressWarnings("NullableProblems")
  @NotNull
  List<T> itemsToTest;
  @SuppressWarnings("NullableProblems")
  @NotNull
  private Function<T, R> test;
  @SuppressWarnings("NullableProblems")
  @NotNull
  private List<Function<T, Boolean>> runConditions;
  @SuppressWarnings("NullableProblems")
  @NotNull
  private Map<T, ResultStorage<R>> storedResults;

  AbstractTester(@NotNull Function<T, R> test, @NotNull List<T> itemsToTest,
      @NotNull List<Function<T, Boolean>> runConditions) {
    realConstructor(test, itemsToTest, runConditions, (int) logTime / (updateDelay / 1000));
  }

  AbstractTester(@NotNull Function<T, R> test, @NotNull List<T> itemsToTest,
      @NotNull List<Function<T, Boolean>> runConditions, float logTime, int updateDelay) {
    this.logTime = logTime;
    this.updateDelay = updateDelay;
    realConstructor(test, itemsToTest, runConditions, (int) logTime / (updateDelay / 1000));
  }

  AbstractTester(@NotNull Function<T, R> test, @NotNull List<T> itemsToTest,
      @NotNull List<Function<T, Boolean>> runConditions, int queueDepth) {
    realConstructor(test, itemsToTest, runConditions, queueDepth);
  }

  private void realConstructor(@NotNull Function<T, R> test, @NotNull List<T> itemsToTest,
      @NotNull List<Function<T, Boolean>> runConditions, int queueDepth) {
    this.test = test;
    this.itemsToTest = itemsToTest;
    this.runConditions = runConditions;
    this.storedResults = new HashMap<>(itemsToTest.size());
    // TODO I feel like there's a cleaner way of doing this
    for (T t : itemsToTest) {
      this.storedResults.put(t, new ResultStorage<>(queueDepth));
    }
  }

  @Override
  @NotNull
  public Function<T, R> getTest() {
    return test;
  }

  @Override
  public void setTest(@NotNull Function<T, R> test) {
    this.test = test;
  }

  @Override
  @NotNull
  public List<Function<T, Boolean>> getRunConditions() {
    return Collections.unmodifiableList(runConditions);
  }

  @Override
  @NotNull
  public List<T> getItemsToTest() {
    return Collections.unmodifiableList(itemsToTest);
  }

  @NotNull
  public EvictingQueue<ResultWithMetadata<R>> getStoredResults(T key) {
    return storedResults.get(key).queuedResults;
  }

  @Nullable
  public ResultWithMetadata<R> peekMostRecentResult(T key) {
    return storedResults.get(key).lastResult;
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

  void periodic() {
    for (T t : itemsToTest) {
      // Run through all the run conditions and make sure they all return true
      for (Function<T, Boolean> runCondition : runConditions) {
        if (!runCondition.apply(t)) {
          return;
        }
      }
      this.storedResults.get(t).addResult(this.test.apply(t), System.currentTimeMillis());
    }
  }

  @Override
  public void run() {
    while (true) {

      if (running) {
        periodic();
      }

      try {
        Thread.sleep(updateDelay);
      } catch (InterruptedException e) {
        // End the thread
        return;
      }
    }
  }

  private class ResultStorage<A> {

    @Nullable
    private ResultWithMetadata<A> lastResult;
    @NotNull
    private EvictingQueue<ResultWithMetadata<A>> queuedResults;

    private ResultStorage(int queueDepth) {
      this.queuedResults = EvictingQueue.create(queueDepth);
    }

    private void addResult(A result, long timeStampMillis) {
      ResultWithMetadata<A> resultWithMetadata = new ResultWithMetadata<>(result, timeStampMillis);
      this.lastResult = resultWithMetadata;
      queuedResults.add(resultWithMetadata);
    }

  }

}
