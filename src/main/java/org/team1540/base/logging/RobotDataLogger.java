package org.team1540.base.logging;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.MatchType;
import edu.wpi.first.wpilibj.Notifier;
import edu.wpi.first.wpilibj.Sendable;
import edu.wpi.first.wpilibj.smartdashboard.SendableBuilder;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemAlreadyExistsException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;
import java.util.function.IntSupplier;
import java.util.function.LongSupplier;
import java.util.function.Supplier;
import org.team1540.base.util.RobotStateUtil;
import org.team1540.base.util.RobotStateUtil.State;

/**
 * Logs assorted robot data to compressed ZIP files.
 * <p>
 * The {@code RobotDataLogger} class can be used to log data (Java primitive types + {@link String
 * Strings}) to a file. Data sources can be registered using the {@code addDataSource()} methods
 * which take lambda methods. These lamdas will be called on a regular interval (set by the {@link
 * #setUpdateInterval(int)} method).
 * <p>
 * This class is <em>not</em> thread-safe; any calls to this class's methods occuring in different
 * threads (including, but not limited to, code running inside a {@link Notifier}) MUST be
 * synchronized externally. Calls occuring within the body of {@link
 * edu.wpi.first.wpilibj.command.Command Commands} are not in different threads and do not need to
 * be synchronized.
 */
public class RobotDataLogger implements Sendable {

  /*
  A note on thread safety:

  While the internal workings of the class itself are thread-safe, their thread-safety is
  dependent on the public methods of the class NOT being called concurrently. The "active" flag
  shows whether the notifier is running (and thus things need to be thread-safe) or not. If the
  active flag is true then any configuration methods will throw IllegalStateException. It's not a
  very nice way of doing things; however, synchronization with blocking could lead to the main robot
  thread being blocked on a method call for an unacceptable amount of time if compressing a zip
  file happens to become a demand. Also, modifications to the telemetry saved while logging is
  in progress would break everything because the data would then be out of alignment with the
  headers.
  */

  private Notifier notifier = new Notifier(this::update);
  private String name;
  private String subsystem;
  private boolean logDuringDisabled = false;
  private boolean logOutsideOfMatches = true;
  private int updateInterval = 20; // milliseconds

  String header;

  // data is specifically written in this order: a timestamp; all objects; all booleans; all doubles; all ints; all longs.
  Map<String, Supplier<?>> objSuppliers = new TreeMap<>();
  Map<String, BooleanSupplier> booleanSuppliers = new TreeMap<>();
  Map<String, DoubleSupplier> doubleSuppliers = new TreeMap<>();
  Map<String, IntSupplier> intSuppliers = new TreeMap<>();
  Map<String, LongSupplier> longSuppliers = new TreeMap<>();

  private boolean active;

  private final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
  private Path zipFolderPath;
  private Path tempFolderPath;


  private Map<String, String> zipFileSystemEnv = new HashMap<>();
  private String id;

  {
    zipFileSystemEnv.put("create", "true");
  }

  private PrintWriter logger;
  private long startTime;
  private Path loggerFilePath;

  private RobotStateUtil.State lastState;

  public RobotDataLogger(String directory) {
    zipFolderPath = Paths.get(URI.create("/home/lvuser/" + directory + "/"));
    tempFolderPath = Paths.get(URI.create("/home/lvuser/" + directory + "/tmp/"));
    name = "Robot Data Logger (" + directory + ")";
    subsystem = "";
  }

  private void update() {
    long startTime = System.currentTimeMillis();
    try {
      RobotStateUtil.State currentState = RobotStateUtil.getRobotState();
      if (currentState != lastState) {
        // we're transitioning modes, so save the data from the last run (if there is any)
        zipLog();

        // now, reset start values and re-open a CSV stream
        if ((DriverStation.getInstance().isFMSAttached() || logOutsideOfMatches) &&
            (currentState.isEnabled() || logDuringDisabled)) {
          createNewLog(currentState);
        } else {
          logger = null;
        }
        lastState = currentState;
      }

      // now write all telemetry values to the file, in order
      if (logger != null) {
        StringBuilder lineBuilder = new StringBuilder();

        lineBuilder.append(System.currentTimeMillis());

        for (Supplier<?> supplier : objSuppliers.values()) {
          lineBuilder.append(", ");
          if (supplier != null) { // necessary because not a primitive
            lineBuilder.append(supplier.get());
          } else {
            lineBuilder.append("null");
          }
        }

        for (BooleanSupplier supplier : booleanSuppliers.values()) {
          lineBuilder.append(", ");
          lineBuilder.append(supplier.getAsBoolean());
        }

        for (DoubleSupplier supplier : doubleSuppliers.values()) {
          lineBuilder.append(", ");
          lineBuilder.append(supplier.getAsDouble());
        }

        for (IntSupplier supplier : intSuppliers.values()) {
          lineBuilder.append(", ");
          lineBuilder.append(supplier.getAsInt());
        }

        for (LongSupplier supplier : longSuppliers.values()) {
          lineBuilder.append(", ");
          lineBuilder.append(supplier.getAsLong());
        }

        logger.println(lineBuilder);
      }
    } catch (IOException e) {
      DriverStation
          .reportError("[RobotDataLogger] IO error occured during logging: " + e.getMessage(),
              e.getStackTrace());
    }
    long time = System.currentTimeMillis() - startTime;

    if (time > updateInterval) {
      DriverStation.reportWarning("[RobotDataLogger] Data logging cycle took " + time
              + " ms, longer than the update interval. Reduce the amount of data written or "
              + "increase the update interval.",
          false);
    }
  }

