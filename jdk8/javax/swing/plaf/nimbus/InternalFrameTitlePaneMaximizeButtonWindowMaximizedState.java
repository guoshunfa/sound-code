package javax.swing.plaf.nimbus;

import java.awt.Component;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;

class InternalFrameTitlePaneMaximizeButtonWindowMaximizedState extends State {
   InternalFrameTitlePaneMaximizeButtonWindowMaximizedState() {
      super("WindowMaximized");
   }

   protected boolean isInState(JComponent var1) {
      Object var2;
      for(var2 = var1; ((Component)var2).getParent() != null && !(var2 instanceof JInternalFrame); var2 = ((Component)var2).getParent()) {
      }

      return var2 instanceof JInternalFrame ? ((JInternalFrame)var2).isMaximum() : false;
   }
}
