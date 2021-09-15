package javax.print.attribute.standard;

import javax.print.attribute.Attribute;
import javax.print.attribute.IntegerSyntax;
import javax.print.attribute.PrintJobAttribute;

public final class JobKOctetsProcessed extends IntegerSyntax implements PrintJobAttribute {
   private static final long serialVersionUID = -6265238509657881806L;

   public JobKOctetsProcessed(int var1) {
      super(var1, 0, Integer.MAX_VALUE);
   }

   public boolean equals(Object var1) {
      return super.equals(var1) && var1 instanceof JobKOctetsProcessed;
   }

   public final Class<? extends Attribute> getCategory() {
      return JobKOctetsProcessed.class;
   }

   public final String getName() {
      return "job-k-octets-processed";
   }
}