  private void zipLog() throws IOException {
    logger.close();

    // create a zip file corresponding to the timestamp
    Path zipPath = zipFolderPath.resolve(id + ".zip");

    try {
      try (FileSystem zip = FileSystems.newFileSystem(zipPath.toUri(), zipFileSystemEnv, null)) {
        Files.move(loggerFilePath, zip.getPath(id + ".csv"));
      }

      System.out.println("Saved data for " + id);
    } catch (FileSystemAlreadyExistsException e) {
      DriverStation.reportError("[RobotDataLogger] ZIP file " + zipPath.toString()
          + " already existed! Saving unzipped file instead.", false);

      try {
        Files.move(loggerFilePath, zipFolderPath.resolve(id + ".csv"));

        System.out.println("Saved unzipped data for " + id);
      } catch (FileAlreadyExistsException e2) {
        DriverStation.reportError("[RobotDataLogger] CSV file " + zipPath.toString()
            + " already existed! Saving data failed.", false);
      }
    }
  }

  private void createNewLog(State currentState) throws FileNotFoundException {
    startTime = System.currentTimeMillis();
    id = buildId(new Date(startTime), currentState.toString());
    loggerFilePath = tempFolderPath.resolve(id + ".csv");

    // create a new writer
    logger = new PrintWriter(loggerFilePath.toFile());

    // headers
    logger.println(header);
  }


  /**
   * Activates the logger. This starts data collection and disables changes to parameters.
   */
  public void activate() {
    if (!active) {
      // "lock in" all changes to avoid any thread-safety or weird CSV writing problems
      active = true;

      try {
        header = buildHeader();

        RobotStateUtil.State currentState = RobotStateUtil.getRobotState();
        lastState = currentState;

        if ((DriverStation.getInstance().isFMSAttached() || logOutsideOfMatches) &&
            (currentState.isEnabled() || logDuringDisabled)) {
          createNewLog(currentState);
        } else {
          logger = null;
        }

        notifier.startPeriodic(updateInterval / 1000.0);
      } catch (IOException e) {
        DriverStation.reportError(
            "[RobotDataLogger] IO error occured during logger activation: " + e.getMessage(),
            e.getStackTrace());
      }
    }
  }

  /**
   * Deactivates the logger. This stops data collection and compresses the output.
   */
  public void deactivate() {
    if (active) {
      try {
        notifier.stop();
        if (logger != null) {
          zipLog();
        }

        // finally, release the lock
        active = false;
      } catch (IOException e) {
        DriverStation.reportError(
            "[RobotDataLogger] IO error occured during logger deactivation: " + e.getMessage(),
            e.getStackTrace());
      }
    }
  }

  // non-static to gain access to the DateFormat instance,
  // which is NOT synchronized so is created on a per-thread basis
  private String buildId(Date startDate, String modeName) {
    // if we are in a match, add that info to the file name
    StringBuilder builder = new StringBuilder();

    builder.append(dateFormat.format(startDate));

    if (DriverStation.getInstance().isFMSAttached()) {
      // we're playing a match
      builder.append(" (");
      MatchType type = DriverStation.getInstance().getMatchType();
      switch (type) {
        case None:
          builder.append("M");
          break;
        case Practice:
          builder.append("P");
          break;
        case Qualification:
          builder.append("Q");
        case Elimination:
          builder.append("E");
          break;
      }

      builder.append(DriverStation.getInstance().getMatchNumber())
          .append(")");
    }

    builder.append(" ").append(modeName);

    return builder.toString();
  }

  private String buildHeader() {
    StringBuilder builder = new StringBuilder();
    builder.append("time");

    for (String key : objSuppliers.keySet()) {
      builder.append(", ");
      builder.append(key);
    }

    for (String key : booleanSuppliers.keySet()) {
      builder.append(", ");
      builder.append(key);
    }

    for (String key : doubleSuppliers.keySet()) {
      builder.append(", ");
      builder.append(key);
    }

    for (String key : intSuppliers.keySet()) {
      builder.append(", ");
      builder.append(key);
    }

    for (String key : longSuppliers.keySet()) {
      builder.append(", ");
      builder.append(key);
    }

    return builder.toString();
  }

  /**
   * Sets the update interval for the logger.
   * <p>
   * The update interval is the interval between data collection cycles, and thus the time between
   * each data point. A higher value will result in less data being written to files at the cost of
   * less precise data. Defaults to 20 milliseconds.
   *
   * @param updateInterval The update interval in milliseconds.
   * @throws IllegalStateException If the logger is currently active.
   */
  public void setUpdateInterval(int updateInterval) {
    ensureInactive();
    this.updateInterval = updateInterval;
  }

