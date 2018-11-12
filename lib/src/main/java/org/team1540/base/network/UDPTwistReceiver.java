package org.team1540.base.network;

import edu.wpi.first.wpilibj.DriverStation;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicLong;
import org.jetbrains.annotations.NotNull;

public class UDPTwistReceiver {

  private Thread receivingThread;

  // these actually hold a double but Java doesn't have an AtomicDouble class cuz heck u
  @NotNull
  private AtomicLong vel = new AtomicLong();
  @NotNull
  private AtomicLong theta = new AtomicLong();

  public UDPTwistReceiver(int port) {
    receivingThread = new Thread(new UDPReceiver(port));
    receivingThread.start();
  }

  public double[] get() {
    return new double[]{vel.get(), theta.get()};
  }

  public void interrupt() {
    receivingThread.interrupt();
  }

  private class UDPReceiver implements Runnable {

    @NotNull
    private DatagramSocket socket;

    long lastTs = 0;

    public UDPReceiver(int port) {
      try {
        socket = new DatagramSocket(port);
        // we won't actually "time out" this is just so we can print a warning if we don't get anything for a certain amount of time
        socket.setSoTimeout(10000);
      } catch (SocketException e) {
        throw new RuntimeException("Exception occured while opening socket", e);
      }
    }

    @Override
    public void run() {
      try {
        DatagramPacket packet = new DatagramPacket(new byte[24], 24); // 8 * 3
        // buffer is backed by the byte[] in packet, so when we call socket.receive() the buffer is updated
        ByteBuffer buffer = ByteBuffer.wrap(packet.getData());
        while (true) {
          try {
            buffer.rewind();
            socket.receive(packet);
            long ts = buffer.getLong();
            if (lastTs > ts) {
              DriverStation.reportWarning(
                  "Received a UDP packet with timestamp " + ts + " but last timestamp was "
                      + lastTs,
                  false);
            } else {
              lastTs = ts;
              vel.set(buffer.getLong());
              theta.set(buffer.getLong());
            }
          } catch (SocketTimeoutException e) {
            DriverStation.reportWarning(
                "UDPVelocityInput has not received a packet for the last 10 seconds",
                false);
          } catch (IOException e) {
            DriverStation.reportError(
                "IOException occured in UDPVelocityInput: " + e.getLocalizedMessage(),
                e.getStackTrace());
          }

          if (Thread.currentThread().isInterrupted()) {
            System.out.println("UDPVelocityInput thread was interrupted; terminating");
            break;
          }
        }
      } finally {
        socket.close();
        vel.set(0);
        theta.set(0);
      }
    }
  }
}
