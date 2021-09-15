package java.net;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamField;
import java.util.Arrays;
import java.util.Enumeration;
import sun.misc.Unsafe;

public final class Inet6Address extends InetAddress {
   static final int INADDRSZ = 16;
   private transient int cached_scope_id;
   private final transient Inet6Address.Inet6AddressHolder holder6;
   private static final long serialVersionUID = 6880410070516793377L;
   private static final ObjectStreamField[] serialPersistentFields;
   private static final long FIELDS_OFFSET;
   private static final Unsafe UNSAFE;
   private static final int INT16SZ = 2;

   Inet6Address() {
      this.holder.init((String)null, 2);
      this.holder6 = new Inet6Address.Inet6AddressHolder();
   }

   Inet6Address(String var1, byte[] var2, int var3) {
      this.holder.init(var1, 2);
      this.holder6 = new Inet6Address.Inet6AddressHolder();
      this.holder6.init(var2, var3);
   }

   Inet6Address(String var1, byte[] var2) {
      this.holder6 = new Inet6Address.Inet6AddressHolder();

      try {
         this.initif(var1, var2, (NetworkInterface)null);
      } catch (UnknownHostException var4) {
      }

   }

   Inet6Address(String var1, byte[] var2, NetworkInterface var3) throws UnknownHostException {
      this.holder6 = new Inet6Address.Inet6AddressHolder();
      this.initif(var1, var2, var3);
   }

   Inet6Address(String var1, byte[] var2, String var3) throws UnknownHostException {
      this.holder6 = new Inet6Address.Inet6AddressHolder();
      this.initstr(var1, var2, var3);
   }

   public static Inet6Address getByAddress(String var0, byte[] var1, NetworkInterface var2) throws UnknownHostException {
      if (var0 != null && var0.length() > 0 && var0.charAt(0) == '[' && var0.charAt(var0.length() - 1) == ']') {
         var0 = var0.substring(1, var0.length() - 1);
      }

      if (var1 != null && var1.length == 16) {
         return new Inet6Address(var0, var1, var2);
      } else {
         throw new UnknownHostException("addr is of illegal length");
      }
   }

   public static Inet6Address getByAddress(String var0, byte[] var1, int var2) throws UnknownHostException {
      if (var0 != null && var0.length() > 0 && var0.charAt(0) == '[' && var0.charAt(var0.length() - 1) == ']') {
         var0 = var0.substring(1, var0.length() - 1);
      }

      if (var1 != null && var1.length == 16) {
         return new Inet6Address(var0, var1, var2);
      } else {
         throw new UnknownHostException("addr is of illegal length");
      }
   }

   private void initstr(String var1, byte[] var2, String var3) throws UnknownHostException {
      try {
         NetworkInterface var4 = NetworkInterface.getByName(var3);
         if (var4 == null) {
            throw new UnknownHostException("no such interface " + var3);
         } else {
            this.initif(var1, var2, var4);
         }
      } catch (SocketException var5) {
         throw new UnknownHostException("SocketException thrown" + var3);
      }
   }

   private void initif(String var1, byte[] var2, NetworkInterface var3) throws UnknownHostException {
      byte var4 = -1;
      this.holder6.init(var2, var3);
      if (var2.length == 16) {
         var4 = 2;
      }

      this.holder.init(var1, var4);
   }

   private static boolean isDifferentLocalAddressType(byte[] var0, byte[] var1) {
      if (isLinkLocalAddress(var0) && !isLinkLocalAddress(var1)) {
         return false;
      } else {
         return !isSiteLocalAddress(var0) || isSiteLocalAddress(var1);
      }
   }

   private static int deriveNumericScope(byte[] var0, NetworkInterface var1) throws UnknownHostException {
      Enumeration var2 = var1.getInetAddresses();

      while(var2.hasMoreElements()) {
         InetAddress var3 = (InetAddress)var2.nextElement();
         if (var3 instanceof Inet6Address) {
            Inet6Address var4 = (Inet6Address)var3;
            if (isDifferentLocalAddressType(var0, var4.getAddress())) {
               return var4.getScopeId();
            }
         }
      }

      throw new UnknownHostException("no scope_id found");
   }

