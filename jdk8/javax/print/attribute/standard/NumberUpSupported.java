package javax.print.attribute.standard;

import javax.print.attribute.Attribute;
import javax.print.attribute.SetOfIntegerSyntax;
import javax.print.attribute.SupportedValuesAttribute;

public final class NumberUpSupported extends SetOfIntegerSyntax implements SupportedValuesAttribute {
   private static final long serialVersionUID = -1041573395759141805L;

   public NumberUpSupported(int[][] var1) {
      super(var1);
      if (var1 == null) {
         throw new NullPointerException("members is null");
      } else {
         int[][] var2 = this.getMembers();
         int var3 = var2.length;
         if (var3 == 0) {
            throw new IllegalArgumentException("members is zero-length");
         } else {
            for(int var4 = 0; var4 < var3; ++var4) {
               if (var2[var4][0] < 1) {
                  throw new IllegalArgumentException("Number up value must be > 0");
               }
            }

         }
      }
   }

   public NumberUpSupported(int var1) {
      super(var1);
      if (var1 < 1) {
         throw new IllegalArgumentException("Number up value must be > 0");
      }
   }

   public NumberUpSupported(int var1, int var2) {
      super(var1, var2);
      if (var1 > var2) {
         throw new IllegalArgumentException("Null range specified");
      } else if (var1 < 1) {
         throw new IllegalArgumentException("Number up value must be > 0");
      }
   }

   public boolean equals(Object var1) {
      return super.equals(var1) && var1 instanceof NumberUpSupported;
   }

   public final Class<? extends Attribute> getCategory() {
      return NumberUpSupported.class;
   }

   public final String getName() {
      return "number-up-supported";
   }
}
