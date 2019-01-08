package org.team1540.rooster.motionprofiling;

import jaci.pathfinder.Pathfinder;
import java.io.File;
import org.jetbrains.annotations.NotNull;

/**
 * A {@link ProfileContainer} to load paths generated by Jaci's Pathfinder. This {@link
 * ProfileContainer} uses {@link Pathfinder#readFromCSV(File)} to load profiles.
 */
public class PathfinderProfileContainer extends ProfileContainer {

  /**
   * Creates a new {@code PathfinderProfileContainer} that searches the provided directory using a
   * left suffix of "{@code _left.csv}" and a right suffix of "{@code _right.csv}"
   *
   * This constructor also searches the provided directory for profiles and loads all the profiles
   * into RAM. For this reason, initialization may take some time (especially for large amounts of
   * profiles).
   *
   * @param profileDirectory The directory containing the profiles. See the {@linkplain
   * ProfileContainer class documentation} for a description of the folder structure.
   * @throws RuntimeException If an I/O error occurs during profile loading.
   */
  public PathfinderProfileContainer(@NotNull File profileDirectory) {
    super(profileDirectory);
  }

  /**
   * Creates a new {@code PathfinderProfileContainer}. This constructor also searches the provided
   * directory for profiles and loads all the profiles into RAM. For this reason, initialization may
   * take some time (especially for large amounts of profiles).
   *
   * @param profileDirectory The directory containing the profiles. See the {@linkplain
   * ProfileContainer class documentation} for a description of the folder structure.
   * @param leftSuffix The suffix to use to identify left-side profile files.
   * @param rightSuffix The suffix to use to identify right-side profile files.
   * @throws RuntimeException If an I/O error occurs during profile loading.
   */
  public PathfinderProfileContainer(@NotNull File profileDirectory, @NotNull String leftSuffix,
      @NotNull String rightSuffix) {
    super(profileDirectory, leftSuffix, rightSuffix);
  }

  @NotNull
  @Override
  protected MotionProfile readProfile(@NotNull File file) {
    return MotionProfileUtils.createProfile(Pathfinder.readFromCSV(file));
  }
}