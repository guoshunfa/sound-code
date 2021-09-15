package java.io;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.SecureRandom;
import java.util.ArrayList;
import sun.misc.Unsafe;
import sun.security.action.GetPropertyAction;

public class File implements Serializable, Comparable<File> {
   private static final FileSystem fs = DefaultFileSystem.getFileSystem();
   private final String path;
   private transient File.PathStatus status = null;
   private final transient int prefixLength;
   public static final char separatorChar;
   public static final String separator;
   public static final char pathSeparatorChar;
   public static final String pathSeparator;
   private static final long PATH_OFFSET;
   private static final long PREFIX_LENGTH_OFFSET;
   private static final Unsafe UNSAFE;
   private static final long serialVersionUID = 301077366599181567L;
   private transient volatile Path filePath;

   final boolean isInvalid() {
      if (this.status == null) {
         this.status = this.path.indexOf(0) < 0 ? File.PathStatus.CHECKED : File.PathStatus.INVALID;
      }

      return this.status == File.PathStatus.INVALID;
   }

   int getPrefixLength() {
      return this.prefixLength;
   }

   private File(String var1, int var2) {
      this.path = var1;
      this.prefixLength = var2;
   }

   private File(String var1, File var2) {
      assert var2.path != null;

      assert !var2.path.equals("");

      this.path = fs.resolve(var2.path, var1);
      this.prefixLength = var2.prefixLength;
   }

