package javax.print.attribute;

import java.io.Serializable;

public abstract class IntegerSyntax implements Serializable, Cloneable {
   private static final long serialVersionUID = 3644574816328081943L;
   private int value;

   protected IntegerSyntax(int var1) {
      this.value = var1;
   }

   protected IntegerSyntax(int var1, int var2, int var3) {
      if (var2 <= var1 && var1 <= var3) {
         this.value = var1;
      } else {
         throw new IllegalArgumentException("Value " + var1 + " not in range " + var2 + ".." + var3);
      }
   }

   public int getValue() {
      return this.value;
   }

   public boolean equals(Object var1) {
      return var1 != null && var1 instanceof IntegerSyntax && this.value == ((IntegerSyntax)var1).value;
   }

   public int hashCode() {
      return this.value;
   }

   public String toString() {
      return "" + this.value;
   }
}
