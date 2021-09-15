package javax.swing.plaf.nimbus;

import javax.swing.JComponent;
import javax.swing.JInternalFrame;

class InternalFrameWindowFocusedState extends State {
   InternalFrameWindowFocusedState() {
      super("WindowFocused");
   }

   protected boolean isInState(JComponent var1) {
      return var1 instanceof JInternalFrame && ((JInternalFrame)var1).isSelected();
   }
}
