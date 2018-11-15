package org.team1540.base.testing;

import com.google.common.collect.EvictingQueue;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings({"unused", "UnstableApiUsage"})
public abstract class AbstractTester<T, R> implements Tester<T, R> {

  private float logTime = 150;
  private int queueDepth;

  private int updateDelay = 2500;
  private boolean running = true;
  @NotNull
  private Function<T, R> test;
  @NotNull
  List<T> itemsToTest;
  @NotNull
  private List<Supplier<Boolean>> runConditions;
  @NotNull
  private Map<T, EvictingQueue<ResultWithMetadata<R>>> storedResults;

  AbstractTester(@NotNull Function<T, R> test, @NotNull List<T> itemsToTest,
      @NotNull List<Supplier<Boolean>> runConditions) {
    realConstructor(test, itemsToTest, runConditions, (int) logTime / (updateDelay / 1000));
  }

  AbstractTester(@NotNull Function<T, R> test, @NotNull List<T> itemsToTest,
      @NotNull List<Supplier<Boolean>> runConditions, float logTime, int updateDelay) {
    this.logTime = logTime;
    this.updateDelay = updateDelay;
    realConstructor(test, itemsToTest, runConditions, (int) logTime / (updateDelay / 1000));
  }

  AbstractTester(@NotNull Function<T, R> test, @NotNull List<T> itemsToTest,
      @NotNull List<Supplier<Boolean>> runConditions, int queueDepth) {
    realConstructor(test, itemsToTest, runConditions, queueDepth);
  }

  private void realConstructor(@NotNull Function<T, R> test, @NotNull List<T> itemsToTest,
      @NotNull List<Supplier<Boolean>> runConditions, int queueDepth) {
    this.test = test;
    this.itemsToTest = itemsToTest;
    this.runConditions = runConditions;
    this.storedResults = new HashMap<>(itemsToTest.size());
    this.queueDepth = queueDepth;
    // TODO I feel like there's a cleaner way of doing this
    for (T t : itemsToTest) {
      this.storedResults.put(t, EvictingQueue.create(queueDepth));
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

  @Override
  @NotNull
  public Map<T, EvictingQueue<ResultWithMetadata<R>>> getStoredResults() {
    return this.storedResults;
  }

  void periodic() {
    for (T t : itemsToTest) {
      this.storedResults.get(t).add(new ResultWithMetadata<>(this.test.apply(t),
          System.currentTimeMillis()));
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
