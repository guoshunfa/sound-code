package sun.management.counter;

import java.io.Serializable;

public class Variability implements Serializable {
   private static final int NATTRIBUTES = 4;
   private static Variability[] map = new Variability[4];
   private String name;
   private int value;
   public static final Variability INVALID = new Variability("Invalid", 0);
   public static final Variability CONSTANT = new Variability("Constant", 1);
   public static final Variability MONOTONIC = new Variability("Monotonic", 2);
   public static final Variability VARIABLE = new Variability("Variable", 3);
   private static final long serialVersionUID = 6992337162326171013L;

   public String toString() {
      return this.name;
   }

   public int intValue() {
      return this.value;
   }

   public static Variability toVariability(int var0) {
      return var0 >= 0 && var0 < map.length && map[var0] != null ? map[var0] : INVALID;
   }

   private Variability(String var1, int var2) {
      this.name = var1;
      this.value = var2;
      map[var2] = this;
   }
}
