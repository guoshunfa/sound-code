package javax.swing.plaf.nimbus;

import javax.swing.JComponent;
import javax.swing.JSplitPane;

class SplitPaneDividerVerticalState extends State {
   SplitPaneDividerVerticalState() {
      super("Vertical");
   }

   protected boolean isInState(JComponent var1) {
      return var1 instanceof JSplitPane && ((JSplitPane)var1).getOrientation() == 1;
   }
}
