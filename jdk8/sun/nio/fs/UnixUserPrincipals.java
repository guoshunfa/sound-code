package sun.nio.fs;

import java.io.IOException;
import java.nio.file.attribute.GroupPrincipal;
import java.nio.file.attribute.UserPrincipal;
import java.nio.file.attribute.UserPrincipalNotFoundException;

class UnixUserPrincipals {
   static final UnixUserPrincipals.User SPECIAL_OWNER = createSpecial("OWNER@");
   static final UnixUserPrincipals.User SPECIAL_GROUP = createSpecial("GROUP@");
   static final UnixUserPrincipals.User SPECIAL_EVERYONE = createSpecial("EVERYONE@");

   private static UnixUserPrincipals.User createSpecial(String var0) {
      return new UnixUserPrincipals.User(-1, var0);
   }

   static UnixUserPrincipals.User fromUid(int var0) {
      String var1 = null;

      try {
         var1 = Util.toString(UnixNativeDispatcher.getpwuid(var0));
      } catch (UnixException var3) {
         var1 = Integer.toString(var0);
      }

      return new UnixUserPrincipals.User(var0, var1);
   }

   static UnixUserPrincipals.Group fromGid(int var0) {
      String var1 = null;

      try {
         var1 = Util.toString(UnixNativeDispatcher.getgrgid(var0));
      } catch (UnixException var3) {
         var1 = Integer.toString(var0);
      }

      return new UnixUserPrincipals.Group(var0, var1);
   }

   private static int lookupName(String var0, boolean var1) throws IOException {
      SecurityManager var2 = System.getSecurityManager();
      if (var2 != null) {
         var2.checkPermission(new RuntimePermission("lookupUserInformation"));
      }

      boolean var3 = true;

      int var7;
      try {
         var7 = var1 ? UnixNativeDispatcher.getgrnam(var0) : UnixNativeDispatcher.getpwnam(var0);
      } catch (UnixException var6) {
         throw new IOException(var0 + ": " + var6.errorString());
      }

      if (var7 == -1) {
         try {
            var7 = Integer.parseInt(var0);
         } catch (NumberFormatException var5) {
            throw new UserPrincipalNotFoundException(var0);
         }
      }

      return var7;
   }

   static UserPrincipal lookupUser(String var0) throws IOException {
      if (var0.equals(SPECIAL_OWNER.getName())) {
         return SPECIAL_OWNER;
      } else if (var0.equals(SPECIAL_GROUP.getName())) {
         return SPECIAL_GROUP;
      } else if (var0.equals(SPECIAL_EVERYONE.getName())) {
         return SPECIAL_EVERYONE;
      } else {
         int var1 = lookupName(var0, false);
         return new UnixUserPrincipals.User(var1, var0);
      }
   }

   static GroupPrincipal lookupGroup(String var0) throws IOException {
      int var1 = lookupName(var0, true);
      return new UnixUserPrincipals.Group(var1, var0);
   }

   static class Group extends UnixUserPrincipals.User implements GroupPrincipal {
      Group(int var1, String var2) {
         super(var1, true, var2, null);
      }
   }

   static class User implements UserPrincipal {
      private final int id;
      private final boolean isGroup;
      private final String name;

      private User(int var1, boolean var2, String var3) {
         this.id = var1;
         this.isGroup = var2;
         this.name = var3;
      }

      User(int var1, String var2) {
         this(var1, false, var2);
      }

      int uid() {
         if (this.isGroup) {
            throw new AssertionError();
         } else {
            return this.id;
         }
      }

      int gid() {
         if (this.isGroup) {
            return this.id;
         } else {
            throw new AssertionError();
         }
      }

      boolean isSpecial() {
         return this.id == -1;
      }

      public String getName() {
         return this.name;
      }

      public String toString() {
         return this.name;
      }

      public boolean equals(Object var1) {
         if (var1 == this) {
            return true;
         } else if (!(var1 instanceof UnixUserPrincipals.User)) {
            return false;
         } else {
            UnixUserPrincipals.User var2 = (UnixUserPrincipals.User)var1;
            if (this.id == var2.id && this.isGroup == var2.isGroup) {
               return this.id == -1 && var2.id == -1 ? this.name.equals(var2.name) : true;
            } else {
               return false;
            }
         }
      }

      public int hashCode() {
         return this.id != -1 ? this.id : this.name.hashCode();
      }

      // $FF: synthetic method
      User(int var1, boolean var2, String var3, Object var4) {
         this(var1, var2, var3);
      }
   }
}
