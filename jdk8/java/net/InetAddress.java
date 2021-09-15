package java.net;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamException;
import java.io.ObjectStreamField;
import java.io.Serializable;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ServiceLoader;
import sun.misc.Unsafe;
import sun.net.InetAddressCachePolicy;
import sun.net.spi.nameservice.NameService;
import sun.net.spi.nameservice.NameServiceDescriptor;
import sun.net.util.IPAddressUtil;
import sun.security.action.GetBooleanAction;
import sun.security.action.GetPropertyAction;

public class InetAddress implements Serializable {
   static final int IPv4 = 1;
   static final int IPv6 = 2;
   static transient boolean preferIPv6Address = false;
   final transient InetAddress.InetAddressHolder holder = new InetAddress.InetAddressHolder();
   private static List<NameService> nameServices = null;
   private transient String canonicalHostName = null;
   private static final long serialVersionUID = 3286316764910316507L;
   private static InetAddress.Cache addressCache;
   private static InetAddress.Cache negativeCache;
   private static boolean addressCacheInit;
   static InetAddress[] unknown_array;
   static InetAddressImpl impl;
   private static final HashMap<String, Void> lookupTable;
   private static InetAddress cachedLocalHost;
   private static long cacheTime;
   private static final long maxCacheTime = 5000L;
   private static final Object cacheLock;
   private static final long FIELDS_OFFSET;
   private static final Unsafe UNSAFE;
   private static final ObjectStreamField[] serialPersistentFields;

   InetAddress.InetAddressHolder holder() {
      return this.holder;
   }

   InetAddress() {
   }

   private Object readResolve() throws ObjectStreamException {
      return new Inet4Address(this.holder().getHostName(), this.holder().getAddress());
   }

   public boolean isMulticastAddress() {
      return false;
   }

   public boolean isAnyLocalAddress() {
      return false;
   }

   public boolean isLoopbackAddress() {
      return false;
   }

   public boolean isLinkLocalAddress() {
      return false;
   }

   public boolean isSiteLocalAddress() {
      return false;
   }

   public boolean isMCGlobal() {
      return false;
   }

   public boolean isMCNodeLocal() {
      return false;
   }

   public boolean isMCLinkLocal() {
      return false;
   }

   public boolean isMCSiteLocal() {
      return false;
   }

   public boolean isMCOrgLocal() {
      return false;
   }

   public boolean isReachable(int var1) throws IOException {
      return this.isReachable((NetworkInterface)null, 0, var1);
   }

   public boolean isReachable(NetworkInterface var1, int var2, int var3) throws IOException {
      if (var2 < 0) {
         throw new IllegalArgumentException("ttl can't be negative");
      } else if (var3 < 0) {
         throw new IllegalArgumentException("timeout can't be negative");
      } else {
         return impl.isReachable(this, var3, var1, var2);
      }
   }

   public String getHostName() {
      return this.getHostName(true);
   }

   String getHostName(boolean var1) {
      if (this.holder().getHostName() == null) {
         this.holder().hostName = getHostFromNameService(this, var1);
      }

      return this.holder().getHostName();
   }

   public String getCanonicalHostName() {
      if (this.canonicalHostName == null) {
         this.canonicalHostName = getHostFromNameService(this, true);
      }

      return this.canonicalHostName;
   }

   private static String getHostFromNameService(InetAddress var0, boolean var1) {
      String var2 = null;
      Iterator var3 = nameServices.iterator();

      while(var3.hasNext()) {
         NameService var4 = (NameService)var3.next();

         try {
            var2 = var4.getHostByAddr(var0.getAddress());
            if (var1) {
               SecurityManager var5 = System.getSecurityManager();
               if (var5 != null) {
                  var5.checkConnect(var2, -1);
               }
            }

            InetAddress[] var10 = getAllByName0(var2, var1);
            boolean var6 = false;
            if (var10 != null) {
               for(int var7 = 0; !var6 && var7 < var10.length; ++var7) {
                  var6 = var0.equals(var10[var7]);
               }
            }

            if (!var6) {
               var2 = var0.getHostAddress();
               return var2;
            }
            break;
         } catch (SecurityException var8) {
            var2 = var0.getHostAddress();
            break;
         } catch (UnknownHostException var9) {
            var2 = var0.getHostAddress();
         }
      }

      return var2;
   }

   public byte[] getAddress() {
      return null;
   }

   public String getHostAddress() {
      return null;
   }

   public int hashCode() {
      return -1;
   }

   public boolean equals(Object var1) {
      return false;
   }

