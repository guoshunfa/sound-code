package java.net;

import java.security.AccessController;
import java.security.PrivilegedAction;

public final class DatagramPacket {
   byte[] buf;
   int offset;
   int length;
   int bufLength;
   InetAddress address;
   int port;

   public DatagramPacket(byte[] var1, int var2, int var3) {
      this.setData(var1, var2, var3);
      this.address = null;
      this.port = -1;
   }

   public DatagramPacket(byte[] var1, int var2) {
      this(var1, 0, var2);
   }

   public DatagramPacket(byte[] var1, int var2, int var3, InetAddress var4, int var5) {
      this.setData(var1, var2, var3);
      this.setAddress(var4);
      this.setPort(var5);
   }

   public DatagramPacket(byte[] var1, int var2, int var3, SocketAddress var4) {
      this.setData(var1, var2, var3);
      this.setSocketAddress(var4);
   }

   public DatagramPacket(byte[] var1, int var2, InetAddress var3, int var4) {
      this(var1, 0, var2, var3, var4);
   }

   public DatagramPacket(byte[] var1, int var2, SocketAddress var3) {
      this(var1, 0, var2, var3);
   }

   public synchronized InetAddress getAddress() {
      return this.address;
   }

   public synchronized int getPort() {
      return this.port;
   }

   public synchronized byte[] getData() {
      return this.buf;
   }

   public synchronized int getOffset() {
      return this.offset;
   }

   public synchronized int getLength() {
      return this.length;
   }

   public synchronized void setData(byte[] var1, int var2, int var3) {
      if (var3 >= 0 && var2 >= 0 && var3 + var2 >= 0 && var3 + var2 <= var1.length) {
         this.buf = var1;
         this.length = var3;
         this.bufLength = var3;
         this.offset = var2;
      } else {
         throw new IllegalArgumentException("illegal length or offset");
      }
   }

   public synchronized void setAddress(InetAddress var1) {
      this.address = var1;
   }

   public synchronized void setPort(int var1) {
      if (var1 >= 0 && var1 <= 65535) {
         this.port = var1;
      } else {
         throw new IllegalArgumentException("Port out of range:" + var1);
      }
   }

   public synchronized void setSocketAddress(SocketAddress var1) {
      if (var1 != null && var1 instanceof InetSocketAddress) {
         InetSocketAddress var2 = (InetSocketAddress)var1;
         if (var2.isUnresolved()) {
            throw new IllegalArgumentException("unresolved address");
         } else {
            this.setAddress(var2.getAddress());
            this.setPort(var2.getPort());
         }
      } else {
         throw new IllegalArgumentException("unsupported address type");
      }
   }

   public synchronized SocketAddress getSocketAddress() {
      return new InetSocketAddress(this.getAddress(), this.getPort());
   }

   public synchronized void setData(byte[] var1) {
      if (var1 == null) {
         throw new NullPointerException("null packet buffer");
      } else {
         this.buf = var1;
         this.offset = 0;
         this.length = var1.length;
         this.bufLength = var1.length;
      }
   }

   public synchronized void setLength(int var1) {
      if (var1 + this.offset <= this.buf.length && var1 >= 0 && var1 + this.offset >= 0) {
         this.length = var1;
         this.bufLength = this.length;
      } else {
         throw new IllegalArgumentException("illegal length");
      }
   }

   private static native void init();

   static {
      AccessController.doPrivileged(new PrivilegedAction<Void>() {
         public Void run() {
            System.loadLibrary("net");
            return null;
         }
      });
      init();
   }
}
