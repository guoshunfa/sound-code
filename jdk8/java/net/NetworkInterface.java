package java.net;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.NoSuchElementException;

public final class NetworkInterface {
   private String name;
   private String displayName;
   private int index;
   private InetAddress[] addrs;
   private InterfaceAddress[] bindings;
   private NetworkInterface[] childs;
   private NetworkInterface parent = null;
   private boolean virtual = false;
   private static final NetworkInterface defaultInterface;
   private static final int defaultIndex;

   NetworkInterface() {
   }

   NetworkInterface(String var1, int var2, InetAddress[] var3) {
      this.name = var1;
      this.index = var2;
      this.addrs = var3;
   }

   public String getName() {
      return this.name;
   }

   public Enumeration<InetAddress> getInetAddresses() {
      class checkedAddresses implements Enumeration<InetAddress> {
         private int i = 0;
         private int count = 0;
         private InetAddress[] local_addrs;

         checkedAddresses() {
            this.local_addrs = new InetAddress[NetworkInterface.this.addrs.length];
            boolean var2 = true;
            SecurityManager var3 = System.getSecurityManager();
            if (var3 != null) {
               try {
                  var3.checkPermission(new NetPermission("getNetworkInformation"));
               } catch (SecurityException var7) {
                  var2 = false;
               }
            }

            for(int var4 = 0; var4 < NetworkInterface.this.addrs.length; ++var4) {
               try {
                  if (var3 != null && !var2) {
                     var3.checkConnect(NetworkInterface.this.addrs[var4].getHostAddress(), -1);
                  }

                  this.local_addrs[this.count++] = NetworkInterface.this.addrs[var4];
               } catch (SecurityException var6) {
               }
            }

         }

         public InetAddress nextElement() {
            if (this.i < this.count) {
               return this.local_addrs[this.i++];
            } else {
               throw new NoSuchElementException();
            }
         }

         public boolean hasMoreElements() {
            return this.i < this.count;
         }
      }

      return new checkedAddresses();
   }

   public List<InterfaceAddress> getInterfaceAddresses() {
      ArrayList var1 = new ArrayList(1);
      SecurityManager var2 = System.getSecurityManager();

      for(int var3 = 0; var3 < this.bindings.length; ++var3) {
         try {
            if (var2 != null) {
               var2.checkConnect(this.bindings[var3].getAddress().getHostAddress(), -1);
            }

            var1.add(this.bindings[var3]);
         } catch (SecurityException var5) {
         }
      }

      return var1;
   }

   public Enumeration<NetworkInterface> getSubInterfaces() {
      class subIFs implements Enumeration<NetworkInterface> {
         private int i = 0;

         public NetworkInterface nextElement() {
            if (this.i < NetworkInterface.this.childs.length) {
               return NetworkInterface.this.childs[this.i++];
            } else {
               throw new NoSuchElementException();
            }
         }

         public boolean hasMoreElements() {
            return this.i < NetworkInterface.this.childs.length;
         }
      }

      return new subIFs();
   }

   public NetworkInterface getParent() {
      return this.parent;
   }

   public int getIndex() {
      return this.index;
   }

   public String getDisplayName() {
      return "".equals(this.displayName) ? null : this.displayName;
   }

   public static NetworkInterface getByName(String var0) throws SocketException {
      if (var0 == null) {
         throw new NullPointerException();
      } else {
         return getByName0(var0);
      }
   }

   public static NetworkInterface getByIndex(int var0) throws SocketException {
      if (var0 < 0) {
         throw new IllegalArgumentException("Interface index can't be negative");
      } else {
         return getByIndex0(var0);
      }
   }

   public static NetworkInterface getByInetAddress(InetAddress var0) throws SocketException {
      if (var0 == null) {
         throw new NullPointerException();
      } else if (!(var0 instanceof Inet4Address) && !(var0 instanceof Inet6Address)) {
         throw new IllegalArgumentException("invalid address type");
      } else {
         return getByInetAddress0(var0);
      }
   }

