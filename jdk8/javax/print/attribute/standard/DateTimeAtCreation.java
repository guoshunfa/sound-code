package javax.print.attribute.standard;

import java.util.Date;
import javax.print.attribute.Attribute;
import javax.print.attribute.DateTimeSyntax;
import javax.print.attribute.PrintJobAttribute;

public final class DateTimeAtCreation extends DateTimeSyntax implements PrintJobAttribute {
   private static final long serialVersionUID = -2923732231056647903L;

   public DateTimeAtCreation(Date var1) {
      super(var1);
   }

   public boolean equals(Object var1) {
      return super.equals(var1) && var1 instanceof DateTimeAtCreation;
   }

   public final Class<? extends Attribute> getCategory() {
      return DateTimeAtCreation.class;
   }

   public final String getName() {
      return "date-time-at-creation";
   }
}
