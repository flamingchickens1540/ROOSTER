package org.team1540.base.adjustables;

@FunctionalInterface
@Deprecated
interface SmartDashboardPut<T> {
  void put(String key, T value);
}
