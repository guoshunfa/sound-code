package sun.nio.fs;

import java.io.IOException;
import java.nio.file.FileStore;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.attribute.GroupPrincipal;
import java.nio.file.attribute.UserPrincipal;
import java.nio.file.attribute.UserPrincipalLookupService;
import java.nio.file.spi.FileSystemProvider;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.regex.Pattern;
import sun.security.action.GetPropertyAction;

abstract class UnixFileSystem extends FileSystem {
   private final UnixFileSystemProvider provider;
   private final byte[] defaultDirectory;
   private final boolean needToResolveAgainstDefaultDirectory;
   private final UnixPath rootDirectory;
   private static final String GLOB_SYNTAX = "glob";
   private static final String REGEX_SYNTAX = "regex";

   UnixFileSystem(UnixFileSystemProvider var1, String var2) {
      this.provider = var1;
      this.defaultDirectory = Util.toBytes(UnixPath.normalizeAndCheck(var2));
      if (this.defaultDirectory[0] != 47) {
         throw new RuntimeException("default directory must be absolute");
      } else {
         String var3 = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("sun.nio.fs.chdirAllowed", "false")));
         boolean var4 = var3.length() == 0 ? true : Boolean.valueOf(var3);
         if (var4) {
            this.needToResolveAgainstDefaultDirectory = true;
         } else {
            byte[] var5 = UnixNativeDispatcher.getcwd();
            boolean var6 = var5.length == this.defaultDirectory.length;
            if (var6) {
               for(int var7 = 0; var7 < var5.length; ++var7) {
                  if (var5[var7] != this.defaultDirectory[var7]) {
                     var6 = false;
                     break;
                  }
               }
            }

            this.needToResolveAgainstDefaultDirectory = !var6;
         }

         this.rootDirectory = new UnixPath(this, "/");
      }
   }

   byte[] defaultDirectory() {
      return this.defaultDirectory;
   }

   boolean needToResolveAgainstDefaultDirectory() {
      return this.needToResolveAgainstDefaultDirectory;
   }

   UnixPath rootDirectory() {
      return this.rootDirectory;
   }

   boolean isSolaris() {
      return false;
   }

   static List<String> standardFileAttributeViews() {
      return Arrays.asList("basic", "posix", "unix", "owner");
   }

   public final FileSystemProvider provider() {
      return this.provider;
   }

   public final String getSeparator() {
      return "/";
   }

   public final boolean isOpen() {
      return true;
   }

   public final boolean isReadOnly() {
      return false;
   }

   public final void close() throws IOException {
      throw new UnsupportedOperationException();
   }

   void copyNonPosixAttributes(int var1, int var2) {
   }

   public final Iterable<Path> getRootDirectories() {
      final List var1 = Collections.unmodifiableList(Arrays.asList(this.rootDirectory));
      return new Iterable<Path>() {
         public Iterator<Path> iterator() {
            try {
               SecurityManager var1x = System.getSecurityManager();
               if (var1x != null) {
                  var1x.checkRead(UnixFileSystem.this.rootDirectory.toString());
               }

               return var1.iterator();
            } catch (SecurityException var3) {
               List var2 = Collections.emptyList();
               return var2.iterator();
            }
         }
      };
   }

   abstract Iterable<UnixMountEntry> getMountEntries();

   abstract FileStore getFileStore(UnixMountEntry var1) throws IOException;

   public final Iterable<FileStore> getFileStores() {
      SecurityManager var1 = System.getSecurityManager();
      if (var1 != null) {
         try {
            var1.checkPermission(new RuntimePermission("getFileStoreAttributes"));
         } catch (SecurityException var3) {
            return Collections.emptyList();
         }
      }

      return new Iterable<FileStore>() {
         public Iterator<FileStore> iterator() {
            return UnixFileSystem.this.new FileStoreIterator();
         }
      };
   }

   public final Path getPath(String var1, String... var2) {
      String var3;
      if (var2.length == 0) {
         var3 = var1;
      } else {
         StringBuilder var4 = new StringBuilder();
         var4.append(var1);
         String[] var5 = var2;
         int var6 = var2.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            String var8 = var5[var7];
            if (var8.length() > 0) {
               if (var4.length() > 0) {
                  var4.append('/');
               }

               var4.append(var8);
            }
         }

         var3 = var4.toString();
      }

      return new UnixPath(this, var3);
   }

   public PathMatcher getPathMatcher(String var1) {
      int var2 = var1.indexOf(58);
      if (var2 > 0 && var2 != var1.length()) {
         String var3 = var1.substring(0, var2);
         String var4 = var1.substring(var2 + 1);
         String var5;
         if (var3.equals("glob")) {
            var5 = Globs.toUnixRegexPattern(var4);
         } else {
            if (!var3.equals("regex")) {
               throw new UnsupportedOperationException("Syntax '" + var3 + "' not recognized");
            }

            var5 = var4;
         }

         final Pattern var6 = this.compilePathMatchPattern(var5);
         return new PathMatcher() {
            public boolean matches(Path var1) {
               return var6.matcher(var1.toString()).matches();
            }
         };
      } else {
         throw new IllegalArgumentException();
      }
   }

   public final UserPrincipalLookupService getUserPrincipalLookupService() {
      return UnixFileSystem.LookupService.instance;
   }

   Pattern compilePathMatchPattern(String var1) {
      return Pattern.compile(var1);
   }

   char[] normalizeNativePath(char[] var1) {
      return var1;
   }

   String normalizeJavaPath(String var1) {
      return var1;
   }

   private static class LookupService {
      static final UserPrincipalLookupService instance = new UserPrincipalLookupService() {
         public UserPrincipal lookupPrincipalByName(String var1) throws IOException {
            return UnixUserPrincipals.lookupUser(var1);
         }

         public GroupPrincipal lookupPrincipalByGroupName(String var1) throws IOException {
            return UnixUserPrincipals.lookupGroup(var1);
         }
      };
   }

   private class FileStoreIterator implements Iterator<FileStore> {
      private final Iterator<UnixMountEntry> entries = UnixFileSystem.this.getMountEntries().iterator();
      private FileStore next;

      FileStoreIterator() {
      }

      private FileStore readNext() {
         assert Thread.holdsLock(this);

         while(true) {
            UnixMountEntry var1;
            while(true) {
               do {
                  if (!this.entries.hasNext()) {
                     return null;
                  }

                  var1 = (UnixMountEntry)this.entries.next();
               } while(var1.isIgnored());

               SecurityManager var2 = System.getSecurityManager();
               if (var2 == null) {
                  break;
               }

               try {
                  var2.checkRead(Util.toString(var1.dir()));
                  break;
               } catch (SecurityException var4) {
               }
            }

            try {
               return UnixFileSystem.this.getFileStore(var1);
            } catch (IOException var5) {
            }
         }
      }

      public synchronized boolean hasNext() {
         if (this.next != null) {
            return true;
         } else {
            this.next = this.readNext();
            return this.next != null;
         }
      }

      public synchronized FileStore next() {
         if (this.next == null) {
            this.next = this.readNext();
         }

         if (this.next == null) {
            throw new NoSuchElementException();
         } else {
            FileStore var1 = this.next;
            this.next = null;
            return var1;
         }
      }

      public void remove() {
         throw new UnsupportedOperationException();
      }
   }
}
