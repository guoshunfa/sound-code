package java.net;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamException;
import java.io.ObjectStreamField;
import sun.misc.Unsafe;

public class InetSocketAddress extends SocketAddress {
   private final transient InetSocketAddress.InetSocketAddressHolder holder;
   private static final long serialVersionUID = 5076001401234631237L;
   private static final ObjectStreamField[] serialPersistentFields;
   private static final long FIELDS_OFFSET;
   private static final Unsafe UNSAFE;

   private static int checkPort(int var0) {
      if (var0 >= 0 && var0 <= 65535) {
         return var0;
      } else {
         throw new IllegalArgumentException("port out of range:" + var0);
      }
   }

   private static String checkHost(String var0) {
      if (var0 == null) {
         throw new IllegalArgumentException("hostname can't be null");
      } else {
         return var0;
      }
   }

   public InetSocketAddress(int var1) {
      this(InetAddress.anyLocalAddress(), var1);
   }

   public InetSocketAddress(InetAddress var1, int var2) {
      this.holder = new InetSocketAddress.InetSocketAddressHolder((String)null, var1 == null ? InetAddress.anyLocalAddress() : var1, checkPort(var2));
   }

   public InetSocketAddress(String var1, int var2) {
      checkHost(var1);
      InetAddress var3 = null;
      String var4 = null;

      try {
         var3 = InetAddress.getByName(var1);
      } catch (UnknownHostException var6) {
         var4 = var1;
      }

      this.holder = new InetSocketAddress.InetSocketAddressHolder(var4, var3, checkPort(var2));
   }

   private InetSocketAddress(int var1, String var2) {
      this.holder = new InetSocketAddress.InetSocketAddressHolder(var2, (InetAddress)null, var1);
   }

   public static InetSocketAddress createUnresolved(String var0, int var1) {
      return new InetSocketAddress(checkPort(var1), checkHost(var0));
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      ObjectOutputStream.PutField var2 = var1.putFields();
      var2.put("hostname", this.holder.hostname);
      var2.put("addr", this.holder.addr);
      var2.put("port", this.holder.port);
      var1.writeFields();
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      ObjectInputStream.GetField var2 = var1.readFields();
      String var3 = (String)var2.get("hostname", (Object)null);
      InetAddress var4 = (InetAddress)var2.get("addr", (Object)null);
      int var5 = var2.get("port", (int)-1);
      checkPort(var5);
      if (var3 == null && var4 == null) {
         throw new InvalidObjectException("hostname and addr can't both be null");
      } else {
         InetSocketAddress.InetSocketAddressHolder var6 = new InetSocketAddress.InetSocketAddressHolder(var3, var4, var5);
         UNSAFE.putObject(this, FIELDS_OFFSET, var6);
      }
   }

   private void readObjectNoData() throws ObjectStreamException {
      throw new InvalidObjectException("Stream data required");
   }

   public final int getPort() {
      return this.holder.getPort();
   }

   public final InetAddress getAddress() {
      return this.holder.getAddress();
   }

   public final String getHostName() {
      return this.holder.getHostName();
   }

   public final String getHostString() {
      return this.holder.getHostString();
   }

   public final boolean isUnresolved() {
      return this.holder.isUnresolved();
   }

   public String toString() {
      return this.holder.toString();
   }

   public final boolean equals(Object var1) {
      return var1 != null && var1 instanceof InetSocketAddress ? this.holder.equals(((InetSocketAddress)var1).holder) : false;
   }

   public final int hashCode() {
      return this.holder.hashCode();
   }

   static {
      serialPersistentFields = new ObjectStreamField[]{new ObjectStreamField("hostname", String.class), new ObjectStreamField("addr", InetAddress.class), new ObjectStreamField("port", Integer.TYPE)};

      try {
         Unsafe var0 = Unsafe.getUnsafe();
         FIELDS_OFFSET = var0.objectFieldOffset(InetSocketAddress.class.getDeclaredField("holder"));
         UNSAFE = var0;
      } catch (ReflectiveOperationException var1) {
         throw new Error(var1);
      }
   }

   private static class InetSocketAddressHolder {
      private String hostname;
      private InetAddress addr;
      private int port;

      private InetSocketAddressHolder(String var1, InetAddress var2, int var3) {
         this.hostname = var1;
         this.addr = var2;
         this.port = var3;
      }

      private int getPort() {
         return this.port;
      }

      private InetAddress getAddress() {
         return this.addr;
      }

      private String getHostName() {
         if (this.hostname != null) {
            return this.hostname;
         } else {
            return this.addr != null ? this.addr.getHostName() : null;
         }
      }

      private String getHostString() {
         if (this.hostname != null) {
            return this.hostname;
         } else if (this.addr != null) {
            return this.addr.holder().getHostName() != null ? this.addr.holder().getHostName() : this.addr.getHostAddress();
         } else {
            return null;
         }
      }

      private boolean isUnresolved() {
         return this.addr == null;
      }

      public String toString() {
         return this.isUnresolved() ? this.hostname + ":" + this.port : this.addr.toString() + ":" + this.port;
      }

      public final boolean equals(Object var1) {
         if (var1 != null && var1 instanceof InetSocketAddress.InetSocketAddressHolder) {
            InetSocketAddress.InetSocketAddressHolder var2 = (InetSocketAddress.InetSocketAddressHolder)var1;
            boolean var3;
            if (this.addr != null) {
               var3 = this.addr.equals(var2.addr);
            } else if (this.hostname != null) {
               var3 = var2.addr == null && this.hostname.equalsIgnoreCase(var2.hostname);
            } else {
               var3 = var2.addr == null && var2.hostname == null;
            }

            return var3 && this.port == var2.port;
         } else {
            return false;
         }
      }

      public final int hashCode() {
         if (this.addr != null) {
            return this.addr.hashCode() + this.port;
         } else {
            return this.hostname != null ? this.hostname.toLowerCase().hashCode() + this.port : this.port;
         }
      }

      // $FF: synthetic method
      InetSocketAddressHolder(String var1, InetAddress var2, int var3, Object var4) {
         this(var1, var2, var3);
      }
   }
}
