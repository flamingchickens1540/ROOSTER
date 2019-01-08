package org.team1540.rooster.drive.pipeline;

import java.util.Objects;
import java.util.OptionalDouble;
import java.util.function.Function;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * Encapsulates drive commands for a tank drive.
 *
 * {@code TankDriveData} instances are usually passed around in drive pipelines. Each {@code
 * TankDriveData} instance contains {@link DriveData} instances for the left and right side, as well
 * as commands for {@linkplain #heading} and {@linkplain #turningRate turning rate}.
 *
 * @see DriveData
 */
@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class TankDriveData {

  /**
   * The drive data for the left side.
   */
  @NotNull
  public final DriveData left;
  /**
   * The drive data for the right side.
   */
  @NotNull
  public final DriveData right;
  /**
   * The desired heading in radians from 0 (straight forward) to 2&pi;, increasing clockwise, or an
   * empty optional if heading should not be controlled.
   */
  @NotNull
  public final OptionalDouble heading;
  /**
   * The desired turning rate in radians/sec, or an empty optional if turning rate should not be
   * controlled.
   */
  @NotNull
  public final OptionalDouble turningRate;

  /**
   * Creates a new {@code TankDriveData} with all fields empty.
   */
  public TankDriveData() {
    this(new DriveData(), new DriveData(), OptionalDouble.empty(), OptionalDouble.empty());
  }

  /**
   * Creates a copy of this {@code TankDriveData} with a modified {@link #heading}.
   *
   * @param function A {@link Function} that takes this {@code DriveData}'s current {@link #heading}
   * and returns a new {@link #heading}.
   * @return A copy of this {@code TankDriveData} with all fields identical except for the {@link
   * #heading} field.
   */
  @NotNull
  @Contract(value = "_ -> new", pure = true)
  public TankDriveData modifyHeading(@NotNull Function<OptionalDouble, OptionalDouble> function) {
    return new TankDriveData(left, right, function.apply(heading), turningRate);
  }

  /**
   * Creates a copy of this {@code TankDriveData} with a modified {@link #turningRate}.
   *
   * @param function A {@link Function} that takes this {@code DriveData}'s current {@link
   * #turningRate} and returns a new {@link #turningRate}.
   * @return A copy of this {@code TankDriveData} with all fields identical except for the {@link
   * #turningRate} field.
   */
  @NotNull
  @Contract(value = "_ -> new", pure = true)
  public TankDriveData modifyTurningRate(
      @NotNull Function<OptionalDouble, OptionalDouble> function) {
    return new TankDriveData(left, right, heading, function.apply(turningRate));
  }

  /**
   * Creates a copy of this {@code TankDriveData} with a modified left {@link DriveData#position
   * position}.
   *
   * @param function A {@link Function} that takes this {@code DriveData}'s current left {@link
   * DriveData#position position} and returns a new {@link DriveData#position position}.
   * @return A copy of this {@code TankDriveData} with all fields identical except for the left
   * {@link DriveData#position position} field.
   */
  @NotNull
  @Contract(value = "_ -> new", pure = true)
  public TankDriveData modifyLeftPosition(
      @NotNull Function<OptionalDouble, OptionalDouble> function) {
    return new TankDriveData(left.modifyPosition(function), right, heading, turningRate);
  }

  /**
   * Creates a copy of this {@code TankDriveData} with a modified left {@link DriveData#velocity
   * velocity}.
   *
   * @param function A {@link Function} that takes this {@code DriveData}'s current left {@link
   * DriveData#velocity velocity} and returns a new {@link DriveData#velocity velocity}.
   * @return A copy of this {@code TankDriveData} with all fields identical except for the left
   * {@link DriveData#velocity velocity} field.
   */
  @NotNull
  @Contract(value = "_ -> new", pure = true)
  public TankDriveData modifyLeftVelocity(
      @NotNull Function<OptionalDouble, OptionalDouble> function) {
    return new TankDriveData(left.modifyVelocity(function), right, heading, turningRate);
  }

  /**
   * Creates a copy of this {@code TankDriveData} with a modified left {@link DriveData#acceleration
   * acceleration}.
   *
   * @param function A {@link Function} that takes this {@code DriveData}'s current left {@link
   * DriveData#acceleration acceleration} and returns a new {@link DriveData#acceleration
   * acceleration}.
   * @return A copy of this {@code TankDriveData} with all fields identical except for the left
   * {@link DriveData#acceleration acceleration} field.
   */
  @NotNull
  @Contract(value = "_ -> new", pure = true)
  public TankDriveData modifyLeftAcceleration(
      @NotNull Function<OptionalDouble, OptionalDouble> function) {
    return new TankDriveData(left.modifyAcceleration(function), right, heading, turningRate);
  }

  /**
   * Creates a copy of this {@code TankDriveData} with a modified left {@link
   * DriveData#additionalFeedForward additionalFeedForward}.
   *
   * @param function A {@link Function} that takes this {@code DriveData}'s current left {@link
   * DriveData#additionalFeedForward additionalFeedForward} and returns a new {@link
   * DriveData#additionalFeedForward additionalFeedForward}.
   * @return A copy of this {@code TankDriveData} with all fields identical except for the left
   * {@link DriveData#additionalFeedForward additionalFeedForward} field.
   */
  @NotNull
  @Contract(value = "_ -> new", pure = true)
  public TankDriveData modifyLeftAdditionalFeedForward(
      @NotNull Function<OptionalDouble, OptionalDouble> function) {
    return new TankDriveData(left.modifyAdditionalFeedForward(function), right, heading,
        turningRate);
  }

  /**
   * Creates a copy of this {@code TankDriveData} with a modified right {@link DriveData#position
   * position}.
   *
   * @param function A {@link Function} that takes this {@code DriveData}'s current right {@link
   * DriveData#position position} and returns a new {@link DriveData#position position}.
   * @return A copy of this {@code TankDriveData} with all fields identical except for the right
   * {@link DriveData#position position} field.
   */
  @NotNull
  @Contract(value = "_ -> new", pure = true)
  public TankDriveData modifyRightPosition(
      @NotNull Function<OptionalDouble, OptionalDouble> function) {
    return new TankDriveData(left, right.modifyPosition(function), heading, turningRate);
  }

  /**
   * Creates a copy of this {@code TankDriveData} with a modified right {@link DriveData#velocity
   * velocity}.
   *
   * @param function A {@link Function} that takes this {@code DriveData}'s current right {@link
   * DriveData#velocity velocity} and returns a new {@link DriveData#velocity velocity}.
   * @return A copy of this {@code TankDriveData} with all fields identical except for the right
   * {@link DriveData#velocity velocity} field.
   */
  @NotNull
  @Contract(value = "_ -> new", pure = true)
  public TankDriveData modifyRightVelocity(
      @NotNull Function<OptionalDouble, OptionalDouble> function) {
    return new TankDriveData(left, right.modifyVelocity(function), heading, turningRate);
  }

  /**
   * Creates a copy of this {@code TankDriveData} with a modified right {@link
   * DriveData#acceleration acceleration}.
   *
   * @param function A {@link Function} that takes this {@code DriveData}'s current right {@link
   * DriveData#acceleration acceleration} and returns a new {@link DriveData#acceleration
   * acceleration}.
   * @return A copy of this {@code TankDriveData} with all fields identical except for the right
   * {@link DriveData#acceleration acceleration} field.
   */
  @NotNull
  @Contract(value = "_ -> new", pure = true)
  public TankDriveData modifyRightAcceleration(
      @NotNull Function<OptionalDouble, OptionalDouble> function) {
    return new TankDriveData(left, right.modifyAcceleration(function), heading, turningRate);
  }

  /**
   * Creates a copy of this {@code TankDriveData} with a modified right {@link
   * DriveData#additionalFeedForward additionalFeedForward}.
   *
   * @param function A {@link Function} that takes this {@code DriveData}'s current right {@link
   * DriveData#additionalFeedForward additionalFeedForward} and returns a new {@link
   * DriveData#additionalFeedForward additionalFeedForward}.
   * @return A copy of this {@code TankDriveData} with all fields identical except for the right
   * {@link DriveData#additionalFeedForward additionalFeedForward} field.
   */
  @NotNull
  @Contract(value = "_ -> new", pure = true)
  public TankDriveData modifyRightAdditionalFeedForward(
      @NotNull Function<OptionalDouble, OptionalDouble> function) {
    return new TankDriveData(left, right.modifyAdditionalFeedForward(function), heading,
        turningRate);
  }

  /**
   * Creates a copy of this {@code TankDriveData} with modified left and right positions.
   *
   * @param lfunc A {@link Function} that takes this {@code DriveData}'s current left {@link
   * DriveData#position position} and returns a new {@link DriveData#position position}.
   * @param rfunc A {@link Function} that takes this {@code DriveData}'s current right {@link
   * DriveData#position position} and returns a new {@link DriveData#position position}.
   * @return A copy of this {@code TankDriveData} with all fields identical except for the left and
   * right {@link DriveData#additionalFeedForward additionalFeedForward} fields.
   */
  @NotNull
  @Contract(value = "_, _ -> new", pure = true)
  public TankDriveData modifyPosition(@NotNull Function<OptionalDouble, OptionalDouble> lfunc,
      @NotNull Function<OptionalDouble, OptionalDouble> rfunc) {
    return new TankDriveData(left.modifyPosition(lfunc), right.modifyPosition(rfunc), heading,
        turningRate);
  }

  /**
   * Creates a copy of this {@code TankDriveData} with modified left and right velocitys.
   *
   * @param lfunc A {@link Function} that takes this {@code DriveData}'s current left {@link
   * DriveData#velocity velocity} and returns a new {@link DriveData#velocity velocity}.
   * @param rfunc A {@link Function} that takes this {@code DriveData}'s current right {@link
   * DriveData#velocity velocity} and returns a new {@link DriveData#velocity velocity}.
   * @return A copy of this {@code TankDriveData} with all fields identical except for the left and
   * right {@link DriveData#additionalFeedForward additionalFeedForward} fields.
   */
  @NotNull
  @Contract(value = "_, _ -> new", pure = true)
  public TankDriveData modifyVelocity(@NotNull Function<OptionalDouble, OptionalDouble> lfunc,
      @NotNull Function<OptionalDouble, OptionalDouble> rfunc) {
    return new TankDriveData(left.modifyVelocity(lfunc), right.modifyVelocity(rfunc), heading,
        turningRate);
  }


  /**
   * Creates a copy of this {@code TankDriveData} with modified left and right accelerations.
   *
   * @param lfunc A {@link Function} that takes this {@code DriveData}'s current left {@link
   * DriveData#acceleration acceleration} and returns a new {@link DriveData#acceleration
   * acceleration}.
   * @param rfunc A {@link Function} that takes this {@code DriveData}'s current right {@link
   * DriveData#acceleration acceleration} and returns a new {@link DriveData#acceleration
   * acceleration}.
   * @return A copy of this {@code TankDriveData} with all fields identical except for the left and
   * right {@link DriveData#additionalFeedForward additionalFeedForward} fields.
   */
  @NotNull
  @Contract(value = "_, _ -> new", pure = true)
  public TankDriveData modifyAcceleration(@NotNull Function<OptionalDouble, OptionalDouble> lfunc,
      @NotNull Function<OptionalDouble, OptionalDouble> rfunc) {
    return new TankDriveData(left.modifyAcceleration(lfunc), right.modifyAcceleration(rfunc),
        heading,
        turningRate);
  }

  /**
   * Creates a copy of this {@code TankDriveData} with modified left and right feed-forwards.
   *
   * @param lfunc A {@link Function} that takes this {@code DriveData}'s current left {@link
   * DriveData#additionalFeedForward additionalFeedForward} and returns a new {@link
   * DriveData#additionalFeedForward additionalFeedForward}.
   * @param rfunc A {@link Function} that takes this {@code DriveData}'s current right {@link
   * DriveData#additionalFeedForward additionalFeedForward} and returns a new {@link
   * DriveData#additionalFeedForward additionalFeedForward}.
   * @return A copy of this {@code TankDriveData} with all fields identical except for the left and
   * right {@link DriveData#additionalFeedForward additionalFeedForward} fields.
   */
  @NotNull
  @Contract(value = "_, _ -> new", pure = true)
  public TankDriveData modifyAdditionalFeedForward(
      @NotNull Function<OptionalDouble, OptionalDouble> lfunc,
      @NotNull Function<OptionalDouble, OptionalDouble> rfunc) {
    return new TankDriveData(left.modifyAdditionalFeedForward(lfunc),
        right.modifyAdditionalFeedForward(rfunc), heading,
        turningRate);
  }

  /**
   * Adds the provided value to this {@code TankDriveData}'s {@link #heading}. If there is already a
   * value present in {@link #heading}, the returned {@code TankDriveData}'s {@link #heading} will
   * be equal to the sum of that value plus the parameter; otherwise, the left {@link #heading} will
   * be equal to the value of the provided heading parameter.
   *
   * @param heading The heading to add.
   * @return A new {@code TankDriveData} with all fields identical except for the left {@link
   * #heading} field.
   */
  @NotNull
  @Contract(value = "_ -> new", pure = true)
  public TankDriveData plusHeading(double heading) {
    return modifyHeading((old) -> OptionalDouble.of(old.orElse(0) + heading));
  }

  /**
   * Adds the provided value to this {@code TankDriveData}'s {@link #turningRate}. If there is
   * already a value present in {@link #turningRate}, the returned {@code TankDriveData}'s {@link
   * #turningRate} will be equal to the sum of that value plus the parameter; otherwise, the left
   * {@link #turningRate} will be equal to the value of the provided turningRate parameter.
   *
   * @param turningRate The turningRate to add.
   * @return A new {@code TankDriveData} with all fields identical except for the left {@link
   * #turningRate} field.
   */
  @NotNull
  @Contract(value = "_ -> new", pure = true)
  public TankDriveData plusTurningRate(double turningRate) {
    return modifyTurningRate((old) -> OptionalDouble.of(old.orElse(0) + turningRate));
  }


  /**
   * Adds the provided value to this {@code TankDriveData}'s left {@link DriveData#position
   * position}. If there is already a value present in {@link #left left.}{@link DriveData#position
   * position}, the returned {@code TankDriveData}'s left {@link DriveData#position position} will
   * be equal to the sum of that value plus the parameter; otherwise, the left {@link
   * DriveData#position position} will be equal to the value of the provided position parameter.
   *
   * @param position The position to add.
   * @return A new {@code TankDriveData} with all fields identical except for the left {@link
   * DriveData#position position} field.
   */
  @NotNull
  @Contract(value = "_ -> new", pure = true)
  public TankDriveData plusLeftPosition(double position) {
    return new TankDriveData(left.plusPosition(position), right, heading, turningRate);
  }

  /**
   * Adds the provided value to this {@code TankDriveData}'s left {@link DriveData#velocity
   * velocity}. If there is already a value present in {@link #left left.}{@link DriveData#velocity
   * velocity}, the returned {@code TankDriveData}'s left {@link DriveData#velocity velocity} will
   * be equal to the sum of that value plus the parameter; otherwise, the left {@link
   * DriveData#velocity velocity} will be equal to the value of the provided velocity parameter.
   *
   * @param velocity The velocity to add.
   * @return A new {@code TankDriveData} with all fields identical except for the left {@link
   * DriveData#velocity velocity} field.
   */
  @NotNull
  @Contract(value = "_ -> new", pure = true)
  public TankDriveData plusLeftVelocity(double velocity) {
    return new TankDriveData(left.plusVelocity(velocity), right, heading, turningRate);
  }


  /**
   * Adds the provided value to this {@code TankDriveData}'s left {@link DriveData#acceleration
   * acceleration}. If there is already a value present in {@link #left left.}{@link
   * DriveData#acceleration acceleration}, the returned {@code TankDriveData}'s left {@link
   * DriveData#acceleration acceleration} will be equal to the sum of that value plus the parameter;
   * otherwise, the left {@link DriveData#acceleration acceleration} will be equal to the value of
   * the provided acceleration parameter.
   *
   * @param acceleration The acceleration to add.
   * @return A new {@code TankDriveData} with all fields identical except for the left {@link
   * DriveData#acceleration acceleration} field.
   */
  @NotNull
  @Contract(value = "_ -> new", pure = true)
  public TankDriveData plusLeftAcceleration(double acceleration) {
    return new TankDriveData(left.plusAcceleration(acceleration), right, heading, turningRate);
  }

  /**
   * Adds the provided value to this {@code TankDriveData}'s left {@link
   * DriveData#additionalFeedForward additionalFeedForward}. If there is already a value present in
   * {@link #left left.}{@link DriveData#additionalFeedForward additionalFeedForward}, the returned
   * {@code TankDriveData}'s left {@link DriveData#additionalFeedForward additionalFeedForward} will
   * be equal to the sum of that value plus the parameter; otherwise, the left {@link
   * DriveData#additionalFeedForward additionalFeedForward} will be equal to the value of the
   * provided additionalFeedForward parameter.
   *
   * @param additionalFeedForward The additionalFeedForward to add.
   * @return A new {@code TankDriveData} with all fields identical except for the left {@link
   * DriveData#additionalFeedForward additionalFeedForward} field.
   */
  @NotNull
  @Contract(value = "_ -> new", pure = true)
  public TankDriveData plusLeftAdditionalFeedForward(double additionalFeedForward) {
    return new TankDriveData(left.plusAdditionalFeedForward(additionalFeedForward), right, heading,
        turningRate);
  }


  /**
   * Adds the provided value to this {@code TankDriveData}'s right {@link DriveData#position
   * position}. If there is already a value present in {@link #right right.}{@link
   * DriveData#position position}, the returned {@code TankDriveData}'s right {@link
   * DriveData#position position} will be equal to the sum of that value plus the parameter;
   * otherwise, the right {@link DriveData#position position} will be equal to the value of the
   * provided position parameter.
   *
   * @param position The position to add.
   * @return A new {@code TankDriveData} with all fields identical except for the right {@link
   * DriveData#position position} field.
   */
  @NotNull
  @Contract(value = "_ -> new", pure = true)
  public TankDriveData plusRightPosition(double position) {
    return new TankDriveData(left, right.plusPosition(position), heading, turningRate);
  }

  /**
   * Adds the provided value to this {@code TankDriveData}'s right {@link DriveData#velocity
   * velocity}. If there is already a value present in {@link #right right.}{@link
   * DriveData#velocity velocity}, the returned {@code TankDriveData}'s right {@link
   * DriveData#velocity velocity} will be equal to the sum of that value plus the parameter;
   * otherwise, the right {@link DriveData#velocity velocity} will be equal to the value of the
   * provided velocity parameter.
   *
   * @param velocity The velocity to add.
   * @return A new {@code TankDriveData} with all fields identical except for the right {@link
   * DriveData#velocity velocity} field.
   */
  @NotNull
  @Contract(value = "_ -> new", pure = true)
  public TankDriveData plusRightVelocity(double velocity) {
    return new TankDriveData(left, right.plusVelocity(velocity), heading, turningRate);
  }


  /**
   * Adds the provided value to this {@code TankDriveData}'s right {@link DriveData#acceleration
   * acceleration}. If there is already a value present in {@link #right right.}{@link
   * DriveData#acceleration acceleration}, the returned {@code TankDriveData}'s right {@link
   * DriveData#acceleration acceleration} will be equal to the sum of that value plus the parameter;
   * otherwise, the right {@link DriveData#acceleration acceleration} will be equal to the value of
   * the provided acceleration parameter.
   *
   * @param acceleration The acceleration to add.
   * @return A new {@code TankDriveData} with all fields identical except for the right {@link
   * DriveData#acceleration acceleration} field.
   */
  @NotNull
  @Contract(value = "_ -> new", pure = true)
  public TankDriveData plusRightAcceleration(double acceleration) {
    return new TankDriveData(left, right.plusAcceleration(acceleration), heading, turningRate);
  }

  /**
   * Adds the provided value to this {@code TankDriveData}'s right {@link
   * DriveData#additionalFeedForward additionalFeedForward}. If there is already a value present in
   * {@link #right right.}{@link DriveData#additionalFeedForward additionalFeedForward}, the
   * returned {@code TankDriveData}'s right {@link DriveData#additionalFeedForward
   * additionalFeedForward} will be equal to the sum of that value plus the parameter; otherwise,
   * the right {@link DriveData#additionalFeedForward additionalFeedForward} will be equal to the
   * value of the provided additionalFeedForward parameter.
   *
   * @param additionalFeedForward The additionalFeedForward to add.
   * @return A new {@code TankDriveData} with all fields identical except for the right {@link
   * DriveData#additionalFeedForward additionalFeedForward} field.
   */
  @NotNull
  @Contract(value = "_ -> new", pure = true)
  public TankDriveData plusRightAdditionalFeedForward(double additionalFeedForward) {
    return new TankDriveData(left, right.plusAdditionalFeedForward(additionalFeedForward), heading,
        turningRate);
  }

  /**
   * Adds the provided values to this {@code TankDriveData}'s left and right {@link
   * DriveData#position position}. If there is already a value present in either {@link
   * DriveData#position position}, the returned {@code TankDriveData}'s {@link DriveData#position
   * positions} will be equal to the sum of that value plus the parameter; otherwise, the {@link
   * DriveData#position position} will be equal to the value of the provided parameter for each
   * side.
   *
   * @param leftPosition The position to add to the left side.
   * @param rightPosition The position to add to the right side.
   * @return A new {@code TankDriveData} with all fields identical except for the left and right
   * {@link DriveData#position position} fields.
   */
  @NotNull
  @Contract(value = "_, _ -> new", pure = true)
  public TankDriveData plusPositions(double leftPosition, double rightPosition) {
    return new TankDriveData(left.plusPosition(leftPosition), right.plusPosition(rightPosition),
        heading, turningRate);
  }

  /**
   * Adds the provided values to this {@code TankDriveData}'s left and right {@link
   * DriveData#velocity velocity}. If there is already a value present in either {@link
   * DriveData#velocity velocity}, the returned {@code TankDriveData}'s {@link DriveData#velocity
   * velocities} will be equal to the sum of that value plus the parameter; otherwise, the {@link
   * DriveData#velocity velocity} will be equal to the value of the provided parameter for each
   * side.
   *
   * @param leftVelocity The velocity to add to the left side.
   * @param rightVelocity The velocity to add to the right side.
   * @return A new {@code TankDriveData} with all fields identical except for the left and right
   * {@link DriveData#velocity velocity} fields.
   */
  @NotNull
  @Contract(value = "_, _ -> new", pure = true)
  public TankDriveData plusVelocities(double leftVelocity, double rightVelocity) {
    return new TankDriveData(left.plusVelocity(leftVelocity), right.plusVelocity(rightVelocity),
        heading, turningRate);
  }

  /**
   * Adds the provided values to this {@code TankDriveData}'s left and right {@link
   * DriveData#acceleration acceleration}. If there is already a value present in either {@link
   * DriveData#acceleration acceleration}, the returned {@code TankDriveData}'s {@link
   * DriveData#acceleration accelerations} will be equal to the sum of that value plus the
   * parameter; otherwise, the {@link DriveData#acceleration acceleration} will be equal to the
   * value of the provided parameter for each side.
   *
   * @param leftAcceleration The acceleration to add to the left side.
   * @param rightAcceleration The acceleration to add to the right side.
   * @return A new {@code TankDriveData} with all fields identical except for the left and right
   * {@link DriveData#acceleration acceleration} fields.
   */
  @NotNull
  @Contract(value = "_, _ -> new", pure = true)
  public TankDriveData plusAccelerations(double leftAcceleration, double rightAcceleration) {
    return new TankDriveData(left.plusAcceleration(leftAcceleration),
        right.plusAcceleration(rightAcceleration),
        heading, turningRate);
  }

  /**
   * Adds the provided values to this {@code TankDriveData}'s left and right {@link
   * DriveData#additionalFeedForward additionalFeedForward}. If there is already a value present in
   * either {@link DriveData#additionalFeedForward additionalFeedForward}, the returned {@code
   * TankDriveData}'s {@link DriveData#additionalFeedForward additionalFeedForwards} will be equal
   * to the sum of that value plus the parameter; otherwise, the {@link
   * DriveData#additionalFeedForward additionalFeedForward} will be equal to the value of the
   * provided parameter for each side.
   *
   * @param leftAdditionalFeedForward The additionalFeedForward to add to the left side.
   * @param rightAdditionalFeedForward The additionalFeedForward to add to the right side.
   * @return A new {@code TankDriveData} with all fields identical except for the left and right
   * {@link DriveData#additionalFeedForward additionalFeedForward} fields.
   */
  @NotNull
  @Contract(value = "_, _ -> new", pure = true)
  public TankDriveData plusAdditionalFeedForwards(double leftAdditionalFeedForward,
      double rightAdditionalFeedForward) {
    return new TankDriveData(left.plusAdditionalFeedForward(leftAdditionalFeedForward),
        right.plusAdditionalFeedForward(rightAdditionalFeedForward),
        heading, turningRate);
  }


  /**
   * Creates a copy of this {@code TankDriveData} and sets its {@link #heading}. If there is already
   * a value present in {@link #heading}, it will be overwritten.
   *
   * @param heading The new heading.
   * @return A {@code TankDriveData} with all fields identical except for {@link #heading}.
   */
  @NotNull
  @Contract(value = "_ -> new", pure = true)
  public TankDriveData withHeading(double heading) {
    return new TankDriveData(left, right, OptionalDouble.of(heading), turningRate);
  }

  /**
   * Creates a copy of this {@code TankDriveData} and sets its {@link #turningRate}. If there is
   * already a value present in {@link #turningRate}, it will be overwritten.
   *
   * @param turningRate The new turningRate.
   * @return A {@code TankDriveData} with all fields identical except for {@link #turningRate}.
   */
  @NotNull
  @Contract(value = "_ -> new", pure = true)
  public TankDriveData withTurningRate(double turningRate) {
    return new TankDriveData(left, right, heading, OptionalDouble.of(turningRate));
  }

  /**
   * Creates a copy of this {@code TankDriveData} and sets its left and right {@link
   * DriveData#position positions}. If there is already a value present in either {@link
   * DriveData#position position}, it will be overwritten during copying.
   *
   * @param leftPosition The position to set on the left side.
   * @param rightPosition The position to set on the right side.
   * @return A new {@code TankDriveData} with all fields identical except for the left and right
   * {@link DriveData#position position} fields.
   */
  @NotNull
  @Contract(value = "_, _ -> new", pure = true)
  public TankDriveData withPositions(double leftPosition, double rightPosition) {
    return new TankDriveData(left.withPosition(leftPosition), right.withPosition(rightPosition),
        heading, turningRate);
  }

  /**
   * Creates a copy of this {@code TankDriveData} and sets its left and right {@link
   * DriveData#velocity velocities}. If there is already a value present in either {@link
   * DriveData#velocity velocity}, it will be overwritten during copying.
   *
   * @param leftVelocity The velocity to set on the left side.
   * @param rightVelocity The velocity to set on the right side.
   * @return A new {@code TankDriveData} with all fields identical except for the left and right
   * {@link DriveData#velocity velocity} fields.
   */
  @NotNull
  @Contract(value = "_, _ -> new", pure = true)
  public TankDriveData withVelocities(double leftVelocity, double rightVelocity) {
    return new TankDriveData(left.withVelocity(leftVelocity), right.withVelocity(rightVelocity),
        heading, turningRate);
  }

  /**
   * Creates a copy of this {@code TankDriveData} and sets its left and right {@link
   * DriveData#acceleration accelerations}. If there is already a value present in either {@link
   * DriveData#acceleration acceleration}, it will be overwritten during copying.
   *
   * @param leftAcceleration The acceleration to set on the left side.
   * @param rightAcceleration The acceleration to set on the right side.
   * @return A new {@code TankDriveData} with all fields identical except for the left and right
   * {@link DriveData#acceleration acceleration} fields.
   */
  @NotNull
  @Contract(value = "_, _ -> new", pure = true)
  public TankDriveData withAccelerations(double leftAcceleration, double rightAcceleration) {
    return new TankDriveData(left.withAcceleration(leftAcceleration),
        right.withAcceleration(rightAcceleration), heading, turningRate);
  }

  /**
   * Creates a copy of this {@code TankDriveData} and sets its left and right {@link
   * DriveData#additionalFeedForward additionalFeedForwards}. If there is already a value present in
   * either {@link DriveData#additionalFeedForward additionalFeedForward}, it will be overwritten
   * during copying.
   *
   * @param leftAdditionalFeedForward The additionalFeedForward to set on the left side.
   * @param rightAdditionalFeedForward The additionalFeedForward to set on the right side.
   * @return A new {@code TankDriveData} with all fields identical except for the left and right
   * {@link DriveData#additionalFeedForward additionalFeedForward} fields.
   */
  @NotNull
  @Contract(value = "_, _ -> new", pure = true)
  public TankDriveData withAdditionalFeedForwards(double leftAdditionalFeedForward,
      double rightAdditionalFeedForward) {
    return new TankDriveData(left.withAdditionalFeedForward(leftAdditionalFeedForward),
        right.withAdditionalFeedForward(rightAdditionalFeedForward), heading, turningRate);
  }

  /**
   * Creates a new {@code TankDriveData} with the supplied values.
   *
   * @param left The {@link DriveData} for the left side.
   * @param right The {@link DriveData} for the right side.
   * @param heading The desired heading in radians from 0 (straight forward) to 2&pi;, increasing
   * clockwise, or an empty optional if heading should not be controlled.
   * @param turningRate The desired turning rate in radians/sec, or an empty optional if turning
   * rate should not be controlled.
   */
  public TankDriveData(@NotNull DriveData left, @NotNull DriveData right,
      @NotNull OptionalDouble heading,
      @NotNull OptionalDouble turningRate) {
    this.left = Objects.requireNonNull(left);
    this.right = Objects.requireNonNull(right);
    this.heading = Objects.requireNonNull(heading);
    this.turningRate = Objects.requireNonNull(turningRate);
  }

  @Override
  public String toString() {
    return "TankDriveData: " + "left: " + left
        + ", right:" + right
        + (heading.isPresent() ? ", heading " + heading.getAsDouble() : "")
        + (turningRate.isPresent() ? ", turning rate " + turningRate.getAsDouble() : "");
  }

  @Override
  @Contract(value = "null -> false", pure = true)
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof TankDriveData)) {
      return false;
    }
    TankDriveData that = (TankDriveData) o;
    return left.equals(that.left) &&
        right.equals(that.right) &&
        heading.equals(that.heading) &&
        turningRate.equals(that.turningRate);
  }

  @Override
  public int hashCode() {
    return Objects.hash(left, right, heading, turningRate);
  }
}
