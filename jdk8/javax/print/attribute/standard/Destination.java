package javax.print.attribute.standard;

import java.net.URI;
import javax.print.attribute.Attribute;
import javax.print.attribute.PrintJobAttribute;
import javax.print.attribute.PrintRequestAttribute;
import javax.print.attribute.URISyntax;

public final class Destination extends URISyntax implements PrintJobAttribute, PrintRequestAttribute {
   private static final long serialVersionUID = 6776739171700415321L;

   public Destination(URI var1) {
      super(var1);
   }

   public boolean equals(Object var1) {
      return super.equals(var1) && var1 instanceof Destination;
   }

   public final Class<? extends Attribute> getCategory() {
      return Destination.class;
   }

   public final String getName() {
      return "spool-data-destination";
   }
}
