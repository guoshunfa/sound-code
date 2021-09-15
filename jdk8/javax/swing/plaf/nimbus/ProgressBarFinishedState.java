package javax.swing.plaf.nimbus;

import javax.swing.JComponent;
import javax.swing.JProgressBar;

class ProgressBarFinishedState extends State {
   ProgressBarFinishedState() {
      super("Finished");
   }

   protected boolean isInState(JComponent var1) {
      return var1 instanceof JProgressBar && ((JProgressBar)var1).getPercentComplete() == 1.0D;
   }
}
