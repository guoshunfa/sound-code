package javax.print.attribute.standard;

import java.util.Locale;
import javax.print.attribute.Attribute;
import javax.print.attribute.PrintServiceAttribute;
import javax.print.attribute.TextSyntax;

public final class PrinterLocation extends TextSyntax implements PrintServiceAttribute {
   private static final long serialVersionUID = -1598610039865566337L;

   public PrinterLocation(String var1, Locale var2) {
      super(var1, var2);
   }

   public boolean equals(Object var1) {
      return super.equals(var1) && var1 instanceof PrinterLocation;
   }

   public final Class<? extends Attribute> getCategory() {
      return PrinterLocation.class;
   }

   public final String getName() {
      return "printer-location";
   }
}
