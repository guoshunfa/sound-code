package javax.print.attribute.standard;

import javax.print.attribute.Attribute;
import javax.print.attribute.IntegerSyntax;
import javax.print.attribute.PrintJobAttribute;

public final class NumberOfDocuments extends IntegerSyntax implements PrintJobAttribute {
   private static final long serialVersionUID = 7891881310684461097L;

   public NumberOfDocuments(int var1) {
      super(var1, 0, Integer.MAX_VALUE);
   }

   public boolean equals(Object var1) {
      return super.equals(var1) && var1 instanceof NumberOfDocuments;
   }

   public final Class<? extends Attribute> getCategory() {
      return NumberOfDocuments.class;
   }

   public final String getName() {
      return "number-of-documents";
   }
}
