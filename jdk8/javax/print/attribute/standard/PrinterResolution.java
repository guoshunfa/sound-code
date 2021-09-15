package javax.print.attribute.standard;

import javax.print.attribute.Attribute;
import javax.print.attribute.DocAttribute;
import javax.print.attribute.PrintJobAttribute;
import javax.print.attribute.PrintRequestAttribute;
import javax.print.attribute.ResolutionSyntax;

public final class PrinterResolution extends ResolutionSyntax implements DocAttribute, PrintRequestAttribute, PrintJobAttribute {
   private static final long serialVersionUID = 13090306561090558L;

   public PrinterResolution(int var1, int var2, int var3) {
      super(var1, var2, var3);
   }

   public boolean equals(Object var1) {
      return super.equals(var1) && var1 instanceof PrinterResolution;
   }

   public final Class<? extends Attribute> getCategory() {
      return PrinterResolution.class;
   }

   public final String getName() {
      return "printer-resolution";
   }
}
