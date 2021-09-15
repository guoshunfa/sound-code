package javax.management;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.security.Permission;

public class MBeanPermission extends Permission {
   private static final long serialVersionUID = -2416928705275160661L;
   private static final int AddNotificationListener = 1;
   private static final int GetAttribute = 2;
   private static final int GetClassLoader = 4;
   private static final int GetClassLoaderFor = 8;
   private static final int GetClassLoaderRepository = 16;
   private static final int GetDomains = 32;
   private static final int GetMBeanInfo = 64;
   private static final int GetObjectInstance = 128;
   private static final int Instantiate = 256;
   private static final int Invoke = 512;
   private static final int IsInstanceOf = 1024;
   private static final int QueryMBeans = 2048;
   private static final int QueryNames = 4096;
   private static final int RegisterMBean = 8192;
   private static final int RemoveNotificationListener = 16384;
   private static final int SetAttribute = 32768;
   private static final int UnregisterMBean = 65536;
   private static final int NONE = 0;
   private static final int ALL = 131071;
   private String actions;
   private transient int mask;
   private transient String classNamePrefix;
   private transient boolean classNameExactMatch;
   private transient String member;
   private transient ObjectName objectName;

   private void parseActions() {
      if (this.actions == null) {
         throw new IllegalArgumentException("MBeanPermission: actions can't be null");
      } else if (this.actions.equals("")) {
         throw new IllegalArgumentException("MBeanPermission: actions can't be empty");
      } else {
         int var1 = getMask(this.actions);
         if ((var1 & 131071) != var1) {
            throw new IllegalArgumentException("Invalid actions mask");
         } else if (var1 == 0) {
            throw new IllegalArgumentException("Invalid actions mask");
         } else {
            this.mask = var1;
         }
      }
   }

   private void parseName() {
      String var1 = this.getName();
      if (var1 == null) {
         throw new IllegalArgumentException("MBeanPermission name cannot be null");
      } else if (var1.equals("")) {
         throw new IllegalArgumentException("MBeanPermission name cannot be empty");
      } else {
         int var2 = var1.indexOf("[");
         if (var2 == -1) {
            this.objectName = ObjectName.WILDCARD;
         } else {
            if (!var1.endsWith("]")) {
               throw new IllegalArgumentException("MBeanPermission: The ObjectName in the target name must be included in square brackets");
            }

            try {
               String var3 = var1.substring(var2 + 1, var1.length() - 1);
               if (var3.equals("")) {
                  this.objectName = ObjectName.WILDCARD;
               } else if (var3.equals("-")) {
                  this.objectName = null;
               } else {
                  this.objectName = new ObjectName(var3);
               }
            } catch (MalformedObjectNameException var5) {
               throw new IllegalArgumentException("MBeanPermission: The target name does not specify a valid ObjectName", var5);
            }

            var1 = var1.substring(0, var2);
         }

         int var6 = var1.indexOf("#");
         if (var6 == -1) {
            this.setMember("*");
         } else {
            String var4 = var1.substring(var6 + 1);
            this.setMember(var4);
            var1 = var1.substring(0, var6);
         }

         this.setClassName(var1);
      }
   }

   private void initName(String var1, String var2, ObjectName var3) {
      this.setClassName(var1);
      this.setMember(var2);
      this.objectName = var3;
   }

   private void setClassName(String var1) {
      if (var1 != null && !var1.equals("-")) {
         if (!var1.equals("") && !var1.equals("*")) {
            if (var1.endsWith(".*")) {
               this.classNamePrefix = var1.substring(0, var1.length() - 1);
               this.classNameExactMatch = false;
            } else {
               this.classNamePrefix = var1;
               this.classNameExactMatch = true;
            }
         } else {
            this.classNamePrefix = "";
            this.classNameExactMatch = false;
         }
      } else {
         this.classNamePrefix = null;
         this.classNameExactMatch = false;
      }

   }

   private void setMember(String var1) {
      if (var1 != null && !var1.equals("-")) {
         if (var1.equals("")) {
            this.member = "*";
         } else {
            this.member = var1;
         }
      } else {
         this.member = null;
      }

   }

   public MBeanPermission(String var1, String var2) {
      super(var1);
      this.parseName();
      this.actions = var2;
      this.parseActions();
   }

   public MBeanPermission(String var1, String var2, ObjectName var3, String var4) {
      super(makeName(var1, var2, var3));
      this.initName(var1, var2, var3);
      this.actions = var4;
      this.parseActions();
   }

