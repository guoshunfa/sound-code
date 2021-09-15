package javax.print.attribute.standard;

import java.util.Locale;
import javax.print.attribute.Attribute;
import javax.print.attribute.PrintServiceAttribute;
import javax.print.attribute.TextSyntax;

public final class PrinterName extends TextSyntax implements PrintServiceAttribute {
   private static final long serialVersionUID = 299740639137803127L;

   public PrinterName(String var1, Locale var2) {
      super(var1, var2);
   }

   public boolean equals(Object var1) {
      return super.equals(var1) && var1 instanceof PrinterName;
   }

   public final Class<? extends Attribute> getCategory() {
      return PrinterName.class;
   }

   public final String getName() {
      return "printer-name";
   }
}
