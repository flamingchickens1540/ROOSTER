package org.team1540.rooster.logging;

import edu.wpi.first.wpilibj.DriverStation;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;

/**
 * Translates Log4J log messages at Error or Warning level to driver station calls. Calls at level
 * {@link Level#ERROR} or above will be reported using {@link DriverStation#reportError(String,
 * boolean) DriverStation.reportError()}, while calls below {@link Level#ERROR} but at or above
 * level {@link Level#WARN} will be reported using {@link DriverStation#reportWarning(String,
 * boolean) DriverStation#reportWarning()}
 */
public class DriverStationAppender extends AppenderSkeleton {

    @Override
    protected void append(LoggingEvent loggingEvent) {
        if (loggingEvent.getLevel().isGreaterOrEqual(Level.WARN)) {
            if (loggingEvent.getLevel().isGreaterOrEqual(Level.ERROR)) {
                DriverStation.reportError(layout.format(loggingEvent), false);
            } else {
                DriverStation.reportWarning(layout.format(loggingEvent), false);
            }
        }
    }

    @Override
    public void close() {

    }

    @Override
    public boolean requiresLayout() {
        return true;
    }
}
