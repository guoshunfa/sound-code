package javax.print.attribute.standard;

import java.util.Locale;
import javax.print.attribute.Attribute;
import javax.print.attribute.PrintJobAttribute;
import javax.print.attribute.PrintRequestAttribute;
import javax.print.attribute.TextSyntax;

public final class JobName extends TextSyntax implements PrintRequestAttribute, PrintJobAttribute {
   private static final long serialVersionUID = 4660359192078689545L;

   public JobName(String var1, Locale var2) {
      super(var1, var2);
   }

   public boolean equals(Object var1) {
      return super.equals(var1) && var1 instanceof JobName;
   }

   public final Class<? extends Attribute> getCategory() {
      return JobName.class;
   }

   public final String getName() {
      return "job-name";
   }
}
