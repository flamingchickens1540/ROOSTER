package org.team1540.rooster.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.function.DoubleSupplier;
import org.team1540.rooster.local.Localizer2D;

public class UdpPoseTwistTransmitter {

  private final DoubleSupplier leftVelSupplier;
  private final DoubleSupplier rightVelSupplier;
  private DoubleSupplier thetaSupplier;
  private double trackWidth;
  private Localizer2D localizer;
  private DatagramSocket socket;
  private SocketAddress addr;

  public UdpPoseTwistTransmitter(DoubleSupplier leftPosSupplier,
      DoubleSupplier rightPosSupplier, DoubleSupplier leftVelSupplier,
      DoubleSupplier rightVelSupplier, DoubleSupplier thetaSupplier, double trackWidth,
      SocketAddress addr) {
    this.leftVelSupplier = leftVelSupplier;
    this.rightVelSupplier = rightVelSupplier;
    this.thetaSupplier = thetaSupplier;
    this.trackWidth = trackWidth;

    localizer = new Localizer2D(leftPosSupplier, rightPosSupplier, thetaSupplier);

    this.addr = addr;

    try {
      socket = new DatagramSocket();
    } catch (SocketException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Calculates the content of a pose-twist packet and transmits it.
   *
   * @throws IOException if an exception occurs during transmission.
   */
  public void transmit() throws IOException {
    localizer.execute();
    double lvel = leftVelSupplier.getAsDouble();
    double rvel = rightVelSupplier.getAsDouble();

    byte[] bytes = new byte[Double.BYTES * 5];
    //noinspection unused
    ByteBuffer buffer = ByteBuffer.wrap(bytes)
        .putDouble(localizer.getX())
        .putDouble(localizer.getY())
        .putDouble(thetaSupplier.getAsDouble())
        .putDouble((lvel + rvel) / 2) // throttle
        .putDouble((rvel - lvel) / trackWidth); // omega

    DatagramPacket p = new DatagramPacket(bytes, Double.BYTES * 5, addr);
    socket.send(p);
  }
}