   private int deriveNumericScope(String var1) throws UnknownHostException {
      Enumeration var2;
      try {
         var2 = NetworkInterface.getNetworkInterfaces();
      } catch (SocketException var4) {
         throw new UnknownHostException("could not enumerate local network interfaces");
      }

      NetworkInterface var3;
      do {
         if (!var2.hasMoreElements()) {
            throw new UnknownHostException("No matching address found for interface : " + var1);
         }

         var3 = (NetworkInterface)var2.nextElement();
      } while(!var3.getName().equals(var1));

      return deriveNumericScope(this.holder6.ipaddress, var3);
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      NetworkInterface var2 = null;
      if (this.getClass().getClassLoader() != null) {
         throw new SecurityException("invalid address type");
      } else {
         ObjectInputStream.GetField var3 = var1.readFields();
         byte[] var4 = (byte[])((byte[])var3.get("ipaddress", (Object)null));
         int var5 = var3.get("scope_id", (int)-1);
         boolean var6 = var3.get("scope_id_set", false);
         boolean var7 = var3.get("scope_ifname_set", false);
         String var8 = (String)var3.get("ifname", (Object)null);
         if (var8 != null && !"".equals(var8)) {
            try {
               var2 = NetworkInterface.getByName(var8);
               if (var2 == null) {
                  var6 = false;
                  var7 = false;
                  var5 = 0;
               } else {
                  var7 = true;

                  try {
                     var5 = deriveNumericScope(var4, var2);
                  } catch (UnknownHostException var10) {
                  }
               }
            } catch (SocketException var11) {
            }
         }

         var4 = (byte[])var4.clone();
         if (var4.length != 16) {
            throw new InvalidObjectException("invalid address length: " + var4.length);
         } else if (this.holder.getFamily() != 2) {
            throw new InvalidObjectException("invalid address family type");
         } else {
            Inet6Address.Inet6AddressHolder var9 = new Inet6Address.Inet6AddressHolder(var4, var5, var6, var2, var7);
            UNSAFE.putObject(this, FIELDS_OFFSET, var9);
         }
      }
   }

   private synchronized void writeObject(ObjectOutputStream var1) throws IOException {
      String var2 = null;
      if (this.holder6.scope_ifname != null) {
         var2 = this.holder6.scope_ifname.getName();
         this.holder6.scope_ifname_set = true;
      }

      ObjectOutputStream.PutField var3 = var1.putFields();
      var3.put("ipaddress", this.holder6.ipaddress);
      var3.put("scope_id", this.holder6.scope_id);
      var3.put("scope_id_set", this.holder6.scope_id_set);
      var3.put("scope_ifname_set", this.holder6.scope_ifname_set);
      var3.put("ifname", var2);
      var1.writeFields();
   }

   public boolean isMulticastAddress() {
      return this.holder6.isMulticastAddress();
   }

   public boolean isAnyLocalAddress() {
      return this.holder6.isAnyLocalAddress();
   }

   public boolean isLoopbackAddress() {
      return this.holder6.isLoopbackAddress();
   }

   public boolean isLinkLocalAddress() {
      return this.holder6.isLinkLocalAddress();
   }

   static boolean isLinkLocalAddress(byte[] var0) {
      return (var0[0] & 255) == 254 && (var0[1] & 192) == 128;
   }

   public boolean isSiteLocalAddress() {
      return this.holder6.isSiteLocalAddress();
   }

   static boolean isSiteLocalAddress(byte[] var0) {
      return (var0[0] & 255) == 254 && (var0[1] & 192) == 192;
   }

   public boolean isMCGlobal() {
      return this.holder6.isMCGlobal();
   }

   public boolean isMCNodeLocal() {
      return this.holder6.isMCNodeLocal();
   }

   public boolean isMCLinkLocal() {
      return this.holder6.isMCLinkLocal();
   }

   public boolean isMCSiteLocal() {
      return this.holder6.isMCSiteLocal();
   }

   public boolean isMCOrgLocal() {
      return this.holder6.isMCOrgLocal();
   }

   public byte[] getAddress() {
      return (byte[])this.holder6.ipaddress.clone();
   }

   public int getScopeId() {
      return this.holder6.scope_id;
   }

   public NetworkInterface getScopedInterface() {
      return this.holder6.scope_ifname;
   }

   public String getHostAddress() {
      return this.holder6.getHostAddress();
   }

   public int hashCode() {
      return this.holder6.hashCode();
   }

   public boolean equals(Object var1) {
      if (var1 != null && var1 instanceof Inet6Address) {
         Inet6Address var2 = (Inet6Address)var1;
         return this.holder6.equals(var2.holder6);
      } else {
         return false;
      }
   }

   public boolean isIPv4CompatibleAddress() {
      return this.holder6.isIPv4CompatibleAddress();
   }

   static String numericToTextFormat(byte[] var0) {
      StringBuilder var1 = new StringBuilder(39);

      for(int var2 = 0; var2 < 8; ++var2) {
         var1.append(Integer.toHexString(var0[var2 << 1] << 8 & '\uff00' | var0[(var2 << 1) + 1] & 255));
         if (var2 < 7) {
            var1.append(":");
         }
      }

      return var1.toString();
   }

   private static native void init();

   static {
      init();
      serialPersistentFields = new ObjectStreamField[]{new ObjectStreamField("ipaddress", byte[].class), new ObjectStreamField("scope_id", Integer.TYPE), new ObjectStreamField("scope_id_set", Boolean.TYPE), new ObjectStreamField("scope_ifname_set", Boolean.TYPE), new ObjectStreamField("ifname", String.class)};

      try {
         Unsafe var0 = Unsafe.getUnsafe();
         FIELDS_OFFSET = var0.objectFieldOffset(Inet6Address.class.getDeclaredField("holder6"));
         UNSAFE = var0;
      } catch (ReflectiveOperationException var1) {
         throw new Error(var1);
      }
   }

