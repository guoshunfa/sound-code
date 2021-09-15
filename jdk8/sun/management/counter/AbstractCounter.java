package sun.management.counter;

public abstract class AbstractCounter implements Counter {
   String name;
   Units units;
   Variability variability;
   int flags;
   int vectorLength;
   private static final long serialVersionUID = 6992337162326171013L;

   protected AbstractCounter(String var1, Units var2, Variability var3, int var4, int var5) {
      this.name = var1;
      this.units = var2;
      this.variability = var3;
      this.flags = var4;
      this.vectorLength = var5;
   }

   protected AbstractCounter(String var1, Units var2, Variability var3, int var4) {
      this(var1, var2, var3, var4, 0);
   }

   public String getName() {
      return this.name;
   }

   public Units getUnits() {
      return this.units;
   }

   public Variability getVariability() {
      return this.variability;
   }

   public boolean isVector() {
      return this.vectorLength > 0;
   }

   public int getVectorLength() {
      return this.vectorLength;
   }

   public boolean isInternal() {
      return (this.flags & 1) == 0;
   }

   public int getFlags() {
      return this.flags;
   }

   public abstract Object getValue();

   public String toString() {
      String var1 = this.getName() + ": " + this.getValue() + " " + this.getUnits();
      return this.isInternal() ? var1 + " [INTERNAL]" : var1;
   }

   class Flags {
      static final int SUPPORTED = 1;
   }
}
