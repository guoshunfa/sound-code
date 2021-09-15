package javax.print.attribute.standard;

import java.util.Locale;
import javax.print.attribute.Attribute;
import javax.print.attribute.PrintServiceAttribute;
import javax.print.attribute.TextSyntax;

public final class PrinterInfo extends TextSyntax implements PrintServiceAttribute {
   private static final long serialVersionUID = 7765280618777599727L;

   public PrinterInfo(String var1, Locale var2) {
      super(var1, var2);
   }

   public boolean equals(Object var1) {
      return super.equals(var1) && var1 instanceof PrinterInfo;
   }

   public final Class<? extends Attribute> getCategory() {
      return PrinterInfo.class;
   }

   public final String getName() {
      return "printer-info";
   }
}
