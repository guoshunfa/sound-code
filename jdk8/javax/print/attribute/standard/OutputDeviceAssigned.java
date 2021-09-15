package javax.print.attribute.standard;

import java.util.Locale;
import javax.print.attribute.Attribute;
import javax.print.attribute.PrintJobAttribute;
import javax.print.attribute.TextSyntax;

public final class OutputDeviceAssigned extends TextSyntax implements PrintJobAttribute {
   private static final long serialVersionUID = 5486733778854271081L;

   public OutputDeviceAssigned(String var1, Locale var2) {
      super(var1, var2);
   }

   public boolean equals(Object var1) {
      return super.equals(var1) && var1 instanceof OutputDeviceAssigned;
   }

   public final Class<? extends Attribute> getCategory() {
      return OutputDeviceAssigned.class;
   }

   public final String getName() {
      return "output-device-assigned";
   }
}
