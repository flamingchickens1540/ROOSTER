package org.team1540.base.adjustables;

import edu.wpi.first.wpilibj.Sendable;

enum TelemetryType {
  STRING(String.class),
  INT(Integer.TYPE),
  DOUBLE(Double.TYPE),
  BOOLEAN(Boolean.TYPE),
  SENDABLE(Sendable.class);

  final Class<?> cls;

  TelemetryType(Class<?> cls) {this.cls = cls;}
}