   public String toString() {
      String var1 = this.holder().getHostName();
      return (var1 != null ? var1 : "") + "/" + this.getHostAddress();
   }

   private static void cacheInitIfNeeded() {
      assert Thread.holdsLock(addressCache);

      if (!addressCacheInit) {
         unknown_array = new InetAddress[1];
         unknown_array[0] = impl.anyLocalAddress();
         addressCache.put(impl.anyLocalAddress().getHostName(), unknown_array);
         addressCacheInit = true;
      }
   }

   private static void cacheAddresses(String var0, InetAddress[] var1, boolean var2) {
      var0 = var0.toLowerCase();
      synchronized(addressCache) {
         cacheInitIfNeeded();
         if (var2) {
            addressCache.put(var0, var1);
         } else {
            negativeCache.put(var0, var1);
         }

      }
   }

   private static InetAddress[] getCachedAddresses(String var0) {
      var0 = var0.toLowerCase();
      synchronized(addressCache) {
         cacheInitIfNeeded();
         InetAddress.CacheEntry var2 = addressCache.get(var0);
         if (var2 == null) {
            var2 = negativeCache.get(var0);
         }

         return var2 != null ? var2.addresses : null;
      }
   }

   private static NameService createNSProvider(String var0) {
      if (var0 == null) {
         return null;
      } else {
         NameService var1 = null;
         if (var0.equals("default")) {
            var1 = new NameService() {
               public InetAddress[] lookupAllHostAddr(String var1) throws UnknownHostException {
                  return InetAddress.impl.lookupAllHostAddr(var1);
               }

               public String getHostByAddr(byte[] var1) throws UnknownHostException {
                  return InetAddress.impl.getHostByAddr(var1);
               }
            };
         } else {
            final String var2 = var0;

            try {
               var1 = (NameService)AccessController.doPrivileged(new PrivilegedExceptionAction<NameService>() {
                  public NameService run() {
                     Iterator var1 = ServiceLoader.load(NameServiceDescriptor.class).iterator();

                     while(true) {
                        NameServiceDescriptor var2x;
                        do {
                           if (!var1.hasNext()) {
                              return null;
                           }

                           var2x = (NameServiceDescriptor)var1.next();
                        } while(!var2.equalsIgnoreCase(var2x.getType() + "," + var2x.getProviderName()));

                        try {
                           return var2x.createNameService();
                        } catch (Exception var4) {
                           var4.printStackTrace();
                           System.err.println("Cannot create name service:" + var2 + ": " + var4);
                        }
                     }
                  }
               });
            } catch (PrivilegedActionException var4) {
            }
         }

         return var1;
      }
   }

   public static InetAddress getByAddress(String var0, byte[] var1) throws UnknownHostException {
      if (var0 != null && var0.length() > 0 && var0.charAt(0) == '[' && var0.charAt(var0.length() - 1) == ']') {
         var0 = var0.substring(1, var0.length() - 1);
      }

      if (var1 != null) {
         if (var1.length == 4) {
            return new Inet4Address(var0, var1);
         }

         if (var1.length == 16) {
            byte[] var2 = IPAddressUtil.convertFromIPv4MappedAddress(var1);
            if (var2 != null) {
               return new Inet4Address(var0, var2);
            }

            return new Inet6Address(var0, var1);
         }
      }

      throw new UnknownHostException("addr is of illegal length");
   }

   public static InetAddress getByName(String var0) throws UnknownHostException {
      return getAllByName(var0)[0];
   }

   private static InetAddress getByName(String var0, InetAddress var1) throws UnknownHostException {
      return getAllByName(var0, var1)[0];
   }

   public static InetAddress[] getAllByName(String var0) throws UnknownHostException {
      return getAllByName(var0, (InetAddress)null);
   }

