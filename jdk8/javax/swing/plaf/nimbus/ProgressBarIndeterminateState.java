package javax.swing.plaf.nimbus;

import javax.swing.JComponent;
import javax.swing.JProgressBar;

class ProgressBarIndeterminateState extends State {
   ProgressBarIndeterminateState() {
      super("Indeterminate");
   }

   protected boolean isInState(JComponent var1) {
      return var1 instanceof JProgressBar && ((JProgressBar)var1).isIndeterminate();
   }
}
