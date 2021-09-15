package sun.security.tools.policytool;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class CancelButtonListener implements ActionListener {
   private ToolDialog td;

   CancelButtonListener(ToolDialog var1) {
      this.td = var1;
   }

   public void actionPerformed(ActionEvent var1) {
      this.td.setVisible(false);
      this.td.dispose();
   }
}
