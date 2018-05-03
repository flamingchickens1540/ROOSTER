package org.team1540.base.adjustables;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;
import org.team1540.base.util.SimpleLoopCommand;

/**
 * Class to manage creating and updating adjustables (tunables and telemetry values.) Add an object
 * containing fields marked with {@link Tunable} or {@link Telemetry} to have those values show up
 * on the SmartDashboard/Shuffleboard.
 */
public class AdjustableManager {

  private static AdjustableManager instance = new AdjustableManager();

  private final Object lock = new Object();
  private List<TunableField> tunables = new LinkedList<>();
  private List<TelemetryField> telemetry = new LinkedList<>();

  private boolean enabled = true;

  private AdjustableManager() {
    SimpleLoopCommand managerUpdate = new SimpleLoopCommand("AdjustableManager Update",
        this::run);
    managerUpdate.setRunWhenDisabled(true);
    managerUpdate.start();
  }

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
    synchronized (lock) {
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
            //noinspection unchecked
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
            //noinspection unchecked
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
  }

  private void run() {
    if (enabled) {
      synchronized (lock) {
        // Update tunables
        for (TunableField tf : tunables) {
          try {
            if (!SmartDashboard.containsKey(tf.label)) {
              //noinspection unchecked
              tf.type.putFunction.put(tf.label, tf.field.get(tf.obj));
            } else {
              //noinspection unchecked
              tf.field.set(tf.obj, tf.type.getFunction.get(tf.label, tf.field.get(tf.obj)));
            }
          } catch (IllegalAccessException e) {
            DriverStation.reportError(e.getMessage(), true);
          }
        }

        // Update telemetry
        for (TelemetryField tf : telemetry) {
          try {
            //noinspection unchecked
            tf.type.putFunction.put(tf.label, tf.field.get(tf.obj));
          } catch (IllegalAccessException e) {
            DriverStation.reportError(e.toString(), true);
          }
        }
      }
    }
  }

  /**
   * Updates adjustable values. This method should be called in {@code robotPeriodic()} in your main
   * {@code Robot} class.
   *
   * @deprecated No longer necessary; now updates automatically.
   */
  public void update() {
    run();
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

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }
}
