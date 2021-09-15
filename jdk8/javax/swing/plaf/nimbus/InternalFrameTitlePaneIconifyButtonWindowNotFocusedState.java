package javax.swing.plaf.nimbus;

import java.awt.Component;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;

class InternalFrameTitlePaneIconifyButtonWindowNotFocusedState extends State {
   InternalFrameTitlePaneIconifyButtonWindowNotFocusedState() {
      super("WindowNotFocused");
   }

   protected boolean isInState(JComponent var1) {
      Object var2;
      for(var2 = var1; ((Component)var2).getParent() != null && !(var2 instanceof JInternalFrame); var2 = ((Component)var2).getParent()) {
      }

      if (var2 instanceof JInternalFrame) {
         return !((JInternalFrame)var2).isSelected();
      } else {
         return false;
      }
   }
}