   private static InetAddress[] getAllByName(String var0, InetAddress var1) throws UnknownHostException {
      if (var0 != null && var0.length() != 0) {
         boolean var7 = false;
         if (var0.charAt(0) == '[') {
            if (var0.length() <= 2 || var0.charAt(var0.length() - 1) != ']') {
               throw new UnknownHostException(var0 + ": invalid IPv6 address");
            }

            var0 = var0.substring(1, var0.length() - 1);
            var7 = true;
         }

         if (Character.digit((char)var0.charAt(0), 16) == -1 && var0.charAt(0) != ':') {
            if (var7) {
               throw new UnknownHostException("[" + var0 + "]");
            }
         } else {
            Object var3 = null;
            int var4 = -1;
            String var5 = null;
            byte[] var8 = IPAddressUtil.textToNumericFormatV4(var0);
            if (var8 == null) {
               int var6;
               if ((var6 = var0.indexOf("%")) != -1) {
                  var4 = checkNumericZone(var0);
                  if (var4 == -1) {
                     var5 = var0.substring(var6 + 1);
                  }
               }

               if ((var8 = IPAddressUtil.textToNumericFormatV6(var0)) == null && var0.contains(":")) {
                  throw new UnknownHostException(var0 + ": invalid IPv6 address");
               }
            } else if (var7) {
               throw new UnknownHostException("[" + var0 + "]");
            }

            InetAddress[] var9 = new InetAddress[1];
            if (var8 != null) {
               if (var8.length == 4) {
                  var9[0] = new Inet4Address((String)null, var8);
               } else if (var5 != null) {
                  var9[0] = new Inet6Address((String)null, var8, var5);
               } else {
                  var9[0] = new Inet6Address((String)null, var8, var4);
               }

               return var9;
            }
         }

         return getAllByName0(var0, var1, true);
      } else {
         InetAddress[] var2 = new InetAddress[]{impl.loopbackAddress()};
         return var2;
      }
   }

   public static InetAddress getLoopbackAddress() {
      return impl.loopbackAddress();
   }

   private static int checkNumericZone(String var0) throws UnknownHostException {
      int var1 = var0.indexOf(37);
      int var2 = var0.length();
      int var4 = 0;
      if (var1 == -1) {
         return -1;
      } else {
         for(int var5 = var1 + 1; var5 < var2; ++var5) {
            char var6 = var0.charAt(var5);
            if (var6 == ']') {
               if (var5 == var1 + 1) {
                  return -1;
               }
               break;
            }

            int var3;
            if ((var3 = Character.digit((char)var6, 10)) < 0) {
               return -1;
            }

            var4 = var4 * 10 + var3;
         }

         return var4;
      }
   }

   private static InetAddress[] getAllByName0(String var0) throws UnknownHostException {
      return getAllByName0(var0, true);
   }

   static InetAddress[] getAllByName0(String var0, boolean var1) throws UnknownHostException {
      return getAllByName0(var0, (InetAddress)null, var1);
   }

   private static InetAddress[] getAllByName0(String var0, InetAddress var1, boolean var2) throws UnknownHostException {
      if (var2) {
         SecurityManager var3 = System.getSecurityManager();
         if (var3 != null) {
            var3.checkConnect(var0, -1);
         }
      }

      InetAddress[] var4 = getCachedAddresses(var0);
      if (var4 == null) {
         var4 = getAddressesFromNameService(var0, var1);
      }

      if (var4 == unknown_array) {
         throw new UnknownHostException(var0);
      } else {
         return (InetAddress[])var4.clone();
      }
   }

   private static InetAddress[] getAddressesFromNameService(String var0, InetAddress var1) throws UnknownHostException {
      InetAddress[] var2 = null;
      boolean var3 = false;
      UnknownHostException var4 = null;
      if ((var2 = checkLookupTable(var0)) == null) {
         try {
            Iterator var5 = nameServices.iterator();

            while(var5.hasNext()) {
               NameService var6 = (NameService)var5.next();

               try {
                  var2 = var6.lookupAllHostAddr(var0);
                  var3 = true;
                  break;
               } catch (UnknownHostException var12) {
                  if (var0.equalsIgnoreCase("localhost")) {
                     InetAddress[] var8 = new InetAddress[]{impl.loopbackAddress()};
                     var2 = var8;
                     var3 = true;
                     break;
                  }

                  var2 = unknown_array;
                  var3 = false;
                  var4 = var12;
               }
            }

            if (var1 != null && var2.length > 1 && !var2[0].equals(var1)) {
               int var14;
               for(var14 = 1; var14 < var2.length && !var2[var14].equals(var1); ++var14) {
               }

               if (var14 < var2.length) {
                  InetAddress var7 = var1;

                  for(int var16 = 0; var16 < var14; ++var16) {
                     InetAddress var15 = var2[var16];
                     var2[var16] = var7;
                     var7 = var15;
                  }

                  var2[var14] = var7;
               }
            }

            cacheAddresses(var0, var2, var3);
            if (!var3 && var4 != null) {
               throw var4;
            }
         } finally {
            updateLookupTable(var0);
         }
      }

      return var2;
   }

