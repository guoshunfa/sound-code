package com.sun.beans.finder;

final class Signature {
   private final Class<?> type;
   private final String name;
   private final Class<?>[] args;
   private volatile int code;

   Signature(Class<?> var1, Class<?>[] var2) {
      this(var1, (String)null, var2);
   }

   Signature(Class<?> var1, String var2, Class<?>[] var3) {
      this.type = var1;
      this.name = var2;
      this.args = var3;
   }

   Class<?> getType() {
      return this.type;
   }

   String getName() {
      return this.name;
   }

   Class<?>[] getArgs() {
      return this.args;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof Signature)) {
         return false;
      } else {
         Signature var2 = (Signature)var1;
         return isEqual((Object)var2.type, (Object)this.type) && isEqual((Object)var2.name, (Object)this.name) && isEqual(var2.args, this.args);
      }
   }

   private static boolean isEqual(Object var0, Object var1) {
      return var0 == null ? var1 == null : var0.equals(var1);
   }

   private static boolean isEqual(Class<?>[] var0, Class<?>[] var1) {
      if (var0 != null && var1 != null) {
         if (var0.length != var1.length) {
            return false;
         } else {
            for(int var2 = 0; var2 < var0.length; ++var2) {
               if (!isEqual((Object)var0[var2], (Object)var1[var2])) {
                  return false;
               }
            }

            return true;
         }
      } else {
         return var0 == var1;
      }
   }

   public int hashCode() {
      if (this.code == 0) {
         byte var1 = 17;
         int var6 = addHashCode(var1, this.type);
         var6 = addHashCode(var6, this.name);
         if (this.args != null) {
            Class[] var2 = this.args;
            int var3 = var2.length;

            for(int var4 = 0; var4 < var3; ++var4) {
               Class var5 = var2[var4];
               var6 = addHashCode(var6, var5);
            }
         }

         this.code = var6;
      }

      return this.code;
   }

   private static int addHashCode(int var0, Object var1) {
      var0 *= 37;
      return var1 != null ? var0 + var1.hashCode() : var0;
   }
}
