package org.team1540.rooster.util;

import org.jetbrains.annotations.Contract;

public class OIUtils {

    /**
     * Processes an axis and returns the value only if it is outside the provided deadzone.
     *
     * @param axis The axis to return.
     * @param deadzone The deadzone to use.
     * @return If |{@code axis}| is greater than |{@code deadzone}|, returns {@code axis};
     * otherwise, returns 0.
     * @deprecated Use {@link #processDeadzone(double, double) processDeadzone()}'s improved
     * algorithm.
     */
    @Deprecated
    public static double processAxisDeadzone(double axis, double deadzone) {
        return (Math.abs(axis) > Math.abs(deadzone)) ? axis : 0;
    }

    /**
     * Processes an axis with a deadzone. This implementation scales the output such that the area
     * between the deadzone and 1 is mapped to the full range of motion.
     * <p>
     * Full implementation details: The function processes the deadzone according to the function
     * \(\DeclareMathOperator{\sgn}{sgn}\frac{d(x)-Z\sgn(d(x))}{1-Z}\), where \(x\) is the {@code
     * axis} parameter, \(Z\) is the {@code deadzone} parameter, and \(d(x)\) is the deadzone
     * function \(d(x)=\begin{cases}x &amp; |x| \geq D  \\  0 &amp; |x| &lt; D\end{cases}\). (Note:
     * Equations may require viewing the Javadoc using a browser.)
     *
     * @param axis The axis to return.
     * @param deadzone The deadzone to use.
     * @return The axis value processed with respect to the specified deadzone.
     */
    @Contract(pure = true)
    public static double processDeadzone(double axis, double deadzone) {
        double baseDeadzone = (Math.abs(axis) > Math.abs(deadzone)) ? axis : 0;
        return baseDeadzone != 0 ? (baseDeadzone - Math.copySign(deadzone, baseDeadzone)) / (1
            - deadzone) : 0;
    }
}