  /**
   * Gets the update interval for the logger. The update interval is the interval between data
   * collection cycles, and thus the time between each data point.
   *
   * @return The update interval in milliseconds.
   */
  public int getUpdateInterval() {
    return updateInterval;
  }

  /**
   * Sets whether the logger should also log data when the robot is disabled. Defaults to {@code
   * false}.
   *
   * @param logDuringDisabled Whether to log data when the robot is disabled.
   * @throws IllegalStateException If the logger is currently active.
   */
  public void setLogDuringDisabled(boolean logDuringDisabled) {
    ensureInactive();
    this.logDuringDisabled = logDuringDisabled;
  }

  /**
   * Sets whether the logger will also log data when the robot is disabled.
   *
   * @return Whether to log data when the robot is disabled.
   */
  public boolean isLogDuringDisabled() {
    return logDuringDisabled;
  }

  public boolean isLogOutsideOfMatches() {
    return logOutsideOfMatches;
  }

  /**
   * Sets whether the logger will log data outside of regular matches. If {@code false}, log files
   * will only be created when the robot is connected to an FMS. Defaults to {@code true}.
   *
   * @param logOutsideOfMatches Whether to log data outside of matches.
   */
  public void setLogOutsideOfMatches(boolean logOutsideOfMatches) {
    this.logOutsideOfMatches = logOutsideOfMatches;
  }

  private void ensureInactive() {
    // prevent any changes to settings/telemetry providers while the logger is active
    // TODO: mechanism to make settings change at the next safe time?
    if (active) {
      throw new IllegalStateException("RobotDataLogger cannot be configured while active");
    }
  }

  /**
   * Adds a data source to be logged.
   *
   * @param name The header for the item's column in the saved CSV files.
   * @param source The supplier for the telemetry item. The supplier will be called periodically and
   * the toString() representation of its result will be written to a file.
   * @throws IllegalStateException If the logger is currently active.
   */
  public void addDataSource(String name, Supplier<?> source) {
    ensureInactive();
    Objects.requireNonNull(name);
    Objects.requireNonNull(source);

    objSuppliers.put(name, source);
  }

  /**
   * Adds a {@code boolean} data source to be logged.
   *
   * @param name The header for the item's column in the saved CSV files.
   * @param source The supplier for the telemetry item. The supplier will be called periodically and
   * its result written to a file as "true" or "false".
   * @throws IllegalStateException If the logger is currently active.
   */
  public void addDataSource(String name, BooleanSupplier source) {
    ensureInactive();
    Objects.requireNonNull(name);
    Objects.requireNonNull(source);

    booleanSuppliers.put(name, source);
  }

  /**
   * Adds a {@code double} data source to be logged.
   *
   * @param name The header for the item's column in the saved CSV files.
   * @param source The supplier for the telemetry item. The supplier will be called periodically and
   * its result written to a file.
   */
  public void addDataSource(String name, DoubleSupplier source) {
    ensureInactive();
    Objects.requireNonNull(name);
    Objects.requireNonNull(source);

    doubleSuppliers.put(name, source);
  }

  /**
   * Adds a {@code int} data source to be logged.
   *
   * @param name The header for the item's column in the saved CSV files.
   * @param source The supplier for the telemetry item. The supplier will be called periodically and
   * its result written to a file.
   */
  public void addDataSource(String name, IntSupplier source) {
    ensureInactive();
    Objects.requireNonNull(name);
    Objects.requireNonNull(source);

    intSuppliers.put(name, source);
  }

  /**
   * Adds a {@code long} data source to be logged.
   *
   * @param name The header for the item's column in the saved CSV files.
   * @param source The supplier for the telemetry item. The supplier will be called periodically and
   * its result written to a file.
   */
  public void addDataSource(String name, LongSupplier source) {
    ensureInactive();
    Objects.requireNonNull(name);
    Objects.requireNonNull(source);

    longSuppliers.put(name, source);
  }

  public boolean isActive() {
    return active;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public void setName(String name) {
    this.name = name;
  }

  @Override
  public String getSubsystem() {
    return subsystem;
  }

  @Override
  public void setSubsystem(String subsystem) {
    this.subsystem = subsystem;
  }

  @Override
  public void initSendable(SendableBuilder builder) {
    builder.addBooleanProperty("Log during disabled", this::isLogDuringDisabled, (value) -> {
      if (!active) {
        setLogDuringDisabled(value);
      }
    });
    builder.addBooleanProperty("Log outside of matches", this::isLogOutsideOfMatches, (value) -> {
      if (!active) {
        setLogOutsideOfMatches(value);
      }
    });
    builder.addDoubleProperty("Update interval", this::getUpdateInterval, (value -> {
      if (!active) {
        setUpdateInterval((int) value);
      }
    }));
    builder.addBooleanProperty("Active", this::isActive, (value) -> {
      if (value) {
        activate();
      } else {
        deactivate();
      }
    });
  }
}
