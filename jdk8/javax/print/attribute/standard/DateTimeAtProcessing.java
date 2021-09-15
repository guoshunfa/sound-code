package javax.print.attribute.standard;

import java.util.Date;
import javax.print.attribute.Attribute;
import javax.print.attribute.DateTimeSyntax;
import javax.print.attribute.PrintJobAttribute;

public final class DateTimeAtProcessing extends DateTimeSyntax implements PrintJobAttribute {
   private static final long serialVersionUID = -3710068197278263244L;

   public DateTimeAtProcessing(Date var1) {
      super(var1);
   }

   public boolean equals(Object var1) {
      return super.equals(var1) && var1 instanceof DateTimeAtProcessing;
   }

   public final Class<? extends Attribute> getCategory() {
      return DateTimeAtProcessing.class;
   }

   public final String getName() {
      return "date-time-at-processing";
   }
}
