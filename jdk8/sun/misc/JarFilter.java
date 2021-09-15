package sun.misc;

import java.io.File;
import java.io.FilenameFilter;

public class JarFilter implements FilenameFilter {
   public boolean accept(File var1, String var2) {
      String var3 = var2.toLowerCase();
      return var3.endsWith(".jar") || var3.endsWith(".zip");
   }
}
