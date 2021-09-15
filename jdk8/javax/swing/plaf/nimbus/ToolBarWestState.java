package javax.swing.plaf.nimbus;

import javax.swing.JComponent;
import javax.swing.JToolBar;

class ToolBarWestState extends State {
   ToolBarWestState() {
      super("West");
   }

   protected boolean isInState(JComponent var1) {
      return var1 instanceof JToolBar && NimbusLookAndFeel.resolveToolbarConstraint((JToolBar)var1) == "West";
   }
}