   private static String makeName(String var0, String var1, ObjectName var2) {
      StringBuilder var3 = new StringBuilder();
      if (var0 == null) {
         var0 = "-";
      }

      var3.append(var0);
      if (var1 == null) {
         var1 = "-";
      }

      var3.append("#" + var1);
      if (var2 == null) {
         var3.append("[-]");
      } else {
         var3.append("[").append(var2.getCanonicalName()).append("]");
      }

      return var3.length() == 0 ? "*" : var3.toString();
   }

   public String getActions() {
      if (this.actions == null) {
         this.actions = getActions(this.mask);
      }

      return this.actions;
   }

   private static String getActions(int var0) {
      StringBuilder var1 = new StringBuilder();
      boolean var2 = false;
      if ((var0 & 1) == 1) {
         var2 = true;
         var1.append("addNotificationListener");
      }

      if ((var0 & 2) == 2) {
         if (var2) {
            var1.append(',');
         } else {
            var2 = true;
         }

         var1.append("getAttribute");
      }

      if ((var0 & 4) == 4) {
         if (var2) {
            var1.append(',');
         } else {
            var2 = true;
         }

         var1.append("getClassLoader");
      }

      if ((var0 & 8) == 8) {
         if (var2) {
            var1.append(',');
         } else {
            var2 = true;
         }

         var1.append("getClassLoaderFor");
      }

      if ((var0 & 16) == 16) {
         if (var2) {
            var1.append(',');
         } else {
            var2 = true;
         }

         var1.append("getClassLoaderRepository");
      }

      if ((var0 & 32) == 32) {
         if (var2) {
            var1.append(',');
         } else {
            var2 = true;
         }

         var1.append("getDomains");
      }

      if ((var0 & 64) == 64) {
         if (var2) {
            var1.append(',');
         } else {
            var2 = true;
         }

         var1.append("getMBeanInfo");
      }

      if ((var0 & 128) == 128) {
         if (var2) {
            var1.append(',');
         } else {
            var2 = true;
         }

         var1.append("getObjectInstance");
      }

      if ((var0 & 256) == 256) {
         if (var2) {
            var1.append(',');
         } else {
            var2 = true;
         }

         var1.append("instantiate");
      }

      if ((var0 & 512) == 512) {
         if (var2) {
            var1.append(',');
         } else {
            var2 = true;
         }

         var1.append("invoke");
      }

      if ((var0 & 1024) == 1024) {
         if (var2) {
            var1.append(',');
         } else {
            var2 = true;
         }

         var1.append("isInstanceOf");
      }

      if ((var0 & 2048) == 2048) {
         if (var2) {
            var1.append(',');
         } else {
            var2 = true;
         }

         var1.append("queryMBeans");
      }

      if ((var0 & 4096) == 4096) {
         if (var2) {
            var1.append(',');
         } else {
            var2 = true;
         }

         var1.append("queryNames");
      }

      if ((var0 & 8192) == 8192) {
         if (var2) {
            var1.append(',');
         } else {
            var2 = true;
         }

         var1.append("registerMBean");
      }

      if ((var0 & 16384) == 16384) {
         if (var2) {
            var1.append(',');
         } else {
            var2 = true;
         }

         var1.append("removeNotificationListener");
      }

      if ((var0 & '耀') == 32768) {
         if (var2) {
            var1.append(',');
         } else {
            var2 = true;
         }

         var1.append("setAttribute");
      }

      if ((var0 & 65536) == 65536) {
         if (var2) {
            var1.append(',');
         } else {
            var2 = true;
         }

         var1.append("unregisterMBean");
      }

      return var1.toString();
   }

   public int hashCode() {
      return this.getName().hashCode() + this.getActions().hashCode();
   }

