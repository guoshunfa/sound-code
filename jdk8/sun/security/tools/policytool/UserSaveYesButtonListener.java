package sun.security.tools.policytool;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.MessageFormat;
import javax.swing.JTextField;

class UserSaveYesButtonListener implements ActionListener {
   private ToolDialog us;
   private PolicyTool tool;
   private ToolWindow tw;
   private int select;

   UserSaveYesButtonListener(ToolDialog var1, PolicyTool var2, ToolWindow var3, int var4) {
      this.us = var1;
      this.tool = var2;
      this.tw = var3;
      this.select = var4;
   }

   public void actionPerformed(ActionEvent var1) {
      this.us.setVisible(false);
      this.us.dispose();

      try {
         String var2 = ((JTextField)this.tw.getComponent(1)).getText();
         if (var2 != null && !var2.equals("")) {
            this.tool.savePolicy(var2);
            MessageFormat var3 = new MessageFormat(PolicyTool.getMessage("Policy.successfully.written.to.filename"));
            Object[] var4 = new Object[]{var2};
            this.tw.displayStatusDialog((Window)null, var3.format(var4));
            this.us.userSaveContinue(this.tool, this.tw, this.us, this.select);
         } else {
            this.us.displaySaveAsDialog(this.select);
         }
      } catch (Exception var5) {
         this.tw.displayErrorDialog((Window)null, (Throwable)var5);
      }

   }
}
