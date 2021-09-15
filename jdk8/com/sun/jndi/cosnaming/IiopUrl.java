package com.sun.jndi.cosnaming;

import com.sun.jndi.toolkit.url.UrlUtil;
import java.net.MalformedURLException;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.naming.Name;
import javax.naming.NamingException;

public final class IiopUrl {
   private static final int DEFAULT_IIOPNAME_PORT = 9999;
   private static final int DEFAULT_IIOP_PORT = 900;
   private static final String DEFAULT_HOST = "localhost";
   private Vector<IiopUrl.Address> addresses;
   private String stringName;

   public Vector<IiopUrl.Address> getAddresses() {
      return this.addresses;
   }

   public String getStringName() {
      return this.stringName;
   }

   public Name getCosName() throws NamingException {
      return CNCtx.parser.parse(this.stringName);
   }

   public IiopUrl(String var1) throws MalformedURLException {
      byte var2;
      boolean var3;
      if (var1.startsWith("iiopname://")) {
         var3 = false;
         var2 = 11;
      } else {
         if (!var1.startsWith("iiop://")) {
            throw new MalformedURLException("Invalid iiop/iiopname URL: " + var1);
         }

         var3 = true;
         var2 = 7;
      }

      int var4 = var1.indexOf(47, var2);
      if (var4 < 0) {
         var4 = var1.length();
         this.stringName = "";
      } else {
         this.stringName = UrlUtil.decode(var1.substring(var4 + 1));
      }

      this.addresses = new Vector(3);
      if (var3) {
         this.addresses.addElement(new IiopUrl.Address(var1.substring(var2, var4), var3));
      } else {
         StringTokenizer var5 = new StringTokenizer(var1.substring(var2, var4), ",");

         while(var5.hasMoreTokens()) {
            this.addresses.addElement(new IiopUrl.Address(var5.nextToken(), var3));
         }

         if (this.addresses.size() == 0) {
            this.addresses.addElement(new IiopUrl.Address("", var3));
         }
      }

   }

   public static class Address {
      public int port = -1;
      public int major;
      public int minor;
      public String host;

      public Address(String var1, boolean var2) throws MalformedURLException {
         int var3;
         int var4;
         int var5;
         if (!var2 && (var4 = var1.indexOf(64)) >= 0) {
            var5 = var1.indexOf(46);
            if (var5 < 0) {
               throw new MalformedURLException("invalid version: " + var1);
            }

            try {
               this.major = Integer.parseInt(var1.substring(0, var5));
               this.minor = Integer.parseInt(var1.substring(var5 + 1, var4));
            } catch (NumberFormatException var8) {
               throw new MalformedURLException("Nonnumeric version: " + var1);
            }

            var3 = var4 + 1;
         } else {
            this.major = 1;
            this.minor = 0;
            var3 = 0;
         }

         var5 = var1.indexOf(47, var3);
         if (var5 < 0) {
            var5 = var1.length();
         }

         int var6;
         if (var1.startsWith("[", var3)) {
            var6 = var1.indexOf(93, var3 + 1);
            if (var6 < 0 || var6 > var5) {
               throw new IllegalArgumentException("IiopURL: name is an Invalid URL: " + var1);
            }

            this.host = var1.substring(var3, var6 + 1);
            var3 = var6 + 1;
         } else {
            var6 = var1.indexOf(58, var3);
            int var7 = var6 >= 0 && var6 <= var5 ? var6 : var5;
            if (var3 < var7) {
               this.host = var1.substring(var3, var7);
            }

            var3 = var7;
         }

         if (var3 + 1 < var5) {
            if (!var1.startsWith(":", var3)) {
               throw new IllegalArgumentException("IiopURL: name is an Invalid URL: " + var1);
            }

            ++var3;
            this.port = Integer.parseInt(var1.substring(var3, var5));
         }

         if ("".equals(this.host) || this.host == null) {
            this.host = "localhost";
         }

         if (this.port == -1) {
            this.port = var2 ? 900 : 9999;
         }

      }
   }
}
