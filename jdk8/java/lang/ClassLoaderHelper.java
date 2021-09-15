package java.lang;

import java.io.File;

class ClassLoaderHelper {
   private ClassLoaderHelper() {
   }

   static File mapAlternativeName(File var0) {
      String var1 = var0.toString();
      int var2 = var1.lastIndexOf(46);
      return var2 < 0 ? null : new File(var1.substring(0, var2) + ".jnilib");
   }
}
