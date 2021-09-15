package sun.nio.fs;

import java.util.regex.Pattern;

class MacOSXFileSystem extends BsdFileSystem {
   MacOSXFileSystem(UnixFileSystemProvider var1, String var2) {
      super(var1, var2);
   }

   Pattern compilePathMatchPattern(String var1) {
      return Pattern.compile(var1, 128);
   }

   char[] normalizeNativePath(char[] var1) {
      char[] var2 = var1;
      int var3 = var1.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         char var5 = var2[var4];
         if (var5 > 128) {
            return MacOSXNativeDispatcher.normalizepath(var1, 0);
         }
      }

      return var1;
   }

   String normalizeJavaPath(String var1) {
      for(int var2 = 0; var2 < var1.length(); ++var2) {
         if (var1.charAt(var2) > 128) {
            return new String(MacOSXNativeDispatcher.normalizepath(var1.toCharArray(), 2));
         }
      }

      return var1;
   }
}
