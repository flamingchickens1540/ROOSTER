package org.team1540.rooster.adjustables;

@FunctionalInterface
@Deprecated
interface SmartDashboardPut<T> {
  void put(String key, T value);
}
