package com.sun.jmx.snmp.daemon;

import com.sun.jmx.defaults.JmxProperties;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.logging.Level;

final class SnmpSocket implements Runnable {
   private DatagramSocket _socket = null;
   private SnmpResponseHandler _dgramHdlr = null;
   private Thread _sockThread = null;
   private byte[] _buffer = null;
   private transient boolean isClosing = false;
   int _socketPort = 0;
   int responseBufSize = 1024;

   public SnmpSocket(SnmpResponseHandler var1, InetAddress var2, int var3) throws SocketException {
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
         JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, SnmpSocket.class.getName(), "constructor", "Creating new SNMP datagram socket");
      }

      this._socket = new DatagramSocket(0, var2);
      this._socketPort = this._socket.getLocalPort();
      this.responseBufSize = var3;
      this._buffer = new byte[this.responseBufSize];
      this._dgramHdlr = var1;
      this._sockThread = new Thread(this, "SnmpSocket");
      this._sockThread.start();
   }

   public synchronized void sendPacket(byte[] var1, int var2, InetAddress var3, int var4) throws IOException {
      DatagramPacket var5 = new DatagramPacket(var1, var2, var3, var4);
      this.sendPacket(var5);
   }

   public synchronized void sendPacket(DatagramPacket var1) throws IOException {
      try {
         if (this.isValid()) {
            if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
               JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, SnmpSocket.class.getName(), "sendPacket", "Sending DatagramPacket. Length = " + var1.getLength() + " through socket = " + this._socket.toString());
            }

            this._socket.send(var1);
         } else {
            throw new IOException("Invalid state of SNMP datagram socket.");
         }
      } catch (IOException var3) {
         if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
            JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpSocket.class.getName(), "sendPacket", (String)"I/O error while sending", (Throwable)var3);
         }

         throw var3;
      }
   }

   public synchronized boolean isValid() {
      return this._socket != null && this._sockThread != null && this._sockThread.isAlive();
   }

   public synchronized void close() {
      this.isClosing = true;
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
         JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, SnmpSocket.class.getName(), "close", "Closing and destroying the SNMP datagram socket -> " + this.toString());
      }

      try {
         DatagramSocket var1 = new DatagramSocket(0);
         byte[] var2 = new byte[1];
         DatagramPacket var3 = new DatagramPacket(var2, 1, InetAddress.getLocalHost(), this._socketPort);
         var1.send(var3);
         var1.close();
      } catch (Exception var5) {
      }

      if (this._socket != null) {
         this._socket.close();
         this._socket = null;
      }

      if (this._sockThread != null && this._sockThread.isAlive()) {
         this._sockThread.interrupt();

         try {
            this._sockThread.join();
         } catch (InterruptedException var4) {
         }

         this._sockThread = null;
      }

   }

   public void run() {
      Thread.currentThread().setPriority(8);

      while(true) {
         while(true) {
            try {
               DatagramPacket var1 = new DatagramPacket(this._buffer, this._buffer.length);
               if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
                  JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, SnmpSocket.class.getName(), "run", "[" + Thread.currentThread().toString() + "]:Blocking for receiving packet");
               }

               this._socket.receive(var1);
               if (this.isClosing) {
                  return;
               }

               if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
                  JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, SnmpSocket.class.getName(), "run", "[" + Thread.currentThread().toString() + "]:Received a packet");
               }

               if (var1.getLength() > 0) {
                  if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
                     JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, SnmpSocket.class.getName(), "run", "[" + Thread.currentThread().toString() + "]:Received a packet from : " + var1.getAddress().toString() + ", Length = " + var1.getLength());
                  }

                  this.handleDatagram(var1);
                  if (this.isClosing) {
                     return;
                  }
               }
            } catch (IOException var2) {
               if (this.isClosing) {
                  return;
               }

               if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
                  JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpSocket.class.getName(), "run", (String)"IOEXception while receiving datagram", (Throwable)var2);
               }
            } catch (Exception var3) {
               if (this.isClosing) {
                  return;
               }

               if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
                  JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpSocket.class.getName(), "run", (String)"Exception in socket thread...", (Throwable)var3);
               }
            } catch (ThreadDeath var4) {
               if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
                  JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpSocket.class.getName(), "run", (String)("Socket Thread DEAD..." + this.toString()), (Throwable)var4);
               }

               this.close();
               throw var4;
            } catch (Error var5) {
               if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
                  JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpSocket.class.getName(), "run", (String)"Got unexpected error", (Throwable)var5);
               }

               this.handleJavaError(var5);
            }
         }
      }
   }

   protected synchronized void finalize() {
      this.close();
   }

   private synchronized void handleJavaError(Throwable var1) {
      if (var1 instanceof OutOfMemoryError) {
         if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
            JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpSocket.class.getName(), "handleJavaError", "OutOfMemory error", var1);
         }

         Thread.yield();
      } else {
         if (this._socket != null) {
            this._socket.close();
            this._socket = null;
         }

         if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST)) {
            JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpSocket.class.getName(), "handleJavaError", "Global Internal error");
         }

         Thread.yield();
      }
   }

   private synchronized void handleDatagram(DatagramPacket var1) {
      this._dgramHdlr.processDatagram(var1);
   }
}
