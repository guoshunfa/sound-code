package java.io;

import java.security.AccessController;
import java.security.Permission;
import java.security.PermissionCollection;
import java.security.PrivilegedAction;

public final class FilePermission extends Permission implements Serializable {
   private static final int EXECUTE = 1;
   private static final int WRITE = 2;
   private static final int READ = 4;
   private static final int DELETE = 8;
   private static final int READLINK = 16;
   private static final int ALL = 31;
   private static final int NONE = 0;
   private transient int mask;
   private transient boolean directory;
   private transient boolean recursive;
   private String actions;
   private transient String cpath;
   private static final char RECURSIVE_CHAR = '-';
   private static final char WILD_CHAR = '*';
   private static final long serialVersionUID = 7930732926638008763L;

   private void init(int var1) {
      if ((var1 & 31) != var1) {
         throw new IllegalArgumentException("invalid actions mask");
      } else if (var1 == 0) {
         throw new IllegalArgumentException("invalid actions mask");
      } else if ((this.cpath = this.getName()) == null) {
         throw new NullPointerException("name can't be null");
      } else {
         this.mask = var1;
         if (this.cpath.equals("<<ALL FILES>>")) {
            this.directory = true;
            this.recursive = true;
            this.cpath = "";
         } else {
            this.cpath = (String)AccessController.doPrivileged(new PrivilegedAction<String>() {
               public String run() {
                  try {
                     String var1 = FilePermission.this.cpath;
                     if (FilePermission.this.cpath.endsWith("*")) {
                        var1 = var1.substring(0, var1.length() - 1) + "-";
                        var1 = (new File(var1)).getCanonicalPath();
                        return var1.substring(0, var1.length() - 1) + "*";
                     } else {
                        return (new File(var1)).getCanonicalPath();
                     }
                  } catch (IOException var2) {
                     return FilePermission.this.cpath;
                  }
               }
            });
            int var2 = this.cpath.length();
            char var3 = var2 > 0 ? this.cpath.charAt(var2 - 1) : 0;
            if (var3 == '-' && this.cpath.charAt(var2 - 2) == File.separatorChar) {
               this.directory = true;
               this.recursive = true;
               --var2;
               this.cpath = this.cpath.substring(0, var2);
            } else if (var3 == '*' && this.cpath.charAt(var2 - 2) == File.separatorChar) {
               this.directory = true;
               --var2;
               this.cpath = this.cpath.substring(0, var2);
            }

         }
      }
   }

   public FilePermission(String var1, String var2) {
      super(var1);
      this.init(getMask(var2));
   }

   FilePermission(String var1, int var2) {
      super(var1);
      this.init(var2);
   }

   public boolean implies(Permission var1) {
      if (!(var1 instanceof FilePermission)) {
         return false;
      } else {
         FilePermission var2 = (FilePermission)var1;
         return (this.mask & var2.mask) == var2.mask && this.impliesIgnoreMask(var2);
      }
   }

