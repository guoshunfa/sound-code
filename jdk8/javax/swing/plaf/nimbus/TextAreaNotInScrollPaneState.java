package javax.swing.plaf.nimbus;

import javax.swing.JComponent;
import javax.swing.JViewport;

class TextAreaNotInScrollPaneState extends State {
   TextAreaNotInScrollPaneState() {
      super("NotInScrollPane");
   }

   protected boolean isInState(JComponent var1) {
      return !(var1.getParent() instanceof JViewport);
   }
}
