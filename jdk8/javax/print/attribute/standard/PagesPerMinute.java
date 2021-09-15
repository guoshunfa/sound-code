package javax.print.attribute.standard;

import javax.print.attribute.Attribute;
import javax.print.attribute.IntegerSyntax;
import javax.print.attribute.PrintServiceAttribute;

public final class PagesPerMinute extends IntegerSyntax implements PrintServiceAttribute {
   private static final long serialVersionUID = -6366403993072862015L;

   public PagesPerMinute(int var1) {
      super(var1, 0, Integer.MAX_VALUE);
   }

   public boolean equals(Object var1) {
      return super.equals(var1) && var1 instanceof PagesPerMinute;
   }

   public final Class<? extends Attribute> getCategory() {
      return PagesPerMinute.class;
   }

   public final String getName() {
      return "pages-per-minute";
   }
}
