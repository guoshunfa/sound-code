package com.sun.beans.finder;

class InstanceFinder<T> {
   private static final String[] EMPTY = new String[0];
   private final Class<? extends T> type;
   private final boolean allow;
   private final String suffix;
   private volatile String[] packages;

   InstanceFinder(Class<? extends T> var1, boolean var2, String var3, String... var4) {
      this.type = var1;
      this.allow = var2;
      this.suffix = var3;
      this.packages = (String[])var4.clone();
   }

   public String[] getPackages() {
      return (String[])this.packages.clone();
   }

   public void setPackages(String... var1) {
      this.packages = var1 != null && var1.length > 0 ? (String[])var1.clone() : EMPTY;
   }

   public T find(Class<?> var1) {
      if (var1 == null) {
         return null;
      } else {
         String var2 = var1.getName() + this.suffix;
         Object var3 = this.instantiate(var1, var2);
         if (var3 != null) {
            return var3;
         } else {
            if (this.allow) {
               var3 = this.instantiate(var1, (String)null);
               if (var3 != null) {
                  return var3;
               }
            }

            int var4 = var2.lastIndexOf(46) + 1;
            if (var4 > 0) {
               var2 = var2.substring(var4);
            }

            String[] var5 = this.packages;
            int var6 = var5.length;

            for(int var7 = 0; var7 < var6; ++var7) {
               String var8 = var5[var7];
               var3 = this.instantiate(var1, var8, var2);
               if (var3 != null) {
                  return var3;
               }
            }

            return null;
         }
      }
   }

   protected T instantiate(Class<?> var1, String var2) {
      if (var1 != null) {
         try {
            if (var2 != null) {
               var1 = ClassFinder.findClass(var2, var1.getClassLoader());
            }

            if (this.type.isAssignableFrom(var1)) {
               return var1.newInstance();
            }
         } catch (Exception var4) {
         }
      }

      return null;
   }

   protected T instantiate(Class<?> var1, String var2, String var3) {
      return this.instantiate(var1, var2 + '.' + var3);
   }
}
