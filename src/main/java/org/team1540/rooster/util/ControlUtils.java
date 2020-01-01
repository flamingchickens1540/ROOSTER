package org.team1540.rooster.util;

import org.jetbrains.annotations.Contract;

public class ControlUtils {

    public static double constrainAbsValue(double velocity, double maxVelocity,
        double minVelocity) {
        if (Math.abs(velocity) > maxVelocity) {
            velocity = Math.copySign(maxVelocity, velocity);
        } else if (Math.abs(velocity) < minVelocity) {
            velocity = Math.copySign(minVelocity, velocity);
        }
        return velocity;
    }

    /**
     * Processes an axis and returns the value only if it is outside the provided deadzone.
     *
     * @param axis The axis to return.
     * @param deadzone The deadzone to use.
     * @return If |{@code axis}| is greater than |{@code deadzone}|, returns {@code axis};
     * otherwise, returns 0.
     */
    public static double simpleDeadzone(double axis, double deadzone) {
        return (Math.abs(axis) > Math.abs(deadzone)) ? axis : 0;
    }

    public static double constrainAndDeadzone(double output, double max, double min,
        double deadzone) {
        return simpleDeadzone(constrainAbsValue(output, max, min), deadzone);
    }

    /**
     * https://www.desmos.com/calculator/ybuyhcfzgm
     *
     * @param input x
     * @param fast f
     * @param slow s
     * @param fastX b, fastX &lt; slowX
     * @param slowX a
     * @return y
     */
    public static double deadzone(double input, boolean allowNegativeValues, double fast,
        double slow, double fastX, double slowX) {
        double slope = (fast - slow) / (fastX - slowX);
        double absInput = Math.abs(input);
        double rawResult = slope * (absInput - slowX) + slow;
        if (absInput < slowX) {
            rawResult = slow;
        } else if (absInput > fastX) {
            rawResult = fast;
        }
        rawResult = Math.copySign(rawResult, input);
        if (allowNegativeValues) {
            return rawResult;
        }
        if (rawResult < 0) {
            rawResult = slow;
        }
        return rawResult;
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
    public static double deadzone(double axis, double deadzone) {
        double baseDeadzone = (Math.abs(axis) > Math.abs(deadzone)) ? axis : 0;
        return baseDeadzone != 0 ? (baseDeadzone - Math.copySign(deadzone, baseDeadzone)) / (1
            - deadzone) : 0;
    }
}
