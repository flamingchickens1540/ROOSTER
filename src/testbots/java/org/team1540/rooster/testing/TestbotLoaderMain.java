package org.team1540.rooster.testing;

import edu.wpi.first.wpilibj.RobotBase;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class TestbotLoaderMain {

  public static void main(String... args) {
    // workaround for GradleRIO simulation throwing away standard error
    if (RobotBase.isSimulation()) {
      System.setErr(System.out);
    }

    // try to find a robotclass.txt file
    File robotClassFile = new File("robotclass.txt");

    if (robotClassFile.isFile()) {
      // attempt to read from the file
      try (BufferedReader reader = new BufferedReader(new FileReader(robotClassFile))) {
        String robotClass = reader.readLine();
        if (robotClass == null) {
          System.err.println(
              "robotclass.txt is empty. Make sure your deployment is correctly configured.");
        }
        System.out.println("Attempting to start robot class " + robotClass);

        RobotBase.startRobot(() -> {
          try {
            return Class.forName(robotClass).asSubclass(RobotBase.class).getDeclaredConstructor()
                .newInstance();
          } catch (ClassNotFoundException e) {
            System.err.println("Could not find robot class " + robotClass
                + ". Check for typos in the provided class name.");
            throw new RuntimeException(e);
          } catch (ClassCastException e) {
            System.err.println("Class " + robotClass
                + " is not a subclass of RobotBase. Make sure your robot class is set up correctly.");
            throw new RuntimeException(e);
          } catch (NoSuchMethodException e) {
            System.err.println("Class " + robotClass
                + " does not have a no-argument constructor. Make sure your robot class is set up correctly.");
            throw new RuntimeException(e);
          } catch (IllegalAccessException e) {
            System.err.println("The no-argument constructor for class " + robotClass
                + " is private; change it to public.");
            throw new RuntimeException(e);
          } catch (IllegalArgumentException e) {
            System.err.println(
                "An IllegalArgumentException was somehow thrown by a method that takes no arguments. Make sure your robot is sane.");
            throw new RuntimeException(e);
          } catch (InstantiationException e) {
            System.err.println(
                "The provided robot class is abstract and cannot be instantiated. Make sure your robot class is set up correctly.");
            throw new RuntimeException(e);
          } catch (InvocationTargetException e) {
            System.err.println("Your robot threw an exception (" + e.getCause().toString()
                + ") while being instantiated. Check your constructor (if applicable) and field initializers.");
            throw new RuntimeException(e);
          }
        });

      } catch (FileNotFoundException e) {
        System.err.println(
            "Could not find robotclass.txt file. Make sure your deployment is correctly configured.");
        throw new RuntimeException(e);
      } catch (IOException e) {
        System.err.println(
            "IOException while reading robotclass.txt file. If this appears repeatedly, something has gone terribly wrong.");
        throw new RuntimeException(e);
      }
    } else {
      System.err.println(
          "Could not find robotclass.txt file. Make sure your deployment is correctly configured.");
      System.exit(1);
    }
  }
}
