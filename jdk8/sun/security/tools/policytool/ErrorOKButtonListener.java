package sun.security.tools.policytool;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class ErrorOKButtonListener implements ActionListener {
   private ToolDialog ed;

   ErrorOKButtonListener(ToolDialog var1) {
      this.ed = var1;
   }

   public void actionPerformed(ActionEvent var1) {
      this.ed.setVisible(false);
      this.ed.dispose();
   }
}
