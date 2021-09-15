package sun.net.spi;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URI;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.regex.Pattern;
import sun.net.NetProperties;
import sun.net.SocksProxy;

public class DefaultProxySelector extends ProxySelector {
   static final String[][] props = new String[][]{{"http", "http.proxy", "proxy", "socksProxy"}, {"https", "https.proxy", "proxy", "socksProxy"}, {"ftp", "ftp.proxy", "ftpProxy", "proxy", "socksProxy"}, {"gopher", "gopherProxy", "socksProxy"}, {"socket", "socksProxy"}};
   private static final String SOCKS_PROXY_VERSION = "socksProxyVersion";
   private static boolean hasSystemProxies = false;

   public List<Proxy> select(URI var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("URI can't be null.");
      } else {
         final String var2 = var1.getScheme();
         String var3 = var1.getHost();
         if (var3 == null) {
            String var4 = var1.getAuthority();
            if (var4 != null) {
               int var5 = var4.indexOf(64);
               if (var5 >= 0) {
                  var4 = var4.substring(var5 + 1);
               }

               var5 = var4.lastIndexOf(58);
               if (var5 >= 0) {
                  var4 = var4.substring(0, var5);
               }

               var3 = var4;
            }
         }

         if (var2 != null && var3 != null) {
            ArrayList var10 = new ArrayList(1);
            final DefaultProxySelector.NonProxyInfo var11 = null;
            if ("http".equalsIgnoreCase(var2)) {
               var11 = DefaultProxySelector.NonProxyInfo.httpNonProxyInfo;
            } else if ("https".equalsIgnoreCase(var2)) {
               var11 = DefaultProxySelector.NonProxyInfo.httpNonProxyInfo;
            } else if ("ftp".equalsIgnoreCase(var2)) {
               var11 = DefaultProxySelector.NonProxyInfo.ftpNonProxyInfo;
            } else if ("socket".equalsIgnoreCase(var2)) {
               var11 = DefaultProxySelector.NonProxyInfo.socksNonProxyInfo;
            }

            final String var8 = var3.toLowerCase();
            Proxy var9 = (Proxy)AccessController.doPrivileged(new PrivilegedAction<Proxy>() {
               public Proxy run() {
                  String var3 = null;
                  boolean var4 = false;
                  String var5 = null;
                  InetSocketAddress var6 = null;

                  for(int var1 = 0; var1 < DefaultProxySelector.props.length; ++var1) {
                     if (DefaultProxySelector.props[var1][0].equalsIgnoreCase(var2)) {
                        int var2x;
                        for(var2x = 1; var2x < DefaultProxySelector.props[var1].length; ++var2x) {
                           var3 = NetProperties.get(DefaultProxySelector.props[var1][var2x] + "Host");
                           if (var3 != null && var3.length() != 0) {
                              break;
                           }
                        }

                        if (var3 != null && var3.length() != 0) {
                           if (var11 != null) {
                              var5 = NetProperties.get(var11.property);
                              synchronized(var11) {
                                 if (var5 == null) {
                                    if (var11.defaultVal != null) {
                                       var5 = var11.defaultVal;
                                    } else {
                                       var11.hostsSource = null;
                                       var11.pattern = null;
                                    }
                                 } else if (var5.length() != 0) {
                                    var5 = var5 + "|localhost|127.*|[::1]|0.0.0.0|[::0]";
                                 }

                                 if (var5 != null && !var5.equals(var11.hostsSource)) {
                                    var11.pattern = DefaultProxySelector.toPattern(var5);
                                    var11.hostsSource = var5;
                                 }

                                 if (DefaultProxySelector.shouldNotUseProxyFor(var11.pattern, var8)) {
                                    return Proxy.NO_PROXY;
                                 }
                              }
                           }

                           int var11x = NetProperties.getInteger(DefaultProxySelector.props[var1][var2x] + "Port", 0);
                           int var12;
                           if (var11x == 0 && var2x < DefaultProxySelector.props[var1].length - 1) {
                              for(var12 = 1; var12 < DefaultProxySelector.props[var1].length - 1; ++var12) {
                                 if (var12 != var2x && var11x == 0) {
                                    var11x = NetProperties.getInteger(DefaultProxySelector.props[var1][var12] + "Port", 0);
                                 }
                              }
                           }

                           if (var11x == 0) {
                              if (var2x == DefaultProxySelector.props[var1].length - 1) {
                                 var11x = DefaultProxySelector.this.defaultPort("socket");
                              } else {
                                 var11x = DefaultProxySelector.this.defaultPort(var2);
                              }
                           }

                           var6 = InetSocketAddress.createUnresolved(var3, var11x);
                           if (var2x == DefaultProxySelector.props[var1].length - 1) {
                              var12 = NetProperties.getInteger("socksProxyVersion", 5);
                              return SocksProxy.create(var6, var12);
                           }

                           return new Proxy(Proxy.Type.HTTP, var6);
                        }

                        if (DefaultProxySelector.hasSystemProxies) {
                           String var7;
                           if (var2.equalsIgnoreCase("socket")) {
                              var7 = "socks";
                           } else {
                              var7 = var2;
                           }

                           Proxy var8x = DefaultProxySelector.this.getSystemProxy(var7, var8);
                           if (var8x != null) {
                              return var8x;
                           }
                        }

                        return Proxy.NO_PROXY;
                     }
                  }

                  return Proxy.NO_PROXY;
               }
            });
            var10.add(var9);
            return var10;
         } else {
            throw new IllegalArgumentException("protocol = " + var2 + " host = " + var3);
         }
      }
   }

   public void connectFailed(URI var1, SocketAddress var2, IOException var3) {
      if (var1 == null || var2 == null || var3 == null) {
         throw new IllegalArgumentException("Arguments can't be null.");
      }
   }

   private int defaultPort(String var1) {
      if ("http".equalsIgnoreCase(var1)) {
         return 80;
      } else if ("https".equalsIgnoreCase(var1)) {
         return 443;
      } else if ("ftp".equalsIgnoreCase(var1)) {
         return 80;
      } else if ("socket".equalsIgnoreCase(var1)) {
         return 1080;
      } else {
         return "gopher".equalsIgnoreCase(var1) ? 80 : -1;
      }
   }

   private static native boolean init();

   private synchronized native Proxy getSystemProxy(String var1, String var2);

   static boolean shouldNotUseProxyFor(Pattern var0, String var1) {
      if (var0 != null && !var1.isEmpty()) {
         boolean var2 = var0.matcher(var1).matches();
         return var2;
      } else {
         return false;
      }
   }

   static Pattern toPattern(String var0) {
      boolean var1 = true;
      StringJoiner var2 = new StringJoiner("|");
      String[] var3 = var0.split("\\|");
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         String var6 = var3[var5];
         if (!var6.isEmpty()) {
            var1 = false;
            String var7 = disjunctToRegex(var6.toLowerCase());
            var2.add(var7);
         }
      }

      return var1 ? null : Pattern.compile(var2.toString());
   }

   static String disjunctToRegex(String var0) {
      String var1;
      if (var0.startsWith("*")) {
         var1 = ".*" + Pattern.quote(var0.substring(1));
      } else if (var0.endsWith("*")) {
         var1 = Pattern.quote(var0.substring(0, var0.length() - 1)) + ".*";
      } else {
         var1 = Pattern.quote(var0);
      }

      return var1;
   }

   static {
      Boolean var1 = (Boolean)AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
         public Boolean run() {
            return NetProperties.getBoolean("java.net.useSystemProxies");
         }
      });
      if (var1 != null && var1) {
         AccessController.doPrivileged(new PrivilegedAction<Void>() {
            public Void run() {
               System.loadLibrary("net");
               return null;
            }
         });
         hasSystemProxies = init();
      }

   }

   static class NonProxyInfo {
      static final String defStringVal = "localhost|127.*|[::1]|0.0.0.0|[::0]";
      String hostsSource;
      Pattern pattern;
      final String property;
      final String defaultVal;
      static DefaultProxySelector.NonProxyInfo ftpNonProxyInfo = new DefaultProxySelector.NonProxyInfo("ftp.nonProxyHosts", (String)null, (Pattern)null, "localhost|127.*|[::1]|0.0.0.0|[::0]");
      static DefaultProxySelector.NonProxyInfo httpNonProxyInfo = new DefaultProxySelector.NonProxyInfo("http.nonProxyHosts", (String)null, (Pattern)null, "localhost|127.*|[::1]|0.0.0.0|[::0]");
      static DefaultProxySelector.NonProxyInfo socksNonProxyInfo = new DefaultProxySelector.NonProxyInfo("socksNonProxyHosts", (String)null, (Pattern)null, "localhost|127.*|[::1]|0.0.0.0|[::0]");

      NonProxyInfo(String var1, String var2, Pattern var3, String var4) {
         this.property = var1;
         this.hostsSource = var2;
         this.pattern = var3;
         this.defaultVal = var4;
      }
   }
}
