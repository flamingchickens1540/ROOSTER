# Simple Utilities

The Simple Utilities, located in the `util` and `triggers` packages, make developing robot code easier and more ergonomic by using [lambdas](https://docs.oracle.com/javase/tutorial/java/javaOO/lambdaexpressions.html) to create WPILib components (`Commands`, `Buttons`, etc.) without having to create an entire anonymous class.

## SimpleButton

[`SimpleButton`](https://flamingchickens1540.github.io/ROOSTER/org/team1540/rooster/triggers/SimpleButton.html) is a way to create a WPILIb `Button` class based off of an arbitrary function returning a `boolean` in one line of code. This is useful if you want to set up complex control schemes; for example, having a button that is only considered pressed if a joystick is in a certain range. 

### Examples

Method reference:

```java
Button fooButton = new SimpleButton(this::getFoo);
```

Simple statement lambda:

```java
Button fooAndBarButton = new SimpleButton(() -> getFoo() && getBar());
```

More complicated logic:

```java
Button atLeast5TrueButton = new SimpleButton(() -> {
    int numTrue = 0;
    for (boolean b : aListOfBooleans) {
        if (b) {
            numTrue++;
        }
    }
    return numTrue >= 5;
});
```

## SimpleCommand/SimpleLoopCommand

[`SimpleCommand`](https://flamingchickens1540.github.io/ROOSTER/org/team1540/base/rooster/SimpleCommand.html) and [`SimpleLoopCommand`](https://flamingchickens1540.github.io/ROOSTER/org/team1540/rooster/util/SimpleLoopCommand.html) are wrappers that allow the creation of WPILib `Commands` that run an arbitrary function when executed. `SimpleCommand` runs the provided code once before finishing, while `SimpleLoopCommand` will run until canceled or interrupted. Both types can have requirements just like normal commands.

Note that the constructor for `SimpleCommand` requires a name; this is so that the command has a sensible name that is preserved across different program runs (as otherwise the names could be different depending on what order something is initialized statically).

`SimpleCommand` in particular is very useful for putting buttons to do things on the SmartDashboard or Shuffleboard. Simply create the command and put it up using `SmartDashboard.putData()`.

### Examples

Hello world:

```java
Command printHello = new SimpleCommand("Print hello", () -> System.out.println("Hello world!"));
```

Command with requirements:

```java
Command openClaw = new SimpleCommand("Open claw", Robot.claw::open, Robot.claw);
```

Command with multiple requirements:

```java
Command openClawAndRaiseArm = new SimpleCommand("Open Claw and Raise Arm",
    () -> {
        Robot.claw.open();
        Robot.arm.raise();
    }, 
    Robot.claw, Robot.arm);
```

Looping command:

```java
Command printTime = new SimpleLoopCommand("Print Current Time", () -> System.out.println(System.currentTimeMillis()));
```

## SimpleConditionalCommand

[`SimpleConditionalCommand`](https://flamingchickens1540.github.io/ROOSTER/org/team1540/rooster/util/SimpleConditionalCommand.html) is a wrapper that allows the creation of WPILib `ConditionalCommand` instances based off of an arbitrary function returning a `boolean` in one line of code. `SimpleConditionalCommands`, like `ConditionalCommands`, can run a `Command` only if the provided condition is `true`, or run one of two commands depending on the condition.

### Examples

Run a command only if `getFoo()` returns `true`:

```java
Command doSomethingIfFoo = new SimpleConditionalCommand(this::getFoo, new DoSomething());
```

Run the `DoSomething` command if `getFoo()` returns `true`, otherwise run `DoSomethingElse`:

```java
Command somethingOrSomethingElse = new SimpleConditionalCommand(this::getFoo, 
    new DoSomething(), 
    new DoSomethingElse());
```

## SimpleAsyncCommand

[`SimpleAsyncCommand`](https://flamingchickens1540.github.io/ROOSTER/org/team1540/rooster/util/SimpleAsyncCommand.html) is a variant of `SimpleLoopCommand` which leverages the [`AsyncCommand`](https://flamingchickens1540.github.io/ROOSTER/org/team1540/rooster/util/AsyncCommand.html) class to run code in a seperate loop outside of the main robot loop. It's useful for mechanism control loops and other code that needs to update faster than the main robot loop. It is used exactly like a `SimpleLoopCommand`, with the exception of an additional parameter specifying the loop time.

Note that any code provided to the command will be run in a separate thread and thus must be thread-safe.

### Examples

Update a drivetrain's control loop every 10 milliseconds:

```java
Command controlLoopCommand = new SimpleAsyncCommand("Update", 20, Robot.drivetrain::update(), Robot.drivetrain);
```

