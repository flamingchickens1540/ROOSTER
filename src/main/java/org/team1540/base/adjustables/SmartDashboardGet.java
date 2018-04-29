package org.team1540.base.adjustables;

@FunctionalInterface
@Deprecated
interface SmartDashboardGet<T> {
  T get(String key, T dfault);
}
