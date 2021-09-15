package sun.security.tools.policytool;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class UserSaveCancelButtonListener implements ActionListener {
   private ToolDialog us;

   UserSaveCancelButtonListener(ToolDialog var1) {
      this.us = var1;
   }

   public void actionPerformed(ActionEvent var1) {
      this.us.setVisible(false);
      this.us.dispose();
   }
}
