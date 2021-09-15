package java.nio;

public final class ByteOrder {
   private String name;
   public static final ByteOrder BIG_ENDIAN = new ByteOrder("BIG_ENDIAN");
   public static final ByteOrder LITTLE_ENDIAN = new ByteOrder("LITTLE_ENDIAN");

   private ByteOrder(String var1) {
      this.name = var1;
   }

   public static ByteOrder nativeOrder() {
      return Bits.byteOrder();
   }

   public String toString() {
      return this.name;
   }
}
