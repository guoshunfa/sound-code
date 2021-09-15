package java.net;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.AccessController;
import java.security.Permission;
import java.security.PermissionCollection;
import java.security.PrivilegedAction;
import java.util.StringTokenizer;
import sun.net.PortConfig;
import sun.net.RegisteredDomain;
import sun.net.util.IPAddressUtil;
import sun.security.action.GetBooleanAction;
import sun.security.util.Debug;

public final class SocketPermission extends Permission implements Serializable {
   private static final long serialVersionUID = -7204263841984476862L;
   private static final int CONNECT = 1;
   private static final int LISTEN = 2;
   private static final int ACCEPT = 4;
   private static final int RESOLVE = 8;
   private static final int NONE = 0;
   private static final int ALL = 15;
   private static final int PORT_MIN = 0;
   private static final int PORT_MAX = 65535;
   private static final int PRIV_PORT_MAX = 1023;
   private static final int DEF_EPH_LOW = 49152;
   private transient int mask;
   private String actions;
   private transient String hostname;
   private transient String cname;
   private transient InetAddress[] addresses;
   private transient boolean wildcard;
   private transient boolean init_with_ip;
   private transient boolean invalid;
   private transient int[] portrange;
   private transient boolean defaultDeny = false;
   private transient boolean untrusted;
   private transient boolean trusted;
   private static boolean trustNameService;
   private static Debug debug = null;
   private static boolean debugInit = false;
   private transient String cdomain;
   private transient String hdomain;

   private static synchronized Debug getDebug() {
      if (!debugInit) {
         debug = Debug.getInstance("access");
         debugInit = true;
      }

      return debug;
   }

   public SocketPermission(String var1, String var2) {
      super(getHost(var1));
      this.init(this.getName(), getMask(var2));
   }

   SocketPermission(String var1, int var2) {
      super(getHost(var1));
      this.init(this.getName(), var2);
   }

   private void setDeny() {
      this.defaultDeny = true;
   }

   private static String getHost(String var0) {
      if (var0.equals("")) {
         return "localhost";
      } else {
         if (var0.charAt(0) != '[' && var0.indexOf(58) != var0.lastIndexOf(58)) {
            StringTokenizer var2 = new StringTokenizer(var0, ":");
            int var3 = var2.countTokens();
            if (var3 == 9) {
               int var1 = var0.lastIndexOf(58);
               var0 = "[" + var0.substring(0, var1) + "]" + var0.substring(var1);
            } else {
               if (var3 != 8 || var0.indexOf("::") != -1) {
                  throw new IllegalArgumentException("Ambiguous hostport part");
               }

               var0 = "[" + var0 + "]";
            }
         }

         return var0;
      }
   }

   private int[] parsePort(String var1) throws Exception {
      if (var1 != null && !var1.equals("") && !var1.equals("*")) {
         int var2 = var1.indexOf(45);
         if (var2 == -1) {
            int var7 = Integer.parseInt(var1);
            return new int[]{var7, var7};
         } else {
            String var3 = var1.substring(0, var2);
            String var4 = var1.substring(var2 + 1);
            int var5;
            if (var3.equals("")) {
               var5 = 0;
            } else {
               var5 = Integer.parseInt(var3);
            }

            int var6;
            if (var4.equals("")) {
               var6 = 65535;
            } else {
               var6 = Integer.parseInt(var4);
            }

            if (var5 >= 0 && var6 >= 0 && var6 >= var5) {
               return new int[]{var5, var6};
            } else {
               throw new IllegalArgumentException("invalid port range");
            }
         }
      } else {
         return new int[]{0, 65535};
      }
   }

   private boolean includesEphemerals() {
      return this.portrange[0] == 0;
   }

