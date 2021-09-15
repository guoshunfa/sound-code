package javax.swing.plaf.nimbus;

import javax.swing.JComponent;
import javax.swing.JSplitPane;

class SplitPaneVerticalState extends State {
   SplitPaneVerticalState() {
      super("Vertical");
   }

   protected boolean isInState(JComponent var1) {
      return var1 instanceof JSplitPane && ((JSplitPane)var1).getOrientation() == 1;
   }
}
