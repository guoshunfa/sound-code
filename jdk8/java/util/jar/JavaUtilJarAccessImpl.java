package java.util.jar;

import java.io.IOException;
import java.net.URL;
import java.security.CodeSource;
import java.util.Enumeration;
import java.util.List;
import sun.misc.JavaUtilJarAccess;

class JavaUtilJarAccessImpl implements JavaUtilJarAccess {
   public boolean jarFileHasClassPathAttribute(JarFile var1) throws IOException {
      return var1.hasClassPathAttribute();
   }

   public CodeSource[] getCodeSources(JarFile var1, URL var2) {
      return var1.getCodeSources(var2);
   }

   public CodeSource getCodeSource(JarFile var1, URL var2, String var3) {
      return var1.getCodeSource(var2, var3);
   }

   public Enumeration<String> entryNames(JarFile var1, CodeSource[] var2) {
      return var1.entryNames(var2);
   }

   public Enumeration<JarEntry> entries2(JarFile var1) {
      return var1.entries2();
   }

   public void setEagerValidation(JarFile var1, boolean var2) {
      var1.setEagerValidation(var2);
   }

   public List<Object> getManifestDigests(JarFile var1) {
      return var1.getManifestDigests();
   }
}
