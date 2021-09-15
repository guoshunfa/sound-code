package javax.print.attribute.standard;

import java.util.Date;
import javax.print.attribute.Attribute;
import javax.print.attribute.DateTimeSyntax;
import javax.print.attribute.PrintJobAttribute;
import javax.print.attribute.PrintRequestAttribute;

public final class JobHoldUntil extends DateTimeSyntax implements PrintRequestAttribute, PrintJobAttribute {
   private static final long serialVersionUID = -1664471048860415024L;

   public JobHoldUntil(Date var1) {
      super(var1);
   }

   public boolean equals(Object var1) {
      return super.equals(var1) && var1 instanceof JobHoldUntil;
   }

   public final Class<? extends Attribute> getCategory() {
      return JobHoldUntil.class;
   }

   public final String getName() {
      return "job-hold-until";
   }
}
