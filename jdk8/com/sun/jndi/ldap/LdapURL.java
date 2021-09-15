package com.sun.jndi.ldap;

import com.sun.jndi.toolkit.url.Uri;
import com.sun.jndi.toolkit.url.UrlUtil;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.StringTokenizer;
import javax.naming.NamingException;

public final class LdapURL extends Uri {
   private boolean useSsl = false;
   private String DN = null;
   private String attributes = null;
   private String scope = null;
   private String filter = null;
   private String extensions = null;

   public LdapURL(String var1) throws NamingException {
      NamingException var3;
      try {
         this.init(var1);
         this.useSsl = this.scheme.equalsIgnoreCase("ldaps");
         if (!this.scheme.equalsIgnoreCase("ldap") && !this.useSsl) {
            throw new MalformedURLException("Not an LDAP URL: " + var1);
         } else {
            this.parsePathAndQuery();
         }
      } catch (MalformedURLException var4) {
         var3 = new NamingException("Cannot parse url: " + var1);
         var3.setRootCause(var4);
         throw var3;
      } catch (UnsupportedEncodingException var5) {
         var3 = new NamingException("Cannot parse url: " + var1);
         var3.setRootCause(var5);
         throw var3;
      }
   }

   public boolean useSsl() {
      return this.useSsl;
   }

   public String getDN() {
      return this.DN;
   }

   public String getAttributes() {
      return this.attributes;
   }

   public String getScope() {
      return this.scope;
   }

   public String getFilter() {
      return this.filter;
   }

   public String getExtensions() {
      return this.extensions;
   }

   public static String[] fromList(String var0) throws NamingException {
      String[] var1 = new String[(var0.length() + 1) / 2];
      int var2 = 0;

      for(StringTokenizer var3 = new StringTokenizer(var0, " "); var3.hasMoreTokens(); var1[var2++] = var3.nextToken()) {
      }

      String[] var4 = new String[var2];
      System.arraycopy(var1, 0, var4, 0, var2);
      return var4;
   }

   public static boolean hasQueryComponents(String var0) {
      return var0.lastIndexOf(63) != -1;
   }

   static String toUrlString(String var0, int var1, String var2, boolean var3) {
      try {
         String var4 = var0 != null ? var0 : "";
         if (var4.indexOf(58) != -1 && var4.charAt(0) != '[') {
            var4 = "[" + var4 + "]";
         }

         String var5 = var1 != -1 ? ":" + var1 : "";
         String var6 = var2 != null ? "/" + UrlUtil.encode(var2, "UTF8") : "";
         return var3 ? "ldaps://" + var4 + var5 + var6 : "ldap://" + var4 + var5 + var6;
      } catch (UnsupportedEncodingException var7) {
         throw new IllegalStateException("UTF-8 encoding unavailable");
      }
   }

   private void parsePathAndQuery() throws MalformedURLException, UnsupportedEncodingException {
      if (!this.path.equals("")) {
         this.DN = this.path.startsWith("/") ? this.path.substring(1) : this.path;
         if (this.DN.length() > 0) {
            this.DN = UrlUtil.decode(this.DN, "UTF8");
         }

         if (this.query != null && this.query.length() >= 2) {
            byte var1 = 1;
            int var2 = this.query.indexOf(63, var1);
            int var3 = var2 == -1 ? this.query.length() : var2;
            if (var3 - var1 > 0) {
               this.attributes = this.query.substring(var1, var3);
            }

            int var4 = var3 + 1;
            if (var4 < this.query.length()) {
               var2 = this.query.indexOf(63, var4);
               var3 = var2 == -1 ? this.query.length() : var2;
               if (var3 - var4 > 0) {
                  this.scope = this.query.substring(var4, var3);
               }

               var4 = var3 + 1;
               if (var4 < this.query.length()) {
                  var2 = this.query.indexOf(63, var4);
                  var3 = var2 == -1 ? this.query.length() : var2;
                  if (var3 - var4 > 0) {
                     this.filter = this.query.substring(var4, var3);
                     this.filter = UrlUtil.decode(this.filter, "UTF8");
                  }

                  var4 = var3 + 1;
                  if (var4 < this.query.length()) {
                     if (this.query.length() - var4 > 0) {
                        this.extensions = this.query.substring(var4);
                        this.extensions = UrlUtil.decode(this.extensions, "UTF8");
                     }

                  }
               }
            }
         }
      }
   }
}
