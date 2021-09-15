package com.sun.jndi.dns;

import com.sun.jndi.toolkit.url.UrlUtil;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import javax.naming.ConfigurationException;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.spi.InitialContextFactory;
import sun.net.dns.ResolverConfiguration;

public class DnsContextFactory implements InitialContextFactory {
   private static final String DEFAULT_URL = "dns:";
   private static final int DEFAULT_PORT = 53;

   public Context getInitialContext(Hashtable<?, ?> var1) throws NamingException {
      if (var1 == null) {
         var1 = new Hashtable(5);
      }

      return urlToContext(getInitCtxUrl(var1), var1);
   }

   public static DnsContext getContext(String var0, String[] var1, Hashtable<?, ?> var2) throws NamingException {
      return new DnsContext(var0, var1, var2);
   }

   public static DnsContext getContext(String var0, DnsUrl[] var1, Hashtable<?, ?> var2) throws NamingException {
      String[] var3 = serversForUrls(var1);
      DnsContext var4 = getContext(var0, var3, var2);
      if (platformServersUsed(var1)) {
         var4.setProviderUrl(constructProviderUrl(var0, var3));
      }

      return var4;
   }

   public static boolean platformServersAvailable() {
      return !filterNameServers(ResolverConfiguration.open().nameservers(), true).isEmpty();
   }

   private static Context urlToContext(String var0, Hashtable<?, ?> var1) throws NamingException {
      DnsUrl[] var2;
      try {
         var2 = DnsUrl.fromList(var0);
      } catch (MalformedURLException var5) {
         throw new ConfigurationException(var5.getMessage());
      }

      if (var2.length == 0) {
         throw new ConfigurationException("Invalid DNS pseudo-URL(s): " + var0);
      } else {
         String var3 = var2[0].getDomain();

         for(int var4 = 1; var4 < var2.length; ++var4) {
            if (!var3.equalsIgnoreCase(var2[var4].getDomain())) {
               throw new ConfigurationException("Conflicting domains: " + var0);
            }
         }

         return getContext(var3, var2, var1);
      }
   }

   private static String[] serversForUrls(DnsUrl[] var0) throws NamingException {
      if (var0.length == 0) {
         throw new ConfigurationException("DNS pseudo-URL required");
      } else {
         ArrayList var1 = new ArrayList();

         for(int var2 = 0; var2 < var0.length; ++var2) {
            String var3 = var0[var2].getHost();
            int var4 = var0[var2].getPort();
            if (var3 == null && var4 < 0) {
               List var5 = filterNameServers(ResolverConfiguration.open().nameservers(), false);
               if (!var5.isEmpty()) {
                  var1.addAll(var5);
                  continue;
               }
            }

            if (var3 == null) {
               var3 = "localhost";
            }

            var1.add(var4 < 0 ? var3 : var3 + ":" + var4);
         }

         return (String[])var1.toArray(new String[var1.size()]);
      }
   }

   private static boolean platformServersUsed(DnsUrl[] var0) {
      if (!platformServersAvailable()) {
         return false;
      } else {
         for(int var1 = 0; var1 < var0.length; ++var1) {
            if (var0[var1].getHost() == null && var0[var1].getPort() < 0) {
               return true;
            }
         }

         return false;
      }
   }

   private static String constructProviderUrl(String var0, String[] var1) {
      String var2 = "";
      if (!var0.equals(".")) {
         try {
            var2 = "/" + UrlUtil.encode(var0, "ISO-8859-1");
         } catch (UnsupportedEncodingException var5) {
         }
      }

      StringBuffer var3 = new StringBuffer();

      for(int var4 = 0; var4 < var1.length; ++var4) {
         if (var4 > 0) {
            var3.append(' ');
         }

         var3.append("dns://").append(var1[var4]).append(var2);
      }

      return var3.toString();
   }

   private static String getInitCtxUrl(Hashtable<?, ?> var0) {
      String var1 = (String)var0.get("java.naming.provider.url");
      return var1 != null ? var1 : "dns:";
   }

   private static List<String> filterNameServers(List<String> var0, boolean var1) {
      SecurityManager var2 = System.getSecurityManager();
      if (var2 != null && var0 != null && !var0.isEmpty()) {
         ArrayList var3 = new ArrayList();
         Iterator var4 = var0.iterator();

         while(var4.hasNext()) {
            String var5 = (String)var4.next();
            int var6 = var5.indexOf(58, var5.indexOf(93) + 1);
            int var7 = var6 < 0 ? 53 : Integer.parseInt(var5.substring(var6 + 1));
            String var8 = var6 < 0 ? var5 : var5.substring(0, var6);

            try {
               var2.checkConnect(var8, var7);
               var3.add(var5);
               if (var1) {
                  return var3;
               }
            } catch (SecurityException var10) {
            }
         }

         return var3;
      } else {
         return var0;
      }
   }
}
