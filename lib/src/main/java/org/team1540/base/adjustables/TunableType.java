package org.team1540.base.adjustables;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

@Deprecated
enum TunableType {
  STRING(String.class, SmartDashboard::putString, SmartDashboard::getString),
  INT(Integer.TYPE, (SmartDashboardPut<Integer>) SmartDashboard::putNumber,
      (key, defaultValue) -> (int) SmartDashboard.getNumber(key, defaultValue)),
  DOUBLE(Double.TYPE, SmartDashboard::putNumber, SmartDashboard::getNumber),
  BOOLEAN(Boolean.TYPE, SmartDashboard::putBoolean, SmartDashboard::getBoolean);

  final Class cls;
  final SmartDashboardGet getFunction;
  final SmartDashboardPut putFunction;

  <T> TunableType(Class<T> cls, SmartDashboardPut<T> putFunction, SmartDashboardGet<T> getFunction) {
    this.cls = cls;
    this.putFunction = putFunction;
    this.getFunction = getFunction;
  }
}
