package javax.management.remote.rmi;

import java.security.ProtectionDomain;

class NoCallStackClassLoader extends ClassLoader {
   private final String[] classNames;
   private final byte[][] byteCodes;
   private final String[] referencedClassNames;
   private final ClassLoader referencedClassLoader;
   private final ProtectionDomain protectionDomain;

   public NoCallStackClassLoader(String var1, byte[] var2, String[] var3, ClassLoader var4, ProtectionDomain var5) {
      this(new String[]{var1}, new byte[][]{var2}, var3, var4, var5);
   }

   public NoCallStackClassLoader(String[] var1, byte[][] var2, String[] var3, ClassLoader var4, ProtectionDomain var5) {
      super((ClassLoader)null);
      if (var1 != null && var1.length != 0 && var2 != null && var1.length == var2.length && var3 != null && var5 != null) {
         int var6;
         for(var6 = 0; var6 < var1.length; ++var6) {
            if (var1[var6] == null || var2[var6] == null) {
               throw new IllegalArgumentException();
            }
         }

         for(var6 = 0; var6 < var3.length; ++var6) {
            if (var3[var6] == null) {
               throw new IllegalArgumentException();
            }
         }

         this.classNames = var1;
         this.byteCodes = var2;
         this.referencedClassNames = var3;
         this.referencedClassLoader = var4;
         this.protectionDomain = var5;
      } else {
         throw new IllegalArgumentException();
      }
   }

   protected Class<?> findClass(String var1) throws ClassNotFoundException {
      int var2;
      for(var2 = 0; var2 < this.classNames.length; ++var2) {
         if (var1.equals(this.classNames[var2])) {
            return this.defineClass(this.classNames[var2], this.byteCodes[var2], 0, this.byteCodes[var2].length, this.protectionDomain);
         }
      }

      if (this.referencedClassLoader != null) {
         for(var2 = 0; var2 < this.referencedClassNames.length; ++var2) {
            if (var1.equals(this.referencedClassNames[var2])) {
               return this.referencedClassLoader.loadClass(var1);
            }
         }
      }

      throw new ClassNotFoundException(var1);
   }

   public static byte[] stringToBytes(String var0) {
      int var1 = var0.length();
      byte[] var2 = new byte[var1];

      for(int var3 = 0; var3 < var1; ++var3) {
         var2[var3] = (byte)var0.charAt(var3);
      }

      return var2;
   }
}