   public static Enumeration<NetworkInterface> getNetworkInterfaces() throws SocketException {
      final NetworkInterface[] var0 = getAll();
      return var0 == null ? null : new Enumeration<NetworkInterface>() {
         private int i = 0;

         public NetworkInterface nextElement() {
            if (var0 != null && this.i < var0.length) {
               NetworkInterface var1 = var0[this.i++];
               return var1;
            } else {
               throw new NoSuchElementException();
            }
         }

         public boolean hasMoreElements() {
            return var0 != null && this.i < var0.length;
         }
      };
   }

   private static native NetworkInterface[] getAll() throws SocketException;

   private static native NetworkInterface getByName0(String var0) throws SocketException;

   private static native NetworkInterface getByIndex0(int var0) throws SocketException;

   private static native NetworkInterface getByInetAddress0(InetAddress var0) throws SocketException;

   public boolean isUp() throws SocketException {
      return isUp0(this.name, this.index);
   }

   public boolean isLoopback() throws SocketException {
      return isLoopback0(this.name, this.index);
   }

   public boolean isPointToPoint() throws SocketException {
      return isP2P0(this.name, this.index);
   }

   public boolean supportsMulticast() throws SocketException {
      return supportsMulticast0(this.name, this.index);
   }

   public byte[] getHardwareAddress() throws SocketException {
      SecurityManager var1 = System.getSecurityManager();
      if (var1 != null) {
         try {
            var1.checkPermission(new NetPermission("getNetworkInformation"));
         } catch (SecurityException var6) {
            if (!this.getInetAddresses().hasMoreElements()) {
               return null;
            }
         }
      }

      InetAddress[] var2 = this.addrs;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         InetAddress var5 = var2[var4];
         if (var5 instanceof Inet4Address) {
            return getMacAddr0(((Inet4Address)var5).getAddress(), this.name, this.index);
         }
      }

      return getMacAddr0((byte[])null, this.name, this.index);
   }

   public int getMTU() throws SocketException {
      return getMTU0(this.name, this.index);
   }

   public boolean isVirtual() {
      return this.virtual;
   }

   private static native boolean isUp0(String var0, int var1) throws SocketException;

   private static native boolean isLoopback0(String var0, int var1) throws SocketException;

   private static native boolean supportsMulticast0(String var0, int var1) throws SocketException;

   private static native boolean isP2P0(String var0, int var1) throws SocketException;

   private static native byte[] getMacAddr0(byte[] var0, String var1, int var2) throws SocketException;

   private static native int getMTU0(String var0, int var1) throws SocketException;

   public boolean equals(Object var1) {
      if (!(var1 instanceof NetworkInterface)) {
         return false;
      } else {
         NetworkInterface var2 = (NetworkInterface)var1;
         if (this.name != null) {
            if (!this.name.equals(var2.name)) {
               return false;
            }
         } else if (var2.name != null) {
            return false;
         }

         if (this.addrs == null) {
            return var2.addrs == null;
         } else if (var2.addrs == null) {
            return false;
         } else if (this.addrs.length != var2.addrs.length) {
            return false;
         } else {
            InetAddress[] var3 = var2.addrs;
            int var4 = var3.length;

            for(int var5 = 0; var5 < var4; ++var5) {
               boolean var6 = false;

               for(int var7 = 0; var7 < var4; ++var7) {
                  if (this.addrs[var5].equals(var3[var7])) {
                     var6 = true;
                     break;
                  }
               }

               if (!var6) {
                  return false;
               }
            }

            return true;
         }
      }
   }

   public int hashCode() {
      return this.name == null ? 0 : this.name.hashCode();
   }

   public String toString() {
      String var1 = "name:";
      var1 = var1 + (this.name == null ? "null" : this.name);
      if (this.displayName != null) {
         var1 = var1 + " (" + this.displayName + ")";
      }

      return var1;
   }

   private static native void init();

   static NetworkInterface getDefault() {
      return defaultInterface;
   }

   static {
      AccessController.doPrivileged(new PrivilegedAction<Void>() {
         public Void run() {
            System.loadLibrary("net");
            return null;
         }
      });
      init();
      defaultInterface = DefaultInterface.getDefault();
      if (defaultInterface != null) {
         defaultIndex = defaultInterface.getIndex();
      } else {
         defaultIndex = 0;
      }

   }
}
