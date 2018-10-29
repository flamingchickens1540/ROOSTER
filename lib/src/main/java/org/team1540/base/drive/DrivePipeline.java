package org.team1540.base.drive;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Pipeline for flexibly controlling a robot drivetrain. A {@code DrivePipeline} can be used for
 * almost any concievable method of drivetrain control, including standard open-loop teleop drive,
 * closed-loop motion profiling, and anywhere in between.
 * <p>
 * Pipelines consist of three stages: an <em>input</em>, a <em>processor</em>, and an
 * <em>output</em>. Since inputs, processors, and outputs are just {@link Supplier Suppliers},
 * {@link Function Functions}, and {@link Consumer Consumers} respectively, they can be extended
 * easily and flexibly.
 * <ul>
 * <li>An <em>input</em> produces target values for processing; for example, an input could get
 * current values from the driver's joysticks, or the currently executing point in a motion
 * profile.</li>
 *
 * <li>A <em>processor</em>, well, processes values; for example, a closed-loop processor
 * might take a desired position, velocity, and/or acceleration and convert them into setpoints,
 * feed-forwards, etc. to send to motors. Note that processors can receive data from things that are
 * not the currently configured input; for example, a gyro.</li>
 *
 * <li>An <em>output</em> turns values from a processor into commands for motors. An output for
 * Talon SRX motors might just pass a setpoint to the Talons' native closed-loop functionality,
 * while an output for PWM motors might perform additional PID logic.</li>
 * </ul>
 *
 * @param <I> The type given by the input stage.
 * @param <O> The type requested by the output stage.
 */
public class DrivePipeline<I, O> {

  private Supplier<I> input;
  private Function<I, O> processor;
  private Consumer<O> output;


  public void execute() {
    output.accept(processor.apply(input.get()));
  }

  public DrivePipeline(Supplier<I> input, Function<I, O> processor, Consumer<O> output) {
    this.input = input;
    this.processor = processor;
    this.output = output;
  }
}
