package sun.rmi.transport.tcp;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.rmi.server.LogStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Enumeration;
import java.util.Hashtable;
import sun.rmi.runtime.Log;
import sun.security.action.GetPropertyAction;

final class ConnectionMultiplexer {
   static int logLevel = LogStream.parseLevel(getLogLevel());
   static final Log multiplexLog;
   private static final int OPEN = 225;
   private static final int CLOSE = 226;
   private static final int CLOSEACK = 227;
   private static final int REQUEST = 228;
   private static final int TRANSMIT = 229;
   private TCPChannel channel;
   private InputStream in;
   private OutputStream out;
   private boolean orig;
   private DataInputStream dataIn;
   private DataOutputStream dataOut;
   private Hashtable<Integer, MultiplexConnectionInfo> connectionTable = new Hashtable(7);
   private int numConnections = 0;
   private static final int maxConnections = 256;
   private int lastID = 4097;
   private boolean alive = true;

   private static String getLogLevel() {
      return (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("sun.rmi.transport.tcp.multiplex.logLevel")));
   }

   public ConnectionMultiplexer(TCPChannel var1, InputStream var2, OutputStream var3, boolean var4) {
      this.channel = var1;
      this.in = var2;
      this.out = var3;
      this.orig = var4;
      this.dataIn = new DataInputStream(var2);
      this.dataOut = new DataOutputStream(var3);
   }

   public void run() throws IOException {
      try {
         while(true) {
            int var1 = this.dataIn.readUnsignedByte();
            int var2;
            int var3;
            MultiplexConnectionInfo var4;
            switch(var1) {
            case 225:
               var2 = this.dataIn.readUnsignedShort();
               if (multiplexLog.isLoggable(Log.VERBOSE)) {
                  multiplexLog.log(Log.VERBOSE, "operation  OPEN " + var2);
               }

               var4 = (MultiplexConnectionInfo)this.connectionTable.get(var2);
               if (var4 != null) {
                  throw new IOException("OPEN: Connection ID already exists");
               }

               var4 = new MultiplexConnectionInfo(var2);
               var4.in = new MultiplexInputStream(this, var4, 2048);
               var4.out = new MultiplexOutputStream(this, var4, 2048);
               synchronized(this.connectionTable) {
                  this.connectionTable.put(var2, var4);
                  ++this.numConnections;
               }

               TCPConnection var5 = new TCPConnection(this.channel, var4.in, var4.out);
               this.channel.acceptMultiplexConnection(var5);
               break;
            case 226:
               var2 = this.dataIn.readUnsignedShort();
               if (multiplexLog.isLoggable(Log.VERBOSE)) {
                  multiplexLog.log(Log.VERBOSE, "operation  CLOSE " + var2);
               }

               var4 = (MultiplexConnectionInfo)this.connectionTable.get(var2);
               if (var4 == null) {
                  throw new IOException("CLOSE: Invalid connection ID");
               }

               var4.in.disconnect();
               var4.out.disconnect();
               if (!var4.closed) {
                  this.sendCloseAck(var4);
               }

               synchronized(this.connectionTable) {
                  this.connectionTable.remove(var2);
                  --this.numConnections;
                  break;
               }
            case 227:
               var2 = this.dataIn.readUnsignedShort();
               if (multiplexLog.isLoggable(Log.VERBOSE)) {
                  multiplexLog.log(Log.VERBOSE, "operation  CLOSEACK " + var2);
               }

               var4 = (MultiplexConnectionInfo)this.connectionTable.get(var2);
               if (var4 == null) {
                  throw new IOException("CLOSEACK: Invalid connection ID");
               }

               if (!var4.closed) {
                  throw new IOException("CLOSEACK: Connection not closed");
               }

               var4.in.disconnect();
               var4.out.disconnect();
               synchronized(this.connectionTable) {
                  this.connectionTable.remove(var2);
                  --this.numConnections;
                  break;
               }
            case 228:
               var2 = this.dataIn.readUnsignedShort();
               var4 = (MultiplexConnectionInfo)this.connectionTable.get(var2);
               if (var4 == null) {
                  throw new IOException("REQUEST: Invalid connection ID");
               }

               var3 = this.dataIn.readInt();
               if (multiplexLog.isLoggable(Log.VERBOSE)) {
                  multiplexLog.log(Log.VERBOSE, "operation  REQUEST " + var2 + ": " + var3);
               }

               var4.out.request(var3);
               break;
            case 229:
               var2 = this.dataIn.readUnsignedShort();
               var4 = (MultiplexConnectionInfo)this.connectionTable.get(var2);
               if (var4 == null) {
                  throw new IOException("SEND: Invalid connection ID");
               }

               var3 = this.dataIn.readInt();
               if (multiplexLog.isLoggable(Log.VERBOSE)) {
                  multiplexLog.log(Log.VERBOSE, "operation  TRANSMIT " + var2 + ": " + var3);
               }

               var4.in.receive(var3, this.dataIn);
               break;
            default:
               throw new IOException("Invalid operation: " + Integer.toHexString(var1));
            }
         }
      } finally {
         this.shutDown();
      }
   }

   public synchronized TCPConnection openConnection() throws IOException {
      int var1;
      do {
         this.lastID = ++this.lastID & 32767;
         var1 = this.lastID;
         if (this.orig) {
            var1 |= 32768;
         }
      } while(this.connectionTable.get(var1) != null);

      MultiplexConnectionInfo var2 = new MultiplexConnectionInfo(var1);
      var2.in = new MultiplexInputStream(this, var2, 2048);
      var2.out = new MultiplexOutputStream(this, var2, 2048);
      synchronized(this.connectionTable) {
         if (!this.alive) {
            throw new IOException("Multiplexer connection dead");
         }

         if (this.numConnections >= 256) {
            throw new IOException("Cannot exceed 256 simultaneous multiplexed connections");
         }

         this.connectionTable.put(var1, var2);
         ++this.numConnections;
      }

      synchronized(this.dataOut) {
         try {
            this.dataOut.writeByte(225);
            this.dataOut.writeShort(var1);
            this.dataOut.flush();
         } catch (IOException var6) {
            multiplexLog.log(Log.BRIEF, "exception: ", var6);
            this.shutDown();
            throw var6;
         }
      }

      return new TCPConnection(this.channel, var2.in, var2.out);
   }

   public void shutDown() {
      synchronized(this.connectionTable) {
         if (!this.alive) {
            return;
         }

         this.alive = false;
         Enumeration var2 = this.connectionTable.elements();

         while(true) {
            if (!var2.hasMoreElements()) {
               this.connectionTable.clear();
               this.numConnections = 0;
               break;
            }

            MultiplexConnectionInfo var3 = (MultiplexConnectionInfo)var2.nextElement();
            var3.in.disconnect();
            var3.out.disconnect();
         }
      }

      try {
         this.in.close();
      } catch (IOException var6) {
      }

      try {
         this.out.close();
      } catch (IOException var5) {
      }

   }

   void sendRequest(MultiplexConnectionInfo var1, int var2) throws IOException {
      synchronized(this.dataOut) {
         if (this.alive && !var1.closed) {
            try {
               this.dataOut.writeByte(228);
               this.dataOut.writeShort(var1.id);
               this.dataOut.writeInt(var2);
               this.dataOut.flush();
            } catch (IOException var6) {
               multiplexLog.log(Log.BRIEF, "exception: ", var6);
               this.shutDown();
               throw var6;
            }
         }

      }
   }

   void sendTransmit(MultiplexConnectionInfo var1, byte[] var2, int var3, int var4) throws IOException {
      synchronized(this.dataOut) {
         if (this.alive && !var1.closed) {
            try {
               this.dataOut.writeByte(229);
               this.dataOut.writeShort(var1.id);
               this.dataOut.writeInt(var4);
               this.dataOut.write(var2, var3, var4);
               this.dataOut.flush();
            } catch (IOException var8) {
               multiplexLog.log(Log.BRIEF, "exception: ", var8);
               this.shutDown();
               throw var8;
            }
         }

      }
   }

   void sendClose(MultiplexConnectionInfo var1) throws IOException {
      var1.out.disconnect();
      synchronized(this.dataOut) {
         if (this.alive && !var1.closed) {
            try {
               this.dataOut.writeByte(226);
               this.dataOut.writeShort(var1.id);
               this.dataOut.flush();
               var1.closed = true;
            } catch (IOException var5) {
               multiplexLog.log(Log.BRIEF, "exception: ", var5);
               this.shutDown();
               throw var5;
            }
         }

      }
   }

   void sendCloseAck(MultiplexConnectionInfo var1) throws IOException {
      synchronized(this.dataOut) {
         if (this.alive && !var1.closed) {
            try {
               this.dataOut.writeByte(227);
               this.dataOut.writeShort(var1.id);
               this.dataOut.flush();
               var1.closed = true;
            } catch (IOException var5) {
               multiplexLog.log(Log.BRIEF, "exception: ", var5);
               this.shutDown();
               throw var5;
            }
         }

      }
   }

   protected void finalize() throws Throwable {
      super.finalize();
      this.shutDown();
   }

   static {
      multiplexLog = Log.getLog("sun.rmi.transport.tcp.multiplex", "multiplex", logLevel);
   }
}
