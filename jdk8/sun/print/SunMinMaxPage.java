package sun.print;

import javax.print.attribute.PrintRequestAttribute;

public final class SunMinMaxPage implements PrintRequestAttribute {
   private int page_max;
   private int page_min;

   public SunMinMaxPage(int var1, int var2) {
      this.page_min = var1;
      this.page_max = var2;
   }

   public final Class getCategory() {
      return SunMinMaxPage.class;
   }

   public final int getMin() {
      return this.page_min;
   }

   public final int getMax() {
      return this.page_max;
   }

   public final String getName() {
      return "sun-page-minmax";
   }
}
