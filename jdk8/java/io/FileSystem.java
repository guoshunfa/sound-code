package java.io;

abstract class FileSystem {
   public static final int BA_EXISTS = 1;
   public static final int BA_REGULAR = 2;
   public static final int BA_DIRECTORY = 4;
   public static final int BA_HIDDEN = 8;
   public static final int ACCESS_READ = 4;
   public static final int ACCESS_WRITE = 2;
   public static final int ACCESS_EXECUTE = 1;
   public static final int SPACE_TOTAL = 0;
   public static final int SPACE_FREE = 1;
   public static final int SPACE_USABLE = 2;
   static boolean useCanonCaches = true;
   static boolean useCanonPrefixCache = true;

   public abstract char getSeparator();

   public abstract char getPathSeparator();

   public abstract String normalize(String var1);

   public abstract int prefixLength(String var1);

   public abstract String resolve(String var1, String var2);

   public abstract String getDefaultParent();

   public abstract String fromURIPath(String var1);

   public abstract boolean isAbsolute(File var1);

   public abstract String resolve(File var1);

   public abstract String canonicalize(String var1) throws IOException;

   public abstract int getBooleanAttributes(File var1);

   public abstract boolean checkAccess(File var1, int var2);

   public abstract boolean setPermission(File var1, int var2, boolean var3, boolean var4);

   public abstract long getLastModifiedTime(File var1);

   public abstract long getLength(File var1);

   public abstract boolean createFileExclusively(String var1) throws IOException;

   public abstract boolean delete(File var1);

   public abstract String[] list(File var1);

   public abstract boolean createDirectory(File var1);

   public abstract boolean rename(File var1, File var2);

   public abstract boolean setLastModifiedTime(File var1, long var2);

   public abstract boolean setReadOnly(File var1);

   public abstract File[] listRoots();

   public abstract long getSpace(File var1, int var2);

   public abstract int compare(File var1, File var2);

   public abstract int hashCode(File var1);

   private static boolean getBooleanProperty(String var0, boolean var1) {
      String var2 = System.getProperty(var0);
      if (var2 == null) {
         return var1;
      } else {
         return var2.equalsIgnoreCase("true");
      }
   }

   static {
      useCanonCaches = getBooleanProperty("sun.io.useCanonCaches", useCanonCaches);
      useCanonPrefixCache = getBooleanProperty("sun.io.useCanonPrefixCache", useCanonPrefixCache);
   }
}
