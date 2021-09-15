package javax.swing;

import java.awt.Component;
import java.util.Comparator;

final class CompareTabOrderComparator implements Comparator<Component> {
   private final DefaultFocusManager defaultFocusManager;

   CompareTabOrderComparator(DefaultFocusManager var1) {
      this.defaultFocusManager = var1;
   }

   public int compare(Component var1, Component var2) {
      if (var1 == var2) {
         return 0;
      } else {
         return this.defaultFocusManager.compareTabOrder(var1, var2) ? -1 : 1;
      }
   }
}
