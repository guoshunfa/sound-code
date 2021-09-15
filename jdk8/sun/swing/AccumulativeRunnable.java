package sun.swing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.SwingUtilities;

public abstract class AccumulativeRunnable<T> implements Runnable {
   private List<T> arguments = null;

   protected abstract void run(List<T> var1);

   public final void run() {
      this.run(this.flush());
   }

   @SafeVarargs
   public final synchronized void add(T... var1) {
      boolean var2 = true;
      if (this.arguments == null) {
         var2 = false;
         this.arguments = new ArrayList();
      }

      Collections.addAll(this.arguments, var1);
      if (!var2) {
         this.submit();
      }

   }

   protected void submit() {
      SwingUtilities.invokeLater(this);
   }

   private final synchronized List<T> flush() {
      List var1 = this.arguments;
      this.arguments = null;
      return var1;
   }
}
