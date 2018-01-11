package org.team1540.base.adjustables;

import edu.wpi.first.wpilibj.Sendable;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

enum TelemetryType {
  STRING(String.class, SmartDashboard::putString),
  INT(Integer.TYPE, (SmartDashboardPut<Integer>) SmartDashboard::putNumber),
  DOUBLE(Double.TYPE, SmartDashboard::putNumber),
  BOOLEAN(Boolean.TYPE, SmartDashboard::putBoolean),
  SENDABLE(Sendable.class, SmartDashboard::putData);

  final Class cls;
  final SmartDashboardPut putFunction;

  <T> TelemetryType(Class<T> cls, SmartDashboardPut<T> putFunction) {
    this.cls = cls;
    this.putFunction = putFunction;
  }
}
