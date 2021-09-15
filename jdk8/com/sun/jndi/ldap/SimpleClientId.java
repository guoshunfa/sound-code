package com.sun.jndi.ldap;

import java.io.OutputStream;
import java.util.Arrays;
import javax.naming.ldap.Control;

class SimpleClientId extends ClientId {
   private final String username;
   private final Object passwd;
   private final int myHash;

   SimpleClientId(int var1, String var2, int var3, String var4, Control[] var5, OutputStream var6, String var7, String var8, Object var9) {
      super(var1, var2, var3, var4, var5, var6, var7);
      this.username = var8;
      int var10 = 0;
      if (var9 == null) {
         this.passwd = null;
      } else if (var9 instanceof byte[]) {
         this.passwd = ((byte[])((byte[])var9)).clone();
         var10 = Arrays.hashCode((byte[])((byte[])var9));
      } else if (var9 instanceof char[]) {
         this.passwd = ((char[])((char[])var9)).clone();
         var10 = Arrays.hashCode((char[])((char[])var9));
      } else {
         this.passwd = var9;
         var10 = var9.hashCode();
      }

      this.myHash = super.hashCode() ^ (var8 != null ? var8.hashCode() : 0) ^ var10;
   }

   public boolean equals(Object var1) {
      if (var1 != null && var1 instanceof SimpleClientId) {
         SimpleClientId var2 = (SimpleClientId)var1;
         return super.equals(var1) && (this.username == var2.username || this.username != null && this.username.equals(var2.username)) && (this.passwd == var2.passwd || this.passwd != null && var2.passwd != null && (this.passwd instanceof String && this.passwd.equals(var2.passwd) || this.passwd instanceof byte[] && var2.passwd instanceof byte[] && Arrays.equals((byte[])((byte[])this.passwd), (byte[])((byte[])var2.passwd)) || this.passwd instanceof char[] && var2.passwd instanceof char[] && Arrays.equals((char[])((char[])this.passwd), (char[])((char[])var2.passwd))));
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this.myHash;
   }

   public String toString() {
      return super.toString() + ":" + this.username;
   }
}
