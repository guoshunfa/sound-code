package sun.management.counter;

import java.io.Serializable;

public class Units implements Serializable {
   private static final int NUNITS = 8;
   private static Units[] map = new Units[8];
   private final String name;
   private final int value;
   public static final Units INVALID = new Units("Invalid", 0);
   public static final Units NONE = new Units("None", 1);
   public static final Units BYTES = new Units("Bytes", 2);
   public static final Units TICKS = new Units("Ticks", 3);
   public static final Units EVENTS = new Units("Events", 4);
   public static final Units STRING = new Units("String", 5);
   public static final Units HERTZ = new Units("Hertz", 6);
   private static final long serialVersionUID = 6992337162326171013L;

   public String toString() {
      return this.name;
   }

   public int intValue() {
      return this.value;
   }

   public static Units toUnits(int var0) {
      return var0 >= 0 && var0 < map.length && map[var0] != null ? map[var0] : INVALID;
   }

   private Units(String var1, int var2) {
      this.name = var1;
      this.value = var2;
      map[var2] = this;
   }
}
