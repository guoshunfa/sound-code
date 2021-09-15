package javax.swing.plaf.nimbus;

import javax.swing.JComponent;
import javax.swing.JToolBar;

class ToolBarNorthState extends State {
   ToolBarNorthState() {
      super("North");
   }

   protected boolean isInState(JComponent var1) {
      return var1 instanceof JToolBar && NimbusLookAndFeel.resolveToolbarConstraint((JToolBar)var1) == "North";
   }
}