   private void init(String var1, int var2) {
      if ((var2 & 15) != var2) {
         throw new IllegalArgumentException("invalid actions mask");
      } else {
         this.mask = var2 | 8;
         byte var3 = 0;
         boolean var4 = false;
         boolean var5 = false;
         boolean var6 = true;
         String var7 = var1;
         byte var14;
         int var15;
         if (var1.charAt(0) == '[') {
            var14 = 1;
            int var13 = var1.indexOf(93);
            if (var13 == -1) {
               throw new IllegalArgumentException("invalid host/port: " + var1);
            }

            var1 = var1.substring(var14, var13);
            var15 = var7.indexOf(58, var13 + 1);
         } else {
            var14 = 0;
            var15 = var1.indexOf(58, var3);
            if (var15 != -1) {
               var1 = var1.substring(var14, var15);
            }
         }

         if (var15 != -1) {
            String var8 = var1.substring(var15 + 1);

            try {
               this.portrange = this.parsePort(var8);
            } catch (Exception var12) {
               throw new IllegalArgumentException("invalid port range: " + var8);
            }
         } else {
            this.portrange = new int[]{0, 65535};
         }

         this.hostname = var1;
         if (var1.lastIndexOf(42) > 0) {
            throw new IllegalArgumentException("invalid host wildcard specification");
         } else if (var1.startsWith("*")) {
            this.wildcard = true;
            if (var1.equals("*")) {
               this.cname = "";
            } else {
               if (!var1.startsWith("*.")) {
                  throw new IllegalArgumentException("invalid host wildcard specification");
               }

               this.cname = var1.substring(1).toLowerCase();
            }

         } else {
            if (var1.length() > 0) {
               char var16 = var1.charAt(0);
               if (var16 == ':' || Character.digit((char)var16, 16) != -1) {
                  byte[] var9 = IPAddressUtil.textToNumericFormatV4(var1);
                  if (var9 == null) {
                     var9 = IPAddressUtil.textToNumericFormatV6(var1);
                  }

                  if (var9 != null) {
                     try {
                        this.addresses = new InetAddress[]{InetAddress.getByAddress(var9)};
                        this.init_with_ip = true;
                     } catch (UnknownHostException var11) {
                        this.invalid = true;
                     }
                  }
               }
            }

         }
      }
   }

   private static int getMask(String var0) {
      if (var0 == null) {
         throw new NullPointerException("action can't be null");
      } else if (var0.equals("")) {
         throw new IllegalArgumentException("action can't be empty");
      } else {
         int var1 = 0;
         if (var0 == "resolve") {
            return 8;
         } else if (var0 == "connect") {
            return 1;
         } else if (var0 == "listen") {
            return 2;
         } else if (var0 == "accept") {
            return 4;
         } else if (var0 == "connect,accept") {
            return 5;
         } else {
            char[] var2 = var0.toCharArray();
            int var3 = var2.length - 1;
            if (var3 < 0) {
               return var1;
            } else {
               while(var3 != -1) {
                  char var4;
                  while(var3 != -1 && ((var4 = var2[var3]) == ' ' || var4 == '\r' || var4 == '\n' || var4 == '\f' || var4 == '\t')) {
                     --var3;
                  }

                  byte var5;
                  if (var3 < 6 || var2[var3 - 6] != 'c' && var2[var3 - 6] != 'C' || var2[var3 - 5] != 'o' && var2[var3 - 5] != 'O' || var2[var3 - 4] != 'n' && var2[var3 - 4] != 'N' || var2[var3 - 3] != 'n' && var2[var3 - 3] != 'N' || var2[var3 - 2] != 'e' && var2[var3 - 2] != 'E' || var2[var3 - 1] != 'c' && var2[var3 - 1] != 'C' || var2[var3] != 't' && var2[var3] != 'T') {
                     if (var3 >= 6 && (var2[var3 - 6] == 'r' || var2[var3 - 6] == 'R') && (var2[var3 - 5] == 'e' || var2[var3 - 5] == 'E') && (var2[var3 - 4] == 's' || var2[var3 - 4] == 'S') && (var2[var3 - 3] == 'o' || var2[var3 - 3] == 'O') && (var2[var3 - 2] == 'l' || var2[var3 - 2] == 'L') && (var2[var3 - 1] == 'v' || var2[var3 - 1] == 'V') && (var2[var3] == 'e' || var2[var3] == 'E')) {
                        var5 = 7;
                        var1 |= 8;
                     } else if (var3 >= 5 && (var2[var3 - 5] == 'l' || var2[var3 - 5] == 'L') && (var2[var3 - 4] == 'i' || var2[var3 - 4] == 'I') && (var2[var3 - 3] == 's' || var2[var3 - 3] == 'S') && (var2[var3 - 2] == 't' || var2[var3 - 2] == 'T') && (var2[var3 - 1] == 'e' || var2[var3 - 1] == 'E') && (var2[var3] == 'n' || var2[var3] == 'N')) {
                        var5 = 6;
                        var1 |= 2;
                     } else {
                        if (var3 < 5 || var2[var3 - 5] != 'a' && var2[var3 - 5] != 'A' || var2[var3 - 4] != 'c' && var2[var3 - 4] != 'C' || var2[var3 - 3] != 'c' && var2[var3 - 3] != 'C' || var2[var3 - 2] != 'e' && var2[var3 - 2] != 'E' || var2[var3 - 1] != 'p' && var2[var3 - 1] != 'P' || var2[var3] != 't' && var2[var3] != 'T') {
                           throw new IllegalArgumentException("invalid permission: " + var0);
                        }

                        var5 = 6;
                        var1 |= 4;
                     }
                  } else {
                     var5 = 7;
                     var1 |= 1;
                  }

                  boolean var6 = false;

                  while(var3 >= var5 && !var6) {
                     switch(var2[var3 - var5]) {
                     case ',':
                        var6 = true;
                     case '\t':
                     case '\n':
                     case '\f':
                     case '\r':
                     case ' ':
                        --var3;
                        break;
                     default:
                        throw new IllegalArgumentException("invalid permission: " + var0);
                     }
                  }

                  var3 -= var5;
               }

               return var1;
            }
         }
      }
   }

