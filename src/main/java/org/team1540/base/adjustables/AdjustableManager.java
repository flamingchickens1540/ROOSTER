package org.team1540.base.adjustables;

import edu.wpi.first.wpilibj.CounterBase;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.interfaces.Accelerometer;
import edu.wpi.first.wpilibj.interfaces.Gyro;
import edu.wpi.first.wpilibj.interfaces.Potentiometer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;

/**
 * Class to manage creating and updating adjustables (tunables and telemetry values.) Add an object
 * containing fields marked with {@link Tunable} or {@link Telemetry} to have those values show up
 * on the SmartDashboard.
 */
public class AdjustableManager {

  private static AdjustableManager instance = new AdjustableManager();
  private List<TunableField> tunables = new LinkedList<>();
  private List<TelemetryField> telemetry = new LinkedList<>();

  public static AdjustableManager getInstance() {
    return instance;
  }

  /**
   * Adds an object to the {@code AdjustableManager}.
   *
   * @param object The object to add. One or more of the fields in this object should be marked with
   * {@link Telemetry} or {@link Tunable}.
   */
  public void add(Object object) {
    // reflection time
    Field[] fields = object.getClass().getFields();

    boolean noneFound = true; // for logging to keep track if we have found at least one adjustable
    for (Field field : fields) {

      // process tunables
      Tunable tunable = field.getAnnotation(Tunable.class);

      if (tunable != null) {
        // check if the field is of a supported type
        TunableType tt = null;
        for (TunableType type : TunableType.values()) {
          if (type.cls.isAssignableFrom(field.getType())) {
            tt = type;
            break;
          }
        }

        if (tt == null) {
          DriverStation.reportError(
              "Annotated tunable in class added to AdjustableManager is not of a supported type",
              false);
          continue;
        }

        tunables.add(new TunableField(object, field, tt, tunable.value()));
        noneFound = false;
      }

      // process telemetry
      Telemetry teleAnnotation = field.getAnnotation(Telemetry.class);

      if (teleAnnotation != null) {
        // check if the field is of a supported type
        TelemetryType tt = null;
        for (TelemetryType type : TelemetryType.values()) {
          if (type.cls.isAssignableFrom(field.getType())) {
            tt = type;
            break;
          }
        }

        if (tt == null) {
          DriverStation.reportError(
              "Annotated telemetry in class added to AdjustableManager is not of a supported type",
              false);
          continue;
        }

        telemetry.add(new TelemetryField(object, field, tt, teleAnnotation.value()));
        noneFound = false;
      }
    }
    if (noneFound) {
      DriverStation.reportWarning(
          "Object passed to AdjustableManager had no annotated adjustable fields",
          false);
    }
  }

  /**
   * Updates adjustable values. This method should be called in {@code robotPeriodic()} in your main
   * {@code Robot} class.
   */
  public void update() {
    // Update tunables
    for (TunableField tf : tunables) {
      try {
        switch (tf.type) {
          case INT:
            tf.field.set(tf.obj,
                (int) SmartDashboard.getNumber(tf.label, (int) tf.field.get(tf.obj)));
            break;
          case DOUBLE:
            tf.field.set(tf.obj, SmartDashboard.getNumber(tf.label, (double) tf.field.get(tf.obj)));
            break;
          case STRING:
            tf.field.set(tf.obj, SmartDashboard.getString(tf.label, (String) tf.field.get(tf.obj)));
            break;
          case BOOLEAN:
            tf.field.set(tf.obj,
                SmartDashboard.getBoolean(tf.label, (boolean) tf.field.get(tf.obj)));
            break;
          default:
            break;
        }
      } catch (IllegalAccessException e) {
        DriverStation.reportError(e.getMessage(), true);
      }
    }

    // Update telemetry
    for (TelemetryField tf : telemetry) {
      try {
        switch (tf.type) {
          case INT:
            SmartDashboard.putNumber(tf.label, (double) tf.field.get(tf.obj));
            break;
          case DOUBLE:
            SmartDashboard.putNumber(tf.label, (double) tf.field.get(tf.obj));
            break;
          case STRING:
            SmartDashboard.putString(tf.label, (String) tf.field.get(tf.obj));
            break;
          case BOOLEAN:
            SmartDashboard.putBoolean(tf.label, (boolean) tf.field.get(tf.obj));
            break;
          case GYRO:
            Gyro gyro = (Gyro) tf.field.get(tf.obj);
            SmartDashboard.putNumber(tf.label + " Angle", gyro.getAngle());
            SmartDashboard.putNumber(tf.label + " Rate", gyro.getRate());
            break;
          case COUNTER:
            CounterBase counter = (CounterBase) tf.field.get(tf.obj);
            SmartDashboard.putNumber(tf.label + " Value", counter.get());
            SmartDashboard.putNumber(tf.label + " Period", counter.getPeriod());
            SmartDashboard.putBoolean(tf.label + " Direction", counter.getDirection());
            SmartDashboard.putBoolean(tf.label + " Stopped", counter.getStopped());
            break;
          case SOLENOID:
            Solenoid solenoid = (Solenoid) tf.field.get(tf.obj);
            SmartDashboard.putBoolean(tf.label + " Enabled", solenoid.get());
            break;
          case ACCELEROMETER:
            Accelerometer accelerometer = (Accelerometer) tf.field.get(tf.obj);
            SmartDashboard.putNumber(tf.label + "X Accel", accelerometer.getX());
            SmartDashboard.putNumber(tf.label + "Y Accel", accelerometer.getY());
            SmartDashboard.putNumber(tf.label + "Z Accel", accelerometer.getZ());
            break;
          case POTENTIOMETER:
            Potentiometer potentiometer = (Potentiometer) tf.field.get(tf.obj);
            SmartDashboard.putNumber(tf.label + " Value", potentiometer.get());
            break;
          case SPEED_CONTROLLER:
            SpeedController speedController = (SpeedController) tf.field.get(tf.obj);
            SmartDashboard.putNumber(tf.label + " Throttle", speedController.get());
            break;
          default:
            break;
        }
      } catch (IllegalAccessException e) {
        DriverStation.reportError(e.toString(), true);
      }
    }
  }

  private static class TunableField {
    Object obj;
    Field field;
    TunableType type;
    String label;

    public TunableField(Object obj, Field field, TunableType type, String label) {
      this.obj = obj;
      this.field = field;
      this.type = type;
      this.label = label;
    }
  }

  private static class TelemetryField {
    Object obj;
    Field field;
    TelemetryType type;
    String label;

    public TelemetryField(Object obj, Field field, TelemetryType type, String label) {
      this.obj = obj;
      this.field = field;
      this.type = type;
      this.label = label;
    }
  }
}
