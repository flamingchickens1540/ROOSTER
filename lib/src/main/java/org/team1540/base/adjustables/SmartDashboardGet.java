package org.team1540.base.adjustables;

@FunctionalInterface
interface SmartDashboardGet<T> {
  T get(String key, T dfault);
}