   public File(String var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         this.path = fs.normalize(var1);
         this.prefixLength = fs.prefixLength(this.path);
      }
   }

   public File(String var1, String var2) {
      if (var2 == null) {
         throw new NullPointerException();
      } else {
         if (var1 != null) {
            if (var1.equals("")) {
               this.path = fs.resolve(fs.getDefaultParent(), fs.normalize(var2));
            } else {
               this.path = fs.resolve(fs.normalize(var1), fs.normalize(var2));
            }
         } else {
            this.path = fs.normalize(var2);
         }

         this.prefixLength = fs.prefixLength(this.path);
      }
   }

   public File(File var1, String var2) {
      if (var2 == null) {
         throw new NullPointerException();
      } else {
         if (var1 != null) {
            if (var1.path.equals("")) {
               this.path = fs.resolve(fs.getDefaultParent(), fs.normalize(var2));
            } else {
               this.path = fs.resolve(var1.path, fs.normalize(var2));
            }
         } else {
            this.path = fs.normalize(var2);
         }

         this.prefixLength = fs.prefixLength(this.path);
      }
   }

   public File(URI var1) {
      if (!var1.isAbsolute()) {
         throw new IllegalArgumentException("URI is not absolute");
      } else if (var1.isOpaque()) {
         throw new IllegalArgumentException("URI is not hierarchical");
      } else {
         String var2 = var1.getScheme();
         if (var2 != null && var2.equalsIgnoreCase("file")) {
            if (var1.getAuthority() != null) {
               throw new IllegalArgumentException("URI has an authority component");
            } else if (var1.getFragment() != null) {
               throw new IllegalArgumentException("URI has a fragment component");
            } else if (var1.getQuery() != null) {
               throw new IllegalArgumentException("URI has a query component");
            } else {
               String var3 = var1.getPath();
               if (var3.equals("")) {
                  throw new IllegalArgumentException("URI path component is empty");
               } else {
                  var3 = fs.fromURIPath(var3);
                  if (separatorChar != '/') {
                     var3 = var3.replace('/', separatorChar);
                  }

                  this.path = fs.normalize(var3);
                  this.prefixLength = fs.prefixLength(this.path);
               }
            }
         } else {
            throw new IllegalArgumentException("URI scheme is not \"file\"");
         }
      }
   }

   public String getName() {
      int var1 = this.path.lastIndexOf(separatorChar);
      return var1 < this.prefixLength ? this.path.substring(this.prefixLength) : this.path.substring(var1 + 1);
   }

   public String getParent() {
      int var1 = this.path.lastIndexOf(separatorChar);
      if (var1 < this.prefixLength) {
         return this.prefixLength > 0 && this.path.length() > this.prefixLength ? this.path.substring(0, this.prefixLength) : null;
      } else {
         return this.path.substring(0, var1);
      }
   }

   public File getParentFile() {
      String var1 = this.getParent();
      return var1 == null ? null : new File(var1, this.prefixLength);
   }

   public String getPath() {
      return this.path;
   }

   public boolean isAbsolute() {
      return fs.isAbsolute(this);
   }

   public String getAbsolutePath() {
      return fs.resolve(this);
   }

   public File getAbsoluteFile() {
      String var1 = this.getAbsolutePath();
      return new File(var1, fs.prefixLength(var1));
   }

   public String getCanonicalPath() throws IOException {
      if (this.isInvalid()) {
         throw new IOException("Invalid file path");
      } else {
         return fs.canonicalize(fs.resolve(this));
      }
   }

   public File getCanonicalFile() throws IOException {
      String var1 = this.getCanonicalPath();
      return new File(var1, fs.prefixLength(var1));
   }

   private static String slashify(String var0, boolean var1) {
      String var2 = var0;
      if (separatorChar != '/') {
         var2 = var0.replace(separatorChar, '/');
      }

      if (!var2.startsWith("/")) {
         var2 = "/" + var2;
      }

      if (!var2.endsWith("/") && var1) {
         var2 = var2 + "/";
      }

      return var2;
   }

   /** @deprecated */
   @Deprecated
   public URL toURL() throws MalformedURLException {
      if (this.isInvalid()) {
         throw new MalformedURLException("Invalid file path");
      } else {
         return new URL("file", "", slashify(this.getAbsolutePath(), this.isDirectory()));
      }
   }

   public URI toURI() {
      try {
         File var1 = this.getAbsoluteFile();
         String var2 = slashify(var1.getPath(), var1.isDirectory());
         if (var2.startsWith("//")) {
            var2 = "//" + var2;
         }

         return new URI("file", (String)null, var2, (String)null);
      } catch (URISyntaxException var3) {
         throw new Error(var3);
      }
   }

   public boolean canRead() {
      SecurityManager var1 = System.getSecurityManager();
      if (var1 != null) {
         var1.checkRead(this.path);
      }

      return this.isInvalid() ? false : fs.checkAccess(this, 4);
   }

   public boolean canWrite() {
      SecurityManager var1 = System.getSecurityManager();
      if (var1 != null) {
         var1.checkWrite(this.path);
      }

      return this.isInvalid() ? false : fs.checkAccess(this, 2);
   }

   public boolean exists() {
      SecurityManager var1 = System.getSecurityManager();
      if (var1 != null) {
         var1.checkRead(this.path);
      }

      if (this.isInvalid()) {
         return false;
      } else {
         return (fs.getBooleanAttributes(this) & 1) != 0;
      }
   }

   public boolean isDirectory() {
      SecurityManager var1 = System.getSecurityManager();
      if (var1 != null) {
         var1.checkRead(this.path);
      }

      if (this.isInvalid()) {
         return false;
      } else {
         return (fs.getBooleanAttributes(this) & 4) != 0;
      }
   }

   public boolean isFile() {
      SecurityManager var1 = System.getSecurityManager();
      if (var1 != null) {
         var1.checkRead(this.path);
      }

      if (this.isInvalid()) {
         return false;
      } else {
         return (fs.getBooleanAttributes(this) & 2) != 0;
      }
   }

   public boolean isHidden() {
      SecurityManager var1 = System.getSecurityManager();
      if (var1 != null) {
         var1.checkRead(this.path);
      }

      if (this.isInvalid()) {
         return false;
      } else {
         return (fs.getBooleanAttributes(this) & 8) != 0;
      }
   }

   public long lastModified() {
      SecurityManager var1 = System.getSecurityManager();
      if (var1 != null) {
         var1.checkRead(this.path);
      }

      return this.isInvalid() ? 0L : fs.getLastModifiedTime(this);
   }

   public long length() {
      SecurityManager var1 = System.getSecurityManager();
      if (var1 != null) {
         var1.checkRead(this.path);
      }

      return this.isInvalid() ? 0L : fs.getLength(this);
   }

   public boolean createNewFile() throws IOException {
      SecurityManager var1 = System.getSecurityManager();
      if (var1 != null) {
         var1.checkWrite(this.path);
      }

      if (this.isInvalid()) {
         throw new IOException("Invalid file path");
      } else {
         return fs.createFileExclusively(this.path);
      }
   }

   public boolean delete() {
      SecurityManager var1 = System.getSecurityManager();
      if (var1 != null) {
         var1.checkDelete(this.path);
      }

      return this.isInvalid() ? false : fs.delete(this);
   }

   public void deleteOnExit() {
      SecurityManager var1 = System.getSecurityManager();
      if (var1 != null) {
         var1.checkDelete(this.path);
      }

      if (!this.isInvalid()) {
         DeleteOnExitHook.add(this.path);
      }
   }

   public String[] list() {
      SecurityManager var1 = System.getSecurityManager();
      if (var1 != null) {
         var1.checkRead(this.path);
      }

      return this.isInvalid() ? null : fs.list(this);
   }

   public String[] list(FilenameFilter var1) {
      String[] var2 = this.list();
      if (var2 != null && var1 != null) {
         ArrayList var3 = new ArrayList();

         for(int var4 = 0; var4 < var2.length; ++var4) {
            if (var1.accept(this, var2[var4])) {
               var3.add(var2[var4]);
            }
         }

         return (String[])var3.toArray(new String[var3.size()]);
      } else {
         return var2;
      }
   }

   public File[] listFiles() {
      String[] var1 = this.list();
      if (var1 == null) {
         return null;
      } else {
         int var2 = var1.length;
         File[] var3 = new File[var2];

         for(int var4 = 0; var4 < var2; ++var4) {
            var3[var4] = new File(var1[var4], this);
         }

         return var3;
      }
   }

   public File[] listFiles(FilenameFilter var1) {
      String[] var2 = this.list();
      if (var2 == null) {
         return null;
      } else {
         ArrayList var3 = new ArrayList();
         String[] var4 = var2;
         int var5 = var2.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            String var7 = var4[var6];
            if (var1 == null || var1.accept(this, var7)) {
               var3.add(new File(var7, this));
            }
         }

         return (File[])var3.toArray(new File[var3.size()]);
      }
   }

   public File[] listFiles(FileFilter var1) {
      String[] var2 = this.list();
      if (var2 == null) {
         return null;
      } else {
         ArrayList var3 = new ArrayList();
         String[] var4 = var2;
         int var5 = var2.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            String var7 = var4[var6];
            File var8 = new File(var7, this);
            if (var1 == null || var1.accept(var8)) {
               var3.add(var8);
            }
         }

         return (File[])var3.toArray(new File[var3.size()]);
      }
   }

   public boolean mkdir() {
      SecurityManager var1 = System.getSecurityManager();
      if (var1 != null) {
         var1.checkWrite(this.path);
      }

      return this.isInvalid() ? false : fs.createDirectory(this);
   }

   public boolean mkdirs() {
      if (this.exists()) {
         return false;
      } else if (this.mkdir()) {
         return true;
      } else {
         File var1 = null;

         try {
            var1 = this.getCanonicalFile();
         } catch (IOException var3) {
            return false;
         }

         File var2 = var1.getParentFile();
         return var2 != null && (var2.mkdirs() || var2.exists()) && var1.mkdir();
      }
   }

   public boolean renameTo(File var1) {
      SecurityManager var2 = System.getSecurityManager();
      if (var2 != null) {
         var2.checkWrite(this.path);
         var2.checkWrite(var1.path);
      }

      if (var1 == null) {
         throw new NullPointerException();
      } else {
         return !this.isInvalid() && !var1.isInvalid() ? fs.rename(this, var1) : false;
      }
   }

   public boolean setLastModified(long var1) {
      if (var1 < 0L) {
         throw new IllegalArgumentException("Negative time");
      } else {
         SecurityManager var3 = System.getSecurityManager();
         if (var3 != null) {
            var3.checkWrite(this.path);
         }

         return this.isInvalid() ? false : fs.setLastModifiedTime(this, var1);
      }
   }

   public boolean setReadOnly() {
      SecurityManager var1 = System.getSecurityManager();
      if (var1 != null) {
         var1.checkWrite(this.path);
      }

      return this.isInvalid() ? false : fs.setReadOnly(this);
   }

   public boolean setWritable(boolean var1, boolean var2) {
      SecurityManager var3 = System.getSecurityManager();
      if (var3 != null) {
         var3.checkWrite(this.path);
      }

      return this.isInvalid() ? false : fs.setPermission(this, 2, var1, var2);
   }

   public boolean setWritable(boolean var1) {
      return this.setWritable(var1, true);
   }

   public boolean setReadable(boolean var1, boolean var2) {
      SecurityManager var3 = System.getSecurityManager();
      if (var3 != null) {
         var3.checkWrite(this.path);
      }

      return this.isInvalid() ? false : fs.setPermission(this, 4, var1, var2);
   }

   public boolean setReadable(boolean var1) {
      return this.setReadable(var1, true);
   }

   public boolean setExecutable(boolean var1, boolean var2) {
      SecurityManager var3 = System.getSecurityManager();
      if (var3 != null) {
         var3.checkWrite(this.path);
      }

      return this.isInvalid() ? false : fs.setPermission(this, 1, var1, var2);
   }

   public boolean setExecutable(boolean var1) {
      return this.setExecutable(var1, true);
   }

   public boolean canExecute() {
      SecurityManager var1 = System.getSecurityManager();
      if (var1 != null) {
         var1.checkExec(this.path);
      }

      return this.isInvalid() ? false : fs.checkAccess(this, 1);
   }

   public static File[] listRoots() {
      return fs.listRoots();
   }

   public long getTotalSpace() {
      SecurityManager var1 = System.getSecurityManager();
      if (var1 != null) {
         var1.checkPermission(new RuntimePermission("getFileSystemAttributes"));
         var1.checkRead(this.path);
      }

      return this.isInvalid() ? 0L : fs.getSpace(this, 0);
   }

   public long getFreeSpace() {
      SecurityManager var1 = System.getSecurityManager();
      if (var1 != null) {
         var1.checkPermission(new RuntimePermission("getFileSystemAttributes"));
         var1.checkRead(this.path);
      }

      return this.isInvalid() ? 0L : fs.getSpace(this, 1);
   }

   public long getUsableSpace() {
      SecurityManager var1 = System.getSecurityManager();
      if (var1 != null) {
         var1.checkPermission(new RuntimePermission("getFileSystemAttributes"));
         var1.checkRead(this.path);
      }

      return this.isInvalid() ? 0L : fs.getSpace(this, 2);
   }

   public static File createTempFile(String var0, String var1, File var2) throws IOException {
      if (var0.length() < 3) {
         throw new IllegalArgumentException("Prefix string too short");
      } else {
         if (var1 == null) {
            var1 = ".tmp";
         }

         File var3 = var2 != null ? var2 : File.TempDirectory.location();
         SecurityManager var4 = System.getSecurityManager();

         File var5;
         do {
            var5 = File.TempDirectory.generateFile(var0, var1, var3);
            if (var4 != null) {
               try {
                  var4.checkWrite(var5.getPath());
               } catch (SecurityException var7) {
                  if (var2 == null) {
                     throw new SecurityException("Unable to create temporary file");
                  }

                  throw var7;
               }
            }
         } while((fs.getBooleanAttributes(var5) & 1) != 0);

         if (!fs.createFileExclusively(var5.getPath())) {
            throw new IOException("Unable to create temporary file");
         } else {
            return var5;
         }
      }
   }

   public static File createTempFile(String var0, String var1) throws IOException {
      return createTempFile(var0, var1, (File)null);
   }

   public int compareTo(File var1) {
      return fs.compare(this, var1);
   }

   public boolean equals(Object var1) {
      if (var1 != null && var1 instanceof File) {
         return this.compareTo((File)var1) == 0;
      } else {
         return false;
      }
   }

   public int hashCode() {
      return fs.hashCode(this);
   }

   public String toString() {
      return this.getPath();
   }

   private synchronized void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
      var1.writeChar(separatorChar);
   }

   private synchronized void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      ObjectInputStream.GetField var2 = var1.readFields();
      String var3 = (String)var2.get("path", (Object)null);
      char var4 = var1.readChar();
      if (var4 != separatorChar) {
         var3 = var3.replace(var4, separatorChar);
      }

      String var5 = fs.normalize(var3);
      UNSAFE.putObject(this, PATH_OFFSET, var5);
      UNSAFE.putIntVolatile(this, PREFIX_LENGTH_OFFSET, fs.prefixLength(var5));
   }

   public Path toPath() {
      Path var1 = this.filePath;
      if (var1 == null) {
         synchronized(this) {
            var1 = this.filePath;
            if (var1 == null) {
               var1 = FileSystems.getDefault().getPath(this.path);
               this.filePath = var1;
            }
         }
      }

      return var1;
   }

   static {
      separatorChar = fs.getSeparator();
      separator = "" + separatorChar;
      pathSeparatorChar = fs.getPathSeparator();
      pathSeparator = "" + pathSeparatorChar;

      try {
         Unsafe var0 = Unsafe.getUnsafe();
         PATH_OFFSET = var0.objectFieldOffset(File.class.getDeclaredField("path"));
         PREFIX_LENGTH_OFFSET = var0.objectFieldOffset(File.class.getDeclaredField("prefixLength"));
         UNSAFE = var0;
      } catch (ReflectiveOperationException var1) {
         throw new Error(var1);
      }
   }

   private static class TempDirectory {
      private static final File tmpdir = new File((String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("java.io.tmpdir"))));
      private static final SecureRandom random = new SecureRandom();

      static File location() {
         return tmpdir;
      }

      static File generateFile(String var0, String var1, File var2) throws IOException {
         long var3 = random.nextLong();
         if (var3 == Long.MIN_VALUE) {
            var3 = 0L;
         } else {
            var3 = Math.abs(var3);
         }

         var0 = (new File(var0)).getName();
         String var5 = var0 + Long.toString(var3) + var1;
         File var6 = new File(var2, var5);
         if (var5.equals(var6.getName()) && !var6.isInvalid()) {
            return var6;
         } else if (System.getSecurityManager() != null) {
            throw new IOException("Unable to create temporary file");
         } else {
            throw new IOException("Unable to create temporary file, " + var6);
         }
      }
   }

   private static enum PathStatus {
      INVALID,
      CHECKED;
   }
}
