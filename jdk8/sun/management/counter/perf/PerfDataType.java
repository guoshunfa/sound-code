package sun.management.counter.perf;

import java.io.UnsupportedEncodingException;

class PerfDataType {
   private final String name;
   private final byte value;
   private final int size;
   public static final PerfDataType BOOLEAN = new PerfDataType("boolean", "Z", 1);
   public static final PerfDataType CHAR = new PerfDataType("char", "C", 1);
   public static final PerfDataType FLOAT = new PerfDataType("float", "F", 8);
   public static final PerfDataType DOUBLE = new PerfDataType("double", "D", 8);
   public static final PerfDataType BYTE = new PerfDataType("byte", "B", 1);
   public static final PerfDataType SHORT = new PerfDataType("short", "S", 2);
   public static final PerfDataType INT = new PerfDataType("int", "I", 4);
   public static final PerfDataType LONG = new PerfDataType("long", "J", 8);
   public static final PerfDataType ILLEGAL = new PerfDataType("illegal", "X", 0);
   private static PerfDataType[] basicTypes;

   public String toString() {
      return this.name;
   }

   public byte byteValue() {
      return this.value;
   }

   public int size() {
      return this.size;
   }

   public static PerfDataType toPerfDataType(byte var0) {
      for(int var1 = 0; var1 < basicTypes.length; ++var1) {
         if (basicTypes[var1].byteValue() == var0) {
            return basicTypes[var1];
         }
      }

      return ILLEGAL;
   }

   private PerfDataType(String var1, String var2, int var3) {
      this.name = var1;
      this.size = var3;

      try {
         byte[] var4 = var2.getBytes("UTF-8");
         this.value = var4[0];
      } catch (UnsupportedEncodingException var5) {
         throw new InternalError("Unknown encoding", var5);
      }
   }

   static {
      basicTypes = new PerfDataType[]{LONG, BYTE, BOOLEAN, CHAR, FLOAT, DOUBLE, SHORT, INT};
   }
}
