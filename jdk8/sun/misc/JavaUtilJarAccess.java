package sun.misc;

import java.io.IOException;
import java.net.URL;
import java.security.CodeSource;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public interface JavaUtilJarAccess {
   boolean jarFileHasClassPathAttribute(JarFile var1) throws IOException;

   CodeSource[] getCodeSources(JarFile var1, URL var2);

   CodeSource getCodeSource(JarFile var1, URL var2, String var3);

   Enumeration<String> entryNames(JarFile var1, CodeSource[] var2);

   Enumeration<JarEntry> entries2(JarFile var1);

   void setEagerValidation(JarFile var1, boolean var2);

   List<Object> getManifestDigests(JarFile var1);
}
