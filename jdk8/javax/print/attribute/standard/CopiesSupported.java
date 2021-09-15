package javax.print.attribute.standard;

import javax.print.attribute.Attribute;
import javax.print.attribute.SetOfIntegerSyntax;
import javax.print.attribute.SupportedValuesAttribute;

public final class CopiesSupported extends SetOfIntegerSyntax implements SupportedValuesAttribute {
   private static final long serialVersionUID = 6927711687034846001L;

   public CopiesSupported(int var1) {
      super(var1);
      if (var1 < 1) {
         throw new IllegalArgumentException("Copies value < 1 specified");
      }
   }

   public CopiesSupported(int var1, int var2) {
      super(var1, var2);
      if (var1 > var2) {
         throw new IllegalArgumentException("Null range specified");
      } else if (var1 < 1) {
         throw new IllegalArgumentException("Copies value < 1 specified");
      }
   }

   public boolean equals(Object var1) {
      return super.equals(var1) && var1 instanceof CopiesSupported;
   }

   public final Class<? extends Attribute> getCategory() {
      return CopiesSupported.class;
   }

   public final String getName() {
      return "copies-supported";
   }
}
