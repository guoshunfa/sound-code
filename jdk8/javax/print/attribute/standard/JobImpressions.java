package javax.print.attribute.standard;

import javax.print.attribute.Attribute;
import javax.print.attribute.IntegerSyntax;
import javax.print.attribute.PrintJobAttribute;
import javax.print.attribute.PrintRequestAttribute;

public final class JobImpressions extends IntegerSyntax implements PrintRequestAttribute, PrintJobAttribute {
   private static final long serialVersionUID = 8225537206784322464L;

   public JobImpressions(int var1) {
      super(var1, 0, Integer.MAX_VALUE);
   }

   public boolean equals(Object var1) {
      return super.equals(var1) && var1 instanceof JobImpressions;
   }

   public final Class<? extends Attribute> getCategory() {
      return JobImpressions.class;
   }

   public final String getName() {
      return "job-impressions";
   }
}
