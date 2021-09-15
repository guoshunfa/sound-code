package javax.print.attribute.standard;

import javax.print.attribute.Attribute;
import javax.print.attribute.SetOfIntegerSyntax;
import javax.print.attribute.SupportedValuesAttribute;

public final class JobImpressionsSupported extends SetOfIntegerSyntax implements SupportedValuesAttribute {
   private static final long serialVersionUID = -4887354803843173692L;

   public JobImpressionsSupported(int var1, int var2) {
      super(var1, var2);
      if (var1 > var2) {
         throw new IllegalArgumentException("Null range specified");
      } else if (var1 < 0) {
         throw new IllegalArgumentException("Job K octets value < 0 specified");
      }
   }

   public boolean equals(Object var1) {
      return super.equals(var1) && var1 instanceof JobImpressionsSupported;
   }

   public final Class<? extends Attribute> getCategory() {
      return JobImpressionsSupported.class;
   }

   public final String getName() {
      return "job-impressions-supported";
   }
}
