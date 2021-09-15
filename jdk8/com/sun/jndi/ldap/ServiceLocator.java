package com.sun.jndi.ldap;

import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;
import javax.naming.Context;
import javax.naming.InvalidNameException;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.ldap.Rdn;
import javax.naming.spi.NamingManager;

class ServiceLocator {
   private static final String SRV_RR = "SRV";
   private static final String[] SRV_RR_ATTR = new String[]{"SRV"};
   private static final Random random = new Random();

   private ServiceLocator() {
   }

   static String mapDnToDomainName(String var0) throws InvalidNameException {
      if (var0 == null) {
         return null;
      } else {
         StringBuffer var1 = new StringBuffer();
         javax.naming.ldap.LdapName var2 = new javax.naming.ldap.LdapName(var0);
         List var3 = var2.getRdns();

         for(int var4 = var3.size() - 1; var4 >= 0; --var4) {
            Rdn var5 = (Rdn)var3.get(var4);
            if (var5.size() == 1 && "dc".equalsIgnoreCase(var5.getType())) {
               Object var6 = var5.getValue();
               if (!(var6 instanceof String)) {
                  var1.setLength(0);
               } else {
                  if (var6.equals(".") || var1.length() == 1 && var1.charAt(0) == '.') {
                     var1.setLength(0);
                  }

                  if (var1.length() > 0) {
                     var1.append('.');
                  }

                  var1.append(var6);
               }
            } else {
               var1.setLength(0);
            }
         }

         return var1.length() != 0 ? var1.toString() : null;
      }
   }

   static String[] getLdapService(String var0, Hashtable<?, ?> var1) {
      if (var0 != null && var0.length() != 0) {
         String var2 = "dns:///_ldap._tcp." + var0;
         String[] var3 = null;

         try {
            Context var4 = NamingManager.getURLContext("dns", var1);
            if (!(var4 instanceof DirContext)) {
               return null;
            }

            Attributes var5 = ((DirContext)var4).getAttributes(var2, SRV_RR_ATTR);
            Attribute var6;
            if (var5 != null && (var6 = var5.get("SRV")) != null) {
               int var7 = var6.size();
               boolean var8 = false;
               ServiceLocator.SrvRecord[] var9 = new ServiceLocator.SrvRecord[var7];
               int var10 = 0;

               int var11;
               for(var11 = 0; var10 < var7; ++var10) {
                  try {
                     var9[var11] = new ServiceLocator.SrvRecord((String)var6.get(var10));
                     ++var11;
                  } catch (Exception var13) {
                  }
               }

               if (var11 < var7) {
                  ServiceLocator.SrvRecord[] var12 = new ServiceLocator.SrvRecord[var11];
                  System.arraycopy(var9, 0, var12, 0, var11);
                  var9 = var12;
               }

               if (var11 > 1) {
                  Arrays.sort((Object[])var9);
               }

               var3 = extractHostports(var9);
            }
         } catch (NamingException var14) {
         }

         return var3;
      } else {
         return null;
      }
   }

   private static String[] extractHostports(ServiceLocator.SrvRecord[] var0) {
      String[] var1 = null;
      boolean var2 = false;
      boolean var3 = false;
      boolean var4 = false;
      int var5 = 0;

      for(int var6 = 0; var6 < var0.length; ++var6) {
         if (var1 == null) {
            var1 = new String[var0.length];
         }

         int var8;
         for(var8 = var6; var6 < var0.length - 1 && var0[var6].priority == var0[var6 + 1].priority; ++var6) {
         }

         int var9 = var6;
         int var10 = var6 - var8 + 1;

         for(int var7 = 0; var7 < var10; ++var7) {
            var1[var5++] = selectHostport(var0, var8, var9);
         }
      }

      return var1;
   }

   private static String selectHostport(ServiceLocator.SrvRecord[] var0, int var1, int var2) {
      if (var1 == var2) {
         return var0[var1].hostport;
      } else {
         int var3 = 0;

         for(int var4 = var1; var4 <= var2; ++var4) {
            if (var0[var4] != null) {
               var3 += var0[var4].weight;
               var0[var4].sum = var3;
            }
         }

         String var7 = null;
         int var5 = var3 == 0 ? 0 : random.nextInt(var3 + 1);

         for(int var6 = var1; var6 <= var2; ++var6) {
            if (var0[var6] != null && var0[var6].sum >= var5) {
               var7 = var0[var6].hostport;
               var0[var6] = null;
               break;
            }
         }

         return var7;
      }
   }

   static class SrvRecord implements Comparable<ServiceLocator.SrvRecord> {
      int priority;
      int weight;
      int sum;
      String hostport;

      SrvRecord(String var1) throws Exception {
         StringTokenizer var2 = new StringTokenizer(var1, " ");
         if (var2.countTokens() == 4) {
            this.priority = Integer.parseInt(var2.nextToken());
            this.weight = Integer.parseInt(var2.nextToken());
            String var3 = var2.nextToken();
            this.hostport = var2.nextToken() + ":" + var3;
         } else {
            throw new IllegalArgumentException();
         }
      }

      public int compareTo(ServiceLocator.SrvRecord var1) {
         if (this.priority > var1.priority) {
            return 1;
         } else if (this.priority < var1.priority) {
            return -1;
         } else if (this.weight == 0 && var1.weight != 0) {
            return -1;
         } else {
            return this.weight != 0 && var1.weight == 0 ? 1 : 0;
         }
      }
   }
}