   private boolean isUntrusted() throws UnknownHostException {
      if (this.trusted) {
         return false;
      } else if (!this.invalid && !this.untrusted) {
         try {
            if (!trustNameService && (this.defaultDeny || sun.net.www.URLConnection.isProxiedHost(this.hostname))) {
               if (this.cname == null) {
                  this.getCanonName();
               }

               if (!this.match(this.cname, this.hostname) && !this.authorized(this.hostname, this.addresses[0].getAddress())) {
                  this.untrusted = true;
                  Debug var1 = getDebug();
                  if (var1 != null && Debug.isOn("failure")) {
                     var1.println("socket access restriction: proxied host (" + this.addresses[0] + ") does not match " + this.cname + " from reverse lookup");
                  }

                  return true;
               }

               this.trusted = true;
            }

            return false;
         } catch (UnknownHostException var2) {
            this.invalid = true;
            throw var2;
         }
      } else {
         return true;
      }
   }

   void getCanonName() throws UnknownHostException {
      if (this.cname == null && !this.invalid && !this.untrusted) {
         try {
            if (this.addresses == null) {
               this.getIP();
            }

            if (this.init_with_ip) {
               this.cname = this.addresses[0].getHostName(false).toLowerCase();
            } else {
               this.cname = InetAddress.getByName(this.addresses[0].getHostAddress()).getHostName(false).toLowerCase();
            }

         } catch (UnknownHostException var2) {
            this.invalid = true;
            throw var2;
         }
      }
   }

   private boolean match(String var1, String var2) {
      String var3 = var1.toLowerCase();
      String var4 = var2.toLowerCase();
      if (!var3.startsWith(var4) || var3.length() != var4.length() && var3.charAt(var4.length()) != '.') {
         if (this.cdomain == null) {
            this.cdomain = RegisteredDomain.getRegisteredDomain(var3);
         }

         if (this.hdomain == null) {
            this.hdomain = RegisteredDomain.getRegisteredDomain(var4);
         }

         return this.cdomain.length() != 0 && this.hdomain.length() != 0 && this.cdomain.equals(this.hdomain);
      } else {
         return true;
      }
   }

   private boolean authorized(String var1, byte[] var2) {
      if (var2.length == 4) {
         return this.authorizedIPv4(var1, var2);
      } else {
         return var2.length == 16 ? this.authorizedIPv6(var1, var2) : false;
      }
   }

   private boolean authorizedIPv4(String var1, byte[] var2) {
      String var3 = "";

      try {
         var3 = "auth." + (var2[3] & 255) + "." + (var2[2] & 255) + "." + (var2[1] & 255) + "." + (var2[0] & 255) + ".in-addr.arpa";
         var3 = this.hostname + '.' + var3;
         InetAddress var4 = InetAddress.getAllByName0(var3, false)[0];
         if (var4.equals(InetAddress.getByAddress(var2))) {
            return true;
         }

         Debug var5 = getDebug();
         if (var5 != null && Debug.isOn("failure")) {
            var5.println("socket access restriction: IP address of " + var4 + " != " + InetAddress.getByAddress(var2));
         }
      } catch (UnknownHostException var7) {
         Debug var6 = getDebug();
         if (var6 != null && Debug.isOn("failure")) {
            var6.println("socket access restriction: forward lookup failed for " + var3);
         }
      }

      return false;
   }

