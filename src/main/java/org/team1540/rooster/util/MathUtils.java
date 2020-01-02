package org.team1540.rooster.util;

import org.jetbrains.annotations.Contract;

public class MathUtils {

    /**
     * Inverts the provided value if {@code shouldInvert} is {@code true}.
     *
     * @param shouldInvert Whether to invert the number.
     * @param toInvert The number to invert.
     * @return If {@code shouldInvert} is {@code true}, {@code -toInvert}; otherwise, {@code
     * toInvert}
     */
    @Contract(pure = true)
    public static double negateDoubleIf(boolean shouldInvert, double toInvert) {
        return (shouldInvert ? -1 : 1) * toInvert;
    }

    /**
     * Constrains an input to a given range in either direction from zero.
     *
     * This does <strong>not</strong> map the input to the range; it simply hard-caps it when it's
     * outside.
     *
     * @param input The input to constrain.
     * @param cap The distance from zero to constrain {@code input} to.
     * @return If {@code input} &gt; {@code cap}, return {@code cap}; if {@code input} &lt; {@code
     * -cap}, return {@code -cap}; otherwise, return {@code input}.
     */
    @Contract(pure = true)
    public static double constrain(double input, double cap) {
        if (cap < 0) {
            throw new IllegalArgumentException("Cap cannot be negative");
        }

        return constrain(input, -cap, cap);
    }

    /**
     * Constrains  an input to a given range.
     *
     * This does <strong>not</strong> map the input to the range; it simply hard-caps it when it's
     * outside.
     *
     * @param input The input to constrain.
     * @param lowerCap The lower bound of the range.
     * @param upperCap The upper bound of the range.
     * @return If {@code input} &gt; {@code upperCap}, return {@code upperCap}; if {@code input}
     * &lt; {@code lowerCap}, return {@code lowerCap}; otherwise, return {@code input}.
     */
    @Contract(pure = true)
    public static double constrain(double input, double lowerCap, double upperCap) {
        if (lowerCap > upperCap) {
            throw new IllegalArgumentException("Lower cap cannot be less than upper cap");
        }

        if (input < lowerCap) {
            return lowerCap;
        } else if (input > upperCap) {
            return upperCap;
        } else {
            return input;
        }
    }

    /**
     * Raises the input to the provided power while preserving the sign. Useful for joystick
     * scaling.
     *
     * @param input The input to be raised.
     * @param pow The power.
     * @return The input raised to the provided power, with the sign of the input.
     */
    @Contract(pure = true)
    public static double preserveSignRaiseToPower(double input, double pow) {
        return Math.copySign(Math.pow(Math.abs(input), pow), input);
    }
}