   private static int getMask(String var0) {
      int var1 = 0;
      if (var0 == null) {
         return var1;
      } else if (var0.equals("*")) {
         return 131071;
      } else {
         char[] var2 = var0.toCharArray();
         int var3 = var2.length - 1;
         if (var3 < 0) {
            return var1;
         } else {
            while(var3 != -1) {
               char var4;
               while(var3 != -1 && ((var4 = var2[var3]) == ' ' || var4 == '\r' || var4 == '\n' || var4 == '\f' || var4 == '\t')) {
                  --var3;
               }

               byte var5;
               if (var3 >= 25 && var2[var3 - 25] == 'r' && var2[var3 - 24] == 'e' && var2[var3 - 23] == 'm' && var2[var3 - 22] == 'o' && var2[var3 - 21] == 'v' && var2[var3 - 20] == 'e' && var2[var3 - 19] == 'N' && var2[var3 - 18] == 'o' && var2[var3 - 17] == 't' && var2[var3 - 16] == 'i' && var2[var3 - 15] == 'f' && var2[var3 - 14] == 'i' && var2[var3 - 13] == 'c' && var2[var3 - 12] == 'a' && var2[var3 - 11] == 't' && var2[var3 - 10] == 'i' && var2[var3 - 9] == 'o' && var2[var3 - 8] == 'n' && var2[var3 - 7] == 'L' && var2[var3 - 6] == 'i' && var2[var3 - 5] == 's' && var2[var3 - 4] == 't' && var2[var3 - 3] == 'e' && var2[var3 - 2] == 'n' && var2[var3 - 1] == 'e' && var2[var3] == 'r') {
                  var5 = 26;
                  var1 |= 16384;
               } else if (var3 >= 23 && var2[var3 - 23] == 'g' && var2[var3 - 22] == 'e' && var2[var3 - 21] == 't' && var2[var3 - 20] == 'C' && var2[var3 - 19] == 'l' && var2[var3 - 18] == 'a' && var2[var3 - 17] == 's' && var2[var3 - 16] == 's' && var2[var3 - 15] == 'L' && var2[var3 - 14] == 'o' && var2[var3 - 13] == 'a' && var2[var3 - 12] == 'd' && var2[var3 - 11] == 'e' && var2[var3 - 10] == 'r' && var2[var3 - 9] == 'R' && var2[var3 - 8] == 'e' && var2[var3 - 7] == 'p' && var2[var3 - 6] == 'o' && var2[var3 - 5] == 's' && var2[var3 - 4] == 'i' && var2[var3 - 3] == 't' && var2[var3 - 2] == 'o' && var2[var3 - 1] == 'r' && var2[var3] == 'y') {
                  var5 = 24;
                  var1 |= 16;
               } else if (var3 >= 22 && var2[var3 - 22] == 'a' && var2[var3 - 21] == 'd' && var2[var3 - 20] == 'd' && var2[var3 - 19] == 'N' && var2[var3 - 18] == 'o' && var2[var3 - 17] == 't' && var2[var3 - 16] == 'i' && var2[var3 - 15] == 'f' && var2[var3 - 14] == 'i' && var2[var3 - 13] == 'c' && var2[var3 - 12] == 'a' && var2[var3 - 11] == 't' && var2[var3 - 10] == 'i' && var2[var3 - 9] == 'o' && var2[var3 - 8] == 'n' && var2[var3 - 7] == 'L' && var2[var3 - 6] == 'i' && var2[var3 - 5] == 's' && var2[var3 - 4] == 't' && var2[var3 - 3] == 'e' && var2[var3 - 2] == 'n' && var2[var3 - 1] == 'e' && var2[var3] == 'r') {
                  var5 = 23;
                  var1 |= 1;
               } else if (var3 >= 16 && var2[var3 - 16] == 'g' && var2[var3 - 15] == 'e' && var2[var3 - 14] == 't' && var2[var3 - 13] == 'C' && var2[var3 - 12] == 'l' && var2[var3 - 11] == 'a' && var2[var3 - 10] == 's' && var2[var3 - 9] == 's' && var2[var3 - 8] == 'L' && var2[var3 - 7] == 'o' && var2[var3 - 6] == 'a' && var2[var3 - 5] == 'd' && var2[var3 - 4] == 'e' && var2[var3 - 3] == 'r' && var2[var3 - 2] == 'F' && var2[var3 - 1] == 'o' && var2[var3] == 'r') {
                  var5 = 17;
                  var1 |= 8;
               } else if (var3 >= 16 && var2[var3 - 16] == 'g' && var2[var3 - 15] == 'e' && var2[var3 - 14] == 't' && var2[var3 - 13] == 'O' && var2[var3 - 12] == 'b' && var2[var3 - 11] == 'j' && var2[var3 - 10] == 'e' && var2[var3 - 9] == 'c' && var2[var3 - 8] == 't' && var2[var3 - 7] == 'I' && var2[var3 - 6] == 'n' && var2[var3 - 5] == 's' && var2[var3 - 4] == 't' && var2[var3 - 3] == 'a' && var2[var3 - 2] == 'n' && var2[var3 - 1] == 'c' && var2[var3] == 'e') {
                  var5 = 17;
                  var1 |= 128;
               } else if (var3 >= 14 && var2[var3 - 14] == 'u' && var2[var3 - 13] == 'n' && var2[var3 - 12] == 'r' && var2[var3 - 11] == 'e' && var2[var3 - 10] == 'g' && var2[var3 - 9] == 'i' && var2[var3 - 8] == 's' && var2[var3 - 7] == 't' && var2[var3 - 6] == 'e' && var2[var3 - 5] == 'r' && var2[var3 - 4] == 'M' && var2[var3 - 3] == 'B' && var2[var3 - 2] == 'e' && var2[var3 - 1] == 'a' && var2[var3] == 'n') {
                  var5 = 15;
                  var1 |= 65536;
               } else if (var3 >= 13 && var2[var3 - 13] == 'g' && var2[var3 - 12] == 'e' && var2[var3 - 11] == 't' && var2[var3 - 10] == 'C' && var2[var3 - 9] == 'l' && var2[var3 - 8] == 'a' && var2[var3 - 7] == 's' && var2[var3 - 6] == 's' && var2[var3 - 5] == 'L' && var2[var3 - 4] == 'o' && var2[var3 - 3] == 'a' && var2[var3 - 2] == 'd' && var2[var3 - 1] == 'e' && var2[var3] == 'r') {
                  var5 = 14;
                  var1 |= 4;
               } else if (var3 >= 12 && var2[var3 - 12] == 'r' && var2[var3 - 11] == 'e' && var2[var3 - 10] == 'g' && var2[var3 - 9] == 'i' && var2[var3 - 8] == 's' && var2[var3 - 7] == 't' && var2[var3 - 6] == 'e' && var2[var3 - 5] == 'r' && var2[var3 - 4] == 'M' && var2[var3 - 3] == 'B' && var2[var3 - 2] == 'e' && var2[var3 - 1] == 'a' && var2[var3] == 'n') {
                  var5 = 13;
                  var1 |= 8192;
               } else if (var3 >= 11 && var2[var3 - 11] == 'g' && var2[var3 - 10] == 'e' && var2[var3 - 9] == 't' && var2[var3 - 8] == 'A' && var2[var3 - 7] == 't' && var2[var3 - 6] == 't' && var2[var3 - 5] == 'r' && var2[var3 - 4] == 'i' && var2[var3 - 3] == 'b' && var2[var3 - 2] == 'u' && var2[var3 - 1] == 't' && var2[var3] == 'e') {
                  var5 = 12;
                  var1 |= 2;
               } else if (var3 >= 11 && var2[var3 - 11] == 'g' && var2[var3 - 10] == 'e' && var2[var3 - 9] == 't' && var2[var3 - 8] == 'M' && var2[var3 - 7] == 'B' && var2[var3 - 6] == 'e' && var2[var3 - 5] == 'a' && var2[var3 - 4] == 'n' && var2[var3 - 3] == 'I' && var2[var3 - 2] == 'n' && var2[var3 - 1] == 'f' && var2[var3] == 'o') {
                  var5 = 12;
                  var1 |= 64;
               } else if (var3 >= 11 && var2[var3 - 11] == 'i' && var2[var3 - 10] == 's' && var2[var3 - 9] == 'I' && var2[var3 - 8] == 'n' && var2[var3 - 7] == 's' && var2[var3 - 6] == 't' && var2[var3 - 5] == 'a' && var2[var3 - 4] == 'n' && var2[var3 - 3] == 'c' && var2[var3 - 2] == 'e' && var2[var3 - 1] == 'O' && var2[var3] == 'f') {
                  var5 = 12;
                  var1 |= 1024;
               } else if (var3 >= 11 && var2[var3 - 11] == 's' && var2[var3 - 10] == 'e' && var2[var3 - 9] == 't' && var2[var3 - 8] == 'A' && var2[var3 - 7] == 't' && var2[var3 - 6] == 't' && var2[var3 - 5] == 'r' && var2[var3 - 4] == 'i' && var2[var3 - 3] == 'b' && var2[var3 - 2] == 'u' && var2[var3 - 1] == 't' && var2[var3] == 'e') {
                  var5 = 12;
                  var1 |= 32768;
               } else if (var3 >= 10 && var2[var3 - 10] == 'i' && var2[var3 - 9] == 'n' && var2[var3 - 8] == 's' && var2[var3 - 7] == 't' && var2[var3 - 6] == 'a' && var2[var3 - 5] == 'n' && var2[var3 - 4] == 't' && var2[var3 - 3] == 'i' && var2[var3 - 2] == 'a' && var2[var3 - 1] == 't' && var2[var3] == 'e') {
                  var5 = 11;
                  var1 |= 256;
               } else if (var3 >= 10 && var2[var3 - 10] == 'q' && var2[var3 - 9] == 'u' && var2[var3 - 8] == 'e' && var2[var3 - 7] == 'r' && var2[var3 - 6] == 'y' && var2[var3 - 5] == 'M' && var2[var3 - 4] == 'B' && var2[var3 - 3] == 'e' && var2[var3 - 2] == 'a' && var2[var3 - 1] == 'n' && var2[var3] == 's') {
                  var5 = 11;
                  var1 |= 2048;
               } else if (var3 >= 9 && var2[var3 - 9] == 'g' && var2[var3 - 8] == 'e' && var2[var3 - 7] == 't' && var2[var3 - 6] == 'D' && var2[var3 - 5] == 'o' && var2[var3 - 4] == 'm' && var2[var3 - 3] == 'a' && var2[var3 - 2] == 'i' && var2[var3 - 1] == 'n' && var2[var3] == 's') {
                  var5 = 10;
                  var1 |= 32;
               } else if (var3 >= 9 && var2[var3 - 9] == 'q' && var2[var3 - 8] == 'u' && var2[var3 - 7] == 'e' && var2[var3 - 6] == 'r' && var2[var3 - 5] == 'y' && var2[var3 - 4] == 'N' && var2[var3 - 3] == 'a' && var2[var3 - 2] == 'm' && var2[var3 - 1] == 'e' && var2[var3] == 's') {
                  var5 = 10;
                  var1 |= 4096;
               } else {
                  if (var3 < 5 || var2[var3 - 5] != 'i' || var2[var3 - 4] != 'n' || var2[var3 - 3] != 'v' || var2[var3 - 2] != 'o' || var2[var3 - 1] != 'k' || var2[var3] != 'e') {
                     throw new IllegalArgumentException("Invalid permission: " + var0);
                  }

                  var5 = 6;
                  var1 |= 512;
               }

               boolean var6 = false;

               while(var3 >= var5 && !var6) {
                  switch(var2[var3 - var5]) {
                  case ',':
                     var6 = true;
                  case '\t':
                  case '\n':
                  case '\f':
                  case '\r':
                  case ' ':
                     --var3;
                     break;
                  default:
                     throw new IllegalArgumentException("Invalid permission: " + var0);
                  }
               }

               var3 -= var5;
            }

            return var1;
         }
      }
   }

