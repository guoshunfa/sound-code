package javax.activation;

import java.io.File;
import java.util.Map;
import java.util.WeakHashMap;

public abstract class FileTypeMap {
   private static FileTypeMap defaultMap = null;
   private static Map<ClassLoader, FileTypeMap> map = new WeakHashMap();

   public abstract String getContentType(File var1);

   public abstract String getContentType(String var1);

   public static synchronized void setDefaultFileTypeMap(FileTypeMap fileTypeMap) {
      SecurityManager security = System.getSecurityManager();
      if (security != null) {
         try {
            security.checkSetFactory();
         } catch (SecurityException var3) {
            if (FileTypeMap.class.getClassLoader() == null || FileTypeMap.class.getClassLoader() != fileTypeMap.getClass().getClassLoader()) {
               throw var3;
            }
         }
      }

      map.remove(SecuritySupport.getContextClassLoader());
      defaultMap = fileTypeMap;
   }

   public static synchronized FileTypeMap getDefaultFileTypeMap() {
      if (defaultMap != null) {
         return defaultMap;
      } else {
         ClassLoader tccl = SecuritySupport.getContextClassLoader();
         FileTypeMap def = (FileTypeMap)map.get(tccl);
         if (def == null) {
            def = new MimetypesFileTypeMap();
            map.put(tccl, def);
         }

         return (FileTypeMap)def;
      }
   }
}
