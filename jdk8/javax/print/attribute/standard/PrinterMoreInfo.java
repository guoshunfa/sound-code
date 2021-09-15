package javax.print.attribute.standard;

import java.net.URI;
import javax.print.attribute.Attribute;
import javax.print.attribute.PrintServiceAttribute;
import javax.print.attribute.URISyntax;

public final class PrinterMoreInfo extends URISyntax implements PrintServiceAttribute {
   private static final long serialVersionUID = 4555850007675338574L;

   public PrinterMoreInfo(URI var1) {
      super(var1);
   }

   public boolean equals(Object var1) {
      return super.equals(var1) && var1 instanceof PrinterMoreInfo;
   }

   public final Class<? extends Attribute> getCategory() {
      return PrinterMoreInfo.class;
   }

   public final String getName() {
      return "printer-more-info";
   }
}
