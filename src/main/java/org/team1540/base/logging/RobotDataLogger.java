package org.team1540.base.logging;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.MatchType;
import edu.wpi.first.wpilibj.Notifier;
import edu.wpi.first.wpilibj.Sendable;
import edu.wpi.first.wpilibj.smartdashboard.SendableBuilder;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;
import java.util.function.IntSupplier;
import java.util.function.LongSupplier;
import java.util.function.Supplier;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.team1540.base.util.RobotStateUtil;
import org.team1540.base.util.RobotStateUtil.State;

/**
 * Logs assorted robot data to compressed ZIP files.
 * <p>
 * The {@code RobotDataLogger} class can be used to log data (in the form of primitive types and
 * {@link Object Objects}) to a file. Data sources can be registered using the {@code
 * addDataSource()} methods which take lambda methods. These lamdas will be called on a regular
 * interval (set by the {@link #setUpdateInterval(int)} method).
 * <p>
 * This class is <em>not</em> thread-safe; any calls to this class's methods occuring in different
 * threads (including, but not limited to, code running inside a {@link Notifier}) must be
 * synchronized externally. Calls occuring within the body of {@link
 * edu.wpi.first.wpilibj.command.Command Commands} are not in different threads and do not need to
 * be synchronized.
 * <p>
 * Unless otherwise noted, all methods in this class throw {@link NullPointerException} if any of
 * their arguments are {@code null}.
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
  private int zipWriteBufferSize = 2048;
  private int updateInterval = 20; // milliseconds

  String header;

  // data is specifically written in this order: a timestamp; all objects; all booleans; all doubles; all ints; all longs.
  Map<String, Supplier<?>> objSuppliers = new TreeMap<>();
  Map<String, BooleanSupplier> booleanSuppliers = new TreeMap<>();
  Map<String, DoubleSupplier> doubleSuppliers = new TreeMap<>();
  Map<String, IntSupplier> intSuppliers = new TreeMap<>();
  Map<String, LongSupplier> longSuppliers = new TreeMap<>();

  private boolean active;

  private final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-kk:mm:ss");
  private Path zipFolderPath;
  private Path tempFolderPath;

  private String id;

  private PrintWriter logger;
  private long startTime;
  private Path loggerFilePath;

  private RobotStateUtil.State lastState;

  /**
   * Creates a new {@code RobotDataLogger} that logs to the specified directory.
   *
   * Log ZIP files will be saved to the directory /home/lvuser/DIRECTORY/ (where DIRECTORY) is the
   * {@code directory} parameter.
   *
   * @param directory The directory name.
   */
  public RobotDataLogger(@NotNull String directory) {
    Objects.requireNonNull(directory);
    zipFolderPath = Paths.get("/home/lvuser/" + directory + "/");
    tempFolderPath = Paths.get("/home/lvuser/" + directory + "/tmp/");

    //noinspection ResultOfMethodCallIgnored
    zipFolderPath.toFile().mkdir();
    //noinspection ResultOfMethodCallIgnored
    tempFolderPath.toFile().mkdir();

    name = "Robot Data Logger (" + directory + ")";
    subsystem = "RDL";
  }

  private void update() {
    long startTime = System.currentTimeMillis();
    boolean performedLargeOperation = false;
    try {
      RobotStateUtil.State currentState = RobotStateUtil.getRobotState();
      if (currentState != lastState) {
        // we're transitioning modes, so save the data from the last run (if there is any)
        if (logger != null) {
          zipLog();
          performedLargeOperation = true;
        }

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
          .reportError("[RobotDataLogger] IO error occured during logging: " + e.toString(),
              e.getStackTrace());
    }
    long time = System.currentTimeMillis() - startTime;

    // don't warn if we take longer than 50 ms to zip up a file, that's no big deal
    if (time > updateInterval && !performedLargeOperation) {
      DriverStation.reportWarning("[RobotDataLogger] Data logging cycle took " + time
              + " ms, longer than the update interval. Reduce the amount of data written or "
              + "increase the update interval",
          false);
    }
  }

  private void zipLog() throws IOException {
    logger.close();
    System.out.println("[RobotDataLogger] Zipping up log file for " + id);

    // create a zip file corresponding to the timestamp
    Path zipPath = zipFolderPath.resolve(id + ".zip");

    try (ZipOutputStream zipOutputStream = new ZipOutputStream(
        new BufferedOutputStream(new FileOutputStream(zipPath.toFile())))) { // java_irl
      try (FileInputStream csvInput = new FileInputStream(loggerFilePath.toFile())) {
        ZipEntry entry = new ZipEntry(id + ".csv");
        entry.setCreationTime(FileTime.fromMillis(loggerFilePath.toFile().lastModified()));

        zipOutputStream.putNextEntry(entry);

        byte[] buffer = new byte[zipWriteBufferSize];
        int amountRead;
        int written = 0;

        while ((amountRead = csvInput.read(buffer)) > 0) {
          zipOutputStream.write(buffer, 0, amountRead);
          written += amountRead;
        }

        System.out.println(
            "[RobotDataLogger] Successfully zipped up " + written / 1000.0 + " KB of log data for "
                + id);

        if (!loggerFilePath.toFile().delete()) {
          DriverStation.reportWarning("[RobotDataLogger] Deleting original log file failed", false);
        }
      }
    }
  }

  private void createNewLog(@NotNull State currentState) throws FileNotFoundException {
    startTime = System.currentTimeMillis();
    id = buildId(new Date(startTime), currentState.toString());
    loggerFilePath = tempFolderPath.resolve(id + ".csv");

    // create a new writer
    logger = new PrintWriter(loggerFilePath.toFile());

    // headers
    logger.println(header);
    System.out.println("[RobotDataLogger] Created new log file at " + loggerFilePath.toString());
  }


  /**
   * Activates the logger. This starts data collection and disables changes to parameters.
   */
  public void activate() {
    if (!active) {
      System.out.println("[RobotDataLogger] Activating logger");
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
            "[RobotDataLogger] IO error occured during logger activation: " + e.toString(),
            e.getStackTrace());
      }
    }
  }

  /**
   * Deactivates the logger. This stops data collection and compresses the output.
   */
  public void deactivate() {
    if (active) {
      System.out.println("[RobotDataLogger] Deactivating logger");
      try {
        notifier.stop();
        if (logger != null) {
          zipLog();
        }

        // finally, release the lock
        active = false;
      } catch (IOException e) {
        DriverStation.reportError(
            "[RobotDataLogger] IO error occured during logger deactivation: " + e.toString(),
            e.getStackTrace());
      }
    }
  }

  // non-static to gain access to the DateFormat instance,
  // which is NOT synchronized so is created on a per-thread basis
  @NotNull
  private String buildId(@NotNull Date startDate, @NotNull String modeName) {
    // if we are in a match, add that info to the file name
    StringBuilder builder = new StringBuilder();

    builder.append(dateFormat.format(startDate));

    if (DriverStation.getInstance().isFMSAttached()) {
      // we're playing a match
      builder.append("-(");
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

    builder.append("-").append(modeName);

    return builder.toString();
  }

  @NotNull
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
  @Contract(pure = true)
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
   * Returns the buffer size when copying data files into a ZIP.
   *
   * @return The write buffer size, in bytes.
   */
  @Contract(pure = true)
  public int getZipWriteBufferSize() {
    return zipWriteBufferSize;
  }

  /**
   * Sets the buffer size when copying data files into a ZIP for storage. Defaults to 2048 bytes.
   *
   * @param zipWriteBufferSize The desired write buffer size, in bytes.
   * @throws IllegalStateException If the logger is currently active.
   */
  public void setZipWriteBufferSize(int zipWriteBufferSize) {
    ensureInactive();
    this.zipWriteBufferSize = zipWriteBufferSize;
  }

  /**
   * Sets whether the logger will also log data when the robot is disabled.
   *
   * @return Whether to log data when the robot is disabled.
   */
  @Contract(pure = true)
  public boolean isLogDuringDisabled() {
    return logDuringDisabled;
  }

  @Contract(pure = true)
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
    ensureInactive();
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
  public void addDataSource(@NotNull String name, @NotNull Supplier<?> source) {
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
  public void addDataSource(@NotNull String name, @NotNull BooleanSupplier source) {
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
  public void addDataSource(@NotNull String name, @NotNull DoubleSupplier source) {
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
  public void addDataSource(@NotNull String name, @NotNull IntSupplier source) {
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
  public void addDataSource(@NotNull String name, @NotNull LongSupplier source) {
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
    builder.setSmartDashboardType("Logger");
  }
}