   private class Inet6AddressHolder {
      byte[] ipaddress;
      int scope_id;
      boolean scope_id_set;
      NetworkInterface scope_ifname;
      boolean scope_ifname_set;

      private Inet6AddressHolder() {
         this.ipaddress = new byte[16];
      }

      private Inet6AddressHolder(byte[] var2, int var3, boolean var4, NetworkInterface var5, boolean var6) {
         this.ipaddress = var2;
         this.scope_id = var3;
         this.scope_id_set = var4;
         this.scope_ifname_set = var6;
         this.scope_ifname = var5;
      }

      void setAddr(byte[] var1) {
         if (var1.length == 16) {
            System.arraycopy(var1, 0, this.ipaddress, 0, 16);
         }

      }

      void init(byte[] var1, int var2) {
         this.setAddr(var1);
         if (var2 >= 0) {
            this.scope_id = var2;
            this.scope_id_set = true;
         }

      }

      void init(byte[] var1, NetworkInterface var2) throws UnknownHostException {
         this.setAddr(var1);
         if (var2 != null) {
            this.scope_id = Inet6Address.deriveNumericScope(this.ipaddress, var2);
            this.scope_id_set = true;
            this.scope_ifname = var2;
            this.scope_ifname_set = true;
         }

      }

      String getHostAddress() {
         String var1 = Inet6Address.numericToTextFormat(this.ipaddress);
         if (this.scope_ifname != null) {
            var1 = var1 + "%" + this.scope_ifname.getName();
         } else if (this.scope_id_set) {
            var1 = var1 + "%" + this.scope_id;
         }

         return var1;
      }

      public boolean equals(Object var1) {
         if (!(var1 instanceof Inet6Address.Inet6AddressHolder)) {
            return false;
         } else {
            Inet6Address.Inet6AddressHolder var2 = (Inet6Address.Inet6AddressHolder)var1;
            return Arrays.equals(this.ipaddress, var2.ipaddress);
         }
      }

      public int hashCode() {
         if (this.ipaddress == null) {
            return 0;
         } else {
            int var1 = 0;

            int var4;
            for(int var2 = 0; var2 < 16; var1 += var4) {
               int var3 = 0;

               for(var4 = 0; var3 < 4 && var2 < 16; ++var2) {
                  var4 = (var4 << 8) + this.ipaddress[var2];
                  ++var3;
               }
            }

            return var1;
         }
      }

      boolean isIPv4CompatibleAddress() {
         return this.ipaddress[0] == 0 && this.ipaddress[1] == 0 && this.ipaddress[2] == 0 && this.ipaddress[3] == 0 && this.ipaddress[4] == 0 && this.ipaddress[5] == 0 && this.ipaddress[6] == 0 && this.ipaddress[7] == 0 && this.ipaddress[8] == 0 && this.ipaddress[9] == 0 && this.ipaddress[10] == 0 && this.ipaddress[11] == 0;
      }

      boolean isMulticastAddress() {
         return (this.ipaddress[0] & 255) == 255;
      }

      boolean isAnyLocalAddress() {
         byte var1 = 0;

         for(int var2 = 0; var2 < 16; ++var2) {
            var1 |= this.ipaddress[var2];
         }

         return var1 == 0;
      }

      boolean isLoopbackAddress() {
         byte var1 = 0;

         for(int var2 = 0; var2 < 15; ++var2) {
            var1 |= this.ipaddress[var2];
         }

         return var1 == 0 && this.ipaddress[15] == 1;
      }

      boolean isLinkLocalAddress() {
         return (this.ipaddress[0] & 255) == 254 && (this.ipaddress[1] & 192) == 128;
      }

      boolean isSiteLocalAddress() {
         return (this.ipaddress[0] & 255) == 254 && (this.ipaddress[1] & 192) == 192;
      }

      boolean isMCGlobal() {
         return (this.ipaddress[0] & 255) == 255 && (this.ipaddress[1] & 15) == 14;
      }

      boolean isMCNodeLocal() {
         return (this.ipaddress[0] & 255) == 255 && (this.ipaddress[1] & 15) == 1;
      }

      boolean isMCLinkLocal() {
         return (this.ipaddress[0] & 255) == 255 && (this.ipaddress[1] & 15) == 2;
      }

      boolean isMCSiteLocal() {
         return (this.ipaddress[0] & 255) == 255 && (this.ipaddress[1] & 15) == 5;
      }

      boolean isMCOrgLocal() {
         return (this.ipaddress[0] & 255) == 255 && (this.ipaddress[1] & 15) == 8;
      }

      // $FF: synthetic method
      Inet6AddressHolder(Object var2) {
         this();
      }

      // $FF: synthetic method
      Inet6AddressHolder(byte[] var2, int var3, boolean var4, NetworkInterface var5, boolean var6, Object var7) {
         this(var2, var3, var4, var5, var6);
      }
   }
}
