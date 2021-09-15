package javax.print.attribute.standard;

import javax.print.attribute.Attribute;
import javax.print.attribute.IntegerSyntax;
import javax.print.attribute.PrintJobAttribute;
import javax.print.attribute.PrintRequestAttribute;

public class JobMediaSheets extends IntegerSyntax implements PrintRequestAttribute, PrintJobAttribute {
   private static final long serialVersionUID = 408871131531979741L;

   public JobMediaSheets(int var1) {
      super(var1, 0, Integer.MAX_VALUE);
   }

   public boolean equals(Object var1) {
      return super.equals(var1) && var1 instanceof JobMediaSheets;
   }

   public final Class<? extends Attribute> getCategory() {
      return JobMediaSheets.class;
   }

   public final String getName() {
      return "job-media-sheets";
   }
}
