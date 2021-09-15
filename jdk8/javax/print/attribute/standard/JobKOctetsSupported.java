package javax.print.attribute.standard;

import javax.print.attribute.Attribute;
import javax.print.attribute.SetOfIntegerSyntax;
import javax.print.attribute.SupportedValuesAttribute;

public final class JobKOctetsSupported extends SetOfIntegerSyntax implements SupportedValuesAttribute {
   private static final long serialVersionUID = -2867871140549897443L;

   public JobKOctetsSupported(int var1, int var2) {
      super(var1, var2);
      if (var1 > var2) {
         throw new IllegalArgumentException("Null range specified");
      } else if (var1 < 0) {
         throw new IllegalArgumentException("Job K octets value < 0 specified");
      }
   }

   public boolean equals(Object var1) {
      return super.equals(var1) && var1 instanceof JobKOctetsSupported;
   }

   public final Class<? extends Attribute> getCategory() {
      return JobKOctetsSupported.class;
   }

   public final String getName() {
      return "job-k-octets-supported";
   }
}
