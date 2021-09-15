package sun.nio.fs;

import java.io.IOException;
import java.nio.file.ProviderMismatchException;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.nio.file.attribute.GroupPrincipal;
import java.nio.file.attribute.PosixFileAttributeView;
import java.nio.file.attribute.PosixFileAttributes;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.UserPrincipal;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

class UnixFileAttributeViews {
   static UnixFileAttributeViews.Basic createBasicView(UnixPath var0, boolean var1) {
      return new UnixFileAttributeViews.Basic(var0, var1);
   }

   static UnixFileAttributeViews.Posix createPosixView(UnixPath var0, boolean var1) {
      return new UnixFileAttributeViews.Posix(var0, var1);
   }

   static UnixFileAttributeViews.Unix createUnixView(UnixPath var0, boolean var1) {
      return new UnixFileAttributeViews.Unix(var0, var1);
   }

   static FileOwnerAttributeViewImpl createOwnerView(UnixPath var0, boolean var1) {
      return new FileOwnerAttributeViewImpl(createPosixView(var0, var1));
   }

   private static class Unix extends UnixFileAttributeViews.Posix {
      private static final String MODE_NAME = "mode";
      private static final String INO_NAME = "ino";
      private static final String DEV_NAME = "dev";
      private static final String RDEV_NAME = "rdev";
      private static final String NLINK_NAME = "nlink";
      private static final String UID_NAME = "uid";
      private static final String GID_NAME = "gid";
      private static final String CTIME_NAME = "ctime";
      static final Set<String> unixAttributeNames;

      Unix(UnixPath var1, boolean var2) {
         super(var1, var2);
      }

      public String name() {
         return "unix";
      }

      public void setAttribute(String var1, Object var2) throws IOException {
         if (var1.equals("mode")) {
            this.setMode((Integer)var2);
         } else if (var1.equals("uid")) {
            this.setOwners((Integer)var2, -1);
         } else if (var1.equals("gid")) {
            this.setOwners(-1, (Integer)var2);
         } else {
            super.setAttribute(var1, var2);
         }
      }

      public Map<String, Object> readAttributes(String[] var1) throws IOException {
         AbstractBasicFileAttributeView.AttributesBuilder var2 = AbstractBasicFileAttributeView.AttributesBuilder.create(unixAttributeNames, var1);
         UnixFileAttributes var3 = this.readAttributes();
         this.addRequestedPosixAttributes(var3, var2);
         if (var2.match("mode")) {
            var2.add("mode", var3.mode());
         }

         if (var2.match("ino")) {
            var2.add("ino", var3.ino());
         }

         if (var2.match("dev")) {
            var2.add("dev", var3.dev());
         }

         if (var2.match("rdev")) {
            var2.add("rdev", var3.rdev());
         }

         if (var2.match("nlink")) {
            var2.add("nlink", var3.nlink());
         }

         if (var2.match("uid")) {
            var2.add("uid", var3.uid());
         }

         if (var2.match("gid")) {
            var2.add("gid", var3.gid());
         }

         if (var2.match("ctime")) {
            var2.add("ctime", var3.ctime());
         }

         return var2.unmodifiableMap();
      }

      static {
         unixAttributeNames = Util.newSet(posixAttributeNames, "mode", "ino", "dev", "rdev", "nlink", "uid", "gid", "ctime");
      }
   }

   private static class Posix extends UnixFileAttributeViews.Basic implements PosixFileAttributeView {
      private static final String PERMISSIONS_NAME = "permissions";
      private static final String OWNER_NAME = "owner";
      private static final String GROUP_NAME = "group";
      static final Set<String> posixAttributeNames;

      Posix(UnixPath var1, boolean var2) {
         super(var1, var2);
      }

      final void checkReadExtended() {
         SecurityManager var1 = System.getSecurityManager();
         if (var1 != null) {
            this.file.checkRead();
            var1.checkPermission(new RuntimePermission("accessUserInformation"));
         }

      }

      final void checkWriteExtended() {
         SecurityManager var1 = System.getSecurityManager();
         if (var1 != null) {
            this.file.checkWrite();
            var1.checkPermission(new RuntimePermission("accessUserInformation"));
         }

      }

      public String name() {
         return "posix";
      }

      public void setAttribute(String var1, Object var2) throws IOException {
         if (var1.equals("permissions")) {
            this.setPermissions((Set)var2);
         } else if (var1.equals("owner")) {
            this.setOwner((UserPrincipal)var2);
         } else if (var1.equals("group")) {
            this.setGroup((GroupPrincipal)var2);
         } else {
            super.setAttribute(var1, var2);
         }
      }

      final void addRequestedPosixAttributes(PosixFileAttributes var1, AbstractBasicFileAttributeView.AttributesBuilder var2) {
         this.addRequestedBasicAttributes(var1, var2);
         if (var2.match("permissions")) {
            var2.add("permissions", var1.permissions());
         }

         if (var2.match("owner")) {
            var2.add("owner", var1.owner());
         }

         if (var2.match("group")) {
            var2.add("group", var1.group());
         }

      }

