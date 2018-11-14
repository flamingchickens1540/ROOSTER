# Drive Pipelines

Drive pipelines are an extensible method to create multiple different ways of driving a robot. Since pipelines are just lambdas, they are highly customizable.

Some "stock" classes (inputs, processors, and outputs) are available in the `org.team1540.base.drive.pipeline` package.

## What's a Drive Pipeline?


A pipeline can be used for almost any concievable method of drivetrain control, including standard open-loop teleop drive, closed-loop motion profiling, and anywhere in between.

Pipelines consist of three different kinds of stages: *inputs*, *processors*, and *outputs*. Since inputs, processors, and outputs are just `Suppliers`, `Functions`, and `Consumers` respectively, they can be extended easily and flexibly.

- An input produces target values for processing; for example, an input could get current values from the driver's joysticks, or the currently executing point in a motion profile.

- A processor, well, processes values; for example, a closed-loop processor might take a desired position, velocity, and/or acceleration and convert them into setpoints, feed-forwards, etc. to send to motors. Note that processors can receive data from things that are not the currently configured input; for example, a gyro.

- An output turns values from a processor into commands for motors or other things. An output for Talon SRX motors might just pass a setpoint to the Talons' native closed-loop functionality, while an output for PWM motors might perform additional PID logic.



## Examples

### Hello World

Control two talons in open-loop tank drive:

```java
Executable pipeline = new SimpleJoystickInput(new Joystick(0), 1, 5, false, false)
    .then(new TalonSRXOutput(leftTalon, rightTalon));
```

Breakdown:

- ` SimpleJoystickInput(new Joystick(0), 1, 5, false, false)`: Takes values from a joystick on port 0, with axis 1 as the left and axis 5 on the right, inverting neither
- `.then(new TalonSRXOutput(leftTalon, rightTalon))`: Sends the output of the previous `SimpleJoystickInput` to your `leftTalon` and `rightTalon`. Since the output of `SimpleJoystickInput` only sets the feed-forward (i.e. raw throttle) term, it'll automatically use `PercentOutput` output mode.

### Execute a Motion Profile

```java
Executable pipeline = new ProfileInput(leftProfile, rightProfile)
    .then(new OpenLoopFeedForward(kV, vIntercept, kA))
    .then(new TalonSRXOutput(leftTalon, rightTalon));
```

Breakdown:

- `ProfileInput` takes values from two provided `MotionProfile` instances and returns the setpoint for the current time. The timer starts when the pipeline is first executed.
- `OpenLoopFeedForward` takes the velocity and acceleration setpoints from the `ProfileInput` and calculates a suitable feed-forward for them using coefficients you provide, Oblarg-style. It then passes those velocities down.
- `TalonSRXOutput`, since it's receiving position setpoints from the `ProfileInput`, tells the Talon closed-loop to PID to those setpoints while providing the feed-forward from the `OpenLoopFeedForward` as an additional bump term.

### Use in a Command

A properly composed pipeline (i.e. with an input on one end and an output on the other) implements `Executable`, so it can be used as the argument to a `SimpleCommand` or `SimpleLoopCommand`:

```java
// drivePipeline contains your pipeline
Command command = new SimpleLoopCommand("Drive", drivePipeline, Robot.driveTrain);
```

### Custom Logic

Most "stock" pipeline elements pass around `TankDriveData` instances to encapsulate position, velocity, etc. It's possible to define your own data classes but there's not too much reason to.

#### Custom Input

An input that returns the same `TankDriveData` every time:

```java
TankDriveData tankDriveData = new TankDriveData(
    new DriveData(0), 
    new DriveData(0), 
    OptionalDouble.empty(), 
    OptionalDouble.empty());

Executable pipeline = ((Input) () -> tankDriveData)
    .then(new TalonSRXOutput(leftTalon, rightTalon))
```

#### Custom Processor

A processor that multiplies the received position by two:

```java
Executable pipeline = new SimpleJoystickInput(new Joystick(0), 1, 5, false, false)
    .then(data -> return new TankDriveData(
        new DriveData(
            d.left.position.isPresent() ? OptionalDouble.of(d.left.position.getAsDouble() * 2) : d.left.position,
            d.left.velocity,
            d.left.acceleration,
            d.left.additionalFeedForward
        ),
        new DriveData(
            d.right.position.isPresent() ? OptionalDouble.of(d.right.position.getAsDouble() * 2) : d.right.position,
            d.right.velocity,
            d.right.acceleration,
            d.right.additionalFeedForward
        ),
        d.heading, d.turningRate);
    ))
    .then(new TalonSRXOutput(leftTalon, rightTalon))
```

#### Custom Output

Here's an output that just prints the data it receives instead of sending it to a motor:

```java
Executable pipeline = new SimpleJoystickInput(new Joystick(0), 1, 5, false, false)
    .then(data -> {
        System.out.println(data)
    })
```

