package sun.security.tools.policytool;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JList;

class MainWindowListener implements ActionListener {
   private PolicyTool tool;
   private ToolWindow tw;

   MainWindowListener(PolicyTool var1, ToolWindow var2) {
      this.tool = var1;
      this.tw = var2;
   }

   public void actionPerformed(ActionEvent var1) {
      ToolDialog var2;
      if (PolicyTool.collator.compare(var1.getActionCommand(), "Add.Policy.Entry") == 0) {
         var2 = new ToolDialog(PolicyTool.getMessage("Policy.Entry"), this.tool, this.tw, true);
         var2.displayPolicyEntryDialog(false);
      } else {
         int var3;
         ToolDialog var4;
         JList var5;
         if (PolicyTool.collator.compare(var1.getActionCommand(), "Remove.Policy.Entry") == 0) {
            var5 = (JList)this.tw.getComponent(3);
            var3 = var5.getSelectedIndex();
            if (var3 < 0) {
               this.tw.displayErrorDialog((Window)null, (Throwable)(new Exception(PolicyTool.getMessage("No.Policy.Entry.selected"))));
               return;
            }

            var4 = new ToolDialog(PolicyTool.getMessage("Remove.Policy.Entry"), this.tool, this.tw, true);
            var4.displayConfirmRemovePolicyEntry();
         } else if (PolicyTool.collator.compare(var1.getActionCommand(), "Edit.Policy.Entry") == 0) {
            var5 = (JList)this.tw.getComponent(3);
            var3 = var5.getSelectedIndex();
            if (var3 < 0) {
               this.tw.displayErrorDialog((Window)null, (Throwable)(new Exception(PolicyTool.getMessage("No.Policy.Entry.selected"))));
               return;
            }

            var4 = new ToolDialog(PolicyTool.getMessage("Policy.Entry"), this.tool, this.tw, true);
            var4.displayPolicyEntryDialog(true);
         } else if (PolicyTool.collator.compare(var1.getActionCommand(), "Edit") == 0) {
            var2 = new ToolDialog(PolicyTool.getMessage("KeyStore"), this.tool, this.tw, true);
            var2.keyStoreDialog(0);
         }
      }

   }
}