   private static InetAddress[] checkLookupTable(String var0) {
      synchronized(lookupTable) {
         if (!lookupTable.containsKey(var0)) {
            lookupTable.put(var0, (Object)null);
            return null;
         }

         while(true) {
            if (!lookupTable.containsKey(var0)) {
               break;
            }

            try {
               lookupTable.wait();
            } catch (InterruptedException var6) {
            }
         }
      }

      InetAddress[] var1 = getCachedAddresses(var0);
      if (var1 == null) {
         synchronized(lookupTable) {
            lookupTable.put(var0, (Object)null);
            return null;
         }
      } else {
         return var1;
      }
   }

   private static void updateLookupTable(String var0) {
      synchronized(lookupTable) {
         lookupTable.remove(var0);
         lookupTable.notifyAll();
      }
   }

   public static InetAddress getByAddress(byte[] var0) throws UnknownHostException {
      return getByAddress((String)null, var0);
   }

   public static InetAddress getLocalHost() throws UnknownHostException {
      SecurityManager var0 = System.getSecurityManager();

      try {
         String var1 = impl.getLocalHostName();
         if (var0 != null) {
            var0.checkConnect(var1, -1);
         }

         if (var1.equals("localhost")) {
            return impl.loopbackAddress();
         } else {
            InetAddress var2 = null;
            synchronized(cacheLock) {
               long var4 = System.currentTimeMillis();
               if (cachedLocalHost != null) {
                  if (var4 - cacheTime < 5000L) {
                     var2 = cachedLocalHost;
                  } else {
                     cachedLocalHost = null;
                  }
               }

               if (var2 == null) {
                  InetAddress[] var6;
                  try {
                     var6 = getAddressesFromNameService(var1, (InetAddress)null);
                  } catch (UnknownHostException var10) {
                     UnknownHostException var8 = new UnknownHostException(var1 + ": " + var10.getMessage());
                     var8.initCause(var10);
                     throw var8;
                  }

                  cachedLocalHost = var6[0];
                  cacheTime = var4;
                  var2 = var6[0];
               }
            }

            return var2;
         }
      } catch (SecurityException var12) {
         return impl.loopbackAddress();
      }
   }

   private static native void init();

   static InetAddress anyLocalAddress() {
      return impl.anyLocalAddress();
   }

