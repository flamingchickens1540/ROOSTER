package org.team1540.base.preferencemanager;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Preferences;
import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.team1540.base.util.SimpleLoopCommand;

/**
 * Class to manage creating and updating robot preferences. Add an object containing fields marked
 * with {@link Preference} to have those values be controlled by what is saved in the robot's {@link
 * Preferences}.
 */
public class PreferenceManager {

  private static PreferenceManager instance = new PreferenceManager();

  private List<PreferenceField> preferences = new LinkedList<>();

  private boolean enabled = true;

  private PreferenceManager() {
    SimpleLoopCommand managerUpdate = new SimpleLoopCommand("PreferenceManager Update",
        this::run);
    managerUpdate.setRunWhenDisabled(true);
    managerUpdate.start();
  }

  @Contract(pure = true)
  @NotNull
  public static synchronized PreferenceManager getInstance() {
    return instance;
  }

  /**
   * Adds an object to the {@code PreferenceManager}.
   *
   * @param object The object to add. One or more of the fields in this object should be marked with
   * {@link Preference}.
   * @throws NullPointerException If object is null.
   */
  public synchronized void add(@NotNull Object object) {
    Objects.requireNonNull(object);

    // reflection time
    Field[] fields = object.getClass().getFields();

    boolean noneFound = true; // for logging to keep track if we have found at least one adjustable
    for (Field field : fields) {

      // process tunables
      Preference preference = field.getAnnotation(Preference.class);

      if (preference != null) {
        // check if the field is of a supported type
        PreferenceType preferenceType = null;
        for (PreferenceType type : PreferenceType.values()) {
          //noinspection unchecked
          if (type.cls.isAssignableFrom(field.getType())) {
            preferenceType = type;
            break;
          }
        }

        if (preferenceType == null) {
          DriverStation.reportError(
              "Annotated tunable in class added to AdjustableManager is not of a supported type",
              false);
          continue;
        }

        preferences.add(new PreferenceField(object, field, preferenceType, preference.value()));
        noneFound = false;
      }
    }
    if (noneFound) {
      DriverStation.reportWarning(
          "Object passed to AdjustableManager had no annotated adjustable fields",
          false);
    }
  }

  private synchronized void run() {
    if (enabled) {
      for (PreferenceField preference : preferences) {
        try {
          if (!Preferences.getInstance().containsKey(preference.label)) {
            //noinspection unchecked
            preference.type.putFunction
                .accept(preference.label, preference.field.get(preference.obj));
          } else {
            //noinspection unchecked
            preference.field.set(preference.obj, preference.type.getFunction
                .apply(preference.label, preference.field.get(preference.obj)));
          }
        } catch (IllegalAccessException e) {
          DriverStation.reportError(e.getMessage(), true);
        }
      }
    }
  }

  /**
   * Data class to hold information about the field, its type, the object it's on, and its label.
   */
  private static class PreferenceField {

    Object obj;
    Field field;
    PreferenceType type;
    String label;

    public PreferenceField(Object obj, Field field, PreferenceType type, String label) {
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

  private enum PreferenceType {
    STRING(String.class, Preferences.getInstance()::putString,
        Preferences.getInstance()::getString),
    INT(Integer.TYPE, Preferences.getInstance()::putInt, Preferences.getInstance()::getInt),
    DOUBLE(Double.TYPE, Preferences.getInstance()::putDouble, Preferences.getInstance()::getDouble),
    BOOLEAN(Boolean.TYPE, Preferences.getInstance()::putBoolean,
        Preferences.getInstance()::getBoolean);

    final Class cls;
    final BiFunction getFunction;
    final BiConsumer putFunction;

    <T> PreferenceType(Class<T> cls, BiConsumer<String, T> putFunction,
        BiFunction<String, T, T> getFunction) {
      this.cls = cls;
      this.putFunction = putFunction;
      this.getFunction = getFunction;
    }
  }
}
