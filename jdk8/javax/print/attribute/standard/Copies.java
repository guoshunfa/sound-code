package javax.print.attribute.standard;

import javax.print.attribute.Attribute;
import javax.print.attribute.IntegerSyntax;
import javax.print.attribute.PrintJobAttribute;
import javax.print.attribute.PrintRequestAttribute;

public final class Copies extends IntegerSyntax implements PrintRequestAttribute, PrintJobAttribute {
   private static final long serialVersionUID = -6426631521680023833L;

   public Copies(int var1) {
      super(var1, 1, Integer.MAX_VALUE);
   }

   public boolean equals(Object var1) {
      return super.equals(var1) && var1 instanceof Copies;
   }

   public final Class<? extends Attribute> getCategory() {
      return Copies.class;
   }

   public final String getName() {
      return "copies";
   }
}
