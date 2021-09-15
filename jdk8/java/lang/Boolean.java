package java.lang;

import java.io.Serializable;

public final class Boolean implements Serializable, Comparable<Boolean> {
   public static final Boolean TRUE = new Boolean(true);
   public static final Boolean FALSE = new Boolean(false);
   public static final Class<Boolean> TYPE = Class.getPrimitiveClass("boolean");
   private final boolean value;
   private static final long serialVersionUID = -3665804199014368530L;

   public Boolean(boolean var1) {
      this.value = var1;
   }

   public Boolean(String var1) {
      this(parseBoolean(var1));
   }

   public static boolean parseBoolean(String var0) {
      return var0 != null && var0.equalsIgnoreCase("true");
   }

   public boolean booleanValue() {
      return this.value;
   }

   public static Boolean valueOf(boolean var0) {
      return var0 ? TRUE : FALSE;
   }

   public static Boolean valueOf(String var0) {
      return parseBoolean(var0) ? TRUE : FALSE;
   }

   public static String toString(boolean var0) {
      return var0 ? "true" : "false";
   }

   public String toString() {
      return this.value ? "true" : "false";
   }

   public int hashCode() {
      return hashCode(this.value);
   }

   public static int hashCode(boolean var0) {
      return var0 ? 1231 : 1237;
   }

   public boolean equals(Object var1) {
      if (var1 instanceof Boolean) {
         return this.value == (Boolean)var1;
      } else {
         return false;
      }
   }

   public static boolean getBoolean(String var0) {
      boolean var1 = false;

      try {
         var1 = parseBoolean(System.getProperty(var0));
      } catch (NullPointerException | IllegalArgumentException var3) {
      }

      return var1;
   }

   public int compareTo(Boolean var1) {
      return compare(this.value, var1.value);
   }

   public static int compare(boolean var0, boolean var1) {
      return var0 == var1 ? 0 : (var0 ? 1 : -1);
   }

   public static boolean logicalAnd(boolean var0, boolean var1) {
      return var0 && var1;
   }

   public static boolean logicalOr(boolean var0, boolean var1) {
      return var0 || var1;
   }

   public static boolean logicalXor(boolean var0, boolean var1) {
      return var0 ^ var1;
   }
}
