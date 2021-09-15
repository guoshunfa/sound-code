package javax.print.attribute.standard;

import javax.print.attribute.Attribute;
import javax.print.attribute.IntegerSyntax;
import javax.print.attribute.PrintJobAttribute;
import javax.print.attribute.PrintRequestAttribute;

public final class JobPriority extends IntegerSyntax implements PrintRequestAttribute, PrintJobAttribute {
   private static final long serialVersionUID = -4599900369040602769L;

   public JobPriority(int var1) {
      super(var1, 1, 100);
   }

   public boolean equals(Object var1) {
      return super.equals(var1) && var1 instanceof JobPriority;
   }

   public final Class<? extends Attribute> getCategory() {
      return JobPriority.class;
   }

   public final String getName() {
      return "job-priority";
   }
}
