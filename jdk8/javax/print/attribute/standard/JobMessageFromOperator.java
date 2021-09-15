package javax.print.attribute.standard;

import java.util.Locale;
import javax.print.attribute.Attribute;
import javax.print.attribute.PrintJobAttribute;
import javax.print.attribute.TextSyntax;

public final class JobMessageFromOperator extends TextSyntax implements PrintJobAttribute {
   private static final long serialVersionUID = -4620751846003142047L;

   public JobMessageFromOperator(String var1, Locale var2) {
      super(var1, var2);
   }

   public boolean equals(Object var1) {
      return super.equals(var1) && var1 instanceof JobMessageFromOperator;
   }

   public final Class<? extends Attribute> getCategory() {
      return JobMessageFromOperator.class;
   }

   public final String getName() {
      return "job-message-from-operator";
   }
}
