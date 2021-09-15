package sun.misc;

import java.io.IOException;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import java.util.jar.JarFile;

public class ClassLoaderUtil {
   public static void releaseLoader(URLClassLoader var0) {
      releaseLoader(var0, (List)null);
   }

   public static List<IOException> releaseLoader(URLClassLoader var0, List<String> var1) {
      LinkedList var2 = new LinkedList();

      try {
         if (var1 != null) {
            var1.clear();
         }

         URLClassPath var3 = SharedSecrets.getJavaNetAccess().getURLClassPath(var0);
         ArrayList var4 = var3.loaders;
         Stack var5 = var3.urls;
         HashMap var6 = var3.lmap;
         synchronized(var5) {
            var5.clear();
         }

         synchronized(var6) {
            var6.clear();
         }

         synchronized(var3) {
            Iterator var8 = var4.iterator();

            while(true) {
               Object var9;
               do {
                  do {
                     if (!var8.hasNext()) {
                        var4.clear();
                        return var2;
                     }

                     var9 = var8.next();
                  } while(var9 == null);
               } while(!(var9 instanceof URLClassPath.JarLoader));

               URLClassPath.JarLoader var10 = (URLClassPath.JarLoader)var9;
               JarFile var11 = var10.getJarFile();

               try {
                  if (var11 != null) {
                     var11.close();
                     if (var1 != null) {
                        var1.add(var11.getName());
                     }
                  }
               } catch (IOException var19) {
                  String var13 = var11 == null ? "filename not available" : var11.getName();
                  String var14 = "Error closing JAR file: " + var13;
                  IOException var15 = new IOException(var14);
                  var15.initCause(var19);
                  var2.add(var15);
               }
            }
         }
      } catch (Throwable var21) {
         throw new RuntimeException(var21);
      }
   }
}