   boolean impliesIgnoreMask(FilePermission var1) {
      if (!this.directory) {
         return var1.directory ? false : this.cpath.equals(var1.cpath);
      } else if (this.recursive) {
         if (var1.directory) {
            return var1.cpath.length() >= this.cpath.length() && var1.cpath.startsWith(this.cpath);
         } else {
            return var1.cpath.length() > this.cpath.length() && var1.cpath.startsWith(this.cpath);
         }
      } else if (var1.directory) {
         return var1.recursive ? false : this.cpath.equals(var1.cpath);
      } else {
         int var2 = var1.cpath.lastIndexOf(File.separatorChar);
         if (var2 == -1) {
            return false;
         } else {
            return this.cpath.length() == var2 + 1 && this.cpath.regionMatches(0, var1.cpath, 0, var2 + 1);
         }
      }
   }

   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof FilePermission)) {
         return false;
      } else {
         FilePermission var2 = (FilePermission)var1;
         return this.mask == var2.mask && this.cpath.equals(var2.cpath) && this.directory == var2.directory && this.recursive == var2.recursive;
      }
   }

   public int hashCode() {
      return 0;
   }

   private static int getMask(String var0) {
      int var1 = 0;
      if (var0 == null) {
         return var1;
      } else if (var0 == "read") {
         return 4;
      } else if (var0 == "write") {
         return 2;
      } else if (var0 == "execute") {
         return 1;
      } else if (var0 == "delete") {
         return 8;
      } else if (var0 == "readlink") {
         return 16;
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
               if (var3 < 3 || var2[var3 - 3] != 'r' && var2[var3 - 3] != 'R' || var2[var3 - 2] != 'e' && var2[var3 - 2] != 'E' || var2[var3 - 1] != 'a' && var2[var3 - 1] != 'A' || var2[var3] != 'd' && var2[var3] != 'D') {
                  if (var3 >= 4 && (var2[var3 - 4] == 'w' || var2[var3 - 4] == 'W') && (var2[var3 - 3] == 'r' || var2[var3 - 3] == 'R') && (var2[var3 - 2] == 'i' || var2[var3 - 2] == 'I') && (var2[var3 - 1] == 't' || var2[var3 - 1] == 'T') && (var2[var3] == 'e' || var2[var3] == 'E')) {
                     var5 = 5;
                     var1 |= 2;
                  } else if (var3 >= 6 && (var2[var3 - 6] == 'e' || var2[var3 - 6] == 'E') && (var2[var3 - 5] == 'x' || var2[var3 - 5] == 'X') && (var2[var3 - 4] == 'e' || var2[var3 - 4] == 'E') && (var2[var3 - 3] == 'c' || var2[var3 - 3] == 'C') && (var2[var3 - 2] == 'u' || var2[var3 - 2] == 'U') && (var2[var3 - 1] == 't' || var2[var3 - 1] == 'T') && (var2[var3] == 'e' || var2[var3] == 'E')) {
                     var5 = 7;
                     var1 |= 1;
                  } else if (var3 < 5 || var2[var3 - 5] != 'd' && var2[var3 - 5] != 'D' || var2[var3 - 4] != 'e' && var2[var3 - 4] != 'E' || var2[var3 - 3] != 'l' && var2[var3 - 3] != 'L' || var2[var3 - 2] != 'e' && var2[var3 - 2] != 'E' || var2[var3 - 1] != 't' && var2[var3 - 1] != 'T' || var2[var3] != 'e' && var2[var3] != 'E') {
                     if (var3 < 7 || var2[var3 - 7] != 'r' && var2[var3 - 7] != 'R' || var2[var3 - 6] != 'e' && var2[var3 - 6] != 'E' || var2[var3 - 5] != 'a' && var2[var3 - 5] != 'A' || var2[var3 - 4] != 'd' && var2[var3 - 4] != 'D' || var2[var3 - 3] != 'l' && var2[var3 - 3] != 'L' || var2[var3 - 2] != 'i' && var2[var3 - 2] != 'I' || var2[var3 - 1] != 'n' && var2[var3 - 1] != 'N' || var2[var3] != 'k' && var2[var3] != 'K') {
                        throw new IllegalArgumentException("invalid permission: " + var0);
                     }

                     var5 = 8;
                     var1 |= 16;
                  } else {
                     var5 = 6;
                     var1 |= 8;
                  }
               } else {
                  var5 = 4;
                  var1 |= 4;
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
                     throw new IllegalArgumentException("invalid permission: " + var0);
                  }
               }

               var3 -= var5;
            }

            return var1;
         }
      }
   }

   int getMask() {
      return this.mask;
   }

   private static String getActions(int var0) {
      StringBuilder var1 = new StringBuilder();
      boolean var2 = false;
      if ((var0 & 4) == 4) {
         var2 = true;
         var1.append("read");
      }

      if ((var0 & 2) == 2) {
         if (var2) {
            var1.append(',');
         } else {
            var2 = true;
         }

         var1.append("write");
      }

      if ((var0 & 1) == 1) {
         if (var2) {
            var1.append(',');
         } else {
            var2 = true;
         }

         var1.append("execute");
      }

      if ((var0 & 8) == 8) {
         if (var2) {
            var1.append(',');
         } else {
            var2 = true;
         }

         var1.append("delete");
      }

      if ((var0 & 16) == 16) {
         if (var2) {
            var1.append(',');
         } else {
            var2 = true;
         }

         var1.append("readlink");
      }

      return var1.toString();
   }

   public String getActions() {
      if (this.actions == null) {
         this.actions = getActions(this.mask);
      }

      return this.actions;
   }

   public PermissionCollection newPermissionCollection() {
      return new FilePermissionCollection();
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      if (this.actions == null) {
         this.getActions();
      }

      var1.defaultWriteObject();
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      this.init(getMask(this.actions));
   }
}
