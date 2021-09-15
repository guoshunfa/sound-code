package sun.management;

import com.sun.management.VMOption;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.List;

class Flag {
   private String name;
   private Object value;
   private VMOption.Origin origin;
   private boolean writeable;
   private boolean external;

   Flag(String var1, Object var2, boolean var3, boolean var4, VMOption.Origin var5) {
      this.name = var1;
      this.value = var2 == null ? "" : var2;
      this.origin = var5;
      this.writeable = var3;
      this.external = var4;
   }

   Object getValue() {
      return this.value;
   }

   boolean isWriteable() {
      return this.writeable;
   }

   boolean isExternal() {
      return this.external;
   }

   VMOption getVMOption() {
      return new VMOption(this.name, this.value.toString(), this.writeable, this.origin);
   }

   static Flag getFlag(String var0) {
      String[] var1 = new String[]{var0};
      List var2 = getFlags(var1, 1);
      return var2.isEmpty() ? null : (Flag)var2.get(0);
   }

   static List<Flag> getAllFlags() {
      int var0 = getInternalFlagCount();
      return getFlags((String[])null, var0);
   }

   private static List<Flag> getFlags(String[] var0, int var1) {
      Flag[] var2 = new Flag[var1];
      getFlags(var0, var2, var1);
      ArrayList var4 = new ArrayList();
      Flag[] var5 = var2;
      int var6 = var2.length;

      for(int var7 = 0; var7 < var6; ++var7) {
         Flag var8 = var5[var7];
         if (var8 != null) {
            var4.add(var8);
         }
      }

      return var4;
   }

   private static native String[] getAllFlagNames();

   private static native int getFlags(String[] var0, Flag[] var1, int var2);

   private static native int getInternalFlagCount();

   static synchronized native void setLongValue(String var0, long var1);

   static synchronized native void setBooleanValue(String var0, boolean var1);

   static synchronized native void setStringValue(String var0, String var1);

   private static native void initialize();

   static {
      AccessController.doPrivileged(new PrivilegedAction<Void>() {
         public Void run() {
            System.loadLibrary("management");
            return null;
         }
      });
      initialize();
   }
}
