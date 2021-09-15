package sun.java2d.xr;

public class MutableInteger {
   private int value;

   public MutableInteger(int var1) {
      this.setValue(var1);
   }

   public int hashCode() {
      return this.getValue();
   }

   public boolean equals(Object var1) {
      return var1 instanceof MutableInteger && ((MutableInteger)var1).getValue() == this.getValue();
   }

   public void setValue(int var1) {
      this.value = var1;
   }

   public int getValue() {
      return this.value;
   }
}
