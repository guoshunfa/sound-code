package sun.net.dns;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

public class ResolverConfigurationImpl extends ResolverConfiguration {
   private static Object lock = new Object();
   private static long lastRefresh = -1L;
   private static final int TIMEOUT = 300000;
   private final ResolverConfiguration.Options opts = new OptionsImpl();
   private LinkedList<String> searchlist;
   private LinkedList<String> nameservers;

   private LinkedList<String> resolvconf(String var1, int var2, int var3) {
      LinkedList var4 = new LinkedList();

      try {
         BufferedReader var5 = new BufferedReader(new FileReader("/etc/resolv.conf"));

         label89:
         do {
            int var7;
            String var8;
            do {
               do {
                  String var6;
                  do {
                     do {
                        do {
                           do {
                              if ((var6 = var5.readLine()) == null) {
                                 break label89;
                              }

                              var7 = var2;
                           } while(var6.length() == 0);
                        } while(var6.charAt(0) == '#');
                     } while(var6.charAt(0) == ';');
                  } while(!var6.startsWith(var1));

                  var8 = var6.substring(var1.length());
               } while(var8.length() == 0);
            } while(var8.charAt(0) != ' ' && var8.charAt(0) != '\t');

            StringTokenizer var9 = new StringTokenizer(var8, " \t");

            while(var9.hasMoreTokens()) {
               String var10 = var9.nextToken();
               if (var10.charAt(0) == '#' || var10.charAt(0) == ';') {
                  break;
               }

               if ("nameserver".equals(var1) && var10.indexOf(58) >= 0 && var10.indexOf(46) < 0 && var10.indexOf(91) < 0 && var10.indexOf(93) < 0) {
                  var10 = "[" + var10 + "]";
               }

               var4.add(var10);
               --var7;
               if (var7 == 0) {
                  break;
               }
            }

            --var3;
         } while(var3 != 0);

         var5.close();
      } catch (IOException var11) {
      }

      return var4;
   }

   private void loadConfig() {
      assert Thread.holdsLock(lock);

      if (lastRefresh >= 0L) {
         long var1 = System.currentTimeMillis();
         if (var1 - lastRefresh < 300000L) {
            return;
         }
      }

      this.nameservers = (LinkedList)AccessController.doPrivileged(new PrivilegedAction<LinkedList<String>>() {
         public LinkedList<String> run() {
            return ResolverConfigurationImpl.this.resolvconf("nameserver", 1, 5);
         }
      });
      this.searchlist = this.getSearchList();
      lastRefresh = System.currentTimeMillis();
   }

   private LinkedList<String> getSearchList() {
      LinkedList var1 = (LinkedList)AccessController.doPrivileged(new PrivilegedAction<LinkedList<String>>() {
         public LinkedList<String> run() {
            LinkedList var1 = ResolverConfigurationImpl.this.resolvconf("search", 6, 1);
            return var1.size() > 0 ? var1 : null;
         }
      });
      if (var1 != null) {
         return var1;
      } else {
         String var2 = localDomain0();
         if (var2 != null && var2.length() > 0) {
            var1 = new LinkedList();
            var1.add(var2);
            return var1;
         } else {
            var1 = (LinkedList)AccessController.doPrivileged(new PrivilegedAction<LinkedList<String>>() {
               public LinkedList<String> run() {
                  LinkedList var1 = ResolverConfigurationImpl.this.resolvconf("domain", 1, 1);
                  return var1.size() > 0 ? var1 : null;
               }
            });
            if (var1 != null) {
               return var1;
            } else {
               var1 = new LinkedList();
               String var3 = fallbackDomain0();
               if (var3 != null && var3.length() > 0) {
                  var1.add(var3);
               }

               return var1;
            }
         }
      }
   }

   ResolverConfigurationImpl() {
   }

   public List<String> searchlist() {
      synchronized(lock) {
         this.loadConfig();
         return (List)this.searchlist.clone();
      }
   }

   public List<String> nameservers() {
      synchronized(lock) {
         this.loadConfig();
         return (List)this.nameservers.clone();
      }
   }

   public ResolverConfiguration.Options options() {
      return this.opts;
   }

   static native String localDomain0();

   static native String fallbackDomain0();

   static {
      AccessController.doPrivileged(new PrivilegedAction<Void>() {
         public Void run() {
            System.loadLibrary("net");
            return null;
         }
      });
   }
}