   public boolean implies(Permission var1) {
      if (!(var1 instanceof MBeanPermission)) {
         return false;
      } else {
         MBeanPermission var2 = (MBeanPermission)var1;
         if ((this.mask & 2048) == 2048) {
            if (((this.mask | 4096) & var2.mask) != var2.mask) {
               return false;
            }
         } else if ((this.mask & var2.mask) != var2.mask) {
            return false;
         }

         if (var2.classNamePrefix != null) {
            if (this.classNamePrefix == null) {
               return false;
            }

            if (this.classNameExactMatch) {
               if (!var2.classNameExactMatch) {
                  return false;
               }

               if (!var2.classNamePrefix.equals(this.classNamePrefix)) {
                  return false;
               }
            } else if (!var2.classNamePrefix.startsWith(this.classNamePrefix)) {
               return false;
            }
         }

         if (var2.member != null) {
            if (this.member == null) {
               return false;
            }

            if (!this.member.equals("*") && !this.member.equals(var2.member)) {
               return false;
            }
         }

         if (var2.objectName != null) {
            if (this.objectName == null) {
               return false;
            }

            if (!this.objectName.apply(var2.objectName) && !this.objectName.equals(var2.objectName)) {
               return false;
            }
         }

         return true;
      }
   }

   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof MBeanPermission)) {
         return false;
      } else {
         MBeanPermission var2 = (MBeanPermission)var1;
         return this.mask == var2.mask && this.getName().equals(var2.getName());
      }
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      this.parseName();
      this.parseActions();
   }
}