   private boolean authorizedIPv6(String var1, byte[] var2) {
      String var3 = "";

      Debug var6;
      try {
         StringBuffer var5 = new StringBuffer(39);

         for(int var8 = 15; var8 >= 0; --var8) {
            var5.append(Integer.toHexString(var2[var8] & 15));
            var5.append('.');
            var5.append(Integer.toHexString(var2[var8] >> 4 & 15));
            var5.append('.');
         }

         var3 = "auth." + var5.toString() + "IP6.ARPA";
         var3 = this.hostname + '.' + var3;
         InetAddress var4 = InetAddress.getAllByName0(var3, false)[0];
         if (var4.equals(InetAddress.getByAddress(var2))) {
            return true;
         }

         var6 = getDebug();
         if (var6 != null && Debug.isOn("failure")) {
            var6.println("socket access restriction: IP address of " + var4 + " != " + InetAddress.getByAddress(var2));
         }
      } catch (UnknownHostException var7) {
         var6 = getDebug();
         if (var6 != null && Debug.isOn("failure")) {
            var6.println("socket access restriction: forward lookup failed for " + var3);
         }
      }

      return false;
   }

   void getIP() throws UnknownHostException {
      if (this.addresses == null && !this.wildcard && !this.invalid) {
         try {
            String var1;
            if (this.getName().charAt(0) == '[') {
               var1 = this.getName().substring(1, this.getName().indexOf(93));
            } else {
               int var2 = this.getName().indexOf(":");
               if (var2 == -1) {
                  var1 = this.getName();
               } else {
                  var1 = this.getName().substring(0, var2);
               }
            }

            this.addresses = new InetAddress[]{InetAddress.getAllByName0(var1, false)[0]};
         } catch (UnknownHostException var3) {
            this.invalid = true;
            throw var3;
         } catch (IndexOutOfBoundsException var4) {
            this.invalid = true;
            throw new UnknownHostException(this.getName());
         }
      }
   }

   public boolean implies(Permission var1) {
      if (!(var1 instanceof SocketPermission)) {
         return false;
      } else if (var1 == this) {
         return true;
      } else {
         SocketPermission var4 = (SocketPermission)var1;
         return (this.mask & var4.mask) == var4.mask && this.impliesIgnoreMask(var4);
      }
   }

   boolean impliesIgnoreMask(SocketPermission var1) {
      if ((var1.mask & 8) != var1.mask && (var1.portrange[0] < this.portrange[0] || var1.portrange[1] > this.portrange[1])) {
         if (!this.includesEphemerals() && !var1.includesEphemerals()) {
            return false;
         }

         if (!inRange(this.portrange[0], this.portrange[1], var1.portrange[0], var1.portrange[1])) {
            return false;
         }
      }

      if (this.wildcard && "".equals(this.cname)) {
         return true;
      } else if (!this.invalid && !var1.invalid) {
         try {
            int var2;
            if (this.init_with_ip) {
               if (var1.wildcard) {
                  return false;
               } else if (var1.init_with_ip) {
                  return this.addresses[0].equals(var1.addresses[0]);
               } else {
                  if (var1.addresses == null) {
                     var1.getIP();
                  }

                  for(var2 = 0; var2 < var1.addresses.length; ++var2) {
                     if (this.addresses[0].equals(var1.addresses[var2])) {
                        return true;
                     }
                  }

                  return false;
               }
            } else if (!this.wildcard && !var1.wildcard) {
               if (this.addresses == null) {
                  this.getIP();
               }

               if (var1.addresses == null) {
                  var1.getIP();
               }

               if (var1.init_with_ip && this.isUntrusted()) {
                  return false;
               } else {
                  for(int var3 = 0; var3 < this.addresses.length; ++var3) {
                     for(var2 = 0; var2 < var1.addresses.length; ++var2) {
                        if (this.addresses[var3].equals(var1.addresses[var2])) {
                           return true;
                        }
                     }
                  }

                  if (this.cname == null) {
                     this.getCanonName();
                  }

                  if (var1.cname == null) {
                     var1.getCanonName();
                  }

                  return this.cname.equalsIgnoreCase(var1.cname);
               }
            } else if (this.wildcard && var1.wildcard) {
               return var1.cname.endsWith(this.cname);
            } else if (var1.wildcard) {
               return false;
            } else {
               if (var1.cname == null) {
                  var1.getCanonName();
               }

               return var1.cname.endsWith(this.cname);
            }
         } catch (UnknownHostException var5) {
            return this.compareHostnames(var1);
         }
      } else {
         return this.compareHostnames(var1);
      }
   }

