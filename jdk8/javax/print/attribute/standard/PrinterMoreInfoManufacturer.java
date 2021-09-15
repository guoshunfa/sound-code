package javax.print.attribute.standard;

import java.net.URI;
import javax.print.attribute.Attribute;
import javax.print.attribute.PrintServiceAttribute;
import javax.print.attribute.URISyntax;

public final class PrinterMoreInfoManufacturer extends URISyntax implements PrintServiceAttribute {
   private static final long serialVersionUID = 3323271346485076608L;

   public PrinterMoreInfoManufacturer(URI var1) {
      super(var1);
   }

   public boolean equals(Object var1) {
      return super.equals(var1) && var1 instanceof PrinterMoreInfoManufacturer;
   }

   public final Class<? extends Attribute> getCategory() {
      return PrinterMoreInfoManufacturer.class;
   }

   public final String getName() {
      return "printer-more-info-manufacturer";
   }
}
