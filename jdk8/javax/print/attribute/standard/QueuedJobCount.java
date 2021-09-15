package javax.print.attribute.standard;

import javax.print.attribute.Attribute;
import javax.print.attribute.IntegerSyntax;
import javax.print.attribute.PrintServiceAttribute;

public final class QueuedJobCount extends IntegerSyntax implements PrintServiceAttribute {
   private static final long serialVersionUID = 7499723077864047742L;

   public QueuedJobCount(int var1) {
      super(var1, 0, Integer.MAX_VALUE);
   }

   public boolean equals(Object var1) {
      return super.equals(var1) && var1 instanceof QueuedJobCount;
   }

   public final Class<? extends Attribute> getCategory() {
      return QueuedJobCount.class;
   }

   public final String getName() {
      return "queued-job-count";
   }
}
