package javax.print.attribute.standard;

import javax.print.attribute.Attribute;
import javax.print.attribute.IntegerSyntax;
import javax.print.attribute.PrintJobAttribute;
import javax.print.attribute.PrintRequestAttribute;

public final class JobKOctets extends IntegerSyntax implements PrintRequestAttribute, PrintJobAttribute {
   private static final long serialVersionUID = -8959710146498202869L;

   public JobKOctets(int var1) {
      super(var1, 0, Integer.MAX_VALUE);
   }

   public boolean equals(Object var1) {
      return super.equals(var1) && var1 instanceof JobKOctets;
   }

   public final Class<? extends Attribute> getCategory() {
      return JobKOctets.class;
   }

   public final String getName() {
      return "job-k-octets";
   }
}
