package javax.print.attribute.standard;

import javax.print.attribute.Attribute;
import javax.print.attribute.IntegerSyntax;
import javax.print.attribute.PrintJobAttribute;

public final class JobMediaSheetsCompleted extends IntegerSyntax implements PrintJobAttribute {
   private static final long serialVersionUID = 1739595973810840475L;

   public JobMediaSheetsCompleted(int var1) {
      super(var1, 0, Integer.MAX_VALUE);
   }

   public boolean equals(Object var1) {
      return super.equals(var1) && var1 instanceof JobMediaSheetsCompleted;
   }

   public final Class<? extends Attribute> getCategory() {
      return JobMediaSheetsCompleted.class;
   }

   public final String getName() {
      return "job-media-sheets-completed";
   }
}
