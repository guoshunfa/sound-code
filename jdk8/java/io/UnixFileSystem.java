package java.io;

import java.security.AccessController;
import java.security.PrivilegedAction;
import sun.security.action.GetPropertyAction;

class UnixFileSystem extends FileSystem {
   private final char slash = ((String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("file.separator")))).charAt(0);
   private final char colon = ((String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("path.separator")))).charAt(0);
   private final String javaHome = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("java.home")));
   private ExpiringCache cache = new ExpiringCache();
   private ExpiringCache javaHomePrefixCache = new ExpiringCache();

   public UnixFileSystem() {
   }

   public char getSeparator() {
      return this.slash;
   }

   public char getPathSeparator() {
      return this.colon;
   }

   private String normalize(String var1, int var2, int var3) {
      if (var2 == 0) {
         return var1;
      } else {
         int var4;
         for(var4 = var2; var4 > 0 && var1.charAt(var4 - 1) == '/'; --var4) {
         }

         if (var4 == 0) {
            return "/";
         } else {
            StringBuffer var5 = new StringBuffer(var1.length());
            if (var3 > 0) {
               var5.append(var1.substring(0, var3));
            }

            char var6 = 0;

            for(int var7 = var3; var7 < var4; ++var7) {
               char var8 = var1.charAt(var7);
               if (var6 != '/' || var8 != '/') {
                  var5.append(var8);
                  var6 = var8;
               }
            }

            return var5.toString();
         }
      }
   }

   public String normalize(String var1) {
      int var2 = var1.length();
      char var3 = 0;

      for(int var4 = 0; var4 < var2; ++var4) {
         char var5 = var1.charAt(var4);
         if (var3 == '/' && var5 == '/') {
            return this.normalize(var1, var2, var4 - 1);
         }

         var3 = var5;
      }

      if (var3 == '/') {
         return this.normalize(var1, var2, var2 - 1);
      } else {
         return var1;
      }
   }

   public int prefixLength(String var1) {
      if (var1.length() == 0) {
         return 0;
      } else {
         return var1.charAt(0) == '/' ? 1 : 0;
      }
   }

   public String resolve(String var1, String var2) {
      if (var2.equals("")) {
         return var1;
      } else if (var2.charAt(0) == '/') {
         return var1.equals("/") ? var2 : var1 + var2;
      } else {
         return var1.equals("/") ? var1 + var2 : var1 + '/' + var2;
      }
   }

   public String getDefaultParent() {
      return "/";
   }

   public String fromURIPath(String var1) {
      String var2 = var1;
      if (var1.endsWith("/") && var1.length() > 1) {
         var2 = var1.substring(0, var1.length() - 1);
      }

      return var2;
   }

   public boolean isAbsolute(File var1) {
      return var1.getPrefixLength() != 0;
   }

   public String resolve(File var1) {
      return this.isAbsolute(var1) ? var1.getPath() : this.resolve(System.getProperty("user.dir"), var1.getPath());
   }

   public String canonicalize(String var1) throws IOException {
      if (!useCanonCaches) {
         return this.canonicalize0(var1);
      } else {
         String var2 = this.cache.get(var1);
         if (var2 == null) {
            String var3 = null;
            String var4 = null;
            if (useCanonPrefixCache) {
               var3 = parentOrNull(var1);
               if (var3 != null) {
                  var4 = this.javaHomePrefixCache.get(var3);
                  if (var4 != null) {
                     String var5 = var1.substring(1 + var3.length());
                     var2 = var4 + this.slash + var5;
                     this.cache.put(var3 + this.slash + var5, var2);
                  }
               }
            }

            if (var2 == null) {
               var2 = this.canonicalize0(var1);
               this.cache.put(var1, var2);
               if (useCanonPrefixCache && var3 != null && var3.startsWith(this.javaHome)) {
                  var4 = parentOrNull(var2);
                  if (var4 != null && var4.equals(var3)) {
                     File var6 = new File(var2);
                     if (var6.exists() && !var6.isDirectory()) {
                        this.javaHomePrefixCache.put(var3, var4);
                     }
                  }
               }
            }
         }

         return var2;
      }
   }

   private native String canonicalize0(String var1) throws IOException;

   static String parentOrNull(String var0) {
      if (var0 == null) {
         return null;
      } else {
         char var1 = File.separatorChar;
         int var2 = var0.length() - 1;
         int var3 = var2;
         int var4 = 0;

         for(int var5 = 0; var3 > 0; --var3) {
            char var6 = var0.charAt(var3);
            if (var6 == '.') {
               ++var4;
               if (var4 >= 2) {
                  return null;
               }
            } else {
               if (var6 == var1) {
                  if (var4 == 1 && var5 == 0) {
                     return null;
                  }

                  if (var3 != 0 && var3 < var2 - 1 && var0.charAt(var3 - 1) != var1) {
                     return var0.substring(0, var3);
                  }

                  return null;
               }

               ++var5;
               var4 = 0;
            }
         }

         return null;
      }
   }

   public native int getBooleanAttributes0(File var1);

   public int getBooleanAttributes(File var1) {
      int var2 = this.getBooleanAttributes0(var1);
      String var3 = var1.getName();
      boolean var4 = var3.length() > 0 && var3.charAt(0) == '.';
      return var2 | (var4 ? 8 : 0);
   }

   public native boolean checkAccess(File var1, int var2);

   public native long getLastModifiedTime(File var1);

   public native long getLength(File var1);

   public native boolean setPermission(File var1, int var2, boolean var3, boolean var4);

   public native boolean createFileExclusively(String var1) throws IOException;

   public boolean delete(File var1) {
      this.cache.clear();
      this.javaHomePrefixCache.clear();
      return this.delete0(var1);
   }

   private native boolean delete0(File var1);

   public native String[] list(File var1);

   public native boolean createDirectory(File var1);

   public boolean rename(File var1, File var2) {
      this.cache.clear();
      this.javaHomePrefixCache.clear();
      return this.rename0(var1, var2);
   }

   private native boolean rename0(File var1, File var2);

   public native boolean setLastModifiedTime(File var1, long var2);

   public native boolean setReadOnly(File var1);

   public File[] listRoots() {
      try {
         SecurityManager var1 = System.getSecurityManager();
         if (var1 != null) {
            var1.checkRead("/");
         }

         return new File[]{new File("/")};
      } catch (SecurityException var2) {
         return new File[0];
      }
   }

   public native long getSpace(File var1, int var2);

   public int compare(File var1, File var2) {
      return var1.getPath().compareTo(var2.getPath());
   }

   public int hashCode(File var1) {
      return var1.getPath().hashCode() ^ 1234321;
   }

   private static native void initIDs();

   static {
      initIDs();
   }
}
