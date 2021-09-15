package javax.swing.plaf.nimbus;

import javax.swing.JComponent;
import javax.swing.JInternalFrame;

class InternalFrameTitlePaneWindowFocusedState extends State {
   InternalFrameTitlePaneWindowFocusedState() {
      super("WindowFocused");
   }

   protected boolean isInState(JComponent var1) {
      return var1 instanceof JInternalFrame && ((JInternalFrame)var1).isSelected();
   }
}
