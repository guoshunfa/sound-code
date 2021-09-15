package javax.print.attribute.standard;

import javax.print.attribute.Attribute;
import javax.print.attribute.IntegerSyntax;
import javax.print.attribute.SupportedValuesAttribute;

public final class JobPrioritySupported extends IntegerSyntax implements SupportedValuesAttribute {
   private static final long serialVersionUID = 2564840378013555894L;

   public JobPrioritySupported(int var1) {
      super(var1, 1, 100);
   }

   public boolean equals(Object var1) {
      return super.equals(var1) && var1 instanceof JobPrioritySupported;
   }

   public final Class<? extends Attribute> getCategory() {
      return JobPrioritySupported.class;
   }

   public final String getName() {
      return "job-priority-supported";
   }
}
