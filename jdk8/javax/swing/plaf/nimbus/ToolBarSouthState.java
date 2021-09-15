package javax.swing.plaf.nimbus;

import javax.swing.JComponent;
import javax.swing.JToolBar;

class ToolBarSouthState extends State {
   ToolBarSouthState() {
      super("South");
   }

   protected boolean isInState(JComponent var1) {
      return var1 instanceof JToolBar && NimbusLookAndFeel.resolveToolbarConstraint((JToolBar)var1) == "South";
   }
}
