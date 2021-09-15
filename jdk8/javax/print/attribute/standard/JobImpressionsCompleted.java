package javax.print.attribute.standard;

import javax.print.attribute.Attribute;
import javax.print.attribute.IntegerSyntax;
import javax.print.attribute.PrintJobAttribute;

public final class JobImpressionsCompleted extends IntegerSyntax implements PrintJobAttribute {
   private static final long serialVersionUID = 6722648442432393294L;

   public JobImpressionsCompleted(int var1) {
      super(var1, 0, Integer.MAX_VALUE);
   }

   public boolean equals(Object var1) {
      return super.equals(var1) && var1 instanceof JobImpressionsCompleted;
   }

   public final Class<? extends Attribute> getCategory() {
      return JobImpressionsCompleted.class;
   }

   public final String getName() {
      return "job-impressions-completed";
   }
}
