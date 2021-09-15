package javax.print.attribute.standard;

import java.util.Locale;
import javax.print.attribute.Attribute;
import javax.print.attribute.PrintServiceAttribute;
import javax.print.attribute.TextSyntax;

public final class PrinterMakeAndModel extends TextSyntax implements PrintServiceAttribute {
   private static final long serialVersionUID = 4580461489499351411L;

   public PrinterMakeAndModel(String var1, Locale var2) {
      super(var1, var2);
   }

   public boolean equals(Object var1) {
      return super.equals(var1) && var1 instanceof PrinterMakeAndModel;
   }

   public final Class<? extends Attribute> getCategory() {
      return PrinterMakeAndModel.class;
   }

   public final String getName() {
      return "printer-make-and-model";
   }
}
