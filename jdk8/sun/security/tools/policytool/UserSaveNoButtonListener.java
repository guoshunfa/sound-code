package sun.security.tools.policytool;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class UserSaveNoButtonListener implements ActionListener {
   private PolicyTool tool;
   private ToolWindow tw;
   private ToolDialog us;
   private int select;

   UserSaveNoButtonListener(ToolDialog var1, PolicyTool var2, ToolWindow var3, int var4) {
      this.us = var1;
      this.tool = var2;
      this.tw = var3;
      this.select = var4;
   }

   public void actionPerformed(ActionEvent var1) {
      this.us.setVisible(false);
      this.us.dispose();
      this.us.userSaveContinue(this.tool, this.tw, this.us, this.select);
   }
}
