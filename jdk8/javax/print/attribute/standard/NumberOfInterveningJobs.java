package javax.print.attribute.standard;

import javax.print.attribute.Attribute;
import javax.print.attribute.IntegerSyntax;
import javax.print.attribute.PrintJobAttribute;

public final class NumberOfInterveningJobs extends IntegerSyntax implements PrintJobAttribute {
   private static final long serialVersionUID = 2568141124844982746L;

   public NumberOfInterveningJobs(int var1) {
      super(var1, 0, Integer.MAX_VALUE);
   }

   public boolean equals(Object var1) {
      return super.equals(var1) && var1 instanceof NumberOfInterveningJobs;
   }

   public final Class<? extends Attribute> getCategory() {
      return NumberOfInterveningJobs.class;
   }

   public final String getName() {
      return "number-of-intervening-jobs";
   }
}
