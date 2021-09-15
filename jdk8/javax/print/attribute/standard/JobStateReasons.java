package javax.print.attribute.standard;

import java.util.Collection;
import java.util.HashSet;
import javax.print.attribute.Attribute;
import javax.print.attribute.PrintJobAttribute;

public final class JobStateReasons extends HashSet<JobStateReason> implements PrintJobAttribute {
   private static final long serialVersionUID = 8849088261264331812L;

   public JobStateReasons() {
   }

   public JobStateReasons(int var1) {
      super(var1);
   }

   public JobStateReasons(int var1, float var2) {
      super(var1, var2);
   }

   public JobStateReasons(Collection<JobStateReason> var1) {
      super(var1);
   }

   public boolean add(JobStateReason var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         return super.add(var1);
      }
   }

   public final Class<? extends Attribute> getCategory() {
      return JobStateReasons.class;
   }

   public final String getName() {
      return "job-state-reasons";
   }
}
