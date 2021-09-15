package javax.print.attribute.standard;

import javax.print.attribute.Attribute;
import javax.print.attribute.IntegerSyntax;
import javax.print.attribute.PrintServiceAttribute;

public final class PagesPerMinuteColor extends IntegerSyntax implements PrintServiceAttribute {
   static final long serialVersionUID = 1684993151687470944L;

   public PagesPerMinuteColor(int var1) {
      super(var1, 0, Integer.MAX_VALUE);
   }

   public boolean equals(Object var1) {
      return super.equals(var1) && var1 instanceof PagesPerMinuteColor;
   }

   public final Class<? extends Attribute> getCategory() {
      return PagesPerMinuteColor.class;
   }

   public final String getName() {
      return "pages-per-minute-color";
   }
}
