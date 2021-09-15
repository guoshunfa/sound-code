package sun.misc;

public class MessageUtils {
   public static String subst(String var0, String var1) {
      String[] var2 = new String[]{var1};
      return subst(var0, var2);
   }

   public static String subst(String var0, String var1, String var2) {
      String[] var3 = new String[]{var1, var2};
      return subst(var0, var3);
   }

   public static String subst(String var0, String var1, String var2, String var3) {
      String[] var4 = new String[]{var1, var2, var3};
      return subst(var0, var4);
   }

   public static String subst(String var0, String[] var1) {
      StringBuffer var2 = new StringBuffer();
      int var3 = var0.length();

      for(int var4 = 0; var4 >= 0 && var4 < var3; ++var4) {
         char var5 = var0.charAt(var4);
         if (var5 == '%') {
            if (var4 != var3) {
               int var6 = Character.digit((char)var0.charAt(var4 + 1), 10);
               if (var6 == -1) {
                  var2.append(var0.charAt(var4 + 1));
                  ++var4;
               } else if (var6 < var1.length) {
                  var2.append(var1[var6]);
                  ++var4;
               }
            }
         } else {
            var2.append(var5);
         }
      }

      return var2.toString();
   }

   public static String substProp(String var0, String var1) {
      return subst(System.getProperty(var0), var1);
   }

   public static String substProp(String var0, String var1, String var2) {
      return subst(System.getProperty(var0), var1, var2);
   }

   public static String substProp(String var0, String var1, String var2, String var3) {
      return subst(System.getProperty(var0), var1, var2, var3);
   }

   public static native void toStderr(String var0);

   public static native void toStdout(String var0);

   public static void err(String var0) {
      toStderr(var0 + "\n");
   }

   public static void out(String var0) {
      toStdout(var0 + "\n");
   }

   public static void where() {
      Throwable var0 = new Throwable();
      StackTraceElement[] var1 = var0.getStackTrace();

      for(int var2 = 1; var2 < var1.length; ++var2) {
         toStderr("\t" + var1[var2].toString() + "\n");
      }

   }
}
