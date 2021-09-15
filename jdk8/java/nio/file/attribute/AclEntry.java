package java.nio.file.attribute;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public final class AclEntry {
   private final AclEntryType type;
   private final UserPrincipal who;
   private final Set<AclEntryPermission> perms;
   private final Set<AclEntryFlag> flags;
   private volatile int hash;

   private AclEntry(AclEntryType var1, UserPrincipal var2, Set<AclEntryPermission> var3, Set<AclEntryFlag> var4) {
      this.type = var1;
      this.who = var2;
      this.perms = var3;
      this.flags = var4;
   }

   public static AclEntry.Builder newBuilder() {
      Set var0 = Collections.emptySet();
      Set var1 = Collections.emptySet();
      return new AclEntry.Builder((AclEntryType)null, (UserPrincipal)null, var0, var1);
   }

   public static AclEntry.Builder newBuilder(AclEntry var0) {
      return new AclEntry.Builder(var0.type, var0.who, var0.perms, var0.flags);
   }

   public AclEntryType type() {
      return this.type;
   }

   public UserPrincipal principal() {
      return this.who;
   }

   public Set<AclEntryPermission> permissions() {
      return new HashSet(this.perms);
   }

   public Set<AclEntryFlag> flags() {
      return new HashSet(this.flags);
   }

   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (var1 != null && var1 instanceof AclEntry) {
         AclEntry var2 = (AclEntry)var1;
         if (this.type != var2.type) {
            return false;
         } else if (!this.who.equals(var2.who)) {
            return false;
         } else if (!this.perms.equals(var2.perms)) {
            return false;
         } else {
            return this.flags.equals(var2.flags);
         }
      } else {
         return false;
      }
   }

   private static int hash(int var0, Object var1) {
      return var0 * 127 + var1.hashCode();
   }

   public int hashCode() {
      if (this.hash != 0) {
         return this.hash;
      } else {
         int var1 = this.type.hashCode();
         var1 = hash(var1, this.who);
         var1 = hash(var1, this.perms);
         var1 = hash(var1, this.flags);
         this.hash = var1;
         return this.hash;
      }
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();
      var1.append(this.who.getName());
      var1.append(':');
      Iterator var2 = this.perms.iterator();

      while(var2.hasNext()) {
         AclEntryPermission var3 = (AclEntryPermission)var2.next();
         var1.append(var3.name());
         var1.append('/');
      }

      var1.setLength(var1.length() - 1);
      var1.append(':');
      if (!this.flags.isEmpty()) {
         var2 = this.flags.iterator();

         while(var2.hasNext()) {
            AclEntryFlag var4 = (AclEntryFlag)var2.next();
            var1.append(var4.name());
            var1.append('/');
         }

         var1.setLength(var1.length() - 1);
         var1.append(':');
      }

      var1.append(this.type.name());
      return var1.toString();
   }

   // $FF: synthetic method
   AclEntry(AclEntryType var1, UserPrincipal var2, Set var3, Set var4, Object var5) {
      this(var1, var2, var3, var4);
   }

   public static final class Builder {
      private AclEntryType type;
      private UserPrincipal who;
      private Set<AclEntryPermission> perms;
      private Set<AclEntryFlag> flags;

      private Builder(AclEntryType var1, UserPrincipal var2, Set<AclEntryPermission> var3, Set<AclEntryFlag> var4) {
         assert var3 != null && var4 != null;

         this.type = var1;
         this.who = var2;
         this.perms = var3;
         this.flags = var4;
      }

      public AclEntry build() {
         if (this.type == null) {
            throw new IllegalStateException("Missing type component");
         } else if (this.who == null) {
            throw new IllegalStateException("Missing who component");
         } else {
            return new AclEntry(this.type, this.who, this.perms, this.flags);
         }
      }

      public AclEntry.Builder setType(AclEntryType var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            this.type = var1;
            return this;
         }
      }

      public AclEntry.Builder setPrincipal(UserPrincipal var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            this.who = var1;
            return this;
         }
      }

      private static void checkSet(Set<?> var0, Class<?> var1) {
         Iterator var2 = var0.iterator();

         while(var2.hasNext()) {
            Object var3 = var2.next();
            if (var3 == null) {
               throw new NullPointerException();
            }

            var1.cast(var3);
         }

      }

      public AclEntry.Builder setPermissions(Set<AclEntryPermission> var1) {
         Object var2;
         if (var1.isEmpty()) {
            var2 = Collections.emptySet();
         } else {
            var2 = EnumSet.copyOf((Collection)var1);
            checkSet((Set)var2, AclEntryPermission.class);
         }

         this.perms = (Set)var2;
         return this;
      }

      public AclEntry.Builder setPermissions(AclEntryPermission... var1) {
         EnumSet var2 = EnumSet.noneOf(AclEntryPermission.class);
         AclEntryPermission[] var3 = var1;
         int var4 = var1.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            AclEntryPermission var6 = var3[var5];
            if (var6 == null) {
               throw new NullPointerException();
            }

            var2.add(var6);
         }

         this.perms = var2;
         return this;
      }

      public AclEntry.Builder setFlags(Set<AclEntryFlag> var1) {
         Object var2;
         if (var1.isEmpty()) {
            var2 = Collections.emptySet();
         } else {
            var2 = EnumSet.copyOf((Collection)var1);
            checkSet((Set)var2, AclEntryFlag.class);
         }

         this.flags = (Set)var2;
         return this;
      }

      public AclEntry.Builder setFlags(AclEntryFlag... var1) {
         EnumSet var2 = EnumSet.noneOf(AclEntryFlag.class);
         AclEntryFlag[] var3 = var1;
         int var4 = var1.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            AclEntryFlag var6 = var3[var5];
            if (var6 == null) {
               throw new NullPointerException();
            }

            var2.add(var6);
         }

         this.flags = var2;
         return this;
      }

      // $FF: synthetic method
      Builder(AclEntryType var1, UserPrincipal var2, Set var3, Set var4, Object var5) {
         this(var1, var2, var3, var4);
      }
   }
}
