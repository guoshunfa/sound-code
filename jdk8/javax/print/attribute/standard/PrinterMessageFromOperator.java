package javax.print.attribute.standard;

import java.util.Locale;
import javax.print.attribute.Attribute;
import javax.print.attribute.PrintServiceAttribute;
import javax.print.attribute.TextSyntax;

public final class PrinterMessageFromOperator extends TextSyntax implements PrintServiceAttribute {
   static final long serialVersionUID = -4486871203218629318L;

   public PrinterMessageFromOperator(String var1, Locale var2) {
      super(var1, var2);
   }

   public boolean equals(Object var1) {
      return super.equals(var1) && var1 instanceof PrinterMessageFromOperator;
   }

   public final Class<? extends Attribute> getCategory() {
      return PrinterMessageFromOperator.class;
   }

   public final String getName() {
      return "printer-message-from-operator";
   }
}