   static InetAddressImpl loadImpl(String var0) {
      Object var1 = null;
      String var2 = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("impl.prefix", "")));

      try {
         var1 = Class.forName("java.net." + var2 + var0).newInstance();
      } catch (ClassNotFoundException var5) {
         System.err.println("Class not found: java.net." + var2 + var0 + ":\ncheck impl.prefix property in your properties file.");
      } catch (InstantiationException var6) {
         System.err.println("Could not instantiate: java.net." + var2 + var0 + ":\ncheck impl.prefix property in your properties file.");
      } catch (IllegalAccessException var7) {
         System.err.println("Cannot access class: java.net." + var2 + var0 + ":\ncheck impl.prefix property in your properties file.");
      }

      if (var1 == null) {
         try {
            var1 = Class.forName(var0).newInstance();
         } catch (Exception var4) {
            throw new Error("System property impl.prefix incorrect");
         }
      }

      return (InetAddressImpl)var1;
   }

   private void readObjectNoData(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      if (this.getClass().getClassLoader() != null) {
         throw new SecurityException("invalid address type");
      }
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      if (this.getClass().getClassLoader() != null) {
         throw new SecurityException("invalid address type");
      } else {
         ObjectInputStream.GetField var2 = var1.readFields();
         String var3 = (String)var2.get("hostName", (Object)null);
         int var4 = var2.get("address", (int)0);
         int var5 = var2.get("family", (int)0);
         InetAddress.InetAddressHolder var6 = new InetAddress.InetAddressHolder(var3, var4, var5);
         UNSAFE.putObject(this, FIELDS_OFFSET, var6);
      }
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      if (this.getClass().getClassLoader() != null) {
         throw new SecurityException("invalid address type");
      } else {
         ObjectOutputStream.PutField var2 = var1.putFields();
         var2.put("hostName", this.holder().getHostName());
         var2.put("address", this.holder().getAddress());
         var2.put("family", this.holder().getFamily());
         var1.writeFields();
      }
   }

   static {
      preferIPv6Address = (Boolean)AccessController.doPrivileged((PrivilegedAction)(new GetBooleanAction("java.net.preferIPv6Addresses")));
      AccessController.doPrivileged(new PrivilegedAction<Void>() {
         public Void run() {
            System.loadLibrary("net");
            return null;
         }
      });
      init();
      addressCache = new InetAddress.Cache(InetAddress.Cache.Type.Positive);
      negativeCache = new InetAddress.Cache(InetAddress.Cache.Type.Negative);
      addressCacheInit = false;
      lookupTable = new HashMap();
      impl = InetAddressImplFactory.create();
      String var0 = null;
      String var1 = "sun.net.spi.nameservice.provider.";
      int var2 = 1;
      nameServices = new ArrayList();

      NameService var3;
      for(var0 = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction(var1 + var2))); var0 != null; var0 = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction(var1 + var2)))) {
         var3 = createNSProvider(var0);
         if (var3 != null) {
            nameServices.add(var3);
         }

         ++var2;
      }

      if (nameServices.size() == 0) {
         var3 = createNSProvider("default");
         nameServices.add(var3);
      }

      cachedLocalHost = null;
      cacheTime = 0L;
      cacheLock = new Object();

      try {
         Unsafe var5 = Unsafe.getUnsafe();
         FIELDS_OFFSET = var5.objectFieldOffset(InetAddress.class.getDeclaredField("holder"));
         UNSAFE = var5;
      } catch (ReflectiveOperationException var4) {
         throw new Error(var4);
      }

      serialPersistentFields = new ObjectStreamField[]{new ObjectStreamField("hostName", String.class), new ObjectStreamField("address", Integer.TYPE), new ObjectStreamField("family", Integer.TYPE)};
   }

   static final class Cache {
      private LinkedHashMap<String, InetAddress.CacheEntry> cache;
      private InetAddress.Cache.Type type;

      public Cache(InetAddress.Cache.Type var1) {
         this.type = var1;
         this.cache = new LinkedHashMap();
      }

      private int getPolicy() {
         return this.type == InetAddress.Cache.Type.Positive ? InetAddressCachePolicy.get() : InetAddressCachePolicy.getNegative();
      }

      public InetAddress.Cache put(String var1, InetAddress[] var2) {
         int var3 = this.getPolicy();
         if (var3 == 0) {
            return this;
         } else {
            if (var3 != -1) {
               LinkedList var4 = new LinkedList();
               long var5 = System.currentTimeMillis();
               Iterator var7 = this.cache.keySet().iterator();

               String var8;
               while(var7.hasNext()) {
                  var8 = (String)var7.next();
                  InetAddress.CacheEntry var9 = (InetAddress.CacheEntry)this.cache.get(var8);
                  if (var9.expiration < 0L || var9.expiration >= var5) {
                     break;
                  }

                  var4.add(var8);
               }

               var7 = var4.iterator();

               while(var7.hasNext()) {
                  var8 = (String)var7.next();
                  this.cache.remove(var8);
               }
            }

            long var10;
            if (var3 == -1) {
               var10 = -1L;
            } else {
               var10 = System.currentTimeMillis() + (long)(var3 * 1000);
            }

            InetAddress.CacheEntry var6 = new InetAddress.CacheEntry(var2, var10);
            this.cache.put(var1, var6);
            return this;
         }
      }

      public InetAddress.CacheEntry get(String var1) {
         int var2 = this.getPolicy();
         if (var2 == 0) {
            return null;
         } else {
            InetAddress.CacheEntry var3 = (InetAddress.CacheEntry)this.cache.get(var1);
            if (var3 != null && var2 != -1 && var3.expiration >= 0L && var3.expiration < System.currentTimeMillis()) {
               this.cache.remove(var1);
               var3 = null;
            }

            return var3;
         }
      }

      static enum Type {
         Positive,
         Negative;
      }
   }

   static final class CacheEntry {
      InetAddress[] addresses;
      long expiration;

      CacheEntry(InetAddress[] var1, long var2) {
         this.addresses = var1;
         this.expiration = var2;
      }
   }

   static class InetAddressHolder {
      String originalHostName;
      String hostName;
      int address;
      int family;

      InetAddressHolder() {
      }

      InetAddressHolder(String var1, int var2, int var3) {
         this.originalHostName = var1;
         this.hostName = var1;
         this.address = var2;
         this.family = var3;
      }

      void init(String var1, int var2) {
         this.originalHostName = var1;
         this.hostName = var1;
         if (var2 != -1) {
            this.family = var2;
         }

      }

      String getHostName() {
         return this.hostName;
      }

      String getOriginalHostName() {
         return this.originalHostName;
      }

      int getAddress() {
         return this.address;
      }

      int getFamily() {
         return this.family;
      }
   }
}
