package javax.tools;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public enum StandardLocation implements JavaFileManager.Location {
   CLASS_OUTPUT,
   SOURCE_OUTPUT,
   CLASS_PATH,
   SOURCE_PATH,
   ANNOTATION_PROCESSOR_PATH,
   PLATFORM_CLASS_PATH,
   NATIVE_HEADER_OUTPUT;

   private static final ConcurrentMap<String, JavaFileManager.Location> locations = new ConcurrentHashMap();

   public static JavaFileManager.Location locationFor(final String var0) {
      if (locations.isEmpty()) {
         StandardLocation[] var1 = values();
         int var2 = var1.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            StandardLocation var4 = var1[var3];
            locations.putIfAbsent(var4.getName(), var4);
         }
      }

      locations.putIfAbsent(var0.toString(), new JavaFileManager.Location() {
         public String getName() {
            return var0;
         }

         public boolean isOutputLocation() {
            return var0.endsWith("_OUTPUT");
         }
      });
      return (JavaFileManager.Location)locations.get(var0);
   }

   public String getName() {
      return this.name();
   }

   public boolean isOutputLocation() {
      switch(this) {
      case CLASS_OUTPUT:
      case SOURCE_OUTPUT:
      case NATIVE_HEADER_OUTPUT:
         return true;
      default:
         return false;
      }
   }
}
