/**
 * Utilities for executing motion profiles.
 *
 * <h1>Summary</h1>
 *
 * This package's main business logic is in the {@link org.team1540.base.motionprofiling.FollowProfile}
 * command, which follows a given Pathfinder motion profile. (A technical explanation of the
 * profile-following algorithm can be found in the {@link org.team1540.base.motionprofiling.FollowProfile}
 * documentation.) Additionally, this package contains {@link org.team1540.base.motionprofiling.FollowProfileFactory},
 * a class to create multiple {@code FollowProfile} instances using common configuration values (for
 * example, multiple autonomous routines for a drivetrain).
 *
 * <h1>A Note on Units</h1>
 * Many problems relating to motion profiling stem from units not being converted properly. As such,
 * to simplify things, this package is unit-agnostic (with the exception of heading which is
 * required to be in radians) and does no unit conversion on its own, instead asking that provided
 * coefficients incorporate necessary unit conversion. When used in package documentation, the term
 * <em>profile units</em> means the units used in motion profiles given to the {@link
 * org.team1540.base.motionprofiling.FollowProfile} command (usually inches, feet or meters); the
 * term <em>native units</em> means the units passed to the motor controllers (usually in terms of
 * raw encoder counts); and the term <em>bump units</em> means the units passed to the throttle-bump
 * parameter of {@link org.team1540.base.motionprofiling.SetpointConsumer#set(double, double)
 * SetpointConsumer.set()} (usually percentage of motor throttle).
 */
package org.team1540.base.motionprofiling;
