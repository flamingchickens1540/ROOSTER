package org.team1540.rooster.adjustables;

@FunctionalInterface
@Deprecated
interface SmartDashboardGet<T> {
  T get(String key, T dfault);
}
