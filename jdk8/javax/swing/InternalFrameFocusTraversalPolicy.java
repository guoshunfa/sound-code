package javax.swing;

import java.awt.Component;
import java.awt.FocusTraversalPolicy;

public abstract class InternalFrameFocusTraversalPolicy extends FocusTraversalPolicy {
   public Component getInitialComponent(JInternalFrame var1) {
      return this.getDefaultComponent(var1);
   }
}