   private boolean compareHostnames(SocketPermission var1) {
      String var2 = this.hostname;
      String var3 = var1.hostname;
      if (var2 == null) {
         return false;
      } else if (this.wildcard) {
         int var4 = this.cname.length();
         return var3.regionMatches(true, var3.length() - var4, this.cname, 0, var4);
      } else {
         return var2.equalsIgnoreCase(var3);
      }
   }

   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof SocketPermission)) {
         return false;
      } else {
         SocketPermission var2 = (SocketPermission)var1;
         if (this.mask != var2.mask) {
            return false;
         } else if ((var2.mask & 8) != var2.mask && (this.portrange[0] != var2.portrange[0] || this.portrange[1] != var2.portrange[1])) {
            return false;
         } else if (this.getName().equalsIgnoreCase(var2.getName())) {
            return true;
         } else {
            try {
               this.getCanonName();
               var2.getCanonName();
            } catch (UnknownHostException var4) {
               return false;
            }

            if (!this.invalid && !var2.invalid) {
               return this.cname != null ? this.cname.equalsIgnoreCase(var2.cname) : false;
            } else {
               return false;
            }
         }
      }
   }

   public int hashCode() {
      if (!this.init_with_ip && !this.wildcard) {
         try {
            this.getCanonName();
         } catch (UnknownHostException var2) {
         }

         return !this.invalid && this.cname != null ? this.cname.hashCode() : this.getName().hashCode();
      } else {
         return this.getName().hashCode();
      }
   }

   int getMask() {
      return this.mask;
   }

   private static String getActions(int var0) {
      StringBuilder var1 = new StringBuilder();
      boolean var2 = false;
      if ((var0 & 1) == 1) {
         var2 = true;
         var1.append("connect");
      }

      if ((var0 & 2) == 2) {
         if (var2) {
            var1.append(',');
         } else {
            var2 = true;
         }

         var1.append("listen");
      }

      if ((var0 & 4) == 4) {
         if (var2) {
            var1.append(',');
         } else {
            var2 = true;
         }

         var1.append("accept");
      }

      if ((var0 & 8) == 8) {
         if (var2) {
            var1.append(',');
         } else {
            var2 = true;
         }

         var1.append("resolve");
      }

      return var1.toString();
   }

   public String getActions() {
      if (this.actions == null) {
         this.actions = getActions(this.mask);
      }

      return this.actions;
   }

   public PermissionCollection newPermissionCollection() {
      return new SocketPermissionCollection();
   }

   private synchronized void writeObject(ObjectOutputStream var1) throws IOException {
      if (this.actions == null) {
         this.getActions();
      }

      var1.defaultWriteObject();
   }

   private synchronized void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      this.init(this.getName(), getMask(this.actions));
   }

   private static int initEphemeralPorts(final String var0, int var1) {
      return (Integer)AccessController.doPrivileged(new PrivilegedAction<Integer>() {
         public Integer run() {
            int var1 = Integer.getInteger("jdk.net.ephemeralPortRange." + var0, -1);
            return var1 != -1 ? var1 : var0.equals("low") ? PortConfig.getLower() : PortConfig.getUpper();
         }
      });
   }

   private static boolean inRange(int var0, int var1, int var2, int var3) {
      int var4 = SocketPermission.EphemeralRange.low;
      int var5 = SocketPermission.EphemeralRange.high;
      if (var2 == 0) {
         if (!inRange(var0, var1, var4, var5)) {
            return false;
         }

         if (var3 == 0) {
            return true;
         }

         var2 = 1;
      }

      if (var0 == 0 && var1 == 0) {
         return var2 >= var4 && var3 <= var5;
      } else if (var0 != 0) {
         return var2 >= var0 && var3 <= var1;
      } else if (var1 >= var4 - 1) {
         return var3 <= var5;
      } else {
         return var2 <= var1 && var3 <= var1 || var2 >= var4 && var3 <= var5;
      }
   }

   static {
      Boolean var0 = (Boolean)AccessController.doPrivileged((PrivilegedAction)(new GetBooleanAction("sun.net.trustNameService")));
      trustNameService = var0;
   }

   private static class EphemeralRange {
      static final int low = SocketPermission.initEphemeralPorts("low", 49152);
      static final int high = SocketPermission.initEphemeralPorts("high", 65535);
   }
}
