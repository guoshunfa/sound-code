package javax.print.attribute.standard;

import java.net.URI;
import javax.print.attribute.Attribute;
import javax.print.attribute.PrintServiceAttribute;
import javax.print.attribute.URISyntax;

public final class PrinterURI extends URISyntax implements PrintServiceAttribute {
   private static final long serialVersionUID = 7923912792485606497L;

   public PrinterURI(URI var1) {
      super(var1);
   }

   public boolean equals(Object var1) {
      return super.equals(var1) && var1 instanceof PrinterURI;
   }

   public final Class<? extends Attribute> getCategory() {
      return PrinterURI.class;
   }

   public final String getName() {
      return "printer-uri";
   }
}