      public Map<String, Object> readAttributes(String[] var1) throws IOException {
         AbstractBasicFileAttributeView.AttributesBuilder var2 = AbstractBasicFileAttributeView.AttributesBuilder.create(posixAttributeNames, var1);
         UnixFileAttributes var3 = this.readAttributes();
         this.addRequestedPosixAttributes(var3, var2);
         return var2.unmodifiableMap();
      }

      public UnixFileAttributes readAttributes() throws IOException {
         this.checkReadExtended();

         try {
            return UnixFileAttributes.get(this.file, this.followLinks);
         } catch (UnixException var2) {
            var2.rethrowAsIOException(this.file);
            return null;
         }
      }

      final void setMode(int var1) throws IOException {
         this.checkWriteExtended();

         try {
            if (this.followLinks) {
               UnixNativeDispatcher.chmod(this.file, var1);
            } else {
               int var2 = this.file.openForAttributeAccess(false);

               try {
                  UnixNativeDispatcher.fchmod(var2, var1);
               } finally {
                  UnixNativeDispatcher.close(var2);
               }
            }
         } catch (UnixException var7) {
            var7.rethrowAsIOException(this.file);
         }

      }

      final void setOwners(int var1, int var2) throws IOException {
         this.checkWriteExtended();

         try {
            if (this.followLinks) {
               UnixNativeDispatcher.chown(this.file, var1, var2);
            } else {
               UnixNativeDispatcher.lchown(this.file, var1, var2);
            }
         } catch (UnixException var4) {
            var4.rethrowAsIOException(this.file);
         }

      }

      public void setPermissions(Set<PosixFilePermission> var1) throws IOException {
         this.setMode(UnixFileModeAttribute.toUnixMode(var1));
      }

      public void setOwner(UserPrincipal var1) throws IOException {
         if (var1 == null) {
            throw new NullPointerException("'owner' is null");
         } else if (!(var1 instanceof UnixUserPrincipals.User)) {
            throw new ProviderMismatchException();
         } else if (var1 instanceof UnixUserPrincipals.Group) {
            throw new IOException("'owner' parameter can't be a group");
         } else {
            int var2 = ((UnixUserPrincipals.User)var1).uid();
            this.setOwners(var2, -1);
         }
      }

      public UserPrincipal getOwner() throws IOException {
         return this.readAttributes().owner();
      }

      public void setGroup(GroupPrincipal var1) throws IOException {
         if (var1 == null) {
            throw new NullPointerException("'owner' is null");
         } else if (!(var1 instanceof UnixUserPrincipals.Group)) {
            throw new ProviderMismatchException();
         } else {
            int var2 = ((UnixUserPrincipals.Group)var1).gid();
            this.setOwners(-1, var2);
         }
      }

      static {
         posixAttributeNames = Util.newSet(basicAttributeNames, "permissions", "owner", "group");
      }
   }

   static class Basic extends AbstractBasicFileAttributeView {
      protected final UnixPath file;
      protected final boolean followLinks;

      Basic(UnixPath var1, boolean var2) {
         this.file = var1;
         this.followLinks = var2;
      }

      public BasicFileAttributes readAttributes() throws IOException {
         this.file.checkRead();

         try {
            UnixFileAttributes var1 = UnixFileAttributes.get(this.file, this.followLinks);
            return var1.asBasicFileAttributes();
         } catch (UnixException var2) {
            var2.rethrowAsIOException(this.file);
            return null;
         }
      }

      public void setTimes(FileTime var1, FileTime var2, FileTime var3) throws IOException {
         if (var1 != null || var2 != null) {
            this.file.checkWrite();
            int var4 = this.file.openForAttributeAccess(this.followLinks);

            try {
               if (var1 == null || var2 == null) {
                  try {
                     UnixFileAttributes var5 = UnixFileAttributes.get(var4);
                     if (var1 == null) {
                        var1 = var5.lastModifiedTime();
                     }

                     if (var2 == null) {
                        var2 = var5.lastAccessTime();
                     }
                  } catch (UnixException var17) {
                     var17.rethrowAsIOException(this.file);
                  }
               }

               long var20 = var1.to(TimeUnit.MICROSECONDS);
               long var7 = var2.to(TimeUnit.MICROSECONDS);
               boolean var9 = false;

               try {
                  if (UnixNativeDispatcher.futimesSupported()) {
                     UnixNativeDispatcher.futimes(var4, var7, var20);
                  } else {
                     UnixNativeDispatcher.utimes(this.file, var7, var20);
                  }
               } catch (UnixException var18) {
                  if (var18.errno() != 22 || var20 >= 0L && var7 >= 0L) {
                     var18.rethrowAsIOException(this.file);
                  } else {
                     var9 = true;
                  }
               }

               if (var9) {
                  if (var20 < 0L) {
                     var20 = 0L;
                  }

                  if (var7 < 0L) {
                     var7 = 0L;
                  }

                  try {
                     if (UnixNativeDispatcher.futimesSupported()) {
                        UnixNativeDispatcher.futimes(var4, var7, var20);
                     } else {
                        UnixNativeDispatcher.utimes(this.file, var7, var20);
                     }
                  } catch (UnixException var16) {
                     var16.rethrowAsIOException(this.file);
                  }
               }
            } finally {
               UnixNativeDispatcher.close(var4);
            }

         }
      }
   }
}
