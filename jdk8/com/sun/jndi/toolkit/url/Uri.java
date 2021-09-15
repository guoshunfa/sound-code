package com.sun.jndi.toolkit.url;

import java.net.MalformedURLException;

public class Uri {
   protected String uri;
   protected String scheme;
   protected String host = null;
   protected int port = -1;
   protected boolean hasAuthority;
   protected String path;
   protected String query = null;

   public Uri(String var1) throws MalformedURLException {
      this.init(var1);
   }

   protected Uri() {
   }

   protected void init(String var1) throws MalformedURLException {
      this.uri = var1;
      this.parse(var1);
   }

   public String getScheme() {
      return this.scheme;
   }

   public String getHost() {
      return this.host;
   }

   public int getPort() {
      return this.port;
   }

   public String getPath() {
      return this.path;
   }

   public String getQuery() {
      return this.query;
   }

   public String toString() {
      return this.uri;
   }

   private void parse(String var1) throws MalformedURLException {
      int var2 = var1.indexOf(58);
      if (var2 < 0) {
         throw new MalformedURLException("Invalid URI: " + var1);
      } else {
         this.scheme = var1.substring(0, var2);
         ++var2;
         this.hasAuthority = var1.startsWith("//", var2);
         int var3;
         if (this.hasAuthority) {
            var2 += 2;
            var3 = var1.indexOf(47, var2);
            if (var3 < 0) {
               var3 = var1.length();
            }

            int var4;
            if (var1.startsWith("[", var2)) {
               var4 = var1.indexOf(93, var2 + 1);
               if (var4 < 0 || var4 > var3) {
                  throw new MalformedURLException("Invalid URI: " + var1);
               }

               this.host = var1.substring(var2, var4 + 1);
               var2 = var4 + 1;
            } else {
               var4 = var1.indexOf(58, var2);
               int var5 = var4 >= 0 && var4 <= var3 ? var4 : var3;
               if (var2 < var5) {
                  this.host = var1.substring(var2, var5);
               }

               var2 = var5;
            }

            if (var2 + 1 < var3 && var1.startsWith(":", var2)) {
               ++var2;
               this.port = Integer.parseInt(var1.substring(var2, var3));
            }

            var2 = var3;
         }

         var3 = var1.indexOf(63, var2);
         if (var3 < 0) {
            this.path = var1.substring(var2);
         } else {
            this.path = var1.substring(var2, var3);
            this.query = var1.substring(var3);
         }

      }
   }
}
