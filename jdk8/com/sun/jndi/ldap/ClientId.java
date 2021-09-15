package com.sun.jndi.ldap;

import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Locale;
import javax.naming.ldap.Control;
import javax.net.SocketFactory;

class ClientId {
   private final int version;
   private final String hostname;
   private final int port;
   private final String protocol;
   private final Control[] bindCtls;
   private final OutputStream trace;
   private final String socketFactory;
   private final int myHash;
   private final int ctlHash;
   private SocketFactory factory = null;
   private Method sockComparator = null;
   private boolean isDefaultSockFactory = false;
   public static final boolean debug = false;

   ClientId(int var1, String var2, int var3, String var4, Control[] var5, OutputStream var6, String var7) {
      this.version = var1;
      this.hostname = var2.toLowerCase(Locale.ENGLISH);
      this.port = var3;
      this.protocol = var4;
      this.bindCtls = var5 != null ? (Control[])var5.clone() : null;
      this.trace = var6;
      this.socketFactory = var7;
      if (var7 != null && !var7.equals("javax.net.ssl.SSLSocketFactory")) {
         try {
            Class var8 = Obj.helper.loadClass(var7);
            Class var9 = Class.forName("java.lang.Object");
            this.sockComparator = var8.getMethod("compare", var9, var9);
            Method var10 = var8.getMethod("getDefault");
            this.factory = (SocketFactory)var10.invoke((Object)null);
         } catch (Exception var11) {
         }
      } else {
         this.isDefaultSockFactory = true;
      }

      this.myHash = var1 + var3 + (var6 != null ? var6.hashCode() : 0) + (this.hostname != null ? this.hostname.hashCode() : 0) + (var4 != null ? var4.hashCode() : 0) + (this.ctlHash = hashCodeControls(var5));
   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof ClientId)) {
         return false;
      } else {
         ClientId var2 = (ClientId)var1;
         return this.myHash == var2.myHash && this.version == var2.version && this.port == var2.port && this.trace == var2.trace && (this.hostname == var2.hostname || this.hostname != null && this.hostname.equals(var2.hostname)) && (this.protocol == var2.protocol || this.protocol != null && this.protocol.equals(var2.protocol)) && this.ctlHash == var2.ctlHash && equalsControls(this.bindCtls, var2.bindCtls) && this.equalsSockFactory(var2);
      }
   }

   public int hashCode() {
      return this.myHash;
   }

   private static int hashCodeControls(Control[] var0) {
      if (var0 == null) {
         return 0;
      } else {
         int var1 = 0;

         for(int var2 = 0; var2 < var0.length; ++var2) {
            var1 = var1 * 31 + var0[var2].getID().hashCode();
         }

         return var1;
      }
   }

   private static boolean equalsControls(Control[] var0, Control[] var1) {
      if (var0 == var1) {
         return true;
      } else if (var0 != null && var1 != null) {
         if (var0.length != var1.length) {
            return false;
         } else {
            for(int var2 = 0; var2 < var0.length; ++var2) {
               if (!var0[var2].getID().equals(var1[var2].getID()) || var0[var2].isCritical() != var1[var2].isCritical() || !Arrays.equals(var0[var2].getEncodedValue(), var1[var2].getEncodedValue())) {
                  return false;
               }
            }

            return true;
         }
      } else {
         return false;
      }
   }

   private boolean equalsSockFactory(ClientId var1) {
      if (this.isDefaultSockFactory && var1.isDefaultSockFactory) {
         return true;
      } else {
         return !var1.isDefaultSockFactory ? this.invokeComparator(var1, this) : this.invokeComparator(this, var1);
      }
   }

   private boolean invokeComparator(ClientId var1, ClientId var2) {
      Object var3;
      try {
         var3 = var1.sockComparator.invoke(var1.factory, var1.socketFactory, var2.socketFactory);
      } catch (Exception var5) {
         return false;
      }

      return (Integer)var3 == 0;
   }

   private static String toStringControls(Control[] var0) {
      if (var0 == null) {
         return "";
      } else {
         StringBuffer var1 = new StringBuffer();

         for(int var2 = 0; var2 < var0.length; ++var2) {
            var1.append(var0[var2].getID());
            var1.append(' ');
         }

         return var1.toString();
      }
   }

   public String toString() {
      return this.hostname + ":" + this.port + ":" + (this.protocol != null ? this.protocol : "") + ":" + toStringControls(this.bindCtls) + ":" + this.socketFactory;
   }
}
