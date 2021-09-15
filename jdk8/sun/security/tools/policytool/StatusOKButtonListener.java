package sun.security.tools.policytool;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class StatusOKButtonListener implements ActionListener {
   private ToolDialog sd;

   StatusOKButtonListener(ToolDialog var1) {
      this.sd = var1;
   }

   public void actionPerformed(ActionEvent var1) {
      this.sd.setVisible(false);
      this.sd.dispose();
   }
}
