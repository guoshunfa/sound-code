package javax.management;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.security.BasicPermission;
import java.security.Permission;
import java.security.PermissionCollection;
import java.util.StringTokenizer;

public class MBeanServerPermission extends BasicPermission {
   private static final long serialVersionUID = -5661980843569388590L;
   private static final int CREATE = 0;
   private static final int FIND = 1;
   private static final int NEW = 2;
   private static final int RELEASE = 3;
   private static final int N_NAMES = 4;
   private static final String[] names = new String[]{"createMBeanServer", "findMBeanServer", "newMBeanServer", "releaseMBeanServer"};
   private static final int CREATE_MASK = 1;
   private static final int FIND_MASK = 2;
   private static final int NEW_MASK = 4;
   private static final int RELEASE_MASK = 8;
   private static final int ALL_MASK = 15;
   private static final String[] canonicalNames = new String[16];
   transient int mask;

   public MBeanServerPermission(String var1) {
      this(var1, (String)null);
   }

   public MBeanServerPermission(String var1, String var2) {
      super(getCanonicalName(parseMask(var1)), var2);
      this.mask = parseMask(var1);
      if (var2 != null && var2.length() > 0) {
         throw new IllegalArgumentException("MBeanServerPermission actions must be null: " + var2);
      }
   }

   MBeanServerPermission(int var1) {
      super(getCanonicalName(var1));
      this.mask = impliedMask(var1);
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      this.mask = parseMask(this.getName());
   }

   static int simplifyMask(int var0) {
      if ((var0 & 1) != 0) {
         var0 &= -5;
      }

      return var0;
   }

   static int impliedMask(int var0) {
      if ((var0 & 1) != 0) {
         var0 |= 4;
      }

      return var0;
   }

   static String getCanonicalName(int var0) {
      if (var0 == 15) {
         return "*";
      } else {
         var0 = simplifyMask(var0);
         synchronized(canonicalNames) {
            if (canonicalNames[var0] == null) {
               canonicalNames[var0] = makeCanonicalName(var0);
            }
         }

         return canonicalNames[var0];
      }
   }

   private static String makeCanonicalName(int var0) {
      StringBuilder var1 = new StringBuilder();

      for(int var2 = 0; var2 < 4; ++var2) {
         if ((var0 & 1 << var2) != 0) {
            if (var1.length() > 0) {
               var1.append(',');
            }

            var1.append(names[var2]);
         }
      }

      return var1.toString().intern();
   }

   private static int parseMask(String var0) {
      if (var0 == null) {
         throw new NullPointerException("MBeanServerPermission: target name can't be null");
      } else {
         var0 = var0.trim();
         if (var0.equals("*")) {
            return 15;
         } else if (var0.indexOf(44) < 0) {
            return impliedMask(1 << nameIndex(var0.trim()));
         } else {
            int var1 = 0;

            int var4;
            for(StringTokenizer var2 = new StringTokenizer(var0, ","); var2.hasMoreTokens(); var1 |= 1 << var4) {
               String var3 = var2.nextToken();
               var4 = nameIndex(var3.trim());
            }

            return impliedMask(var1);
         }
      }
   }

   private static int nameIndex(String var0) throws IllegalArgumentException {
      for(int var1 = 0; var1 < 4; ++var1) {
         if (names[var1].equals(var0)) {
            return var1;
         }
      }

      String var2 = "Invalid MBeanServerPermission name: \"" + var0 + "\"";
      throw new IllegalArgumentException(var2);
   }

   public int hashCode() {
      return this.mask;
   }

   public boolean implies(Permission var1) {
      if (!(var1 instanceof MBeanServerPermission)) {
         return false;
      } else {
         MBeanServerPermission var2 = (MBeanServerPermission)var1;
         return (this.mask & var2.mask) == var2.mask;
      }
   }

   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof MBeanServerPermission)) {
         return false;
      } else {
         MBeanServerPermission var2 = (MBeanServerPermission)var1;
         return this.mask == var2.mask;
      }
   }

   public PermissionCollection newPermissionCollection() {
      return new MBeanServerPermissionCollection();
   }
}
